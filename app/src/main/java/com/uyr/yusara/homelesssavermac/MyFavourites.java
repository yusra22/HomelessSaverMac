package com.uyr.yusara.homelesssavermac;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uyr.yusara.homelesssavermac.Agency.Agency_Details;
import com.uyr.yusara.homelesssavermac.Agency.MyAgencyPost;
import com.uyr.yusara.homelesssavermac.Modal.Posts;

public class MyFavourites extends AppCompatActivity {

    private RecyclerView postList;

    private DatabaseReference UsersRef;
    private DatabaseReference BookmarksRef,PostTest,BookmarkRef;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private Toolbar mToolbar;

    private DatabaseReference PostsRef2;

    //Query SortAgentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourites);

        postList = findViewById(R.id.recyclerview_myfavourites);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        BookmarksRef = FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(currentUserid);
        PostTest = FirebaseDatabase.getInstance().getReference().child("Posts");

        BookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmarks");

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Favourites");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(MyFavourites.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(BookmarksRef == null) {

            Toast.makeText(MyFavourites.this, "x Masuk Bookmark ", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(MyFavourites.this, "Successfully Masuk Bookmark ", Toast.LENGTH_SHORT).show();

            Query SortBookmarkPost = BookmarksRef.orderByChild(currentUserid);

            FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(SortBookmarkPost, Posts.class).build();

            FirebaseRecyclerAdapter<Posts, PostsViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull PostsViewHolder holder, final int position, @NonNull Posts model) {
                            holder.productname.setText(model.getAgencyname());
                            holder.productprice.setText(model.getCategories());
                            holder.productdate.setText(model.getDate());
                            holder.productstatus.setText(model.getTags());
                            holder.productnumber.setText(model.getOfficenumber());
                            //Glide.with(MyAgencyPost.this).load(model.getPostImage()).into(holder.productimage);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Untuk dpat id user
                                    //String PostKey = getSnapshots().get(position).getUid();

                                    // Untuk dpat Id dalam table post
                                    String PostKey = getSnapshots().getSnapshot(position).getKey();
                                    String Agencyname = getSnapshots().get(position).getAgencyname();


                                    Intent click_post = new Intent(MyFavourites.this, Agency_Details.class);
                                    click_post.putExtra("PostKey", PostKey);
                                    //click_post.putExtra("Agencyname", Agencyname);
                                    startActivity(click_post);

                                }
                            });

                            holder.layout_action1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MyFavourites.this);
                                    builder.setTitle("Are you sure about this?");

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String PostKey = getSnapshots().getSnapshot(position).getKey();

                                            BookmarkRef.child(currentUserid).child(PostKey).child(currentUserid).removeValue();
                                            Toast.makeText(MyFavourites.this, "Favourites delete successfully ", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    AlertDialog ad = builder.create();
                                    ad.show();
                                }
                            });
                        }

                        @NonNull
                        @Override
                        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_myfavourites, viewGroup, false);
                            PostsViewHolder viewHolder = new PostsViewHolder(view);

                            return viewHolder;
                        }
                    };


            postList.setAdapter(adapter);
            adapter.startListening();

            }
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView productname, productprice, productdate, productstatus,productnumber;
        //ImageView productimage;
        LinearLayout layout_action1;

        public PostsViewHolder(View itemView) {
            super(itemView);

            productname = itemView.findViewById(R.id.post_product_name);
            productprice = itemView.findViewById(R.id.post_product_price);
            productdate = itemView.findViewById(R.id.post_product_date);
            productnumber = itemView.findViewById(R.id.post_product_phoneno);

            //productimage = itemView.findViewById(R.id.post_product_image);
            productstatus = itemView.findViewById(R.id.post_product_status);
            layout_action1 = itemView.findViewById(R.id.layout_action1);
        }

    }
}

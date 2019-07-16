package com.uyr.yusara.homelesssavermac.Agency;

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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uyr.yusara.homelesssavermac.MainActivity;
import com.uyr.yusara.homelesssavermac.Modal.Posts;
import com.uyr.yusara.homelesssavermac.R;

public class MyAgencyPost extends AppCompatActivity {

    private RecyclerView postList;

    private DatabaseReference UsersRef;
    private DatabaseReference Postsref;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private Toolbar mToolbar;

    private DatabaseReference PostsRef2;

    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_agency_post);

        postList = findViewById(R.id.recyclerview_allpostagency);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        Postsref = FirebaseDatabase.getInstance().getReference().child("Posts");

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Community Services");
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
        Intent mainIntent = new Intent(MyAgencyPost.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query SortAgentPost = Postsref.orderByChild("uid").startAt(currentUserid).endAt(currentUserid + "\uf8ff");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(SortAgentPost, Posts.class).build();

        FirebaseRecyclerAdapter<Posts,PostsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostsViewHolder holder, final int position, @NonNull Posts model)
                    {
                        holder.productname.setText(model.getAgencyname().substring(0, 1).toUpperCase() + model.getAgencyname().substring(1));
                        holder.productprice.setText(model.getEmail());
                        holder.productdate.setText(model.getDate());
                        holder.productstatus.setText(model.getTags());
                        holder.productnumber.setText(model.getOfficenumber());
                        //Glide.with(MyAgencyPost.this).load(model.getPostImage()).into(holder.productimage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                //Untuk dpat id user
                                //String PostKey = getSnapshots().get(position).getUid();

                                // Untuk dpat Id dalam table post
                                String PostKey = getSnapshots().getSnapshot(position).getKey();
                                String Agencyname = getSnapshots().get(position).getAgencyname();

                                //Toast.makeText(MyAgencyPost.this, PostKey, Toast.LENGTH_SHORT).show();


                                Intent click_post = new Intent(MyAgencyPost.this,Agency_Details.class);
                                click_post.putExtra("PostKey", PostKey);
                                //click_post.putExtra("Agencyname", Agencyname);
                                startActivity(click_post);

                            }
                        });

                        holder.layout_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(MyAgencyPost.this);
                                builder.setTitle("Are you sure about this?");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String PostKey = getSnapshots().getSnapshot(position).getKey();

                                        PostsRef2 = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

                                        PostsRef2.removeValue();
                                        Toast.makeText(MyAgencyPost.this, "Deleted successfully ", Toast.LENGTH_SHORT).show();
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

                        holder.layout_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                String PostKey = getSnapshots().getSnapshot(position).getKey();

                                Intent click_edit_post = new Intent(MyAgencyPost.this,EditMyAgencyPost.class);
                                click_edit_post.putExtra("PostKey", PostKey);
                                startActivity(click_edit_post);
                                Animatoo.animateZoom(MyAgencyPost.this);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout_agent, viewGroup, false);
                        PostsViewHolder viewHolder = new PostsViewHolder(view);

                        return viewHolder;
                    }
                };
        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView productname, productprice, productdate, productstatus,productnumber;
        //ImageView productimage;
        LinearLayout layout_action1,layout_action2;

        public PostsViewHolder(View itemView) {
            super(itemView);

            productname = itemView.findViewById(R.id.post_product_name);
            productprice = itemView.findViewById(R.id.post_product_price);
            productdate = itemView.findViewById(R.id.post_product_date);
            productnumber = itemView.findViewById(R.id.post_product_phoneno);

            //productimage = itemView.findViewById(R.id.post_product_image);
            productstatus = itemView.findViewById(R.id.post_product_status);
            layout_action1 = itemView.findViewById(R.id.layout_action1);
            layout_action2 = itemView.findViewById(R.id.layout_action2);
        }

    }
}


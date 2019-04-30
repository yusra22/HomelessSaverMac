package com.uyr.yusara.homelesssavermac.Agency;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.MainActivity;
import com.uyr.yusara.homelesssavermac.Modal.Posts;
import com.uyr.yusara.homelesssavermac.R;

import java.lang.reflect.Field;

public class Agency_post extends AppCompatActivity {

    private RecyclerView postList;

    private DatabaseReference UsersRef,Postsref,LikesRef, DisLikesRef;
    Boolean LikeChecker = false;
    Boolean DislikeChecker = false;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private Toolbar mToolbar;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_post);

        postList = findViewById(R.id.recyclerview_allpostagent2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        Postsref = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        DisLikesRef = FirebaseDatabase.getInstance().getReference().child("DisLikes");

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Services Available");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mp = MediaPlayer.create(this, R.raw.blop);

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();
                if(id == R.id.navigation_homeless)
                {
                    food();
                }
                if(id == R.id.navigation_shelter)
                {
                    shelter();
                }

                return false;
            }
        });
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

    public void food()
    {
        //Query SortAgentPost = Postsref.orderByChild("uid").startAt(currentUserid).endAt(currentUserid + "\uf8ff");
        Query SortAgentPost = Postsref.orderByChild("service").startAt("Food").endAt("Food" + "\uf8ff");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(SortAgentPost, Posts.class).build();

        FirebaseRecyclerAdapter<Posts,PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, Agency_post.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull Agency_post.PostsViewHolder holder, final int position, @NonNull Posts model)
            {

                final String PostKey = getRef(position).getKey();

                holder.productname.setText(model.getAgencyname());
                holder.productprice.setText(model.getCategories());
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


                        Intent click_post = new Intent(Agency_post.this,Agency_Details.class);
                        click_post.putExtra("PostKey", PostKey);
                        //click_post.putExtra("Agencyname", Agencyname);
                        startActivity(click_post);

                    }
                });

                holder.setLikeButtonStatus(PostKey);
                //holder.setDisLikeButtonStatus(PostKey);

                holder.layout_likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        LikeChecker = true;

                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(LikeChecker.equals(true))
                                {
                                    if(dataSnapshot.child(PostKey).hasChild(currentUserid))
                                    {
                                        LikesRef.child(PostKey).child(currentUserid).removeValue();
                                        LikeChecker = false;
                                        mp.start();
                                    }
                                    else {

                                        LikesRef.child(PostKey).child(currentUserid).setValue(true);
                                        DisLikesRef.child(PostKey).child(currentUserid).removeValue();
                                        LikeChecker = false;
                                        mp.start();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });
                    }
                });

                holder.layout_dislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DislikeChecker = true;

                        DisLikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(DislikeChecker.equals(true))
                                {
                                    if(dataSnapshot.child(PostKey).hasChild(currentUserid))
                                    {
                                        DisLikesRef.child(PostKey).child(currentUserid).removeValue();
                                        DislikeChecker = false;
                                        mp.start();
                                    }
                                    else {

                                        DisLikesRef.child(PostKey).child(currentUserid).setValue(true);
                                        LikesRef.child(PostKey).child(currentUserid).removeValue();
                                        DislikeChecker = false;
                                        mp.start();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public Agency_post.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout_agent2, viewGroup, false);
                Agency_post.PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public void shelter()
    {
        Query SortAgentPost = Postsref.orderByChild("service").startAt("Shelter").endAt("Shelter" + "\uf8ff");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(SortAgentPost, Posts.class).build();

        FirebaseRecyclerAdapter<Posts,PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, Agency_post.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull Agency_post.PostsViewHolder holder, final int position, @NonNull Posts model)
            {

                final String PostKey = getRef(position).getKey();

                holder.productname.setText(model.getAgencyname());
                holder.productprice.setText(model.getCategories());
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


                        Intent click_post = new Intent(Agency_post.this,Agency_Details.class);
                        click_post.putExtra("PostKey", PostKey);
                        //click_post.putExtra("Agencyname", Agencyname);
                        startActivity(click_post);

                    }
                });

                holder.setLikeButtonStatus(PostKey);
                //holder.setDisLikeButtonStatus(PostKey);

                holder.layout_likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        LikeChecker = true;

                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(LikeChecker.equals(true))
                                {
                                    if(dataSnapshot.child(PostKey).hasChild(currentUserid))
                                    {
                                        LikesRef.child(PostKey).child(currentUserid).removeValue();
                                        LikeChecker = false;
                                        mp.start();
                                    }
                                    else {

                                        LikesRef.child(PostKey).child(currentUserid).setValue(true);
                                        DisLikesRef.child(PostKey).child(currentUserid).removeValue();
                                        LikeChecker = false;
                                        mp.start();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });
                    }
                });

                holder.layout_dislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DislikeChecker = true;

                        DisLikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(DislikeChecker.equals(true))
                                {
                                    if(dataSnapshot.child(PostKey).hasChild(currentUserid))
                                    {
                                        DisLikesRef.child(PostKey).child(currentUserid).removeValue();
                                        DislikeChecker = false;
                                        mp.start();
                                    }
                                    else {

                                        DisLikesRef.child(PostKey).child(currentUserid).setValue(true);
                                        LikesRef.child(PostKey).child(currentUserid).removeValue();
                                        DislikeChecker = false;
                                        mp.start();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public Agency_post.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout_agent2, viewGroup, false);
                Agency_post.PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();

    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(Agency_post.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //Query SortAgentPost = Postsref.orderByChild("uid").startAt(currentUserid).endAt(currentUserid + "\uf8ff");
        Query SortAgentPost = Postsref.orderByChild("counter");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(SortAgentPost, Posts.class).build();

        FirebaseRecyclerAdapter<Posts,PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, Agency_post.PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull Agency_post.PostsViewHolder holder, final int position, @NonNull Posts model)
            {

                final String PostKey = getRef(position).getKey();

                holder.productname.setText(model.getAgencyname());
                holder.productprice.setText(model.getCategories());
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


                        Intent click_post = new Intent(Agency_post.this,Agency_Details.class);
                        click_post.putExtra("PostKey", PostKey);
                        //click_post.putExtra("Agencyname", Agencyname);
                        startActivity(click_post);

                    }
                });

                holder.setLikeButtonStatus(PostKey);
                //holder.setDisLikeButtonStatus(PostKey);

                holder.layout_likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        LikeChecker = true;

                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(LikeChecker.equals(true))
                                {
                                    if(dataSnapshot.child(PostKey).hasChild(currentUserid))
                                    {
                                        LikesRef.child(PostKey).child(currentUserid).removeValue();
                                        LikeChecker = false;
                                        mp.start();
                                    }
                                    else {

                                        LikesRef.child(PostKey).child(currentUserid).setValue(true);
                                        DisLikesRef.child(PostKey).child(currentUserid).removeValue();
                                        LikeChecker = false;
                                        mp.start();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });
                    }
                });

                holder.layout_dislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DislikeChecker = true;

                        DisLikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(DislikeChecker.equals(true))
                                {
                                    if(dataSnapshot.child(PostKey).hasChild(currentUserid))
                                    {
                                        DisLikesRef.child(PostKey).child(currentUserid).removeValue();
                                        DislikeChecker = false;
                                        mp.start();
                                    }
                                    else {

                                        DisLikesRef.child(PostKey).child(currentUserid).setValue(true);
                                        LikesRef.child(PostKey).child(currentUserid).removeValue();
                                        DislikeChecker = false;
                                        mp.start();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public Agency_post.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout_agent2, viewGroup, false);
                Agency_post.PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView productname, productprice, productdate, productstatus,productnumber,productlikes,productdislikes;
        ImageView button_likes, button_dislikes;
        Integer countLikes;
        long countDisLikes;
        String currentUserid;
        DatabaseReference LikesRef,DisLikesRef;
        LinearLayout layout_likes,layout_dislikes;


        public PostsViewHolder(View itemView)
        {
            super(itemView);

            productname = itemView.findViewById(R.id.post_product_name);
            productprice = itemView.findViewById(R.id.post_product_price);
            productdate = itemView.findViewById(R.id.post_product_date);
            productnumber = itemView.findViewById(R.id.post_product_phoneno);
            productstatus = itemView.findViewById(R.id.post_product_status);
            productlikes = itemView.findViewById(R.id.post_product_likes);
            productdislikes = itemView.findViewById(R.id.post_product_dislikes);

            layout_likes = itemView.findViewById(R.id.layout_likes);
            layout_dislikes = itemView.findViewById(R.id.layout_dislikes);


            button_likes = (ImageView) itemView.findViewById(R.id.button_likes);
            button_dislikes = (ImageView) itemView.findViewById(R.id.button_dislikes);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            DisLikesRef = FirebaseDatabase.getInstance().getReference().child("DisLikes");
            currentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonStatus(final String PostKey)
        {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(PostKey).hasChild(currentUserid))
                    {
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        button_likes.setImageResource(R.drawable.ic_unlike);
                        productlikes.setText(Integer.toString(countLikes) + " Likes");
                    }
                    else {

                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        button_likes.setImageResource(R.drawable.ic_like);
                        productlikes.setText(Integer.toString(countLikes) + " Likes");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DisLikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(PostKey).hasChild(currentUserid))
                    {
                        countDisLikes = dataSnapshot.child(PostKey).getChildrenCount();
                        button_dislikes.setImageResource(R.drawable.ic_dislike);
                        productdislikes.setText(Integer.toString((int) countDisLikes) + " Dislikes");

                    }
                    else {

                        countDisLikes = dataSnapshot.child(PostKey).getChildrenCount();
                        button_dislikes.setImageResource(R.drawable.ic_like);
                        productdislikes.setText(Integer.toString((int) countDisLikes) + " Dislikes");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setDisLikeButtonStatus(final String PostKey2) {

        }
    }
}

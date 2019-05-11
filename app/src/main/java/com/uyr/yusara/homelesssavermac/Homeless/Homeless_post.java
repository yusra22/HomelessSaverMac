package com.uyr.yusara.homelesssavermac.Homeless;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.Agency.Agency_post;
import com.uyr.yusara.homelesssavermac.MainActivity;
import com.uyr.yusara.homelesssavermac.Modal.Posts_Homeless;
import com.uyr.yusara.homelesssavermac.R;

public class Homeless_post extends AppCompatActivity {

    private RecyclerView postList;

    private DatabaseReference UsersRef,Postsref,LikesRef, DisLikesRef;
    Boolean LikeChecker = false;
    Boolean DislikeChecker = false;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private Toolbar mToolbar;
    private SearchView searchView;
    private TextView alllist;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeless_post);

        postList = findViewById(R.id.recyclerview_allhomeless2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        Postsref = FirebaseDatabase.getInstance().getReference().child("People Report Post");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        DisLikesRef = FirebaseDatabase.getInstance().getReference().child("DisLikes");

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Homeless List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchView = (SearchView)findViewById(R.id.search);
        alllist = (TextView)findViewById(R.id.alllist);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String searchBoxInput = searchView.getQuery().toString();
                searchhomelessname(searchBoxInput);
                return false;
            }
        });

        alllist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allpost();
            }
        });

        mp = MediaPlayer.create(this, R.raw.blop);

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();
                if(id == R.id.navigation_gender)
                {
                    //food();
                }
                if(id == R.id.navigation_martialstatus)
                {
                    //shelter();
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        allpost();
    }

    private void searchhomelessname(String searchBoxInput) {

        String query = searchBoxInput.toLowerCase();
        Query SortAgentPost = Postsref.orderByChild("fullname").startAt(query).endAt(query + "\uf8ff");
        //Query SortAgentPost = Postsref.orderByChild("fullname");

        FirebaseRecyclerOptions<Posts_Homeless> options = new FirebaseRecyclerOptions.Builder<Posts_Homeless>().setQuery(SortAgentPost, Posts_Homeless.class).build();

        FirebaseRecyclerAdapter<Posts_Homeless,PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts_Homeless, PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, final int position, @NonNull Posts_Homeless model)
            {

                final String PostKey = getRef(position).getKey();

                holder.post_fullname.setText(model.getFullname());
                holder.post_ages.setText(model.getAge());
                holder.post_gender.setText(model.getGender());
                holder.post_martialstatus.setText(model.getMartialstatus());
                holder.post_date.setText(model.getDate());
                //Glide.with(MyAgencyPost.this).load(model.getPostImage()).into(holder.productimage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        //Untuk dpat id user
                        //String PostKey = getSnapshots().get(position).getUid();

                        // Untuk dpat Id dalam table post
                        String PostKey = getSnapshots().getSnapshot(position).getKey();
                        //String Agencyname = getSnapshots().get(position).getAgencyname();


                        Intent click_post = new Intent(Homeless_post.this,activity_homeless_details.class);
                        click_post.putExtra("PostKey", PostKey);
                        //click_post.putExtra("Agencyname", Agencyname);
                        startActivity(click_post);
                        Animatoo.animateZoom(Homeless_post.this);

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
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout_homeless, viewGroup, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    private void allpost() {

        //Query SortAgentPost = Postsref.orderByChild("uid").startAt(currentUserid).endAt(currentUserid + "\uf8ff");
        Query SortAgentPost = Postsref.orderByChild("counter");

        FirebaseRecyclerOptions<Posts_Homeless> options = new FirebaseRecyclerOptions.Builder<Posts_Homeless>().setQuery(SortAgentPost, Posts_Homeless.class).build();

        FirebaseRecyclerAdapter<Posts_Homeless,PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts_Homeless, PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, final int position, @NonNull Posts_Homeless model)
            {

                final String PostKey = getRef(position).getKey();

                holder.post_fullname.setText(model.getFullname());
                holder.post_ages.setText(model.getAge());
                holder.post_gender.setText(model.getGender());
                holder.post_martialstatus.setText(model.getMartialstatus());
                holder.post_date.setText(model.getDate());
                //Glide.with(MyAgencyPost.this).load(model.getPostImage()).into(holder.productimage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        //Untuk dpat id user
                        //String PostKey = getSnapshots().get(position).getUid();

                        // Untuk dpat Id dalam table post
                        String PostKey = getSnapshots().getSnapshot(position).getKey();
                        //String Agencyname = getSnapshots().get(position).getAgencyname();


                        Intent click_post = new Intent(Homeless_post.this,activity_homeless_details.class);
                        click_post.putExtra("PostKey", PostKey);
                        //click_post.putExtra("Agencyname", Agencyname);
                        startActivity(click_post);
                        Animatoo.animateZoom(Homeless_post.this);

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
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout_homeless, viewGroup, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView post_fullname, post_ages, post_gender, post_martialstatus,post_date,productlikes,productdislikes;
        ImageView button_likes, button_dislikes;
        Integer countLikes;
        long countDisLikes;
        String currentUserid;
        DatabaseReference LikesRef,DisLikesRef;
        LinearLayout layout_likes,layout_dislikes;


        public PostsViewHolder(View itemView)
        {
            super(itemView);

            post_fullname = itemView.findViewById(R.id.post_fullname);
            post_ages = itemView.findViewById(R.id.post_ages);
            post_gender = itemView.findViewById(R.id.post_gender);
            post_martialstatus = itemView.findViewById(R.id.post_martialstatus);
            post_date = itemView.findViewById(R.id.post_date);
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

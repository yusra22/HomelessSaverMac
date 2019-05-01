package com.uyr.yusara.homelesssavermac.Favourites;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.uyr.yusara.homelesssavermac.Homeless.activity_homeless_details;
import com.uyr.yusara.homelesssavermac.Modal.Posts_Homeless;
import com.uyr.yusara.homelesssavermac.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomelessFavFragment extends Fragment {

    private View HomFavView;

    private RecyclerView postList;

    private DatabaseReference UsersRef;
    private DatabaseReference BookmarksRef,PostHomeless,BookmarkRef;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private LinearLayout layout;

    public HomelessFavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HomFavView = inflater.inflate(R.layout.fragment_homeless_fav, container, false);

        postList = (RecyclerView) HomFavView.findViewById(R.id.recyclerview_myfavourites);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        BookmarksRef = FirebaseDatabase.getInstance().getReference().child("BookmarksHomeless").child(currentUserid);
        PostHomeless = FirebaseDatabase.getInstance().getReference().child("People Report Post");

        BookmarkRef = FirebaseDatabase.getInstance().getReference().child("BookmarksHomeless");

        layout = (LinearLayout) HomFavView.findViewById(R.id.my_layout_bookmark);
        layout.setVisibility(View.VISIBLE);


        return HomFavView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(BookmarksRef == null) {



        } else {

            //Toast.makeText(getContext(), "Successfully Masuk Bookmark ", Toast.LENGTH_SHORT).show();

            Query SortBookmarkPost = BookmarksRef.orderByChild(currentUserid);

            FirebaseRecyclerOptions<Posts_Homeless> options = new FirebaseRecyclerOptions.Builder<Posts_Homeless>().setQuery(SortBookmarkPost, Posts_Homeless.class).build();

            FirebaseRecyclerAdapter<Posts_Homeless, PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts_Homeless, PostsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final PostsViewHolder holder, final int position, @NonNull Posts_Homeless model) {
                    //Glide.with(MyAgencyPost.this).load(model.getPostImage()).into(holder.productimage);

                    PostHomeless.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String Postkey = getSnapshots().getSnapshot(position).getKey();

                            PostHomeless.child(Postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                                    String ic = dataSnapshot.child("ic").getValue().toString();
                                    String date = dataSnapshot.child("date").getValue().toString();
                                    String age = dataSnapshot.child("age").getValue().toString();
                                    String gender = dataSnapshot.child("gender").getValue().toString();

                                    holder.fullname.setText(fullname);
                                    holder.ic.setText(ic);
                                    holder.ages.setText(age);
                                    holder.gender.setText(gender);
                                    holder.date.setText(date);


                                    layout = (LinearLayout) HomFavView.findViewById(R.id.my_layout_bookmark);
                                    layout.setVisibility(View.GONE);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // Untuk dpat Id dalam table post
                            String PostKey = getSnapshots().getSnapshot(position).getKey();

                            Intent click_post = new Intent(getContext(), activity_homeless_details.class);
                            click_post.putExtra("PostKey", PostKey);
                            startActivity(click_post);

                        }
                    });

                    holder.layout_action1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Are you sure about this?");

                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String PostKey = getSnapshots().getSnapshot(position).getKey();

                                    BookmarkRef.child(currentUserid).child(PostKey).child(currentUserid).removeValue();
                                    Toast.makeText(getContext(), "Favourites delete successfully ", Toast.LENGTH_SHORT).show();

                                    layout = (LinearLayout) HomFavView.findViewById(R.id.my_layout_bookmark);
                                    layout.setVisibility(View.VISIBLE);
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
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_myhomelessfavourites, viewGroup, false);
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
        TextView fullname, ic, ages, gender, date;
        //ImageView productimage;
        LinearLayout layout_action1;

        public PostsViewHolder(View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.post_homeless_name);
            ic = itemView.findViewById(R.id.post_ic);
            ages = itemView.findViewById(R.id.post_ages);
            gender = itemView.findViewById(R.id.post_gender);

            //productimage = itemView.findViewById(R.id.post_product_image);
            date = itemView.findViewById(R.id.post_date);
            layout_action1 = itemView.findViewById(R.id.layout_action1);
        }

    }

}

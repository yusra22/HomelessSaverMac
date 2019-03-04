package com.uyr.yusara.homelesssavermac;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.Modal.CommentModal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Comment extends AppCompatActivity {

    private ImageButton postCommentbtn;
    private EditText commentInputText;
    private RecyclerView CommentsList;

    private DatabaseReference UsersRef,PostsRef;
    private FirebaseAuth mAuth;
    private String currentUserid;

    private DatabaseReference Postsref;

    private String PostKey;

    private long countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        PostKey = getIntent().getExtras().get("PostKey").toString();

        CommentsList = (RecyclerView) findViewById(R.id.recycleView_comment);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("comment");

        commentInputText = (EditText)findViewById(R.id.comment_input);
        postCommentbtn = (ImageButton)findViewById(R.id.btnPostComment);

        postCommentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                UsersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            String userName = dataSnapshot.child("name").getValue().toString();

                            validateComment(userName);
                            commentInputText.setText("");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

                PostsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            countPosts = dataSnapshot.getChildrenCount();
                        }
                        else {

                            countPosts = 0;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query SortPostInDecendingOrder = PostsRef.orderByChild("counter");

        FirebaseRecyclerOptions<CommentModal> options = new FirebaseRecyclerOptions.Builder<CommentModal>()
                .setQuery(SortPostInDecendingOrder,CommentModal.class)
                .build();

        FirebaseRecyclerAdapter<CommentModal, CommentsViewHolder> adapter = new FirebaseRecyclerAdapter<CommentModal, Comment.CommentsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull CommentModal model)
            {

                holder.setUsername(model.getUsername());
                holder.setComment(model.getComment());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());

            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_comment_layout, viewGroup, false);
                CommentsViewHolder viewholder = new CommentsViewHolder(view);
                return viewholder;
            }
        };

        CommentsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {

        public CommentsViewHolder(View itemView)
        {
            super(itemView);
        }

        public void setUsername(String username)
        {
            TextView myName = (TextView) itemView.findViewById(R.id.comment_username);
            myName.setText("@" + username +"  ") ;

        }

        public void setComment(String comment)
        {

            TextView myComment = (TextView) itemView.findViewById(R.id.comment_text);
            myComment.setText(comment);

        }

        public void setDate(String date)
        {
            TextView mydate = (TextView) itemView.findViewById(R.id.comment_date);
            mydate.setText(date);
        }

        public void setTime(String time)
        {
            TextView mytime = (TextView) itemView.findViewById(R.id.comment_time);
            mytime.setText("  Time: "+time);
        }
    }

    private void validateComment(String userName)
    {
        String commentText = commentInputText.getText().toString();

        if(TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Please right text to comment ...", Toast.LENGTH_LONG).show();
        }
        else
        {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:");
            final String saveCurrentTime = currentTime.format(calFordTime.getTime());

            final String RandomKey = currentUserid + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", currentUserid);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", userName);
            commentsMap.put("counter",countPosts);

            PostsRef.child(RandomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(Comment.this, "Comment update successfully ", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(Comment.this, "Update Comment error ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}

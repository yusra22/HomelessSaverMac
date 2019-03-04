package com.uyr.yusara.homelesssavermac.Menu;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.R;

import java.util.HashMap;

public class TestAdd extends AppCompatActivity implements View.OnClickListener {

    private Button updatepostbutton;
    private EditText post_test;
    private String test;

    private FirebaseAuth mAuth;
    private String currentUserid;
    private DatabaseReference UsersRef,PostsRef;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_add);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        updatepostbutton = (Button) findViewById(R.id.updatetest);
        post_test = (EditText) findViewById(R.id.edit_test);

        findViewById(R.id.updatetest).setOnClickListener(this);

/*        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.updatetest:
                UpdateBtnPost();
                break;
        }

    }

    private void UpdateBtnPost()
    {
        test = post_test.getText().toString();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                HashMap postMap = new HashMap();
                postMap.put("uid", currentUserid);
                postMap.put("testdata", test);

                PostsRef.child(currentUserid + "1").setValue(postMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {

                        Toast.makeText(TestAdd.this, "Post update successfully ", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


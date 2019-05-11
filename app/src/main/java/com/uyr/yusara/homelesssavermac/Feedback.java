package com.uyr.yusara.homelesssavermac;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Feedback extends AppCompatActivity {

    String email,message,subject;
    private EditText edit_email,edit_message,edit_subject;
    private Button btnsendfeedback;

    private FirebaseAuth mAuth;
    private String currentUserid;
    private DatabaseReference UsersRef,FeedbackRef;

    private String saveCurrentDate, saveCurrentTime, postRandomName;

    private Toolbar mToolbar;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //Custom Toolbar
        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Feedback/Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Feedback/Report being Send...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        FeedbackRef = FirebaseDatabase.getInstance().getReference().child("Feedback");

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_message = (EditText) findViewById(R.id.edit_message);
        edit_subject = (EditText) findViewById(R.id.edit_subject);
        btnsendfeedback = (Button) findViewById(R.id.btnFeedback);

        btnsendfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddFeedback();

            }
        });

        //Close Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void AddFeedback() {

        email= edit_email.getText().toString();
        subject = edit_subject.getText().toString();
        message = edit_message.getText().toString();

            if(email.isEmpty())
            {
                edit_email.setError("Please add valid email");
                edit_email.requestFocus();
                return;
            }
            if(subject.isEmpty())
            {
                edit_subject.setError("Please add the subject");
                edit_subject.requestFocus();
                return;
            }
            if(message.isEmpty())
            {
                edit_message.setError("Please add the message");
                edit_message.requestFocus();

            }
            else
            {
                UsersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            if(dataSnapshot !=null)
                            {

                                progressDialog.show();

                                Calendar calFordDate = Calendar.getInstance();
                                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                saveCurrentDate = currentDate.format(calFordDate.getTime());

                                Calendar calFordTime = Calendar.getInstance();
                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:");
                                saveCurrentTime = currentTime.format(calFordTime.getTime());

                                postRandomName = saveCurrentDate + saveCurrentTime;

                                final HashMap postMap = new HashMap();
                                postMap.put("uid", currentUserid);
                                postMap.put("date", saveCurrentDate);
                                postMap.put("time", saveCurrentTime);
                                postMap.put("email", email);
                                postMap.put("subject", subject);
                                postMap.put("message", message);

                                FeedbackRef.child(currentUserid + postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {

                                    @Override
                                    public void onComplete(@NonNull Task task)
                                    {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Feedback.this, "Feedback have been send successfully ", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            finish();

                                        }
                                        else {
                                            Log.d("LOGGER", "No such document");
                                        }
                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
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
}

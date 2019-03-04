package com.uyr.yusara.homelesssavermac.Agency;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.R;
import com.uyr.yusara.homelesssavermac.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddServices extends AppCompatActivity implements View.OnClickListener,TimePickerDialog.OnTimeSetListener
{
    private Button UpdatePostButton;
    private EditText post_agencyname,post_categories,post_location,post_officenumber,post_email,post_website,post_facebook,post_twitter;

    private TextView post_selectscheduletype,edit_selectday,edit_starttime;

    private RadioGroup servicechoice,categorychoice;
    private RadioButton radioButtonServicesoption,radioButtonCatergoryoption;

    private String agencyname,categories,location,officenumber,email,website,facebook,twitter;

    private FirebaseAuth mAuth;
    private String currentUserid;
    private DatabaseReference UsersRef,PostsRef,NotisRef;

    private int countPosts = 0;

    private String saveCurrentDate, saveCurrentTime, postRandomName;
    private String service,tags;

    private Toolbar mToolbar;
    private ProgressBar progressBar;

    private LinearLayout layoutday,layoutstarttime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        NotisRef = FirebaseDatabase.getInstance().getReference().child("Notification");

        UpdatePostButton = findViewById(R.id.btnupdatepost);
        post_agencyname = findViewById(R.id.edit_agencyname);
        post_categories = findViewById(R.id.edit_catergories);
        post_location = findViewById(R.id.edit_location);
        post_officenumber = findViewById(R.id.edit_officenumber);
        post_email = findViewById(R.id.edit_email);
        post_website = findViewById(R.id.edit_website);
        post_facebook = findViewById(R.id.edit_facebook);
        post_twitter = findViewById(R.id.edit_twitter);
        post_selectscheduletype = findViewById(R.id.edit_scheduletype);

        layoutday = findViewById(R.id.layoutday);
        layoutstarttime = findViewById(R.id.layoutstarttime);


        edit_selectday = findViewById(R.id.edit_selectday);
        edit_starttime = findViewById(R.id.edit_starttime);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        findViewById(R.id.btnupdatepost).setOnClickListener(this);
        findViewById(R.id.edit_scheduletype).setOnClickListener(this);
        findViewById(R.id.edit_selectday).setOnClickListener(this);
        findViewById(R.id.edit_starttime).setOnClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        servicechoice = (RadioGroup)findViewById(R.id.servicechoice);
        servicechoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButtonServicesoption = servicechoice.findViewById(i);
                switch (i){
                    case R.id.radioButton_food:
                        service = radioButtonServicesoption.getText().toString();
                        break;
                    case R.id.radioButton_shelter:
                        service = radioButtonServicesoption.getText().toString();
                        break;
                    case R.id.radioButton_health:
                        service = radioButtonServicesoption.getText().toString();
                        break;
                    case R.id.radioButton_resources:
                        service = radioButtonServicesoption.getText().toString();
                        break;

                    default:
                }
            }
        });
        int selectedId = servicechoice.getCheckedRadioButtonId();
        radioButtonServicesoption = (RadioButton) findViewById(selectedId);

        categorychoice = (RadioGroup)findViewById(R.id.categorychoice);
        categorychoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButtonCatergoryoption = categorychoice.findViewById(i);
                switch (i){
                    case R.id.radioButton_men:
                        tags = radioButtonCatergoryoption.getText().toString();
                        break;
                    case R.id.radioButton_woman:
                        tags = radioButtonCatergoryoption.getText().toString();
                        break;
                    case R.id.radioButton_youth:
                        tags = radioButtonCatergoryoption.getText().toString();
                        break;
                    case R.id.radioButton_disable:
                        tags = radioButtonCatergoryoption.getText().toString();
                        break;
                    case R.id.radioButton_anyone:
                        tags = radioButtonCatergoryoption.getText().toString();
                        break;

                    default:
                }
            }
        });
        int selectedId2 = categorychoice.getCheckedRadioButtonId();
        radioButtonServicesoption = (RadioButton) findViewById(selectedId2);

        layoutday.setVisibility(View.GONE);


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
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

/*        FirebaseFirestore database = FirebaseFirestore.getInstance();
        com.google.android.gms.tasks.Task<DocumentSnapshot> xx = database.collection("Users").document(uid).get();

        xx.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String role;
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    role = doc.get("role").toString();

                    if(role.equals("Agent")){
                        Intent intent = new Intent(PostActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (role.equals("Customer")) {
                        Intent intent = new Intent(PostActivity.this, MainActivityCustomer.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (role.equals("Admin")) {
                        Intent intent = new Intent(PostActivity.this, AdminMainMenu.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(PostActivity.this, "Unable to find roles", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });*/
    }

    private void scheduletype()
    {
        final CharSequence options[] = new CharSequence[]
                {
                        "Weekly",
                        "Date Range"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddServices.this);
        builder.setTitle("Select Options");


        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    post_selectscheduletype.setText(options[0]);
                    layoutday.setVisibility(View.VISIBLE);
                }
                if (which == 1)
                {
                    post_selectscheduletype.setText(options[1]);
                    layoutday.setVisibility(View.GONE);

                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void selectday()
    {
        final CharSequence options[] = new CharSequence[]
                {
                        "Monday",
                        "Tuesday",
                        "Wednesday",
                        "Thursday",
                        "Friday",
                        "Saturday",
                        "Sunday"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddServices.this);
        builder.setTitle("Select Options");


        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    edit_selectday.setText(options[0]);
                    layoutstarttime.setVisibility(View.VISIBLE);
                }
                if (which == 1)
                {
                    edit_selectday.setText(options[1]);
                    layoutstarttime.setVisibility(View.VISIBLE);
                }
                if (which == 2)
                {
                    edit_selectday.setText(options[2]);
                    layoutstarttime.setVisibility(View.VISIBLE);
                }
                if (which == 3)
                {
                    edit_selectday.setText(options[3]);
                    layoutstarttime.setVisibility(View.VISIBLE);
                }
                if (which == 4)
                {
                    edit_selectday.setText(options[4]);
                    layoutstarttime.setVisibility(View.VISIBLE);
                }
                if (which == 5)
                {
                    edit_selectday.setText(options[5]);
                    layoutstarttime.setVisibility(View.VISIBLE);
                }
                if (which == 6)
                {
                    edit_selectday.setText(options[6]);
                    layoutstarttime.setVisibility(View.VISIBLE);
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void UpdateBtnPost()
    {
        agencyname = post_agencyname.getText().toString();
        categories = post_categories.getText().toString();
        location = post_location.getText().toString();
        officenumber = post_officenumber.getText().toString();
        email = post_email.getText().toString();
        website = post_website.getText().toString();
        facebook = post_facebook.getText().toString();
        twitter = post_twitter.getText().toString();

        if(agencyname.isEmpty())
        {
            post_agencyname.setError("Please add agency name");
            post_agencyname.requestFocus();
            return;
        }
        if(categories.isEmpty())
        {
            post_categories.setError("Please add the description");
            post_categories.requestFocus();
            return;
        }
        if(location.isEmpty())
        {
            post_location.setError("Please add the location");
            post_location.requestFocus();
            Toast.makeText(this, "Please select property type",Toast.LENGTH_SHORT).show();
            return;

        }
        if(officenumber.isEmpty())
        {
            post_officenumber.setError("Please number to contact");
            post_officenumber.requestFocus();
            return;
        }
        if(email.isEmpty())
        {
            post_email.setError("Please add the email address");
            post_email.requestFocus();

        }
        else
        {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:");
            saveCurrentTime = currentTime.format(calFordTime.getTime());

            postRandomName = saveCurrentDate + saveCurrentTime;
            SavingPostInformationToDB();
        }
    }

    private void SavingPostInformationToDB()
    {
        //Untuk Susun
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    if (dataSnapshot != null)
                    {
                        //

                    }
                    else
                    {
                        countPosts = 0;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot !=null)
                    {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String userprofile = dataSnapshot.child("profileimage2").getValue().toString();

                        HashMap postMap = new HashMap();
                        postMap.put("uid", currentUserid);
                        postMap.put("date", saveCurrentDate);
                        postMap.put("time", saveCurrentTime);
                        postMap.put("agencyname", agencyname);
                        postMap.put("categories", categories);
                        postMap.put("officenumber", officenumber);
                        postMap.put("email", email);
                        postMap.put("website", website);
                        postMap.put("facebook",facebook);
                        postMap.put("twitter",twitter);
                        postMap.put("location",location);
                        postMap.put("service",service);
                        postMap.put("tags",tags);

                        postMap.put("counter", countPosts);
                        postMap.put("status","Pending");

/*                        postMap.put("name", name);
                        postMap.put("profileimage2", userprofile);*/

                        PostsRef.child(currentUserid + postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(AddServices.this, "Post update successfully ", Toast.LENGTH_SHORT).show();

                                    HashMap postnotification = new HashMap();
                                    postnotification.put("from",currentUserid);
                                    postnotification.put("type","new post noti");
                                    SendUserToMainActivity();

                                    NotisRef.updateChildren(postnotification).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(AddServices.this, "Notification Work!", Toast.LENGTH_SHORT).show();

                                            }
                                            else
                                            {
                                                Toast.makeText(AddServices.this, "Update Post error ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
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

    @Override
    public void onTimeSet(TimePicker view, int hourofDay, int minute)
    {
        edit_starttime.setText(hourofDay + " : " + minute);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnupdatepost:
                UpdateBtnPost();
                break;
            case R.id.edit_scheduletype:
                scheduletype();
                break;
            case R.id.edit_selectday:
                selectday();
                break;
            case R.id.edit_starttime:
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                break;
        }
    }
}
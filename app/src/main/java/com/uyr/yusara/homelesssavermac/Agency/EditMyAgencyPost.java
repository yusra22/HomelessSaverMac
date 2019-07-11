package com.uyr.yusara.homelesssavermac.Agency;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.santalu.maskedittext.MaskEditText;
import com.uyr.yusara.homelesssavermac.MainActivity;
import com.uyr.yusara.homelesssavermac.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditMyAgencyPost extends AppCompatActivity implements View.OnClickListener
{
    private Button UpdatePostButton;
    private EditText post_agencyname,post_categories,post_location,post_email,post_website,post_facebook,post_twitter;
    private MaskEditText post_officenumber;

    private TextView post_selectscheduletype,edit_selectstartdate,edit_selectenddate,edit_starttime,edit_endtime,edit_scheduletype;
    private String starttime,endtime,startdate,enddate;

    private RadioGroup servicechoice,categorychoice;
    private RadioButton radioButtonServicesoption,radioButtonCatergoryoption;
    private RadioButton radioButton_food,radioButton_shelter,radioButton_health,radioButton_resources;
    private RadioButton radioButton_men,radioButton_woman,radioButton_youth,radioButton_disable,radioButton_anyone;

    private String agencyname,categories,location,officenumber,email,website,facebook,twitter,scheduletype;

    private FirebaseAuth mAuth;
    private String currentUserid;
    private DatabaseReference UsersRef,PostsRef,PostsRef2;

    private long countPosts = 0;

    private String saveCurrentDate, saveCurrentTime, postRandomName;
    private String service,tags;
    private String PostKey;

    private Toolbar mToolbar;
    private ProgressDialog progressDialog;

    private LinearLayout layoutstartdate,layoutenddate,layoutstarttime,layoutendtime;


    Calendar currentTimeNcurrentDate;
    int hour, minute, day, month, year;
    String format;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_agency_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

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

        radioButton_food = (RadioButton) findViewById(R.id.radioButton_food);
        radioButton_shelter = (RadioButton) findViewById(R.id.radioButton_shelter);
        radioButton_health = (RadioButton) findViewById(R.id.radioButton_health);
        radioButton_resources = (RadioButton) findViewById(R.id.radioButton_resources);

        radioButton_men = (RadioButton)findViewById(R.id.radioButton_men);
        radioButton_woman = (RadioButton)findViewById(R.id.radioButton_woman);
        radioButton_youth = (RadioButton)findViewById(R.id.radioButton_youth);
        radioButton_disable = (RadioButton)findViewById(R.id.radioButton_disable);
        radioButton_anyone = (RadioButton)findViewById(R.id.radioButton_anyone);

        layoutstartdate = findViewById(R.id.layoutstartdate);
        layoutenddate = findViewById(R.id.layoutenddate);
        layoutstarttime = findViewById(R.id.layoutstarttime);
        layoutendtime = findViewById(R.id.layoutendtime);

        edit_selectstartdate = findViewById(R.id.edit_selectstartdate);
        edit_selectenddate = findViewById(R.id.edit_selectenddate);
        edit_starttime = findViewById(R.id.edit_starttime);
        edit_endtime = findViewById(R.id.edit_endtime);

        // TimePicker && DatePicker
        currentTimeNcurrentDate = Calendar.getInstance();

        hour = currentTimeNcurrentDate.get(Calendar.HOUR_OF_DAY);
        minute = currentTimeNcurrentDate.get(Calendar.MINUTE);

        day = currentTimeNcurrentDate.get(Calendar.DAY_OF_MONTH);
        month = currentTimeNcurrentDate.get(Calendar.MONTH);
        year = currentTimeNcurrentDate.get(Calendar.YEAR);

        month = month+1;
        selectTimeFormat(hour);
        //edit_starttime.setText(hour + " : " + minute + " " + format);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Service Added Successfully...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        //Click Listener
        findViewById(R.id.btnupdatepost).setOnClickListener(this);
        findViewById(R.id.edit_scheduletype).setOnClickListener(this);
        findViewById(R.id.edit_selectstartdate).setOnClickListener(this);
        findViewById(R.id.edit_selectenddate).setOnClickListener(this);
        findViewById(R.id.edit_starttime).setOnClickListener(this);
        findViewById(R.id.edit_endtime).setOnClickListener(this);

        //Custom Toolbar
        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Services");
        //getSupportActionBar().setIcon(getDrawable(R.drawable.logo));
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

        layoutstartdate.setVisibility(View.GONE);
        layoutenddate.setVisibility(View.GONE);
        layoutstarttime.setVisibility(View.GONE);
        layoutendtime.setVisibility(View.GONE);

        displaydata();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(EditMyAgencyPost.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void displaydata() {

        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                agencyname = dataSnapshot.child("agencyname").getValue().toString();
                categories = dataSnapshot.child("categories").getValue().toString();
                officenumber = dataSnapshot.child("officenumber").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                website = dataSnapshot.child("website").getValue().toString();
                facebook = dataSnapshot.child("facebook").getValue().toString();
                twitter = dataSnapshot.child("twitter").getValue().toString();
                location = dataSnapshot.child("location").getValue().toString();
                service = dataSnapshot.child("service").getValue().toString();
                tags =dataSnapshot.child("tags").getValue().toString();
                scheduletype = dataSnapshot.child("scheduletype").getValue().toString();

                post_agencyname.setText(agencyname);
                post_categories.setText(categories);
                post_officenumber.setText(officenumber);
                post_email.setText(email);
                post_website.setText(website);
                post_facebook.setText(facebook);
                post_twitter.setText(twitter);
                post_location.setText(location);

                //radioButton_shelter.setText(service);

                if(service.equalsIgnoreCase("Food"))
                {
                    radioButton_food.setChecked(true);
                }
                else if(service.equalsIgnoreCase("Shelter"))
                {
                    radioButton_shelter.setChecked(true);
                }
                else if(service.equalsIgnoreCase("Health"))
                {
                    radioButton_health.setChecked(true);
                }
                else if(service.equalsIgnoreCase("Resource"))
                {
                    radioButton_resources.setChecked(true);
                }

                if(tags.equalsIgnoreCase("Men"))
                {
                    radioButton_men.setChecked(true);
                }
                else if(tags.equalsIgnoreCase("Woman"))
                {
                    radioButton_woman.setChecked(true);
                }
                else if(tags.equalsIgnoreCase("Youth/Kids"))
                {
                    radioButton_youth.setChecked(true);
                }
                else if(tags.equalsIgnoreCase("Disable/Senior"))
                {
                    radioButton_disable.setChecked(true);
                }
                else if(tags.equalsIgnoreCase("Anyone"))
                {
                    radioButton_anyone.setChecked(true);
                }

                if(scheduletype.equalsIgnoreCase("Open 24/7") || scheduletype.equalsIgnoreCase("Permanently Closed") || scheduletype.equalsIgnoreCase("Please contact this service"))
                {
                    post_selectscheduletype.setText(scheduletype);

                    layoutstartdate.setVisibility(View.GONE);
                    layoutenddate.setVisibility(View.GONE);
                    layoutstarttime.setVisibility(View.GONE);
                    layoutendtime.setVisibility(View.GONE);
                }
                else
                {

                        PostsRef.child("scheduletype").addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                startdate = dataSnapshot.child("startdate").getValue().toString();
                                enddate = dataSnapshot.child("enddate").getValue().toString();
                                starttime = dataSnapshot.child("starttime").getValue().toString();
                                endtime = dataSnapshot.child("endtime").getValue().toString();

                                String scheduletype1 = dataSnapshot.child("scheduletype1").getValue().toString();

                                post_selectscheduletype.setText(scheduletype1);

                                layoutstartdate.setVisibility(View.VISIBLE);
                                layoutenddate.setVisibility(View.VISIBLE);
                                layoutstarttime.setVisibility(View.VISIBLE);
                                layoutendtime.setVisibility(View.VISIBLE);

                                edit_selectstartdate.setText(startdate);
                                edit_selectenddate.setText(enddate);
                                edit_starttime.setText(starttime);
                                edit_endtime.setText(endtime);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void scheduletype()
    {
        final CharSequence options[] = new CharSequence[]
                {
                        "Open 24/7",
                        "Date Range",
                        "Please contact this service",
                        "Permanently Closed"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditMyAgencyPost.this);
        builder.setTitle("Please Select One");

        builder.setIcon(R.drawable.ic_list);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    post_selectscheduletype.setText(options[0]);
                    layoutstartdate.setVisibility(View.GONE);
                    layoutenddate.setVisibility(View.GONE);
                    layoutstarttime.setVisibility(View.GONE);
                    layoutendtime.setVisibility(View.GONE);
                }
                if (which == 1)
                {
                    post_selectscheduletype.setText(options[1]);
                    layoutstartdate.setVisibility(View.VISIBLE);
                    layoutenddate.setVisibility(View.VISIBLE);
                    layoutstarttime.setVisibility(View.VISIBLE);
                    layoutendtime.setVisibility(View.VISIBLE);
                }
                if (which == 2)
                {
                    post_selectscheduletype.setText(options[2]);
                    layoutstartdate.setVisibility(View.GONE);
                    layoutenddate.setVisibility(View.GONE);
                    layoutstarttime.setVisibility(View.GONE);
                    layoutendtime.setVisibility(View.GONE);
                }
                if (which == 3)
                {
                    post_selectscheduletype.setText(options[3]);
                    layoutstartdate.setVisibility(View.GONE);
                    layoutenddate.setVisibility(View.GONE);
                    layoutstarttime.setVisibility(View.GONE);
                    layoutendtime.setVisibility(View.GONE);
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
        scheduletype = post_selectscheduletype.getText().toString();

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
        if(scheduletype.equals("Schedule Type"))
        {
            post_selectscheduletype.setError("Please choose schedule type");
            post_selectscheduletype.requestFocus();
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
                    if (dataSnapshot.exists())
                    {
                        countPosts = dataSnapshot.getChildrenCount();

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

                        final HashMap postMap = new HashMap();
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

                        PostsRef.updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditMyAgencyPost.this, "Post update successfully ", Toast.LENGTH_SHORT).show();

                                    progressDialog.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            SendUserToMainActivity();
                                            progressDialog.dismiss();


                                        }
                                    }, 2000);
                                }
                                else {
                                    Log.d("LOGGER", "No such document");
                                }
                            }
                        });

                        if(scheduletype.equalsIgnoreCase("Date Range"))
                        {
                            startdate = edit_selectstartdate.getText().toString();
                            enddate = edit_selectenddate.getText().toString();
                            starttime = edit_starttime.getText().toString();
                            endtime = edit_endtime.getText().toString();

                            HashMap tabletime = new HashMap();
                            tabletime.put("scheduletype1", scheduletype);
                            tabletime.put("startdate", startdate);
                            tabletime.put("enddate", enddate);
                            tabletime.put("starttime", starttime);
                            tabletime.put("endtime", endtime);

                            PostsRef.child("scheduletype").setValue(tabletime);

                        } else if(scheduletype.equalsIgnoreCase("Open 24/7")) {

                            scheduletype = post_selectscheduletype.getText().toString();

                            HashMap scheduletype1 = new HashMap();
                            scheduletype1.put("scheduletype", scheduletype);

                            PostsRef.updateChildren(scheduletype1);

                        } else if(scheduletype.equalsIgnoreCase("Please contact this service")) {

                            scheduletype = post_selectscheduletype.getText().toString();

                            HashMap scheduletype1 = new HashMap();
                            scheduletype1.put("scheduletype", scheduletype);

                            PostsRef.updateChildren(scheduletype1);
                        } else if(scheduletype.equals("Permanently Closed")) {

                            scheduletype = post_selectscheduletype.getText().toString();

                            HashMap scheduletype1 = new HashMap();
                            scheduletype1.put("scheduletype", scheduletype);

                            PostsRef.updateChildren(scheduletype1);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void selectTimeFormat(int hour)
    {
        if(hour == 0)
        {
            hour +=12;
            format = "AM";
        }
        else if ( hour == 12)
        {

            format = "PM";
        }
        else if (hour > 12)
        {
            hour -= 12;
            format = "PM";
        }
        else {

            format = "AM";
        }

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
            case R.id.edit_selectstartdate:
                datePickerDialog = new DatePickerDialog(EditMyAgencyPost.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        monthOfYear = monthOfYear+1;
                        edit_selectstartdate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);

                    }
                }, year, month-1, day);
                datePickerDialog.show();
                break;
            case R.id.edit_selectenddate:
                datePickerDialog = new DatePickerDialog(EditMyAgencyPost.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        monthOfYear = monthOfYear+1;
                        edit_selectenddate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);

                    }
                }, year, month-1, day);
                datePickerDialog.show();
                break;
            case R.id.edit_starttime:
                timePickerDialog = new TimePickerDialog(EditMyAgencyPost.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        selectTimeFormat(hourOfDay);
                        edit_starttime.setText(hourOfDay + ":" + minute + " " + format);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
                break;
            case R.id.edit_endtime:
                timePickerDialog = new TimePickerDialog(EditMyAgencyPost.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        selectTimeFormat(hourOfDay);
                        edit_endtime.setText(hourOfDay + ":" + minute + " " + format);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
                break;
        }

    }
}

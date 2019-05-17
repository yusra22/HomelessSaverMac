package com.uyr.yusara.homelesssavermac.Homeless;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.uyr.yusara.homelesssavermac.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditMyHomelessPost extends AppCompatActivity implements View.OnClickListener {


    private ImageButton SelectPostImage,SelectPostImage2,SelectPostImage3;

    private Button UpdatePostButton;
    private EditText edit_fullname,edit_ic,edit_age,edit_location,edit_description;

    //Untuk Radio Button
    private String gender, martialstatus, illness;
    private RadioGroup genderchoice,martialchoice, illnesschoice;

    private RadioButton radioButton_mental,radioButton_disabled,radioButton_sillness,radioButton_other;
    private RadioButton radioButtonGenderoption,radioButtonMartialoption, radioButtonillnessoption;
    private RadioButton radioButton_male,radioButton_female;
    private RadioButton radioButton_single,radioButton_married,radioButton_divorced,radioButton_legallyseparated,radioButton_widowed;

    private String fullname,ic,age,location,description,image,image2,image3;
    private String PostKey;

    final static int gallerypick = 1, gallerypick2 = 2, gallerypick3 = 3;
    final int TAKE_PICTURE = 4,TAKE_PICTURE1 = 5,TAKE_PICTURE2 = 6;
    private Uri ImageUri,ImageUri2, ImageUri3;
    Bitmap photo,photo1,photo2;
    private String downloadUrl,uriurl,uriurl2,uriurl3;

    private FirebaseAuth mAuth;
    private String currentUserid;
    private DatabaseReference UsersRef,PeoplePostsRef,NotisRef;
    private StorageReference PostImageRef;

    private long countPosts = 0;

    private String saveCurrentDate, saveCurrentTime, postRandomName;

    private Toolbar mToolbar;
    private ProgressDialog progressDialog;

    //Auto COmplete
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 10;
    private String TAG = "Location Check";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_homeless_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
        PostImageRef = FirebaseStorage.getInstance().getReference();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        PeoplePostsRef = FirebaseDatabase.getInstance().getReference().child("People Report Post").child(PostKey);
        NotisRef = FirebaseDatabase.getInstance().getReference().child("Notification");


        SelectPostImage = (ImageButton) findViewById(R.id.select_post1);
        SelectPostImage2 = (ImageButton) findViewById(R.id.select_post2);
        SelectPostImage3 = (ImageButton) findViewById(R.id.select_post3);

        UpdatePostButton = (Button)findViewById(R.id.btnupdatepeoplepost);
        edit_fullname = (EditText)findViewById(R.id.edit_fullname);
        edit_ic = (EditText)findViewById(R.id.edit_ic);
        edit_age = (EditText)findViewById(R.id.edit_age);
        edit_location = (EditText)findViewById(R.id.edit_location);
        edit_description = (EditText)findViewById(R.id.edit_description);

        radioButton_mental = (RadioButton) findViewById(R.id.radioButton_mental);
        radioButton_disabled = (RadioButton) findViewById(R.id.radioButton_disabled);
        radioButton_sillness = (RadioButton) findViewById(R.id.radioButton_seriousillness);
        radioButton_other = (RadioButton) findViewById(R.id.radioButton_other);
        radioButton_male = (RadioButton) findViewById(R.id.radioButton_male);
        radioButton_female = (RadioButton) findViewById(R.id.radioButton_female);
        radioButton_single = (RadioButton)findViewById(R.id.radioButton_single);
        radioButton_married = (RadioButton)findViewById(R.id.radioButton_married);
        radioButton_divorced = (RadioButton)findViewById(R.id.radioButton_divorced);
        radioButton_legallyseparated = (RadioButton)findViewById(R.id.radioButton_legallyseparated);
        radioButton_widowed = (RadioButton)findViewById(R.id.radioButton_widowed);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Homeless Report Is Being Added...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        findViewById(R.id.select_post1).setOnClickListener(this);
        findViewById(R.id.select_post2).setOnClickListener(this);
        findViewById(R.id.select_post3).setOnClickListener(this);
        findViewById(R.id.btnupdatepeoplepost).setOnClickListener(this);
        findViewById(R.id.edit_location).setOnClickListener(this);

        //Custom Toolbar
        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Make Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        genderchoice = (RadioGroup)findViewById(R.id.genderchoice);
        genderchoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButtonGenderoption = genderchoice.findViewById(i);
                switch (i){
                    case R.id.radioButton_male:
                        gender = radioButtonGenderoption.getText().toString();
                        break;
                    case R.id.radioButton_female:
                        gender = radioButtonGenderoption.getText().toString();
                        break;

                    default:
                }
            }
        });
        int selectedId = genderchoice.getCheckedRadioButtonId();
        radioButtonGenderoption = (RadioButton) findViewById(selectedId);

        martialchoice = (RadioGroup)findViewById(R.id.martialchoice);
        martialchoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButtonMartialoption = martialchoice.findViewById(i);
                switch (i){
                    case R.id.radioButton_single:
                        martialstatus = radioButtonMartialoption.getText().toString();
                        break;
                    case R.id.radioButton_married:
                        martialstatus = radioButtonMartialoption.getText().toString();
                        break;
                    case R.id.radioButton_divorced:
                        martialstatus = radioButtonMartialoption.getText().toString();
                        break;
                    case R.id.radioButton_legallyseparated:
                        martialstatus = radioButtonMartialoption.getText().toString();
                        break;
                    case R.id.radioButton_widowed:
                        martialstatus = radioButtonMartialoption.getText().toString();
                        break;

                    default:
                }
            }
        });
        int selectedId2 = martialchoice.getCheckedRadioButtonId();
        radioButtonMartialoption = (RadioButton) findViewById(selectedId2);

        illnesschoice = (RadioGroup) findViewById(R.id.illnesschoice);
        illnesschoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                radioButtonillnessoption = illnesschoice.findViewById(i);
                switch (i){
                    case R.id.radioButton_mental:
                        illness = radioButtonillnessoption.getText().toString();
                        break;
                    case R.id.radioButton_disabled:
                        illness = radioButtonillnessoption.getText().toString();
                        break;
                    case R.id.radioButton_seriousillness:
                        illness = radioButtonillnessoption.getText().toString();
                        break;
                    case R.id.radioButton_other:
                        illness = radioButtonillnessoption.getText().toString();
                        break;

                    default:
                }
            }
        });
        int selectedId3 = illnesschoice.getCheckedRadioButtonId();
        radioButtonillnessoption = (RadioButton) findViewById(selectedId3);

        displaydata();

        //Close Keyboard
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

    public void UpdateBtnPost()
    {
        fullname = edit_fullname.getText().toString();
        ic = edit_ic.getText().toString();
        age = edit_age.getText().toString();
        location = edit_location.getText().toString();
        description = edit_description.getText().toString();


        if(fullname.isEmpty())
        {
            edit_fullname.setError("Please add agency name");
            edit_fullname.requestFocus();
            return;
        }
        if(age.isEmpty())
        {
            edit_age.setError("Please add the description");
            edit_age.requestFocus();
            return;
        }
        if(location.isEmpty())
        {
            edit_location.setError("Please add the location");
            edit_location.requestFocus();

        }
        if(description.isEmpty())
        {
            edit_description.setError("Please add the location");
            edit_description.requestFocus();
            Toast.makeText(this, "Please select your relationship",Toast.LENGTH_SHORT).show();
            return;

        }
        else
        {
            StoringImageToStorage();
        }
    }


    private void StoringImageToStorage()
    {
        progressDialog.show();

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = PostImageRef.child("Homeless Reported Images").child(ImageUri + postRandomName + ".jpg");
        final StorageReference filePath2 = PostImageRef.child("Homeless Reported Images").child(ImageUri2 + postRandomName + ".jpg");
        final StorageReference filePath3 = PostImageRef.child("Homeless Reported Images").child(ImageUri3 + postRandomName + ".jpg");




        if(photo != null){

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] b = stream.toByteArray();

            filePath.putBytes(b).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {

                    if(task.isSuccessful())
                    {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                Toast.makeText(EditMyHomelessPost.this, "Image uploaded success..",Toast.LENGTH_SHORT).show();
                                uriurl = uri.toString();
                                downloadUrl = filePath.getDownloadUrl().toString();
                                SavingPostInformationToDB();
                            }
                        });
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(EditMyHomelessPost.this, "Error occurt" + message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(ImageUri !=null)
        {
            filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {

                    if(task.isSuccessful())
                    {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                Toast.makeText(EditMyHomelessPost.this, "Image uploaded success..",Toast.LENGTH_SHORT).show();
                                uriurl = uri.toString();
                                downloadUrl = filePath.getDownloadUrl().toString();
                            }
                        });
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(EditMyHomelessPost.this, "Error occurt" + message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(ImageUri2 !=null) {
            filePath2.putFile(ImageUri2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        filePath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uriurl2 = uri.toString();
                                downloadUrl = filePath.getDownloadUrl().toString();
                            }
                        });
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(EditMyHomelessPost.this, "Error occurt" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        filePath3.putFile(ImageUri3).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    filePath3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            uriurl3 = uri.toString();
                            downloadUrl = filePath.getDownloadUrl().toString();
                            SavingPostInformationToDB();
                        }
                    });
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(EditMyHomelessPost.this, "Error occurt" + message,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingPostInformationToDB() {

        //Untuk Susun
        PeoplePostsRef.addValueEventListener(new ValueEventListener() {
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

                        final HashMap postMap = new HashMap();
                        postMap.put("uid", currentUserid);
                        postMap.put("date", saveCurrentDate);
                        postMap.put("time", saveCurrentTime);
                        postMap.put("counter", countPosts);
                        postMap.put("postImage", uriurl);
                        postMap.put("postimage2", uriurl2);
                        postMap.put("postImage3", uriurl3);
                        postMap.put("fullname", fullname);
                        postMap.put("ic", ic);
                        postMap.put("age", age);
                        postMap.put("location",location);
                        postMap.put("illness",illness);
                        postMap.put("gender",gender);
                        postMap.put("martialstatus",martialstatus);
                        postMap.put("description",description);

                        PeoplePostsRef.updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {

                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditMyHomelessPost.this, "Post update successfully ", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                    HashMap postnotification = new HashMap();
                                    postnotification.put("from", currentUserid);
                                    postnotification.put("type", "new post noti");

                                    NotisRef.updateChildren(postnotification);
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

    private void displaydata() {

        PeoplePostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                fullname = dataSnapshot.child("fullname").getValue().toString();
                ic = dataSnapshot.child("ic").getValue().toString();
                age = dataSnapshot.child("age").getValue().toString();
                location = dataSnapshot.child("location").getValue().toString();
                illness = dataSnapshot.child("illness").getValue().toString();
                gender = dataSnapshot.child("gender").getValue().toString();
                martialstatus = dataSnapshot.child("martialstatus").getValue().toString();
                description = dataSnapshot.child("description").getValue().toString();
                image = dataSnapshot.child("postImage").getValue().toString();
                image2 = dataSnapshot.child("postimage2").getValue().toString();
                image3 = dataSnapshot.child("postImage3").getValue().toString();

                //Picasso.get().load(image).placeholder(R.drawable.cc).into(ProfileImage);


                edit_fullname.setText(fullname);
                edit_ic.setText(ic);
                edit_age.setText(age);
                edit_location.setText(location);
                edit_description.setText(description);

                Glide.with(EditMyHomelessPost.this).load(image).into(SelectPostImage);
                Glide.with(EditMyHomelessPost.this).load(image2).into(SelectPostImage2);
                Glide.with(EditMyHomelessPost.this).load(image3).into(SelectPostImage3);

                if(illness.equalsIgnoreCase("Mental"))
                {
                    radioButton_mental.setChecked(true);

                }
                else if (illness.equalsIgnoreCase("Disabled"))
                {
                    radioButton_disabled.setChecked(true);
                }
                else if (illness.equalsIgnoreCase("S.illness"))
                {
                    radioButton_sillness.setChecked(true);
                }
                else if (illness.equalsIgnoreCase("Others"))
                {
                    radioButton_other.setChecked(true);
                }


                if(gender.equalsIgnoreCase("Male"))
                {
                    radioButton_male.setChecked(true);
                }
                else if(gender.equalsIgnoreCase("Female"))
                {
                    radioButton_female.setChecked(true);
                }

                if(martialstatus.equalsIgnoreCase("Single"))
                {
                    radioButton_single.setChecked(true);
                }
                else if(martialstatus.equalsIgnoreCase("Married"))
                {
                    radioButton_married.setChecked(true);
                }
                else if(martialstatus.equalsIgnoreCase("Divorced"))
                {
                    radioButton_divorced.setChecked(true);
                }
                else if(martialstatus.equalsIgnoreCase("Legally separated"))
                {
                    radioButton_legallyseparated.setChecked(true);
                }
                else if(martialstatus.equalsIgnoreCase("Widowed"))
                {
                    radioButton_widowed.setChecked(true);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AutoCompleteMap() {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(EditMyHomelessPost.this); // notice CrateRide.this
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_PICTURE && resultCode == RESULT_OK && data!= null)
        {
            //Display user in image button
            //ImageUri = data.getData();
            //SelectPostImage.setImageURI(ImageUri);
            photo = (Bitmap) data.getExtras().get("data");
            SelectPostImage.setImageBitmap(photo);

        }
        if(requestCode == TAKE_PICTURE1 && resultCode == RESULT_OK && data!= null)
        {
            //Display user in image button
            //ImageUri = data.getData();
            //SelectPostImage.setImageURI(ImageUri);
            photo1 = (Bitmap)data.getExtras().get("data");
            SelectPostImage2.setImageBitmap(photo1);

        }
        if(requestCode == gallerypick && resultCode == RESULT_OK && data!= null)
        {
            //Display user in image button
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);

        }
        if(requestCode == gallerypick2 && resultCode == RESULT_OK && data!= null)
        {
            ImageUri2 = data.getData();
            SelectPostImage2.setImageURI(ImageUri2);
        }
        if(requestCode == gallerypick3 && resultCode == RESULT_OK && data!= null)
        {
            ImageUri3 = data.getData();
            SelectPostImage3.setImageURI(ImageUri3);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                ImageUri = result.getUri();
                SelectPostImage.setImageURI(ImageUri);

            }
            if (resultCode == RESULT_OK && requestCode == gallerypick2) {

                ImageUri2 = result.getUri();
                SelectPostImage.setImageURI(ImageUri2);

            }/*else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }*/
        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                edit_location.setText(place.getAddress());
                Log.i(TAG, "Place: " + place.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


    }

    public void OpenGalleryOrCamera()
    {

        final CharSequence options[] = new CharSequence[]
                {
                        "Gallery",
                        "Camera"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditMyHomelessPost.this);
        builder.setTitle("From Gallery or Camera");

        builder.setIcon(R.drawable.photo);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, gallerypick);

                }
                if (which == 1)
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PICTURE);
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void OpenGallery1()
    {
/*        final CharSequence options[] = new CharSequence[]
                {
                        "Gallery",
                        "Camera"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddHomelessInfo.this);
        builder.setTitle("From Gallery or Camera");

        builder.setIcon(R.drawable.photo);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, gallerypick2);

                }
                if (which == 1)
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PICTURE1);
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();*/

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, gallerypick2);

    }

    public void OpenGallery2()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, gallerypick3);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.select_post1:
                //OpenGallery();
                OpenGalleryOrCamera();
                break;
            case R.id.select_post2:
                OpenGallery1();
                break;
            case R.id.select_post3:
                OpenGallery2();
                break;
            case R.id.btnupdatepeoplepost:
                UpdateBtnPost();
                break;
            case R.id.edit_location:
                AutoCompleteMap();
                break;
        }
    }
}

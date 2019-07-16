package com.uyr.yusara.homelesssavermac.Homeless;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.msoftworks.easynotify.EasyNotify;
import com.santalu.maskedittext.MaskEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.uyr.yusara.homelesssavermac.Agency.AddServices;
import com.uyr.yusara.homelesssavermac.MainActivity;
import com.uyr.yusara.homelesssavermac.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;
import pyxis.uzuki.live.mediaresizer.MediaResizer;
import pyxis.uzuki.live.mediaresizer.data.ImageResizeOption;
import pyxis.uzuki.live.mediaresizer.data.ResizeOption;
import pyxis.uzuki.live.mediaresizer.model.ImageMode;
import pyxis.uzuki.live.mediaresizer.model.MediaType;
import pyxis.uzuki.live.mediaresizer.model.ScanRequest;

public class AddHomelessInfo extends AppCompatActivity implements View.OnClickListener {

    private ImageButton SelectPostImage,SelectPostImage2,SelectPostImage3;
    private Button UpdatePeoplePostButton;
    private EditText edit_fullname,edit_age,edit_location,edit_description; //edit_ic
    private MaskEditText edit_ic;


    //Untuk Radio Button
    private String gender, martialstatus,illness;
    private RadioGroup genderchoice,martialchoice,illnesschoice;
    private RadioButton radioButtonGenderoption,radioButtonMartialoption,radioButtonillnessoption;

    private String fullname,ic,age,location,description;

    //Images files
    final static int gallerypick = 1, gallerypick2 = 2, gallerypick3 = 3;
    final int TAKE_PICTURE = 4,TAKE_PICTURE1 = 5,TAKE_PICTURE2 = 6;
    private Uri ImageUri,ImageUri2, ImageUri3;
    Bitmap photo,photo1,photo2;
    private String downloadUrl,uriurl,uriurl2,uriurl3;

    //For compress
    Bitmap thumb_bitmap = null;

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
        setContentView(R.layout.activity_add_homeless_info);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
        PostImageRef = FirebaseStorage.getInstance().getReference();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        PeoplePostsRef = FirebaseDatabase.getInstance().getReference().child("People Report Post");
        NotisRef = FirebaseDatabase.getInstance().getReference().child("Notification");

        SelectPostImage = (ImageButton) findViewById(R.id.select_post1);
        SelectPostImage2 = (ImageButton) findViewById(R.id.select_post2);
        SelectPostImage3 = (ImageButton) findViewById(R.id.select_post3);

        UpdatePeoplePostButton = (Button)findViewById(R.id.btnupdatepeoplepost);
        edit_fullname = (EditText)findViewById(R.id.edit_fullname);
        edit_ic = (MaskEditText) findViewById(R.id.edit_ic);
        edit_age = (EditText)findViewById(R.id.edit_age);
        edit_location = (EditText)findViewById(R.id.edit_location);
        edit_description = (EditText)findViewById(R.id.edit_description);


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
        getSupportActionBar().setTitle("Add Homeless");
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

/*        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(1996, 5, 3);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        Toast.makeText(AddHomelessInfo.this, ageS,Toast.LENGTH_SHORT).show();*/

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
        if(requestCode == TAKE_PICTURE1)
        {
            //Display user in image button
            //ImageUri = data.getData();
            //SelectPostImage.setImageURI(ImageUri);

            Bundle extras = data.getExtras();
            photo1 = (Bitmap) extras.get("data");
            SelectPostImage2.setImageBitmap(photo1);
            Toast.makeText(AddHomelessInfo.this, "camera 2 Selected!", Toast.LENGTH_SHORT).show();

        }
        if(requestCode == TAKE_PICTURE2)
        {
            //Display user in image button
            //ImageUri = data.getData();
            //SelectPostImage.setImageURI(ImageUri);

            Bundle extras = data.getExtras();
            photo2 = (Bitmap) extras.get("data");
            SelectPostImage3.setImageBitmap(photo2);
            Toast.makeText(AddHomelessInfo.this, "camera 3 Selected!", Toast.LENGTH_SHORT).show();

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
        final CharSequence options[] = new CharSequence[]
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
        alert.show();

/*        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, gallerypick2);*/

    }

    public void OpenGallery2()
    {

        final CharSequence options[] = new CharSequence[]
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
                    startActivityForResult(galleryIntent, gallerypick3);

                }
                if (which == 1)
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PICTURE2);
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }


    public void UpdateBtnPost()
    {
        fullname = edit_fullname.getText().toString().toLowerCase();
        ic = edit_ic.getText().toString();
        age = edit_age.getText().toString();
        location = edit_location.getText().toString();
        description = edit_description.getText().toString().toLowerCase();


        if(fullname.isEmpty())
        {
            edit_fullname.setError("Please add the name");
            edit_fullname.requestFocus();
            return;
        }
        if(age.isEmpty())
        {
            edit_age.setError("Please add the age");
            edit_age.requestFocus();
            return;
        }
        if(location.isEmpty())
        {
            edit_location.setError("Please add the location");
            edit_location.requestFocus();
            return;

        }
        if(description.isEmpty())
        {
            edit_description.setError("Please add the description");
            edit_description.requestFocus();
            Toast.makeText(this, "Please insert description",Toast.LENGTH_SHORT).show();
            return;

        }
        if(genderchoice.getCheckedRadioButtonId()==-1)
        {
            Toast.makeText(this, "Please select gender",Toast.LENGTH_SHORT).show();
            return;

        }
        if(illnesschoice.getCheckedRadioButtonId()==-1)
        {
            Toast.makeText(this, "Please select illness",Toast.LENGTH_SHORT).show();
            return;

        }
        if(martialchoice.getCheckedRadioButtonId()==-1)
        {
            Toast.makeText(this, "Please select martial status",Toast.LENGTH_SHORT).show();
            return;

        }
        else
        {
            StoringImageToStorage();
        }
    }


    private void StoringImageToStorage()
    {
        //

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = PostImageRef.child("Homeless Reported Images").child(ImageUri + postRandomName + "cameraorgallery.jpg");
        final StorageReference filePath2 = PostImageRef.child("Homeless Reported Images").child(ImageUri2 + postRandomName + "camera1orgallery1.jpg");
        final StorageReference filePath3 = PostImageRef.child("Homeless Reported Images").child(ImageUri3 + postRandomName + "camera2orgallery2.jpg");

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
                                uriurl = uri.toString();
                                downloadUrl = filePath.getDownloadUrl().toString();
                            }
                        });
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(AddHomelessInfo.this, "Error occurt" + message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(ImageUri !=null) {

/*            File actualImage = new File(ImageUri.getPath());

            byte[] final_image;
            Bitmap compressedImage = null;

            try {
                compressedImage = new Compressor(this)
                        .setMaxHeight(300)
                        .setMaxWidth(300)
                        .setQuality(80)
                        .compressToBitmap(actualImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                final_image = baos.toByteArray();

                final UploadTask uploadTask = filePath.putBytes(final_image);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        uriurl = taskSnapshot.toString();
                        downloadUrl = filePath.getDownloadUrl().toString();

                        callEasyNotify();
                        SavingPostInformationToDB();
                        Toast.makeText(AddHomelessInfo.this, "Post upload successfully ", Toast.LENGTH_SHORT).show();

                    }
                });*/


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
                                uriurl = uri.toString();
                                downloadUrl = filePath.getDownloadUrl().toString();

                            }
                        });
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(AddHomelessInfo.this, "Error occurt" + message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(photo1 != null){

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo1.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] b = stream.toByteArray();

            filePath2.putBytes(b).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task3)
                {

                    if(task3.isSuccessful())
                    {
                        Toast.makeText(AddHomelessInfo.this, "Camera 2 upload success", Toast.LENGTH_SHORT).show();
                        filePath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri2)
                            {

                                uriurl2 = uri2.toString();
                                downloadUrl = filePath2.getDownloadUrl().toString();

                            }
                        });
                    }
                    else
                    {
                        String message = task3.getException().getMessage();
                        Toast.makeText(AddHomelessInfo.this, "Error occurt" + message,Toast.LENGTH_SHORT).show();
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
                            public void onSuccess(Uri uri2) {
                                uriurl2 = uri2.toString();
                                downloadUrl = filePath2.getDownloadUrl().toString();
                            }
                        });
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(AddHomelessInfo.this, "Error occurt" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(photo2 != null){

            progressDialog.show();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo2.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] b = stream.toByteArray();

            filePath3.putBytes(b).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task3)
                {

                    if(task3.isSuccessful())
                    {
                        Toast.makeText(AddHomelessInfo.this, "Camera 3 upload success", Toast.LENGTH_SHORT).show();
                        filePath3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri2)
                            {

                                uriurl3 = uri2.toString();
                                downloadUrl = filePath3.getDownloadUrl().toString();

                                //last image insert bru save ke db :D

                                SavingPostInformationToDB();
                            }
                        });
                    }
                    else
                    {
                        String message = task3.getException().getMessage();
                        Toast.makeText(AddHomelessInfo.this, "Error occurt" + message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (ImageUri3 !=null)
        {
            progressDialog.show();

            filePath3.putFile(ImageUri3).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        filePath3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri3)
                            {
                                uriurl3 = uri3.toString();
                                downloadUrl = filePath3.getDownloadUrl().toString();

                                //last image insert bru save ke db :D

                                SavingPostInformationToDB();
                            }
                        });
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(AddHomelessInfo.this, "Error occurt" + message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

/*        String empty_picture = "http://www.sclance.com/pngs/no-image-png/no_image_png_935205.png";

        if(photo == null || ImageUri == null && photo2 == null || ImageUri2 == null && photo2 == null || ImageUri3 == null)
        {
            uriurl = empty_picture;
            uriurl2 = empty_picture;
            uriurl3 = empty_picture;
            SavingPostInformationToDB();
        }
        if(photo == null || ImageUri == null && photo2 == null || ImageUri2 == null && photo2 != null || ImageUri3 != null)
        {
            uriurl = empty_picture;
            uriurl2 = empty_picture;
        }
        if(photo == null || ImageUri == null && photo2 != null || ImageUri2 != null && photo2 == null || ImageUri3 == null)
        {
            uriurl = empty_picture;
            uriurl3 = empty_picture;
        }*/
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
                        postMap.put("fullname", fullname);
                        postMap.put("ic", ic);
                        postMap.put("age", age);
                        postMap.put("location",location);
                        postMap.put("illness", illness);
                        postMap.put("gender",gender);
                        postMap.put("martialstatus",martialstatus);
                        postMap.put("description",description);
                        postMap.put("postImage", uriurl);
                        postMap.put("postimage2", uriurl2);
                        postMap.put("postImage3", uriurl3);

                        PeoplePostsRef.child(currentUserid + postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {

                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(AddHomelessInfo.this, "Post upload successfully ", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                    SendUserToMainActivity();
                                    callEasyNotify();

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

    private void SendUserToMainActivity()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    private void AutoCompleteMap() {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(AddHomelessInfo.this); // notice CrateRide.this
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void callEasyNotify() {
        EasyNotify easyNotify = new EasyNotify();
        easyNotify.setAPI_KEY("AAAA3fk9DIM:APA91bGEYuo47DI2_oRJR7hL8u58nj5WheDVqrBi989rSKXSrQz8Qf9spdbOeS-hTIDPTfRreD7xVdRj_omLhMQ3ETuJ_E_WgV9ctgwwPBSC4rpK64ku0z006WFlg6xPf9PgwKAyrqPm");
        easyNotify.setNtitle("New Homeless Need help!");
        easyNotify.setNbody( "Check out for the name " + fullname);
        easyNotify.setNtopic("news");
        easyNotify.setNclick_action("mainActivity");
        easyNotify.setNsound("default");
        easyNotify.nPush();
        easyNotify.setEasyNotifyListener(new EasyNotify.EasyNotifyListener() {
            @Override
            public void onNotifySuccess(String s) {
                Toast.makeText(AddHomelessInfo.this, "success" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotifyError(String s) {
                Toast.makeText(AddHomelessInfo.this, "error" + s, Toast.LENGTH_SHORT).show();
            }

        });
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

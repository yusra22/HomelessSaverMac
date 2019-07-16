package com.uyr.yusara.homelesssavermac.Homeless;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskedittext.MaskEditText;
import com.uyr.yusara.homelesssavermac.Agency.Agency_Details;
import com.uyr.yusara.homelesssavermac.Agency.Comment;
import com.uyr.yusara.homelesssavermac.R;
import com.uyr.yusara.homelesssavermac.StreetView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;
import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class activity_homeless_details extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitude, longitude;
    private int ProximityRadius = 10000;

    private String PostKey,img1,img2,img3,fullname,ic,age,location,illness,gender,martialstatus,description,posteruid,posterphoneno;

    private TextView Postfullname,Postage,Postlocation,Postillness,Postgender,Postmartialstatus,Postdescription;
    private MaskEditText Postic;
    //private TextView btnComment;
    private ImageView button_save,sharebtn,btncomment,bookmark;

    private DatabaseReference ClickPostRef, BookmarkRef, UsersRef;
    Boolean BookmarkChecker = false;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private Toolbar mToolbar;

    FloatingActionButton fab;

    TextView bookmarkbtn; //sharebtn;

    private MediaPlayer mp;

    FlipperLayout flipper;

    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeless_details);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Postfullname = findViewById(R.id.text_fullname);
        Postage = findViewById(R.id.text_ages);
        Postic = findViewById(R.id.text_ic);
        Postillness = findViewById(R.id.text_illness);
        Postgender = findViewById(R.id.text_gender);
        Postmartialstatus = findViewById(R.id.text_martialstatus);
        Postlocation = findViewById(R.id.text_location);
        Postdescription = findViewById(R.id.text_description);

        //mDemoSlider = (SliderLayout)findViewById(R.id.slider2);

        PostKey = getIntent().getExtras().get("PostKey").toString();

        //Underline kan location
        Postlocation.setPaintFlags(Postlocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        BookmarkRef = FirebaseDatabase.getInstance().getReference().child("BookmarksHomeless");
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("People Report Post").child(PostKey);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
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
                posteruid = dataSnapshot.child("uid").getValue().toString();

                Postfullname.setText(fullname.substring(0, 1).toUpperCase() + fullname.substring(1));
                Postage.setText("Age " + age);
                Postic.setText(ic);
                Postlocation.setText(location);
                Postillness.setText(illness);
                Postgender.setText(gender);
                Postmartialstatus.setText(martialstatus);
                Postdescription.setText(description);



                UsersRef.child(posteruid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        posterphoneno = dataSnapshot.child("phone").getValue().toString();

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

        fab = (FloatingActionButton) findViewById(R.id.save);
        button_save = (ImageView) findViewById(R.id.save);

        //Hilangkan fab tpi xpadam sebab mlas nk ubah
        fab.setVisibility(View.GONE);

        //sebelum berubah
        //btnComment = (TextView) findViewById(R.id.btncomment);
        btncomment = (ImageView) findViewById(R.id.btncomment);

        bookmark = (ImageView) findViewById(R.id.bookmark);

        //sharebtn = (TextView) findViewById(R.id.sharebtn);
        sharebtn = (ImageView) findViewById(R.id.sharebtn);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Homeless");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.btncomment).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        findViewById(R.id.bookmark).setOnClickListener(this);
        findViewById(R.id.sharebtn).setOnClickListener(this);
        findViewById(R.id.smsinformer).setOnClickListener(this);
        findViewById(R.id.callinformer).setOnClickListener(this);
        findViewById(R.id.text_location).setOnClickListener(this);

        mp = MediaPlayer.create(this, R.raw.pindrop);
        setLikeButtonStatus(PostKey);
        setLayout();

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }

    }

    public boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btncomment:
                Intent commentsIntent = new Intent(activity_homeless_details.this, CommentHomeless.class);
                commentsIntent.putExtra("PostKey", PostKey);
                startActivity(commentsIntent);
                Animatoo.animateFade(activity_homeless_details.this);
                break;
            case R.id.bookmark:
                Bookmark();
                break;
            case R.id.sharebtn:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "A homeless name " + Postfullname.getText() + " need our help. " + "Check out on Homeless Saver!";
                String shareSub = " Check out on Homeless Saver!";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
                break;
            case R.id.smsinformer:
                Intent message = new Intent( Intent.ACTION_VIEW, Uri.parse( "sms:" + posterphoneno ) );
                message.putExtra( "sms_body", "Hi, I like to help and know more details about Mr/Ms " + Postfullname.getText() );
                startActivity(message);
                break;
            case R.id.callinformer:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ posterphoneno));
                startActivity(intent);
                break;
            case R.id.text_location:
                String PostLat = String.valueOf(latitude);
                String PostLng = String.valueOf(longitude);
                Intent streetview = new Intent(activity_homeless_details.this, StreetView.class);
                streetview.putExtra("PostKey", PostLat);
                streetview.putExtra("PostKey2", PostLng);
                streetview.putExtra("PostKey3", location);
                startActivity(streetview);
                Animatoo.animateShrink(activity_homeless_details.this);
                break;
        }

    }

    private void Bookmark()
    {
        BookmarkChecker = true;

        BookmarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(BookmarkChecker.equals(true))
                {
                    if(dataSnapshot.child(currentUserid).child(PostKey).hasChild(currentUserid))
                    {
                        //BookmarkRef.child(PostKey).child(currentUserid).removeValue();
                        BookmarkRef.child(currentUserid).child(PostKey).child(currentUserid).removeValue();
                        BookmarkChecker = false;
                        mp.start();
                    }
                    else {

                        BookmarkRef.child(currentUserid).child(PostKey).child(currentUserid).setValue(true);
                        //BookmarkRef.child(PostKey).child(currentUserid).setValue(true);
                        BookmarkChecker = false;
                        Toast.makeText(activity_homeless_details.this, "Save clicked ! ...", Toast.LENGTH_SHORT).show();
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

    private void setLayout()
    {

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String  img1 = dataSnapshot.child("postImage").getValue().toString();
                String  img2 = dataSnapshot.child("postimage2").getValue().toString();
                String  img3 = dataSnapshot.child("postImage3").getValue().toString();

/*                HashMap<String,String> url_maps = new HashMap<String, String>();
                url_maps.put("Hannibal", img1);
                url_maps.put("Big Bang Theory", img2);
                url_maps.put("House of Cards", img3);

                for(String name : url_maps.keySet()){
                    TextSliderView textSliderView = new TextSliderView(activity_homeless_details.this);
                    // initialize a SliderLayout
                    textSliderView
                            .description(name)
                            .image(url_maps.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(activity_homeless_details.this);

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra",name);

                    mDemoSlider.addSlider(textSliderView);
                }

                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(4000);
                mDemoSlider.addOnPageChangeListener(activity_homeless_details.this);*/

;

                Slider slider = findViewById(R.id.slider);

                //create list of slides
                List<Slide> slideList = new ArrayList<>();
                slideList.add(new Slide(0,img1 , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
                slideList.add(new Slide(1,img2 , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
                slideList.add(new Slide(2,img3 , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));

                //handle slider click listener
                slider.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //do what you want
                    }
                });

                //add slides to slider
                slider.addSlides(slideList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void setLikeButtonStatus(final String PostKey)
    {
        BookmarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(currentUserid).child(PostKey).hasChild(currentUserid))
                {
                    //Toast.makeText(Agency_Details.this, "Color patut berubah ...", Toast.LENGTH_LONG).show();
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.sunflower)));
                    bookmark.setImageResource(R.drawable.wishlist3);
                }
                else {

                    //Toast.makeText(Agency_Details.this, "Color asl ...", Toast.LENGTH_SHORT).show();
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.sunset)));
                    bookmark.setImageResource(R.drawable.wishlist2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {

        lastlocation = location;

        if (currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }

        TextView addressField = findViewById(R.id.text_location);
        String address = addressField.getText().toString();

        List<Address> addressList = null;
        MarkerOptions userMarkerOptions = new MarkerOptions();

        if(!TextUtils.isEmpty(address))
        {
            Geocoder geocoder = new Geocoder(this);

            try
            {
                addressList = geocoder.getFromLocationName(address,6);

                if(addressList != null)
                {
                    for(int i=0; i<addressList.size(); i++)
                    {
                        Address userAddress = addressList.get(i);
                        LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                        userMarkerOptions.position(latLng);
                        userMarkerOptions.title(address);
                        userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        //mMap.addMarker(userMarkerOptions);

                        currentUserLocationMarker = mMap.addMarker(userMarkerOptions);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                        latitude = userAddress.getLatitude();
                        longitude = userAddress.getLongitude();
                    }
                }
                else
                {
                    Toast.makeText(this, "Location not found ...", Toast.LENGTH_LONG).show();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        else
        {
            Toast.makeText(this, "Please write any location name", Toast.LENGTH_LONG).show();
        }


        if (googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (LocationListener) this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}

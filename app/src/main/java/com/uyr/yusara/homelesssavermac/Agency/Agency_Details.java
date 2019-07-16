package com.uyr.yusara.homelesssavermac.Agency;

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
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
import com.uyr.yusara.homelesssavermac.Homeless.activity_homeless_details;
import com.uyr.yusara.homelesssavermac.Modal.Posts;
import com.uyr.yusara.homelesssavermac.R;
import com.uyr.yusara.homelesssavermac.StreetView;
import com.uyr.yusara.homelesssavermac.testpayment.paypaltest;

import java.io.IOException;
import java.util.List;

public class Agency_Details extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitude, longitude;
    private int ProximityRadius = 10000;

    private String PostKey,Agencyname,agencyname,categories,phoneno,email,website,facebook,twitter,location,schedule,tags;
    private String startdate,enddate,starttime,endtime;


    private TextView Postagencynametxt,Postcategoriestxt,Postemailtxt,Postwebsitetxt,Postfacebooktxt,Posttwittertxt,Postlocationtxt,PostScheduletxt,PostTagstxt;
    private MaskEditText Postphonenotxt;

    //For function comment,bookmark and share
    private ImageView btnComment,bookmark,sharebtn;

    //old fab bookmark yg xbuang
    FloatingActionButton fab;
    ImageView button_save;

    private DatabaseReference ClickPostRef, BookmarkRef,PaymentRef;
    Boolean BookmarkChecker = false;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private Toolbar mToolbar;

    private MediaPlayer mp;

    private TextView Postdonationreceive,intentdonatenow;

    private TextView callid;

    private Button btntest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency__details);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Postagencynametxt = findViewById(R.id.text_agencyname);
        Postcategoriestxt = findViewById(R.id.text_catergories);
        Postphonenotxt = findViewById(R.id.text_officenumber);
        Postemailtxt = findViewById(R.id.text_email);
        Postwebsitetxt = findViewById(R.id.text_website);
        Postfacebooktxt = findViewById(R.id.text_facebook);
        Posttwittertxt = findViewById(R.id.text_twitter);
        Postlocationtxt = findViewById(R.id.text_location);
        PostScheduletxt = findViewById(R.id.text_schedule);
        PostTagstxt = findViewById(R.id.text_tags);

        Postdonationreceive = findViewById(R.id.donationreceive);
        intentdonatenow = findViewById(R.id.donatenow);

        callid = (TextView)findViewById(R.id.text_officenumber);

        //btntest = (Button) findViewById(R.id.btntest);

        //Underline kan location
        Postlocationtxt.setPaintFlags(Postlocationtxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        //For function comment,bookmark and share
        btnComment = (ImageView) findViewById(R.id.btncomment);
        bookmark = (ImageView) findViewById(R.id.bookmark);
        sharebtn = (ImageView) findViewById(R.id.sharebtn);

        PostKey = getIntent().getExtras().get("PostKey").toString();
        //Agencyname = getIntent().getExtras().get("Agencyname").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        BookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmarks");

        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //final Posts phomeless = new Posts();

                agencyname = dataSnapshot.child("agencyname").getValue().toString();
                categories = dataSnapshot.child("categories").getValue().toString();
                phoneno = dataSnapshot.child("officenumber").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                website = dataSnapshot.child("website").getValue().toString();
                facebook = dataSnapshot.child("facebook").getValue().toString();
                twitter = dataSnapshot.child("twitter").getValue().toString();
                location = dataSnapshot.child("location").getValue().toString();
                tags = dataSnapshot.child("tags").getValue().toString();
                schedule = dataSnapshot.child("scheduletype").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

/*
                if(status.equalsIgnoreCase("accept"))
                {
                    btntest.setVisibility(View.GONE);
                }
*/

                Postagencynametxt.setText(agencyname.substring(0, 1).toUpperCase() + agencyname.substring(1));
                Postcategoriestxt.setText(categories.substring(0, 1).toUpperCase() + categories.substring(1));
                Postphonenotxt.setText(phoneno);
                Postemailtxt.setText(email);
                Postwebsitetxt.setText(website);
                Postfacebooktxt.setText(facebook);
                Posttwittertxt.setText(twitter);
                Postlocationtxt.setText(location);
                PostTagstxt.setText(tags);

                ClickPostRef.child("totalDonationReceive").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int sum = 0;
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            int value = ds.getValue(Integer.class);
                            sum =+ sum + value;
                        }

                        String a = String.valueOf(sum);
                        Postdonationreceive.setText("FUND: RM " + a);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(schedule.equalsIgnoreCase("Open 24/7") || schedule.equalsIgnoreCase("Permanently Closed") || schedule.equalsIgnoreCase("Please contact this service"))
                {
                    PostScheduletxt.setText(schedule);
                } else {

                    ClickPostRef.child("scheduletype").addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            startdate = dataSnapshot.child("startdate").getValue().toString();
                            enddate = dataSnapshot.child("enddate").getValue().toString();
                            starttime = dataSnapshot.child("starttime").getValue().toString();
                            endtime = dataSnapshot.child("endtime").getValue().toString();

                            PostScheduletxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                            PostScheduletxt.setText(startdate +" - "+ enddate +"    "+ starttime +" - "+ endtime);

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

        fab = (FloatingActionButton) findViewById(R.id.save);
        button_save = (ImageView) findViewById(R.id.save);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Service Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.btncomment).setOnClickListener(this);
        findViewById(R.id.bookmark).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.sharebtn).setOnClickListener(this);
        findViewById(R.id.donatenow).setOnClickListener(this);

        findViewById(R.id.text_officenumber).setOnClickListener(this);
        findViewById(R.id.text_location).setOnClickListener(this);

        mp = MediaPlayer.create(this, R.raw.pindrop);
        setBookmarkstatus(PostKey);

        //Hilangkan fab tpi xpadam sebab mlas nk ubah
        fab.setVisibility(View.GONE);

        //Close Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
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
            case R.id.text_officenumber:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+callid.getText()));
                startActivity(intent);
                break;
            case R.id.btncomment:
                Intent commentsIntent = new Intent(Agency_Details.this, Comment.class);
                commentsIntent.putExtra("PostKey", PostKey);
                startActivity(commentsIntent);
                Animatoo.animateFade(Agency_Details.this);
                break;
            case R.id.save:
                Bookmark();
                break;
            case R.id.bookmark:
                Bookmark();
                break;
            case R.id.sharebtn:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "The are new homeless community to share with you called " + Postagencynametxt.getText() + "." + "Check out on Homeless Saver!";
                String shareSub = " Check out on Homeless Saver!";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
                break;
            case R.id.donatenow:
                Intent donatationIntent = new Intent(Agency_Details.this, paypaltest.class);
                startActivity(donatationIntent);
                Animatoo.animateFade(Agency_Details.this);
                break;
            case R.id.text_location:
                String PostLat = String.valueOf(latitude);
                String PostLng = String.valueOf(longitude);
                Intent streetview = new Intent(Agency_Details.this, StreetView.class);
                streetview.putExtra("PostKey", PostLat);
                streetview.putExtra("PostKey2", PostLng);
                streetview.putExtra("PostKey3", location);
                startActivity(streetview);
                Animatoo.animateShrink(Agency_Details.this);
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
                        Toast.makeText(Agency_Details.this, "Save clicked ! ...", Toast.LENGTH_SHORT).show();
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

    public void setBookmarkstatus(final String PostKey)
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

/*        latitude = location.getLatitude();
        longitude = location.getLongitude();*/

        lastlocation = location;

        if (currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }

        //Old user current location
/*        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));*/

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
    public void onConnected(@Nullable Bundle bundle)
    {

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
}

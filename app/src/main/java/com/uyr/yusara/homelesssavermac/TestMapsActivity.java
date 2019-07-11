package com.uyr.yusara.homelesssavermac;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.Agency.AddServices;
import com.uyr.yusara.homelesssavermac.Agency.Agency_Details;
import com.uyr.yusara.homelesssavermac.Agency.MyAgencyPost;
import com.uyr.yusara.homelesssavermac.Homeless.activity_homeless_details;
import com.uyr.yusara.homelesssavermac.MapTest.UserInformation;
import com.uyr.yusara.homelesssavermac.Modal.Posts;
import com.uyr.yusara.homelesssavermac.Modal.Posts_Homeless;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestMapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    LatLng latLng;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers,mUsers2;
    Marker marker;
    private double latitude, longitude;

    private MediaPlayer mp;

    BottomSheetBehavior sheetBehavior;

    //@BindView(R.id.btn_bottom_sheet)
    Button btnBottomSheet;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    private TextView locationhomelessid, fullnameid, ageid, dateid, titleid;
    private Button moredetailsid;

    com.getbase.floatingactionbutton.FloatingActionButton fab1,fab2;

    private Toolbar mToolbar;
    String a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        //Nama post dlm db
        mUsers = FirebaseDatabase.getInstance().getReference("People Report Post");
        mUsers2 = FirebaseDatabase.getInstance().getReference("Posts");
        //mUsers.push().setValue(marker);

        mp = MediaPlayer.create(this, R.raw.imhere);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        titleid = (TextView)findViewById(R.id.titleid);
        fullnameid = (TextView) findViewById(R.id.fullnameid);
        locationhomelessid = (TextView) findViewById(R.id.locationhomelessid);
        ageid = (TextView) findViewById(R.id.ageid);
        dateid = (TextView) findViewById(R.id.dateid);
        moredetailsid = (Button) findViewById(R.id.moredetailsid);

        fab1 = findViewById(R.id.fab_action1);
        fab2 = findViewById(R.id.fab_action2);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MapView");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("MapActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapActivity", "Can't find style. Error: ", e);
        }

        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap = googleMap;


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconmarker2));
                communitymap();

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconmarker2));
                homelessmap();

            }
        });


        //communitymap();
        homelessmap();

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            return;
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

        locationRequest = new LocationRequest();
/*        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);*/
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        lastLocation = location;

        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!"));
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconmarker2));
        a = String.valueOf(location.getLatitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void homelessmap()
    {
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                //final MarkerOptions userMarkerOptions;

                for (final DataSnapshot s : dataSnapshot.getChildren())
                {
                    List<Address> addressList = null;

                    Geocoder geocoder = new Geocoder(TestMapsActivity.this);

                    final Posts_Homeless phomeless = s.getValue(Posts_Homeless.class);

                    final MarkerOptions userMarkerOptions = new MarkerOptions();

                    try
                    {
                        addressList = geocoder.getFromLocationName(phomeless.location,6);

                        if(addressList != null)
                        {
                            for(int i=0; i<addressList.size(); i++)
                            {
                                Address userAddress = addressList.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(phomeless.location);
                                userMarkerOptions.draggable(true);
                                userMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_poor3));

                                marker = mMap.addMarker(userMarkerOptions);
                                latitude = userAddress.getLatitude();
                                longitude = userAddress.getLongitude();

                                //locationhomelessid.setText(message);


                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(final Marker marker) {


                                        mp.start();
                                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                        final String a = marker.getTitle();
                                        //Toast.makeText(TestMapsActivity.this, a, Toast.LENGTH_LONG).show();


                                        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                                                    //Cara get parent dri children
                                                    final String father = ds.getKey();

                                                    String date = ds.child("date").getValue().toString();
                                                    String name = ds.child("fullname").getValue().toString();
                                                    String age = ds.child("age").getValue().toString();
                                                    String location = ds.child("location").getValue().toString();

                                                    if(a.equalsIgnoreCase(location))
                                                    {
                                                        //Toast.makeText(TestMapsActivity.this, a, Toast.LENGTH_LONG).show();
                                                        titleid.setText("Homeless Details");
                                                        dateid.setText(date);
                                                        fullnameid.setText(name);
                                                        ageid.setText("Age : " + age);
                                                        locationhomelessid.setText(location);

                                                        moredetailsid.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v)
                                                            {
                                                                Intent click_post = new Intent(TestMapsActivity.this,activity_homeless_details.class);
                                                                click_post.putExtra("PostKey", father);
                                                                startActivity(click_post);
                                                            }
                                                        });
                                                    }

                                                } return;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        /*UserInformation user = dataSnapshot.getValue(UserInformation.class);*/
                                        return false;
                                    }
                                });
                            }
                        }
                        else
                        {
                            Toast.makeText(TestMapsActivity.this, "Location not found ...", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    /*UserInformation user = s.getValue(UserInformation.class);
                    LatLng location=new LatLng(user.latitude,user.longitude);
                    mMap.addMarker(new MarkerOptions().position(location).title(user.name)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));*/
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void communitymap()
    {
        mUsers2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                //final MarkerOptions userMarkerOptions;

                for (final DataSnapshot s : dataSnapshot.getChildren())
                {
                    List<Address> addressList = null;

                    Geocoder geocoder = new Geocoder(TestMapsActivity.this);

                    final Posts communityPost = s.getValue(Posts.class);

                    final MarkerOptions userMarkerOptions = new MarkerOptions();

                    try
                    {
                        addressList = geocoder.getFromLocationName(communityPost.location,6);

                        if(addressList != null)
                        {
                            for(int i=0; i<addressList.size(); i++)
                            {
                                Address userAddress = addressList.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(communityPost.location);
                                userMarkerOptions.draggable(true);
                                userMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_communitymap));

                                marker = mMap.addMarker(userMarkerOptions);
                                latitude = userAddress.getLatitude();
                                longitude = userAddress.getLongitude();

                                //locationhomelessid.setText(message);


                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(final Marker marker) {

                                        mp.start();
                                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                        final String a = marker.getTitle();
                                        //Toast.makeText(TestMapsActivity.this, a, Toast.LENGTH_LONG).show();


                                        mUsers2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                                                    //Cara get parent dri children
                                                    final String father = ds.getKey();

                                                    String date = ds.child("date").getValue().toString();
                                                    String agencyname = ds.child("agencyname").getValue().toString();
                                                    String service = ds.child("service").getValue().toString();
                                                    String location = ds.child("location").getValue().toString();

                                                    if(a.equalsIgnoreCase(location))
                                                    {
                                                        //Toast.makeText(TestMapsActivity.this, a, Toast.LENGTH_LONG).show();
                                                        titleid.setText("Community Details");
                                                        dateid.setText(date);
                                                        fullnameid.setText(agencyname);
                                                        ageid.setText("Service Provided : " + service);
                                                        locationhomelessid.setText(location);

                                                        moredetailsid.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v)
                                                            {
                                                                Intent click_post = new Intent(TestMapsActivity.this,Agency_Details.class);
                                                                click_post.putExtra("PostKey", father);
                                                                startActivity(click_post);
                                                                Animatoo.animateFade(TestMapsActivity.this);
                                                            }
                                                        });
                                                    }

                                                } return;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        return false;
                                    }
                                });
                            }
                        }
                        else
                        {
                            Toast.makeText(TestMapsActivity.this, "Location not found ...", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    /*UserInformation user = s.getValue(UserInformation.class);
                    LatLng location=new LatLng(user.latitude,user.longitude);
                    mMap.addMarker(new MarkerOptions().position(location).title(user.name)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));*/
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

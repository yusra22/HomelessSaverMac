package com.uyr.yusara.homelesssavermac;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.gms.maps.model.StreetViewSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.Modal.Users;

import java.util.Locale;

public class StreetView extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama streetViewPanorama;
    private boolean secondlocation=false;
    private TextToSpeech addressspeech;

    String PostLtd,PostLng,PostAddress;
    Double LtdText,LngText;

    private FirebaseAuth mAuth;
    private String currentUserid,name;

    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        SupportStreetViewPanoramaFragment supportStreetViewPanoramaFragment=(SupportStreetViewPanoramaFragment)getSupportFragmentManager()
                .findFragmentById(R.id.googlemapstreetview);


        PostLtd = getIntent().getExtras().get("PostKey").toString();
        PostLng = getIntent().getExtras().get("PostKey2").toString();
        PostAddress = getIntent().getExtras().get("PostKey3").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);


        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                name = dataSnapshot.child("name").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        supportStreetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        addressspeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR)
                {
                    addressspeech.setLanguage(Locale.UK);
                    String toSpeak = "You are now at," + PostAddress;
                    addressspeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);

                }
            }
        });

/*
        FloatingActionButton fabview = findViewById(R.id.fabview);
        fabview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondlocation=!secondlocation;
                onStreetViewPanoramaReady(streetViewPanorama);
            }
        });
*/

    }

    @Override
    protected void onPause() {
        if(addressspeech!=null)
        {
            addressspeech.stop();
            addressspeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

        LtdText = Double.parseDouble(PostLtd);
        LngText = Double.parseDouble(PostLng);

        streetViewPanorama.setPosition(new LatLng(LtdText, LngText));

/*
        streetViewPanorama=streetViewPanorama;
        if(secondlocation){

            //streetViewPanorama.setPosition(new LatLng(3.161470,101.678890),StreetViewSource.DEFAULT);

        }
        else {

            streetViewPanorama.setPosition(new LatLng(3.161470, 101.678890));
        }
*/

        streetViewPanorama.setStreetNamesEnabled(true);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(true);
        streetViewPanorama.setUserNavigationEnabled(true);
        streetViewPanorama.animateTo(
                new StreetViewPanoramaCamera.Builder().orientation(new StreetViewPanoramaOrientation(20,20))
                .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                .build(), 2000
        );
        streetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(panoramachangelistener);

    }

    private StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener panoramachangelistener = new StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener() {
        @Override
        public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera) {
            Toast.makeText(StreetView.this, "Locaetd updated", Toast.LENGTH_SHORT);
        }
    };
}

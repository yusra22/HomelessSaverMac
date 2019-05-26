package com.uyr.yusara.homelesssavermac;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.Agency.AddServices;
import com.uyr.yusara.homelesssavermac.Agency.Agency_post;
import com.uyr.yusara.homelesssavermac.Agency.MyAgencyPost;
import com.uyr.yusara.homelesssavermac.Favourites.MainFavourites;
import com.uyr.yusara.homelesssavermac.Homeless.AddHomelessInfo;
import com.uyr.yusara.homelesssavermac.Homeless.Homeless_post;
import com.uyr.yusara.homelesssavermac.Homeless.Myhomelesspost;
import com.uyr.yusara.homelesssavermac.Menu.Login;
import com.uyr.yusara.homelesssavermac.Modal.Users;
import com.uyr.yusara.homelesssavermac.testpayment.paypaltest;

import de.hdodenhof.circleimageview.CircleImageView;
import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private TextView userEmail;
    FirebaseUser firebaseUser;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private DatabaseReference UsersRef,allUserDatabaseRef;

    private CircleImageView NavProfileImage;

    private CardView homelessid,communityid,mapviewid,bookmarkid,aboutid;

    private static final int  permsRequestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] perms = {Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION};

        for (int i = 0; i < perms.length; i++) {
            if(ContextCompat.checkSelfPermission(this,perms[i])!=PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,perms, permsRequestCode);
                break;
            }
        }

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

/*        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));*/

        View navView = navigationView.getHeaderView(0);
        NavProfileImage = (CircleImageView)navView.findViewById(R.id.nav_image);


        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                    Menu menuNav;
                    MenuItem nav_itempads,nav_itemcomads,nav_itemaddservices,nav_itemaddreport;

                    Users usersData = dataSnapshot.child("Users").child(currentUserid).getValue(Users.class);

                    if(usersData.getRole().equalsIgnoreCase("Community"))
                    {
                        menuNav = navigationView.getMenu();
                        nav_itempads = menuNav.findItem(R.id.nav_peopleads);
                        nav_itemaddreport = menuNav.findItem(R.id.add_reports);

                        nav_itempads.setVisible(false);
                        nav_itemaddreport.setVisible(false);
                    } else if (usersData.getRole().equalsIgnoreCase("People"))
                    {
                        menuNav= navigationView.getMenu();
                        nav_itemcomads = menuNav.findItem(R.id.nav_communityads) ;
                        nav_itemaddservices = menuNav.findItem(R.id.add_services);

                        nav_itemcomads.setVisible(false);
                        nav_itemaddservices.setVisible(false);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UsersRef.child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profileimage2").getValue().toString();

                    Glide.with(MainActivity.this).load(image).into(NavProfileImage);
                    //Picasso.get().load(image).into(NavProfileImage);
                    //Toast.makeText(MainActivity.this, image, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        homelessid = (CardView) findViewById(R.id.homelessid);
        communityid = (CardView) findViewById(R.id.communityid);
        mapviewid = (CardView) findViewById(R.id.mapviewid);
        bookmarkid = (CardView) findViewById(R.id.bookmarkid);
        aboutid = (CardView) findViewById(R.id.aboutid);

        homelessid.setOnClickListener(this);
        communityid.setOnClickListener(this);
        mapviewid.setOnClickListener(this);
        bookmarkid.setOnClickListener(this);
        aboutid.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        userEmail = findViewById(R.id.txtemail);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userEmail.setText(firebaseUser.getEmail());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
/*        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
/*            Intent homeIntent = new Intent(MainActivity.this, imageslidertest.class);
            startActivity(homeIntent);
            finish();*/
            Intent home = new Intent(MainActivity.this, MainActivity.class);
            startActivity(home);
            finish();
            Animatoo.animateInAndOut(MainActivity.this);


        } else if (id == R.id.nav_profile) {

            Intent profile = new Intent(MainActivity.this, Profile.class);
            startActivity(profile);
            Animatoo.animateInAndOut(MainActivity.this);

        } else if (id == R.id.add_services) {

            Intent profile = new Intent(MainActivity.this, AddServices.class);
            startActivity(profile);
            Animatoo.animateInAndOut(MainActivity.this);

        } else if (id == R.id.add_reports) {

            Intent addhomelessinfo = new Intent(MainActivity.this, AddHomelessInfo.class);
            startActivity(addhomelessinfo);
            Animatoo.animateInAndOut(MainActivity.this);

        }else if (id == R.id.nav_peopleads){

            Intent post = new Intent(MainActivity.this, Myhomelesspost.class);
            startActivity(post);
            Animatoo.animateInAndOut(MainActivity.this);

        }else if (id == R.id.nav_find) {

            Intent postcrisis = new Intent(MainActivity.this, CrisisLine.class);
            startActivity(postcrisis);
            Animatoo.animateInAndOut(MainActivity.this);

        }else if (id == R.id.nav_donation) {

            Intent post = new Intent(MainActivity.this, paypaltest.class);
            startActivity(post);
            Animatoo.animateInAndOut(MainActivity.this);


        } else if (id == R.id.nav_communityads) {

            Intent find = new Intent(MainActivity.this,  MyAgencyPost.class);
            startActivity(find);
            Animatoo.animateInAndOut(MainActivity.this);
        } else if (id == R.id.nav_news) {

            Intent feedback = new Intent(MainActivity.this, Feedback.class);
            startActivity(feedback);
            Animatoo.animateInAndOut(MainActivity.this);

        } else if (id == R.id.nav_share) {

            /*Intent news = new Intent(MainActivity.this, Notification.class);
            startActivity(news);*/

            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String shareBody = "Homeless Saver.Now available at playstore!";
            String shareSub = "Now available at playstore!";
            myIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
            myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(myIntent, "Share Using"));

        }else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, Login.class));
            Animatoo.animateFade(MainActivity.this);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //ABout punye screen
    public void MyCustomAlertDialog(){
        final Dialog MyDialog = new Dialog(MainActivity.this);
        MyDialog.requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        MyDialog.setContentView(R.layout.about_dialog);
        MyDialog.setTitle("My Custom Dialog");

/*        ((ViewGroup)MyDialog.getWindow().getDecorView())
                .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                MainActivity.this,android.R.anim.slide_out_right));*/

        //hello = (Button)MyDialog.findViewById(R.id.hello);
        Button close = (Button)MyDialog.findViewById(R.id.closebtn);

        close.setEnabled(true);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();

            }
        });

        MyDialog.show();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.homelessid:

                Intent posth = new Intent(MainActivity.this, Homeless_post.class);
                startActivity(posth);
                Animatoo.animateZoom(MainActivity.this);

                break;
            case R.id.communityid:

                Intent post = new Intent(MainActivity.this, Agency_post.class);
                startActivity(post);
                Animatoo.animateZoom(MainActivity.this);
                break;
            case R.id.mapviewid:

                Intent test = new Intent(MainActivity.this, TestMapsActivity.class);
                startActivity(test);
                Animatoo.animateZoom( MainActivity.this);
                break;
            case R.id.bookmarkid:

                Intent fav = new Intent(MainActivity.this, MainFavourites.class);
                startActivity(fav);
                Animatoo.animateZoom(MainActivity.this);
                break;
            case R.id.aboutid:

                MyCustomAlertDialog();
                break;

        }

    }
}

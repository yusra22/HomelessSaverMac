package com.uyr.yusara.homelesssavermac;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.uyr.yusara.homelesssavermac.Homeless.AddHomelessInfo;
import com.uyr.yusara.homelesssavermac.Homeless.Myhomelesspost;
import com.uyr.yusara.homelesssavermac.ImageSliderTest.imageslidertest;
import com.uyr.yusara.homelesssavermac.Menu.Login;
import com.uyr.yusara.homelesssavermac.Modal.Users;
import com.uyr.yusara.homelesssavermac.News.NewsMainActivity;
import com.uyr.yusara.homelesssavermac.TestBottomSheet.MainTestBottomSheet;
import com.uyr.yusara.homelesssavermac.TestNotification.test_notification;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private TextView userEmail;
    FirebaseUser firebaseUser;

    private FirebaseAuth mAuth;
    private String currentUserid;

    private DatabaseReference UsersRef,allUserDatabaseRef;

    private CircleImageView NavProfileImage;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            Intent homeIntent = new Intent(MainActivity.this, imageslidertest.class);
            startActivity(homeIntent);
            finish();

        } else if (id == R.id.nav_profile) {

            Intent profile = new Intent(MainActivity.this, Profile.class);
            startActivity(profile);

        } else if (id == R.id.nav_mapview) {

            Intent test = new Intent(MainActivity.this, TestMapsActivity.class);
            startActivity(test);

        }else if (id == R.id.add_services) {

            Intent profile = new Intent(MainActivity.this, AddServices.class);
            startActivity(profile);

        } else if (id == R.id.add_reports) {

            Intent addhomelessinfo = new Intent(MainActivity.this, AddHomelessInfo.class);
            startActivity(addhomelessinfo);

        }else if (id == R.id.nav_onsell) {

            Intent post = new Intent(MainActivity.this, Agency_post.class);
            startActivity(post);

        } else if (id == R.id.nav_peopleads){

            Intent post = new Intent(MainActivity.this, Myhomelesspost.class);
            startActivity(post);

        }else if (id == R.id.nav_find) {



        } else if (id == R.id.nav_communityads) {

            Intent find = new Intent(MainActivity.this,  MyAgencyPost.class);
            startActivity(find);
        }
        else if (id == R.id.nav_fav) {

            Intent wish = new Intent(MainActivity.this, MyFavourites.class);
            startActivity(wish);

        } else if (id == R.id.nav_news) {

            Intent news = new Intent(MainActivity.this, NewsMainActivity.class);
            startActivity(news);

        } else if (id == R.id.nav_share) {

            Intent news = new Intent(MainActivity.this, MainTestBottomSheet.class);
            startActivity(news);

/*            Intent news = new Intent(MainActivity.this, test_notification.class);
            startActivity(news);*/

        } else if (id == R.id.nav_about) {

            MyCustomAlertDialog();

        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, Login.class));

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
    public void onClick(View view)
    {

    }
}

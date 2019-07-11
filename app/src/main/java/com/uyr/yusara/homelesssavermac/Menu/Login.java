package com.uyr.yusara.homelesssavermac.Menu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uyr.yusara.homelesssavermac.MainActivity;
import com.uyr.yusara.homelesssavermac.Modal.Users;
import com.uyr.yusara.homelesssavermac.R;

public class Login extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    EditText editTextEmail, editTextPassword;
    Button login;
    ProgressBar progressBar;

    private ImageView loginlogo;
    private static int SPLASH_TIME_OUT = 3000;

    private Toolbar mToolbar;

    private RelativeLayout R1,R2;
    private Animation uptodown,downtoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        if(mAuth.getCurrentUser() !=null)
        {
            DetermineRole();
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        login = (Button)findViewById(R.id.buttonLogin);

        findViewById(R.id.textViewSignup).setOnClickListener(this);
        findViewById(R.id.textResetPassword).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);

        loginlogo = findViewById(R.id.loginlogo);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.fadein);
        loginlogo.startAnimation(anim);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Sign In To Our System");

        // Untuk animation
        R1 = (RelativeLayout) findViewById(R.id.R1);
        R2 = (RelativeLayout) findViewById(R.id.R2);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        R1.setAnimation(uptodown);
        R2.setAnimation(downtoup);

/*        // CHange to gradient
        Drawable gradient = getResources().getDrawable( R.drawable.gradient_3);
        mToolbar.setBackgroundDrawable(gradient);
        login.setBackgroundDrawable(gradient);*/
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String roles = "0";

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }



        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful())
                        {
                            if (password.length() < 6)
                            {
                                editTextPassword.setError(getString(R.string.minimum_password));
                            }
                            else
                            {
                                Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            DetermineRole();
                            //Toast.makeText(Login.this, "Welcome", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void DetermineRole(){
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        final String deviceToken = FirebaseInstanceId.getInstance().getToken();

        //replace noti setiap tukar devices
        UserRef.child(uid).child("devicetoken").setValue(deviceToken);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference xx = db.getReference();

        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
        Animatoo.animateFade(Login.this);

/*        DatabaseReference rolecheck = FirebaseDatabase.getInstance().getReference();

        rolecheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users usersData = dataSnapshot.child("Users").child(uid).getValue(Users.class);

                if(usersData.getRole().equals("Disabled"))
                {
                    Toast.makeText(Login.this, "Your Account has been disabled", Toast.LENGTH_LONG).show();
                }
                else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignup:
                startActivity(new Intent(this, Register2.class));
                Animatoo.animateFade(Login.this);
                break;
            case R.id.buttonLogin:
                userLogin();
                break;
            case R.id.textResetPassword:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                Animatoo.animateFade(Login.this);
                break;
        }

    }
}
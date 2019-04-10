package com.uyr.yusara.homelesssavermac.Menu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uyr.yusara.homelesssavermac.Modal.Users;
import com.uyr.yusara.homelesssavermac.R;

public class Register2 extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private RadioGroup radioGroupRoles;
    private RadioButton radioButtonRolesoption;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private String role;

    //test push
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextPhone = findViewById(R.id.edit_text_phone);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        radioGroupRoles = (RadioGroup)findViewById(R.id.registerRoleRadioGroup);
        radioGroupRoles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButtonRolesoption = radioGroupRoles.findViewById(i);
                switch (i){
                    case R.id.radioButton_agent:
                        role = radioButtonRolesoption.getText().toString();
                        break;
                    case R.id.radioButton_customer:
                        role = radioButtonRolesoption.getText().toString();
                        break;

                    default:
                }
            }
        });
        int selectedId = radioGroupRoles.getCheckedRadioButtonId();
        radioButtonRolesoption = (RadioButton) findViewById(selectedId);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.button_register).setOnClickListener(this);

        //Close Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        role = radioButtonRolesoption.getText().toString();
        final String profileimages2 = "https://firebasestorage.googleapis.com/v0/b/dreamhome-a806d.appspot.com/o/profile%20Images%2Fnophoto.png?alt=media&token=724e2036-5419-4eef-bbeb-d8b0083ea123";
        final String profiledescription = "";
        final String devicetoken = FirebaseInstanceId.getInstance().getToken();

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.input_error_name));
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.input_error_email));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.input_error_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError(getString(R.string.input_error_phone));
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            editTextPhone.setError(getString(R.string.input_error_phone_invalid));
            editTextPhone.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            Users user = new Users(
                                    name,
                                    email,
                                    phone,
                                    role,
                                    profileimages2,
                                    profiledescription,
                                    devicetoken

                            );


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register2.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        finish();
                                        SendUserToLoginActivity();

                                    } else {
                                        //display a failure message
                                    }

                                }
                            });

                        } else {
                            Toast.makeText(Register2.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void SendUserToLoginActivity()
    {
        Intent mainIntent = new Intent(Register2.this, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

/*    public void next(View view)
    {
        Intent homeIntent = new Intent(Register2.this, TestFireBaseActivity.class);
        startActivity(homeIntent);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_register:
                registerUser();
                break;
        }
    }
}
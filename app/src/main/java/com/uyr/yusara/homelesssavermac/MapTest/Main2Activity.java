package com.uyr.yusara.homelesssavermac.MapTest;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.uyr.yusara.homelesssavermac.R;
import com.uyr.yusara.homelesssavermac.TestMapsActivity;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabase;
    private Button btnsave;
    private Button btnproceed;
    private EditText editTextName;
    private EditText editTextLatitude;
    private EditText editTextLongitude;
    CircleMenu circleMenu;

    String arrayname[] = { "ABD", "AUDIO", "CHILD", "ANDROID", "LAUCHER"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnproceed=(Button)findViewById(R.id.btnproceed);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Location");
        editTextName=(EditText)findViewById(R.id.editTextName);
        editTextLatitude=(EditText)findViewById(R.id.editTextLatitude);
        editTextLongitude=(EditText)findViewById(R.id.editTextLongitude);
        btnsave=(Button)findViewById(R.id.btnsave);

        findViewById(R.id.btnsave).setOnClickListener(this);

        btnproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Main2Activity.this,TestMapsActivity.class);
                startActivity(i);
            }
        });

        circleMenu = (CircleMenu) findViewById(R.id.circleshare);

        circleMenu=(CircleMenu)findViewById(R.id.circleshare);
        circleMenu.setMainMenu(Color.parseColor("#970024"),R.drawable.ic_menu_camera, R.drawable.ic_add)
                .addSubMenu(Color.parseColor("#970024"),R.drawable.ic_train)
                .addSubMenu(Color.parseColor("#970024"),R.drawable.ic_bathroom)
                .addSubMenu(Color.parseColor("#970024"),R.drawable.ic_approve)
                .addSubMenu(Color.parseColor("#970024"),R.drawable.ic_date)
                .addSubMenu(Color.parseColor("#970024"),R.drawable.ic_shop)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index)
                    {
                        Toast.makeText(Main2Activity.this, "you select" + arrayname, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveUserInformation(){
        String name =editTextName.getText().toString().trim();
        double latitude= Double.parseDouble(editTextLatitude.getText().toString().trim());
        double longitude= Double.parseDouble(editTextLongitude.getText().toString().trim());
        UserInformation userInformation=new UserInformation(name,latitude,longitude);
        mDatabase.child("Location1").setValue(userInformation);
        Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClick(View view) {
        if(view==btnproceed){
            finish();
        }
        if (view==btnsave){
            saveUserInformation();
            editTextName.getText().clear();
            editTextLatitude.getText().clear();
            editTextLongitude.getText().clear();
        }
    }
}

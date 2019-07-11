package com.uyr.yusara.homelesssavermac.testsms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uyr.yusara.homelesssavermac.R;

public class testsms extends AppCompatActivity {

    EditText numtxt,smstxt;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testsms);

        numtxt = (EditText)findViewById(R.id.numtxt);
        smstxt = (EditText)findViewById(R.id.smstxt);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(testsms.this,Manifest.permission.SEND_SMS);

                if(permissionCheck==PackageManager.PERMISSION_GRANTED)
                {
                    MyMessage();
                }
                else {

                    ActivityCompat.requestPermissions(testsms.this,new String[]{Manifest.permission.SEND_SMS},0);
                }

            }
        });

    }


    private void MyMessage() {

        String phoneNumber = numtxt.getText().toString().trim();
        String message = smstxt.getText().toString().trim();

        if(!numtxt.getText().toString().equals("") || smstxt.getText().toString().equals(""))
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,message,null,null);

            Toast.makeText(this,"Message Send", Toast.LENGTH_SHORT);
        }
        else {

            Toast.makeText(this,"Message cannot send", Toast.LENGTH_SHORT);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 0:

                if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    MyMessage();
                }
                else {

                    Toast.makeText(this,"You dont have permission", Toast.LENGTH_SHORT);
                }

                break;
        }
    }
}

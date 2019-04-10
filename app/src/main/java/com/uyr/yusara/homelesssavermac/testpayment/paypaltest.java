package com.uyr.yusara.homelesssavermac.testpayment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.uyr.yusara.homelesssavermac.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;

public class paypaltest extends AppCompatActivity {

    Button mPayment;
    TextView totalmoneyid,balanceid;
    private Double a, Balance = 0.0;

    private FirebaseAuth mAuth;
    private String currentUserid;
    private DatabaseReference PaymentRefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypaltest);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
        PaymentRefs = FirebaseDatabase.getInstance().getReference().child("Payment");

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        mPayment = (Button)findViewById(R.id.paymentid);
        totalmoneyid = (TextView) findViewById(R.id.totalmoneyid);
        balanceid = (TextView)findViewById(R.id.balanceid);

        a = 50.0;
        
        mPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPalPayment();
            }
        });

        PaymentRefs.child(currentUserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("customerPaid").getValue() != null && dataSnapshot.child("communityPayout").getValue() == null)
                {
                    balanceid.setText("Balance: " + String.valueOf(Balance) + "$" + " Details :");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private int PAYPAL_REQUEST_CODE = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENTID);

    private void payPalPayment()
    {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(a), "USD", "Homeless Donation",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                PaymentConfirmation comfirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(comfirm != null)
                {
                    try{
                        JSONObject jsonObject = new JSONObject(comfirm.toJSONObject().toString());

                        String paymentResponse = jsonObject.getJSONObject("response").getString("state");

                        if(paymentResponse.equals("approved"))
                        {
                            Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                            PaymentRefs.child(currentUserid).child("customerPaid").setValue(true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "Payment unsuccessful", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}

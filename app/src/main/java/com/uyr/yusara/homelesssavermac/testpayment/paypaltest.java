package com.uyr.yusara.homelesssavermac.testpayment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.uyr.yusara.homelesssavermac.Modal.Posts;
import com.uyr.yusara.homelesssavermac.R;
import com.uyr.yusara.homelesssavermac.testsms.testsms;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class paypaltest extends AppCompatActivity implements View.OnClickListener {

    Button mPayment;
    TextView totalmoneyid,balanceid;
    private int totaldonate, Balance = 0;

    private FirebaseAuth mAuth;
    private String currentUserid;
    private DatabaseReference PaymentRefs;
    private DatabaseReference UsersRef,Postsref;

    private Toolbar mToolbar;
    private RecyclerView postList;

    private ImageButton SearchButton;
    private EditText SearchInputText;

    private Button button10,button20,button30,button40,button50,button100;
    TextView test;
    String PostKey;
    String AgencyName;
    String DonaterName,DonaterEmail;

    String payid;

    private String saveCurrentDate, saveCurrentTime, postRandomName;

    LinearLayout recyclerviewresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypaltest);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();


        PaymentRefs = FirebaseDatabase.getInstance().getReference().child("Payment");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        Postsref = FirebaseDatabase.getInstance().getReference().child("Posts");

        SearchButton = (ImageButton) findViewById(R.id.search_community_button);
        SearchInputText = (EditText) findViewById(R.id.search_box_input);

        button10 = (Button) findViewById(R.id.button10);
        button20 = (Button) findViewById(R.id.button20);
        button30 = (Button) findViewById(R.id.button30);
        button40 = (Button) findViewById(R.id.button40);
        button50 = (Button) findViewById(R.id.button50);
        button100 = (Button) findViewById(R.id.button100);

        test = (TextView)findViewById(R.id.test);

        recyclerviewresult = (LinearLayout) findViewById(R.id.recyclerviewresult);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        mPayment = (Button)findViewById(R.id.paymentid);
        //totalmoneyid = (TextView) findViewById(R.id.totalmoneyid);
        balanceid = (TextView)findViewById(R.id.balanceid);

        postList = findViewById(R.id.search_result_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Donate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        //Toast.makeText(this, b, Toast.LENGTH_LONG).show();

        PaymentRefs.child(currentUserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("donatePaid").getValue() != null)
                {
                    //balanceid.setText("Balance: " + String.valueOf(Balance) + "$" + " Details :");
                    Toast.makeText(getApplicationContext(), "msuk paymentrefbalance hehe", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                DonaterEmail = dataSnapshot.child("email").getValue().toString();
                DonaterName = dataSnapshot.child("name").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.button10).setOnClickListener(this);
        findViewById(R.id.button20).setOnClickListener(this);
        findViewById(R.id.button30).setOnClickListener(this);
        findViewById(R.id.button40).setOnClickListener(this);
        findViewById(R.id.button50).setOnClickListener(this);
        findViewById(R.id.button100).setOnClickListener(this);
        findViewById(R.id.paymentid).setOnClickListener(this);
        findViewById(R.id.search_community_button).setOnClickListener(this);

        mPayment.setEnabled(false);
        button10.setEnabled(false);
        button20.setEnabled(false);
        button30.setEnabled(false);
        button40.setEnabled(false);
        button50.setEnabled(false);
        button100.setEnabled(false);

        //Close Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private int PAYPAL_REQUEST_CODE = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENTID);

    private void payPalPayment()
    {

        String a = test.getText().toString();
        int numbera = Integer.parseInt(a);
        totaldonate = numbera;

        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(totaldonate)), "MYR", "Homeless Donation",
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

                        if(paymentResponse.equals("approved")) {

                            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                            System.out.println(confirm.toJSONObject().toString(4));
                            System.out.println(confirm.getPayment().toJSONObject().toString(4));


                            payid = confirm.toJSONObject().getJSONObject("response").getString("id");

                            Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();

                            HashMap paymentdetails = new HashMap();
                            paymentdetails.put("donaterEmail", DonaterEmail);
                            paymentdetails.put("donaterName", DonaterName);
                            paymentdetails.put("donateTo", AgencyName);
                            paymentdetails.put("donateToid", PostKey);
                            paymentdetails.put("transactionID", payid);
                            paymentdetails.put("date", saveCurrentDate);
                            paymentdetails.put("time", saveCurrentTime);
                            paymentdetails.put("donationAmount", totaldonate);
                            paymentdetails.put("uid", currentUserid);

                            PaymentRefs.child(postRandomName + payid).setValue(paymentdetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        Postsref.child(PostKey).child("totalDonationReceive").child(payid).setValue(totaldonate);

                                        int permissionCheck = ContextCompat.checkSelfPermission(paypaltest.this,Manifest.permission.SEND_SMS);

                                        if(permissionCheck==PackageManager.PERMISSION_GRANTED)
                                        {
                                            MyMessage();
                                        }
                                        else {

                                            ActivityCompat.requestPermissions(paypaltest.this,new String[]{Manifest.permission.SEND_SMS},0);
                                        }
                                    }

                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "Payment unsuccessful or Payment cancel", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void MyMessage() {

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String phoneNumber = dataSnapshot.child("phone").getValue().toString();

                String message = "Dear Mr/Ms " + DonaterName + ", Thanks for Donating to " + AgencyName + ". Transaction ID: " + payid + ". Donation Amount: RM "+ totaldonate;

                if(!phoneNumber.equals(""))
                {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null,message,null,null);

                    Toast.makeText(paypaltest.this,"Message Send", Toast.LENGTH_SHORT);
                }
                else {

                    Toast.makeText(paypaltest.this,"Message cannot send", Toast.LENGTH_SHORT);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
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

    private void SearchCommunity(String searchBoxInput) {

        String query = searchBoxInput.toLowerCase();

        Query SortAgentPost = Postsref.orderByChild("agencyname").startAt(query).endAt(query + "\uf8ff");
        //Query SortAgentPost = Postsref.orderByChild("counter");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(SortAgentPost, Posts.class).build();

        FirebaseRecyclerAdapter<Posts,PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, final int position, @NonNull Posts model)
            {

                //final String PostKey = getRef(position).getKey();

                holder.setProductname(model.getAgencyname());
                holder.setProductprice(model.getCategories());
                holder.setProductdate(model.getDate());
                holder.setProductnumber(model.getOfficenumber());
                holder.setProductstatus(model.getTags());

                PostKey = getSnapshots().getSnapshot(position).getKey();

                AgencyName = getSnapshots().get(position).getAgencyname();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        recyclerviewresult.setBackgroundColor(Color.parseColor("#0bcce6"));

                        mPayment.setEnabled(true);
                        button10.setEnabled(true);
                        button20.setEnabled(true);
                        button30.setEnabled(true);
                        button40.setEnabled(true);
                        button50.setEnabled(true);
                        button100.setEnabled(true);

                    }
                });

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout_donatesearch, viewGroup, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        TextView post_product_name, post_product_price, post_product_status, post_product_date,post_product_phoneno;
        String currentUserid;


        public PostsViewHolder(View itemView)
        {
            super(itemView);
/*          productstatus = itemView.findViewById(R.id.post_product_status);*/

            currentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setProductname(String productname)
        {
            post_product_name = itemView.findViewById(R.id.post_product_name);
            post_product_name.setText(productname);
        }

        public void setProductprice(String productprice)
        {
            post_product_price = itemView.findViewById(R.id.post_product_price);
            post_product_price.setText(productprice);
        }

        public void setProductstatus(String productstatus)
        {
            post_product_status = itemView.findViewById(R.id.post_product_status);
            post_product_status.setText(productstatus);
        }

        public void setProductdate(String productdate)
        {
            post_product_date = itemView.findViewById(R.id.post_product_date);
            post_product_date.setText(productdate);
        }

        public void setProductnumber(String productnumber)
        {
            post_product_phoneno = itemView.findViewById(R.id.post_product_phoneno);
            post_product_phoneno.setText(productnumber);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.paymentid:
                payPalPayment();
                break;
            case R.id.search_community_button:
                String searchBoxInput = SearchInputText.getText().toString();
                SearchCommunity(searchBoxInput);
                break;
            case R.id.button10:
                String button1 = button10.getContentDescription().toString().trim();
                test.setText(button1);

                button10.setBackgroundColor(Color.parseColor("#0bcce6"));
                button20.setBackgroundColor(Color.parseColor("#ffffff"));
                button30.setBackgroundColor(Color.parseColor("#ffffff"));
                button40.setBackgroundColor(Color.parseColor("#ffffff"));
                button50.setBackgroundColor(Color.parseColor("#ffffff"));
                button100.setBackgroundColor(Color.parseColor("#ffffff"));

                break;
            case R.id.button20:
                String button2 = button20.getContentDescription().toString().trim();
                test.setText(button2);

                button10.setBackgroundColor(Color.parseColor("#ffffff"));
                button20.setBackgroundColor(Color.parseColor("#0bcce6"));
                button30.setBackgroundColor(Color.parseColor("#ffffff"));
                button40.setBackgroundColor(Color.parseColor("#ffffff"));
                button50.setBackgroundColor(Color.parseColor("#ffffff"));
                button100.setBackgroundColor(Color.parseColor("#ffffff"));


                break;
            case R.id.button30:
                String button3 = button30.getContentDescription().toString().trim();
                test.setText(button3);

                button10.setBackgroundColor(Color.parseColor("#ffffff"));
                button20.setBackgroundColor(Color.parseColor("#ffffff"));
                button30.setBackgroundColor(Color.parseColor("#0bcce6"));
                button40.setBackgroundColor(Color.parseColor("#ffffff"));
                button50.setBackgroundColor(Color.parseColor("#ffffff"));
                button100.setBackgroundColor(Color.parseColor("#ffffff"));

                break;
            case R.id.button40:
                String button4 = button40.getContentDescription().toString().trim();
                test.setText(button4);

                button10.setBackgroundColor(Color.parseColor("#ffffff"));
                button20.setBackgroundColor(Color.parseColor("#ffffff"));
                button30.setBackgroundColor(Color.parseColor("#ffffff"));
                button40.setBackgroundColor(Color.parseColor("#0bcce6"));
                button50.setBackgroundColor(Color.parseColor("#ffffff"));
                button100.setBackgroundColor(Color.parseColor("#ffffff"));

                break;
            case R.id.button50:
                String button5 = button50.getContentDescription().toString().trim();
                test.setText(button5);

                button10.setBackgroundColor(Color.parseColor("#ffffff"));
                button20.setBackgroundColor(Color.parseColor("#ffffff"));
                button30.setBackgroundColor(Color.parseColor("#ffffff"));
                button40.setBackgroundColor(Color.parseColor("#ffffff"));
                button50.setBackgroundColor(Color.parseColor("#0bcce6"));
                button100.setBackgroundColor(Color.parseColor("#ffffff"));

                break;
            case R.id.button100:
                String button10dua = button100.getContentDescription().toString().trim();
                test.setText(button10dua);

                button10.setBackgroundColor(Color.parseColor("#ffffff"));
                button20.setBackgroundColor(Color.parseColor("#ffffff"));
                button30.setBackgroundColor(Color.parseColor("#ffffff"));
                button40.setBackgroundColor(Color.parseColor("#ffffff"));
                button50.setBackgroundColor(Color.parseColor("#ffffff"));
                button100.setBackgroundColor(Color.parseColor("#0bcce6"));

                break;
        }

    }
}

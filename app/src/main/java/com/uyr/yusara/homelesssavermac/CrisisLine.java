package com.uyr.yusara.homelesssavermac;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CrisisLine extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private TextView mercycallid,maiwpid,yalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crisis_line);

        mercycallid = (TextView)findViewById(R.id.mercycallid);
        maiwpid = (TextView)findViewById(R.id.maiwpid);
        yalid = (TextView)findViewById(R.id.yalid);



        mToolbar = (Toolbar) findViewById(R.id.find_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Crisis Line");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.mercycallid).setOnClickListener(this);
        findViewById(R.id.maiwpid).setOnClickListener(this);
        findViewById(R.id.yalid).setOnClickListener(this);

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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.mercycallid:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mercycallid.getText()));
                startActivity(intent);
                break;
            case R.id.maiwpid:
                Intent intent2 = new Intent(Intent.ACTION_DIAL);
                intent2.setData(Uri.parse("tel:"+maiwpid.getText()));
                startActivity(intent2);
                break;
            case R.id.yalid:
                Intent intent3 = new Intent(Intent.ACTION_DIAL);
                intent3.setData(Uri.parse("tel:"+yalid.getText()));
                startActivity(intent3);
                break;
        }

    }
}

package com.uyr.yusara.homelesssavermac.News;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.uyr.yusara.homelesssavermac.MainActivity;
import com.uyr.yusara.homelesssavermac.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsMainActivity extends AppCompatActivity {

    String API_KEY = "70c198f1a00a4ec48981b8538e2ee02b"; // ### YOUE NEWS API HERE ###
    String NEWS_SOURCE = "country=my";
    ListView listNews;
    ProgressBar loader;
    private Toolbar mToolbar;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    static final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);
        listNews = (ListView) findViewById(R.id.listNews);
        loader = (ProgressBar) findViewById(R.id.loader);
        listNews.setEmptyView(loader);



        if(Function.isNetworkAvailable(getApplicationContext()))
        {
            NewsMainActivity.DownloadNews newsTask = new NewsMainActivity.DownloadNews();
            newsTask.execute();
        }else{
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        mToolbar = (Toolbar) findViewById(R.id.news_details);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Current News");
/*        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(NewsMainActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    class DownloadNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String... args) {
            String xml = "";

            String urlParameters = "";
            //xml = Function.excuteGet("https://newsapi.org/v1/articles?source="+NEWS_SOURCE+"&sortBy=top&apiKey="+API_KEY, urlParameters);
            xml = Function.excuteGet("https://newsapi.org/v2/top-headlines?country=my&category=health&apiKey="+API_KEY, urlParameters);
            return  xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            if(xml.length()>10){ // Just checking if not empty

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR).toString());
                        map.put(KEY_TITLE, jsonObject.optString(KEY_TITLE).toString());
                        map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION).toString());
                        map.put(KEY_URL, jsonObject.optString(KEY_URL).toString());
                        map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE).toString());
                        map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT).toString());
                        dataList.add(map);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                }

                ListNewsAdapter adapter = new ListNewsAdapter(NewsMainActivity.this, dataList);
                listNews.setAdapter(adapter);

                listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent i = new Intent(NewsMainActivity.this, DetailsActivity.class);
                        i.putExtra("url", dataList.get(+position).get(KEY_URL));
                        startActivity(i);
                    }
                });

            }else{
                Toast.makeText(getApplicationContext(), "No news found", Toast.LENGTH_SHORT).show();
            }
        }
    }

}



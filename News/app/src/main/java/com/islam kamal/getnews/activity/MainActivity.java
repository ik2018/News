package com.bassambadr.getnews.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.bassambadr.getnews.R;
import com.bassambadr.getnews.activity.adapter.ListViewNewsAdapter;
import com.bassambadr.getnews.activity.model.ListViewNewsItem;
import com.bassambadr.getnews.java.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ListView mListView;
    private ListViewNewsAdapter listViewNewsAdapter;
    private ArrayList<ListViewNewsItem> listViewNewsItems;

    private JSONParser jsonParser = new JSONParser();

    private String READNEWS_URL =
            "http://10.0.2.2:1234/newsite/readnews.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list_view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_add:
                AddNewsActivity addNewsActivity = new AddNewsActivity(this);
                addNewsActivity.show();
                return true;
            case R.id.action_refresh:
                new GetNewsTask().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {

    }

    private class GetNewsTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog mProgressDialog;

        private JSONObject jsonObjectResult = null;

        private String error;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            listViewNewsItems = new ArrayList<ListViewNewsItem>();
            mProgressDialog = ProgressDialog.show(MainActivity.this,
                    "Processing...", "Get last news", false, false);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            jsonObjectResult = jsonParser.makeHttpRequest(READNEWS_URL, null);

            if (jsonObjectResult == null)
            {
                error = "Error int the connection";
                return false;
            }

            try
            {
                if (jsonObjectResult.getInt("success") == 1)
                {
                    JSONArray jsonArray = jsonObjectResult.getJSONArray("posts");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject news = jsonArray.getJSONObject(i);

                        ListViewNewsItem listViewNewsItem = new ListViewNewsItem
                                (
                                        news.getInt("news_id"),
                                        news.getString("news_title"),
                                        news.getString("news_date"),
                                        news.getString("news_body")
                                );
                        listViewNewsItems.add(listViewNewsItem);
                    }
                    return true;
                }
                else
                    error = jsonObjectResult.getString("message");

            }
            catch (Exception ex)
            {

            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            mProgressDialog.dismiss();
            if (aBoolean)
            {
                listViewNewsAdapter = new ListViewNewsAdapter(getApplicationContext(),
                        listViewNewsItems);
                mListView.setAdapter(listViewNewsAdapter);
            }
            else
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
        }
    }
}

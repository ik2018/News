package com.bassambadr.getnews.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bassambadr.getnews.R;
import com.bassambadr.getnews.java.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Bassam on 10/21/2014.
 */
public class AddNewsActivity extends Dialog {

    private Activity mActivity;

    private EditText mNewsTitleET;
    private EditText mNewsET;

    private Button mPublishBtn;

    JSONParser jsonParser = new JSONParser();

    private String ADD_URL =
            "http://10.0.2.2:1234/newsite/addnews.php";

    public AddNewsActivity(Activity mActivity)
    {
        super(mActivity);
        this.mActivity = mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_news);

        mNewsTitleET = (EditText) findViewById(R.id.title_news);
        mNewsET      = (EditText) findViewById(R.id.news_box);
        mPublishBtn  = (Button) findViewById(R.id.publish_btn);

        mPublishBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attempAdding();
            }
        });
    }

    private void attempAdding()
    {
        if (!mNewsTitleET.getText().toString().equals("") &&
                !mNewsET.getText().toString().equals(""))
        {
            new AddNewsTask().execute();
        }
        else
        {
            Toast.makeText(mActivity.getApplicationContext(),
                    "All fields are requires", Toast.LENGTH_LONG).show();
        }
    }

    private class AddNewsTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog mProgressDialog;

        private JSONObject jsonObjectResult = null;

        private String error;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(AddNewsActivity.this.getContext(),
                    "Processing...", "Adding new news", false, false);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();

            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("news_date", dateFormat.format(date).toString()));
            pairs.add(new BasicNameValuePair("news_title", mNewsTitleET.getText().toString()));
            pairs.add(new BasicNameValuePair("news_body", mNewsET.getText().toString()));

            jsonObjectResult = jsonParser.makeHttpRequest(ADD_URL, pairs);

            if (jsonObjectResult == null)
            {
                error = "Error int the connection";
                return false;
            }

            try
            {
                if (jsonObjectResult.getInt("success") == 1)
                {
                    error = jsonObjectResult.getString("message");
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
            Toast.makeText(mActivity.getApplicationContext(), error, Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

}

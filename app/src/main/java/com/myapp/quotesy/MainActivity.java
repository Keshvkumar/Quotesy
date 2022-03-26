package com.myapp.quotesy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hsalf.smileyrating.SmileyRating;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class MainActivity extends AppCompatActivity {

    TextView btnHit;
    TextView txtJson;
    SmileyRating smileyRating;
    ProgressDialog pd;

    String shareQuote="Be Happy!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHit = findViewById(R.id.refresh_btn);
        txtJson = findViewById(R.id.quote_view);
        smileyRating = findViewById(R.id.smile_rating);

        smileyRating.setRating(-1,true);
        txtJson.setText("Be Happy!");
        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJSON("https://api.quotable.io/random?tags=famous-quotes");
            }
        });

        smileyRating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                // You can compare it with rating Type
                if (SmileyRating.Type.GREAT == type) {
                    Toast.makeText(getApplicationContext(), "Tadaa! Feeling Great ...", Toast.LENGTH_LONG).show();
                }
                // You can get the user rating too
                // rating will between 1 to 5
                int rating = type.getRating();

                switch(rating)
                {
                    case 1:
                        smileyRating.setRating(1,true);
                        Toast.makeText(getApplicationContext(), "Ouch! Feeling Terrible ...", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        smileyRating.setRating(2,true);
                        Toast.makeText(getApplicationContext(), "Feeling Bad ...", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        smileyRating.setRating(3,true);
                        Toast.makeText(getApplicationContext(), "Just! Feeling Okay ...", Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        smileyRating.setRating(4,true);
                        Toast.makeText(getApplicationContext(), "Feeling Good ...", Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        smileyRating.setRating(5,true);
                        Toast.makeText(getApplicationContext(), "Taadaa! Feeling Great ...", Toast.LENGTH_LONG).show();
                        break;
                    case -1:
                        smileyRating.setRating(-1,true);
                        Toast.makeText(getApplicationContext(), "Atleast select minimum rating.", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        });

    }
    private void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();


                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Please wait");
                pd.setCancelable(false);
                pd.show();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (pd.isShowing()){
                    pd.dismiss();
                }


                JSONObject obj = null;
                try {
                    obj = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    s = obj.getString("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtJson.setText(s);
                shareQuote=s;
                Toast.makeText(getApplicationContext(), "Tadaa!\nfetched new quote ...", Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;


                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    private Context getContext() {
        return super.getApplicationContext();
    }

    public void share(View view)
    {
        PackageManager pm=getPackageManager();
        try
        {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            /* Check if package exists or not. If not then code
            in catch block will be called */
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, shareQuote);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        }
        catch (PackageManager.NameNotFoundException e)
        {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));


                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            txtJson.setText(result);
        }
    }
}
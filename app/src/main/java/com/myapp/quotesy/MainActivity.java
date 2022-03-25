package com.myapp.quotesy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
public class MainActivity extends AppCompatActivity {

    TextView btnHit;
    TextView txtJson;
    ProgressDialog pd;

    String shareQuote="Be Happy!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHit = (TextView) findViewById(R.id.refresh_btn);
        txtJson = (TextView) findViewById(R.id.quote_view);

        txtJson.setText("Be Happy!");
        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJSON("https://api.quotable.io/random?tags=famous-quotes");
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
                Toast.makeText(getApplicationContext(), "TADAA!\nfetched new quote ...", Toast.LENGTH_SHORT).show();
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
    public Bitmap takeScreenshot(View view) {
        RelativeLayout iv = (RelativeLayout) findViewById(R.id.qview);
        Bitmap bitmap = Bitmap.createBitmap(
                iv.getChildAt(0).getWidth(),
                iv.getChildAt(0).getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        iv.getChildAt(0).draw(c);

//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
        return bitmap;
    }
    private void shareIt(Bitmap bitmap) {
//        Bitmap icon = bitmap;
//        Intent share = new Intent(Intent.ACTION_SEND);
//        share.setType("image/jpeg");
//
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, "title");
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                values);
//
//
//        OutputStream outstream;
//        try {
//            outstream = getContentResolver().openOutputStream(uri);
//            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
//            outstream.close();
//        } catch (Exception e) {
//            System.err.println(e.toString());
//        }
//
//        share.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(share, "Share Image"));

//        String path= MediaStore.Images.Media.insertImage(ctx.getContentResolver(), bitmap,"title", null);
//        ContentValues values=new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE,"Title");
//        values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
//        Uri path=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("image/jpeg");
//
//        Uri uri = Uri.parse(String.valueOf(path));
//        OutputStream outstream;
//        try {
//            outstream = getContentResolver().openOutputStream(uri);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
//            outstream.close();
//        } catch (Exception e) {
//            System.err.println(e.toString());
//        }
//
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quotes App");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey there!I have shared you a quote from Quotes App.");
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(shareIntent, "Share new quote via"));
    }

    private Context getContext() {
        return super.getApplicationContext();
    }

    public void share(View view)
    {
//        Bitmap bitmap = takeScreenshot(view);
//        shareIt(bitmap);
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
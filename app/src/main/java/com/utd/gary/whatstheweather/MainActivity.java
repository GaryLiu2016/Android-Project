package com.utd.gary.whatstheweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;
    ImageView weatherIcon;


    public void findWeather(View view) {

        //Log.i("City Name", cityName.getText().toString());


        /* Hide the keyboard after input */
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {

            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            if (encodedCityName.length() != 0) {
                DownloadTask task = new DownloadTask();
                task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=ff85e0b38d8193ad25dbb8e92a1b8b4e");



            }


        } catch (UnsupportedEncodingException e) {

            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }


    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return null;

        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                //Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();        // Why Crash????
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            if (s != null) {

                try {

                    String message = "";

                    String descriptionForIcon = "";             // Just for icon

                    JSONObject jsonObject = new JSONObject(s);

                    String weatherInfo = jsonObject.getString("weather");

                    //Log.i("Website Content: ", weatherInfo);

                    JSONArray arr = new JSONArray(weatherInfo);

                    for (int i = 0; i < arr.length(); i++) {

                        JSONObject jsonPart = arr.getJSONObject(i);

                        //Log.i("main", jsonPart.getString("main"));
                        //Log.i("description", jsonPart.getString("description"));

                        String mainText = "";
                        String description = "";

                        mainText = jsonPart.getString("main");
                        description = jsonPart.getString("description");
                        descriptionForIcon = jsonPart.getString("icon");

                        if (!mainText.equals("") && !description.equals("")) {

                            message += mainText + ": " + description + "\r\n";

                        }

                    }

                    if (message != "") {

                        resultTextView.setText(message);

                        // set weather icon
                        ImageDownloader task1 = new ImageDownloader();
                        Bitmap myImage;
                        try {

                            //String iconName = "";

                            //iconName = findIconName(descriptionForIcon);

                            myImage = task1.execute("http://openweathermap.org/img/w/" + descriptionForIcon + ".png").get();

                            weatherIcon.setImageBitmap(myImage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {

                        Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();

                        weatherIcon.setImageBitmap(null);

                    }


                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }

            } else {

                resultTextView.setText("Invalid city!");
                weatherIcon.setImageBitmap(null);

            }



        }
    }

    /*public String findIconName(String description) {

        String iconName = "";

        switch (description) {

            case "clear sky" : iconName = "01d";
                break;
            case "few clouds" : iconName = "02d";
                break;
            case "scattered clouds" : iconName = "03d";
                break;
            case "broken clouds" : iconName = "04d";
                break;
            case "shower rain" : iconName = "09d";
                break;
            case "rain" : iconName = "10d";
                break;
            case "thunderstorm" : iconName = "11d";
                break;
            case "snow" : iconName = "13d";
                break;
            case "mist" : iconName = "50d";
                break;
            default: break;

        }

        return iconName;


    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText)findViewById(R.id.cityName);
        resultTextView = (TextView)findViewById(R.id.resutlTextView);
        weatherIcon = (ImageView)findViewById(R.id.weatherIcon);

    }
}

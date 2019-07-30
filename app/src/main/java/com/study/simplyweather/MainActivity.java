package com.study.simplyweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private EditText editCity;
    private TextView textWeather;
    private static final String APP_ID = "b6907d289e10d714a6e88b30761fae22";
    private String url = "https://openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editCity = findViewById(R.id.editCity);
        textWeather = findViewById(R.id.textViewWeather);
    }

    public void showWeather(View view) {
        String city = editCity.getText().toString().trim();

        if (!city.isEmpty()) {
            WeatherTask weatherTask = new WeatherTask();
            String urlRequest = String.format(url, city, APP_ID);
            weatherTask.execute(urlRequest);
        } else Toast.makeText(this, "Введите город", Toast.LENGTH_SHORT).show();
    }

    private class WeatherTask extends AsyncTask<String, Void, String> {
        URL url;
        HttpURLConnection connection;
        StringBuilder stringBuilder;
        InputStream inputStream;
        BufferedReader reader;

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    stringBuilder = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) stringBuilder.append(line);
                }
                return stringBuilder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String humidity = jsonObject.getJSONObject("main").getString("humidity");
                String desc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format("Город: %s\nТемпература: %s\nВлажность: %s\n%s", city, temp, humidity, desc);

                textWeather.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

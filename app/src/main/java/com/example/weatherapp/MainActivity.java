package com.example.weatherapp;

import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    TextView cityName;

    Button search;

    TextView show;

    String url;

    class getWeather extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject =  new JSONObject(result);
                String weatherinfo = jsonObject.getString("main");
                weatherinfo = weatherinfo.replace("temp", "Temperature");
                weatherinfo = weatherinfo.replace("feels_like", "Feels Like");
                weatherinfo = weatherinfo.replace("temp_min", "Min");
                weatherinfo = weatherinfo.replace("temp_max", "Max");
                weatherinfo = weatherinfo.replace("pressure", "Pressure");
                weatherinfo = weatherinfo.replace("humidity", "Humidity");
                weatherinfo = weatherinfo.replace("{", "");
                weatherinfo = weatherinfo.replace("}", "");
                weatherinfo = weatherinfo.replace(",", "\n");
                weatherinfo = weatherinfo.replace(":", " : ");
                show.setText(weatherinfo);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.info);

        final String[] temp={""};

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                try {
                    if(city!=null){
                        url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=dfda56122d04af1b23790dd7d2ffc801&units=metric";
                    }else{
                        Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task = new getWeather();
                    temp[0] = task.execute(url).get();
                } catch(ExecutionException e){
                    e.printStackTrace();
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("Cannot able to find weather");
                }

            }
        });




//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}
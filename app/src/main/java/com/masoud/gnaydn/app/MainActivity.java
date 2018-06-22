package com.masoud.gnaydn.app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> mForecastAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        context = this;

        String[] forecastArray = {
                "Today - Sunny - 86/73",
                "Satarday - Fogy - 67/52",
                "Friday - Cloady - 78/64",
                "Thersday - Sunny - 86/73",
                "Wendsday - Rainy - 48/34",
                "Tuesday - Rainy - 52/49",
                "Monday - Sunny - 86/73"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));
        //create addapter to pupulate listview by data

        mForecastAdapter =
                new
                        ArrayAdapter<String>(context,
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);
        //find listview by id and inflate with addapter
        ListView forecastListView = findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(mForecastAdapter);
        //setup a click event for forecast list view
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               String foreCastItem= mForecastAdapter.getItem(i);
               Intent intent=new Intent(context,DetailActivity.class).putExtra(Intent.EXTRA_TEXT,foreCastItem);
               startActivity(intent);

                          //        Snackbar.make(view, "You Tapped On "+foreCastItem, Snackbar.LENGTH_LONG)
                           //               .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_Refresh) {
            FetchWeatherTask fetchWeatherTaskObject = new FetchWeatherTask();
            fetchWeatherTaskObject.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


        private String getReadbleDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            roundedHigh=roundedHigh-274;
            roundedLow=roundedLow-274;
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the compelete forecast in JSON Format
         * and pull out the data we need to construct the string needed for wireframes.
         * <p>
         * Fortunetely parsing is easy : Constructor takes the json string and convert it
         * into an Obkect hierarchy for us.
         */
        private String[] getWeatherDateFromJson(String forcastJsonStr, int numDays) throws JSONException {

            //thes are the names of the JSOn object that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";
            final String OWM_DATETIME = "dt";
            final String OWM_MAIN = "main";
            final String OWM_DESCRIPTION = "description";
            JSONObject forecastJson = new JSONObject(forcastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                // for now , using the format "Day , Description, hi/low"
                String day;
                String description;
                String highAndLow;

                //get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // the date/time is returned as long . we need to convert that
                //into something human readble , since most people wonte read "1400356800" as
                //"this saturday"
                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadbleDateString(dateTime);

                //description is an a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                //Temperatures are in a child object called "temp". try not to name variables
                //"temp" when working with temperature . it confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_MAIN);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " ( " + highAndLow+" )";
            }
            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry" + s);
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

            //network calling api
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            int numDays = 40;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                // URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                URL url = new URL("http://samples.openweathermap.org/data/2.5/forecast?id=94043&appid=b6907d289e10d714a6e88b30761fae22");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.e(LOG_TAG, "Forecast JSON String :" + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getWeatherDateFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            //this will only happen if there was an error getting or parsing forecast.
            return null;

        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                mForecastAdapter.addAll(result);


            }
        }
    }

}

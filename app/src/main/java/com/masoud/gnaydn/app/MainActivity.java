/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.masoud.gnaydn.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.masoud.gnaydn.app.sync.SunshineSyncAdapter;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        // db create for first time
        /*
        WeatherDbHelper weatherDbHelper=new WeatherDbHelper(this);
        Log.e(LOG_TAG,weatherDbHelper.getDatabaseName().toString());
        SQLiteDatabase sqLiteDatabase=weatherDbHelper.getWritableDatabase();
        ContentValues locationValues = new ContentValues();

        // Then add the data, along with the corresponding name of the data type,
        // so the content provider knows what kind of value is being inserted.
        locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Moscow");
        locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, "1");
        locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,"55.7522" );
        locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, "37.6156");

     long locationId= sqLiteDatabase.insert(WeatherContract.LocationEntry.TABLE_NAME,null,locationValues);

        ContentValues weatherValues = new ContentValues();

        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, "1");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, "2017-02-04");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, "90");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, "1021.81");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, "2.07");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, "170.005");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, "261.256");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, "261.256");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Clear");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, "800");

      sqLiteDatabase.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,weatherValues);

        Log.e(LOG_TAG,locationId+"");
        */
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment forecastFragment =  ((ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
            if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}

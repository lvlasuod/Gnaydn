package com.masoud.gnaydn.app;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    // constructor of class ForecastFragment
    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main, container, false);
      /*  // create some dummy data for list view
        String [] forecastArray={
                "Today - Sunny - 86/73",
                "Satarday - Fogy - 67/52",
                "Friday - Cloady - 78/64",
                "Thersday - Sunny - 86/73",
                "Wendsday - Rainy - 48/34",
                "Tuesday - Rainy - 52/49",
                "Monday - Sunny - 86/73"
        };
        List<String> weekForecast=new ArrayList<String >(Arrays.asList(forecastArray));
        //create addapter to pupulate listview by data

        mForecastAdapter=
                new
                        ArrayAdapter<String>(getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);
        //find listview by id and inflate with addapter
        ListView forecastListView=(ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(mForecastAdapter);
        */
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // do the job here
        // not in on create ok
        // create some dummy data for list view

    }

}
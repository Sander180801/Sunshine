package com.example.sander.sunshine;


import android.animation.TimeAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;

import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sander.sunshine.data.WeatherContract;
import com.example.sander.sunshine.data.WeatherDbHelper;
import com.example.sander.sunshine.service.SunshineService;
import com.example.sander.sunshine.sync.SunshineSyncAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private ForecastAdapter forecastAdapter;
    private boolean mUseTodayLayout;
    private static final int LOADER_ID = 10;
    private ListView mListView;
    private int mPosition=ListView.INVALID_POSITION;
    private String SELECTED_KEY="selected_position";
    public AlarmManager alarmManager;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;


    public ForecastFragment() {

    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        Log.v(ForecastFragment.class.getSimpleName(), "Created");


    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    public void onPause() {
        super.onPause();
        Log.v(ForecastFragment.class.getSimpleName(), "Paused");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(ForecastFragment.class.getSimpleName(), "Destroyed");
    }

    public void onResume() {
        super.onResume();
        Log.v(ForecastFragment.class.getSimpleName(), "Resumed");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(ForecastFragment.class.getSimpleName(), "Stopped");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id==R.id.location_viewer){
            OpenMap();
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = connectivityManager.getActiveNetworkInfo();
        boolean isWifi = current != null && current.getType() == ConnectivityManager.TYPE_WIFI;
        if (isWifi) {
            SunshineSyncAdapter.syncImmediately(getActivity());




        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        ForecastFragment forecastFragment = new ForecastFragment();
        forecastFragment.setHasOptionsMenu(true);
        forecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        mListView.setAdapter(forecastAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                onSaveInstanceState(savedInstanceState);
                if (cursor != null) {

                    String locationSetting = Utility.getPreferredLocation(getActivity());
                            ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, cursor.getLong(COL_WEATHER_DATE)));
                }
                mPosition = position;
            }

        });
        if (savedInstanceState!=null&&savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition=savedInstanceState.getInt(SELECTED_KEY);
        }



        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortorder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherforlocationuri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());
        return new CursorLoader(getActivity(), weatherforlocationuri, FORECAST_COLUMNS, null, null, sortorder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        forecastAdapter.swapCursor(data);
        if (mPosition!=ListView.INVALID_POSITION){
            mListView.smoothScrollToPosition(mPosition);
        }



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition!=ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.swapCursor(null);
    }

    void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout=useTodayLayout;
        if (forecastAdapter!=null){
            forecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }




    public void onItemSelected(Uri dateUri) {
        if (getActivity().findViewById(R.id.weather_detail_container).getVisibility() == View.VISIBLE) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, new DetailFragment())
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);

        }

    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }
    private void OpenMap(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current=connectivityManager.getActiveNetworkInfo();
        boolean isWifi=current!=null&&current.getType()==ConnectivityManager.TYPE_WIFI;
        if (isWifi) {
            Cursor cursor=forecastAdapter.getCursor();
            cursor.moveToPosition(0);
            String latitude=cursor.getString(COL_COORD_LAT);
            String longitude=cursor.getString(COL_COORD_LONG);
            Uri uri=Uri.parse("geo:" + latitude + "," + longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            if (intent.resolveActivity(getActivity().getPackageManager())!=null){
                startActivity(Intent.createChooser(intent, "Choose an app to view your location"));
            }
        }
}
}




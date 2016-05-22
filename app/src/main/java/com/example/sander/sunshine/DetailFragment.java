package com.example.sander.sunshine;


import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sander.sunshine.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String HASHTAG_STRING="#SunshineApp";
    private String mForecastString;
    static final String DETAIL_URI="URI";
    private Uri mUri;
    private ShareActionProvider actionProvider;
    private static final int LOADER_ID=0;
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES
    };
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    static final int COL_HUMIDITY=9;
    static final int COL_PRESSURE=10;
    static final int COL_WIND_SPEED=11;
    static final int COL_WIND_DIR=12;


    public TextView FriendlyDateView;
    public  TextView DetailDescription;
    public  TextView DetailHigh;
    public  TextView DetailLow;
    public  TextView humidityView;
    public  TextView pressureView;
    public  TextView FriendlyDay;
    public  TextView wind;
    public  ImageView DetailIcon;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments=getArguments();
        if (arguments!=null){
            mUri=arguments.getParcelable(DETAIL_URI);
        }

        View rootView=inflater.inflate(R.layout.activity_detail,container,false);
        DetailDescription=(TextView)rootView.findViewById(R.id.description);
        DetailHigh=(TextView)rootView.findViewById(R.id.detail_high_textview);
        DetailLow=(TextView)rootView.findViewById(R.id.detail_low_textview);
        DetailIcon=(ImageView)rootView.findViewById(R.id.detail_icon);
        humidityView=(TextView)rootView.findViewById(R.id.humidity);
        FriendlyDay=(TextView)rootView.findViewById(R.id.day_textview);
        FriendlyDateView=(TextView)rootView.findViewById(R.id.date_textview);
        wind=(TextView)rootView.findViewById(R.id.wind);
        pressureView=(TextView)rootView.findViewById(R.id.pressure);
return rootView;





    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail, menu);
        MenuItem shareItem=menu.findItem(R.id.share);
        actionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (mForecastString!=null) {
            actionProvider.setShareIntent(createShareIntent());
        }

    }

    private Intent createShareIntent(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mForecastString + HASHTAG_STRING);



        return intent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       if (null!=mUri){
           return new CursorLoader(getActivity(),mUri,FORECAST_COLUMNS,null,null,null);
        }
return null;
    }




    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data!=null&&data.moveToFirst()){
            return;
        }
        long date=data.getLong(COL_WEATHER_DATE);
        String FriendlyDate=Utility.getDayName(getActivity(), date);
        String dateText=Utility.getFormattedMonthDay(getActivity(),date);
        FriendlyDay.setText(FriendlyDate);
        FriendlyDateView.setText(dateText);
        int weatherId=data.getInt(COL_WEATHER_ID);
        DetailIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        DetailDescription.setText(weatherDescription);

        boolean isMetric = Utility.isMetric(getActivity());
        double high=data.getDouble(COL_WEATHER_MAX_TEMP);
        String highString=Utility.formatTemperature(getActivity(), high);
        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        String lowString=Utility.formatTemperature(getActivity(), low);
        DetailHigh.setText(highString);
        DetailLow.setText(lowString);

        float humidity=data.getFloat(COL_HUMIDITY);
        humidityView.setText(getActivity().getString(R.string.format_humidity,humidity));

        float pressure=data.getFloat(COL_PRESSURE);
        pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        float wind_speed=data.getFloat(COL_WIND_SPEED);
        float windDir=data.getFloat(COL_WIND_DIR);
        wind.setText(Utility.getFormattedWind(getActivity(),wind_speed,windDir));

        MyView myView=(MyView)getActivity().findViewById(R.id.myView);
        myView.setWindDirection(Utility.getWindDirection(getActivity(),windDir));
        myView.setWindSpeed(Utility.getFormattedWind(getActivity(),wind_speed,windDir));


        mForecastString=String.format("%s - %s- %s/%s",dateText,weatherDescription,high,low);
        if (actionProvider!=null){
            actionProvider.setShareIntent(createShareIntent());
        }


    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }




    void onLocationChanged(String newLocation) {
        Uri uri = mUri;
        if (null!=uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(mUri);
            Uri updatedUri=WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation,date);
            mUri=updatedUri;
            getLoaderManager().restartLoader(LOADER_ID,null,this);

        }
    }
}



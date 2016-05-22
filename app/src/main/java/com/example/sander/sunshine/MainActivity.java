package com.example.sander.sunshine;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.renderscript.ScriptGroup;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.sander.sunshine.sync.SunshineSyncAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    private String Location;
    private final String DETAIL_FRAGMENT_TAG="DFTAG";
   static boolean mTwoPane;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Location=Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container)!=null){
            mTwoPane=true;
        }
if (savedInstanceState!=null){
    getSupportFragmentManager().beginTransaction()
            .replace(R.id.weather_detail_container,new DetailFragment(),DETAIL_FRAGMENT_TAG)
            .commit();
}
        else{
    mTwoPane=false;
    getSupportActionBar().setElevation(0f);
}
        ForecastFragment forecastFragment=((ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);
        SunshineSyncAdapter.initializeSyncAdapter(this);






    }


    @Override
    protected void onResume() {
        super.onResume();
        String location=Utility.getPreferredLocation(this);
        if (location!=null&&!location.equals(Location)){
            ForecastFragment forecastFragment=
                    (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (null!=forecastFragment){
                forecastFragment.onLocationChanged();
            }
            DetailFragment detailFragment=(DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if (null!=detailFragment){
                detailFragment.onLocationChanged(location);
            }
            Location=location;
        }
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
           Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id==R.id.location_viewer){
            OpenMap();
        }

        return super.onOptionsItemSelected(item);
    }
private void OpenMap(){
    ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo current=connectivityManager.getActiveNetworkInfo();
    boolean isWifi=current!=null&&current.getType()==ConnectivityManager.TYPE_WIFI;
    if (isWifi) {
        String postalCode=Utility.getPreferredLocation(this);
        Uri uri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",postalCode)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(Intent.createChooser(intent, "Choose an app to view your location"));
        }
    }

}


    @Override
    public void onItemSelected(Uri dateUri) {
        if (mTwoPane){
            Bundle args=new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,dateUri);
            DetailFragment fragment=new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container,fragment,DETAIL_FRAGMENT_TAG)
                    .commit();
        }else {
            Intent intent=new Intent(this,DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);

        }
    }
}

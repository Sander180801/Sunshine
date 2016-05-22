package com.example.sander.sunshine;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sander.sunshine.data.WeatherContract;

import org.w3c.dom.Text;

public class ForecastAdapter extends CursorAdapter {

    Context mContext;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    private boolean mUseTodayLayout=true;

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


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext=context;
    }
    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout=useTodayLayout;

    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0&& mUseTodayLayout)? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Copy/paste note: Replace existing newView() method in ForecastAdapter with this one.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if (viewType==VIEW_TYPE_TODAY){
            layoutId=R.layout.list_item_forecast_today;
        }else if (viewType==VIEW_TYPE_FUTURE_DAY){
            layoutId=R.layout.list_item_forecast;
        }
       View view=LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder=(ViewHolder)view.getTag();
        int ViewType=getItemViewType(cursor.getPosition());
        switch (ViewType){
            case VIEW_TYPE_TODAY:
                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(
                        cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            case VIEW_TYPE_FUTURE_DAY:
                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(
                        cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
        }
        long dateInMilis=cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.DateView.setText(Utility.getFriendlyDayString(context,dateInMilis));
        boolean isMetric=Utility.isMetric(context);
        double high=cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double low=cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.HighView.setText(Utility.formatTemperature(context,high));
        viewHolder.HighView.setContentDescription(context.getString(R.string.format_high, Utility.formatTemperature(context, high)));
        viewHolder.LowView.setText(Utility.formatTemperature(context, low));
        viewHolder.LowView.setContentDescription(context.getString(R.string.format_low,Utility.formatTemperature(context,low)));
        String description=cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.iconView.setContentDescription(description);
        viewHolder.descriptionView.setText(description);
    }
    public static class ViewHolder{
        public final ImageView iconView;
        public final TextView HighView;
        public final TextView LowView;
        public final TextView DateView;
        public final TextView descriptionView;



        public ViewHolder(View view){
           iconView=(ImageView)view.findViewById(R.id.list_item_icon);
            HighView=(TextView)view.findViewById(R.id.list_item_high_textview);
            LowView=(TextView)view.findViewById(R.id.list_item_low_textview);
            descriptionView=(TextView)view.findViewById(R.id.list_item_forecast_textview);
            DateView=(TextView)view.findViewById(R.id.list_item_date_textview);

        }
    }
}

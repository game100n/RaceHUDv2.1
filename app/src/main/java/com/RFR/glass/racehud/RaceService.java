package com.RFR.glass.racehud;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class RaceService extends Service {

    /** For logging. */
    private static final String TAG = "RaceService";
    private static final String LIVE_CARD_TAG = "RaceService";

    private GPSManager mGPSManager;

    private LiveCard mLiveCard;

    @Override
    public void onCreate()
    {
        super.onCreate();

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mGPSManager = new GPSManager(locationManager);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (mLiveCard == null)
        {
            Log.d(TAG, "Publishing LiveCard");
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            RaceRenderer renderer = new RaceRenderer(this, mGPSManager);
            mLiveCard.setDirectRenderingEnabled(true).getSurfaceHolder().addCallback(renderer);

            // Display the options menu when the live card is tapped.
            Intent menuIntent = new Intent(this, RaceActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.attach(this);
            mLiveCard.publish(PublishMode.REVEAL);
            Log.d(TAG, "Done publishing LiveCard");
        }

        else
        {
            mLiveCard.navigate();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        if (mLiveCard != null && mLiveCard.isPublished())
        {
            Log.d(TAG, "Unpublishing LiveCard");
            mLiveCard.unpublish();
            mLiveCard = null;
        }

        mGPSManager = null;
        super.onDestroy();
    }
}

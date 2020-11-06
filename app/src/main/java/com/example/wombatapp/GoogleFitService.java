package com.example.wombatapp;

import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.wombatapp.dashboard.DashboardActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GoogleFitService extends Service implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Google Fit API's
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    private String emailaddress = "E Null";
    protected DashboardActivity dashboardActivity;
    ArrayList<String> accountlist = new ArrayList<>();
    DashboardActivity activity;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //      accountlist = intent.getStringArrayListExtra("UserID");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        accountlist = intent.getStringArrayListExtra("UserID");
        Toast.makeText(this, "" + accountlist.size(), Toast.LENGTH_SHORT).show();
        dashboardActivity = new DashboardActivity();
        Toast.makeText(this, "service created " + accountlist.size(), Toast.LENGTH_SHORT).show();
     //   for (String accountname : accountlist) {
//            mApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(Fitness.SENSORS_API)
//                    //set Account name
//                    .setAccountName(accountname)
//                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
//                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//            mApiClient.connect();
    //        emailaddress = accountname;
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    // your action
//                    Log.e("epple:", "9 sec");
//                }
//            }, 20000);
     //   }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activity=new DashboardActivity();
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                //set Account name
                .setAccountName("Ahmad.rehman12@gmail.com")
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //   mApiClient.connect();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();
        Toast.makeText(GoogleFitService.this, "Email " + emailaddress, Toast.LENGTH_SHORT).show();
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        PendingResult<DataReadResult> resultPendingResult = Fitness.HistoryApi.readData(mApiClient, readRequest);
        resultPendingResult.setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(@NonNull DataReadResult dataReadResult) {
                if (dataReadResult.getBuckets().size() > 0) {
                    for (Bucket bucket : dataReadResult.getBuckets()) {
                        List<DataSet> datasets = bucket.getDataSets();
                        for (DataSet dataSet : datasets) {
                            //show data points
                            processDataSet(dataSet);
                        }
                    }
                }
                //if empty and data fetched
                if (dataReadResult.getBuckets().isEmpty()) {

                }
            }
        });
    }

    public void processDataSet(DataSet dataSet) {
        for (DataPoint dp : dataSet.getDataPoints()) {
            //obtain human readable start and end times
            long dpStart = dp.getStartTime(TimeUnit.NANOSECONDS);
            long dpEnd = dp.getEndTime(TimeUnit.NANOSECONDS);

            for (Field field : dp.getDataType().getFields()) {
                String fieldName = field.getName();
                Toast.makeText(GoogleFitService.this, "" + fieldName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!authInProgress) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult(activity, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {

            }
        } else {
            Log.e("GoogleFitf", "authInProgresss");
        }
        if (connectionResult.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                connectionResult.startResolutionForResult(activity, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
            }
        }

    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {

    }
}

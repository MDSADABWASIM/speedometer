package mdsadabwasimcom.speedometer;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.IBinder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    public static TextView dist, time, speed;
    Button start, pause, stop;
    static long startTime, endTime;
    ImageView image;
    static ProgressDialog locate;
    static int p = 0;

    private ServiceConnection sc= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder= (LocationService.LocalBinder) service;
            myService=binder.getService();
            status=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
             status=false;
        }
    };

    void bindService(){
        if (status)
            return;
        Intent intent= new Intent(getApplicationContext(),LocationService.class);
        bindService(intent,sc,BIND_AUTO_CREATE);
        status=true;
        startTime = System.currentTimeMillis();
    }



    void unbindService(){
        if (!status)
            return;
        unbindService(sc);
            status=false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (status)
            unbindService();
    }

    @Override
    public void onBackPressed() {
        if (!status)
            super.onBackPressed();
        else
            moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dist = (TextView) findViewById(R.id.distancetext);
        time = (TextView) findViewById(R.id.timetext);
        speed = (TextView) findViewById(R.id.speedtext);

        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);

        image = (ImageView) findViewById(R.id.image);


//set onClick listener on start button.
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //The method below checks if Location is enabled on device or not. If not, then an alert dialog box appears with option
                //to enable gps.
                checkGps();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    return;
                }


                if (!status)
                    //Here, the Location Service gets bound and the GPS Speedometer gets Active.
                    bindService();
                locate = new ProgressDialog(MainActivity.this);
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Getting Location...");
                locate.show();
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                pause.setText(R.string.pause);
                stop.setVisibility(View.VISIBLE);


            }
        });
        //set Onclick listener in pause button.

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause.getText().toString().equalsIgnoreCase("pause")) {
                    pause.setText(R.string.resume);
                    p = 1;

                } else if (pause.getText().toString().equalsIgnoreCase("Resume")) {
                    checkGps();
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pause.setText(R.string.pause);
                    p = 0;

                }
            }
        });


        //set onclick listener in stop button.

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status)
                    unbindService();
                start.setVisibility(View.VISIBLE);
                pause.setText(R.string.pause);
                pause.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                p = 0;
            }
        });

    }

    //This method leads you to the alert dialog box.
    void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}


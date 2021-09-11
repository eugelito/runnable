package com.example.runnable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.widget.Toolbar;

import android.view.MenuItem;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.Manifest;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private SensorManager sensorManager;
    private float acceleration, previousY, currentY;
    private int numSteps;
    private SeekBar seekBar;
    private double threshold;
    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    static TextView tvDistance, tvDuration, tvSpeed, tvSteps;
    static long startTime, endTime;
    static ProgressDialog locate;
    static int p = 0;
    private static final int CAM_REQUEST = 1313;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button endBtn;
    private String userChoosenTask;
    private TextView textviewFirstName, textviewLastName, textviewEmail;
    private int STORAGE_PERMISSION_CODE = 23;
    private ImageView ivImage;

    //ET - Create a constant of the variable NavigationView to be used
    private NavigationView navigationView;
    NavigationView mNavigationView;
    View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Runnable");


        //JC - created the request for permission required by app for Android 6.0 or higher
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

       /*     if(!SharedPrefManager.getInstance(this).isLoggedIn()){

                //finish();
                startActivity(new Intent(this, LoginActivity.class));
            }*/ //BL - Allow facebook to login through to MainActivity without session

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        // NavigationView Header
        mHeaderView = mNavigationView.getHeaderView(0);


        textviewFirstName = (TextView) mHeaderView.findViewById(R.id.textViewFirstNameNav);
        textviewFirstName.setText(SharedPrefManager.getInstance(this).getFirstName());
        textviewLastName = (TextView) mHeaderView.findViewById(R.id.textViewLastNameNav);
        textviewLastName.setText(SharedPrefManager.getInstance(this).getLastName());
        textviewEmail = (TextView) mHeaderView.findViewById(R.id.textViewEmailNav);
        textviewEmail.setText(SharedPrefManager.getInstance(this).getEmail());
        mNavigationView.setNavigationItemSelectedListener(this);

        setTitle("Runnable");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ET - Find the id of the drawer layout, and on back press close the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

         /*   LinearLayout navigationViews = (LinearLayout) findViewById(R.id.nav_view);
            textviewUsername = (TextView)navigationViews.findViewById(R.id.textViewUsernameNav);
            textviewEmail = (TextView)navigationViews.findViewById(R.id.textViewEmailNav);
            textviewUsername.setText("Test");
            textviewEmail.setText("Test");
*/


        tvDuration = (TextView) findViewById(R.id.durationValue);
        tvSteps = (TextView) findViewById(R.id.stepsValue);
        tvSpeed = (TextView) findViewById(R.id.speedValue);
        tvDistance = (TextView) findViewById(R.id.distanceValue);

        //textViewX = (TextView) findViewById(R.id.textViewX);
        //textViewY = (TextView) findViewById(R.id.textViewY);
        //textViewZ = (TextView) findViewById(R.id.textViewZ);
        // JC - these variables show the acceleration sensor


        final Button buttonStart = (Button) findViewById(R.id.startBtn);

        //JC - Variables to set threshold for step counter and acceleration
        threshold = 5.8;
        previousY = 0;
        currentY = 0;
        numSteps = 0;
        acceleration = 0.00f;


        //btnSelect = (Button) findViewById(R.id.imageButton);
        endBtn = (Button) findViewById(R.id.endBtn);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //JC - call method
                enableAccelerometerListening();


                //JC - set visibility of buttons
                endBtn.setVisibility(View.VISIBLE);
                buttonStart.setVisibility(View.INVISIBLE);
                //JC - The method below checks if Location is enabled on device or not. If not, then an alert dialog box appears with option
                //to enable gps.
                checkGps();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    return;
                }


                if (status == false)
                    //JC - Here, the Location Service gets bound and the GPS Speedometer gets Active.
                    bindService();
                locate = new ProgressDialog(MainActivity.this);
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Getting Location...");
                locate.show();

            }
        });
        //JC - Set on click listener when user clicks on button perform following:
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //JC - set visibility of buttons
                endBtn.setVisibility(View.INVISIBLE);
                buttonStart.setVisibility(View.VISIBLE);

                //JC - assign variables to XML file objects
                final TextView tDistance = (TextView) findViewById(R.id.distanceValue);
                final TextView tSteps = (TextView) findViewById(R.id.stepsValue);
                final TextView tSpeed = (TextView) findViewById(R.id.speedValue);
                final TextView tDuration = (TextView) findViewById(R.id.durationValue);


                //JC - set TextView(EditText) variables to final string variables
                final String distance = tDistance.getText().toString();
                final String steps = tSteps.getText().toString();
                final String speed = tSpeed.getText().toString();
                final String duration = tDuration.getText().toString();


                //JC - Response listener pass to register request
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JC - Get string and convert to JSONObject
                            JSONObject jsonResponse = new JSONObject(response);

                            //JC - get from Register.php $response variable with the value 'success' = true
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                //JC - use alert dialog to display when a user run is logged to database
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Run Logged.")
                                        .setNegativeButton("Okay", null)
                                        .create();
                                builder.show();


                            } else {
                                //If user is not successful display error
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Run Save Failed.")
                                        .setNegativeButton("Retry", null)
                                        .create();
                                builder.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //JC - Volley request which send data to database connection
                MainRequest mainRequest = new MainRequest(distance, steps, speed, duration, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(mainRequest);


            }
        });
        //ET - Location icon on app bar, directs to Map with current location
        ImageView mapClick = (ImageView) findViewById(R.id.app_location);
        mapClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Fragment mapFragment = new MapCal();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, mapFragment);
                ft.commit();
                setTitle("Planner");
            }
        });

        //ET - Music icon on app bar, directs to in built phone music
        ImageView musicClick = (ImageView) findViewById(R.id.app_music);
        musicClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent musicIntent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                startActivity(musicIntent);
            }
        });

        //ET - Camera icon on app bar, directs to Camera Fragment
        ImageView cameraClick = (ImageView) findViewById(R.id.app_camera);
        cameraClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Fragment fragment = new CameraFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, fragment);
                ft.commit();
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //ET - Checks what view the user has clicked and implements the opening of fragment
    //Pass navigation Item id, telling us which screen to be displayed with a fragment object
    //Switch the ID that is passed, cases each for the menus
    private void displaySelectedScreen(int id) {
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_home:
                fragment = new Fragment();
                setTitle("Runnable");
                break;
            case R.id.nav_account:
                fragment = new AccountFragment();
                break;
            case R.id.nav_activity:
                fragment = new ActivityFragment();
                break;
            case R.id.nav_gallery:
                fragment = new GalleryFragment();
                break;
            case R.id.nav_directions:
                fragment = new MapCal();
                setTitle("Planner");
                break;
            case R.id.nav_logout:
                SharedPrefManager.getInstance(this).logout();
                Toast.makeText(this, "You have successfully logged out", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
    }

    //ET - display selected screen methods get item id of item of navigation drawer clicked
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);
        return true;
    }

    //JC - check method to make sure GPS is available
    void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            showGPSDisabledAlertToUser();
        }
    }

    //JC - This method configures the Alert Dialog box.
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


    private void enableAccelerometerListening() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
    }

    //JC - method to count steps of users
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            currentY = y;

            if (Math.abs(currentY - previousY) > threshold) {
                numSteps++;
                tvSteps.setText(String.valueOf(numSteps));
            }

            //textViewX.setText(String.valueOf(x));
            //textViewY.setText(String.valueOf(y));
            //textViewZ.setText(String.valueOf(z));
            previousY = y;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        status = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    //JC - Check for permissions needed for Android 6.0 or higher
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                }
                break;

            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to Storage or Location", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }


}




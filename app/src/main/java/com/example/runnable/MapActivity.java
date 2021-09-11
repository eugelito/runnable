package com.example.runnable;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.SyncStateContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import static com.example.runnable.R.id.map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    // JD - Declaration of map variable - type private
    private GoogleMap mMap;

    //JD - onCreate method used to load the map fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        //JD -  Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

    }

    //JD - Casting of map variable when map is loaded
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //JD - Add a marker to specified location and move the camera
        mMap.addMarker(new MarkerOptions().position(new LatLng(54.607868, -5.926437)).title("Marker"));
        // JD - Check to see if permissions have been granted to access course and fine location for updating location using gps
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        // JD - Enabling the maps to locate user location through gps by setting the boolean value to true
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // JD - Handle navigation view item clicks here.
        int id = item.getItemId();
         //JD - if statement used alongside intents to navigate users to different areas of the site
        if (id == R.id.nav_home) {
            Intent MainActivityIntent = new Intent(MapActivity.this, MainActivity.class);
            MapActivity.this.startActivity(MainActivityIntent);
        }
        else if (id == R.id.nav_account) {
            // JD - Handle the camera action
            AccountFragment accountFragment = new AccountFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.coordinatorLayout_fragment,
                    accountFragment,
                    accountFragment.getTag()).commit();
            // Use of toast library to display messages to the user to inform them about what section of the app they are visiting
            Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_activity) {
            Toast.makeText(this, "Activity", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "You have successfully logged out", Toast.LENGTH_LONG).show();
            Intent LoginActivityIntent = new Intent(MapActivity.this, LoginActivity.class);
            MapActivity.this.startActivity(LoginActivityIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
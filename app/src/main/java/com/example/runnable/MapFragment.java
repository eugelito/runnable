package com.example.runnable;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

public class MapFragment extends Fragment implements OnMapReadyCallback {

//JD - Declaration of Global Variables to be used within methods

    MapView mMapView;
    View mView;
    private GoogleMap mMap;

//JD - Casting of View variable to inflate the map layout through the use of LayoutInflater
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    //JD - When view is created the title is set to maps and the mapView variable is casted to the map by reading the id
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Maps");
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

//JD - Initialising the  map variable with google maps
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // JD -Addition of a marker to the map
        mMap.addMarker(new MarkerOptions().position(new LatLng(54.596484,-5.930053)).title("Marker"));

        //JD - Setting the map type to normal - standard google map view
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //JD - Moving the position of the camera to Belfast to get the map to focus on that area
        CameraPosition Belfast = CameraPosition.builder().target(new LatLng(54.571822,-5.937491)).zoom(16).bearing(0).tilt(16).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Belfast));

        // JD - Check to see if permissions have been granted to access course and fine location for updating location using gps
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //JD - Enabling the location settings to detect the location of the users device by setting the boolean value of location enabled to true
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


    }


}

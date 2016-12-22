package com.timdingess.timlearnsmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import android.location.LocationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;



import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private LatLng prevPos;
    private Marker prevMarker;

    public final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0;

    private int NumUpdates = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BuildMapMarkers();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener =  new MyLocationListener();

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Toast.makeText(getApplicationContext(), "its true", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "its false", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        }
        else
        {
            //Toast.makeText(getApplicationContext(), "skipped", Toast.LENGTH_LONG).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
        }
    }

    private void BuildMapMarkers()
    {
        MapsCommon.MarkerList.add(new MapMaker(new LatLng(32.747619, -97.094093), "Cowboys Stadium"));
        //MapsCommon.MarkerList.add(new MapMaker(new LatLng(33.008630, -96.991791), "Empty Lot"));
        MapsCommon.MarkerList.add(new MapMaker(new LatLng(33.008669, -96.994406), "Home"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    }
                    catch (SecurityException sex)
                    {
                        Toast.makeText(getApplicationContext(), sex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(), "99 " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
//                    Toast.makeText(getApplicationContext(), "Denied " + grantResults.length, Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        for(int x=0; x< MapsCommon.MarkerList.size(); x++)
        {
            MapMaker marker = (MapMaker)MapsCommon.MarkerList.get(x);
            mMap.addMarker(new MarkerOptions().position(marker.Coordinates).title(marker.Name));
        }

        if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
//        else
//            Toast.makeText(getApplicationContext(), "Don't have permission yet?", Toast.LENGTH_LONG).show();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.008630, -96.997791), 14.0f) );


    }

    public void setMapLocation(Location loc)
    {
        NumUpdates++;

        LatLng location = new LatLng(loc.getLatitude(), loc.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        if(prevMarker != null)
            prevMarker.remove();
        prevMarker = mMap.addMarker(new MarkerOptions().position(location).title(new Integer(NumUpdates).toString()));

        //MapsCommon.LocationsList.add(location);
//        for(int x=0; x< MapsCommon.LocationsList.size(); x++)
//        {
//            if (x==0)
//            {
//                prev = (LatLng)MapsCommon.LocationsList.get(x);
//            }
//            else
//            {
//                LatLng current = (LatLng)MapsCommon.LocationsList.get(x);
//                PolygonOptions po = new PolygonOptions().add(prev, current).strokeColor(Color.RED);
//                mMap.addPolygon(po);
//                prev = current;
//            }
//        }

        if(prevPos == null)
        {
            prevPos = location;
        }
        else
        {
            PolygonOptions po = new PolygonOptions().add(prevPos, location).strokeColor(Color.RED);
            mMap.addPolygon(po);
            prevPos = location;
        }
    }

    private class MyLocationListener implements LocationListener
    {
        public MyLocationListener()
        {

        }

        public void onLocationChanged(Location location)
        {
            try
            {
                setMapLocation(location);
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}

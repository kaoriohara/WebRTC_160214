package io.skyway.testpeerjava;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //システムサービスのLOCATION_SERVICEからLocationManager objectを取得
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //retrieve providerへcriteria objectを生成
        Criteria criteria = new Criteria();
        //Best providerの名前を取得
        String provider = locationManager.getBestProvider(criteria, true);
//        //現在位置を取得
//        Location location = locationManager.getLastKnownLocation(provider);
//        if(location!=null){
//            onLocationChanged(location);
//        }
//        locationManager.requestLocationUpdates(provider, 20000, 0, this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Google MapのMyLocationレイヤーを使用可能にする
        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location loc) {
                LatLng curr = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(curr));
                //Google Mapの Zoom値を指定
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        });

//        LatLng position = new LatLng(0, 0);
//        MarkerOptions options = new MarkerOptions();
//        options.position(position);
//        options.title("title");
//        map.addMarker(options);
        }

}

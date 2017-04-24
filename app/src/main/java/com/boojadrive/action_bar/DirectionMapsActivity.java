package com.boojadrive.action_bar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;

public class DirectionMapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,LocationListener {

    private GoogleMap mMap;
    public List<Address> addresses = null;
    public static final String TAG = "@@@";



    View marker_root_view;
    TextView tv_marker;
    MarkerItem sendmarkerItem;
    public   boolean test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        setCustomMarkerView();


        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener()
        {
            @Override
            public void onCameraMoveStarted(int i) {
                test=true;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(test) {
                    makemaker();
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


                }
                test=false;
            }
        });


        mMap.setOnMarkerClickListener(this);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(newLatLng(sydney));
        SetMapPosition();
    }

    public void SetMapPosition(){
        Intent intent = getIntent();
        double startlat = intent.getDoubleExtra("Lat", 11);
        double startlog = intent.getDoubleExtra("Log", 22);
        LatLng latLng = new LatLng(startlat, startlog);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
    public void makemaker(){


        String errorMessage = "";
        mMap.clear();
        LatLng latLng = mMap.getCameraPosition().target;
        //지도 상에서 보여주는 영역 이동
        mMap.moveCamera(newLatLng(latLng));
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setCompassEnabled(true);
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(DirectionMapsActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude, 1);
            MarkerItem markerItem = new MarkerItem(latLng.latitude, latLng.longitude, addresses.get(0).getAddressLine(0).toString());
            sendmarkerItem=markerItem;
            addMarker(markerItem, false);
            // In this sample, we get just a single address.1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "지오코더 서비스 사용불가";
            Toast.makeText(DirectionMapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "잘못된 GPS 좌표";
            Toast.makeText(DirectionMapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();

        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = String.format("주소 미발견" + latLng.longitude);
                Log.e(TAG, errorMessage);
            }
            Toast.makeText(DirectionMapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        } else {

        }

    }

    ///마처초기화
    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }

    ////////커스텀마커추가
    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        String direction = markerItem.getdirection();
        tv_marker.setText(direction);
        if (isSelectedMarker) {
            tv_marker.setBackgroundResource(R.drawable.ic_marker_phone_blue);
            tv_marker.setTextColor(Color.WHITE); }
        else {
            tv_marker.setBackgroundResource(R.drawable.ic_marker_phone);
            tv_marker.setTextColor(Color.BLACK);
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(direction);
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
        return mMap.addMarker(markerOptions);
    }

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent=new Intent(this,MainActivity.class);

        intent.putExtra("end",sendmarkerItem.direction);

        intent.putExtra("Lat",sendmarkerItem.getLat());
        intent.putExtra("Log",sendmarkerItem.getLon());

        setResult(RESULT_OK, intent);
        finish();
        return false;
    }
}

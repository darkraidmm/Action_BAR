package com.boojadrive.action_bar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_GPS = 2001;//임의의 정수로 정의
    private static final int RC_LOCATION_CONTACTS_PERM = 124;
    private static final String TAG_RESULTS="result";

    private static final int REQUEST_START_LOCATION = 111;
    private static final int REQUEST_END_LOCATION = 112;
    private static final int REQUEST_START_MAP = 113;
    private static final int REQUEST_END_MAP = 114;
    private static final int REQUEST_COD_CARD = 115;
    private static final int REQUEST_REG_HOME = 116;
    private static final int REQUEST_REG_FAVER = 117;
    private static final int REQUEST_WAIT_ORDER = 118;
    public static final String TAG = "@@@";

    public String ss = "@@@";
    public List<Address> addresses = null;
    boolean butencheck = true;
    boolean chekCustmer=false;
    public LatLng StartlatLng;
    public LatLng DirectionatLng;

    public float nNumber = 0;
    public boolean directionfull = false;
    public boolean checkcoustom=false;
    boolean endbuttnclicked = false;
    public String AllPrice = "전화문의";
    public Cutemer_Data cutemer_data;


    String constomer = null;//고객데디터
    String myJSON=null;
    phpdo task;
    TextView txtview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ///////////////////퍼미션체크
        permissionsTask();
        ///////////////GPS활성화체크
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            // GPS가 꺼져있을 시 앱이 수행할 작업 코드
            showGPSDisabledAlertToUser();
        }
        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);
        }


        final Custom_layout custom_layout = (Custom_layout) findViewById(R.id.CustomStart);
        Button startbutton = custom_layout.button;
        startbutton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Startbutton_clicked(v);

            }
        });

        Button start_map_button = custom_layout.button2;
        start_map_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                StartMaputo_clicked(v);


            }
        });


        Custom_layout custom_layout2 = (Custom_layout) findViewById(R.id.Customend);
        Button endbutton = custom_layout2.button;
        endbutton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Directbutton_clicked(v);
                endbuttnclicked = true;

            }
        });

        //집으로 가는 길 설정
        Faivort_Button faivort_button = (Faivort_Button) findViewById(R.id.CustomTest);
        TextView textView = faivort_button.textView2;
        textView.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View v) {
              SetHome();
            }
        });


        //집말고 자주가는 곳 설정
        TextView textView2 = faivort_button.textView3;
        textView2.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View v) {
                SetFaver();

            }
        });


        Button end_map_button = custom_layout2.button2;
        end_map_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (endbuttnclicked) {
                    Direction_clicked(v);
                } else {
                    Toast.makeText(getApplicationContext(), "목적지를 확인해 주세요 ", Toast.LENGTH_LONG).show();
                }
            }
        });


        if(CheckAppFirstExecute()){

            UserDataCheck();

        }
        cutemer_data=new Cutemer_Data();
        SetcustomerData();


    }
    //key down
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){


        //백할 페이가 없다면
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //다이아로그박스 출력
            new AlertDialog.Builder(this)
                    .setTitle("프로그램 종료")
                    .setMessage("프로그램을 종료하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton("아니오",  null).show();
        }

        return super.onKeyDown(keyCode, event);
    }








    // 최초실행확인
    public boolean CheckAppFirstExecute(){
        SharedPreferences pref = getSharedPreferences("IsFirst" , Activity.MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", false);
        if(!isFirst){ //최초 실행시 true 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.commit();
        }

        return !isFirst;
    }




    public void SetHome(){

        if(cutemer_data.home!=null){



            Custom_layout custom_layout=(Custom_layout)findViewById(R.id.Customend);
            custom_layout.setButton(cutemer_data.home);
            LatLng latLngA = StartlatLng;
            Location locationA = new Location("a");
            Location locationB = new Location("b");

            locationA.setLatitude(latLngA.latitude);
            locationA.setLongitude(latLngA.longitude);

            locationB.setLatitude(cutemer_data.Home_lat);
            locationB.setLongitude(cutemer_data.Home_log);


            float nNber = 0;
            nNber = Math.round(locationA.distanceTo(locationB) / 1000);
            CalPrice(nNber);

           // float nNumber=locationA.distanceTo(locationB);

            endbuttnclicked=true;
        }
       else
        {
            Intent intent = null;
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setBoundsBias(new LatLngBounds(
                                new LatLng(34, 125),
                                new LatLng(38, 130)))
                        .build(this);
                if (intent != null) {
                    startActivityForResult(intent, REQUEST_REG_HOME);
                }
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                        0 /* requestCode */).show();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        }



    }
    public void SetFaver() {
        // CustomDataHandler customDataHandler = new CustomDataHandler(this, null, null, 1);
        // CustomerData customerData =customDataHandler.findCustomerDara();
        if (cutemer_data.faver != null) {

            Custom_layout custom_layout = (Custom_layout) findViewById(R.id.Customend);
            custom_layout.setButton(cutemer_data.faver);
            LatLng latLngA = StartlatLng;
            Location locationA = new Location("a");
            Location locationB = new Location("b");

            locationA.setLatitude(latLngA.latitude);
            locationA.setLongitude(latLngA.longitude);

            locationB.setLatitude(cutemer_data.f_lat);
            locationB.setLongitude(cutemer_data.f_log);


            float nNber = 0;
            nNber = Math.round(locationA.distanceTo(locationB) / 1000);
            CalPrice(nNber);

            endbuttnclicked=true;
        } else {
            Intent intent = null;
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setBoundsBias(new LatLngBounds(
                                new LatLng(34, 125),
                                new LatLng(38, 130)))
                        .build(this);
                if (intent != null) {
                    startActivityForResult(intent, REQUEST_REG_FAVER);
                }
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                        0 /* requestCode */).show();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

            //  }
        }
    }


    //////////////////////////GPS활성화 코드 입력
    private void showGPSDisabledAlertToUser() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS가 비활성화 되어있습니다. 활성화 할까요?")
                .setCancelable(false)
                .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(callGPSSettingIntent, REQUEST_CODE_GPS);
                    }
                });

        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    ////////////장소학인해서 현재우치 를 설정하기
    private final android.location.LocationListener mLocationListener = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {
            if (butencheck) {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    StartlatLng = latLng;
                    StringBuilder stringBuilder = new StringBuilder(addresses.get(0).getAddressLine(0));
                    stringBuilder.delete(0, 4);
                    Custom_layout custom_layout = (Custom_layout) findViewById(R.id.CustomStart);
                    custom_layout.setButton(stringBuilder.toString());

                    butencheck = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //현제위치 확인하고 고객 위치 체크

        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    ////////////////////////퍼미션 설정하기 시작...
    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    public void permissionsTask() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
            //Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_contacts),
                    RC_LOCATION_CONTACTS_PERM, perms);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        Toast.makeText(this, "위치정보와 전화번호 확인에 동의해주셔서 감사합니다.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "거부를하다니 ??" + requestCode + "해줘 응????" + perms.size());
        Toast.makeText(this, "위치정보와 전화번호 확인에 동의해 주셔야 프로그램을 정상적으로 이용하실수 있습니다..", Toast.LENGTH_LONG).show();
        finish();

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    //////////////////퍼미션 확인 끝

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    ////////////////////////////////네비계이션 바 컨트롤 해주는부분

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent intent = null;
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setBoundsBias(new LatLngBounds(
                                new LatLng(34, 125),
                                new LatLng(38, 130)))
                        .build(this);
                if (intent != null) {
                    startActivityForResult(intent, REQUEST_REG_HOME);
                }
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                        0 /* requestCode */).show();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }


        } else if (id == R.id.nav_favorate) {


            Intent intent = null;
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setBoundsBias(new LatLngBounds(
                                new LatLng(34, 125),
                                new LatLng(38, 130)))
                        .build(this);
                if (intent != null) {
                    startActivityForResult(intent, REQUEST_REG_FAVER);
                }
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                        0 /* requestCode */).show();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }


        }else if(id==R.id.nav_list){

            Intent intent = new Intent(this, DriveList.class);
            startActivity(intent);



        }
        else if (id == R.id.nav_share) {
            //추천하기기
            Intent msg = new Intent(Intent.ACTION_SEND);

            msg.addCategory(Intent.CATEGORY_DEFAULT);

            msg.putExtra(Intent.EXTRA_SUBJECT, "추천");

            msg.putExtra(Intent.EXTRA_TEXT, "부자대리운전을 추전합니다.");

            msg.putExtra(Intent.EXTRA_TITLE, "부자대리운전 추천");

            msg.setType("text/plain");

            startActivity(Intent.createChooser(msg, "공유"));


        } else if (id == R.id.nav_send) {
            // 카드등록

            Intent intent = new Intent(this, RegisterPaymentActivity.class);
            startActivityForResult(intent, REQUEST_COD_CARD);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    ///////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////


    /////////////목적지 주소검색
    public void Directbutton_clicked(View view) {
        Intent intent = null;
        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setBoundsBias(new LatLngBounds(
                            new LatLng(34, 125),
                            new LatLng(38, 130)))
                    .build(this);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_END_LOCATION);
            }
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    /////출발지 지도보기
    public void StartMaputo_clicked(View view) {

        Intent intent = new Intent(this, DirectionMapsActivity.class);
        intent.putExtra("Lat", StartlatLng.latitude);
        intent.putExtra("Log", StartlatLng.longitude);
        startActivityForResult(intent, REQUEST_START_MAP);

    }


    ////목적지 지도보기
    public void Direction_clicked(View view) {

        Intent intent = new Intent(this, DirectionMapsActivity.class);
        intent.putExtra("Lat", DirectionatLng.latitude);
        intent.putExtra("Log", DirectionatLng.longitude);
        startActivityForResult(intent, REQUEST_END_MAP);

    }


    ///////////출발지를 변경할때
    public void Startbutton_clicked(View view) {
        Intent intent = null;
        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setBoundsBias(new LatLngBounds(
                            new LatLng(34, 125),
                            new LatLng(38, 130)))
                    .build(this);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_START_LOCATION);
            }
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

   /////고객정보확인
    void SetcustomerData()
    {
        String user_phone="";
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getLine1Number()!=null) {
            user_phone=telephonyManager.getLine1Number().toString();
        }
        String uri="http://darkraidmm.cafe24.com/getCustomerData.php?user_phone="+user_phone;

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;

                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    bufferedReader.close();

                    return sb.toString().trim();

                } catch (Exception e) {
                    return new String("Exexption" + e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Savedata(result);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(uri);
    }
    public void Savedata(String s)
    {
        try {
            JSONArray customer = null;
            JSONObject jsonObj = new JSONObject(s);
            customer = jsonObj.getJSONArray("result");
            JSONObject c = customer.getJSONObject(0);
            cutemer_data.user_id=c.getInt("user_id");
            cutemer_data.user_phone=c.getString("user_phone");
            cutemer_data.home=c.getString("home");
            cutemer_data.Home_lat=c.getDouble("Home_lat");
            cutemer_data.Home_log=c.getDouble("Home_log");
            cutemer_data.faver=c.getString("faver");
            cutemer_data.f_lat=c.getDouble("f_lat");
            cutemer_data.f_log=c.getDouble("f_log");
            cutemer_data.card_number=c.getString("card_number");
            cutemer_data.card_date=c.getString("card_date");
            cutemer_data.user_phoint=c.getInt("user_phoint");
            cutemer_data.etc=c.getString("etc");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /////////////////////////////요금계산하기
    public float CalPrice(float fnumber) {



        float rNum = 0;
        if (fnumber < 10) {

            rNum = fnumber * 1000;

        } else if (fnumber >= 10.0 && fnumber < 20.0) {

            rNum = 10000 + (fnumber - 10) * 500;

        } else if (fnumber >= 20.0 && fnumber < 50.0) {

            rNum = 15000 + (fnumber - 20) * 400;

        } else if (fnumber >= 50.0 && fnumber < 100.0) {

            rNum = 27000 + (fnumber - 50) * 300;

        } else if (fnumber >= 100.0 && fnumber < 200.0) {

            rNum = 52000 + (fnumber - 100) * 200;

        } else if (fnumber >= 200.0 && fnumber < 500.0) {

            rNum = 72000 + (fnumber - 200) * 150;
        }

        rNum = Math.round(rNum / 1000) * 1000;
        rNum += 12000;
        if (rNum > 120000) {
            rNum = 120000;
        }

        EditText edttext=(EditText)findViewById(R.id.PRICE_EDIT_TEXT);
        nNumber=rNum;
        String AB=String.format("%.0f", nNumber);
        AB+="원";
        edttext.setText(AB);
        return rNum;
    }


    //// 목적지 입력시 예상요금 및 안내사항 출력
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //리턴되는 값이 존재
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_END_LOCATION) {
                //결과값으로 목적지 주소 입력하기
                Place place = PlaceAutocomplete.getPlace(this, data);
                StringBuilder stringBuilder2 = new StringBuilder(place.getAddress());
                stringBuilder2.delete(0, 4);
                Custom_layout custom_layout = (Custom_layout) findViewById(R.id.Customend);
                custom_layout.setButton(stringBuilder2.toString());

                Log.i(TAG, "Place Selected: " + place.getName());

                LatLng latLngA = StartlatLng;
                LatLng latLngB = place.getLatLng();
                DirectionatLng = latLngB;

                Location locationA = new Location("a");
                Location locationB = new Location("b");

                locationA.setLatitude(latLngA.latitude);
                locationA.setLongitude(latLngA.longitude);

                locationB.setLatitude(latLngB.latitude);
                locationB.setLongitude(latLngB.longitude);

                //nNumber=Math.round(locationA.distanceTo(locationB)/1000)*1000;

                float nNber = 0;
                nNber = Math.round(locationA.distanceTo(locationB) / 1000);
                nNumber = CalPrice(nNber);

                //float nNumber=locationA.distanceTo(locationB);
                String AB = String.format("%.0f", nNumber);
                AllPrice = AB;
                AB += "원";
                EditText editText = (EditText) findViewById(R.id.PRICE_EDIT_TEXT);
                editText.setText(AB);
                directionfull = true;
            } else if (requestCode == REQUEST_START_LOCATION) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());
                StringBuilder stringBuilder2 = new StringBuilder(place.getAddress());
                stringBuilder2.delete(0, 4);
                Custom_layout custom_layout = (Custom_layout) findViewById(R.id.CustomStart);
                custom_layout.setButton(stringBuilder2.toString());
                if (directionfull == true) {
                    LatLng latLngA = place.getLatLng();
                    LatLng latLngB = DirectionatLng;

                    Location locationA = new Location("a");
                    Location locationB = new Location("b");

                    locationA.setLatitude(latLngA.latitude);
                    locationA.setLongitude(latLngA.longitude);

                    locationB.setLatitude(latLngB.latitude);
                    locationB.setLongitude(latLngB.longitude);

                    float nNber = 0;
                    nNber = Math.round(locationA.distanceTo(locationB) / 1000);
                    nNumber = CalPrice(nNber);

                    //float nNumber=locationA.distanceTo(locationB);
                    String AB = String.format("%.0f", nNumber);
                    AB += "원";
                    EditText editText = (EditText) findViewById(R.id.PRICE_EDIT_TEXT);
                    editText.setText(AB);

                }
            } else if (requestCode == REQUEST_START_MAP) {


                String rData = data.getStringExtra("end");
                StringBuilder stringBuilder2 = new StringBuilder(rData);
                stringBuilder2.delete(0, 4);
                Custom_layout custom_layout3 = (Custom_layout) findViewById(R.id.CustomStart);
                custom_layout3.setButton(stringBuilder2.toString());


                if (directionfull == true) {

                    double Directionlat = data.getDoubleExtra("Lat", 1);
                    double Directionlog = data.getDoubleExtra("Log", 2);

                    Location locationA = new Location("a");
                    Location locationB = new Location("b");

                    locationA.setLatitude(StartlatLng.latitude);
                    locationA.setLongitude(StartlatLng.longitude);

                    locationB.setLatitude(Directionlat);
                    locationB.setLongitude(Directionlog);

                    float nNber = 0;
                    nNber = Math.round(locationA.distanceTo(locationB) / 1000);
                    nNumber = CalPrice(nNber);

                    //float nNumber=locationA.distanceTo(locationB);
                    String AB = String.format("%.0f", nNumber);
                    AB += "원";
                    EditText editText = (EditText) findViewById(R.id.PRICE_EDIT_TEXT);
                    editText.setText(AB);
                }
            } else if (requestCode == REQUEST_END_MAP) {


                String rData = data.getStringExtra("end");
                StringBuilder stringBuilder2 = new StringBuilder(rData);
                stringBuilder2.delete(0, 4);
                Custom_layout custom_layout4 = (Custom_layout) findViewById(R.id.Customend);
                custom_layout4.setButton(stringBuilder2.toString());


                if (directionfull == true) {
                    double Directionlat = data.getDoubleExtra("Lat", 1);
                    double Directionlog = data.getDoubleExtra("Log", 2);

                    Location locationA = new Location("a");
                    Location locationB = new Location("b");

                    locationA.setLatitude(StartlatLng.latitude);
                    locationA.setLongitude(StartlatLng.longitude);

                    locationB.setLatitude(Directionlat);
                    locationB.setLongitude(Directionlog);

                    float nNber = 0;
                    nNber = Math.round(locationA.distanceTo(locationB) / 1000);
                    nNumber = CalPrice(nNber);

                    //float nNumber=locationA.distanceTo(locationB);
                    String AB = String.format("%.0f", nNumber);
                    AB += "원";
                    EditText editText = (EditText) findViewById(R.id.PRICE_EDIT_TEXT);
                    editText.setText(AB);
                }

            } else if (requestCode == REQUEST_REG_HOME) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                StringBuilder stringBuilder2 = new StringBuilder(place.getAddress());
                stringBuilder2.delete(0, 4);
                LatLng latLngHome = place.getLatLng();
                double Directionlat =latLngHome.latitude;
                double Directionlog = latLngHome.longitude;
                String d1=String.valueOf(Directionlat);
                String d2=String.valueOf(Directionlog);

                String phonenymber="";
                TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                if(telephonyManager.getLine1Number()!=null) {
                    phonenymber=telephonyManager.getLine1Number().toString();
                }



                upDateHome(phonenymber,stringBuilder2.toString(),d1,d2);


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("홈주소 저장");
                builder.setMessage("주소가 자장되었습니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            } else if (requestCode == REQUEST_REG_FAVER) {


                Place place = PlaceAutocomplete.getPlace(this, data);
                StringBuilder stringBuilder2 = new StringBuilder(place.getAddress());
                stringBuilder2.delete(0, 4);
                LatLng latLngfaver = place.getLatLng();
                double Directionlat =latLngfaver.latitude;
                double Directionlog = latLngfaver.longitude;
                String d1=String.format("%f",Directionlat);
                String d2=String.format("%f",Directionlog);
                String phonenymber="";
                TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                if(telephonyManager.getLine1Number()!=null) {
                    phonenymber=telephonyManager.getLine1Number().toString();
                }
                upDatefaver(phonenymber,stringBuilder2.toString(),d1,d2);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("즐겨찾는 장소 저장");
                builder.setMessage("주소가 자장되었습니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();


            }

        } else {
            //Toast.makeText(getApplicationContext(), "비정상적인접근경로 입니다. ", Toast.LENGTH_LONG).show();
        }
    }


    public void upDatefaver(String user_phone,String faver ,String f_lat, String f_log){
        class InsertCData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please " +
                        "Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                // Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                BufferedReader bufferedReader = null;
                try {
                    String user_phone = (String) params[0];
                    String faver = (String) params[1];
                    String f_lat = (String) params[2];
                    String f_log = (String) params[3];

                    String link = "http://darkraidmm.cafe24.com/upDatefaver.php?user_phone="+user_phone+"&faver="+faver+"&f_lat="+f_lat+"&f_log="+f_log;


                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {


                        chekCustmer=true;
                        sb.append(line);
                    }

                    bufferedReader.close();
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        InsertCData task = new InsertCData();
        task.execute(user_phone,faver,f_lat,f_log);
    }


/////집 위치 변경
    public void upDateHome(String user_phone,String home ,String Home_lat, String Home_log){
        class InsertCData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please " +
                        "Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
               super.onPostExecute(s);
                loading.dismiss();
               // Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                BufferedReader bufferedReader = null;
                try {
                    String user_phone = (String) params[0];
                    String home = (String) params[1];
                    String Home_lat = (String) params[2];
                    String Home_log = (String) params[3];

                    String link = "http://darkraidmm.cafe24.com/upDateHome.php?user_phone="+user_phone+"&home="+home+"&Home_lat="+Home_lat+"&Home_log="+Home_log;


                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {


                        chekCustmer=true;
                        sb.append(line);
                    }

                    bufferedReader.close();
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        InsertCData task = new InsertCData();
        task.execute(user_phone,home,Home_lat,Home_log);

    }




    ////////////////전화걸기
    public void CallButton_clicked(View view) {

        String companny_id = "16448898";
        String call_type = "전화접수";

        Custom_layout start = (Custom_layout) findViewById(R.id.CustomStart);
        String startpoin = start.button.getText().toString();
        Custom_layout end = (Custom_layout) findViewById(R.id.Customend);
        String endpoint = end.button.getText().toString();
        String phonenymber = "";
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getLine1Number() != null) {
            phonenymber = telephonyManager.getLine1Number().toString();
        }
        String price = AllPrice;
        String card_number = "";
        String card_date = "";
        String card_pass = "";
        String use_point = "";

        insertToDatabase(companny_id, call_type, startpoin, endpoint, phonenymber, price, card_number, card_date, card_pass, use_point);


        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:16448898"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);




    }
    ///////////요금올리기
    public void Up_button_clicked(View view){
        EditText editText =(EditText)findViewById(R.id.PRICE_EDIT_TEXT);
        nNumber+=1000;
        String AB=String.format("%.0f", nNumber);
        AB+="원";
        editText.setText(AB);

    }

    ////////////////요금내리기
    public void Down_button_clicked(View view){
        if(nNumber>=1000) {
            EditText editText = (EditText) findViewById(R.id.PRICE_EDIT_TEXT);
            nNumber -= 1000;
            String AB = String.format("%.0f", nNumber);
            AB += "원";
            editText.setText(AB);
        }
    }


    //////카드건 처리
    public void card_button_clicked(View view){

        if (endbuttnclicked) {
            String string=cutemer_data.card_number;
            string+="\n";
            string+=cutemer_data.card_date;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("카드번호확인");
            builder.setMessage(string);

            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String companny_id="16448898";
                            String call_type="카드";
                            Custom_layout start=(Custom_layout)findViewById(R.id.CustomStart);;
                            String startpoin=start.getButtom();
                            Custom_layout end=(Custom_layout)findViewById(R.id.Customend);
                            String endpoint=end.getButtom();
                            String phonenymber="";
                            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                            if(telephonyManager.getLine1Number()!=null) {
                                phonenymber=telephonyManager.getLine1Number().toString();
                            }
                            String price=AllPrice;
                            String card_number=cutemer_data.card_number;
                            String card_date=cutemer_data.card_date;
                            String card_pass="";
                            String use_point="";
                            insertToDatabase(companny_id,call_type,startpoin,endpoint,phonenymber,price,card_number,card_date,card_pass,use_point);
                            // Toast.makeText(getApplicationContext(), "접수되었습니다. 잠시만 기달려주세요.", Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            GoRegisterPaymentActivity();
                        }
                    });
            builder.show();
        } else {
            Toast.makeText(getApplicationContext(), "목적지를 확인해 주세요 ", Toast.LENGTH_LONG).show();
        }

    }



    ///////카드결제 입력받기
    public void GoRegisterPaymentActivity()
    {

        Intent intent = new Intent(this, RegisterPaymentActivity.class);
        startActivityForResult(intent, REQUEST_COD_CARD);

    }

    //////////////////////현금결제건 처리
    public void cash_button_clicked(View view){
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("현금 접수");
        builder.setMessage("접수가 완료되업습니다.");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });*/

        //companny_id,call_type,startpoin,endpoint,phonenymber,price,card_number,card_date,card_pass,use_point



        if (endbuttnclicked) {
        String companny_id="16448898";
        String call_type="현금";

        Custom_layout start=(Custom_layout)findViewById(R.id.CustomStart);
        String startpoin=start.button.getText().toString();
        Custom_layout end=(Custom_layout)findViewById(R.id.Customend);
        String endpoint=end.button.getText().toString();
        String phonenymber="";
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getLine1Number()!=null) {
            phonenymber=telephonyManager.getLine1Number().toString();
        }
        String price=AllPrice;
        String card_number="";
        String card_date="";
        String card_pass="";
        String use_point="";

        insertToDatabase(companny_id,call_type,startpoin,endpoint,phonenymber,price,card_number,card_date,card_pass,use_point);
        } else {
            Toast.makeText(getApplicationContext(), "목적지를 확인해 주세요 ", Toast.LENGTH_LONG).show();
        }
       // builder.show();
    }






    public void point_button_clicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("포인트 결제");
        builder.setMessage("포인트가 부족합니다.");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();

    }

    /////// 신규고격인 경우 고객DB 생성
    public void insetCoustomerData(String user_phone,String home,String Home_lat,String Home_log,String faver,String f_lat ,
                                   String f_log,String card_number,String card_date,String user_phoint,String etc){

        class InsertCData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please " +
                        "Wait", null, true, true);


            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {

                    String user_phone = (String) params[0];
                    String home = (String) params[1];
                    String Home_lat = (String) params[2];
                    String Home_log = (String) params[3];
                    String faver = (String) params[4];
                    String f_lat = (String) params[5];
                    String f_log = (String) params[6];
                    String card_number = (String) params[7];
                    String card_date = (String) params[8];
                    String user_phoint = (String) params[9];
                    String etc = (String) params[10];


                    String link = "http://darkraidmm.cafe24.com/customerinsert.php";

                    String data = URLEncoder.encode("user_phone", "UTF-8")
                            + "=" + URLEncoder.encode(user_phone, "UTF-8");
                    data += "&" + URLEncoder.encode("home", "UTF-8") +
                            "=" + URLEncoder.encode(home, "UTF-8");
                    data += "&" + URLEncoder.encode("Home_lat", "UTF-8") +
                            "=" + URLEncoder.encode(Home_lat, "UTF-8");
                    data += "&" + URLEncoder.encode("Home_log", "UTF-8") +
                            "=" + URLEncoder.encode(Home_log, "UTF-8");
                    data += "&" + URLEncoder.encode("faver", "UTF-8")
                            + "=" + URLEncoder.encode(faver, "UTF-8");
                    data += "&" + URLEncoder.encode("f_lat", "UTF-8") + "="
                            + URLEncoder.encode(f_lat, "UTF-8");
                    data += "&" + URLEncoder.encode("f_log", "UTF-8")
                            + "=" + URLEncoder.encode(f_log, "UTF-8");
                    data += "&" + URLEncoder.encode("card_number", "UTF-8") +
                            "=" + URLEncoder.encode(card_number, "UTF-8");
                    data += "&" + URLEncoder.encode("card_date", "UTF-8") +
                            "=" + URLEncoder.encode(card_date, "UTF-8");
                    data += "&" + URLEncoder.encode("user_phoint", "UTF-8") +
                            "=" + URLEncoder.encode(user_phoint, "UTF-8");
                    data += "&" + URLEncoder.encode("etc", "UTF-8") +
                            "=" + URLEncoder.encode(etc, "UTF-8");
                    //ss=data.toString();


                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter
                            (conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;


                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);

                        break;
                    }

                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        InsertCData task = new InsertCData();
        task.execute(user_phone,home,Home_lat,Home_log,faver,f_lat,f_log,card_number,
                card_date,user_phoint,etc);
    }

    /////사용자 존제 유무 체그
    public boolean UserDataCheck() {

        String phonenymber = "";
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getLine1Number() != null) {
            phonenymber = telephonyManager.getLine1Number().toString();
        }

        String user_phone = phonenymber;
        task = new phpdo();
        txtview = (TextView) findViewById(R.id.textView2);
        task.execute(user_phone);
        if(chekCustmer)
        {
            Toast.makeText(getApplicationContext(),"고객은 존해 ",Toast.LENGTH_LONG).show();
            return true;
        }
        else {
            return false;
        }
    }

    /////데이터 체크 클레스(전화번호)
    public class phpdo extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            BufferedReader bufferedReader = null;
            try {
                String user_phone = arg0[0];
                String link = "http://darkraidmm.cafe24.com/getUserData.php?user_phone="+user_phone;
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {


                    chekCustmer=true;
                    sb.append(line);
                }

                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
        @Override
        protected void onPostExecute(String result) {
            constomer = result;
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(constomer);



            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray peoples = null;
            try {
                peoples = jsonObj.getJSONArray(TAG_RESULTS);



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }



    ////////대리운전 접수
    public void insertToDatabase(String companny_id,String call_type,
                                 String startpoin,String endpoint,String phonenymber,String price,
                                 String card_number,String card_date,String card_pass,String use_point){

        wait_Orders orders=new wait_Orders(companny_id,call_type,startpoin,endpoint,phonenymber,price,card_number,card_date,card_pass,use_point);



        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);


            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    String companny_id=(String)params[0];
                    String call_type=(String)params[1];
                    String startpoin=(String)params[2];
                    String endpoint=(String)params[3];
                    String phonenymber=(String)params[4];
                    String price=(String)params[5];
                    String card_number=(String)params[6];
                    String card_date=(String)params[7];
                    String card_pass=(String)params[8];
                    String use_point=(String)params[9];

                    String link="http://darkraidmm.cafe24.com/appinsert.php";

                    String data  = URLEncoder.encode("companny_id", "UTF-8") + "=" + URLEncoder.encode(companny_id, "UTF-8");
                    data += "&" + URLEncoder.encode("call_type", "UTF-8") + "=" + URLEncoder.encode(call_type, "UTF-8");
                    data += "&" + URLEncoder.encode("startpoin", "UTF-8") + "=" + URLEncoder.encode(startpoin, "UTF-8");
                    data += "&" + URLEncoder.encode("endpoint", "UTF-8") + "=" + URLEncoder.encode(endpoint, "UTF-8");
                    data += "&" + URLEncoder.encode("phonenymber", "UTF-8") + "=" + URLEncoder.encode(phonenymber, "UTF-8");
                    data += "&" + URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(price, "UTF-8");
                    data += "&" + URLEncoder.encode("card_number", "UTF-8") + "=" + URLEncoder.encode(card_number, "UTF-8");
                    data += "&" + URLEncoder.encode("card_date", "UTF-8") + "=" + URLEncoder.encode(card_date, "UTF-8");
                    data += "&" + URLEncoder.encode("card_pass", "UTF-8") + "=" + URLEncoder.encode(card_pass, "UTF-8");
                    data += "&" + URLEncoder.encode("use_point", "UTF-8") + "=" + URLEncoder.encode(use_point, "UTF-8");
                    ss=data.toString();



                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;


                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);

                        break;                    }

                    return sb.toString();

                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }



        InsertData task = new InsertData();
        task.execute(companny_id,call_type,startpoin,endpoint,phonenymber,price,card_number,card_date,card_pass,use_point);

        Intent intent = new Intent(this, Wait_Activity.class);
        intent.putExtra("Order",orders);
        startActivityForResult(intent, REQUEST_WAIT_ORDER);



    }
    void testsometing(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
}

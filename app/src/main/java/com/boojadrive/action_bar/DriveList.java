package com.boojadrive.action_bar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DriveList extends AppCompatActivity {

    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_DATE= "call_date";
    private static final String TAG_START = "startpoin";
    private static final String TAG_END ="endpoint";
    private static final String TAG_PRICE ="price";

    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> DriveList;

    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_list);
        list = (ListView) findViewById(R.id.Drive_list);
        DriveList = new ArrayList<HashMap<String,String>>();
        String phonenymber="";
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getLine1Number()!=null) {
            phonenymber=telephonyManager.getLine1Number().toString();
        }
        getData("http://darkraidmm.cafe24.com/getDriveList.php?phonenymber="+phonenymber);

    }
    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){

                StringBuilder stringBuilder=new StringBuilder("접수시간");
                JSONObject c = peoples.getJSONObject(i);


                stringBuilder.append(c.getString(TAG_DATE));
                stringBuilder.append("\n");
                stringBuilder.append(c.getString(TAG_START));
                stringBuilder.append("\n");
                stringBuilder.append(c.getString(TAG_END));
                stringBuilder.append("\n");
                stringBuilder.append(c.getString(TAG_PRICE));



                HashMap<String, String> drives = new HashMap<String, String>();

                drives.put(TAG_DATE, stringBuilder.toString());

                DriveList.add(drives);

            }

            ListAdapter adapter = new SimpleAdapter(
                    DriveList.this, DriveList, R.layout.drive_liset_item,
                    new String[]{TAG_DATE},
                    new int[]{R.id.drive_date}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(String url){
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

                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }
                    bufferedReader.close();

                    return sb.toString().trim();

                }catch(Exception e){
                    return new String("Exexption"+e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(String result){

                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }






}

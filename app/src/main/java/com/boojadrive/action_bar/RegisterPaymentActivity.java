package com.boojadrive.action_bar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegisterPaymentActivity extends AppCompatActivity {

    EditText creditedcard, mm, yy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_payment);
    }

    public void Reg_Card_clicked(View view) {


        String a, b, c;
        creditedcard = (EditText) this.findViewById(R.id.registerPayment_credit_card);
        mm = (EditText) this.findViewById(R.id.registerPayment_mm);
        yy = (EditText) this.findViewById(R.id.registerPayment_yy);
        a = creditedcard.getText().toString();
        b = mm.getText().toString();
        c = yy.getText().toString();

        if(!validateCard())
        {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle("카드정보오류");
            builder2.setMessage("카드정보오류 입니다. 확인해주세요 .");
            builder2.setPositiveButton("예",
                    new DialogInterface.OnClickListener()

                    {
                        public void onClick (DialogInterface dialog,int which){
                        }
                    });
            builder2.show();
        }
        else {

            String phonenymber="";
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
            if(telephonyManager.getLine1Number()!=null) {
                phonenymber=telephonyManager.getLine1Number().toString();
            }
            b+=c;

            upDateCard(phonenymber,a,b);




            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("카드등록");
            builder.setMessage("카드등록이 완료되었습니다. 다시접수해주세요.");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }
            );
            builder.show();
        }


    }
    public void upDateCard(String user_phone,String card_number ,String card_date){
        class InsertCData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterPaymentActivity.this, "Please " +
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
                    String card_number = (String) params[1];
                    String card_date = (String) params[2];


                    String link = "http://darkraidmm.cafe24.com/upDateCard.php?user_phone="+user_phone+"&card_number="+card_number+"&card_date="+card_date;


                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {


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
        task.execute(user_phone,card_number,card_date);
    }



    public boolean validateCard()
    {
        boolean valid = true;
        //TODO some fancy credit card validation
        if(creditedcard.getText().toString().trim().length()<15)
        {
            valid = false;
        }


        if (mm.getText().toString().trim().length()==0)
        {
            valid = false;
        }
        else if(Integer.parseInt(mm.getText().toString())>12 || Integer.parseInt(mm.getText().toString())<1)
        {
            valid = false;
        }

        if(yy.getText().toString().trim().length()<2)
        {
            valid = false;
        }

        return valid;
    }
}


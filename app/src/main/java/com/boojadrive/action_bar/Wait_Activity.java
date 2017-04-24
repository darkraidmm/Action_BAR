package com.boojadrive.action_bar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Wait_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_);


        wait_Orders wait_orders;

        Intent intent = getIntent();
        wait_orders=(wait_Orders)intent.getSerializableExtra("Order");

        TextView textView=(TextView)findViewById(R.id.wait_TextView);

        StringBuilder stringBuilder=new StringBuilder("접수시간 : ");

        String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

        stringBuilder.append(time);

        stringBuilder.append("\n출발지 : ");
        stringBuilder.append(wait_orders.startpoin);
        stringBuilder.append("\n목적지 : ");
        stringBuilder.append(wait_orders.endpoint);
        stringBuilder.append("\n결제 방식 : ");
        stringBuilder.append(wait_orders.call_type);
        stringBuilder.append("\n요금 : ");
        stringBuilder.append(wait_orders.price);

        textView.setTextSize(20);

        textView.setText(stringBuilder);
    }




}

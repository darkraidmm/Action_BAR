package com.boojadrive.action_bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by Administrator on 2017-04-12.
 */

public class Faivort_Button extends LinearLayout {
    LinearLayout bg2;

    ImageView symbol2;
    TextView textView2;
    ImageView symbol3;
    TextView textView3;

    public Faivort_Button(Context context) {

        super(context);
        initView();

    }

    public Faivort_Button(Context context, AttributeSet attrs2) {

        super(context, attrs2);

        initView();
        getAttrs(attrs2);

    }

    public Faivort_Button(Context context, AttributeSet attrs2, int defStyle) {

        super(context, attrs2);
        initView();
        getAttrs(attrs2, defStyle);
    }


    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.faivort_button, this, false);
        addView(v);

        bg2 = (LinearLayout) findViewById(R.id.bg22);
        symbol2 = (ImageView) findViewById(R.id.symbol22);
        textView2=(TextView)findViewById(R.id.textView22);
        symbol3 = (ImageView) findViewById(R.id.symbol33);
        textView3=(TextView)findViewById(R.id.textView33);
    }


    private void getAttrs(AttributeSet attrs2) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs2, R.styleable.LoginButton2);

        setTypeArray(typedArray);
    }


    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LoginButton2, defStyle, 0);
        setTypeArray(typedArray);

    }


    private void setTypeArray(TypedArray typedArray) {


        int symbol_resID = typedArray.getResourceId(R.styleable.LoginButton2_symbol22, R.mipmap.ic_directions_black_36dp);
        symbol2.setImageResource(symbol_resID);

        String text_string = typedArray.getString(R.styleable.LoginButton2_text22);
        textView2.setText(text_string);

        int symbol_resID2 = typedArray.getResourceId(R.styleable.LoginButton2_symbol33, R.mipmap.ic_directions_black_36dp);
        symbol3.setImageResource(symbol_resID2);

        String text_string2 = typedArray.getString(R.styleable.LoginButton2_text33);
        textView3.setText(text_string2);

        typedArray.recycle();

    }




}
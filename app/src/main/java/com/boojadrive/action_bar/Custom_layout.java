package com.boojadrive.action_bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017-04-12.
 */

public class Custom_layout extends LinearLayout {
    LinearLayout bg;
    ImageView symbol;
    Button button;
    Button button2;

    public Custom_layout(Context context) {

        super(context);
        initView();

    }

    public Custom_layout(Context context, AttributeSet attrs) {

        super(context, attrs);

        initView();
        getAttrs(attrs);

    }

    public Custom_layout(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }


    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.cusom_layout, this, false);
        addView(v);

        bg = (LinearLayout) findViewById(R.id.bg);
        symbol = (ImageView) findViewById(R.id.symbol);
        button=(Button)findViewById(R.id.button2);
        button2=(Button)findViewById((R.id.map_button2));

        button.setSingleLine(true);
        button.setEllipsize(TextUtils.TruncateAt.MARQUEE); //전광판 효과 흘러가는 효과
        button.setSelected(true);

        button.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                tost();


            }
        });
        button2.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){


            }
        });
    }
    public void tost(){

    }

    private void getAttrs(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LoginButton);

        setTypeArray(typedArray);
    }


    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LoginButton, defStyle, 0);
        setTypeArray(typedArray);

    }


    private void setTypeArray(TypedArray typedArray) {


        int symbol_resID = typedArray.getResourceId(R.styleable.LoginButton_symbol, R.mipmap.ic_directions_black_36dp);
        symbol.setImageResource(symbol_resID);

        String text_string = typedArray.getString(R.styleable.LoginButton_button2);
        button.setText(text_string);






        typedArray.recycle();

    }
    void setBg(int bg_resID) {

        bg.setBackgroundResource(bg_resID);
    }

    public void setSymbol(int symbol_resID) {
        symbol.setImageResource(symbol_resID);
    }

    public void setButton(String string)
    {
        button.setText(string);
    }
    public String getButtom(){
        return this.button.getText().toString();
    }

    public void setButton2(String string)
    {
        button2.setText(string);
    }

    public String getButtom2(){
        return this.button2.getText().toString();
    }





}

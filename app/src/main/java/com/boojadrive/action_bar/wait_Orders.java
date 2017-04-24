package com.boojadrive.action_bar;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-04-23.
 */

public class wait_Orders implements Serializable {
    String companny_id;
    String call_type;
    String startpoin;
    String endpoint;
    String phonenymber;
    String price;
    String card_number;
    String card_date;
    String card_pass;
    String use_point;

    public wait_Orders(){
        companny_id="";
        call_type="";
        startpoin="";
        endpoint="";
        phonenymber="";
        price="";
        card_number="";
        card_date="";
        card_pass="";
        use_point="";
    }
    public wait_Orders(String companny_id,String call_type,String startpoin,String endpoint,
                    String phonenymber,String price,String card_number,String card_date,String card_pass,String use_point){
        this.companny_id=companny_id;
        this.call_type=call_type;
        this.startpoin=startpoin;
        this. endpoint=endpoint;
        this. phonenymber=phonenymber;
        this.price=price;
        this. card_number=card_number;
        this. card_date=card_date;
        this. card_pass=card_pass;
        this.use_point=use_point;
    }
    public String toString(){
        return     "("+ companny_id+","+call_type+","+startpoin+","+endpoint+","+phonenymber+
                ","+price+","+card_number+","+card_date+","+card_pass+","+use_point+")";
    }

}

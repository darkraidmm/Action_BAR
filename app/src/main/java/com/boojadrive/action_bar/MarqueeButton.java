package com.boojadrive.action_bar;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;


/**
 * Created by Administrator on 2017-04-14.
 */

public class MarqueeButton extends android.support.v7.widget.AppCompatButton {

    public MarqueeButton(Context context) {
        super(context);
    }
    public MarqueeButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect
            previouslyFocusedRect) {
        if(focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if(focused)
            super.onWindowFocusChanged(focused);
    }
    @Override
    public boolean isFocused() {
        return true;
    }

}

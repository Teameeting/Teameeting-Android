package org.dync.teameeting.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * Created by zhangqilu on 2016/3/2.
 */
public class VitualKey extends RelativeLayout {

    private LayoutKeyChange mLayoutKeyChange ;

    public VitualKey(Context context) {

        super(context);


    }

    public VitualKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VitualKey(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //Log.e("onLayout", "onLayout: b "+b);
        if(changed)
            mLayoutKeyChange.onLayoutKeyChange(b);
    }


    public void setInterface(LayoutKeyChange layoutKeyChange){
        mLayoutKeyChange = layoutKeyChange;
    }


    public interface LayoutKeyChange{
        public void onLayoutKeyChange(int b);
    }

}

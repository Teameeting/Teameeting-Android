package org.dync.teameeting.widgets;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by H_lang on 2016/1/11.
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    public MySwipeRefreshLayout(Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int downY;
    private int downX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getY();
                downX = (int) event.getX();
                return super.onInterceptTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (event.getY() - downY);
                int deltaX = (int) (event.getX() - downX);
                if (deltaY - Math.abs(deltaX) > 10)
                    return super.onInterceptTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }


}

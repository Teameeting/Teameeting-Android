package org.dync.teameeting.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Xiao_Bailong on 2016/1/21.
 */
public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                   int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        Log.e("MyListView", "overScrollBy: deltaY" +deltaY);
        Log.e("MyListView", "overScrollBy: scrollY" +scrollY);
        Log.e("MyListView", "overScrollBy: scrollRangeY" +scrollRangeY);
        Log.e("MyListView", "overScrollBy: maxOverScrollY" +maxOverScrollY);
        Log.e("MyListView", "-----------------------------------------" );

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, 100, isTouchEvent);
    }
}

/**
 * CommonAdapter.java [V 1.0.0]
 * classes:com.example.menu.adapter.CommonAdapter
 * Zlang Create at 2015-12-1.上午10:15:47
 */
package org.dync.teameeting.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.dync.teameeting.bean.MeetingListEntity;

import java.util.List;

/**
 * com.example.menu.adapter.CommonAdapter
 *
 * @param <T>
 * @author ZLang <br/>
 *         <p/>
 *         create at 2015-12-1 上午10:15:47 <br/>
 *         versions 1.0 viersion 2.0
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected final Context mContext;
    protected final List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int itemLayoutId;
    protected Resources mResources;


    public CommonAdapter(Context context, List<T> mDatas) {
        this(context, mDatas, 0);
    }

    /**
     * @param context
     * @param mDatas
     * @param itemLayoutId
     */
    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = mDatas;
        this.itemLayoutId = itemLayoutId;
        mResources = context.getResources();
    }

    public void commaMapAdapter(Context context) {

    }


    public int getMeetingIdPosition(String meetingId) {
        for (int i = 0; i < mDatas.size(); i++) {
            MeetingListEntity meetingListEntity = (MeetingListEntity) mDatas.get(i);
            if (meetingId.equals(meetingListEntity.getMeetingid())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

}

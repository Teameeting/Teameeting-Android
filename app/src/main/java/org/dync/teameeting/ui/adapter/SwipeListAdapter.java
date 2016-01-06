/**
 * SwipeListAdapter.java [V 1.0.0]
 * classes:com.example.menu.swipe.SwipeListAdapter
 * Zlang Create at 2015-12-1.上午9:57:09
 */
package org.dync.teameeting.ui.adapter;

import java.util.HashSet;
import java.util.List;

import org.dync.teameeting.R;
import org.dync.teameeting.bean.MeetingList;
import org.dync.teameeting.widgets.swipe.FrontLayout;
import org.dync.teameeting.widgets.swipe.SwipeLayout;
import org.dync.teameeting.widgets.swipe.SwipeLayout.SwipeListener;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * @author ZLang <br/>
 *         create at 2015-12-1 上午9:57:09 <br/>
 */
public class SwipeListAdapter extends CommonAdapter<MeetingList.MeetingListEntity>
{

    private SwipeListOnClick mSwipeListOnClick;
    HashSet<SwipeLayout> mUnClosedLayouts = new HashSet<SwipeLayout>();
    private Context mContext;
    private InputMethodManager mIMM;

    public SwipeListAdapter(Context context, List<MeetingList.MeetingListEntity> data,
                            SwipeListOnClick mswipeListOnClick)
    {
        super(context, data);
        mContext = context;
        mSwipeListOnClick = mswipeListOnClick;
    }

    public interface SwipeListOnClick
    {
        public void onItemClickListener(View v, int position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        MeetingList.MeetingListEntity meetingListEntity = mDatas.get(position);
        ViewHolder mHolder;
        if (convertView != null)
        {
            mHolder = (ViewHolder) convertView.getTag();
        } else
        {
            convertView = (SwipeLayout) mInflater.inflate(
                    R.layout.item_room_adapter, null);
            mHolder = ViewHolder.fromValues(convertView);
            convertView.setTag(mHolder);
        }

        SwipeLayout swipeLayout = (SwipeLayout) convertView;
        swipeLayout.close(false, false);
        swipeLayout.setSwipeListener(mSwipeListener);

        setData(meetingListEntity, mHolder);

        showEidt(position, mHolder);

        setHolderListener(position, mHolder);
        return swipeLayout;
    }

    private void setData(MeetingList.MeetingListEntity meetingListEntity, ViewHolder mHolder)
    {
        mHolder.mRoomName.setText("" + meetingListEntity.getMeetname());
        mHolder.mRoomTime.setText("创建: " + meetingListEntity.getJointime());
        mHolder.mRoomPeopleCount.setText("" + meetingListEntity.getMemnumber());

        if (meetingListEntity.getMemnumber() > 0)
        {
            mHolder.mNotificationsClose.setVisibility(View.GONE);
            mHolder.mPeopleico.setVisibility(View.VISIBLE);
            mHolder.mRoomPeopleCount.setVisibility(View.VISIBLE);
            mHolder.mRoomPeopleCount.setText(meetingListEntity.getMemnumber());
        }
        if (meetingListEntity.getMemnumber() <= 0)
        {
            mHolder.mPeopleico.setVisibility(View.GONE);
            mHolder.mRoomPeopleCount.setVisibility(View.GONE);
            if (meetingListEntity.getPushable() == 0)
            {
                mHolder.mNotificationsClose.setVisibility(View.VISIBLE);
            } else
            {
                mHolder.mNotificationsClose.setVisibility(View.GONE);
            }

        }
        if (!meetingListEntity.isApplyTyep())
        {
            mHolder.mPbCreeat.setVisibility(View.VISIBLE);
            mHolder.mMoreSetting.setVisibility(View.GONE);
        }else
        {
            mHolder.mPbCreeat.setVisibility(View.GONE);
            mHolder.mMoreSetting.setVisibility(View.VISIBLE);
        }
    }

    private void showEidt(int position, ViewHolder mHolder)
    {
        if (mDatas.get(position).getmMeetType2() == 2)
        {
            mHolder.mReName.setVisibility(View.VISIBLE);
            mHolder.mRLShowView.setVisibility(View.GONE);
            String name = mDatas.get(position).getMeetname();
            mHolder.mReName.setText(name);
            mHolder.mReName.setTextColor(mContext.getResources().getColor(
                    R.color.white));
            mHolder.mReName.setSelection(name.length());
            mHolder.mReName.setFocusable(true);
            mHolder.mReName.setFocusableInTouchMode(true);
            mHolder.mReName.requestFocus();
            mIMM = (InputMethodManager) mContext
                    .getSystemService(mContext.INPUT_METHOD_SERVICE);
            mIMM.showSoftInput(mHolder.mReName, 0);
        } else
        {
            mHolder.mReName.setVisibility(View.GONE);
            mHolder.mRLShowView.setVisibility(View.VISIBLE);
        }
    }

    private void setHolderListener(int position, ViewHolder mHolder)
    {
        mHolder.mItemLayout.setTag(position);
        mHolder.mItemLayout.setOnClickListener(onActionClick);
        mHolder.mRoomDel.setTag(position);
        mHolder.mRoomDel.setOnClickListener(onActionClick);
        mHolder.mRoomDel.setTag(position);
        mHolder.mMoreSetting.setOnClickListener(onActionClick);
        mHolder.mMoreSetting.setTag(position);
        mHolder.mReName.setTag(position);
        mHolder.mReName.setOnEditorActionListener(onEditorListener);
    }

    /**
     *
     */
    OnEditorActionListener onEditorListener = new OnEditorActionListener()
    {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            // TODO Auto-generated method stub
            Integer postion = (Integer) v.getTag();
            mSwipeListOnClick.onItemClickListener(v, postion);
            return false;
        }
    };

    /**
     * Click callback
     */
    OnClickListener onActionClick = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Integer postion = (Integer) v.getTag();
            mSwipeListOnClick.onItemClickListener(v, postion);

        }
    };

    /**
     * slide listener
     */
    SwipeListener mSwipeListener = new SwipeListener()
    {
        @Override
        public void onOpen(SwipeLayout swipeLayout)
        {
            // Utils.showToast(context, "onOpen");
            mUnClosedLayouts.add(swipeLayout);
        }

        @Override
        public void onClose(SwipeLayout swipeLayout)
        {
            // Utils.showToast(context, "onClose");
            mUnClosedLayouts.remove(swipeLayout);
        }

        @Override
        public void onStartClose(SwipeLayout swipeLayout)
        {
            // Utils.showToast(context, "onStartClose");
        }

        @Override
        public void onStartOpen(SwipeLayout swipeLayout)
        {
            // Utils.showToast(mContext, "onStartOpen");
            closeAllLayout();
            mUnClosedLayouts.add(swipeLayout);
        }

    };


    public void closeAllLayout()
    {
        if (mUnClosedLayouts.size() == 0)
            return;

        for (SwipeLayout l : mUnClosedLayouts)
        {
            l.close(true, false);
        }
        mUnClosedLayouts.clear();
    }

    /**
     * org.dync.teammeeting.adapter.ViewHolder <br/>
     * View Holder
     *
     * @author ZLang <br/>
     *         create at 2015-12-1 下午12:42:48
     */
    private static class ViewHolder
    {

        EditText mReName;
        RelativeLayout mRLShowView;

        TextView mRoomName;
        TextView mRoomTime;
        TextView mRoomPeopleCount;

        ImageButton mMoreSetting;
        ImageView mNotificationsClose;
        ImageView mPeopleico;
        ImageButton mRoomDel;
        FrontLayout mItemLayout;
        ProgressBar mPbCreeat;


        private ViewHolder(EditText reName, RelativeLayout rlShowView,
                           TextView roomName, TextView roomTime, TextView roomPeopleCount,
                           ImageButton moreSetting, ImageButton roomDel, ImageView mNotificationsClose, ImageView mPeopleico,
                           FrontLayout itemLayout,ProgressBar pbCreeat)
        {
            super();
            this.mReName = reName;
            this.mRLShowView = rlShowView;
            this.mRoomName = roomName;
            this.mRoomTime = roomTime;
            this.mRoomPeopleCount = roomPeopleCount;
            this.mMoreSetting = moreSetting;
            this.mRoomDel = roomDel;
            this.mItemLayout = itemLayout;
            this.mNotificationsClose = mNotificationsClose;
            this.mPeopleico = mPeopleico;
            this.mPbCreeat = pbCreeat;
        }

        public static ViewHolder fromValues(View view)
        {
            return new ViewHolder((EditText) view.findViewById(R.id.et_rename),
                    (RelativeLayout) view.findViewById(R.id.rl_show_view),
                    (TextView) view.findViewById(R.id.tv_room_name),
                    (TextView) view.findViewById(R.id.tv_room_time),
                    (TextView) view.findViewById(R.id.tv_people_count),
                    (ImageButton) view.findViewById(R.id.imgbtn_more_setting),
                    (ImageButton) view.findViewById(R.id.btn_delete),
                    (ImageView) view.findViewById(R.id.iv_notifications_close),
                    (ImageView) view.findViewById(R.id.iv_people),
                    (FrontLayout) view.findViewById(R.id.fl_front),
                    (ProgressBar)view.findViewById(R.id.pb_creat));
        }
    }

}

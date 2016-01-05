package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.gson.Gson;

import org.dync.teameeting.R;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientType;
import org.dync.teameeting.structs.Intent_KEY;
import org.dync.teameeting.ui.adapter.SwipeListAdapter;
import org.dync.teameeting.ui.adapter.SwipeListAdapter.SwipeListOnClick;
import org.dync.teameeting.http.NetWork;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingList;
import org.dync.teameeting.ui.helper.DialogHelper;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.ExtraType;
import org.dync.teameeting.structs.NetType;
import org.dync.teameeting.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

public class MainActivity extends BaseActivity
{
    public final static int UPDATE_COPY_LINK = 0X01;
    public final static int UPDATE_RENAME_SHOW = 0X02;
    public final static int UPDATE_LISTVIEW_SCROLL = 0X03;
    public final static int UPDATE_RENAME_END = 0X04;
    public final static int SHOW_EDIT_TEXT_TIME = 2000;

    private final static String TAG = "MainActivity";
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private RelativeLayout mRlMain;
    private ListView mListView;
    private TextView mRoomCancel;
    private Button mGetRoom;
    private EditText mCreateRoom;
    private SwipeListAdapter mAdapter;
    public SweetAlertDialog mNetErrorSweetAlertDialog;
    private ImageButton mJoinMeeting;

    private Context mContext;
    private List<MeetingList.MeetingListEntity> mRoomMeetingList = new ArrayList<MeetingList.MeetingListEntity>();
    private InputMethodManager mIMM;
    private long mExitTime = 0;
    private Boolean mCreateRoomFlag = false;
    private Boolean mReNameFlag = false;

    private boolean mSoftInputFlag = false;
    private int mDy;
    private int mPosition;
    private String mShareUrl = "没有设置连接";
    private String mPass="123456";
    private String mUserId = TeamMeetingApp.getTeamMeetingApp().getDevId();

    private Handler mUIHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UPDATE_COPY_LINK:
                    break;
                case UPDATE_RENAME_SHOW:
                    int position = msg.getData().getInt("position");
                    mRoomMeetingList.get(position).setmMeetType2(2);
                    if (mDebug)
                    {
                        Log.e(TAG, "handleMessage:position " + position);
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mDebug)
                        Log.e(TAG, "UPDATE_RENAME_SHOW");
                    break;
                case UPDATE_LISTVIEW_SCROLL:
                    mAdapter.notifyDataSetChanged();
                    mListView.animate().translationY(-mDy)
                            .setDuration(SHOW_EDIT_TEXT_TIME);
                    break;
                case UPDATE_RENAME_END:
                    mAdapter.notifyDataSetChanged();
                    if (mDy == 0)
                        mListView.smoothScrollToPositionFromTop(0, 0, 1000);
                    else
                    {
                        mListView.animate().translationYBy(mDy).setDuration(10);
                        mListView.smoothScrollToPositionFromTop(0, 0, 500);
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        initdata();
        inintLayout();
        if (mDebug)
        {
            Log.e(TAG, "onCreate: MainActivity");
        }
    }

    private void initdata()
    {
        Intent intent = getIntent();
        String meetingListStr = intent.getExtras().getString(NetWork.MEETING_LIST);
        upDataMeetingList(meetingListStr);
    }

    /**
     * inintLayout
     */
    private void inintLayout()
    {

        mIMM = (InputMethodManager) MainActivity.this
                .getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        createNetErroDilaog();
        mRlMain = (RelativeLayout) findViewById(R.id.rl_main);
        mCreateRoom = (EditText) findViewById(R.id.et_create_room);
        mRoomCancel = (TextView) findViewById(R.id.tv_cancel_create_room);
        mListView = (ListView) findViewById(R.id.lv_listView);
        mListView.setEmptyView(findViewById(R.id.empty_layout));
        mGetRoom = (Button) findViewById(R.id.btn_get_room);
        mJoinMeeting =(ImageButton) findViewById(R.id.ibtn_join_meeting);
        mGetRoom.setOnClickListener(mOnClickListener);
        mRoomCancel.setOnClickListener(mOnClickListener);
        mJoinMeeting.setOnClickListener(mOnClickListener);


        mAdapter = new SwipeListAdapter(mContext, mRoomMeetingList, mSwipeListOnClick);
        mListView.setAdapter(mAdapter);

        mRlMain.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {

                        if (isKeyboardShown(mRlMain.getRootView()))
                        {
                            if (mDebug)
                                Log.e(TAG, "isKeyboardShown open keyboard");
                            mSoftInputFlag = true;
                        } else
                        {
                            if (mDebug)
                                Log.e(TAG, "isKeyboardShown close keyboard");
                            if (mReNameFlag && mSoftInputFlag)
                            {
                                mUIHandler.sendEmptyMessageDelayed(
                                        UPDATE_RENAME_END, 500);
                                mReNameFlag = false;
                                mRoomMeetingList.get(mPosition).setmMeetType2(1);
                            }

                            if (mSoftInputFlag)
                            {
                                mCreateRoom.setVisibility(View.GONE);
                                mRoomCancel.setVisibility(View.GONE);
                                mSoftInputFlag = false;
                            }

                        }

                    }
                });
    }

    /**
     * isKeyboardShown
     *
     * @param rootView
     * @return true soft keyboard is open false soft keyboard is open
     */

    private boolean isKeyboardShown(View rootView)
    {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    /**
     * listViewSetScroll
     *
     * @param position
     */

    private void listViewSetScroll(int position)
    {

        int itemHeight = getItemHeight(mListView);
        float temp = mListView.getHeight() / (float) getItemHeight(mListView);
        int maxItemTop = 0;

        int visibleItem = (int) Math.ceil(temp);

        if (mAdapter.getCount() < visibleItem)
        {
            mDy = itemHeight * position;
            mUIHandler.sendEmptyMessageDelayed(UPDATE_LISTVIEW_SCROLL, 100);
            return;
        } else
        {
            maxItemTop = mAdapter.getCount() - visibleItem;
        }
        if (position <= maxItemTop)
        {
            mDy = 0;
            mListView.smoothScrollToPositionFromTop(position, 0, 2000);
        } else
        {
            int incompleteItemheight = mListView.getHeight()
                    - (visibleItem - 1) * itemHeight;

            mDy = itemHeight * (position - maxItemTop - 1)
                    + incompleteItemheight;
            Log.e(TAG, "maxItemTop " + maxItemTop + " incompleteItemheight "
                    + incompleteItemheight);
            // mListView.smoothScrollToPositionFromTop(maxItemTop, 0, 1000);
            // mListView.smoothScrollToPosition(maxItemTop-1);
            mListView.setSelection(mListView.getBottom());
            // mListView.animate().translationY(-mDy).setDuration(2000);
            mUIHandler.sendEmptyMessageDelayed(UPDATE_LISTVIEW_SCROLL, 1500);

        }

    }


    /**
     * getItemHeight
     *
     * @param listView
     * @return
     */
    private int getItemHeight(final ListView listView)
    {
        View view = mAdapter.getView(0, null, listView);

        view.measure(0, 0);
        int i = (int) ScreenUtils.dip2Dimension(10.0f, this);
        Log.e(TAG, " i " + i);
        return view.getMeasuredHeight();
    }

    /**
     * hideKeyboard
     *
     * @return
     */
    private boolean hideKeyboard()
    {
        if (mIMM.isActive(mCreateRoom))
        {
            mIMM.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            mIMM.restartInput(mCreateRoom);

            // mIMM.hideSoftInputFromWindow(mCreateRoom.getWindowToken(), 0);
            mCreateRoom.setVisibility(View.GONE);
            mRoomCancel.setVisibility(View.GONE);
            mGetRoom.setVisibility(View.VISIBLE);

            return true;
        }

        return false;
    }

    /**
     * OnClickListener
     */
    private OnClickListener mOnClickListener = new OnClickListener()
    {

        @Override
        public void onClick(View view)
        {
            // TODO Auto-generated method stub
            switch (view.getId())
            {
                case R.id.tv_cancel_create_room:
                    mIMM.hideSoftInputFromWindow(mCreateRoom.getWindowToken(), 0);

                    mCreateRoom.setVisibility(View.GONE);
                    mRoomCancel.setVisibility(View.GONE);
                    mGetRoom.setVisibility(View.VISIBLE);
                    break;

                case R.id.btn_get_room:

                    mRoomCancel.setVisibility(View.VISIBLE);
                    mCreateRoom.setVisibility(view.VISIBLE);
                    mCreateRoom.setText("");

                    mCreateRoom.setFocusable(true);
                    mCreateRoom.setFocusableInTouchMode(true);
                    mCreateRoom.requestFocus();
                    mIMM.showSoftInput(mCreateRoom, 0);
                    mCreateRoom.setOnEditorActionListener(editorActionListener);

                    break;
                case R.id.ibtn_join_meeting:
                    Intent intent = new Intent(mContext,JoinMeetingActivity.class);
                    startActivity(intent);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * SwipeListOnClick
     */
    private SwipeListOnClick mSwipeListOnClick = new SwipeListOnClick()
    {

        @Override
        public void onItemClickListener(View v, int position)
        {
            int code;
            String meetingId;
            String meetingName;

            if (hideKeyboard())
            {
                return;
            }

            Intent intent;
            switch (v.getId())
            {
                case R.id.fl_front:
                    meetingName = mRoomMeetingList.get(position).getMeetname();
                    meetingId = mRoomMeetingList.get(position).getMeetingid();
                   // mUserId = mRoomMeetingList.get(position).getMeetinguserid();
                    if (mDebug)
                    {
                        Log.i(TAG, "meetingId-fl_front" + meetingId);
                    }
                     code = StartFlashActivity.mMsgSender.TMOptRoom(JMClientType.TMCMD_ENTER,mUserId, mPass, meetingId,"");
                    if(code==0){
                        if(mDebug){
                            Log.e(TAG, "onItemClickListener: "+"TMEnterRoom Successed");
                        }
                    }else if(mDebug){
                        Log.e(TAG, "onItemClickListener: "+"TMEnterRoom Failed");
                    }

                    // 推送接口
                    // 跳转
                    intent = new Intent(mContext, MeetingActivity.class);
                    intent.putExtra("meetingName", meetingName);
                    intent.putExtra("meetingId", meetingId);
                    intent.putExtra("userId", mUserId);
                    mContext.startActivity(intent);

                    break;

                case R.id.btn_delete:
                    mSign = getSign();
                    meetingId = mRoomMeetingList.get(position).getMeetingid();
                    //mUserId = mRoomMeetingList.get(position).getMeetinguserid();
                    mNetWork.deleteRoom(mSign, meetingId);
                    mRoomMeetingList.remove(position);
                    mAdapter.notifyDataSetChanged();
                    code = StartFlashActivity.mMsgSender.TMOptRoom(JMClientType.TMCMD_DESTROY,mUserId, mPass, meetingId,"");
                    if(code==0){
                        if(mDebug){
                            Log.e(TAG, "onItemClickListener: "+"TMDestroyRoom Successed");
                        }
                    }else if(mDebug){
                        Log.e(TAG, "onItemClickListener: "+"TMDestroyRoom Failed");
                    }
                    break;

                case R.id.imgbtn_more_setting:
                    moreSetting(position);
                    break;
                case R.id.et_rename:

                    EditText reName = (EditText) v.findViewById(R.id.et_rename);
                    String newName = reName.getText().toString();
                    String oldName = mRoomMeetingList.get(position).getMeetname();

                    if (!newName.equals(oldName))
                    {

                        mSign = getSign();
                        meetingId = mRoomMeetingList.get(position).getMeetingid();
                        mNetWork.updateMeetRoomName(mSign, meetingId, newName);
                        mRoomMeetingList.get(position).setMeetname(newName);
                    }

                    mRoomMeetingList.get(position).setmMeetType2(1);
                    mIMM.hideSoftInputFromWindow(reName.getWindowToken(), 0);
                    mUIHandler.sendEmptyMessageDelayed(UPDATE_RENAME_END, 500);
                    mReNameFlag = false;
                    break;

                default:
                    break;
            }

        }
    };


    /**
     * soft keyboard Listener
     */
    OnEditorActionListener editorActionListener = new OnEditorActionListener()
    {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {

            String roomName = mCreateRoom.getText().toString();

            if (roomName.length() == 0 || roomName == null)
            {
                roomName = "Untitled room";
            }
            mSign = getSign();
            if (mDebug)
                if (mDebug)
                    Log.e(TAG, "onEditorAction: roomName" + roomName + mSign);

            mIMM.hideSoftInputFromWindow(mCreateRoom.getWindowToken(), 0);
            mCreateRoom.setVisibility(View.GONE);
            mRoomCancel.setVisibility(View.GONE);
            String pushable = "1";
            String meetdesc = "";//会议描述
            String meetenablde = "1";//是否可用或者私密

            mNetWork.applyRoom(mSign, roomName, "0", "", meetenablde, pushable);
            mAdapter.notifyDataSetChanged();
            mCreateRoomFlag = true;

            return false;
        }
    };

    /**
     * moreSetting
     * @param position
     */
    private void moreSetting(int position)
    {
        Intent intent;
        intent = new Intent(mContext, RoomSettingActivity.class);
        MeetingList.MeetingListEntity meetingEntity = mRoomMeetingList.get(position);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Intent_KEY.MEETING_ENTY, meetingEntity);
        intent.putExtras(mBundle);
        intent.putExtra(Intent_KEY.POSITION, position);
        startActivityForResult(intent, ExtraType.REQUEST_CODE_ROOM_SETTING);
        ((Activity) mContext).overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if (hideKeyboard())
            {
                return false;
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000)
            {
                Toast.makeText(this, R.string.exit_once_more, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else
            {
                mSign = getSign();
                mNetWork.signOut(mSign);
                String userid = TeamMeetingApp.getTeamMeetingApp().getDevId();
                StartFlashActivity.mMsgSender.TMLogout(userid, mPass);
                if (mDebug)
                    Log.e(TAG, "Exit-signOut");
                this.finish();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode)
        {
            case ExtraType.RESULT_CODE_ROOM_SETTING_MESSAGE_INVITE:
                break;
            case ExtraType.RESULT_CODE_ROOM_SETTING_WEIXIN_INVITE:
                break;
            case ExtraType.RESULT_CODE_ROOM_SETTING_COPY_LINK:
                DialogHelper.onClickCopy(MainActivity.this, mShareUrl);
                break;
            case ExtraType.RESULT_CODE_ROOM_SETTING_NOTIFICATION:
                break;
            case ExtraType.RESULT_CODE_ROOM_SETTING_RENAME:
                settingReName(data);
                break;
            case ExtraType.RESULT_CODE_ROOM_SETTING_DELETE:
                seetingDeleteRoom(data);
            case ExtraType.RESULT_CODE_ROOM_SETTING_CLOSE:
                if (mDebug)
                    Log.e(TAG, "onActivityResult: 关闭");
                getListNetWork();
                break;
            default:
                break;
        }

    }


    private void settingReName(Intent data)
    {
        String meetingId;
        mReNameFlag = true;
        mSign = TeamMeetingApp.getmSelfData().getAuthorization();
        int position = data.getIntExtra("position", 0);
        mPosition = position;
        meetingId = data.getStringExtra("meetingId");
        String meetingName = data.getStringExtra("meetingName");
        listViewSetScroll(position);
        Message msg = new Message();
        msg.what = UPDATE_RENAME_SHOW;
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        msg.setData(bundle);
        mUIHandler.sendMessageDelayed(msg, SHOW_EDIT_TEXT_TIME);
    }

    private void seetingDeleteRoom(Intent data)
    {
        mSign = getSign();
        int position = data.getIntExtra("position", 0);
        String meetingId = data.getStringExtra("meetingId");
        mNetWork.deleteRoom(mSign, meetingId);
        mRoomMeetingList.remove(position);
        mAdapter.notifyDataSetChanged();

        String userId = mRoomMeetingList.get(position).getMeetinguserid();
        int code = StartFlashActivity.mMsgSender.TMOptRoom(JMClientType.TMCMD_DESTROY,mUserId, mPass, meetingId,"");
        if(code==0){
            if(mDebug){
                Log.e(TAG, "onItemClickListener: "+"TMDestroyRoom Successed");
            }
        }else if(mDebug){
            Log.e(TAG, "onItemClickListener: "+"TMDestroyRoom Failed");
        }

    }




    private void getListNetWork()
    {
        mNetWork.getRoomLists(mSign, 1 + "", 20 + "");
    }

    private void getRoomListSuccess(Message msg)
    {
        Bundle bundle = msg.getData();
        String meetingListStr = bundle.getString(NetWork.MEETING_LIST);
        upDataMeetingList(meetingListStr);

        mAdapter.notifyDataSetChanged();
        startInvitePeopleActivity();
    }

    private void startInvitePeopleActivity()
    {
        if (mCreateRoomFlag)
        {
            Intent intent = new Intent(MainActivity.this,
                    InvitePeopleActivity.class);
            intent.putExtra("roomUrl", "www.baidu.com");
            startActivityForResult(intent, ExtraType.RESULT_CODE_ROOM_SETTING_COPY_LINK);
            overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
            mCreateRoomFlag = false;
        }
    }

    private void upDataMeetingList(String meetingListStr)
    {
        if (mDebug)
            Log.e(TAG, "upDataMeetingList: " + meetingListStr);
        if (meetingListStr != null)
        {
            mSign = getSign();
            Gson gson = new Gson();
            MeetingList meetingList = gson.fromJson(meetingListStr, MeetingList.class);
            mRoomMeetingList.clear();
            mRoomMeetingList.addAll(meetingList.getMeetingList());
        }
    }


    public void netWorkTypeStart(int type)
    {
        if (type == NetType.TYPE_NULL.ordinal())
        {
            mNetErrorSweetAlertDialog.show();
        } else
        {
            mSign = getSign();
            Log.e(TAG, "netWorkTypeStart: mSign" + mSign);
            getListNetWork();
        }
    }

    void createNetErroDilaog()
    {
        mNetErrorSweetAlertDialog = new SweetAlertDialog(this,
                SweetAlertDialog.ERROR_TYPE).setTitleText("网络已断开...")
                .setConfirmText("ok").setContentText("请连接网络!")
                .setConfirmClickListener(sweetClickListener);
    }

    OnSweetClickListener sweetClickListener = new OnSweetClickListener()
    {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog)
        {
            sweetAlertDialog.dismiss();
            // 设置是否一只提示 没有网络状态
            // mNetWork.getRoomList(mSign, 1 + "", 20 + "");
        }
    };




    /**
     * For EventBus callback.
     */
    public void onEventMainThread(Message msg)
    {
        switch (EventType.values()[msg.what])
        {
            case MSG_SIGNOUT_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_SIGNOUT_SUCCESS");
                finish();
                System.exit(0);
                break;
            case MSG_SIGNOUT_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_SIGNOUT_FAILED");
                break;
            case MSG_MESSAGE_LOGOUT_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_MESSAGE_LOGOUT_SUCCESS");
                break;
            case MSG_MESSAGE_LOGOUT_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_MESSAGE_LOGOUT_FAILED");
                break;
            case MSG_GET_ROOM_LIST_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_ROOM_LIST_SUCCESS");
                // 创建房间
                getRoomListSuccess(msg);
                break;
            case MSG_GET_ROOM_LIST_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_ROOM_LIST_FAILED");
                break;
            case MSG_APPLY_ROOM_SUCCESS:
                String meetingId = msg.getData().getString("meetingId");
                if (mDebug)
                    Log.e(TAG, "MSG_APPLY_ROOM_SUCCESS "+meetingId);
                String userid = TeamMeetingApp.getTeamMeetingApp().getDevId();
                int code  = StartFlashActivity.mMsgSender.TMOptRoom(JMClientType.TMCMD_CREATE,userid,mPass,meetingId,"");
                if(code==0){
                    if (mDebug)
                        Log.e(TAG, "TMCreateRoom "+"Successed");
                }
                else {
                    if (mDebug)
                        Log.e(TAG, "TMCreateRoom "+"Failed");
                }
                getListNetWork();
                break;
            case MSG_APPLY_ROOMT_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_APPLY_ROOMT_FAILED");
                break;
            case MSG_UPDATE_MEET_ROOM_NAME_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_UPDATE_MEET_ROOM_NAME_SUCCESS");
                break;
            case MSG_UPDATE_MEET_ROOM_NAME_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_UPDATE_MEET_ROOM_NAME_FAILED");
                break;
            case MSG_NET_WORK_TYPE:
                if (mDebug)
                    Log.e(TAG, "MSG_NET_WORK_TYPE");
                int type = msg.getData().getInt("net_type");
                netWorkTypeStart(type);
                break;
            case MSG_RESPONS_ESTR_NULl:
                if (mDebug)
                    Log.e(TAG, "MSG_RESPONS_ESTR_NULl");
                mNetErrorSweetAlertDialog.show();
                break;
            default:
                break;
        }
    }

}

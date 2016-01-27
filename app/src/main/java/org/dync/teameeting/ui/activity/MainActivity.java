package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.orhanobut.logger.Logger;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingListEntity;
import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.db.CRUDChat;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientType;
import org.dync.teameeting.sdkmsgclientandroid.msgs.TMMsgSender;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.ExtraType;
import org.dync.teameeting.structs.Intent_KEY;
import org.dync.teameeting.structs.JoinActType;
import org.dync.teameeting.structs.NetType;
import org.dync.teameeting.ui.PushSetActivity;
import org.dync.teameeting.ui.adapter.SwipeListAdapter;
import org.dync.teameeting.ui.adapter.SwipeListAdapter.SwipeListOnClick;
import org.dync.teameeting.ui.helper.DialogHelper;
import org.dync.teameeting.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

public class MainActivity extends BaseActivity {
    public final static int UPDATE_COPY_LINK = 0X01;
    public final static int UPDATE_RENAME_SHOW = 0X02;
    public final static int UPDATE_LISTVIEW_SCROLL = 0X03;
    public final static int UPDATE_RENAME_END = 0X04;
    public final static int SHOW_EDIT_TEXT_TIME = 1000;

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
    private List<MeetingListEntity> mRoomMeetingList = new ArrayList<MeetingListEntity>();
    private InputMethodManager mIMM;
    private long mExitTime = 0;
    private boolean mCreateRoomFlag = false;
    private boolean mReNameFlag = false;
    private boolean mUrlInsertMeegting = false;

    private boolean mSoftInputFlag = false;
    private int mDy;
    private int mPosition;
    private String mShareUrl = "empty Url";
    private final String mPass = getSign();
    private String mUserId = TeamMeetingApp.getTeamMeetingApp().getDevId();
    private TMMsgSender mMsgSender;

    private String mUrlMeetingId;
    private String mUrlMeetingName;

    private Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_COPY_LINK:
                    break;
                case UPDATE_RENAME_SHOW:
                    int position = msg.getData().getInt("position");
                    mRoomMeetingList.get(position).setmMeetType2(2);
                    if (mDebug) {
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
                    else {
                        mListView.animate().translationYBy(mDy).setDuration(10);
                        mListView.smoothScrollToPositionFromTop(0, 0, 500);
                    }
                    break;

                default:
                    break;
            }
        }
    };
    private SweetAlertDialog mWarningCancel;
    private SweetAlertDialog progressDiloag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        initdata();
        inintLayout();
        if (mDebug) {
            Log.e(TAG, "onCreate: " + TeamMeetingApp.getmSelfData().getMeetingLists().toString());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //  getListNetWork();
    }

    private void initdata() {

        mWarningCancel = DialogHelper.createWarningCancel(mContext);
        upDataMeetingList();
        mMsgSender = TeamMeetingApp.getmMsgSender();

        mUrlMeetingId = getIntent().getStringExtra("urlMeetingId");

        if (mUrlMeetingId != null) {
            mUrlInsertMeegting = true;
            if (mDebug) {
                Log.e(TAG, "initdata: mUrlMeetingId " + mUrlMeetingId);
            }
            int position = TeamMeetingApp.getmSelfData().getMeetingIdPosition(mUrlMeetingId);
            if (position >= 0) {

                meetingPositiotrue(position);

            } else {
                Toast.makeText(mContext, R.string.str_join_room_wait, Toast.LENGTH_LONG);
                mNetWork.getMeetingInfo(mUrlMeetingId, JoinActType.JOIN_LINK_JOIN_ACTIVITY);
            }
        }


    }

    private void meetingPositiotrue(final int position) {

        if (mDebug) {
            Log.e(TAG, "initdata:+position " + position);
        }
        mWarningCancel.setCancelClickListener(new OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
                sweetAlertDialog.dismiss();
            }
        }).setConfirmClickListener(new OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                sweetAlertDialog.cancel();
                sweetAlertDialog.dismiss();
                enterMeetingActivity(position);
            }
        });
        mWarningCancel.show();

    }

    /**
     * inintLayout
     */
    private void inintLayout() {

        mIMM = (InputMethodManager) MainActivity.this
                .getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        mNetErrorSweetAlertDialog = DialogHelper.createNetErroDilaog(this, sweetClickListener);
        mRlMain = (RelativeLayout) findViewById(R.id.rl_main);
        mCreateRoom = (EditText) findViewById(R.id.et_create_room);
        mRoomCancel = (TextView) findViewById(R.id.tv_cancel_create_room);
        mListView = (ListView) findViewById(R.id.lv_listView);
        mListView.setEmptyView(findViewById(R.id.empty_layout));
        mGetRoom = (Button) findViewById(R.id.btn_get_room);
        mJoinMeeting = (ImageButton) findViewById(R.id.ibtn_join_meeting);
        mGetRoom.setOnClickListener(mOnClickListener);
        mRoomCancel.setOnClickListener(mOnClickListener);
        mJoinMeeting.setOnClickListener(mOnClickListener);

        // initSwipeRefreshLayout();

        mAdapter = new SwipeListAdapter(mContext, mRoomMeetingList, mSwipeListOnClick);
        mListView.setAdapter(mAdapter);

        mRlMain.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (isKeyboardShown(mRlMain.getRootView())) {
                            if (mDebug)
                                Log.e(TAG, "isKeyboardShown open keyboard");
                            mSoftInputFlag = true;
                        } else {
                            if (mDebug)
                                Log.e(TAG, "isKeyboardShown close keyboard");
                            if (mReNameFlag && mSoftInputFlag) {
                                mUIHandler.sendEmptyMessageDelayed(
                                        UPDATE_RENAME_END, 500);
                                mReNameFlag = false;
                                mRoomMeetingList.get(mPosition).setmMeetType2(1);
                            }

                            if (mSoftInputFlag) {
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

    private boolean isKeyboardShown(View rootView) {
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

    private void listViewSetScroll(int position) {

        int itemHeight = getItemHeight(mListView);
        float temp = mListView.getHeight() / (float) getItemHeight(mListView);
        int maxItemTop = 0;
        int d = 0;
        int visibleItem = (int) Math.ceil(temp);

        if (mAdapter.getCount() < visibleItem) {
            mDy = itemHeight * position;
            d = (int) (mDy * 0.5);
            mUIHandler.sendEmptyMessageDelayed(UPDATE_LISTVIEW_SCROLL, 100);
            sendMsgUpDateReadMeShow(position, d);
            return;
        } else {
            maxItemTop = mAdapter.getCount() - visibleItem;
        }
        if (position <= maxItemTop) {
            mDy = 0;
            int i = maxItemTop - position;
            d = (int) (i * itemHeight * 0.5);
            mListView.smoothScrollToPositionFromTop(position, 0, SHOW_EDIT_TEXT_TIME);
            sendMsgUpDateReadMeShow(position, SHOW_EDIT_TEXT_TIME);

        } else {
            int incompleteItemheight = mListView.getHeight() - (visibleItem - 1) * itemHeight;
            mDy = itemHeight * (position - maxItemTop - 1) + incompleteItemheight;
            int posDiff = position - maxItemTop;
            d = (int) (posDiff * itemHeight * 0.5);
            mListView.setSelection(mListView.getBottom());
            mUIHandler.sendEmptyMessageDelayed(UPDATE_LISTVIEW_SCROLL, d);
            sendMsgUpDateReadMeShow(position, SHOW_EDIT_TEXT_TIME + d);

            //sendMsgUpDateReadMeShow(position, d+SHOW_EDIT_TEXT_TIME);
            Log.e(TAG, "maxItemTop " + maxItemTop + " incompleteItemheight "
                    + incompleteItemheight);
            // mListView.smoothScrollToPositionFromTop(maxItemTop, 0, 1000);
            // mListView.smoothScrollToPosition(maxItemTop-1);
            // mListView.animate().translationY(-mDy).setDuration(2000);

        }

    }


    /**
     * getItemHeight
     *
     * @param listView
     * @return
     */
    private int getItemHeight(final ListView listView) {
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
    private boolean hideKeyboard() {
        if (mIMM.isActive(mCreateRoom)) {
            mIMM.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            switch (view.getId()) {
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
                    Intent intent = new Intent(mContext, JoinMeetingActivity.class);
                    //Intent intent = new Intent(mContext, PushSetActivity.class);

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
    private SwipeListOnClick mSwipeListOnClick = new SwipeListOnClick() {

        @Override
        public void onItemClickListener(View v, int position) {
            String meetingId;
            if (hideKeyboard()) {
                return;
            }
            switch (v.getId()) {
                case R.id.fl_front:
                    if (mDebug) {
                        Log.i(TAG, "meetingId-fl_front" + position);
                    }
                    enterMeetingActivity(position);
                    break;
                case R.id.btn_delete:
                    mSign = getSign();
                    meetingId = mRoomMeetingList.get(position).getMeetingid();
                    //mUserId = mRoomMeetingList.get(position).getMeetinguserid();

                    mNetWork.deleteRoom(mSign, meetingId);

                    break;

                case R.id.imgbtn_more_setting:
                    moreSetting(position);
                    break;
                case R.id.et_rename:

                    EditText reName = (EditText) v.findViewById(R.id.et_rename);
                    String newName = reName.getText().toString();
                    String oldName = mRoomMeetingList.get(position).getMeetname();

                    if (!newName.equals(oldName)) {

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

    private void enterMeetingActivity(int position) {
        MeetingListEntity meetingListEntity = mRoomMeetingList.get(position);
        String meetingName = meetingListEntity.getMeetname();
        String meetingId = meetingListEntity.getMeetingid();
        mNetWork.updateUserMeetingJointime(getSign(), meetingId, position);

        int owner = meetingListEntity.getOwner();
        if (owner == 0) {
            mNetWork.getMeetingInfo(meetingId, JoinActType.JOIN_ENTER_ACTIVITY);
        } else {
            statrMeetingActivity(meetingName, meetingId);
        }
    }

    private void statrMeetingActivity(String meetingName, String meetingId) {
        Intent intent = new Intent(mContext, MeetingActivity.class);
        intent.putExtra("meetingName", meetingName);
        intent.putExtra("meetingId", meetingId);
        intent.putExtra("userId", mUserId);
        //int position = TeamMeetingApp.getmSelfData().getMeetingIdPosition(meetingId);
        //if (mDebug)
        //  Log.e(TAG, "statrMeetingActivity: position" + position);
        //mNetWork.updateUserMeetingJointime(getSign(), meetingId, position);
        mContext.startActivity(intent);
    }


    /**
     * soft keyboard Listener
     */
    OnEditorActionListener editorActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            String meetingName = mCreateRoom.getText().toString();

            if (meetingName.length() == 0 || meetingName == null) {
                meetingName = "Untitled room";
            }
            mSign = getSign();
            if (mDebug)
                if (mDebug)
                    Log.e(TAG, "onEditorAction: roomName" + meetingName + mSign);

            applyRoom(meetingName);

            return false;
        }
    };

    private void applyRoom(String meetingName) {
        mIMM.hideSoftInputFromWindow(mCreateRoom.getWindowToken(), 0);
        mCreateRoom.setVisibility(View.GONE);
        mRoomCancel.setVisibility(View.GONE);

        String pushable = "1";
        String meetdesc = "";
        String meetenablde = "1";

        MeetingListEntity meetingList = new MeetingListEntity();
        meetingList.setMeetname(meetingName);
        meetingList.setPushable(1);
        meetingList.setApplyTyep(false);
        meetingList.setJointime(System.currentTimeMillis());

        mRoomMeetingList.add(0, meetingList);

        mAdapter.notifyDataSetChanged();
        mListView.setSelection(0);

        mNetWork.applyRoom(mSign, meetingName, "0", "", meetenablde, pushable);
        // applyRoomNetWrod(meetingName, "0", "", meetenablde, pushable);
        mCreateRoomFlag = true;
    }


    /**
     * moreSetting
     *
     * @param position
     */
    private void moreSetting(int position) {

        Intent intent = new Intent(mContext, RoomSettingActivity.class);
        MeetingListEntity meetingEntity = mRoomMeetingList.get(position);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Intent_KEY.MEETING_ENTY, meetingEntity);
        intent.putExtras(mBundle);
        intent.putExtra(Intent_KEY.POSITION, position);
        startActivityForResult(intent, ExtraType.REQUEST_CODE_ROOM_SETTING);
        ((Activity) mContext).overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (hideKeyboard()) {
                return false;
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.exit_once_more, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                mSign = getSign();
                mNetWork.signOut(mSign);

                this.finish();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case ExtraType.RESULT_CODE_ROOM_SETTING_MESSAGE_INVITE:
                break;
            case ExtraType.RESULT_CODE_ROOM_SETTING_WEIXIN_INVITE:
                break;
            case ExtraType.RESULT_CODE_ROOM_SETTING_COPY_LINK:
                String shareurl = data.getStringExtra("shareUrl");
                if (mDebug) {
                    Log.e(TAG, "onActivityResult: shareurl " + shareurl);
                }

                DialogHelper.onClickCopy(MainActivity.this, shareurl);
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
                    Log.e(TAG, "onActivityResult-Seeting: 关闭");
                // getListNetWork();
                break;
            case ExtraType.REQUEST_CODE_ROOM_MEETING:
                if (mDebug)
                    Log.e(TAG, "onActivityResult: -Meeting 关闭");
                mAdapter.notifyDataSetChanged();
            default:
                break;
        }

    }


    private void settingReName(Intent data) {

        String meetingId;
        mReNameFlag = true;
        mSign = TeamMeetingApp.getmSelfData().getAuthorization();
        int position = data.getIntExtra("position", 0);
        mPosition = position;
        meetingId = data.getStringExtra("meetingId");
        String meetingName = data.getStringExtra("meetingName");

        listViewSetScroll(position);
    }

    private void sendMsgUpDateReadMeShow(int position, int delayed) {
        Message msg = new Message();
        msg.what = UPDATE_RENAME_SHOW;
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        msg.setData(bundle);
        mUIHandler.sendMessageDelayed(msg, delayed);
    }

    private void seetingDeleteRoom(Intent data) {
        mSign = getSign();
        int position = data.getIntExtra("position", 0);
        String meetingId = data.getStringExtra("meetingId");
        mNetWork.deleteRoom(mSign, meetingId);
        mRoomMeetingList.remove(position);
        mAdapter.notifyDataSetChanged();

        //String userId = mRoomMeetingList.get(position).getMeetinguserid();
    }


    private void getListNetWork() {
        mNetWork.getRoomLists(getSign(), 1 + "", 20 + "");
    }

    private void getRoomListSuccess(Message msg) {

        upDataMeetingList();

        mAdapter.notifyDataSetChanged();

        // startInvitePeopleActivity();
    }

    private void startInvitePeopleActivity() {
        if (mCreateRoomFlag) {
            Intent intent = new Intent(MainActivity.this,
                    InvitePeopleActivity.class);
            String meetingId = mRoomMeetingList.get(0).getMeetingid();
            intent.putExtra("meetingId", meetingId);
            startActivityForResult(intent, ExtraType.RESULT_CODE_ROOM_SETTING_COPY_LINK);
            overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
            mCreateRoomFlag = false;
        }
    }

    private void upDataMeetingList() {
        List<MeetingListEntity> list = TeamMeetingApp.getmSelfData().getMeetingLists();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).initUnReadMessage(mContext);
        }
        if (list != null) {
            mRoomMeetingList.clear();
            //mRoomMeetingList.addAll();
            mRoomMeetingList = list;
            Logger.e(list.toString() + "----" + mRoomMeetingList.toString());
        }
        if (mListView != null) {
            mListView.setSelection(0);
        }

    }




    private void netCatchGreatRoom() {
        for (int i = 0; i < mRoomMeetingList.size(); i++) {
            MeetingListEntity meetingListEntity = mRoomMeetingList.get(i);
            if (!meetingListEntity.isApplyTyep()) {
                mNetWork.applyRoom(getSign(), meetingListEntity.getMeetname(), meetingListEntity.getMeettype() + "",
                        meetingListEntity.getMeetdesc(), meetingListEntity.getMeetusable() + "",
                        meetingListEntity.getPushable() + "", 0, i);
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    OnSweetClickListener sweetClickListener = new OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
        }
    };


    @Override
    public void onRequesageMsg(ReqSndMsgEntity requestMsg) {
        switch (requestMsg.getTags()) {
            case JMClientType.MCSENDTAGS_TALK:
                if (mDebug)
                    Log.e(TAG, CRUDChat.selectLoadListSize(mContext, requestMsg.getRoom()) + "onEventMainThread :" + (CRUDChat.setectAllList(mContext)).size());
                if (requestMsg.getTags() == JMClientType.MCSENDTAGS_TALK) {
                    mAdapter.notifyNoReadMessageChanged(requestMsg.getRoom(), requestMsg.getNtime());
                }
                break;
            case JMClientType.MCSENDTAGS_LEAVE:
                if (mDebug && requestMsg.getCmd() == 2)
                    Log.e(TAG, "Someone is leave room !!!!!!!!!!!!!!!");
            case JMClientType.MCSENDTAGS_ENTER:
                mAdapter.notifyMemnumberSetChanged(requestMsg.getRoom(), requestMsg.getNmem());

                if (mDebug && requestMsg.getCmd() == 1)
                    Log.e(TAG, "Someone is go room !!!!!!!!!!!!!!!");
                break;
        }

    }


    private void getMeetingInfoSuccess(Message msg) {
        MeetingListEntity meetingListEntity = TeamMeetingApp.getmSelfData().getMeetingListEntity();
        int usable = meetingListEntity.getMeetusable();
        mUrlMeetingName = meetingListEntity.getMeetname();
        String meetinId = meetingListEntity.getMeetingid();
        String joinType;
        switch (usable) {
            case 0://no
                Toast.makeText(mContext, R.string.str_meeting_deleted, Toast.LENGTH_SHORT).show();
                break;
            case 1:

                joinType = msg.getData().getString(JoinActType.JOIN_TYPE);
                if (joinType == JoinActType.JOIN_ENTER_ACTIVITY) {
                    statrMeetingActivity(mUrlMeetingName, meetinId);
                } else if (joinType == JoinActType.JOIN_LINK_JOIN_ACTIVITY) {
                    mNetWork.insertUserMeetingRoom(getSign(), meetinId, JoinActType.JOIN_INSERT_LINK_JOIN_ACTIVITY);
                }
                break;

            case 2://private

                joinType = msg.getData().getString(JoinActType.JOIN_TYPE);
                if (joinType == JoinActType.JOIN_ENTER_ACTIVITY) {
                    statrMeetingActivity(mUrlMeetingName, meetinId);
                } else if (joinType == JoinActType.JOIN_LINK_JOIN_ACTIVITY) {

                    Toast.makeText(mContext, R.string.str_meeting_privated, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * netWork can user
     *
     * @param type
     */

    public void netWorkTypeStart(int type) {
        switch (NetType.values()[type]) {
            case TYPE_WIFI:
                if (mDebug)
                    Log.e(TAG, "TYPE_WIFI ");
                netTyp = true;
                break;
            case TYPE_4G:
                if (mDebug)
                    Log.e(TAG, "TYPE_4G ");
                netTyp = true;
                break;
            case TYPE_3G:
                if (mDebug)
                    Log.e(TAG, "TYPE_3G ");
                netTyp = true;
                break;
            case TYPE_2G:
                if (mDebug)
                    Log.e(TAG, "TYPE_2G ");
                netTyp = true;
                break;

            case TYPE_NULL:
                if (mDebug)
                    Log.e(TAG, "TYPE_NULL ");
                netTyp = false;
                progressDiloag();
                break;
            case TYPE_UNKNOWN:
                netTyp = false;
                if (mDebug)
                    Log.e(TAG, "TYPE_UNKNOWN: ");
            default:
                break;
        }

    }


    /**
     * For EventBus callback.
     */
    public void onEventMainThread(Message msg) {

        switch (EventType.values()[msg.what]) {
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
                if (mDebug)
                    Log.e(TAG, "MSG_APPLY_ROOM_SUCCESS ");
                mAdapter.notifyDataSetChanged();
                startInvitePeopleActivity();
                // getListNetWork();
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
                // mNetErrorSweetAlertDialog.show();
                break;
            case MSG_MESSAGE_RECEIVE:
                if (mDebug)
                    break;
            case MCCMD_LEAVE:

                break;
            case MCCMD_ENTER:
                if (mDebug)
                    Log.e(TAG, "Some people comming room!!!!!!!!!!!!!!!!!");
                break;
            case MSG_GET_MEETING_INFO_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_MEETING_INFO_SUCCESS");
                getMeetingInfoSuccess(msg);
                break;
            case MSG_GET_MEETING_INFO_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_MEETING_INFO_FAILED");
                String meetingId = msg.getData().getString("meetingid");
                mNetWork.deleteRoom(getSign(), meetingId);
                Toast.makeText(mContext, R.string.meeting_delete_create, Toast.LENGTH_SHORT).show();

                break;
            case MSG_INSERT_USER_MEETING_ROOM_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_INSERT_USER_MEETING_ROOM_SUCCESS");
                Log.e(TAG, "insertUserMeetingRoomSuccess: " + msg.getData().toString());
                mAdapter.notifyDataSetChanged();
                String join_insert_type = msg.getData().getString(JoinActType.JOIN_INSERT_TYPE);
                if (join_insert_type == JoinActType.JOIN_INSERT_LINK_JOIN_ACTIVITY) {
                    statrMeetingActivity(mUrlMeetingName, mUrlMeetingId);
                }
                break;
            case MSG_INSERT_USER_MEETING_ROOM_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_INSERT_USER_MEETING_ROOM_FAILED");
                Toast.makeText(mContext, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                break;
            case MSG_UP_DATE_USER_MEETING_JOIN_TIME_SUCCESS:
                mAdapter.notifyDataSetChanged();
                if (mDebug)
                    Log.e(TAG, " " + mAdapter.getCount());
                break;
            case MSG_UPDATE_ROOM_PUSHABLE_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_UPDATE_ROOM_PUSHABLE_SUCCESS");
                mAdapter.notifyDataSetChanged();
            case MSG_UPDATE_ROOM_ENABLE_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_UPDATE_ROOM_PUSHABLE_SUCCESS");
                mAdapter.notifyDataSetChanged();
                break;

            case MSG_DELETE_ROOM_SUCCESS:
                meetingId = msg.getData().getString("meetingid");
                int position = mAdapter.getMeetingIdPosition(meetingId);
                mRoomMeetingList.remove(position);
                mAdapter.notifyDataSetChanged();
                CRUDChat.deleteByMeetingId(mContext, meetingId);

            case JOIN_MEETINGID_EXIST:
                int pos = (int) msg.obj;
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: pos" + pos);
                meetingPositiotrue(pos);
                //enterMeetingActivity(pos);
            default:
                break;
        }
    }


}

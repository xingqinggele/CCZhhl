package com.hulian.oa.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hulian.oa.R;
import com.hulian.oa.address.AddressFragment;
import com.hulian.oa.address.pad.Address_Pad_Fragment;
import com.hulian.oa.address.AddressFragment2;
import com.hulian.oa.agency.AgencyFragment;
import com.hulian.oa.message.Wechat;
import com.hulian.oa.message.helper.SystemMessageUnreadManager;
import com.hulian.oa.message.reminder.ReminderItem;
import com.hulian.oa.message.reminder.ReminderManager;
import com.hulian.oa.message.helper.SessionHelper;
import com.hulian.oa.news.fragment.NewsFragment;
import com.hulian.oa.message.team.TeamCreateHelper;
import com.hulian.oa.utils.StatusBarUtil;
import com.hulian.oa.work.fragment.WorkFragment;
import com.hulian.oa.work.activity.mail.pad.MailFragment;
import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.AVChatProfile;
import com.netease.nim.avchatkit.TeamAVChatProfile;
import com.netease.nim.avchatkit.receiver.PhoneCallStateObserver;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.support.permission.MPermission;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener , ReminderManager.UnreadNumChangedCallback{
//测试

    private static final int BASIC_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_CODE_ADVANCED = 2;
    private static final int REQUEST_CODE_NORMAL = 1;
    private static final String[] BASIC_PERMISSIONS = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    };
    private boolean isFirstIn;
    private Observer<Integer> sysMsgUnreadCountChangedObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer unreadCount) {
            SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unreadCount);
            ReminderManager.getInstance().updateContactUnreadNum(unreadCount);
        }
    };


    boolean isgowork=false;
    boolean isgoaddress=false;
    boolean isgoagency=false;
    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.rb_message)
    RadioButton rbMessage;
    @BindView(R.id.rb_agency)
    RadioButton rbAgency;
    @BindView(R.id.rb_address)
    RadioButton rbAddress;
    @BindView(R.id.rb_work)
    RadioButton rb_work;
    @BindView(R.id.rb_news)
    RadioButton rbNews;
    @BindView(R.id.rg_footer)
    RadioGroup rgFooter;
    private FragmentTransaction transaction;
    private AddressFragment addressFragment;
    private AddressFragment2 addressFragment2;
    private Wechat messageFragment;
    private NewsFragment newsFragment;
    private WorkFragment workFragment;
    private AgencyFragment agencyFragment;

    private MailFragment mailFragment;
    private Address_Pad_Fragment address_Pad_Fragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        rgFooter.setOnCheckedChangeListener(this);
        rgFooter.check(R.id.rb_message);
        registerMsgUnreadInfoObserver(true);
        registerSystemMessageObservers(true);
        requestSystemMessageUnreadCount();
        requestBasicPermission();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isgowork=intent.getBooleanExtra("isgowork", false);
        isgoaddress=intent.getBooleanExtra("isgoaddress", false);
        if(isgowork){
            rb_work.performClick();
        }
        if(isgoaddress){
            rbAddress.performClick();
        }
        if(isgoagency){
            rbAgency.performClick();
        }
        else{
            rbMessage.performClick();
        }
    }
    private boolean parseIntent() {

        Intent intent = getIntent();

        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            intent.removeExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                case Team:
                    SessionHelper.startTeamSession(this, message.getSessionId());
                    break;
            }

            return true;
        }

        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            intent.removeExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                case Team:
                    SessionHelper.startTeamSession(this, message.getSessionId());
                    break;
            }

            return true;
        }

        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch (i) {
            case R.id.rb_message:
                StatusBarUtil.statusBarLightMode(this);
                    if (messageFragment == null) {
                        messageFragment = new Wechat().newInstance("");
                        transaction.add(R.id.content, messageFragment);
                    } else {
                        transaction.show(messageFragment);
                    }
                break;
            case R.id.rb_news:
                StatusBarUtil.statusBarLightMode(this);
                if (newsFragment == null) {
                    newsFragment = new NewsFragment().newInstance("");
                    transaction.add(R.id.content,newsFragment);
                } else {
                    transaction.show(newsFragment);
                }
                break;
            case R.id.rb_agency:
                StatusBarUtil.statusBarLightMode_white(this);
                if (agencyFragment == null) {
                    agencyFragment = new AgencyFragment().newInstance("");
                    transaction.add(R.id.content,agencyFragment);
                } else {
                    transaction.show(agencyFragment);
                }
                break;
            case R.id.rb_address:
                StatusBarUtil.statusBarLightMode(this);

//                if(BuildConfig.IsPad){
//
//                    if (addressFragment == null) {
//                        addressFragment = new AddressFragment().newInstance("");
//                        transaction.add(R.id.content, addressFragment);
//                    } else {
//                        transaction.show(addressFragment);
//                    }
//                }
//                else {
                // 我注释的
//                    if (addressFragment == null) {
//                        addressFragment = new AddressFragment().newInstance("");
//                        transaction.add(R.id.content, addressFragment);
//                    } else {
//                        transaction.show(addressFragment);
//                    }
                //我加的
                if (addressFragment2 == null) {
                    addressFragment2 = new AddressFragment2().newInstance("");
                    transaction.add(R.id.content, addressFragment2);
                } else {
                    transaction.show(addressFragment2);
                }

                break;
            case R.id.rb_work:
                StatusBarUtil.statusBarLightMode(this);
                if (workFragment == null) {
                    workFragment =  WorkFragment.newInstance("");
                    transaction.add(R.id.content, workFragment);
                } else {
                    transaction.show(workFragment);
                }
                break;
        }
        transaction.commit();
    }
    private void hideAllFragment(FragmentTransaction transaction) {

        if (newsFragment != null) this.transaction.hide(newsFragment);
        if (messageFragment!=null) this.transaction.hide(messageFragment);
        if (mailFragment!=null) this.transaction.hide(mailFragment);
        if (workFragment !=null) this.transaction.hide(workFragment);
        if (addressFragment2 != null) this.transaction.hide(addressFragment2);
//        if (addressFragment != null) this.transaction.hide(addressFragment);
        if (agencyFragment != null) this.transaction.hide(agencyFragment);
        if (address_Pad_Fragment != null) this.transaction.hide(address_Pad_Fragment);
    }

    /**
     * 注册未读消息数量观察者
     */
    private void registerMsgUnreadInfoObserver(boolean register) {
        if (register) {
            ReminderManager.getInstance().registerUnreadNumChangedCallback(this);
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(this);
        }
    }

    /**
     * 注册/注销系统消息未读数变化
     */
    private void registerSystemMessageObservers(boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver, register);
    }

    /**
     * 查询系统消息未读数
     */
    private void requestSystemMessageUnreadCount() {
        int unread = NIMClient.getService(SystemMessageService.class).querySystemMessageUnreadCountBlock();
        SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unread);
        ReminderManager.getInstance().updateContactUnreadNum(unread);
    }


    private void requestBasicPermission() {
        MPermission.printMPermissionResult(true, this, BASIC_PERMISSIONS);
        MPermission.with(MainActivity.this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }
    //未读消息数量观察者实现
    @Override
    public void onUnreadNumChanged(ReminderItem item) {
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_NORMAL) {
            final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            if (selected != null && !selected.isEmpty()) {
                TeamCreateHelper.createNormalTeam(this, selected, false, null);
            } else {
                ToastHelper.showToast(this, "请选择至少一个联系人！");
            }
        } else if (requestCode == REQUEST_CODE_ADVANCED) {
            final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            TeamCreateHelper.createAdvancedTeam(this, selected);
        }
    }


    private void enableAVChat() {
        registerAVChatIncomingCallObserver(true);
    }

    private void registerAVChatIncomingCallObserver(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>() {
            @Override
            public void onEvent(AVChatData data) {
                String extra = data.getExtra();
                Log.e("Extra", "Extra Message->" + extra);
                if (PhoneCallStateObserver.getInstance().getPhoneCallState() != PhoneCallStateObserver.PhoneCallStateEnum.IDLE
                        || AVChatProfile.getInstance().isAVChatting()
                        || AVChatManager.getInstance().getCurrentChatId() != 0) {
//                    LogUtil.i(TAG, "reject incoming call data =" + data.toString() + " as local phone is not idle");
                    AVChatManager.getInstance().sendControlCommand(data.getChatId(), AVChatControlCommand.BUSY, null);
                    return;
                }
                // 有网络来电打开AVChatActivity
                TeamAVChatProfile.sharedInstance().setTeamAVChatting(true);
                AVChatKit.outgoingTeamCall(MainActivity.this, true, "", "", null, "视频会议");
            }
        }, register);
    }


    private long mPressedTime = 0;
    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {//比较两次按键时间差
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        } else {//退出程序
            this.finish();
            System.exit(0);
        }
    }

}

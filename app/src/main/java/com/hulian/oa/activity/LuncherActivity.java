package com.hulian.oa.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.os.Bundle;
import android.widget.Toast;

import com.hulian.oa.BuildConfig;
import com.hulian.oa.DemoCache;
import com.hulian.oa.R;
import com.hulian.oa.bean.User;
import com.hulian.oa.net.HttpRequest;
import com.hulian.oa.net.OkHttpException;
import com.hulian.oa.net.RequestParams;
import com.hulian.oa.net.ResponseCallback;
import com.hulian.oa.socket.JWebSocketClientService;
import com.hulian.oa.utils.SPUtils;
import com.hulian.oa.utils.StatusBarUtil;
import com.hulian.oa.utils.ToastHelper;
import com.hulian.oa.utils.preference.Preferences;
import com.hulian.oa.utils.preference.UserPreferences;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.lang.Thread.sleep;

public class LuncherActivity extends BaseActivity {
    private AbortableFuture<LoginInfo> loginRequest;
//    private JWebSocketClientService.JWebSocketClientBinder binder;
//    private JWebSocketClientService jWebSClientService;
    private Intent login;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_luncher);
//        login=new Intent(LuncherActivity.this, LoginActivity.class);
        login=new Intent(LuncherActivity.this, LoginActivity_ceshi.class);
//        new Thread( new Runnable( ) {
//            @Override
//            public void run() {
//                //耗时任务，比如加载网络数据
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 这里可以睡几秒钟，如果要放广告的话
//                        try {
//                            sleep(3000);
//
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        } ).start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SPUtils.contains(mContext,"userId")) {
//                    if(BuildConfig.IsPad){
//                        doLogin(SPUtils.get(mContext,"username","-1").toString(),SPUtils.get(mContext,"password","-1").toString());
//                    }else {
                        //doLogin(SPUtils.get(mContext,"username","-1").toString(),SPUtils.get(mContext,"password","-1").toString());
                    PostKeyValueRequet(SPUtils.get(mContext,"username","-1").toString(),SPUtils.get(mContext,"password","-1").toString());

//                    }
                }
                else {
                    Intent intent = new Intent(LuncherActivity.this, LoginActivity_ceshi.class);
                    startActivity(intent);
                    LuncherActivity.this.finish();
                }
            }
        }, 2000);

        //绑定服务
//        bindService();
    }
    private void doLogin(String account1,String token1) {
        //   onLoginDone();
//         云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
//         在这里直接使用同步到云信服务器的帐号和token登录。
//         这里为了简便起见，demo就直接使用了密码的md5作为token。
//         如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
        final String account = account1;
        final String token =token1;
        // 登录
        loginRequest = NimUIKit.login(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                loadDialog.dismiss();
                onLoginDone();
                DemoCache.setAccount(account);
                saveLoginInfo(account, token);
                PostKeyValueRequet(account1,token1);
            }
            @Override
            public void onFailed(int code) {
                loadDialog.dismiss();
                onLoginDone();
                if (code == 302 || code == 404) {
                    ToastHelper.showToast(LuncherActivity.this, "登录失败");
                } else {
                    ToastHelper.showToast(LuncherActivity.this, "登录失败: " + code);
                }

                // 跳转登录页
                startActivity(login);
            }

            @Override
            public void onException(Throwable exception) {
                loadDialog.dismiss();
                ToastHelper.showToast(LuncherActivity.this, "登录失败");
                onLoginDone();
                // 跳转登录页
                startActivity(login);
            }
        });

    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }
    private void onLoginDone() {
        loginRequest = null;
        // DialogMaker.dismissProgressDialog();
    }
    private void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
        // 加载状态栏配置
        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = DemoCache.getNotificationConfig();
            UserPreferences.setStatusConfig(statusBarNotificationConfig);
        }
        // 更新配置
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }
    /**
     * POST请求
     */
    public void PostKeyValueRequet(String account1,String token1) {
        RequestParams params = new RequestParams();
        params.put("username", account1);
        params.put("password", token1);
        HttpRequest.postLoginApi(params, new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                //    UserInfo userInfo = (UserInfo) responseObj;
                User user=(User) responseObj;
//                Toast.makeText(LoginActivity.this, "请求成功"+responseObj.toString(), Toast.LENGTH_SHORT).show();
                // 初始化消息提醒配置
                SPUtils.clear(mContext);
                SPUtils.put(mContext, "userId", user.getUserId());
                SPUtils.put(mContext, "username", account1);
                SPUtils.put(mContext, "loginName", user.getLoginName());
                SPUtils.put(mContext, "phonenumber", user.getPhonenumber());
                SPUtils.put(mContext, "sex", user.getSex());
                SPUtils.put(mContext,"nickname",user.getUserName());
                SPUtils.put(mContext,"isLead",user.getIsLead());
                SPUtils.put(mContext, "deptId", user.getDeptId());
                SPUtils.put(mContext,"deptname",user.getDept().getDeptName());
                SPUtils.put(mContext,"email",user.getEmail());
                SPUtils.put(mContext, "password", token1);
                SPUtils.put(mContext, "roleKey", user.getRolesStr());
                initNotificationConfig();
                Intent intent=new Intent(LuncherActivity.this, MainActivity.class);
//                Intent intent=new Intent(LuncherActivity.this, NoticeMeetingActivity.class);
//                Intent intent=new Intent(LuncherActivity.this, NoticeWorkActivity.class);
                startActivity(intent);
                /*******************************新加的开启Socket**/

                //启动服务
//                startJWebSClientService();
//                //检测通知是否开启
//                checkNotification(mContext);
//                jWebSClientService.startWebSocket(user.getUserId());
                finish();
            }
            @Override
            public void onFailure(OkHttpException failuer) {
                Toast.makeText(LuncherActivity.this, "请求失败="+failuer.getEmsg(), Toast.LENGTH_SHORT).show();
                // 跳转登录页
                startActivity(login);
            }
        });

    }

    /**
     * 启动服务（websocket客户端服务）
     */
    private void startJWebSClientService() {
        Intent intent = new Intent(mContext, JWebSocketClientService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            Log.e("MainActivity", "服务与活动成功绑定");
//            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
//            jWebSClientService = binder.getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            Log.e("MainActivity", "服务与活动成功断开");
//        }
//    };

    /**
     * 绑定服务
     */
//    private void bindService() {
//        Intent bindIntent = new Intent(mContext, JWebSocketClientService.class);
//        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
//    }

    /**
     * 检测是否开启通知
     *
     * @param context
     */
    private void checkNotification(final Context context) {
        if (!isNotificationEnabled(context)) {
            new AlertDialog.Builder(context).setTitle("温馨提示")
                    .setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setNotification(context);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    /**
     * 如果没有开启通知，跳转至设置界面
     *
     * @param context
     */
    private void setNotification(Context context) {
        Intent localIntent = new Intent();
        //直接跳转到应用通知设置的代码：
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    /**
     * 获取通知权限,监测是否开启了系统通知
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(serviceConnection);
    }

}

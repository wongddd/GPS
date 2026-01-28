package com.yyt.trackcar.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.socks.library.KLog;
import com.xuexiang.xui.widget.activity.BaseSplashActivity;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAADeviceModel_Table;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.AAAUserModel_Table;
import com.yyt.trackcar.utils.AAANetworkUtils;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.SettingSPUtils;

import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      SplashActivity
 * @ author:        QING
 * @ createTime:    2019-11-14 17:08
 * @ describe:      TODO 启动页
 */
public class SplashActivity extends BaseSplashActivity implements CancelAdapt {
    private boolean isFinish;
    //    private boolean isFirstEnterApp;
    private static final String firstEnterApp = "firstEnterApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            isFinish = true;
            finish();
        }
    }

    @Override
    public void onCreateActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AAANetworkUtils.analysisNet();
            }
        }).start();
        if (!isFinish)
            startSplash(false);
    }

    @Override
    public void onSplashFinished() {
        long uid = SettingSPUtils.getInstance().getLong(CWConstant.U_ID, -1);
        if (uid >= 0) {
            AAAUserModel userModel = SQLite.select().from(AAAUserModel.class)
                    .where(AAAUserModel_Table.userId.eq(uid))
                    .querySingle();
            if (userModel == null)
                ActivityUtils.startActivity(LoginActivity.class);
            else {
                List<AAADeviceModel> deviceList = SQLite.select().from(AAADeviceModel.class)
                        .where(AAADeviceModel_Table.userId.eq(uid))
                        .queryList();
                MainApplication.getInstance().setTrackDeviceList(deviceList);
                MainApplication.getInstance().setTrackUserModel(userModel);
                String selectImei = userModel.getSelectDeviceId();
                if (selectImei == null)
                    selectImei = "";
                for (AAADeviceModel deviceModel : deviceList) {
                    if (selectImei.equals(deviceModel.getDeviceImei())) {
                        MainApplication.getInstance().setTrackDeviceModel(deviceModel);
                        break;
                    }
                }
                if (MainApplication.getInstance().getTrackDeviceModel() == null && deviceList.size() > 0) {
                    MainApplication.getInstance().setTrackDeviceModel(deviceList.get(0));
                    userModel.setSelectDeviceId(MainApplication.getInstance().getTrackDeviceModel().getDeviceImei());
                    userModel.save();
                }
                Intent intent;
                if (deviceList.size() == 0) {
                    intent = new Intent(this, BindDeviceActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(CWConstant.TYPE, "1");
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    intent = new Intent(this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(CWConstant.TYPE, "1");
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                ActivityUtils.startActivity(intent);
            }
        } else {
//            if (isFirstEnterApp()) {
//                startDialog();
//            }else {
            SettingSPUtils.getInstance().putString(CWConstant.USERNAME_LOGIN,
                    SettingSPUtils.getInstance().getString(CWConstant.USERNAME,
                            ""));
            SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN,
                    SettingSPUtils.getInstance().getString(CWConstant.PASSWORD,
                            ""));
            ActivityUtils.startActivity(LoginActivity.class);
//            }
        }
        finish();

//        if (SettingSPUtils.getInstance().getBoolean(CWConstant.FIRST_OPEN, true)) {
//            SettingSPUtils.getInstance().putBoolean(CWConstant.FIRST_OPEN, false);
//            Bundle bundle = new Bundle();
//            bundle.putBoolean(CWConstant.FIRST_OPEN, true);
//            RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP,
//            "0", "",bundle);
//        }
//        finish();

//        long uid = SettingSPUtils.getInstance().getLong(TConstant.USER_ID_NEW,-1);
//        if (uid > 0){
//            AAAUserModel userModel = SQLite.select().from(AAAUserModel.class)
//                    .where(AAAUserModel_Table.userId.eq(uid))
//                    .querySingle();
//            if (userModel == null)
//                ActivityUtils.startActivity(LoginActivity.class);
//            else {
//                List<AAADeviceModel> deviceList = SQLite.select().from(AAADeviceModel.class)
//                        .where(AAADeviceModel_Table.userId.eq(uid))
//                        .queryList();
//                MainApplication.getInstance().setTrackDeviceList(deviceList);
//                MainApplication.getInstance().setTrackUserModel(userModel);
//                String selectImei = userModel.getSelectDeviceId();
//                KLog.d(new Gson().toJson(deviceList));
//                if (selectImei == null)
//                    selectImei = "";
//                for (AAADeviceModel deviceModel : deviceList) {
//                    if (selectImei.equals(deviceModel.getDeviceImei())) {
//                        MainApplication.getInstance().setTrackDeviceModel(deviceModel);
//                        break;
//                    }
//                }
//                if (MainApplication.getInstance().getTrackDeviceModel() == null && deviceList
//                .size() > 0) {
//                    MainApplication.getInstance().setTrackDeviceModel(deviceList.get(0));
//                    userModel.setSelectDeviceId(MainApplication.getInstance()
//                    .getTrackDeviceModel().getDeviceImei());
//                    userModel.save();
//                }
//                Intent intent;
//                if (deviceList.size() == 0) {
////                    intent = new Intent(this, SelectDeviceTypeActivity.class);
////                    Bundle bundle = new Bundle();
////                    bundle.putInt(CWConstant.TYPE, 3);
////                    intent.putExtras(bundle);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent = new Intent(this, BindDeviceActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(CWConstant.TYPE, "1");
//                    intent.putExtras(bundle);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                } else {
//                    intent = new Intent(this, MainActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(CWConstant.TYPE, "1");
//                    intent.putExtras(bundle);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                ActivityUtils.startActivity(intent);
//            }
//        } else {
//            SettingSPUtils.getInstance().putString(CWConstant.USERNAME_LOGIN,
//                    SettingSPUtils.getInstance().getString(CWConstant.USERNAME,
//                    ""));
//            SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN,
//                    SettingSPUtils.getInstance().getString(CWConstant.PASSWORD,
//                    ""));
//            ActivityUtils.startActivity(LoginActivity.class);
//        }
////        if (SettingSPUtils.getInstance().getBoolean(CWConstant.FIRST_OPEN, true)) {
////            SettingSPUtils.getInstance().putBoolean(CWConstant.FIRST_OPEN, false);
////            Bundle bundle = new Bundle();
////            bundle.putBoolean(CWConstant.FIRST_OPEN, true);
////            RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP,
////            "0", "",bundle);
////        }
//        finish();

    }

    /**
     * 是否是首次进入APP
     */
    private boolean isFirstEnterApp() {
        return SettingSPUtils.getInstance().getBoolean(firstEnterApp, true);
    }

    /**
     * 保存是否为首次进入APP状态
     */
    private void saveFirstEnterApp(boolean value) {
        SettingSPUtils.getInstance().putBoolean(firstEnterApp, value);
    }


    private void startDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_intimate);
            window.setGravity(Gravity.CENTER);

            TextView tvContent = window.findViewById(R.id.tv_content);
            TextView tvCancel = window.findViewById(R.id.tvCancel);
            TextView tvAgree = window.findViewById(R.id.tv_agree);
            String str = "    感谢您使用传信鸽软件，本软件非常重视您的个人信息和隐私保护。" +
                    "为了更好地保障您的个人权益，在您使用我们的产品前，" +
                    "请务必审慎阅读《隐私政策》和《用户服务协议》内的所有条款，" +
                    "尤其是:\n" +
                    " 1.我们对您的个人信息的收集/保存/使用/对外提供/保护等规则条款，以及您的用户权利等条款;\n" +
                    " 2.约定我们的限制责任、免责条款;\n" +
                    " 3.其他以颜色或加粗进行标识的重要条款。\n" +
                    "您点击“同意并继续”的行为即表示您已阅读完毕并同意以上协议的全部内容。" +
                    "如您同意以上协议内容，请点击“同意”，开始使用我们的产品和服务!";

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(str);
            final int start = str.indexOf("《");//第一个出现的位置
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(SplashActivity.this,
                            ProtocolAndAgreementActivity.class);
                    intent.putExtra(CWConstant.TYPE, 0);
                    startActivity(intent);
//                    Toast.makeText(SplashActivity.this, "《隐私政策》", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.blue));
                    ds.setUnderlineText(false);
                }
            }, start, start + 6, 0);

            int end = str.lastIndexOf("《");
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(SplashActivity.this,
                            ProtocolAndAgreementActivity.class);
                    intent.putExtra(CWConstant.TYPE, 1);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.blue));
                    ds.setUnderlineText(false);
                }
            }, end, end + 8, 0);

            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            tvContent.setText(ssb, TextView.BufferType.SPANNABLE);


            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveFirstEnterApp(false);
                    finish();
                }
            });

            tvAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    saveFirstEnterApp(false);

                    SettingSPUtils.getInstance().putString(CWConstant.USERNAME_LOGIN,
                            SettingSPUtils.getInstance().getString(CWConstant.USERNAME,
                                    ""));
                    SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN,
                            SettingSPUtils.getInstance().getString(CWConstant.PASSWORD,
                                    ""));
                    ActivityUtils.startActivity(LoginActivity.class);
                    finish();
//                    checkPermission();
                    alertDialog.cancel();
                }
            });
        }

    }


    @Override
    protected long getSplashDurationMillis() {
        return 500;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}

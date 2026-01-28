package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.SettingSPUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      NewWatchLoginFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 09:10
 * @ describe:      TODO 在新手表上登录
 */
@Page(name = "NewWatchLogin")
public class NewWatchLoginFragment extends BaseFragment {
    @BindView(R.id.ivPortrait)
    ImageView mIvPortrait; // 头像
    @BindView(R.id.tvName)
    TextView mTvName; // 名称文本
    @BindView(R.id.tvId)
    TextView mTvId; // 设备号文本
    @BindView(R.id.tvPoint)
    TextView mTvPoint; // 积分文本
    @BindView(R.id.tvFriend)
    TextView mTvFriend; // 好友文本
    @BindView(R.id.tvContacts)
    TextView mTvContacts; // 联系人文本

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_watch_login;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.new_watch_login);
        return titleBar;
    }

    @Override
    protected void initViews() {
        refreshDeviceInfo(null);
        mTvFriend.setText("0");
        mTvContacts.setText("0");
    }

    /**
     * 刷新设备信息
     */
    private void refreshDeviceInfo(DeviceInfoModel infoModel) {
        DeviceModel deviceModel = getDevice();
        if (infoModel == null) {
            UserModel userModel = getUserModel();
            if (userModel != null && deviceModel != null) {
                OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                        .and(DeviceInfoModel_Table.u_id.eq(userModel.getU_id()))
                        .and(DeviceInfoModel_Table.imei.eq(deviceModel.getImei())));
                infoModel = SQLite.select().from(DeviceInfoModel.class)
                        .where(operatorGroup)
                        .querySingle();
            }
        }
        int imgRes;
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
            imgRes = R.mipmap.ic_device_portrait;
        else
            imgRes = R.mipmap.ic_default_pigeon_marker;
        if (infoModel == null)
            ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
                    mIvPortrait);
        else
            ImageLoadUtils.loadPortraitImage(getContext(), infoModel.getHead(),
                    imgRes, mIvPortrait);
        if (deviceModel == null) {
            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                mTvName.setText(R.string.baby);
            else
                mTvName.setText(R.string.device_name);
            mTvId.setText(getString(R.string.device_imei, ""));
        } else {
            if (infoModel == null || TextUtils.isEmpty(infoModel.getNickname())) {
                if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                    mTvName.setText(R.string.baby);
                else
                    mTvName.setText(R.string.device_name);
            } else
                mTvName.setText(infoModel.getNickname());
            mTvId.setText(getString(R.string.device_imei, deviceModel.getImei()));
        }
    }

    @SingleClick
    @OnClick({R.id.scanBtn})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.scanBtn: // 扫一扫
                bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE, 2);
                openNewPage(CameraCaptureFragment.class, bundle);
                break;
            default:
                break;
        }
    }
}

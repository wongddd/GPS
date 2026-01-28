package com.yyt.trackcar.ui.adapter;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.utils.DensityUtils;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.StringUtils;
import com.yyt.trackcar.utils.TextColorSizeHelper;
import com.yyt.trackcar.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      DeviceListAdapter
 * @ author:        QING
 * @ createTime:    2023/3/27 16:39
 * @ describe:      TODO 设备列表适配器
 */
public class DeviceListAdapter extends BaseQuickAdapter<AAADeviceModel, BaseViewHolder> {
    private int type; // 类型 3 已解绑

    public DeviceListAdapter(List<AAADeviceModel> data) {
        super(R.layout.item_device_list, data);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull AAADeviceModel model) {
        String imei = StringUtils.getNotNullText(model.getDeviceImei());
        String name = StringUtils.getNotNullText(model.getDeviceName());
        boolean isActivated = model.getActivated() != null && model.getActivated() == 1;
        helper.setText(R.id.tvImei, String.format("%s:%s",
                mContext.getString(R.string.imei), imei));
        ImageLoadUtils.loadPortraitImage(mContext, model.getHeadPic(),
                R.mipmap.ic_default_pigeon_marker, helper.getView(R.id.ivHeadPhoto));
        if (DataUtils.isPigeonDevice(imei)) {
            boolean isLost = model.getDeviceStatus() != null && model.getDeviceStatus() == 2;
            if (!isActivated || isLost) {
                List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
                String notActiceText = null;
                String lostText = null;
                if (!isActivated) {
                    notActiceText = String.format(" %s ",
                            mContext.getString(R.string.device_list_selector_not_active));
                    list.add(new TextColorSizeHelper.SpanInfo(notActiceText,
                            DensityUtils.sp2px(14),
                            ContextCompat.getColor(mContext, R.color.white),
                            ContextCompat.getColor(mContext, R.color.red),
                            DensityUtils.dp2px(2), true));
                }
                if (isLost) {
                    lostText = String.format(" %s ",
                            mContext.getString(R.string.device_list_selector_lost));
                    list.add(new TextColorSizeHelper.SpanInfo(lostText,
                            DensityUtils.sp2px(14),
                            ContextCompat.getColor(mContext, R.color.white),
                            ContextCompat.getColor(mContext, R.color.red),
                            DensityUtils.dp2px(2), true));
                }
                if (!isActivated && isLost) {
                    helper.setText(R.id.tvName, TextColorSizeHelper.getTextSpan(mContext,
                            String.format("%s:%s %s %s", mContext.getString(R.string.nickname),
                                    name, notActiceText, lostText), list));
                    helper.setText(R.id.handleBtn, R.string.device_list_selector_find);
                } else if (!isActivated) {
                    helper.setText(R.id.tvName, TextColorSizeHelper.getTextSpan(mContext,
                            String.format("%s:%s %s", mContext.getString(R.string.nickname), name
                                    , notActiceText), list));
                    helper.setText(R.id.handleBtn, R.string.device_list_selector_lost);
                } else {
                    helper.setText(R.id.tvName, TextColorSizeHelper.getTextSpan(mContext,
                            String.format("%s:%s %s", mContext.getString(R.string.nickname), name
                                    , lostText), list));
                    helper.setText(R.id.handleBtn, R.string.device_list_selector_find);
                }
            } else {
                helper.setText(R.id.tvName, String.format("%s:%s",
                        mContext.getString(R.string.nickname), name));
                helper.setText(R.id.handleBtn, R.string.device_list_selector_lost);
            }
            if (model.getRaceStatus() == null || model.getRaceStatus() == 0) {
                helper.setText(R.id.tvRaceStatus,
                        String.format(mContext.getString(R.string.device_list_race_status),
                        mContext.getString(R.string.private_mode)));
            } else {
                helper.setText(R.id.tvRaceStatus,
                        String.format(mContext.getString(R.string.device_list_race_status),
                        mContext.getString(R.string.competition_mode)));
            }
            helper.setText(R.id.tvType, String.format("%s:%d",
                    mContext.getString(R.string.device_type), model.getDeviceType()));
            helper.getView(R.id.tvRingNo).setVisibility(View.VISIBLE);
            helper.setText(R.id.tvRingNo,
                    String.format(mContext.getString(R.string.device_list_ring_no),
                            StringUtils.getNotNullText(model.getRingNo())));
            helper.getView(R.id.tvLoseTime).setVisibility(View.VISIBLE);
            helper.getView(R.id.tvRetrieveTime).setVisibility(View.VISIBLE);
            helper.getView(R.id.tvRaceStatus).setVisibility(model.getRaceStatus() == null ?
                    View.GONE : View.VISIBLE);
            helper.getView(R.id.handleBtn).setVisibility(View.VISIBLE);
        } else {
            if (!isActivated) {
                List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
                String notActiceText = String.format(" %s ",
                        mContext.getString(R.string.device_list_selector_not_active));
                list.add(new TextColorSizeHelper.SpanInfo(notActiceText,
                        DensityUtils.sp2px(14),
                        ContextCompat.getColor(mContext, R.color.white),
                        ContextCompat.getColor(mContext, R.color.red),
                        DensityUtils.dp2px(2), true));
                helper.setText(R.id.tvName, TextColorSizeHelper.getTextSpan(mContext,
                        String.format("%s:%s %s",
                                mContext.getString(R.string.nickname), name, notActiceText), list));
            } else {
                helper.setText(R.id.tvName, String.format("%s:%s",
                        mContext.getString(R.string.nickname), name));
            }
            String online;
            float vol = 0;
            if (model.isOnlineStatus()) {
                online = mContext.getString(R.string.online);
            } else {
                online = mContext.getString(R.string.offline);
            }
            if (!TextUtils.isEmpty(model.getLastDeviceVol())) {
                try {
                    vol = Float.parseFloat(model.getLastDeviceVol());
                    if (vol < 0)
                        vol = 0;
                    else if (vol > 100)
                        vol = 100;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
            String onlineText = String.format(" %s %s:%.0f%% ", online,
                    mContext.getString(R.string.device_power), vol);
            list.add(new TextColorSizeHelper.SpanInfo(onlineText,
                    DensityUtils.sp2px(14),
                    ContextCompat.getColor(mContext, R.color.white),
                    ContextCompat.getColor(mContext, R.color.red),
                    DensityUtils.dp2px(2), true));
            helper.setText(R.id.tvType, TextColorSizeHelper.getTextSpan(mContext,
                    String.format("%s:%d %s",
                            mContext.getString(R.string.device_type), model.getDeviceType(),
                            onlineText), list));
            helper.getView(R.id.tvRingNo).setVisibility(View.GONE);
            helper.getView(R.id.tvLoseTime).setVisibility(View.GONE);
            helper.getView(R.id.tvRetrieveTime).setVisibility(View.GONE);
            helper.getView(R.id.tvRaceStatus).setVisibility(View.GONE);
            helper.getView(R.id.handleBtn).setVisibility(View.GONE);
        }
        if (type == 3 || type == 0) {
            helper.getView(R.id.tvLastGpsTime).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(model.getLastGpsTime())) {
                helper.setText(R.id.tvLastGpsTime,
                        mContext.getString(R.string.device_list_selector_last_gps_time, ""));
            } else {
                helper.setText(R.id.tvLastGpsTime,
                        mContext.getString(R.string.device_list_selector_last_gps_time,
                                model.getLastGpsTime()));
            }
        } else {
            helper.getView(R.id.tvLastGpsTime).setVisibility(View.GONE);
        }
        if (type == 3) {
            helper.getView(R.id.tvActivatedTime).setVisibility(View.GONE);
            helper.getView(R.id.tvExpireTime).setVisibility(View.GONE);
            helper.getView(R.id.tvGuaranteeTime).setVisibility(View.GONE);
            helper.getView(R.id.tvBindTime).setVisibility(View.VISIBLE);
            helper.getView(R.id.tvUnbindTime).setVisibility(View.VISIBLE);
            helper.getView(R.id.tvLoseTime).setVisibility(View.GONE);
            helper.getView(R.id.tvRetrieveTime).setVisibility(View.GONE);
            helper.getView(R.id.tvRemark).setVisibility(View.GONE);
            helper.getView(R.id.unbindBtn).setVisibility(View.GONE);
            helper.getView(R.id.handleBtn).setVisibility(View.GONE);
            helper.getView(R.id.activatedBtn).setVisibility(View.GONE);
            helper.getView(R.id.guaranteeBtn).setVisibility(View.GONE);
            helper.getView(R.id.expireBtn).setVisibility(View.GONE);
            helper.getView(R.id.detailsBtn).setVisibility(View.GONE);
            helper.getView(R.id.remarkBtn).setVisibility(View.GONE);
            helper.getView(R.id.tvPrompt).setVisibility(View.GONE);
            if (TextUtils.isEmpty(model.getBindDatetime())) {
                helper.setText(R.id.tvBindTime,
                        mContext.getString(R.string.device_list_selector_bind_time, ""));
            } else {
                helper.setText(R.id.tvBindTime,
                        mContext.getString(R.string.device_list_selector_bind_time,
                                model.getBindDatetime()));
            }
            if (TextUtils.isEmpty(model.getUnbindDatetime())) {
                helper.setText(R.id.tvUnbindTime,
                        mContext.getString(R.string.device_list_selector_unbind_time, ""));
            } else {
                helper.setText(R.id.tvUnbindTime,
                        mContext.getString(R.string.device_list_selector_unbind_time,
                                model.getUnbindDatetime()));
            }
        } else {
            if (type == 2) {
                helper.getView(R.id.tvActivatedTime).setVisibility(View.GONE);
                helper.getView(R.id.tvExpireTime).setVisibility(View.GONE);
                helper.getView(R.id.tvGuaranteeTime).setVisibility(View.GONE);
                helper.getView(R.id.tvBindTime).setVisibility(View.GONE);
                helper.getView(R.id.tvUnbindTime).setVisibility(View.GONE);
                helper.getView(R.id.tvLoseTime).setVisibility(View.VISIBLE);
                helper.getView(R.id.tvRetrieveTime).setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(model.getLoseCreatetime())) {
                    helper.setText(R.id.tvLoseTime,
                            mContext.getString(R.string.device_list_selector_lose_time, ""));
                } else {
                    helper.setText(R.id.tvLoseTime,
                            mContext.getString(R.string.device_list_selector_lose_time,
                                    model.getLoseCreatetime()));
                }
                if (TextUtils.isEmpty(model.getRetrieveCreatetime())) {
                    helper.setText(R.id.tvRetrieveTime,
                            mContext.getString(R.string.device_list_selector_retrieve_time, ""));
                } else {
                    helper.setText(R.id.tvRetrieveTime,
                            mContext.getString(R.string.device_list_selector_retrieve_time,
                                    model.getRetrieveCreatetime()));
                }
            } else {
                if (TextUtils.isEmpty(model.getActivatedDatetime())) {
                    helper.setText(R.id.tvActivatedTime, "");
                    helper.getView(R.id.tvActivatedTime).setVisibility(View.GONE);
                } else {
                    helper.setText(R.id.tvActivatedTime,
                            mContext.getString(R.string.device_list_selector_activated_time,
                                    model.getActivatedDatetime()));
                    helper.getView(R.id.tvActivatedTime).setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(model.getExpireDate())) {
                    helper.setText(R.id.tvExpireTime, "");
                    helper.getView(R.id.tvExpireTime).setVisibility(View.GONE);
                } else {
                    helper.setText(R.id.tvExpireTime,
                            mContext.getString(R.string.device_list_selector_expire_time,
                                    model.getExpireDate()));
                    helper.getView(R.id.tvExpireTime).setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(model.getGuaranteeDate())) {
                    helper.setText(R.id.tvGuaranteeTime, "");
                    helper.getView(R.id.tvGuaranteeTime).setVisibility(View.GONE);
                } else {
                    helper.setText(R.id.tvGuaranteeTime,
                            mContext.getString(R.string.device_list_selector_guarantee_time,
                                    model.getGuaranteeDate()));
                    helper.getView(R.id.tvGuaranteeTime).setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(model.getBindDatetime())) {
                    helper.setText(R.id.tvBindTime, "");
                    helper.getView(R.id.tvBindTime).setVisibility(View.GONE);
                } else {
                    helper.setText(R.id.tvBindTime,
                            mContext.getString(R.string.device_list_selector_bind_time,
                                    model.getBindDatetime()));
                    helper.getView(R.id.tvBindTime).setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(model.getUnbindDatetime())) {
                    helper.setText(R.id.tvUnbindTime, "");
                    helper.getView(R.id.tvUnbindTime).setVisibility(View.GONE);
                } else {
                    helper.setText(R.id.tvUnbindTime,
                            mContext.getString(R.string.device_list_selector_unbind_time,
                                    model.getUnbindDatetime()));
                    helper.getView(R.id.tvUnbindTime).setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(model.getLoseCreatetime())) {
                    helper.setText(R.id.tvLoseTime, "");
                    helper.getView(R.id.tvLoseTime).setVisibility(View.GONE);
                } else {
                    helper.setText(R.id.tvLoseTime,
                            mContext.getString(R.string.device_list_selector_lose_time,
                                    model.getLoseCreatetime()));
                    helper.getView(R.id.tvLoseTime).setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(model.getRetrieveCreatetime())) {
                    helper.setText(R.id.tvRetrieveTime, "");
                    helper.getView(R.id.tvRetrieveTime).setVisibility(View.GONE);
                } else {
                    helper.setText(R.id.tvRetrieveTime,
                            mContext.getString(R.string.device_list_selector_retrieve_time,
                                    model.getRetrieveCreatetime()));
                    helper.getView(R.id.tvRetrieveTime).setVisibility(View.VISIBLE);
                }
            }
            helper.setText(R.id.tvRemark, String.format("%s:%s",
                    mContext.getString(R.string.device_info_remark),
                    StringUtils.getNotNullText(model.getDeviceRemark())));
            helper.getView(R.id.detailsBtn).setVisibility(View.VISIBLE);
            if (DataUtils.isAgent() && !DataUtils.isDeviceLogin()) {
                helper.getView(R.id.unbindBtn).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.unbindBtn).setVisibility(View.GONE);
            }
            if (DataUtils.getPwdType() == 2 && !isActivated) {
                helper.getView(R.id.activatedBtn).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.activatedBtn).setVisibility(View.GONE);
            }
            if (DataUtils.getPwdType() == 2) {
                helper.getView(R.id.tvRemark).setVisibility(View.VISIBLE);
                helper.getView(R.id.remarkBtn).setVisibility(View.VISIBLE);
                helper.getView(R.id.expireBtn).setVisibility(View.VISIBLE);
                helper.getView(R.id.guaranteeBtn).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.tvRemark).setVisibility(View.GONE);
                helper.getView(R.id.remarkBtn).setVisibility(View.GONE);
                helper.getView(R.id.expireBtn).setVisibility(View.GONE);
                helper.getView(R.id.guaranteeBtn).setVisibility(View.GONE);
            }

            Date expireDate = null;
            if (!TextUtils.isEmpty(model.getExpireDate())) {
                expireDate = TimeUtils.formatUTC(model.getExpireDate(), "yyyy-MM-dd");
            }
            if (expireDate == null || expireDate.getTime() - System.currentTimeMillis() < TimeUtils.DAY * -1000L) {
                helper.getView(R.id.tvPrompt).setVisibility(View.VISIBLE);
                helper.setText(R.id.tvPrompt, R.string.device_list_expire_date_pass_prompt);
            } else if (expireDate.getTime() - System.currentTimeMillis() < TimeUtils.MONTH * 1000L) {
                helper.getView(R.id.tvPrompt).setVisibility(View.VISIBLE);
                helper.setText(R.id.tvPrompt, R.string.device_list_expire_date_prompt);
            } else {
                helper.getView(R.id.tvPrompt).setVisibility(View.GONE);
            }
        }

        if (helper.getView(R.id.handleBtn).getVisibility() == View.VISIBLE
                || helper.getView(R.id.activatedBtn).getVisibility() == View.VISIBLE
                || helper.getView(R.id.guaranteeBtn).getVisibility() == View.VISIBLE
                || helper.getView(R.id.expireBtn).getVisibility() == View.VISIBLE) {
            helper.setText(R.id.handleBtn, R.string.operation);
            helper.getView(R.id.handleBtn).setVisibility(View.VISIBLE);
        }
        helper.getView(R.id.activatedBtn).setVisibility(View.GONE);
        helper.getView(R.id.guaranteeBtn).setVisibility(View.GONE);
        helper.getView(R.id.expireBtn).setVisibility(View.GONE);

        AAADeviceModel deviceModel = MainApplication.getInstance().getTrackDeviceModel();
        helper.getView(R.id.ivSelect).setSelected(deviceModel != null && deviceModel.getDeviceId() == model.getDeviceId());
        helper.addOnClickListener(R.id.unbindBtn, R.id.handleBtn, R.id.activatedBtn,
                R.id.expireBtn, R.id.guaranteeBtn, R.id.detailsBtn, R.id.remarkBtn);
    }

    public void setType(int type) {
        this.type = type;
    }
}

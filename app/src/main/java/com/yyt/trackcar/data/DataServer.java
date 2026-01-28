package com.yyt.trackcar.data;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.HomeMultiItemBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.bean.SectionMultiItem;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.DeviceType;
import com.yyt.trackcar.utils.SettingSPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.data
 * @ fileName:      DataServer
 * @ author:        QING
 * @ createTime:    2020/3/5 16:58
 * @ describe:      TODO 数据服务
 */
public class DataServer {

    /**
     * 获取更多的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getModeData(Context context, List<SectionItem> itemList) {
        itemList.clear();
        itemList.add(new SectionItem(true, null));
        BaseItemBean itemBean;
//        BaseItemBean itemBean = new BaseItemBean(CWConstant.MORE_UPDATE_FIRMWARE,
//                context.getString(R.string.update_device_firmware), R.mipmap.ic_update_firmware);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        BaseItemBean itemBean = new BaseItemBean(CWConstant.MORE_BIND_MEMBER,
//                context.getString(R.string.bind_member), R.mipmap.ic_bind_member);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
//            itemBean = new BaseItemBean(CWConstant.MORE_ADDRESS_BOOK,
//                    context.getString(R.string.address_book), R.mipmap.ic_address_book);
//            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//            itemList.add(new SectionItem(itemBean));
//        } else {
//            itemBean = new BaseItemBean(CWConstant.MORE_ADDRESS_BOOK,
//                    context.getString(R.string.device_address_book), R.mipmap.ic_address_book);
//            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//            itemList.add(new SectionItem(itemBean));
//        }
//
//        itemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(CWConstant.MORE_APP_STORE,
//                context.getString(R.string.app_store), R.mipmap.ic_app_store);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_APP_MANAGER,
//                context.getString(R.string.app_manager), R.mipmap.ic_app_manager)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_ACTIVITY_SQUARE,
//                context.getString(R.string.activity_square), R.mipmap.ic_activity_square)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_PHOTO_DIAL,
//                context.getString(R.string.photo_dial), R.mipmap.ic_photo_dial)));
//        itemBean = new BaseItemBean(CWConstant.MORE_SMS_COLLECTION,
//                context.getString(R.string.sms_collection), R.mipmap.ic_sms_collection);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(CWConstant.MORE_WATCH_BILL,
//                context.getString(R.string.watch_bill), R.mipmap.ic_watch_bill);
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemList.add(new SectionItem(itemBean));

//        itemList.add(new SectionItem(true, null));
//        DeviceModel deviceModel = MainApplication.getInstance().getDeviceModel();
//        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
//            itemBean = new BaseItemBean(CWConstant.MORE_SEDENTARY_REMINDER,
//                    context.getString(R.string.health_settings), R.mipmap.ic_sedentary_reminder);
//            itemList.add(new SectionItem(itemBean));
//        } else {
//            itemBean = new BaseItemBean(CWConstant.MORE_BAN_CLASSES,
//                    context.getString(R.string.ban_classes), R.mipmap.ic_ban_classes);
//            itemList.add(new SectionItem(itemBean));
//        }
//        if (deviceModel.getDevice_type() == CWConstant.DEVICE_TYPE_S9)
//            itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_FALL_OFF,
//                    context.getString(R.string.fall_off), R.mipmap.ic_fall_off)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_REFUSE_STRANGERS,
//                context.getString(R.string.refuse_strangers), R.mipmap.ic_refuse_strangers)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_SCHOOL_GUARDIAN,
//                context.getString(R.string.school_guardian), R.mipmap.ic_school_guradian)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_HOLIDAY_WATCH,
//                context.getString(R.string.holiday_watch), R.mipmap.ic_holiday_watch)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_TIME_SWITCH,
//                context.getString(R.string.time_switch), R.mipmap.ic_time_switch)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_AUTOMATIC_CONNECTION,
//                context.getString(R.string.automatic_connection), R.mipmap.ic_auto_connect)));
//        itemBean = new BaseItemBean(CWConstant.MORE_RESERVED_ELECTRIC,
//                context.getString(R.string.reserved_electric), R.mipmap.ic_reserved_electric);
//        itemBean = new BaseItemBean(CWConstant.MORE_WATER_REMINDER,
//                context.getString(R.string.water_reminder), R.mipmap.ic_water_reminder);
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemList.add(new SectionItem(itemBean));
//
//        itemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(CWConstant.MORE_DEVICE_WIFI,
//                context.getString(R.string.device_wifi), R.mipmap.ic_device_wifi);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(CWConstant.MORE_FENCE,
//                context.getString(R.string.fence), R.mipmap.ic_fence);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_FENCE,
//                context.getString(R.string.fence), R.mipmap.ic_fence)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_CALL_RECORD,
//                context.getString(R.string.call_record), R.mipmap.ic_change_number)));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_DEIVCE_SETTINGS,
//                context.getString(R.string.device_settings), R.mipmap.ic_device_settings)));
//        itemBean = new BaseItemBean(CWConstant.MORE_DEIVCE_REPORT_LOSS,
//                context.getString(R.string.device_report_loss), R.mipmap.ic_device_report_loss);
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemList.add(new SectionItem(itemBean));

//        itemList.add(new SectionItem(true, null));
//        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
//            itemBean = new BaseItemBean(CWConstant.MORE_ALARM_CLOCK,
//                    context.getString(R.string.alarm_and_prompt), R.mipmap.ic_alarm_clock);
//            itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//            itemList.add(new SectionItem(itemBean));
//        } else {
//            itemBean = new BaseItemBean(CWConstant.MORE_ALARM_CLOCK,
//                    context.getString(R.string.alarm_clock_settings), R.mipmap.ic_alarm_clock);
//            itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//            itemList.add(new SectionItem(itemBean));
//        }
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_FIND_WATCH,
//                context.getString(R.string.find_device), R.mipmap.ic_change_device_assistant)));
//
//        if (deviceModel.getDevice_type() == CWConstant.DEVICE_TYPE_S9) {
//            itemBean = new BaseItemBean(CWConstant.MORE_VOICE_MONITOR,
//                    context.getString(R.string.voice_monitor), R.mipmap.ic_my_service);
//            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//            itemList.add(new SectionItem(itemBean));
//        } else {
//            itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_VOICE_MONITOR,
//                    context.getString(R.string.voice_monitor), R.mipmap.ic_my_service)));
//            itemBean = new BaseItemBean(CWConstant.MORE_REMOTE_PHOTO_TAKE,
//                    context.getString(R.string.remote_photo_take), R.mipmap.ic_remote_photo_take);
//            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//            itemList.add(new SectionItem(itemBean));
//        }
//
//        itemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(CWConstant.MORE_CHANGE_DEIVCE,
//                context.getString(R.string.change_device_assistant),
//                R.mipmap.ic_change_device_assistant);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(CWConstant.MORE_CHANGE_NUMBER,
//                context.getString(R.string.change_number), R.mipmap.ic_change_number);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        itemList.add(new SectionItem(new BaseItemBean(CWConstant.MORE_MY_SERVICE,
//                context.getString(R.string.my_service), R.mipmap.ic_my_service)));
        itemBean = new BaseItemBean(CWConstant.MORE_APP_SETTINGS,
                context.getString(R.string.app_settings), R.mipmap.ic_app_settings);
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemList.add(new SectionItem(itemBean));

//        itemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(CWConstant.MORE_BLUE_TOOTH,
//                context.getString(R.string.blue_tooth), R.mipmap.ic_device_report_loss);
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(CWConstant.ONLINE_UPDATE,
                context.getString(R.string.app_version_update), R.mipmap.ic_settings_update);
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemList.add(new SectionItem(itemBean));

        if (!DataUtils.isDeviceLogin() && DataUtils.isAgent()) {  //帐号登录时添加解绑功能
            itemList.add(new SectionItem(true, null));
            itemBean = new BaseItemBean(CWConstant.MORE_BIND_AND_UNBIND,
                    context.getString(R.string.bind_and_unbind), R.mipmap.ic_bind_and_unbind);
            itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
            itemList.add(new SectionItem(itemBean));
        }

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(CWConstant.MORE_MESSAGE_CENTER,
                context.getString(R.string.message_center), R.mipmap.ic_change_number);
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(CWConstant.MORE_BLUE_TOOTH,
                context.getString(R.string.blue_tooth), R.mipmap.ic_activity_square);
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(CWConstant.PRECAUTIONS,
                context.getString(R.string.precautions), R.mipmap.electronic_normal);
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(CWConstant.LOGIN_OUT,
                context.getString(R.string.login_out), R.mipmap.ic_power_off);
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
    }

    /**
     * 获取更多的弹出菜单
     *
     * @param context 上下文
     */
    public static AdapterItem[] getModeMenuData(Context context) {
        if (DataUtils.isDeviceLogin()) { //
            return new AdapterItem[]{
                    new AdapterItem(context.getString(R.string.switch_device_new),
                            R.mipmap.ic_change_device),
                    new AdapterItem(context.getString(R.string.device_qrcode_new),
                            R.mipmap.ic_device_qrcode)
            };
        } else if (DataUtils.isAgent()) {
            return new AdapterItem[]{
                    new AdapterItem(context.getString(R.string.switch_device_new),
                            R.mipmap.ic_change_device),
                    new AdapterItem(context.getString(R.string.device_qrcode_new),
                            R.mipmap.ic_device_qrcode),
                    new AdapterItem(context.getString(R.string.add_device_new),
                            R.mipmap.ic_add_device),
                    new AdapterItem(context.getString(R.string.unbind_device_new),
                            R.mipmap.ic_unbind_device)
            };
        } else {
            return new AdapterItem[]{
                    new AdapterItem(context.getString(R.string.switch_device_new),
                            R.mipmap.ic_change_device),
                    new AdapterItem(context.getString(R.string.device_qrcode_new),
                            R.mipmap.ic_device_qrcode),
                    new AdapterItem(context.getString(R.string.add_device_new),
                            R.mipmap.ic_add_device)
            };
        }
    }

    /**
     * 获取设置的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getSettingsData(Context context, List<SectionItem> itemList) {
        itemList.clear();
        itemList.add(new SectionItem(true, context.getString(R.string.settings_close_prompt)));
//        BaseItemBean itemBean = new BaseItemBean(0,"到家", R.mipmap.ic_watch_setting_change);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        BaseItemBean itemBean = new BaseItemBean(1, context.getString(R.string.sos),
//                R.mipmap.ic_sos);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        BaseItemBean itemBean = new BaseItemBean(2, context.getString(R.string.location),
//                R.mipmap.ic_settings_location);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemList.add(new SectionItem(itemBean));
//        itemList.add(new SectionItem(new BaseItemBean(3,
//                "添加朋友",
//                R.mipmap.ic_watch_electric)));
//        itemList.add(new SectionItem(new BaseItemBean(4,
//                "计步",
//                R.mipmap.ic_entry_reserved_electric)));
        BaseItemBean itemBean;
        DeviceModel deviceModel = MainApplication.getInstance().getDeviceModel();
        if (deviceModel != null && deviceModel.getDevice_type() == CWConstant.DEVICE_TYPE_S9) {
            itemBean = new BaseItemBean(6,
                    context.getString(R.string.call_record_log), R.mipmap.ic_call_record_log);
            itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
            itemList.add(new SectionItem(itemBean));
        } else {
            itemBean = new BaseItemBean(5, context.getString(R.string.upload_photo)
                    , R.mipmap.ic_upload_photo);
            itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
            itemList.add(new SectionItem(itemBean));
            itemList.add(new SectionItem(new BaseItemBean(6,
                    context.getString(R.string.call_record_log), R.mipmap.ic_call_record_log)));
        }
//        itemList.add(new SectionItem(new BaseItemBean(7,
//                "短信",
//                R.mipmap.ic_settings_sport)));
        itemList.add(new SectionItem(new BaseItemBean(8,
                context.getString(R.string.update_firmware), R.mipmap.ic_settings_update)));
        itemBean = new BaseItemBean(9, context.getString(R.string.fence),
                R.mipmap.ic_settings_fence);
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
    }

    /**
     * 获取定位设置的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getLocationSettingsData(Context context, List<SectionItem> itemList) {
        itemList.clear();
        itemList.add(new SectionItem(true, null));
//        BaseItemBean itemBean = new BaseItemBean(0,
//                context.getString(R.string.common_location));
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));

//        itemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(1,
//                context.getString(R.string.indoor_map));
//        itemBean.setContent(context.getString(R.string.is_open));
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));

//        BaseItemBean itemBean = new BaseItemBean(3,
//                context.getString(R.string.fence));
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));
//
//        itemList.add(new SectionItem(true, null));
        BaseItemBean itemBean = new BaseItemBean(2,
                context.getString(R.string.locate_mode_explain));
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
    }

    /**
     * 获取定位方式的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getLocateModeExplainData(Context context, List<BaseItemBean> itemList) {
        itemList.clear();
        itemList.add(new BaseItemBean(0, context.getString(R.string.locate_mode_satellite),
                context.getString(R.string.locate_mode_satellite_content), R.mipmap.ic_satellite));
        itemList.add(new BaseItemBean(1, context.getString(R.string.locate_mode_wifi),
                context.getString(R.string.locate_mode_wifi_content),
                R.mipmap.ic_locate_mode_wifi));
        itemList.add(new BaseItemBean(2, context.getString(R.string.locate_mode_fuzzy),
                context.getString(R.string.locate_mode_fuzzy_content),
                R.mipmap.ic_locate_mode_lbs));
        itemList.add(new BaseItemBean(3, context.getString(R.string.locate_mode_agps),
                context.getString(R.string.locate_mode_agps_content), R.mipmap.ic_locate_mode_gps));
        itemList.add(new BaseItemBean(4, context.getString(R.string.acceleration_sensor),
                context.getString(R.string.acceleration_sensor_content),
                R.mipmap.ic_locate_mode_acceleration_sensor));
    }

    /**
     * 获取选择名称的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getSelectNameData(Context context, List<SectionItem> itemList) {
        itemList.clear();
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
            itemList.add(new SectionItem(true, null));
            itemList.add(new SectionItem(new BaseItemBean(1,
                    context.getString(R.string.name_type_twelfth),
                    R.mipmap.ic_name_type_eleventh)));
            itemList.add(new SectionItem(new BaseItemBean(2,
                    context.getString(R.string.name_type_thirteenth),
                    R.mipmap.ic_name_type_twelfth)));
            itemList.add(new SectionItem(new BaseItemBean(3,
                    context.getString(R.string.name_type_fourteenth),
                    R.mipmap.ic_name_type_thirteenth)));
            itemList.add(new SectionItem(new BaseItemBean(4,
                    context.getString(R.string.name_type_fifteenth),
                    R.mipmap.ic_name_type_fourteenth)));
            itemList.add(new SectionItem(new BaseItemBean(5,
                    context.getString(R.string.name_type_sixteenth),
                    R.mipmap.ic_name_type_fifteenth)));
            itemList.add(new SectionItem(new BaseItemBean(0,
                    context.getString(R.string.name_type_tenth),
                    R.mipmap.ic_default_pigeon_marker)));
            itemList.add(new SectionItem(true, null));
        } else {
            itemList.add(new SectionItem(true, null));
            itemList.add(new SectionItem(new BaseItemBean(1,
                    context.getString(R.string.name_type_first),
                    R.mipmap.ic_name_type_first)));
            itemList.add(new SectionItem(new BaseItemBean(2,
                    context.getString(R.string.name_type_second),
                    R.mipmap.ic_name_type_second)));
//        itemList.add(new SectionItem(new BaseItemBean(2,
//                context.getString(R.string.name_type_third),
//                R.mipmap.ic_name_type_third)));
            itemList.add(new SectionItem(new BaseItemBean(3,
                    context.getString(R.string.name_type_fourth),
                    R.mipmap.ic_name_type_fourth)));
            itemList.add(new SectionItem(new BaseItemBean(4,
                    context.getString(R.string.name_type_fifth),
                    R.mipmap.ic_name_type_fifth)));
//        itemList.add(new SectionItem(new BaseItemBean(5,
//                context.getString(R.string.name_type_sixth),
//                R.mipmap.ic_name_type_sixth)));
            itemList.add(new SectionItem(new BaseItemBean(5,
                    context.getString(R.string.name_type_seventh),
                    R.mipmap.ic_name_type_seventh)));
            itemList.add(new SectionItem(new BaseItemBean(6,
                    context.getString(R.string.name_type_eighth),
                    R.mipmap.ic_name_type_eighth)));
//        itemList.add(new SectionItem(new BaseItemBean(8,
//                context.getString(R.string.name_type_ninth),
//                R.mipmap.ic_name_type_ninth)));
            itemList.add(new SectionItem(new BaseItemBean(0,
                    context.getString(R.string.name_type_tenth),
                    R.mipmap.ic_default_pigeon_marker)));
            itemList.add(new SectionItem(true, null));
        }
    }

    /**
     * 获取App设置的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getAppSettingsData(Context context, List<SectionItem> itemList) {
        itemList.clear();
        itemList.add(new SectionItem(true, null));


        BaseItemBean itemBean = new BaseItemBean(7,
                context.getString(R.string.location_refresh_interval));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));

        itemBean = new BaseItemBean(0, context.getString(R.string.message_notify));
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(6,
//                context.getString(R.string.language));
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(5,
//                context.getString(R.string.app_server_select));
//        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(1,
                context.getString(R.string.change_pwd));
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);

        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(2,
                context.getString(R.string.clear_cache));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(4, context.getString(R.string.about));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(8, context.getString(R.string.log_off));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);

        itemList.add(new SectionItem(itemBean));
    }

    /**
     * 获取关于的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getAboutData(Context context, List<SectionItem> itemList) {
        itemList.clear();
        BaseItemBean itemBean = new BaseItemBean(0,
                context.getString(R.string.about_user_service));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(1,
                context.getString(R.string.about_privacy_protocol));
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(2,
//                context.getString(R.string.about_user_experience));
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));
        itemList.add(new SectionItem(true, null));
    }

    /**
     * 获取个人中心的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getPersonalCenterData(Context context, List<SectionItem> itemList) {
        itemList.clear();

        itemList.add(new SectionItem(true, null));
        BaseItemBean itemBean = new BaseItemBean(0,
                context.getString(R.string.phone_number));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(1, context.getString(R.string.device_qrcode_new));
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        itemBean.setImgDrawable(R.mipmap.ic_device_qrcode);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(2, context.getString(R.string.device_info));
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));

//        itemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(3,
//                context.getString(R.string.baby_short_numer));
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));
    }

    /**
     * 获取宝贝信息的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getBabyInfoData(Context context, List<SectionItem> itemList) {
        itemList.clear();

        itemList.add(new SectionItem(true, null));
        BaseItemBean itemBean = new BaseItemBean(0,
                context.getString(R.string.portrait));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        int imgRes = R.mipmap.ic_default_pigeon_marker;
        itemBean.setImgDrawable(imgRes);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(1, context.getString(R.string.device_name));
        itemBean.setHasArrow(true);

        if (DataUtils.getPwdType() == 2) {
            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
            itemList.add(new SectionItem(itemBean));
            itemBean = new BaseItemBean(2, context.getString(R.string.device_info_remark));
            itemBean.setHasArrow(true);
            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
            itemList.add(new SectionItem(itemBean));
        } else {
            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
            itemList.add(new SectionItem(itemBean));
        }

//        itemBean = new BaseItemBean(2, context.getString(R.string.baby_level));
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));

//        itemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(3, context.getString(R.string.sex));
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(4, context.getString(R.string.birthday));
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));
////        itemBean = new BaseItemBean(5, context.getString(R.string.school));
////        itemBean.setHasArrow(true);
////        itemList.add(new SectionItem(itemBean));
//        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
//            itemBean = new BaseItemBean(10, context.getString(R.string.height));
//            itemBean.setHasArrow(true);
//            itemList.add(new SectionItem(itemBean));
//            itemBean = new BaseItemBean(11, context.getString(R.string.weight));
//            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//            itemBean.setHasArrow(true);
//            itemList.add(new SectionItem(itemBean));
//        } else {
//            itemBean = new BaseItemBean(6, context.getString(R.string.grade));
//            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//            itemBean.setHasArrow(true);
//            itemList.add(new SectionItem(itemBean));
//        }
////        itemBean = new BaseItemBean(7, context.getString(R.string.classes));
////        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
////        itemBean.setHasArrow(true);
////        itemList.add(new SectionItem(itemBean));
//
//        itemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(8, context.getString(R.string.family_address));
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(9, context.getString(R.string.family_wifi));
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionItem(itemBean));
//
        itemList.add(new SectionItem(true, null));
    }

    /**
     * 获取定位设置的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getCommonLocationData(Context context, List<SectionItem> itemList) {
        itemList.clear();
        itemList.add(new SectionItem(true, null));
        BaseItemBean itemBean = new BaseItemBean(0, context.getString(R.string.family),
                R.mipmap.ic_home);
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(1, context.getString(R.string.school),
                R.mipmap.ic_school);
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));
    }

    /**
     * 获取更多的弹出菜单
     *
     * @param context 上下文
     */
    public static AdapterItem[] getCommonLocationMenuData(Context context) {
        return new AdapterItem[]{
                new AdapterItem(context.getString(R.string.common_location_first)),
                new AdapterItem(context.getString(R.string.common_location_second)),
                new AdapterItem(context.getString(R.string.common_location_third)),
                new AdapterItem(context.getString(R.string.customize))
        };
    }

    /**
     * 获取宝贝通讯录的弹出菜单
     *
     * @param context 上下文
     */
    public static AdapterItem[] getAddressBookMenuData(Context context) {
        return new AdapterItem[]{
                new AdapterItem(context.getString(R.string.address_book_add),
                        R.mipmap.ic_add_contacts),
//                new AdapterItem(context.getString(R.string.add_sos_number),
//                        R.mipmap.ic_add_contacts),
                new AdapterItem(context.getString(R.string.address_book_sort),
                        R.mipmap.ic_sort)
        };
    }

    /**
     * 获取通讯录详情的列表
     *
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getContactsDetailsData(Context context, List<SectionMultiItem> itemList) {
        itemList.clear();

        itemList.add(new SectionMultiItem(true, null));
        BaseItemBean itemBean = new BaseItemBean(0,
                context.getString(R.string.portrait));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionMultiItem(1, itemBean));
        itemBean = new BaseItemBean(1, context.getString(R.string.name));
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        itemList.add(new SectionMultiItem(1, itemBean));

        itemList.add(new SectionMultiItem(true, null));
        itemBean = new BaseItemBean(2, context.getString(R.string.phone_number));
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
        itemList.add(new SectionMultiItem(1, itemBean));
//        itemBean = new BaseItemBean(3, context.getString(R.string.short_number));
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemBean.setHasArrow(true);
//        itemList.add(new SectionMultiItem(1, itemBean));

        itemList.add(new SectionMultiItem(0, new BaseItemBean(-1,
                context.getString(R.string.guard_permission))));
        itemBean = new BaseItemBean(4, context.getString(R.string.emergency_call),
                context.getString(R.string.emergency_call_content));
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemList.add(new SectionMultiItem(2, itemBean));

        itemList.add(new SectionMultiItem(true, null));
    }

    /**
     * 获取性别列表
     *
     * @param context 上下文
     */
    public static String[] getSexArray(Context context) {
        return new String[]{context.getString(R.string.male), context.getString(R.string.female)};
    }

    /**
     * 获取性别列表
     *
     * @param context 上下文
     */
    public static String[] getGradeArray(Context context) {
        return new String[]{context.getString(R.string.grade_type_first),
                context.getString(R.string.grade_type_second),
                context.getString(R.string.grade_type_third),
                context.getString(R.string.grade_type_fourth),
                context.getString(R.string.grade_type_fifth),
                context.getString(R.string.grade_type_sixth),
                context.getString(R.string.grade_type_seventh),
                context.getString(R.string.grade_type_eighth),
                context.getString(R.string.grade_type_ninth),
                context.getString(R.string.grade_type_tenth),
                context.getString(R.string.grade_type_eleventh),
                context.getString(R.string.grade_type_twelfth),
                context.getString(R.string.grade_type_thirteenth),
                context.getString(R.string.grade_type_seventeenth),
                context.getString(R.string.grade_type_fourteenth),
                context.getString(R.string.grade_type_fifteenth),
                context.getString(R.string.grade_type_sixteenth),
                context.getString(R.string.others)};
    }

    /**
     * 获取发现的列表
     *
     * @param itemList 列表
     */
    public static void getFindData(List<BaseItemBean> itemList) {
        itemList.clear();
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_first));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_second));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_third));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_fourth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_fifth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_sixth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_seventh));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_eighth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_ninth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_tenth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_eleventh));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_twelfth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_thirteenth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_fourteenth));
        itemList.add(new BaseItemBean(null, R.mipmap.ic_instructions_fifteenth));
    }

    /**
     * @param context  上下文
     * @param itemList 列表
     */
    public static void getHealthSettingsData(Context context, List<SectionItem> itemList) {
        itemList.clear();
        itemList.add(new SectionItem(true, null));

        BaseItemBean itemBean = new BaseItemBean(0,
                context.getString(R.string.sedentary_reminder));
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
        itemList.add(new SectionItem(itemBean));

        itemList.add(new SectionItem(true, null));

        DeviceModel deviceModel = MainApplication.getInstance().getDeviceModel();
        if (deviceModel != null && deviceModel.getDevice_type() == CWConstant.DEVICE_TYPE_S9) {
            itemBean = new BaseItemBean(1,
                    context.getString(R.string.heart_rate_continue_test));
            itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
            itemBean.setHasArrow(true);
            itemList.add(new SectionItem(itemBean));

            itemList.add(new SectionItem(true, null));
        }
    }

    /**
     * 获取主页选项的列表
     *
     * @param context  上下文
     * @param type     设备类型
     * @param itemList 列表
     */
    public static void getHomeItemData(Context context, int type,
                                       List<HomeMultiItemBean> itemList) {
        itemList.clear();
        int pwdType = DataUtils.getPwdType();
        boolean isAgent = DataUtils.isAgent();
        HomeMultiItemBean model;
        List<BaseItemBean> list = new ArrayList<>();
        if (DeviceType.PIGEON.getValue() == type) {
            list.add(getHomeItemModel(context, CWConstant.HOME_MAP_CENTER));
            list.add(getHomeItemModel(context, CWConstant.HOME_TRACK_PLAYBACK));
            model = new HomeMultiItemBean(list, 2);
            itemList.add(model);

            list = new ArrayList<>();
            list.add(getHomeItemModel(context, CWConstant.HOME_SAFE_ZONE));
            list.add(getHomeItemModel(context, CWConstant.HOME_TRAIN_FLIGHT));
            model = new HomeMultiItemBean(list, 3);
            itemList.add(model);

//            list = new ArrayList<>();
//            list.add(getHomeItemModel(context, CWConstant.HOME_BLUE_TOOTH));
//            model = new HomeMultiItemBean(list, 0);
//            itemList.add(model);

            if (isAgent) {
                list = new ArrayList<>();
                list.add(getHomeItemModel(context, CWConstant.HOME_PRESET_PARAMS));
                list.add(getHomeItemModel(context, CWConstant.HOME_TRAJECTORY_ANALYSIS));
                list.add(getHomeItemModel(context, CWConstant.HOME_SEARCH_DEVICE));
                model = new HomeMultiItemBean(list, 4);
                itemList.add(model);

                list = new ArrayList<>();
                list.add(getHomeItemModel(context, CWConstant.HOME_DISTRIBUTION_MANAGEMENT));
                model = new HomeMultiItemBean(list, 0);
                itemList.add(model);
            } else {
                list = new ArrayList<>();
                list.add(getHomeItemModel(context, CWConstant.HOME_PRESET_PARAMS));
                list.add(getHomeItemModel(context, CWConstant.HOME_TRAJECTORY_ANALYSIS));
                model = new HomeMultiItemBean(list, 1);
                itemList.add(model);
            }
        } else if (DeviceType.PET.getValue() == type) {
            list.add(getHomeItemModel(context, CWConstant.HOME_MAP_CENTER));
            list.add(getHomeItemModel(context, CWConstant.HOME_TRACK));
            model = new HomeMultiItemBean(list, 2);
            itemList.add(model);

            list = new ArrayList<>();
            list.add(getHomeItemModel(context, CWConstant.HOME_SAFE_ZONE));
            list.add(getHomeItemModel(context, CWConstant.HOME_SEND_ORDER_PET));

//            list = new ArrayList<>();
//            list.add(getHomeItemModel(context, CWConstant.HOME_BLUE_TOOTH));
//            model = new HomeMultiItemBean(list, 0);
//            itemList.add(model);

            if (isAgent) {
                model = new HomeMultiItemBean(list, 3);
                itemList.add(model);

                list = new ArrayList<>();
                list.add(getHomeItemModel(context, CWConstant.HOME_SEND_COMMAND_LIST));
                list.add(getHomeItemModel(context, CWConstant.HOME_DISTRIBUTION_MANAGEMENT));
                model = new HomeMultiItemBean(list, 1);
                itemList.add(model);
            } else {
                list.add(getHomeItemModel(context, CWConstant.HOME_SEND_COMMAND_LIST));
                model = new HomeMultiItemBean(list, 4);
                itemList.add(model);
            }
        } else {
            list.add(getHomeItemModel(context, CWConstant.HOME_MAP_CENTER));
            list.add(getHomeItemModel(context, CWConstant.HOME_TRACK));
            model = new HomeMultiItemBean(list, 2);
            itemList.add(model);

            list = new ArrayList<>();
            list.add(getHomeItemModel(context, CWConstant.HOME_SAFE_ZONE));
            list.add(getHomeItemModel(context, CWConstant.HOME_SEND_ORDER));
            model = new HomeMultiItemBean(list, 3);
            itemList.add(model);

//            list = new ArrayList<>();
//            list.add(getHomeItemModel(context, CWConstant.HOME_BLUE_TOOTH));
//            model = new HomeMultiItemBean(list, 0);
//            itemList.add(model);

            list = new ArrayList<>();
            list.add(getHomeItemModel(context, CWConstant.HOME_TRIP_REPORT));
            list.add(getHomeItemModel(context, CWConstant.HOME_SUMMARY_RECORD));
            list.add(getHomeItemModel(context, CWConstant.HOME_DEVICE_SETTING));
            model = new HomeMultiItemBean(list, 4);
            itemList.add(model);

            if (isAgent) {
                list = new ArrayList<>();
                list.add(getHomeItemModel(context, CWConstant.HOME_DISTRIBUTION_MANAGEMENT));
                model = new HomeMultiItemBean(list, 0);
                itemList.add(model);
            }
        }

        if (pwdType == 2) {
            list = new ArrayList<>();
            list.add(getHomeItemModel(context, CWConstant.HOME_SYSTEM_CONFIGURE));
            model = new HomeMultiItemBean(list, 0);
            itemList.add(model);
        }
    }

    /**
     * 获取主页选项对象
     *
     * @param context 上下文
     * @param type    选项类型
     * @return 选项对象
     */
    private static BaseItemBean getHomeItemModel(Context context, int type) {
        BaseItemBean itemModel;
        switch (type) {
            case CWConstant.HOME_TRACK_PLAYBACK: // 轨迹回放
            case CWConstant.HOME_TRACK:
                itemModel = new BaseItemBean(type, context.getString(R.string.home_history),
                        R.drawable.home_history, ContextCompat.getColor(context,
                        R.color.darkseagreen));
                break;
            case CWConstant.HOME_SAFE_ZONE: // 电子围栏
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.home_electronic), R.drawable.home_electronic,
                        ContextCompat.getColor(context, R.color.mediumseagreen));
                break;
            case CWConstant.HOME_TRAIN_FLIGHT: // 训飞
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.flight_training),
                        R.drawable.home_flight_training_plan,
                        ContextCompat.getColor(context, R.color.steelblue));
                break;
            case CWConstant.HOME_PRESET_PARAMS: // 预约设定
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.appointment_parameter),
                        R.drawable.home_report_stop, ContextCompat.getColor(context, R.color.peru));
                break;
            case CWConstant.HOME_TRAJECTORY_ANALYSIS: // 轨迹分析
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.trajectory_analysis),
                        R.drawable.home_command,
                        ContextCompat.getColor(context, R.color.lightseagreen));
                break;
            case CWConstant.HOME_SEARCH_DEVICE: // 搜索设备
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.search_device), R.drawable.home_search,
                        ContextCompat.getColor(context, R.color.mediumpurple));
                break;
            case CWConstant.HOME_DISTRIBUTION_MANAGEMENT: // 经销管理
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.dealer_manage), R.drawable.home_dealer,
                        ContextCompat.getColor(context, R.color.lightsteelblue));
                break;
            case CWConstant.HOME_SEND_ORDER: // 发送指令
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.home_send_order), R.drawable.home_send_order,
                        ContextCompat.getColor(context, R.color.steelblue));
                break;
            case CWConstant.HOME_SEND_ORDER_PET:  // 发送指令
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.settings), R.drawable.home_send_order,
                        ContextCompat.getColor(context, R.color.steelblue));
                break;
            case CWConstant.HOME_TRIP_REPORT: // 旅程报表
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.home_trip_report), R.drawable.home_report_stop,
                        ContextCompat.getColor(context, R.color.peru));
                break;
            case CWConstant.HOME_SUMMARY_RECORD: // 历史记录
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.home_summary_record),
                        R.drawable.home_report_mileage,
                        ContextCompat.getColor(context, R.color.deepskyblue));
                break;
            case CWConstant.HOME_DEVICE_SETTING: // 设备参数
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.home_device_setting), R.drawable.home_command,
                        ContextCompat.getColor(context, R.color.olive));
                break;
            case CWConstant.HOME_SYSTEM_CONFIGURE: // 系统工具
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.home_system_configure), R.drawable.home_safe,
                        ContextCompat.getColor(context, R.color.fuchsia));
                break;
            case CWConstant.HOME_SEND_COMMAND_LIST: // 发送指令列表
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.send_command_message),
                        R.drawable.home_report_stop,
                        ContextCompat.getColor(context, R.color.plum));
                break;
            case CWConstant.HOME_BLUE_TOOTH: // 蓝牙连接
                itemModel = new BaseItemBean(type,
                        context.getString(R.string.blue_tooth),
                        R.drawable.home_dealer,
                        ContextCompat.getColor(context, R.color.royalblue));
                break;
            default: // 地图中心
                itemModel = new BaseItemBean(CWConstant.HOME_MAP_CENTER,
                        context.getString(R.string.map_center), R.drawable.home_tracking,
                        ContextCompat.getColor(context, R.color.blueviolet));
                break;
        }
        return itemModel;
    }

}

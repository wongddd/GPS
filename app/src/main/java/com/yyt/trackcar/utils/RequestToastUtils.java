package com.yyt.trackcar.utils;

import android.content.Context;

import com.yyt.trackcar.R;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      RequestToastUtils
 * @ author:        QING
 * @ createTime:    2020-02-25 23:07
 * @ describe:      TODO 请求结果提示类
 */
public class RequestToastUtils {

    /**
     * 请求结果提示
     *
     * @param code 请求结果
     */
    public static void toast(Context context, int code) {
        switch (code) {
            case CWConstant.SUCCESS: //成功
                XToastUtils.toast(context, R.string.request_success_prompt);
                break;
            case CWConstant.ERROR: //操作失败
                XToastUtils.toast(context, R.string.request_error_prompt);
                break;
            case CWConstant.NOT_RESULT: // 查询没结果
//                XToastUtils.toast(R.string.not_result_prompt);
                break;
            case CWConstant.NOT_ONLINE: //设备不在线
                XToastUtils.toast(context, R.string.device_ont_online_prompt);
                break;
            case CWConstant.WAIT_ONLINE_UPDATE: //设置成功上线后设备会自动同步
                XToastUtils.toast(context, R.string.wait_online_update_prompt);
                break;
            case CWConstant.U_ALREADY_REGED: //账号重复注册
                XToastUtils.toast(context, R.string.repeat_request_prompt);
                break;
            case CWConstant.U_AUTHCODE_NOTEXIST: // 验证码不存在或者失效
                XToastUtils.toast(context, R.string.tip_verification_code_expired_or_invalid);
                break;
            case CWConstant.NOTEXIST_PARAM: // 必要参数不存在！
                XToastUtils.toast(context, R.string.not_exist_param_prompt);
                break;
            case CWConstant.U_NOT_EXIST: // 账号不存在！
                XToastUtils.toast(context, R.string.account_not_exist_prompt);
                break;
            case CWConstant.U_PWD_ERROR: // 密码错误！
                XToastUtils.toast(context, R.string.tip_password_error);
                break;
            case CWConstant.U_TOKEN_ERR: // Token过期
                XToastUtils.toast(context, R.string.token_error_prompt);
                break;
            case CWConstant.YU_PWD_ERROR: // 原密码错误
                XToastUtils.toast(context, R.string.old_pwd_error_prompt);
                break;
            case CWConstant.BIND_ADMIN_IS_SURE: // 等待管理员确认
                XToastUtils.toast(context, R.string.wait_admin_confirm_prompt);
                break;
            case CWConstant.USER_ALREADY_BIND: // 用户已经绑定过
                XToastUtils.toast(context, R.string.user_already_bind_prompt);
                break;
            case CWConstant.USER_NOT_REQUEST_BIND: // 用户尚未请求过
                XToastUtils.toast(context, R.string.user_not_request_bind_prompt);
                break;
            case CWConstant.DONTNOT_TURN_MYSELF: // 不可以转让给自己
                XToastUtils.toast(context, R.string.not_turn_myself_prompt);
                break;
            case CWConstant.DEVICE_NOT_ACTIVE: // 该设备未激活
                XToastUtils.toast(context, R.string.device_not_active_prompt);
                break;
            case CWConstant.WATCH_NOT_REGISTER: // 手表不存在
                XToastUtils.toast(context, R.string.watch_not_register_prompt);
                break;
            case CWConstant.DEVICE_BIND_USER_NUMBER_EIGHT: // 绑定用户超过8个
                XToastUtils.toast(context, R.string.bind_member_to_max_prompt);
                break;
            case CWConstant.SERVICE_BUSY: // 服务器繁忙
                XToastUtils.toast(context, R.string.service_busy_prompt);
                break;
            case CWConstant.USER_NOT_BIND: // 用户没绑定过
                XToastUtils.toast(context, R.string.user_unbind_device_prompt);
                break;
            case CWConstant.REQUERT_TOO_BUSY: // 操作太频繁
                XToastUtils.toast(context,R.string.request_too_busy_prompt);
                break;
            default:
                XToastUtils.toast(context, R.string.service_busy_prompt);
                break;
        }
    }

    /**
     * 请求结果提示
     *
     * @param code 请求结果
     */
    public static void toast(int code) {
        switch (code) {
            case CWConstant.SUCCESS: //成功
                XToastUtils.toast(R.string.request_success_prompt);
                break;
            case CWConstant.ERROR: //操作失败
                XToastUtils.toast(R.string.request_error_prompt);
                break;
            case CWConstant.NOT_RESULT: // 查询没结果
//                XToastUtils.toast(R.string.not_result_prompt);
                break;
            case CWConstant.NOT_ONLINE: //设备不在线
                XToastUtils.toast(R.string.device_ont_online_prompt);
                break;
            case CWConstant.WAIT_ONLINE_UPDATE: //设置成功上线后设备会自动同步
                XToastUtils.toast(R.string.wait_online_update_prompt);
                break;
            case CWConstant.U_ALREADY_REGED: //账号重复注册
                XToastUtils.toast(R.string.repeat_request_prompt);
                break;
            case CWConstant.U_AUTHCODE_NOTEXIST: // 验证码不存在或者失效
                XToastUtils.toast(R.string.tip_verification_code_expired_or_invalid);
                break;
            case CWConstant.NOTEXIST_PARAM: // 必要参数不存在！
                XToastUtils.toast(R.string.not_exist_param_prompt);
                break;
            case CWConstant.U_NOT_EXIST: // 账号不存在！
                XToastUtils.toast(R.string.account_not_exist_prompt);
                break;
            case CWConstant.U_PWD_ERROR: // 密码错误！
                XToastUtils.toast(R.string.tip_password_error);
                break;
            case CWConstant.U_TOKEN_ERR: // Token过期
                XToastUtils.toast(R.string.token_error_prompt);
                break;
            case CWConstant.YU_PWD_ERROR: // 原密码错误
                XToastUtils.toast(R.string.old_pwd_error_prompt);
                break;
            case CWConstant.BIND_ADMIN_IS_SURE: // 等待管理员确认
                XToastUtils.toast(R.string.wait_admin_confirm_prompt);
                break;
            case CWConstant.USER_ALREADY_BIND: // 用户已经绑定过
                XToastUtils.toast(R.string.user_already_bind_prompt);
                break;
            case CWConstant.USER_NOT_REQUEST_BIND: // 用户尚未请求过
                XToastUtils.toast(R.string.user_not_request_bind_prompt);
                break;
            case CWConstant.DONTNOT_TURN_MYSELF: // 不可以转让给自己
                XToastUtils.toast(R.string.not_turn_myself_prompt);
                break;
            case CWConstant.DEVICE_NOT_ACTIVE: // 该设备未激活
                XToastUtils.toast(R.string.device_not_active_prompt);
                break;
            case CWConstant.WATCH_NOT_REGISTER: // 手表不存在
                XToastUtils.toast(R.string.watch_not_register_prompt);
                break;
            case CWConstant.DEVICE_BIND_USER_NUMBER_EIGHT: // 绑定用户超过8个
                XToastUtils.toast(R.string.bind_member_to_max_prompt);
                break;
            case CWConstant.SERVICE_BUSY: // 服务器繁忙
                XToastUtils.toast(R.string.service_busy_prompt);
                break;
            case CWConstant.REQUERT_TOO_BUSY: // 操作太频繁
                XToastUtils.toast(R.string.request_too_busy_prompt);
                break;
            default:
                XToastUtils.toast(R.string.request_unkonow_prompt);
                break;
        }
    }

    /**
     * 当前没有网络
     */
    public static void toastNetwork(Context context) {
        XToastUtils.toast(context, R.string.network_error_prompt);
    }

    /**
     * 当前没有网络
     */
    public static void toastNetwork() {
        XToastUtils.toast(R.string.network_error_prompt);
    }
}

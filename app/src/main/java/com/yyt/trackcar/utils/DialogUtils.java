package com.yyt.trackcar.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;

import java.util.Calendar;
import java.util.Date;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      TrackDialogUtils
 * @ author:        QING
 * @ createTime:    2020-02-26 15:57
 * @ describe:      TODO 对话框工具类
 */
public class DialogUtils {

    /**
     * 提示框
     *
     * @param context        上下文
     * @param materialDialog 对话框
     * @param title          标题
     * @param message        内容
     * @param confirmText    确定文本
     * @return 提示框
     */
    public static MaterialDialog customMaterialDialog(Context context, MaterialDialog
            materialDialog, String title, String message, String confirmText) {
        if ((materialDialog == null || !materialDialog.isShowing()) && context != null) {
            MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(context);
            try {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/arial" +
                        ".ttf");
                if (typeface != null)
                    materialDialogBuilder.typeface(typeface, typeface);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            if (title != null)
                materialDialogBuilder.title(title);
            if (message != null)
                materialDialogBuilder.content(message);
            if (confirmText != null)
                materialDialogBuilder.positiveText(confirmText);
            materialDialog = materialDialogBuilder.show();
        }
        return materialDialog;
    }

    /**
     * 提示框
     *
     * @param context        上下文
     * @param materialDialog 对话框
     * @param title          标题
     * @param message        内容
     * @param confirmText    确定文本
     * @param cancelText     取消文本
     * @param obj            对象
     * @param type           类型
     * @param handler        消息处理
     * @return 提示框
     */
    public static MaterialDialog customMaterialDialog(Context context, MaterialDialog
            materialDialog, String title, String message, String confirmText, String cancelText,
                                                      final Object obj, final int type,
                                                      final Handler handler) {
        if ((materialDialog == null || !materialDialog.isShowing()) && context != null) {
            MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(context);
            try {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/arial" +
                        ".ttf");
                if (typeface != null)
                    materialDialogBuilder.typeface(typeface, typeface);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            if (title != null)
                materialDialogBuilder.title(title);
            if (message != null)
                materialDialogBuilder.content(message);
            MaterialDialog.SingleButtonCallback callback = null;
            if (confirmText != null || cancelText != null)
                callback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        dialog.dismiss();
                        if (handler != null) {
                            int actionType;
                            if (which == DialogAction.POSITIVE)
                                actionType = CWConstant.HANDLE_CONFIRM_ACTION;
                            else
                                actionType = CWConstant.HANDLE_CANCEL_ACTION;
                            android.os.Message msg =
                                    handler.obtainMessage(actionType, obj);
                            msg.arg1 = type;
                            handler.sendMessage(msg);
                        }
                    }
                };
            if (confirmText != null)
                materialDialogBuilder.positiveText(confirmText)
                        .onPositive(callback);
            if (cancelText != null)
                materialDialogBuilder.negativeText(cancelText)
                        .onNegative(callback);
            materialDialog = materialDialogBuilder.show();
        }
        return materialDialog;
    }

    /**
     * 提示框
     *
     * @param context        上下文
     * @param materialDialog 对话框
     * @param title          标题
     * @param message        内容
     * @param confirmText    确定文本
     * @param neutralText    中间文本
     * @param cancelText     取消文本
     * @param obj            对象
     * @param type           类型
     * @param handler        消息处理
     * @return 提示框
     */
    public static MaterialDialog customMaterialDialog(Context context,
                                                      MaterialDialog materialDialog, String title
            , String message, String confirmText, String neutralText, String cancelText,
                                                      final Object obj, final int type,
                                                      final Handler handler) {
        if ((materialDialog == null || !materialDialog.isShowing()) && context != null) {
            MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(context);
            try {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/arial" +
                        ".ttf");
                if (typeface != null)
                    materialDialogBuilder.typeface(typeface, typeface);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            if (title != null)
                materialDialogBuilder.title(title);
            if (message != null)
                materialDialogBuilder.content(message);
            MaterialDialog.SingleButtonCallback callback = null;
            if (confirmText != null || cancelText != null || neutralText != null)
                callback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        dialog.dismiss();
                        if (handler != null) {
                            int actionType;
                            if (which == DialogAction.POSITIVE)
                                actionType = CWConstant.HANDLE_CONFIRM_ACTION;
                            else if (which == DialogAction.NEUTRAL)
                                actionType = CWConstant.HANDLE_CANCEL_ACTION;
                            else
                                actionType = CWConstant.HANDLE_NEUTRAL_ACTION;
                            android.os.Message msg =
                                    handler.obtainMessage(actionType, obj);
                            msg.arg1 = type;
                            handler.sendMessage(msg);
                        }
                    }
                };
            if (confirmText != null)
                materialDialogBuilder.positiveText(confirmText)
                        .onPositive(callback);
            if (cancelText != null)
                materialDialogBuilder.neutralText(cancelText)
                        .onNeutral(callback);
            if (neutralText != null)
                materialDialogBuilder.negativeText(neutralText)
                        .onNegative(callback);
            materialDialog = materialDialogBuilder.show();
        }
        return materialDialog;
    }

    /**
     * 提示输入框
     *
     * @param context        上下文
     * @param materialDialog 对话框
     * @param title          标题
     * @param message        内容
     * @param hint           输入框提示信息
     * @param prefill        输入框输入文本
     * @param inputType      输入格式
     * @param maxLen         最大输入长度
     * @param minLen         最小输入长度
     * @param confirmText    确定文本
     * @param cancelText     取消文本
     * @param type           类型
     * @param handler        消息处理
     * @return 提示框
     */
    public static MaterialDialog customInputMaterialDialog(Context context, MaterialDialog
            materialDialog, String title, String message, String hint, String prefill,
                                                           int inputType, int maxLen, int minLen,
                                                           String confirmText, String cancelText,
                                                           final int type, final Handler handler) {
        if ((materialDialog == null || !materialDialog.isShowing()) && context != null) {
            MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(context);
            try {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/arial" +
                        ".ttf");
                if (typeface != null)
                    materialDialogBuilder.typeface(typeface, typeface);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            if (title != null)
                materialDialogBuilder.title(title);
            if (message != null)
                materialDialogBuilder.content(message);
            materialDialogBuilder.inputType(inputType);
            materialDialogBuilder.input(hint, prefill, false,
                            (new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog,
                                                    CharSequence input) {
                                }
                            }))
                    .inputRange(minLen, maxLen);
            MaterialDialog.SingleButtonCallback callback = null;
            if (confirmText != null || cancelText != null)
                callback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        dialog.dismiss();
                        if (handler != null) {
                            String inputText = null;
                            if (dialog.getInputEditText() != null)
                                inputText = dialog.getInputEditText().getText().toString();
                            android.os.Message msg =
                                    handler.obtainMessage(CWConstant.HANDLE_INPUT_ACTION,
                                            inputText);
                            msg.arg1 = type;
                            handler.sendMessage(msg);
                        }
                    }
                };
            if (confirmText != null)
                materialDialogBuilder.positiveText(confirmText)
                        .onPositive(callback);
            if (cancelText != null)
                materialDialogBuilder.negativeText(cancelText);
            materialDialog = materialDialogBuilder.show();
            EditText editText = materialDialog.getInputEditText();
            if (editText != null) {
                editText.setFilters(new InputFilter[]{new InputFilter
                        .LengthFilter(maxLen)});
                editText.setBackgroundResource(R.drawable.edittext_common_bg);
            }
        }
        return materialDialog;
    }

    public static TimePickerView customTimePickerDialog(Context context, TimePickerView
            dialog, String title, String message, String hint, String prefill,
                                                        int inputType, int maxLen, int minLen,
                                                        String confirmText, String cancelText,
                                                        final int type, final Handler handler) {
        if ((dialog == null || !dialog.isShowing()) && context != null) {
            Date date = TimeUtils.formatUTC(message, "HH:mm");
            if (date == null)
                date = TimeUtils.formatUTC("00:00", "HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            dialog = new TimePickerBuilder(context, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {
                }
            })
                    .setType(false, false, false, true, true, false)
                    .setTitleText(title)
                    .setDate(calendar)
                    .build();
            dialog.show();
        }
        return dialog;
    }

    /**
     * 关闭弹窗
     *
     * @param dialog 对话框
     */
    public static void dismiss(Dialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    //========================================================================================

    /**
     * 提示框
     *
     * @param context        上下文
     * @param materialDialog 对话框
     * @param title          标题
     * @param message        内容
     * @param confirmText    确定文本
     * @param cancelText     取消文本
     * @param type           类型
     * @param handler        消息处理
     * @return 提示框
     */
    public static MaterialDialog customMaterialDialog(Context context, MaterialDialog
            materialDialog, String title, String message, String confirmText, String cancelText,
                                                      final int type, final Handler handler) {
        return customMaterialDialog(context, materialDialog, title, message, confirmText,
                cancelText, null, type, handler);
    }


    /**
     * 关闭弹窗
     *
     * @param dialog 对话框
     */
    public static void dialogDismiss(Dialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }


}

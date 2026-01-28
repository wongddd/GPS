package com.yyt.trackcar.ui.adapter;

import android.text.TextUtils;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      AlarmClockAdapter
 * @ author:        QING
 * @ createTime:    2020/4/9 02:26
 * @ describe:      TODO 闹钟设置适配器
 */
public class AlarmClockAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener;

    public AlarmClockAdapter(List<BaseItemBean> data,
                             CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        super(R.layout.item_alarm_clock, data);
        mCheckedChangeListener = checkedChangeListener;
    }


    @Override
    protected void convert(BaseViewHolder helper, BaseItemBean model) {
        if (TextUtils.isEmpty(model.getTitle()))
            helper.setText(R.id.tvTitle, R.string.alarm_clock);
        else
            helper.setText(R.id.tvTitle, model.getTitle());
        helper.setText(R.id.tvContent, model.getContent());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
        SwitchButton switchButton = helper.getView(R.id.sbSwitch);
        switchButton.setTag(model.getType());
        switchButton.setOnCheckedChangeListener(null);
        switchButton.setCheckedImmediately(model.isSelect());
        switchButton.setOnCheckedChangeListener(mCheckedChangeListener);
    }

}

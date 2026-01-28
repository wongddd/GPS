package com.yyt.trackcar.ui.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAAPigeonRaceBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class PigeonRaceListAdapter extends BaseQuickAdapter<AAAPigeonRaceBean, BaseViewHolder> {
    public PigeonRaceListAdapter(@Nullable List<AAAPigeonRaceBean> data) {
        super(R.layout.adapter_pigeon_race_list,data);
    }
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    @Override
    protected void convert(BaseViewHolder helper, AAAPigeonRaceBean item) {
        Date nowTime = new Date();
        String year = yearDateFormat.format(item.getPigeonRaceDate());
        String now = yearDateFormat.format(nowTime);
        String[] sub = simpleDateFormat.format(item.getPigeonRaceDate()).split(" ");
        String time;
        if (year.equals(now)){
            time = String.format("%s\n%s",sub[0],sub[1]);
        }else{
            time = String.format("%s-%s\n%s",year,sub[0],sub[1]);
        }
        helper.setText(R.id.tv_pigeon_race_name,item.getPigeonRaceName())
                .setText(R.id.tv_date,time)
                .setText(R.id.tv_remark,item.getRemark());
    }
}

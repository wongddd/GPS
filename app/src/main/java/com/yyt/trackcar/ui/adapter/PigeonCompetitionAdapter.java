package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.GpsPigeonRaceBean;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PigeonCompetitionAdapter extends BaseQuickAdapter<GpsPigeonRaceBean, BaseViewHolder> {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public PigeonCompetitionAdapter(@Nullable List<GpsPigeonRaceBean> data) {
        super(R.layout.adapter_pigeon_competition_list,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GpsPigeonRaceBean item) {
        helper.setText(R.id.tv_competition_name,item.getPigeonRaceName())
                .setText(R.id.tv_start_time, simpleDateFormat.format(item.getPigeonRaceDate()));
        helper.addOnClickListener(R.id.fl_delete,R.id.fl_edit_contestant,R.id.fl_edit_configuration, R.id.fl_modify_info);
    }
}

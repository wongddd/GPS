package com.yyt.trackcar.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.country.CountrySortModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * projectName：   ChatYou
 * packageName：   com.llt.chatyou.ui.adapter
 * fileName：      CountrySelectCodeDialogAdapter
 * author：        QING
 * createTime：    2018/9/25 15:06
 * describe：      TODO 国家或地区区号对话框选择适配器
 */
public class CountrySelectCodeDialogAdapter extends BaseQuickAdapter<CountrySortModel, BaseViewHolder> {

    public CountrySelectCodeDialogAdapter(List<CountrySortModel> data) {
        super(R.layout.layout_country_code_select_item, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull CountrySortModel item) {
        helper.setText(R.id.tvItemCountryName, item.countryName).setText(R.id
                .tvItemCountryCode, item.countryNumber);
    }
}

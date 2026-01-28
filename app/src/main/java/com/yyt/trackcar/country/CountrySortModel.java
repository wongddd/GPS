package com.yyt.trackcar.country;

/*                              
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date     |  Who          |  What  
 * 2015年3月18日   | duanbokan     |   create the file                       
 */

import me.yokeyword.indexablerv.IndexableEntity;

/**
 * 
 * 类简要描述
 * 
 * <p>
 * 类详细描述
 * </p>
 * 
 * @author duanbokan
 * 
 */

public class CountrySortModel extends CountryModel implements IndexableEntity

{
    // 显示数据拼音的首字母
    public String sortLetters;

    public CountrySortToken sortToken = new CountrySortToken();

    public CountrySortModel(String name, String number, String countrySortKey) {
        super(name, number, countrySortKey);
    }

    @Override
    public String getFieldIndexBy() {
        return sortLetters;
    }

    @Override
    public void setFieldIndexBy(String indexField) {
        this.sortLetters = indexField;
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {
        // 需要用到拼音时(比如:搜索), 可增添pinyin字段 this.pinyin  = pinyin
        // 见 CityEntity
    }

    public CountrySortModel(String countryName, String countryNumber,
                            String countrySortKey, String sortLetters,
                            CountrySortToken sortToken) {
        super(countryName, countryNumber, countrySortKey);
        this.sortLetters = sortLetters;
        this.sortToken = sortToken;
    }

}

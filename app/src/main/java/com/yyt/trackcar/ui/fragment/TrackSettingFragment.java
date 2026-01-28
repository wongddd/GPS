package com.yyt.trackcar.ui.fragment;

import android.view.View;
import android.widget.Toast;

import com.xuexiang.xpage.annotation.Page;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;

import butterknife.OnClick;

@Page(name = "TrackSetting")
public class TrackSettingFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.aaa_setting_fragment;
    }

    @Override
    protected void initViews() {

    }

    @OnClick({R.id.first,R.id.second,R.id.third,R.id.fourth,R.id.fifth,R.id.sixth})
    protected void onMultiClick(View view){
        switch (view.getId()){
            case R.id.first:
                Toast.makeText(mActivity, "first", Toast.LENGTH_SHORT).show();
                break;
            case R.id.second:
                Toast.makeText(mActivity, "second", Toast.LENGTH_SHORT).show();
                break;
            case R.id.third:
                Toast.makeText(mActivity, "third", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fourth:
                Toast.makeText(mActivity, "fourth", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fifth:
                Toast.makeText(mActivity, "fifth", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sixth:
                Toast.makeText(mActivity, "sixth", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }
    }


}

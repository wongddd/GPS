package com.yyt.trackcar.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.ui.adapter.CommonLocationAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      SchoolGuardianFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 15:21
 * @ describe:      TODO 上学守护页面
 */
@Page(name = "SchoolGuardian")
public class SchoolGuardianFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener{
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CommonLocationAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private boolean mIsOpen; // 是否开启

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_btn;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.school_guardian);
        return titleBar;
    }

    @Override
    protected void initViews() {
        BaseItemBean itemBean = new BaseItemBean(0, "上学时间", R.mipmap.ic_time);
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setContent("08:00-11:30；14:00-16:30；最晚到家时间XXXXXX");
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(1, "学校地址", R.mipmap.ic_school);
        itemBean.setContent("XXXXX小学");
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));

        mItemList.add(new SectionItem(true, null));
        itemBean = new BaseItemBean(2, "家里地址", R.mipmap.ic_home);
        itemBean.setContent("XXXXX区XXXXX街道XXXX小区");
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(3, "家里Wi-Fi", R.mipmap.ic_wifi);
        itemBean.setContent("还未设置");
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
        mItemList.add(new SectionItem(true, null));
        mSwitchBtn.setText("开启守护");
        initAdapters();
        initRecyclerViews();
        initHeaderView();
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CommonLocationAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_info_second,
                mRecyclerView, false);
        ImageView ivBg = headerView.findViewById(R.id.ivBg);
        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
        TextView tvContent = headerView.findViewById(R.id.tvContent);
        ivBg.setImageResource(R.mipmap.bg_school_guardian);
        tvTitle.setText("到家到校有提醒");
        tvContent.setText("迟到逗留有提醒");
        mAdapter.addHeaderView(headerView);
    }


    @SingleClick
    @OnClick({R.id.switchBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchBtn: // 开启/关闭
                mIsOpen = !mIsOpen;
                if (mIsOpen)
                    mSwitchBtn.setText("关闭守护");
                else
                    mSwitchBtn.setText("开启守护");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                switch (itemBean.getType()) {
                    case 0: // 上学时间
                        openNewPage(GuardianTimeFragment.class);
                        break;
                    case 3: // 家里Wi-Fi
                        openNewPage(WifiFragment.class);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

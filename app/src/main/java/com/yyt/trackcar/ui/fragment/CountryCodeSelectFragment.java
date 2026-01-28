package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.display.ScreenUtils;
import com.xuexiang.xutil.system.KeyboardUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.country.CharacterParserUtil;
import com.yyt.trackcar.country.CountryComparator;
import com.yyt.trackcar.country.CountrySortModel;
import com.yyt.trackcar.country.GetCountryNameSort;
import com.yyt.trackcar.ui.adapter.CountryCodeSelectAdapter;
import com.yyt.trackcar.ui.adapter.CountrySelectCodeDialogAdapter;
import com.yyt.trackcar.ui.adapter.SearchHeaderAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.DialogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import me.yokeyword.indexablerv.SimpleHeaderAdapter;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      CountryCodeSelectFragment
 * @ author:        QING
 * @ createTime:    2020-02-26 15:37
 * @ describe:      TODO 选择国家和地区区号页面
 */
@Page(name = "CountryCodeSelect")
public class CountryCodeSelectFragment extends BaseFragment implements View
        .OnClickListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.indexablelayout)
    IndexableLayout indexableLayout; // 列表布局
    private SearchHeaderAdapter mSearchHeaderAdapter; // 搜索布局
    private CountryCodeSelectAdapter mAdapter; // 适配器
    private SimpleHeaderAdapter mCustomCityAdapter; // 常用地区适配器
    private List<CountrySortModel> mAllCountryList = new ArrayList<>(); // 所有国家
    private List<CountrySortModel> mCustomCountryList = new ArrayList<>(); // 常用国家
    private Dialog mDialog; // 弹窗菜单
    private MaterialSearchBar mSearchBar; // 搜索栏
    private RecyclerView mRecyclerView; // 搜索列表
    private List<CountrySortModel> countryList = new ArrayList<>(); // 所有国家
    private List<CountrySortModel> mCountryList = new ArrayList<>(); // 搜索的国家列表
    private GetCountryNameSort countryChangeUtil; // 模糊查询
    private CountrySelectCodeDialogAdapter mDialogAdapter; // 搜索适配器

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_country_code_select;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.country_code);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initIndexableLayouts();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        // 拼音
        CountryComparator pinyinComparator = new CountryComparator();
        countryChangeUtil = new GetCountryNameSort();
        CharacterParserUtil characterParserUtil = new CharacterParserUtil();
        String[] countryArray;
        if ("CN".equals(Locale.getDefault().getCountry()))
            countryArray = getResources().getStringArray(R.array.country_code_list_ch);
        else
            countryArray = getResources().getStringArray(R.array.country_code_list_tw);
        for (int i = 9, length = countryArray.length; i < length; i++) {
            String[] country = countryArray[i].split("\\*");
            String countryName = country[0];
            String countryNumber = country[1];
            String countrySortKey = characterParserUtil.getSelling(countryName);
            CountrySortModel countrySortModel = new CountrySortModel(
                    countryName, countryNumber, countrySortKey);
            String sortLetter = countryChangeUtil
                    .getSortLetterBySortKey(countrySortKey);
            if (sortLetter == null)
                sortLetter = countryChangeUtil.getSortLetterBySortKey(countryName);
            countrySortModel.sortLetters = sortLetter;
            mAllCountryList.add(countrySortModel);
        }
        Collections.sort(mAllCountryList, pinyinComparator);

        for (int i = 0; i <= 8; i++) {
            String[] country = countryArray[i].split("\\*");
            String countryName = country[0];
            String countryNumber = country[1];
            String countrySortKey = characterParserUtil.getSelling(countryName);
            CountrySortModel countrySortModel = new CountrySortModel(
                    countryName, countryNumber, countrySortKey);
            countrySortModel.sortLetters = getString(R.string.country_code_often);
            mCustomCountryList.add(countrySortModel);
        }
        countryList.addAll(mCustomCountryList);
        countryList.addAll(mAllCountryList);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        List<Object> array = new ArrayList<>();
        array.add(new Object());
        mAdapter = new CountryCodeSelectAdapter(mActivity);
        mAdapter.setOnItemContentClickListener(mItemContentClickListener);
        mCustomCityAdapter = new SimpleHeaderAdapter<>(mAdapter, "☆", getString(R.string
                .country_code_often), mCustomCountryList);
        mSearchHeaderAdapter = new SearchHeaderAdapter(mActivity, null, null, array, this);
    }

    /**
     * 初始化IndexableLayout
     */
    private void initIndexableLayouts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        indexableLayout.setLayoutManager(layoutManager);
        indexableLayout.setOverlayStyle_Center();
        indexableLayout.setAdapter(mAdapter);
        mAdapter.setDatas(mAllCountryList);
        indexableLayout.addHeaderAdapter(mCustomCityAdapter);
        indexableLayout.addHeaderAdapter(mSearchHeaderAdapter);
    }

    /**
     * 隐藏键盘
     */
    private void hideSoftInput() {
        if (mSearchBar != null)
            KeyboardUtils.hideSoftInput(mSearchBar);
    }

    /**
     * 搜索
     */
    private void onSearch() {
        if (mDialog == null) {
            mDialog = new Dialog(mActivity, R.style.dialog_normal_style);
            mDialog.setCanceledOnTouchOutside(true);
            @SuppressLint("InflateParams")
            View mView = getLayoutInflater().inflate(R.layout.popwindow_country_code_search, null);
            mRecyclerView = mView.findViewById(R.id.recyclerView);
            mSearchBar = mView.findViewById(R.id.searchBar);
            mDialogAdapter = new CountrySelectCodeDialogAdapter(mCountryList);
            mDialogAdapter.setOnItemClickListener(this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mDialogAdapter);
            View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view,
                    mRecyclerView, false);
            emptyView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color
                    .white));
            emptyView.findViewById(R.id.ivEmpty).setVisibility(View.GONE);
            TextView tvEmpty = emptyView.findViewById(R.id.tvEmpty);
            tvEmpty.setText(R.string.seach_result_empty_prompt);
            emptyView.setOnClickListener(this);
            mDialogAdapter.setEmptyView(emptyView);
            mRecyclerView.setVisibility(View.GONE);
            mSearchBar.setMaxSuggestionCount(0);
            mSearchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    String mSearchContent = mSearchBar.getText();
                    if (TextUtils.isEmpty(mSearchContent)) {
                        mCountryList.clear();
                        mDialogAdapter.notifyDataSetChanged();
                    } else {
                        mCountryList.clear();
                        // 按照输入内容进行匹配
                        List<CountrySortModel> mSearchCountryList = countryChangeUtil.search
                                (mSearchContent, countryList);
                        mCountryList.addAll(mSearchCountryList);
                        mDialogAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(0);
                    }
                }
            });
            mSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                    if (enabled)
                        KeyboardUtils.showSoftInput(mSearchBar);
                    else
                        DialogUtils.dismiss(mDialog);
                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                }

                @Override
                public void onButtonClicked(int buttonCode) {
                }
            });
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                        hideSoftInput();
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
            mSearchBar.enableSearch();
            mDialog.setContentView(mView);
            // 获得当前窗体
            Window window = mDialog.getWindow();
            // 重新设置
            assert window != null;
            WindowManager.LayoutParams lp = window
                    .getAttributes();
            window.setGravity(Gravity.START | Gravity.TOP);
            lp.x = 0; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.width = ScreenUtils.getScreenWidth(); // 宽度
//            lp.height = ScreenUtils.getScreenHeight();
            // 高度
            // dialog.onWindowAttributesChanged(lp);
            // (当Window的Attributes改变时系统会调用此函数)
            window.setAttributes(lp);
        } else {
            mSearchBar.setText("");
            mSearchBar.enableSearch();
            mRecyclerView.setVisibility(View.GONE);
        }
        mDialog.show();
//        mSearchBar.post(new Runnable() {
//            @Override
//            public void run() {
//                KeyboardUtils.showSoftInput(mSearchBar);
//            }
//        });
    }

    /**
     * 列表选项点击监听器
     */
    private IndexableAdapter.OnItemContentClickListener<CountrySortModel>
            mItemContentClickListener = new IndexableAdapter
            .OnItemContentClickListener<CountrySortModel>() {
        @Override
        public void onItemClick(View v, int originalPosition, int currentPosition,
                                CountrySortModel entity) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(CWConstant.COUNTRY_NAME,
                    entity.countryName);
            bundle.putString(CWConstant.COUNTRY_CODE,
                    entity.countryNumber);
            intent.putExtras(bundle);
            setFragmentResult(Activity.RESULT_OK, intent);
            popToBack();
        }
    };

    @Override
    @SingleClick
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchBar: // 搜索栏
                onSearch();
                break;
            default:
                hideSoftInput();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mCountryList.size()) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(CWConstant.COUNTRY_NAME,
                    mCountryList.get(position).countryName);
            bundle.putString(CWConstant.COUNTRY_CODE,
                    mCountryList.get(position).countryNumber);
            intent.putExtras(bundle);
            setFragmentResult(Activity.RESULT_OK, intent);
            popToBack();
        }
    }

    @Override
    public void onDestroy() {
        DialogUtils.dismiss(mDialog);
        hideSoftInput();
        super.onDestroy();
    }
}

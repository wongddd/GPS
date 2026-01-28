package com.yyt.trackcar.ui.fragment;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.preview.PreviewBuilder;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PhotoBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.AlbumModel;
import com.yyt.trackcar.dbflow.AlbumModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.PreviewActivity;
import com.yyt.trackcar.ui.adapter.PreviewImageAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      RemotePhotoTakeFragment
 * @ author:        QING
 * @ createTime:    2020/4/16 17:48
 * @ describe:      TODO 远程拍照页面
 */
@Page(name = "RemotePhotoTake")
public class RemotePhotoTakeFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout; // 下拉加载控件
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private PreviewImageAdapter mAdapter; // 适配器
    private List<PhotoBean> mItemList = new ArrayList<>(); // 列表
    private GridLayoutManager mGridLayoutManager;
    private TitleBar mTitleBar; // 标题栏
    private boolean mIsEdit; // 是否编辑

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        mTitleBar = super.initTitle();
        mTitleBar.setTitle(R.string.remote_photo_take);
        initAction();
        return mTitleBar;
    }

    @Override
    protected void initViews() {
        mRefreshLayout.setBackgroundResource(R.color.colorLayoutBackground);
        mRefreshLayout.setEnableLoadMore(false);
        initItems();
        initAdapters();
        initRecyclerViews();
        initEmptyView();
        getDevicePhoto();
    }

    @Override
    protected void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getDevicePhoto();
            }
        });
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            List<AlbumModel> list = SQLite.select().from(AlbumModel.class)
                    .where(AlbumModel_Table.imei.eq(deviceModel.getImei()))
                    .orderBy(AlbumModel_Table.createtime, false)
                    .queryList();
            for (AlbumModel model : list) {
                PhotoBean photoBean = new PhotoBean();
                photoBean.setImei(model.getImei());
                photoBean.setUrl(model.getUrl());
                photoBean.setCreatetime(model.getCreatetime());
                photoBean.setName(model.getName());
                mItemList.add(photoBean);
            }
        }
        initAction();
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new PreviewImageAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        mGridLayoutManager = new GridLayoutManager(mActivity, 3);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化空布局
     */
    private void initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view,
                mRecyclerView, false);
        emptyView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color
                .white));
        ImageView ivEmpty = emptyView.findViewById(R.id.ivEmpty);
        TextView tvEmpty = emptyView.findViewById(R.id.tvEmpty);
        ivEmpty.setImageResource(R.mipmap.ic_no_query_data);
        tvEmpty.setText(R.string.no_remote_photo_take_prompt);
        mAdapter.setEmptyView(emptyView);
    }

    /**
     * 用户获取设备拍照的图片
     */
    private void getDevicePhoto() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getDevicePhoto(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), deviceModel.getImei(), mHandler);
    }

    /**
     * 远程拍照
     */
    private void captDevice() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            SettingSPUtils.getInstance().putLong(CWConstant.REMOTE_PHOTO,
                    System.currentTimeMillis());
            CWRequestUtils.getInstance().captDevice(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), "",
                    mHandler);
        }
    }

    /**
     * 查找信息
     * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
     */
    private void computeBoundsBackward(int firstCompletelyVisiblePos) {
        for (int i = firstCompletelyVisiblePos; i < mAdapter.getItemCount(); i++) {
            View itemView = mGridLayoutManager.findViewByPosition(i);
            Rect bounds = new Rect();
            if (itemView != null) {
                ImageView imageView = itemView.findViewById(R.id.ivImage);
                imageView.getGlobalVisibleRect(bounds);
            }
            PhotoBean photoBean = mAdapter.getItem(i);
            if (photoBean != null)
                photoBean.setBounds(bounds);
        }
    }

    /**
     * 初始化标题栏动作
     */
    private void initAction() {
        mTitleBar.removeAllActions();
        if (mIsEdit && mItemList.size() > 0) {
            mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.del)) {
                @Override
                public void performAction(View view) {
                    for (int i = mItemList.size() - 1; i >= 0; i--) {
                        PhotoBean photoBean = mItemList.get(i);
                        if (photoBean.isSelect()) {
                            OperatorGroup operatorGroup =
                                    OperatorGroup.clause(OperatorGroup.clause()
                                            .and(AlbumModel_Table.imei.eq(photoBean.getImei()))
                                            .and(AlbumModel_Table.url.eq(photoBean.getUrl()))
                                            .and(AlbumModel_Table.createtime.eq(photoBean.getCreatetime())));
                            SQLite.delete(AlbumModel.class).where(operatorGroup).execute();
                            mItemList.remove(i);
                        }
                    }
                    mIsEdit = false;
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    initAction();
                }
            });
            mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.cancel)) {
                @Override
                public void performAction(View view) {
                    mIsEdit = false;
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    initAction();
                }
            });
        } else {
            mIsEdit = false;
            if (mAdapter != null)
                mAdapter.setEdit(mIsEdit);
            mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.photograph)) {
                @Override
                public void performAction(View view) {
                    long photoTime = SettingSPUtils.getInstance().getLong(CWConstant.REMOTE_PHOTO
                            , 0);
                    if (System.currentTimeMillis() - photoTime < 15 * 1000)
                        XToastUtils.toast(getString(R.string.operation_is_too_frequent_prompt,
                                getString(R.string.remote_photo_take)));
                    else
                        mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                                mMaterialDialog, getString(R.string.prompt),
                                getString(R.string.instruct_send_prompt,
                                        getString(R.string.remote_photo_take)),
                                getString(R.string.confirm), getString(R.string.cancel), null,
                                CWConstant.MORE_REMOTE_PHOTO_TAKE, mHandler);
                }
            });
            if (mItemList.size() > 0)
                mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.edit)) {
                    @Override
                    public void performAction(View view) {
                        mIsEdit = true;
                        for (PhotoBean photoBean : mItemList) {
                            photoBean.setSelect(false);
                        }
                        mAdapter.setEdit(mIsEdit);
                        mAdapter.notifyDataSetChanged();
                        initAction();
                    }
                });
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            computeBoundsBackward(mGridLayoutManager.findFirstVisibleItemPosition());
            PreviewBuilder.from(this)
                    .to(PreviewActivity.class)
                    .setImgs(mAdapter.getData())
                    .setCurrentIndex(position)
                    .setSingleFling(true)
                    .setProgressColor(R.color.colorTexNormal)
                    .setType(PreviewBuilder.IndicatorType.Number)
                    .start();
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            PhotoBean photoBean = mItemList.get(position);
            photoBean.setSelect(!photoBean.isSelect());
            mAdapter.notifyItemChanged(position);
        }
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                RequestBean requestBean;
                UserModel userModel;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_DEVICE_PHOTO: // 用户获取设备拍照的图片
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                RequestBean.class);
                                if (resultBean.getList() != null) {
                                    for (Object obj : resultBean.getList()) {
                                        AlbumModel albumModel = mGson.fromJson(mGson.toJson(obj),
                                                AlbumModel.class);
                                        albumModel.setImei(requestBean.getImei());
                                        albumModel.save();
                                    }
                                    mItemList.clear();
                                    initItems();
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        mRefreshLayout.finishRefresh();
                        break;
                    case CWConstant.REQUEST_URL_CAPT_DEVICE: // 远程监拍
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().captDevice(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getCome(), mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS)
                                XToastUtils.toast(R.string.send_success_prompt);
                            else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.MORE_REMOTE_PHOTO_TAKE: // 远程拍照
                                captDevice();
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            return false;
        }
    });

}

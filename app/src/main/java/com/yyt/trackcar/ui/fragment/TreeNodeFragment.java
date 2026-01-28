package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.ui.holder.IconTreeItem;
import com.yyt.trackcar.ui.holder.MyHolder;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;

import java.util.List;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
@Page(name = "TreeNodeFragment", anim = CoreAnim.none)
public class TreeNodeFragment extends BaseFragment {

    @BindView(R.id.container)
    RelativeLayout containerView;

    private Context mContext;
    private TreeNode rootNode;
    private AndroidTreeView androidTreeView;
    private final AAAUserModel userModel = getTrackUserModel();
    private TreeNode firstNode = null;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_default;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(R.string.dealer_manage);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.dealer_manage)));
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.add)) {
            @Override
            public void performAction(View view) {
                EditText editText = new EditText(mContext);
                editText.setHint(R.string.enter_dealer_account);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle(R.string.add_subordinate_dealer);
                alertDialog.setView(editText);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm), (dialogInterface, i) -> {
                    KeyboardUtils.hideSoftInput(editText);
                    String content = editText.getText().toString().trim();
                    if (content.length() == 0) {
                        showMessage(R.string.enter_dealer_account);
                    } else {
                        CarGpsRequestUtils.addSubordinateDealer(userModel, content, mHandler);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialogInterface, i) -> {

                });
                alertDialog.show();
            }
        });
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.create)) {
            @Override
            public void performAction(View view) {
                View v1 = LayoutInflater.from(mContext).inflate(R.layout.layout_create_account, null, false);
                EditText account = v1.findViewById(R.id.et_account);
                EditText password = v1.findViewById(R.id.et_password);
                EditText repeatPwd = v1.findViewById(R.id.et_repeat);
                CheckBox unbindPermission = v1.findViewById(R.id.unbind_permission);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle(getString(R.string.create_and_bind_dealer_account));
                alertDialog.setView(v1);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm), (dialogInterface, i) -> {
                    KeyboardUtils.hideSoftInput(v1);
                    String accountStr = account.getText().toString().trim();
                    String passwordStr = password.getText().toString().trim();
                    String repeatPwdStr = repeatPwd.getText().toString().trim();
                    if (TextUtils.isEmpty(accountStr)) {
                        showMessage(R.string.account_hint);
                    } else if (TextUtils.isEmpty(passwordStr)) {
                        showMessage(R.string.enter_password);
                    } else if (TextUtils.isEmpty(repeatPwdStr) || !repeatPwdStr.equals(passwordStr)) {
                        showMessage(R.string.passwords_inconsistent);
                    } else {
                        showDialog();
                        StringBuilder permissions = new StringBuilder();
                        if (unbindPermission.isChecked()) {
                            permissions.append("102");
                        }
                        CarGpsRequestUtils.createSubordinateDealerAccount(userModel, accountStr, passwordStr, permissions.toString(), mHandler);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialogInterface, i) -> {

                });
                alertDialog.show();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mContext = getContext();
    }

    /**
     * 重新获取数据更新树形结构
     */
    private void refreshData() {
        if (androidTreeView != null) {
            containerView.removeAllViews();
            androidTreeView = null;
            firstNode = null;
            rootNode = null;
        }
        rootNode = TreeNode.root();
        showDialog();
        CarGpsRequestUtils.queryDealerAndSubDealersBoundDevices(userModel, userModel.getUserId(), mHandler);
    }

    /**
     * 更新树形结构列表渲染
     */
    private void refreshTreeView() {
        androidTreeView = new AndroidTreeView(getActivity(), rootNode);
        containerView.addView(androidTreeView.getView());
    }

    /**
     * 添加经销商节点
     *
     * @param parentNode  父节点
     * @param currentUser 经销商用户对象
     * @return TreeNode
     */
    private TreeNode addDealerChildNode(TreeNode parentNode, AAAUserModel currentUser) {
        IconTreeItem iconTreeItem = new IconTreeItem()
                .Title(currentUser.getUserName())
                .Depth(parentNode.getLevel())
                .ID(currentUser.getUserId())
                .ExtraData(currentUser.getPassword())
                .NodeType(1);
        TreeNode node = new TreeNode(iconTreeItem).setViewHolder(new MyHolder(mContext, userModel, this));
        parentNode.addChild(node);
        if (currentUser.getGpsDeviceList() != null && currentUser.getGpsDeviceList().size() > 0) {
            addDeviceChildNode(node, currentUser.getGpsDeviceList());
        }
        return node;
    }

    /**
     * 添加设备节点
     *
     * @param parentNode 父节点
     * @param deviceList 经销商的设备列表
     */
    private void addDeviceChildNode(TreeNode parentNode, List<AAADeviceModel> deviceList) {
        for (AAADeviceModel item : deviceList) {
            IconTreeItem iconTreeItem = new IconTreeItem()
                    .Title(item.getDeviceImei())
                    .Depth(parentNode.getLevel())
                    .ID(item.getDeviceId())
                    .NodeType(2);
            TreeNode node = new TreeNode(iconTreeItem).setViewHolder(new MyHolder(mContext, userModel, this));
            parentNode.addChild(node);
        }
    }

    /**
     * 递归将列表拆分填入树形组件中
     *
     * @param parentNode 父节点
     * @param list       待处理的经销商对象
     */
    private void recursionEditNode(TreeNode parentNode, List<AAAUserModel> list) {
        if (list.size() > 0 && list.get(0) != null) {
            try {
                IconTreeItem iconTreeItem = (IconTreeItem) parentNode.getValue();
                for (int i = 0; i < list.size(); i++) {
                    AAAUserModel element = list.get(i);
                    if (element.getParentId() == iconTreeItem.getId()) {
                        addDealerChildNode(parentNode, element);
                        list.remove(i);
                        i -= 1;
                    } else if (parentNode.getChildren() != null && parentNode.getChildren().size() > 0) {
                        for (TreeNode node : parentNode.getChildren()) {
                            if (((IconTreeItem) node.getValue()).getNodeType() == 1) {
                                recursionEditNode(node, list);
                            }
                        }
                    }
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            try {
                if (message.obj == null) {
                    dismisDialog();
                    showMessage(R.string.request_error_prompt);
                    return false;
                }
                AAABaseResponseBean response;
                switch (message.what) {
                    case TConstant.REQUEST_QUERY_DEALER_AND_SUBORDINATE_DEALERS_BOUND_DEVICES:
                        dismisDialog();
                        response = (AAABaseResponseBean) message.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            List<List<AAAUserModel>> subordinateList =
                                    mGson.fromJson(mGson.toJson(response.getData())
                                            , new TypeToken<List<List<AAAUserModel>>>() {
                                            }.getType());
                            if (subordinateList.size() > 0) {
                                List<AAAUserModel> list = subordinateList.get(0);
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getUserId() == getTrackUserModel().getUserId()) {
                                        firstNode = addDealerChildNode(rootNode, userModel);
                                        list.remove(i);
                                        break;
                                    }
                                }
                                recursionEditNode(firstNode, list);
                                dismisDialog();
                                refreshTreeView();
                            } else {
                                dismisDialog();
                                showMessage(ErrorCode.getResId(response.getCode()));
                            }
                        } else {
                            dismisDialog();
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                        break;
                    case TConstant.REQUEST_ADD_SUBORDINATE_DEALER:
                        dismisDialog();
                        response = (AAABaseResponseBean) message.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS && response.getData() != null) {
                            List<AAAUserModel> list = mGson.fromJson(mGson.toJson(response.getData()), new TypeToken<List<AAAUserModel>>() {
                            }.getType());
                            addDealerChildNode(firstNode, list.get(0));
                            showMessage(R.string.send_success_prompt);
                        } else {
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                        break;
                    case TConstant.REQUEST_CREATE_SUBORDINATE_DEALER_ACCOUNT:
                        dismisDialog();
                        response = (AAABaseResponseBean) message.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS && response.getData() != null) {
                            showMessage(R.string.create_dealer_account_succeed);
                            List<List<AAAUserModel>> list = mGson.fromJson(mGson.toJson(response.getData()), new TypeToken<List<List<AAAUserModel>>>() {
                            }.getType());
                            if (list.size() > 0 && list.get(0).size() > 0) {
                                addDealerChildNode(firstNode, list.get(0).get(0));
                            }
                        } else {
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            return false;
        }
    });
}

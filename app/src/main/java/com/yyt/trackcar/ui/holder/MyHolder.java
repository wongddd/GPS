package com.yyt.trackcar.ui.holder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.unnamed.b.atv.model.TreeNode;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.ui.dialog.LoadingDialog;
import com.yyt.trackcar.ui.fragment.LoginToSubDealerFragment;
import com.yyt.trackcar.ui.fragment.SearchAgentUserFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;

public class MyHolder extends TreeNode.BaseNodeViewHolder<IconTreeItem> {

    private final Context context;
    private final AAAUserModel userModel;
    private final BaseFragment fragment;
    private final int UNBIND_DEVICE = 0x101;
    private final int UNBIND_DEALER = 0x102;
    private LoadingDialog mLoadingDialog;
    private TreeNode currentNode = null; // 当前操作的节点
    private boolean operating = false; // 是否处于请求操作中,防止重复请求

    public MyHolder(Context context, AAAUserModel userModel, BaseFragment fragment) {
        super(context);
        this.context = context;
        this.userModel = userModel;
        this.fragment = fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public View createNodeView(TreeNode node, IconTreeItem iconTreeItem) {
        final View view = LayoutInflater.from(context).inflate(R.layout.aaa_layout_tree_node, null, false);
        view.setPadding(node.getLevel() * 20, 0, 0, 0);
        final TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        final ImageView delete = view.findViewById(R.id.node_delete);
        final ImageView arrow = view.findViewById(R.id.node_arrow);
        final ImageView edit = view.findViewById(R.id.node_edit);
        final ImageView change = view.findViewById(R.id.node_switch);
        final ImageView sign = view.findViewById(R.id.node_sign);

        node.setExpanded(true);
        arrow.setRotation(90.f);

        tvValue.setText(iconTreeItem.getTitle());
        if (iconTreeItem.getNodeType() == 2) {
            delete.setBackgroundResource(R.mipmap.ic_unbind_grey);
            edit.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
            sign.setImageResource(R.mipmap.ic_device_azure);
        } else {
            if (iconTreeItem.getDepth() == 0 || iconTreeItem.getDepth() > 1) {
                delete.setVisibility(View.GONE);
            }
            sign.setImageResource(R.mipmap.ic_dealer_azure);
            if (!iconTreeItem.getTitle().equals(userModel.getUserName())) {
                change.setVisibility(View.VISIBLE);
            }
        }

        edit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(TConstant.USERNAME, iconTreeItem.getTitle());
            bundle.putLong(TConstant.USER_ID, iconTreeItem.getId());
            fragment.openNewPage(SearchAgentUserFragment.class, bundle);
        });

        delete.setOnClickListener(view1 -> {
            if (operating) return;
//                getTreeView().removeNode(node);
            if (iconTreeItem.getNodeType() == 2) {
                DialogUtils.customMaterialDialog(
                        context,
                        null,
                        context.getString(R.string.prompt),
                        context.getString(R.string.unbind_prompt),
                        context.getString(R.string.confirm),
                        context.getString(R.string.cancel),
                        node,
                        UNBIND_DEVICE,
                        mHandler).show();
            } else if (iconTreeItem.getNodeType() == 1) {
                DialogUtils.customMaterialDialog(
                        context,
                        null,
                        context.getString(R.string.prompt),
                        context.getString(R.string.remove_subordinate_dealer_tip),
                        context.getString(R.string.confirm),
                        context.getString(R.string.cancel),
                        node,
                        UNBIND_DEALER,
                        mHandler).show();
            }
        });

        change.setOnClickListener(view12 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.prompt).setMessage(R.string.switch_to_sub_dealer_account_tip)
                    .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(TConstant.USERNAME, iconTreeItem.getTitle());
                        fragment.openNewPage(LoginToSubDealerFragment.class, bundle);
                    }).setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

                    }).show();
        });

        node.setClickListener((node1, value) -> {
            try {
                IconTreeItem iconTreeNode = (IconTreeItem) value;
                if (iconTreeNode.getNodeType() == 1) {
                    node1.setSelected(!node1.isSelected());
                    iconTreeNode.setCollapsed(!iconTreeNode.isCollapsed());
                    if (iconTreeNode.isCollapsed()) {
                        arrow.setRotation(0.f);
                    } else {
                        arrow.setRotation(90.f);
                    }
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        });
        return view;
    }

    private void showMessage(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(int resourceId) {
        Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show();
    }

    protected void showDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(context, R.style.dialog_loading_style);
            mLoadingDialog.setCanceledOnTouchOutside(false);
        }
        if (mLoadingDialog != null && !mLoadingDialog.isShowing())
            mLoadingDialog.show();
    }

    protected void dismissDialog() {
        DialogUtils.dialogDismiss(mLoadingDialog);
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            try {
                AAABaseResponseBean response;
                switch (message.what) {
                    case CWConstant.HANDLE_CONFIRM_ACTION:
                        if (message.arg1 == UNBIND_DEVICE) {
                            TreeNode node = (TreeNode) message.obj;
                            IconTreeItem item = (IconTreeItem) node.getValue();
                            currentNode = node;
                            operating = true;
                            showDialog();
                            CarGpsRequestUtils.unbindDeviceFromDealer(userModel, String.valueOf(item.getTitle()), mHandler);
                        } else if (message.arg1 == UNBIND_DEALER) {
                            TreeNode node = (TreeNode) message.obj;
                            IconTreeItem item = (IconTreeItem) node.getValue();
                            currentNode = node;
                            operating = true;
                            showDialog();
                            CarGpsRequestUtils.deleteSubordinateDealer(userModel, item.getId(), mHandler);
                        }
                        break;
                    case TConstant.REQUEST_UNBIND_DEVICE_FROM_AGENCY: // 从经销商处解绑设备
                        dismissDialog();
                        operating = false;
                        response = (AAABaseResponseBean) message.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            getTreeView().removeNode(currentNode);
                            showMessage(R.string.unbind_succeed_prompt);
                        } else {
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                        currentNode = null;
                        break;
                    case TConstant.REQUEST_DELETE_SUBORDINATE_DEALER: // 删除下级经销商
                        dismissDialog();
                        operating = false;
                        if (message.obj == null) {
                            showMessage(R.string.request_error_prompt);
                            return false;
                        }
                        response = (AAABaseResponseBean) message.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            getTreeView().removeNode(currentNode);
                            showMessage(context.getString(R.string.remove_sub_dealer_succeed));
                        } else {
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                        currentNode = null;
                        break;
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            return false;
        }
    });
}
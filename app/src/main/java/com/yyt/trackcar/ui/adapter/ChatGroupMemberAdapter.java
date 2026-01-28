package com.yyt.trackcar.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.SettingSPUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      ChatGroupMemberAdapter
 * @ author:        QING
 * @ createTime:    2020/6/29 14:46
 * @ describe:      TODO 微聊成员适配器
 */
public class ChatGroupMemberAdapter extends BaseQuickAdapter<PortraitModel, BaseViewHolder> {

    public ChatGroupMemberAdapter(List<PortraitModel> data) {
        super(R.layout.item_chat_group_member, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull PortraitModel item) {
        if (String.valueOf(R.mipmap.ic_add_member).equals(item.getUrl()))
            ImageLoadUtils.loadPortraitImage(mContext, "", R.mipmap.ic_add_member,
                    helper.getView(R.id.ivPortrait));
        else {
            if (item.getUserId().equals(item.getImei())) {
                int imgRes;
                if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                    imgRes = R.mipmap.ic_device_portrait;
                else
                    imgRes = R.mipmap.ic_default_pigeon_marker;
                ImageLoadUtils.loadPortraitImage(mContext, item.getUrl(), imgRes,
                        helper.getView(R.id.ivPortrait));
            } else
                ImageLoadUtils.loadPortraitImage(mContext, item.getUrl(),
                        helper.getView(R.id.ivPortrait));
        }
        String name = item.getName();
        if (name == null)
            name = " ";
        helper.setText(R.id.tvTitle, name);
    }
}

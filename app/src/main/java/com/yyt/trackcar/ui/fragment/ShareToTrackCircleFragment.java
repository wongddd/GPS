package com.yyt.trackcar.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.BitmapBlobUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 分享内容至轨迹圈界面
 */
@Page(name = "shareToTrackCircle", anim = CoreAnim.none)
public class ShareToTrackCircleFragment extends BaseFragment {

    private Bitmap bitmap;
    private String mStartTime;
    private String mEndTime;
    private String mContent;
    private int apartDistance;

    @BindView(R.id.iv_image)
    ImageView imageOfTrack;
    @BindView(R.id.et_content)
    EditText circleSubject; //轨迹圈主题

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_share_content;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) popToBack();
        else {
            byte[] bytes = bundle.getByteArray("image");
            bitmap = BitmapBlobUtils.Bytes2Bimap(bytes);
            mStartTime = bundle.getString(TConstant.START_TIME);
            mEndTime = bundle.getString(TConstant.END_TIME);
//            apartDistance = (int) Math.ceil(bundle.getLong("distance")/1000.0d);
            apartDistance = (int) bundle.getLong("distance");
        }
    }

    @Override
    protected void initViews() {
        imageOfTrack.setImageBitmap(bitmap);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(R.string.share_to_track_sharing_circle);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.share_to_track_sharing_circle)));
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.post)) {
            @Override
            public void performAction(View view) {
                mContent = circleSubject.getText().toString().trim();
                if (TextUtils.isEmpty(mContent)) {
                    showMessage(String.format("%s%s", getString(R.string.content), getString(R.string.cannot_empty_prompt)));
                    return;
                }
                if (bitmap != null) {
                    showDialog();
                    CarGpsRequestUtils.uploadTrackImage(getTrackUserModel()
                            , getTrackDeviceModel().getDeviceImei()
                            , BitmapBlobUtils.bitmapToString(bitmap)
                            , mHandler);
                }
            }
        });
        return titleBar;
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            AAABaseResponseBean responseBean;
            try {
                switch (message.what) {
                    case TConstant.REQUEST_UPLOAD_TRACK_IMAGE: //上传图片到服务器
                        if (message.obj == null) {
                            dismisDialog();
                            showMessage(R.string.upload_error_tips);
                            return false;
                        }
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS
                                || responseBean.getCode() == TConstant.RESPONSE_SUCCESS_NEW) {
                            List<String> list = new ArrayList<>();
                            list.add(responseBean.getStrParameter());
                            CarGpsRequestUtils.shareToTrackCircle(getTrackUserModel()
                                    , getTrackDeviceModel().getDeviceImei()
                                    , list
                                    , mStartTime, mEndTime, mContent, apartDistance, mHandler);
                        } else {
                            showMessage(R.string.upload_error_tips);
                            dismisDialog();
                        }
                        break;
                    case TConstant.REQUEST_SHARE_TRACK_TOT_TRACK_CIRCLE: //分享到轨迹圈
                        if (message.obj == null) {
                            dismisDialog();
                            showMessage(R.string.send_success_prompt);
                            return false;
                        }
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS
                                || responseBean.getCode() == TConstant.RESPONSE_SUCCESS_NEW) {
                            dismisDialog();
                            showMessage(R.string.send_success_prompt);
                            popToBack();
                        } else {
                            dismisDialog();
                            showMessage(R.string.send_error_prompt);
                        }
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
    });
}

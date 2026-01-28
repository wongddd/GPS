package com.yyt.trackcar.utils;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.Fragment;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.yyt.trackcar.R;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * projectName：   FAXWIN
 * packageName：   com.fax.win.utils
 * fileName：      PictureSelectorUtils
 * author：        QING
 * createTime：    2018/11/14 14:47
 * describe：      TODO 图片选择器工具类
 */
public class PictureSelectorUtils {

    /**
     * 选择头像
     *
     * @param context 上下文
     * @param type    0.相册 1.拍照
     */
    static public void selectPortrait(Activity activity, Fragment context, int type) {
        if (type == 1) {
            if(activity == null)
                return;
            RxPermissions permissions = new RxPermissions(activity);
            permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        PictureSelector.create(context)
                                .openCamera(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll
                                // ()、图片
                                // .ofImage()、视频.ofVideo()、音频.ofAudio()
                                .theme(R.style.picture_default_style)//主题样式(不设置为默认样式)
                                // 也可参考demo
                                // values/styles下 例如：R.style.picture.white.style
                                .maxSelectNum(1)// 最大图片选择数量 int
                                .minSelectNum(1)// 最小选择数量 int
                                .imageSpanCount(4)// 每行显示个数 int
                                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig
                                // .MULTIPLE
                                // or PictureConfig.SINGLE
                                .previewImage(true)// 是否可预览图片 true or false
                                .previewVideo(true)// 是否可预览视频 true or false
                                .enablePreviewAudio(false) // 是否可播放音频 true or false
                                .isCamera(false)// 是否显示拍照按钮 true or false
                                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                .enableCrop(true)// 是否裁剪 true or false
                                .compress(true)// 是否压缩 true or false
                                //.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                                .isGif(false)// 是否显示gif图片 true or false
                                //.compressSavePath(getPath())//压缩图片保存地址
                                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                .openClickSound(false)// 是否开启点击声音 true or false
                                //.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
                                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true
                                // or false
                                //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                                //.minimumCompressSize(100)// 小于100kb的图片不压缩
                                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                                .isDragFrame(true)// 是否可拖动裁剪框(固定)
                                .forResult(CWConstant.REQUEST_CAMERA);
                    } else
                        XToastUtils.toast(R.string.picture_jurisdiction);
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            });
        } else {
            PictureSelector.create(context)
                    .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片
                    // .ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)//主题样式(不设置为默认样式) 也可参考demo
                    // values/styles下 例如：R.style.picture.white.style
                    .maxSelectNum(1)// 最大图片选择数量 int
                    .minSelectNum(1)// 最小选择数量 int
                    .imageSpanCount(4)// 每行显示个数 int
                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE
                    // or PictureConfig.SINGLE
                    .previewImage(true)// 是否可预览图片 true or false
                    .previewVideo(true)// 是否可预览视频 true or false
                    .enablePreviewAudio(false) // 是否可播放音频 true or false
                    .isCamera(false)// 是否显示拍照按钮 true or false
                    .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .enableCrop(true)// 是否裁剪 true or false
                    .compress(true)// 是否压缩 true or false
                    //.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                    .isGif(false)// 是否显示gif图片 true or false
                    //.compressSavePath(getPath())//压缩图片保存地址
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                    .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                    .openClickSound(false)// 是否开启点击声音 true or false
                    //.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                    //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                    //.minimumCompressSize(100)// 小于100kb的图片不压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                    .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                    .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                    .isDragFrame(true)// 是否可拖动裁剪框(固定)
                    .forResult(CWConstant.REQUEST_GALLREY);
        }
    }

    static public void selectQRImage(Activity activity, Fragment context) {
        PictureSelector.create(context)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片
                // .ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)//主题样式(不设置为默认样式) 也可参考demo
                // values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE
                // or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .enableCrop(false)// 是否裁剪 true or false
                .compress(false)// 是否压缩 true or false
                //.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                //.compressSavePath(getPath())//压缩图片保存地址
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                //.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                //.minimumCompressSize(100)// 小于100kb的图片不压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .isDragFrame(true)// 是否可拖动裁剪框(固定)
                .forResult(CWConstant.REQUEST_GALLREY);
    }

    /**
     * 选择图片
     *
     * @param context 上下文
     * @param type    0.相册 1.拍照
     */
    static public void selectPhoto(Activity context, int type) {
        if (type == 1) {
            PictureSelector.create(context)
                    .openCamera(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll
                    // ()、图片
                    // .ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)//主题样式(不设置为默认样式)
                    // 也可参考demo
                    // values/styles下 例如：R.style.picture.white.style
                    .maxSelectNum(9)// 最大图片选择数量 int
                    .minSelectNum(1)// 最小选择数量 int
                    .imageSpanCount(4)// 每行显示个数 int
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig
                    // .MULTIPLE
                    // or PictureConfig.SINGLE
                    .previewImage(true)// 是否可预览图片 true or false
                    .previewVideo(true)// 是否可预览视频 true or false
                    .enablePreviewAudio(false) // 是否可播放音频 true or false
                    .isCamera(false)// 是否显示拍照按钮 true or false
                    .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .enableCrop(false)// 是否裁剪 true or false
                    .compress(true)// 是否压缩 true or false
                    //.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                    .isGif(false)// 是否显示gif图片 true or false
                    //.compressSavePath(getPath())//压缩图片保存地址
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                    .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                    .openClickSound(false)// 是否开启点击声音 true or \false
                    //.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true
                    // or false
                    //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                    //.minimumCompressSize(100)// 小于100kb的图片不压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                    .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                    .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                    .isDragFrame(true)// 是否可拖动裁剪框(固定)
                    .forResult(CWConstant.REQUEST_CAMERA);
        } else {
            PictureSelector.create(context)
                    .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片
                    // .ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)//主题样式(不设置为默认样式) 也可参考demo
                    // values/styles下 例如：R.style.picture.white.style
                    .maxSelectNum(9)// 最大图片选择数量 int
                    .minSelectNum(1)// 最小选择数量 int
                    .imageSpanCount(4)// 每行显示个数 int
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE
                    // or PictureConfig.SINGLE
                    .previewImage(true)// 是否可预览图片 true or false
                    .previewVideo(true)// 是否可预览视频 true or false
                    .enablePreviewAudio(false) // 是否可播放音频 true or false
                    .isCamera(false)// 是否显示拍照按钮 true or false
                    .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .enableCrop(false)// 是否裁剪 true or false
                    .compress(true)// 是否压缩 true or false
                    //.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                    .isGif(false)// 是否显示gif图片 true or false
                    //.compressSavePath(getPath())//压缩图片保存地址
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                    .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                    .openClickSound(false)// 是否开启点击声音 true or false
                    //.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                    //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                    //.minimumCompressSize(100)// 小于100kb的图片不压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                    .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                    .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                    .isDragFrame(true)// 是否可拖动裁剪框(固定)
                    .forResult(CWConstant.REQUEST_GALLREY);
        }
    }

    /**
     * 初始化权限
     *
     * @param activity 上下文
     */
    public static void initRxPermissions(Activity activity) {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(activity);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean)
                    PictureFileUtils.deleteCacheDirFile(activity);
                else
                    XToastUtils.toast(R.string.picture_jurisdiction);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }
}

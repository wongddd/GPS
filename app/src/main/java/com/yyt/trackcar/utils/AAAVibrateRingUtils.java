package com.yyt.trackcar.utils;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

/**
 * @ projectName:
 * @ packageName:   com.yyt.duda.utils
 * @ fileName:      AAAVibrateRingUtils
 * @ author:        QING
 * @ createTime:    5/12/21 20:14
 * @ describe:      TODO
 */
public class AAAVibrateRingUtils {
    private MediaPlayer mMediaPlayer;
    private boolean mIsVibrating;
    private Context mContext;

    private static AAAVibrateRingUtils mAAAVibrateRingUtils = null;

    private AAAVibrateRingUtils() {
    }

    public static AAAVibrateRingUtils getInstance() {
        synchronized (AAAVibrateRingUtils.class) {
            if (mAAAVibrateRingUtils == null) {
                mAAAVibrateRingUtils = new AAAVibrateRingUtils();
            }
        }
        return mAAAVibrateRingUtils;
    }


    // 初始化函数
    public void initialize(Context context) {
        mContext = context;
    }

    public void startRingVibrate() {
        stopRingVibrate();
        playRing();
        vibrate(1000);
    }

    public void stopRingVibrate() {
        stopRing();
        virateCancle();
    }

    /**
     * 让手机振动milliseconds毫秒
     */
    private void vibrate(long milliseconds) {
        if (mContext == null || mIsVibrating)
            return;
        Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib.hasVibrator()) {  //判断手机硬件是否有振动器
            mIsVibrating = true;
            vib.vibrate(milliseconds);
        }
    }

    /**
     * 让手机以我们自己设定的pattern[]模式振动
     * long pattern[] = {1000, 20000, 10000, 10000, 30000};
     */
    private void vibrate(long[] pattern, int repeat) {
        if (mContext == null || mIsVibrating)
            return;
        Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib.hasVibrator()) {
            mIsVibrating = true;
            vib.vibrate(pattern, repeat);
        }
    }

    /**
     * 取消震动
     */
    private void virateCancle() {
        if (mContext == null || !mIsVibrating)
            return;
        //关闭震动
        Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib.hasVibrator()) {
            mIsVibrating = false;
            vib.cancel();
        }
    }

    //开始播放
    private void playRing() {
        if (mContext == null || (mMediaPlayer != null && mMediaPlayer.isPlaying()))
            return;
        try {
            stopRing();
            //用于获取手机默认铃声的Uri
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, alert);
            //告诉mediaPlayer播放的是铃声流
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            stopRing();
        }
    }

    //停止播放
    private void stopRing() {
        try {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

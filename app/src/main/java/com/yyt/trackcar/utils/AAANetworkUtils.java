package com.yyt.trackcar.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.socks.library.KLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;

public class AAANetworkUtils {
    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isNetworkAvailable(Activity activity) {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /* @author suncat
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @return
     */
    public static boolean ping() {
        String result = null;
        try {
            String ip = "www.google.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    public static void pingNet(){
        try {
            if (InetAddress.getByName("183.232.231.174").isReachable(3000)){
                KLog.d("morse pingNet succeed");
            }else{
                KLog.d("morse pingNet failed");
            }
        }catch (Exception e){
            KLog.d("an error occurred");
        }
    }

    public static void analysisNet() {
        // 这种方式如果ping不通 会阻塞一分钟左右
        // 也是要放在另一个线程里面ping
        try {
            InetAddress addr = InetAddress.getByName("www.google.com");
            if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                Log.d("morse", "analysisNet onSuccess ");
            } else {
                Log.d("morse", "analysisNet onFailure 0");
            }
        } catch (Throwable e) {
            Log.d("morse", "analysisNet onFailure 1 " + e);
        }
    }
}

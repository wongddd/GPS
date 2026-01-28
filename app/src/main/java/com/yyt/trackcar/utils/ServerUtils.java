package com.yyt.trackcar.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      ServerUtils
 * @ author:        QING
 * @ createTime:    2020/7/14 12:30
 * @ describe:      TODO
 */
public class ServerUtils {

    public static String getServerIp() {
        String ipUrl;
//        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
//            if (CWConstant.APP_TEST) {
//                int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 2);
//                if (serverType == 0)
//                    ipUrl = CWConstant.SERVER_ADDRESS_SECOND;
//                else if (serverType == 1)
//                    ipUrl = CWConstant.SERVER_ADDRESS_AMERICA_SECOND;
//                else if (serverType == 3)
//                    ipUrl = CWConstant.SERVER_ADDRESS_EUROPE_SECOND;
//                else if (serverType == 4)
//                    ipUrl = CWConstant.SERVER_ADDRESS_CHINA_SECOND;
//                else
//                    ipUrl = CWConstant.SERVER_ADDRESS_CHINA_SECOND;
//            } else {
//                int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
//                if (serverType == 1)
//                    ipUrl = CWConstant.SERVER_ADDRESS_AMERICA_SECOND;
//                else if (serverType == 3)
//                    ipUrl = CWConstant.SERVER_ADDRESS_EUROPE_SECOND;
//                else if (serverType == 4)
//                    ipUrl = CWConstant.SERVER_ADDRESS_CHINA_SECOND;
//                else
//                    ipUrl = CWConstant.SERVER_ADDRESS_SECOND;
//            }
//        } else {
//            if (CWConstant.APP_TEST) {
//                int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 2);
//                if (serverType == 0)
//                    ipUrl = CWConstant.SERVER_ADDRESS;
//                else if (serverType == 1)
//                    ipUrl = CWConstant.SERVER_ADDRESS_AMERICA;
//                else if (serverType == 3)
//                    ipUrl = CWConstant.SERVER_ADDRESS_EUROPE;
//                else if (serverType == 4)
//                    ipUrl = CWConstant.SERVER_ADDRESS_CHINA_TWO;
//                else
//                    ipUrl = CWConstant.SERVER_ADDRESS_CHINA;
//            } else {
//                int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
//                if (serverType == 1)
//                    ipUrl = CWConstant.SERVER_ADDRESS_AMERICA;
//                else if (serverType == 3)
//                    ipUrl = CWConstant.SERVER_ADDRESS_EUROPE;
//                else if (serverType == 4)
//                    ipUrl = CWConstant.SERVER_ADDRESS_CHINA_TWO;
//                else
//                    ipUrl = CWConstant.SERVER_ADDRESS;
//            }
//        }

        int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
        if (serverType == 5)
            ipUrl = CWConstant.SERVER_ADDRESS_AMERICA_TWO;
        else
            ipUrl = CWConstant.SERVER_ADDRESS_CHINA_TWO;
        return ipUrl;
    }

    public static String getCustomServerIp() {
        String ipUrl;
//        if (CWConstant.APP_TEST) {
//            int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 2);
//            if (serverType == 0)
//                ipUrl = CWConstant.SERVER_ADDRESS;
//            else if (serverType == 1)
//                ipUrl = CWConstant.SERVER_ADDRESS_AMERICA;
//            else if (serverType == 3)
//                ipUrl = CWConstant.SERVER_ADDRESS_EUROPE;
//            else if (serverType == 4)
//                ipUrl = CWConstant.SERVER_ADDRESS_CHINA_TWO;
//            else
//                ipUrl = CWConstant.SERVER_ADDRESS_CHINA;
//        } else {
//            int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
//            if (serverType == 1)
//                ipUrl = CWConstant.SERVER_ADDRESS_AMERICA;
//            else if (serverType == 3)
//                ipUrl = CWConstant.SERVER_ADDRESS_EUROPE;
//            else if (serverType == 4)
//                ipUrl = CWConstant.SERVER_ADDRESS_CHINA_TWO;
//            else
//                ipUrl = CWConstant.SERVER_ADDRESS;
//        }

        int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
        if (serverType == 5)
            ipUrl = CWConstant.SERVER_ADDRESS_AMERICA_TWO;
        else
            ipUrl = CWConstant.SERVER_ADDRESS_CHINA_TWO;
        return ipUrl;
    }

    public static String getRequestUrl(String ip) {
        String ipUrl;
//        int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
//        if (serverType == 4)
//            ipUrl = String.format(CWConstant.SERVER_IP_CHINA_TWO, ip);
//        else if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
//            ipUrl = String.format(CWConstant.SERVER_IP_SECOND, ip);
//        else
//            ipUrl = String.format(CWConstant.SERVER_IP, ip);

        ipUrl = String.format(CWConstant.SERVER_IP_CHINA_TWO, ip);
        return ipUrl;
    }

    public static String getCustomRequestUrl(String ip) {
        String ipUrl;
//        int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
//        if (serverType == 4)
//            ipUrl = String.format(CWConstant.SERVER_IP_CHINA_TWO, ip);
//        else
//            ipUrl = String.format(CWConstant.SERVER_IP, ip);

        ipUrl = String.format(CWConstant.SERVER_IP_CHINA_TWO, ip);
        return ipUrl;
    }

    public static String getNewServiceIp(){
        String ipUrl;
        ipUrl = SettingSPUtils.getInstance().getString(TConstant.SERVER_ADDRESS,TConstant.DEFAULT_IP);
        return ipUrl;
    }

    public static List<String> getServerList(){
        ArrayList<String> serverList;
        serverList = (ArrayList<String>)SettingSPUtils.getInstance().get(TConstant.SERVER_LIST);
        return serverList;
    }

}

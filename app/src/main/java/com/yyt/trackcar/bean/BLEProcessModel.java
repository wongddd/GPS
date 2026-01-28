package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      BLEProcessModel
 * @ author:        QING
 * @ createTime:    2023/4/26 11:28
 * @ describe:      TODO 蓝牙步骤对象
 */
public class BLEProcessModel {

   private String macAddress; // mac地址
   private int processType; // 进度类型
   private String matchId; // 读取的赛场ID
   private int locationCount; // 定位点数

   public String getMacAddress() {
      return macAddress;
   }

   public void setMacAddress(String macAddress) {
      this.macAddress = macAddress;
   }

   public int getProcessType() {
      return processType;
   }

   public void setProcessType(int processType) {
      this.processType = processType;
   }

   public String getMatchId() {
      return matchId;
   }

   public void setMatchId(String matchId) {
      this.matchId = matchId;
   }

   public int getLocationCount() {
      return locationCount;
   }

   public void setLocationCount(int locationCount) {
      this.locationCount = locationCount;
   }

}

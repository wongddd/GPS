package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      CommandMessageBean
 * @ author:        QING
 * @ createTime:    2023/7/11 15:46
 * @ describe:      TODO 指令消息对象
 */
public class CommandMessageBean {

   private long id; // id
   private String deviceImei; // 设备IME
   private int commandFlag; // 指令状态  0 待发送，1 已发送 2 已确认、3 已接收 4、已回复
   private int commandMode; // 接收/发送标志  0 接收、1 发送
   private String requestData; // 发送数据
   private String commandRemark; // 备注
   private String receiveCommand; // 指令
   private String sendTime; // 指令下发设备时间
   private String createTime; // 创建时间

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getDeviceImei() {
      return deviceImei;
   }

   public void setDeviceImei(String deviceImei) {
      this.deviceImei = deviceImei;
   }

   public int getCommandFlag() {
      return commandFlag;
   }

   public void setCommandFlag(int commandFlag) {
      this.commandFlag = commandFlag;
   }

   public int getCommandMode() {
      return commandMode;
   }

   public void setCommandMode(int commandMode) {
      this.commandMode = commandMode;
   }

   public String getRequestData() {
      return requestData;
   }

   public void setRequestData(String requestData) {
      this.requestData = requestData;
   }

   public String getCommandRemark() {
      return commandRemark;
   }

   public void setCommandRemark(String commandRemark) {
      this.commandRemark = commandRemark;
   }

   public String getReceiveCommand() {
      return receiveCommand;
   }

   public void setReceiveCommand(String receiveCommand) {
      this.receiveCommand = receiveCommand;
   }

   public String getSendTime() {
      return sendTime;
   }

   public void setSendTime(String sendTime) {
      this.sendTime = sendTime;
   }

   public String getCreateTime() {
      return createTime;
   }

   public void setCreateTime(String createTime) {
      this.createTime = createTime;
   }

}

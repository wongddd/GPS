package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      AAATrackRequestBeanOldEdition
 * @ author:        QING
 * @ createTime:    2020-02-21 18:01
 * @ describe:      TODO 请求对象
 */
public class RequestBean {
    private String username; // 用户名
    private String pwd; // 密码
    private String code; // 验证码
    private String type; // 类型
    private String country; // 国家编码
    private String token; // token
    private long u_id; // 用户id
    private String imei; // imei
    private long d_id; // 设备d_id
    private String time; // 定位模式
    private long id; // 设备id
    private String wifi; // 家庭wifi
    private String birday; // 生日
    private int sex; // 性别0女1男
    private String weight; // 体重
    private String height; // 身高
    private String head; // 头像
    private String name; // 昵称
    private String nickname; // 昵称
    private String school_age; // 年级
    private String phone; // 电话
    private String shortNumber; // 短号
    private String familyNumber; // 家庭号
    private String school_info; // 学校信息
    private String home_info; // 家庭信息
    private String disabledInClass; // 上课禁用
    private String other; // 其他设置
    private String come; // 语音监听手机号
    private String alarm_clock; // 闹钟设置
    private String wifiStatus; // 有新版本自动安装  1打开 0关闭
    private String webTraffic; // 是否打开流量下载  1打开  0关闭
    private String phonebook; // 通讯录
    private String steps; // APP设置的目标步数
    private String devicestep; // 设备今日步数
    private int arriveHome; // 到家
    private int sos; // SOS
    private int location; // 定位
    private int addFriend; // 添加朋友
    private int step; // 计步
    private int uploadPhoto; // 上传图片
    private int phoneLog; // 通话日志
    private int cost; // 短信
    private int upgrade; // 升级
    private int fence; // 电子围栏
    private String lat; // 纬度
    private String lng; // 经度
    private String startTime; // 开始时间
    private String endTime;
    private String sendId; // 发送id
    private String user_id; // 用户id
    private String mail; // 邮箱
    private int status; // wifi验证状态
    private String headurl; // 头像
    private String info;
    private String deviceImei;
    private Integer positionType;
    private Integer positionInterval;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getD_id() {
        return d_id;
    }

    public void setD_id(long d_id) {
        this.d_id = d_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getBirday() {
        return birday;
    }

    public void setBirday(String birday) {
        this.birday = birday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSchool_age() {
        return school_age;
    }

    public void setSchool_age(String school_age) {
        this.school_age = school_age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShortNumber() {
        return shortNumber;
    }

    public void setShortNumber(String shortNumber) {
        this.shortNumber = shortNumber;
    }

    public String getFamilyNumber() {
        return familyNumber;
    }

    public void setFamilyNumber(String familyNumber) {
        this.familyNumber = familyNumber;
    }

    public String getSchool_info() {
        return school_info;
    }

    public void setSchool_info(String school_info) {
        this.school_info = school_info;
    }

    public String getHome_info() {
        return home_info;
    }

    public void setHome_info(String home_info) {
        this.home_info = home_info;
    }

    public String getDisabledInClass() {
        return disabledInClass;
    }

    public void setDisabledInClass(String disabledInClass) {
        this.disabledInClass = disabledInClass;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getCome() {
        return come;
    }

    public void setCome(String come) {
        this.come = come;
    }

    public String getAlarm_clock() {
        return alarm_clock;
    }

    public void setAlarm_clock(String alarm_clock) {
        this.alarm_clock = alarm_clock;
    }

    public String getWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(String wifiStatus) {
        this.wifiStatus = wifiStatus;
    }

    public String getWebTraffic() {
        return webTraffic;
    }

    public void setWebTraffic(String webTraffic) {
        this.webTraffic = webTraffic;
    }

    public String getPhonebook() {
        return phonebook;
    }

    public void setPhonebook(String phonebook) {
        this.phonebook = phonebook;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDevicestep() {
        return devicestep;
    }

    public void setDevicestep(String devicestep) {
        this.devicestep = devicestep;
    }

    public int getArriveHome() {
        return arriveHome;
    }

    public void setArriveHome(int arriveHome) {
        this.arriveHome = arriveHome;
    }

    public int getSos() {
        return sos;
    }

    public void setSos(int sos) {
        this.sos = sos;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getAddFriend() {
        return addFriend;
    }

    public void setAddFriend(int addFriend) {
        this.addFriend = addFriend;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getUploadPhoto() {
        return uploadPhoto;
    }

    public void setUploadPhoto(int uploadPhoto) {
        this.uploadPhoto = uploadPhoto;
    }

    public int getPhoneLog() {
        return phoneLog;
    }

    public void setPhoneLog(int phoneLog) {
        this.phoneLog = phoneLog;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(int upgrade) {
        this.upgrade = upgrade;
    }

    public int getFence() {
        return fence;
    }

    public void setFence(int fence) {
        this.fence = fence;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public Integer getPositionType() {
        return positionType;
    }

    public void setPositionType(Integer positionType) {
        this.positionType = positionType;
    }

    public Integer getPositionInterval() {
        return positionInterval;
    }

    public void setPositionInterval(Integer positionInterval) {
        this.positionInterval = positionInterval;
    }

}

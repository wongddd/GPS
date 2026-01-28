package com.yyt.trackcar.utils;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      TConstant
 * @ author:        QING
 * @ createTime:    6/19/21 14:13
 * @ describe:      TODO
 */
public interface TConstant {

    String USERNAME = "username"; // 用户名
    String PASSWORD = "password"; // 密码
    String SERVER_ADDRESS = "server_address"; // 服务器地址
    /**
     * 地图类型:0.高德地图 1.谷歌地图
     */
    String MAP_TYPE = "map_type";
    String USER_ID = "user_id"; // 用户ID
    String IMEI_NO = "imei_no";
    String START_TIME = "start_time";
    String END_TIME = "end_time";
    String BEAN = "bean"; // 对象
    String TYPE = "type"; // 对象
    String ROLES = "roles";

    String IP = "api.globalgpstrace.com"; // IP地址
    String URL_USER_LOGIN = "http://%s/app/user/login"; // 登录
    int REQUEST_URL_USER_LOGIN = 1000;
    String URL_GET_DEVICE_LIST = "http://%s/app/device/listDevice";
    int REQUEST_URL_GET_DEVICE_LIST = 1001;
    String URL_GET_LAST_LOCATION = "http://%s/app/device/getSingleDevice";
    int REQUEST_URL_GET_LAST_LOCATION = 1002;
    String URL_GET_HISTORY_LOCATION = "http://%s/app/gps/location/deviceTrackLog";
    int REQUEST_URL_GET_HISTORY_LOCATION = 1003;
    String URL_SEND_COMMAND = "http://%s/app/gps/command/sendRemoteControl";
    int REQUEST_URL_SEND_COMMAND = 1004;
    String URL_GET_COMMAND_RESULT = "http://%s/Api/CommandResult.aspx";
    int REQUEST_URL_GET_COMMAND_RESULT = 1005;
    String URL_ADD_GEO_FENCE = "http://%s/app/circularFence/insertCircularFence";
    int REQUEST_URL_ADD_GEO_FENCE = 1006;
    String URL_DEL_GEO_FENCE = "http://%s/app/circularFence/deleteCircularFence";
    int REQUEST_URL_DEL_GEO_FENCE = 1007;
    String URL_UPDATE_GEO_FENCE = "http://%s/app/circularFence/updateCircularFence";
    int REQUEST_URL_UPDATE_GEO_FENCE = 1008;
    String URL_GET_GEO_FENCE_LIST = "http://%s/app/circularFence/list";
    int REQUEST_URL_GET_GEO_FENCE_LIST = 1009;
    String URL_CHANGE_PASSWORD = "http://%s/app/user/changePassword";
    int REQUEST_URL_CHANGE_PASSWORD = 1011;
    String URL_GET_ALARM_LIST = "http://%s/Api/AlarmList.aspx";
    int REQUEST_URL_GET_ALARM_LIST = 1012;
    String URL_GET_TRIP_LIST = "http://%s/Api/TripList.aspx";
    int REQUEST_URL_GET_TRIP_LIST = 1013;
    String URL_GET_PIC_LIST = "http://%s/Api/PicList.aspx";
    int REQUEST_URL_GET_PIC_LIST = 1014;
    String URL_GET_TRACK_SUM_LIST = "http://%s/Api/TrackSum.aspx";
    int REQUEST_URL_GET_TRACK_SUM_LIST = 1015;
    String URL_GET_CUSTOMER_LIST = "http://%s/app/device/listCustomer"; // app获取公司列表（公司、车队、设备 3级结构）
    int REQUEST_URL_GET_CUSTOMER_LIST = 1016;
    String URL_SEND_REMOTE_CONTROL = "http://%s/app/gps/command/sendRemoteControl"; //
    // 发送远程控制命令（停车、恢复、开门、关门、启动、停车（10KM执行）、拍照）
    int REQUEST_URL_SEND_REMOTE_CONTROL = 1017;
    String URL_SET_TIME_INTERVAL_FOR_TRACKING = "http://%s/app/gps/command" +
            "/setTimeIntervalForTracking"; // 设置跟踪时间间隔
    int REQUEST_URL_SET_TIME_INTERVAL_FOR_TRACKING = 1018;
    String URL_SET_TIME_ZONE = "http://%s/app/gps/command/setTimeZone"; // 设置时区
    int REQUEST_URL_SET_TIME_ZONE = 1019;
    String URL_SET_ANGLE_FOR_TRACKING = "http://%s/app/gps/command/setAngleForTracking"; // 设置跟踪角度
    int REQUEST_URL_SET_ANGLE_FOR_TRACKING = 1020;
    String URL_SET_ODOMETER_FOR_INTERVAL = "http://%s/app/gps/command/setOdometerInterval"; //
    // 设置跟踪里程
    int REQUEST_URL_SET_ODOMETER_FOR_INTERVAL = 1021;
    String URL_SET_TELEPHONE_FOR_WIRETAPPING = "http://%s/app/gps/command" +
            "/setTelePhoneForWiretapping"; // 设置监听号码
    int REQUEST_URL_SET_TELEPHONE_FOR_WIRETAPPING = 1022;
    String URL_SET_SPEED_LIMIT = "http://%s/app/gps/command/setSpeedLimit"; // 设置速度限制
    int REQUEST_URL_SET_SPEED_LIMIT = 1023;
    String URL_SET_MOVEMENT_ALERT = "http://%s/app/gps/command/setMovementAlert"; // 设置移动警报
    int REQUEST_URL_SET_MOVEMENT_ALERT = 1024;
    String URL_SET_AUTHORIZED_PHONE_NUMBER =
            "http://%s/app/gps/command/setAuthorizedPhoneNumber"; // 设置授权号码
    int REQUEST_URL_SET_AUTHORIZED_PHONE_NUMBER = 1025;
    String URL_REQUEST_SINGLE_LOCATION = "http://%s/app/gps/command/requestSingleLocation"; //
    // 下发一次定位指令
    int REQUEST_URL_REQUEST_SINGLE_LOCATION = 1026;
    String URL_READ_DEVICE_VERSION = "http://%s/app/gps/command/readDeviceVersion"; // 读取设备版本号
    int REQUEST_URL_READ_DEVICE_VERSION = 1027;
    String URL_READ_INTERVAL_FOR_TRACKING = "http://%s/app/gps/command/readIntervalForTracking";
    // 读取跟踪时间间隔
    int REQUEST_URL_READ_INTERVAL_FOR_TRACKING = 1028;
    String URL_READ_ODOMETER_FOR_TRACKING = "http://%s/app/gps/command/readOdometerForTracking";
    // 读取跟踪里程间隔
    int REQUEST_URL_READ_ODOMETER_FOR_TRACKING = 1029;
    String URL_GET_ODOMETER_REPORT = "http://%s/app/gps/report/appGetOdometerReport"; // APP请求查询里程报表
    int REQUEST_URL_GET_ODOMETER_REPORT = 1030;
    String URL_GET_LAST_DEVICE_CONFIG_TRACK = "http://%s/app/gps/location" +
            "/getLastConfigDeviceTraceLog";
    int REQUEST_URL_GET_LAST_DEVICE_CONFIG_TRACK = 1031;
    String URL_GET_MULTIPLE_DEVICE_TRACE_LOG = "http://%s/app/gps/location" +
            "/getMultipleLastConfigDeviceTraceLog";
    int REQUEST_URL_GET_MULTIPLE_DEVICE_TRACE_LOG = 1032;
    String URL_MAIN = "http://%s/MainServlet";
    String DISTRIBUTION_MANAGEMENT_URL  = "http://xingeh5.gps866.com/#/subordinate?uid=%s&token=%s";
    String URL_MULTIPLE_HISTORY_AMAP = "http://xingeh5.gps866.com/#/appMultipleHistory?id=%s&token=%s";
    String URL_MULTIPLE_HISTORY_GOOGLE_MAP = "http://xingeh5.gps866.com/#/appMultipleHistoryGoogle?id=%s&token=%s";
    String URL_HISTORY_AMAP = "http://xingeh5.gps866.com/#/appAnalyzeTrajectoryAmap?token=%s&uid=%s&deviceImei=%s";
    String URL_HISTORY_GOOGLE_MAP = "http://xingeh5.gps866.com/#/appAnalyzeTrajectoryGoogle?token=%s&uid=%s&deviceImei=%s";
    String URL_SYSTEM_CONFIGURE = "http://xingeh5.gps866.com/#/systemConfigure?id=%s&token=%s";

    int RESPONSE_SUCCESS = 0;  //成功
    int RESPONSE_SUCCESS_NEW = 1;
    int RESPONSE_NET_ERROR = -88;  // 网络异常

    int RESPONSE_INTERNAL_ERROR = 1000;  // internal error
    int RESPONSE_REQ_PARAMETER_ERROR = 1001;  // The requested parameter is wrong
    int RESPONSE_ILLEGAL_REQUEST = 1002;  // Illegal request
    int RESPONSE_TOKEN_IS_EXPIRED = 2001;  // token has expired
    int RESPONSE_TOKEN_IS_EMPTY = 2002;  // The token in the request header cannot be empty
    int RESPONSE_TOKEN_IS_ERROR = 2003;  // Incorrect token
    int RESPONSE_USER_NOT_LOGIN = 2004;  // User not logged in
    int RESPONSE_USER_ID_IS_EMPTY = 2005;  // The user ID in the request header cannot be empty
    int RESPONSE_USER_ID_IS_ERROR = 2006;  // Incorrect user ID in request header
    int RESPONSE_NO_CLIENT_FLAG = 2007;  // There is no clientFlag in the request header
    int RESPONSE_CLIENT_FLAG_ERROR = 2008;  // clientFlag only in 1, 2
    int RESPONSE_NO_SESSION = 2009;  // There is no session in the request header
    int RESPONSE_AUTH_KEY_FORBID = 21000;  // Authorization code is invalid
    int RESPONSE_AUTH_KEY_EXPIRED = 21001;  // Authorization code has expired
    int RESPONSE_USER_IS_NOT_EXISTS_OR_PASSWORD_ERROR = 3000;  // The user does not exist or the
    // password is incorrect
    int RESPONSE_USER_IS_NOT_EXISTS = 3001;  // user does not exist
    int RESPONSE_DEVICE_NOT_EXISTS = 3002;  // The device does not exist
    int RESPONSE_RECEIVE_SMS_AND_RECEIVE_CALL_PHONE_IS_NULL = 3003;  // Authorized phone number
    // is Null
    int RESPONSE_DEVICE_NO_LINK = 3004;  // device is offline
    int RESPONSE_SET_MOVEMENT_ALARM_AREA_ERROR = 3005;  // Incorrect setting of movement warning
    // distance
    int RESPONSE_OLD_PASSWORD_ERROR = 3006;  // The old password is incorrect
    int RESPONSE_PASSWORD_INCORRECT = 3011; // The password incorrect
    int RESPONSE_DEVICE_ALREADY_BIND = 3013; // device already bind and cannot be bound repeatedly
    int RESPONSE_COMMAND_ERROR = 4000;  // 指令错误
    int RESPONSE_OUTPUT_NUMBER_NOT_CONFIG = 4001;  // The send output number of the instruction
    // is not config
    int RESPONSE_DEPT_ID_IS_ERROR = 5000;  // Incorrect Department ID
    int RESPONSE_DEPT_NAME_IS_EXISTS = 5001;  // Department name is exists
    int RESPONSE_DEVICE_CONTROL_ITEM_NOT_CONFIG = 5002;  // control item not config
    int RESPONSE_POLYGONAL_FENCE_LESS_THREE_POINTS = 5003;  // The polygon cannot have less than
    // 3 points
    int RESPONSE_CUSTOMER_NOT_EXISTS = 5004;  // customer does not exist

    int REQUEST_ADD_ELECTRONIC = 6001; // 添加围栏
    int REQUEST_EDIT_ELECTRONIC = 6002; // 编辑围栏
    int REQUEST_QRCODE_SCANNER = 6003; // 扫描二维码
    int REQUEST_GALLREY = 6004; // 相册
    int REQUEST_CAMERA = 6005; // 拍照
    int REQUEST_INPUT = 6006;
    int REQUEST_INPUT_AUTH = 6007;
    int REQUEST_INPUT_RFID = 6008;
    int REQUEST_PERMISSION_READ_PHONE_STATE = 10001; // 读取手机imei权限
    int REQUEST_PERMISSION_AMAP = 10002; // 高德地图定位权限
    int REQUEST_PERMISSION_MOBILE_LOCATION = 10003; // 手机定位权限
    int REQUEST_PERMISSION_WRITE_READ = 10004; // 手机读写权限
    int REQUEST_PERMISSION_CAMERA = 10005; // 摄像头权限
    int REQUEST_PERMISSION_RECORD = 10006; // 录音权限
    int REQUEST_PERMISSION_CALL_PHONE = 10007; // 打电话权限

    int REQUEST_LIST_NUM = 20;

    int NOTIFY_PARENT_VIEW_TO_FINISH = 0x100123;

    int ADD_SERVER_ADDRESS = 0x100001;
    int ADD_GEO_FENCE = 0x100002;
    int CONNECT_TIMEOUT = 60;
    int READ_TIMEOUT = 60;

    String ACTION = "action";
    String METHOD = "method";
    String PARAMETER = "parameter";
    String CLIENT_FLAG = "clientFlag";
    String CLIENT_FLAG_VALUE = "1";
    String DEFAULT_URL = "http://%s/MainServlet";
    String DEFAULT_IP = "gps.518915.com:8080";
    String GPS_IP = "gps.518915.com:8080";
    String UID = "uid";
    String DATA = "data";
    String PREFIX_OF_URL = "http://";
    String TOKEN = "token";
    String USER_ID_NEW = "userId";
    String TRACK_DEVICE_MODEL = "trackDeviceModel";
    String TRACK_USER_MODEL = "trackUserModel";
    String SELECTED_IMEI = "selected_imei"; // imei of the selected device
    String SELECTED_INDEX = "indexOfSelectedDevice";
    String TITLE = "title";
    String PAGE_INDEX = "pageIndex";
    String PAGE_SIZE = "pageSize";
    String DEVICE_IMEI = "deviceImei";
    String DEVICE_TYPE = "deviceType";
    String IS_AGENT = "isAgent";
    String CONFIGURATION = "configuration";
    String WHERE_FROM = "whereFrom";
    String LOCATION_REFRESH_INTERVAL = "locationRefreshInterval";

    String IMAGE_MAP = "image_map";
    String SERVER_LIST = "server_list";
    String SERIALIZABLE = "serializable";
    String PARCELABLE = "parcelable";

    String DEVICE_HEAD_PORTRAIT = "headPic";

    int REQUEST_SERVER_ADDRESS = 20001;

    int RESPONSE_FAILED = -0x001;

    // 各网络请求的识别码
    int REQUEST_ACCOUNT_LOGIN = 0x101;
    int REQUEST_IMEI_LOGIN = 0x102;
    int REQUEST_GET_VERIFICATION_CODE = 0x103;
    int REQUEST_VERIFY_VERIFICATION_CODE = 0x104;
    int REQUEST_REGISTER_BY_EMAIL = 0x105;
    int REQUEST_REGISTER_BY_MOBILE_PHONE = 0x106;
    int REQUEST_BIND_DEVICE = 0x107;
    int REQUEST_UNBIND_DEVICE = 0x108;
    int REQUEST_FORGET_PASSWORD_BY_EMAIL = 0x109;
    int REQUEST_FORGET_PASSWORD_BY_PHONE = 0x110;
    int REQUEST_RESET_PASSWORD = 0x111;
    int REQUEST_UPLOAD_DEVICE_HEAD_PORTRAIT = 0x112;
    int REQUEST_GET_DEVICE_LIST_OF_PIGEON_RACE_LIVE = 0x113;
    int REQUEST_GET_PIGEON_RACE_LIST = 0x114;
    int REQUEST_RESET_NICKNAME = 0x115;
    int REQUEST_BIND_MOBILE_FOR_DEVICE = 0x116;
    int REQUEST_GET_DEVICE_RACE_CONFIGURATION = 0x117;
    int REQUEST_UPDATE_DEVICE_RACE_CONFIGURATION = 0x118;
    int REQUEST_GET_FLIGHT_TRAINING_PLAN = 0x119;
    int REQUEST_UPDATE_FLIGHT_TRAINING_VALID_END_TIME = 0x120;
    int REQUEST_DELETE_FLIGHT_TRAINING_PLAN = 0x121;
    int REQUEST_GET_TRACK_CIRCLE_LIST = 0x122;
    int REQUEST_UPLOAD_TRACK_IMAGE = 0x123;
    int REQUEST_SHARE_TRACK_TOT_TRACK_CIRCLE = 0x124;
    int REQUEST_SEARCH_DEVICE_BY_IMEI = 0x125;
    int REQUEST_TRACK_CIRCLE_THUMBS_UP_AND_THUMB_DOWN = 0x126;
    int REQUEST_VIEW_TRACK_CIRCLE = 0x127;
    int REQUEST_DELETE_MY_SHARED_TRACK = 0x128;
    int REQUEST_CREATE_PIGEON_COMPETITION = 0x129;
    int REQUEST_QUERY_PIGEON_COMPETITION = 0x130;
    int REQUEST_DELETE_PIGEON_COMPETITION = 0x131;
    int REQUEST_QUERY_PIGEON_COMPETITION_CONFIGURATION = 0x132;
    int REQUEST_UPDATE_PIGEON_COMPETITION_CONFIGURATION = 0x133;
    int REQUEST_QUERY_DEVICE_OF_PIGEON_COMPETITION = 0x134;
    int REQUEST_UPDATE_DEVICE_OF_COMPETITION = 0x135;
    int REQUEST_QUERY_AGENT_USER = 0x136;
    int REQUEST_BIND_DEVICE_FOR_DEALER = 0x137;
    int REQUEST_UNBIND_DEVICE_FROM_AGENCY = 0x138;
    int REQUEST_QUERY_DEALER_BOUND_DEVICES = 0x139;
    int REQUEST_QUERY_DEALER_AND_SUBORDINATE_DEALERS_BOUND_DEVICES = 0x140;
    int REQUEST_LOG_OFF = 0x141;
    int REQUEST_UPDATE_DEVICE_STATUS = 0x142;
    int REQUEST_DELETE_SUBORDINATE_DEALER = 0x143;
    int REQUEST_ADD_SUBORDINATE_DEALER = 0x144;
    int REQUEST_LOGIN_TO_SUBORDINATE_DEALER_ACCOUNT = 0x145;
    int REQUEST_CREATE_SUBORDINATE_DEALER_ACCOUNT = 0x146;
    int REQUEST_UPDATE_COMPETITION_INFO = 0x147;
    int REQUEST_GET_DEVICE_PRESET_CONFIGURATION = 0x148;
    int REQUEST_UPDATE_DEVICE_PRESET_CONFIGURATION = 0x149;
    int REQUEST_QUERY_UNUNBOUND_LIST = 0x14A;
    int REQUEST_ACTIVATED_DEVICE = 0x14B;
    int REQUEST_UPDATE_EXPIRE_DATE = 0x14C;
    int REQUEST_COMMAND_UPLOAD_INTERVAL = 0x14D;
    int REQUEST_COMMAND_RESTART = 0x14E;
    int REQUEST_COMMAND_SHUT_DOWN = 0x14F;
    int REQUEST_COMMAND_IMMEDIAL_LOCATION = 0x150;
    int REQUEST_COMMAND_FIND = 0x151;
    int REQUEST_COMMAND_FIND_END = 0x152;
    int REQUEST_UPDATE_DEVICE_REMARK = 0x153;
    int REQUEST_SEND_COMMAND = 0x154;
    String URL_GET_MULTIPLE_TRACE_LOG = "http://%s/app/gps/location/getMultipleDeviceTraceLog";
    int REQUEST_GET_MULTIPLE_TRACE_LOG = 0x155;
    int REQUEST_QUERY_SEND_COMMAND_LIST = 0x156;
    int REQUEST_GET_BLE_DEVICE_CONFIG = 0x157; // 获取蓝牙设备配置
    int REQUEST_SEND_BLE_DEVICE_LOCATION = 0x158; // 发送蓝牙设备定位数据
    int REQUEST_UPDATE_GUARANTEE_DATE = 0x159;
    int REQUEST_UPDATE_RACE_STATUS = 0x15A;

}
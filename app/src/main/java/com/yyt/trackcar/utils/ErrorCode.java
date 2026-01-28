package com.yyt.trackcar.utils;

import com.yyt.trackcar.R;

public enum ErrorCode {
    RESPONSE_REQUEST_ERROR(-1, R.string.tip_request_error), // request error(default )
    RESPONSE_INTERNAL_ERROR(1000, R.string.tip_internal_error), // internal error
    RESPONSE_REQ_PARAMETER_ERROR(1001, R.string.tip_request_parameter_error), // The requested parameter is wrong
    RESPONSE_ILLEGAL_REQUEST(1002, R.string.tip_illegal_request),  // Illegal request
    RESPONSE_TOKEN_IS_EXPIRED(2001, R.string.tip_token_has_expired),  // token has expired
    RESPONSE_TOKEN_IS_EMPTY(2002, R.string.tip_token_in_request_header_cannot_be_empty),  // The token in the request header cannot be empty
    RESPONSE_TOKEN_IS_ERROR(2003, R.string.tip_token_incorrect),  // Incorrect token
    RESPONSE_USER_NOT_LOGIN(2004, R.string.tip_user_not_logged_in),  // User not logged in
    RESPONSE_USER_ID_IS_EMPTY(2005, R.string.tip_user_id_in_request_header_cannot_be_empty),  // The user ID in the request header cannot be empty
    RESPONSE_USER_ID_IS_ERROR(2006, R.string.tip_user_id_incorrect),  // Incorrect user ID in request header
    RESPONSE_NO_CLIENT_FLAG(2007, R.string.tip_clientFlag_cannot_be_empty_in_request_header),  // There is no clientFlag in the request header
    RESPONSE_CLIENT_FLAG_ERROR(2008, R.string.tip_clientFlag_only_equal_1_or_2),  // clientFlag only in 1, 2
    RESPONSE_NO_SESSION(2009, R.string.tip_no_session_in_request_header),  // There is no session in the request header
    RESPONSE_USER_IS_NOT_EXISTS_OR_PASSWORD_ERROR(3000, R.string.tip_account_not_exist_or_password_incorrect),  // The user does not exist or the password is incorrect
    RESPONSE_USER_IS_NOT_EXISTS(3001, R.string.tip_user_does_not_exist),  // user does not exist
    RESPONSE_DEVICE_NOT_EXISTS(3002, R.string.tip_device_does_not_exist),  // The device does not exist
    //    RESPONSE_RECEIVE_SMS_AND_RECEIVE_CALL_PHONE_IS_NULL(3003, 0),  // Authorized phone number is Null
//    RESPONSE_DEVICE_NO_LINK(3004, 0),  // device is offline
//    RESPONSE_SET_MOVEMENT_ALARM_AREA_ERROR(3005, 0),  // Incorrect setting of movement warning distance
    RESPONSE_OLD_PASSWORD_ERROR(3006, R.string.tip_old_password_incorrect),  // The old password is incorrect
    RESPONSE_PASSWORD_INCORRECT(3011, R.string.tip_password_error), // The password incorrect
    RESPONSE_DEVICE_ALREADY_BIND(3013, R.string.tip_device_cannot_bind_repeatedly), // device already bind and cannot be bound repeatedly
    RESPONSE_NO_RIGHT_TO_UNBIND(3015, R.string.tip_no_authorization_to_unbind),
    //    RESPONSE_COMMAND_ERROR(4000, 0),  // 指令错误
//    RESPONSE_OUTPUT_NUMBER_NOT_CONFIG(4001, 0),  // The send output number of the instruction is not config
//    RESPONSE_DEPT_ID_IS_ERROR(5000, 0),  // Incorrect Department ID
//    RESPONSE_DEPT_NAME_IS_EXISTS(5001, 0),  // Department name is exists
//    RESPONSE_DEVICE_CONTROL_ITEM_NOT_CONFIG(5002, 0),  // control item not config
//    RESPONSE_POLYGONAL_FENCE_LESS_THREE_POINTS(5003, 0),  // The polygon cannot have less than 3 points
//    RESPONSE_CUSTOMER_NOT_EXISTS(5004, 0),  // customer does
    RESPONSE_NO_RIGHT_TO_OPERATE(5006, R.string.tip_no_authorization_to_operate),
    RESPONSE_AUTH_KEY_FORBID(21000, R.string.tip_verification_code_error),  // Authorization code is incorrect
    RESPONSE_AUTH_KEY_EXPIRED(21001, R.string.tip_verification_code_expired_or_invalid),  // Authorization code has expired or invalid
    ;

    private final int errorCode;
    private final int resId;

    ErrorCode(int errorCode, int resId) {
        this.errorCode = errorCode;
        this.resId = resId;
    }

    public static ErrorCode getInstance(int code) {
        for (ErrorCode item : ErrorCode.values()) {
            if (item.errorCode == code) {
                return item;
            }
        }
        return ErrorCode.RESPONSE_REQUEST_ERROR;
    }

    public static int getResId(int code) {
        for (ErrorCode item : ErrorCode.values()) {
            if (item.errorCode == code) {
                return item.resId;
            }
        }
        return ErrorCode.RESPONSE_REQUEST_ERROR.resId;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public int getResourceId() {
        return this.resId;
    }
}

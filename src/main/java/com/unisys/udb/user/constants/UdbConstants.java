package com.unisys.udb.user.constants;


import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class UdbConstants {
    public static final String PAYEE_DELETE_MESSAGE_200 = "Payee deleted successfully";
    public static final String USER_SERVICE = "User Service";
    public static final String USER_API_DESCRIPTION = "User API Description";
    public static final String STATUS_CODE = "statusCode";
    public static final String MESSAGE = "message";
    public static final String TIMESTAMP = "timestamp";
    public static final String INCORRECT_FORMAT = "Argument Incorrect format";
    public static final String INCORRECT_EMAIL_FORMAT = "Incorrect email format";
    public static final String INCORRECT_UUID_FORMAT = "Invalid digital customer profile id";
    public static final String INVALID_REQUEST_PAYLOAD = "Invalid request payload";
    public static final String DIGITAL_PROFILE_ID_NOT_NULL = "digital customer profile id cannot be null";

    public static final int MAX_DEVICES = 6;

    public static final String FAILURE = "failure";
    public static final String SUCCESS = "success";
    public static final String FIRST_ATTEMPT = "errorLoginPwdFailed";
    public static final String USER_LOCKED = "User Account Is Locked";
    public static final String INVALID_USER = "Invalid Credentials";
    public static final String SUCCESS_CODE = "200";
    public static final int INTERNAL_ERROR_CODE = 500;
    public static final String TOPIC = "notification-topic";
    public static final String SENDER_INFO = "sender info";
    public static final String ALERT = "UserLockedAlert";
    public static final int MAX_LENGTH_FIFTY = 50;
    public static final int MAX_LENGTH_THIRTY = 30;
    public static final int MAX_LENGTH_250 = 250;
    public static final int MAX_LENGTH_1000 = 1000;
    public static final int MAX_LENGTH_100 = 100;
    public static final String STATUS_MESSAGE = "Successful";
    public static final String MODIFIED_BY = "John";
    public static final String BOOLEAN_TRUE = "true";
    public static final String EMPTY_REQUEST_BODY = "{}";

    public static final int DIGITAL_CUSTOMER_STATUS_TYPE_ID = 1;
    public static final int ZERO_CONSTANT = 0;
    public static final int ONE_CONSTANT = 1;
    public static final int TWO_CONSTANT = 2;
    public static final int THREE_CONSTANT = 3;
    public static final int FOUR_CONSTANT = 4;
    public static final int FIVE_CONSTANT = 5;
    public static final String PAYEE_CREATE_MESSAGE_200 = "Payee created successfully";
    public static final Integer PAYEE_SUCCESS_STATUS_200 = 200;
    public static final int TWO_THOUSAND_TWENTY_TWO_CONSTANT = 2022;

    public static final String ALERT_INVALID_LOGIN_ATTEMPT = "alertInvalidLoginAttempt";

    public static final int THIRTY_ONE_CONSTANT = 31;
    public static final int FIFTY = 50;
    public static final int NINTEEN_SEVENTY = 1970;

    public static final String PIN_MATCHED = "Pin Matched";

    public static final String PIN_NOT_MATCHED = "Pin Not Matched";

    public static final String
            PROFILE_ID_COMPARE_CONSTANT = "WHERE digital_customer_profile_id = :digitalCustomerProfileId ";

    public static final String NOTIFICATION_MESSAGE = "At least one communication channel should be enabled";
    public static final String UPDATE_BY = "Nirbikar";

    public static final String NOT_FOUND = "Not Found";
    public static final String BAD_REQUEST = "Bad_Request";
    public static final String EXCEPTION_MESSAGE = "Exception Caught: {}";
    public static final String UPDATE_FIELD_DEVICEID_NULL = "UpdateField and Device ID are required";
    public static final String UPDATE_FIELD_NULL = "UpdateField is required";
    public static final String INCORRECT_CREDENTIALS = "errorPleaseEnsureCredentials";
    public static final String THREE_INCORRECT_CREDENTIALS = "errorIncorrectCredential3Times";
    public static final String ANOTHER_LOCK_ATTEMPT = "errorOneMoreAttemptLock";
    public static final String ENSURE_CREDENTIALS = "errorEnsureCredentials";
    public static final String ACCOUNT_MULTIPLE_ATTEMPT_LOCKED = "errorAccountLockedDueToMultilpleAttempts";
    public static final String DEVICE_FIELD_NULL = "Device ID is required";
    public static final String UPDATE_TERMS_CONDITIONS = "terms";
    public static final String UPDATE_COOKIES = "cookies";
    public static final String DEVICE_TABLE_UPDATE_SUCCESS = "Success";
    public static final String EXCEPTION = " Exception StackTrace : {}";
    public static final String MISSING_REQUEST_PARAMETER = "Request Parameter Missing";
    public static final String PUBLIC_KEY_EXIST = "Device Public key is already exist";
    public static final String DELETE = "delete";
    public static final String UNSUSPEND = "unsuspend";
    public static final String SUSPEND = "suspend";
    public static final String UNLOCK = "unlock";
    public static final String DEACTIVATED_ACTION = "deactivated";
    public static final String UNSUSPENDED_ACTION = "unsuspended";
    public static final String SUSPENDED_ACTION = "suspended";
    public static final String UNLOCKED_ACTION = "unlocked";
    public static final String LANGUAGE_CODE = "locale_language_code";
    public static final String INVALID_REQUEST_BODY = "Invalid request body. It should contain byChannel, byActivity "
            + "and byDate";
    public static final Integer ACTIVE = 1;
    public static final String ACTIVE_STATUS = "Active";
    public static final String UNLOCK_PENDING_STATUS = "UnlockPending";
    public static final String SUSPENDED_STATUS = "Suspended";
    public static final String DEACTIVATED_STATUS = "Deactivated";
    public static final Integer LOCKED = 2;
    public static final Integer SUSPENDED = 3;
    public static final Integer DEACTIVATED = 4;
    public static final Integer INVALID_STATUS = 5;
    public static final String SENDER_APP = "udb-user-service";
    public static final String LANGUAGE_PREFERENCE = "en_US";
    public static final String
            USER_ACCOUNT_STATUS_MESSAGE = "User account %s successfully for digital customer profile id %s";
    public static final String
            USER_ACCOUNT_STATUS_UNLOCK_MESSAGE = "User account moved to UnlockPending state for digital customer "
            + "profile id %s";
    public static final String
            UNLOCK_ERROR = "Unable to unlock as account is not in locked state for digital customer profile : ";
    public static final String
            PUBLIC_KEY_NOT_FOUND_FOR_THE_GIVEN_DEVICE_UUID = "Public key not found for the given device UUID";
    public static final String
            ERROR_RETRIEVING_PUBLIC_KEY_FOR_DEVICE_UDID = "Error retrieving public key for device UDID: ";
    public static final int SIX_CONSTANT = 6;
    public static final String TIME_PATTERN_24H = "HH:mm:ss";
    public static final String TIME_PATTERN_12H = "hh:mm:ss a";
    public static final String INPUT_DATE_PATTERN = "dd-MM-yyyy";
    public static final String OUTPUT_DATE_PATTERN = "dd/MM/yyyy";
    public static final Long DATE = 12345678L;
    public static final String DEACTIVATE_ERROR = "Unable to deactivate as account is not in "
            + "active/suspended state for digital customer profile : ";
    public static final String SUSPEND_ERROR = "Unable to suspend as account is not in "
            + "active state for digital customer profile : ";
    public static final String UNSUSPEND_ERROR = "Unable to unsuspend as account is not in "
            + "suspended state for digital customer profile : ";
    public static final String DIGITAL_CUSTOMER_DEVICE_ID = "dcd.digital_customer_device_id";
    // Device Id hardcode value for de-register the devices
    public static final String DEVICE_ID1 = "96471bc8-cbde-4e0f-be92-6814ed9bd7c3";
    public static final String NOT_FOUND_ERROR_CODE = "404";
    public static final String CONFLICT_ERROR_CODE = "409";
    public static final String CORE_CUSTOMER_PROFILE_ALREADY_EXISTS = "Core customer profile already exists";
    public static final String CORE_CUSTOMER_PROFILE_ID_MISSING = "Core customer profile is missing in the body";
    public static final String DUPLICATE_KEY_VIOLATION = "Duplicate Key Violation";
    public static final String NOT_ACCEPTABLE = "406";
    public static final Integer OK_RESPONSE_CODE = 200;
    public static final String STATUS_TYPE_NOT_FOUND_ERROR_MESSAGE = "Digital Customer Status Type Not found ";
    public static final String NOT_FOUND_ERROR_MESSAGE = "Digital Customer Profile Id Not found ";
    public static final String DIGITAL_PROFILE_NOT_FOUND = "Digital customer profile id not found for "
            + "core customer profile id ";
    public static final String PROMOTION_OFFER_NOT_FOUND = "Promotion Offers Not Found";
    public static final String SHORTCUTS_NOT_ACCEPTABLE_MESSAGE = "At Least Three Shortcuts Should Enabled";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DIGITAL_ACCESS_LOCKED = "triggerDigitalAccessLockedNotification";
    public static final String DIGITAL_ACCESS_UNLOCKED = "triggerDigitalAccessUnlockedNotification";
    public static final String NOTIFICATION_EVENT_SOURCE = "notificationEventSource";
    public static final String NOTIFICATION_EVENT_TIMESTAMP = "notificationEventTimeStamp";
    public static final String NOTIFICATION_ACTIVITY = "notificationActivity";
    public static final String NOTIFICATION_LANGUAGE_PREFERENCE = "notificationLanguagePreference";
    public static final String NOTIFICATION_TEMPLATE_NAME = "notificationTemplateName";
    public static final String DIGITAL_CUSTOMER_PROFILE_ID = "digitalCustomerProfileId";
    public static final String DIGITAL_USER_NAME = "digitalUserName";
    public static final String NOTIFICATION_DIGITAL_CUSTOMER_DEVICE_ID = "digitalCustomerDeviceId";
    public static final String USER_AND_DEVICE_INFO_SAVED = "User and Device ID is Saved";
    public static final String ERROR_SAVING_PIN_DETAILS = "Failed to save PIN details in the database";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String SERVICE_UNAVAILABLE = "503";
    public static final String UNSUSPEND_USER = "unSuspendDigitalBankingAccess";
    public static final String SUSPEND_USER = "SuspendDigitalBankingAccess";
    public static final String UNLOCK_USER = "unLockDigitalBankingAccess";
    public static final String DEACTIVATE_USER = "deactivateDigitalBankingAccess";
    public static final String STEP = "Step";
    public static final String FACE_ID = "faceId";
    public static final String TOUCH_ID = "touchId";
    public static final String DIGITAL_CUSTOMER_PROFILEID = "digital customer profile id";
    public static final String INVALID_ACTION_EXCEPTION = "Invalid action. "
            + "Action can only be unlock/suspend/unsuspend/delete";
    public static final String ID = "id";
    public static final int THIRTY_THOUSAND_CONSTANT = 30000;
    public static final int SIXTY_THOUSAND_CONSTANT = 60000;
    public static final int TEN_THOUSAND_CONSTANT = 10000;
    public static final String EMPTY_STRING = "";
    public static final int EIGHT_CONSTANT = 8;
    public static final int NINE_CONSTANT = 9;
    public static final int SEVEN_CONSTANT = 7;
    public static final int TWO_THOUSAND_TWENTY_FOUR_CONSTANT = 2024;
    public static final int THIRTEEN = 13;
    public static final int ELEVAN = 11;
    public static final int THIRTY_FOUR = 34;
    public static final int THREE = 3;
    public static final int NINETY_SEVEN_HUNDREDS = 970000000;
    public static final String PAYEE_DELETE_FAILED_MESSAGE_404 = "Delete failed: Payee not found";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String CHANGES_UPDATED_SUCCESS = "Changes Updated Successfully";
    public static final String NON_NUMERIC_DATA = "Payee Nick Name should be non numeric-Data Not Updated";
    public static final String NO_PAY_ID = "Update failed: Payee not found";
    public static final String NO_PAY_ID_GET = "Get failed: Payee not found";
    public static final int PREFIX = 3;
    public static final String NAME = "name";
    public static final String CREATION_DATE = "creationDate";
    public static final String PIN_NOT_SETUP_MESSAGE = "Pin is Not Setup";
    public static final String DEVICE_REGISTRATION_SUCCESS = "Device Registration Successful";
    public static final String PUBLIC_KEY_MISSING_IN_PAYLOAD = "POST API: "
            + "devicePublicKeyForPin is empty or missing in the payload.";
    public static final String IP_ADDRESS = "constantIPAddress";
    public static final String FAILURE_RESPONSE_TYPE = "Failure";
    public static final String RETRY_MSG =
            "We have attempted to reconnect but were unsuccessful."
                    + "Please try again later.";
    public static final String CONFIGURATION_SERVICE_RETRY_MSG =
            "Configuration Service is currently unavailable. " + RETRY_MSG;
    public static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error";
    public static final String CUSTOMER_INACTIVE_PERIOD_MESSAGE = "Customer has exceeded the inactivity period";
    public static final String CUSTOMER_UNLOCK_PENDING_MESSAGE = "Customer has status Unlock Pending";
    public static final String CUSTOMER_ACTIVE_PERIOD_MESSAGE = "Customer is active";
    public static final String AUDIT_LOG = "Audit details : {}";
    public static final String UDB_PWD_EXPRY_PERIOD = "UDB_PWD_EXPRY_PERIOD";
    public static final String UDB_PIN_EXPRY_PERIOD = "UDB_PIN_EXPRY_PERIOD";
    public static final String UDB_REAUTHENTICATION_DURATION = "UDB_REAUTHENTICATION_DURATION";
    public static final String REAUTHENTICATION_ACTIVITY_NAME = "Reauthorization";
    public static final String REAUTHENTICATION_ACTIVITY_STATUS_SUCCESS = "success";

    public static final String PASSWORD_EXPIRY_DATE_UPDATED_SUCCESSFULLY = "Password expiry updated successfully";
    public static final String PASSWORD = "password";
    public static final String PIN = "pin";
    public static final String PIN_EXPIRY_DATE_UPDATED_SUCCESSFULLY = "PIN expiry updated successfully";
    public static final String INVALID_UPDATE_TYPE_MESSAGE = "Invalid update type. Must be either 'password' or 'pin'.";
    public static final String ERROR_UPDATING_EXPIRY_MESSAGE = "Something went wrong while updating the expiry";
    public static final String REAUTHORIZATION = "Reauthorization";
    public static final String STATUS_UNLOCK_PENDING = "UnlockPending";
    public static final String REAUTHENTICATION_RECORDED = "Re-Authentication activity recorded successfully";
    private static final LocalDateTime CREATE_OR_UPDATE = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
    public static final String BROADCAST_WITHDRAW_STATUS_ID = "withdrawn";
    public static final String USER_LOCKED_SUCCESS_MESSAGE = "User locked successfully";
    public static final String DATABASE_ERROR_MESSAGE = "Database error occurred while locking user account";
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred while locking user account";
    public static final String STATUS_MESSAGE_SUCCESSFUL = "Successful";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final int UNLOCK_PENDING = 5;
    public static final String BROADCAST_DELETE_STATUS_ID = "delete";
    public static final String UDB_MARKETING_PRFNC_HDR_EMAIL = "hdrEmail";
    public static final String UDB_BANKING_PRFNC_HDR_EMAIL = "hdrEmail";
    public static final String UDB_MARKETING_PRFNC_HDR_SMS = "hdrSMS";
    public static final String UDB_BANKING_PRFNC_HDR_SMS = "hdrSMS";
    public static final String UDB_BANKING_PRFNC_HDR_MOBILE_PUSH = "hdrMobilePush";
    public static final String UDB_MARKETING_PRFNC_HDR_POST = "hdrPost";
    public static final String UDB_MARKETING_PRFNC_HDR_TELEPHONE = "hdrTelephone";
    public static final String UDB_MARKETING_PRFNC_HDR_ONLINE = "hdrOnline";

    public static final String HASH_KEY = "customer-access-data";

    public static final String REDIS_CONNECTION_FAILURE = "Failed to create Redis connection factory";

    public static final String REDIS_TEMPLATE_FAILURE = "Failed to create Redis template";

    public  static  final String UDB_MFA_CONFIG = "mfaConfig";

    public static final String MFA_TYPE = "mfaType";
    public static final String MOBILE = "Mobile";
    public static final String OTP = "OTP";
    public static final String MFA_NONE = "None";

    public static final String MFA_RECEIVED = "MFA Received";

    public static final String ADMIN = "Admin";
    private UdbConstants() {
    }

    public static LocalDateTime getCreateOrUpdateDate() {
        return CREATE_OR_UPDATE;
    }

    @Getter
    public enum ShortcutEnum {
        FUND_TRANSFER_SHORTCUT("fundTransferShortcut"), E_STATEMENT_SHORTCUT("estatementShortcut"),
        PAYEE_SHORTCUT("payeeShortcut"), SCHEDULED_PAYMENTS_SHORTCUT("scheduledPaymentsShortcut"),
        COMM_PREF_SHORTCUT("commPrefShortcut"), SESSION_HISTORY_SHORTCUT("sessionHistoryShortcut");

        private final String shortcutValue;

        ShortcutEnum(String shortcutValue) {
            this.shortcutValue = shortcutValue;
        }
    }

    @Getter
    public enum TermsEnum implements CommonEnumFields {
        TERMS_AND_CONDITIONS("termsConditions");

        private final String enumValue;

        TermsEnum(String enumValue) {
            this.enumValue = enumValue;
        }
    }

    @Getter
    public enum CookiesEnum implements CommonEnumFields {
        STRICTLY_ACCEPTABLE_COOKIE("strictlyAcceptanceCookie"), PERFORMANCE_COOKIE("performanceCookie"),
        FUNCTIONAL_COOKIE("functionalCookie");

        private final String enumValue;

        CookiesEnum(String enumValue) {
            this.enumValue = enumValue;
        }
    }

    public interface CommonEnumFields {
        String getEnumValue();
    }
}

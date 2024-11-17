package com.unisys.udb.user.constants;


import static com.unisys.udb.user.constants.UdbConstants.DIGITAL_CUSTOMER_DEVICE_ID;
import static com.unisys.udb.user.constants.UdbConstants.PROFILE_ID_COMPARE_CONSTANT;

public final class QueryConstants {

    private QueryConstants() {
    }

    private static final String SELECT = "SELECT ";
    private static final String FROM = "FROM ";
    public static final String GET_CUSTOMER_SESSION_HISTORY_DETAILS =
            "EXEC digital.usp_GetCustSessionHist @iCustomerId = ?1,  @ioffset_row_count = ?2, "
                    + "@ifetch_row_count = ?3," + "@iactivityName = ?4, @iactivityChannel = ?5, "
                    + "@ifromDate = ?6, @itoDate = ?7";
    public static final String GET_USER_LOGIN_ATTEMPT_DETAILS = "EXEC digital.usp_GetFailedLoginAttempt "
            + "@iCustUserName = ?1, "
            + "@iStatusName = ?2, "
            + "@iDeviceUid = ?3,"
            + "@iMaxCount = ?4";
    public static final String UPDATE_DIGITAL_CUSTOMER_STATUS_TYPE_REF_ID = "UPDATE digital.digital_customer_profile "
            + "SET digital_customer_status_type_ref_id = :statusCode , digital_account_status_reason = :reason, "
            + "profile_modified_by = :modifiedBy, profile_modification_date = :modifiedTime "
            + "WHERE digital_customer_profile_id = :digitalCustomerProfileId";
    public static final String INSERT_INTO_DEVICE_LINK_AUDIT = "INSERT INTO digital.digital_device_link_audit "
            + "(digital_customer_profile_id, "
            + "digital_customer_device_id, "
            + "device_link_registered_flag_audit, digital_audit_type_ref_id, digital_device_link_id, "
            + "device_link_audit_creation_date, device_link_audit_created_by) "
            + "VALUES "
            + "(:#{#entity.digitalCustomerProfile.digitalCustomerProfileId}, "
            + ":#{#entity.digitalCustomerDevice.digitalCustomerDeviceId}, "
            + ":#{#entity.deviceLinkRegisteredFlagAudit}, :#{#entity.deviceAuditTypeRefId}, "
            + ":#{#entity.digitalDeviceLink.digitalDeviceLinkId}, :#{#entity.deviceLinkAuditCreationDate}, "
            + ":#{#entity.deviceLinkAuditCreatedBy})";
    public static final String UPDATE_DIGITAL_DEVICE_LINK =
            "UPDATE digital.digital_device_link SET device_link_registered_flag = 0, "
                    + "device_link_modification_date = CURRENT_TIMESTAMP "
                    + "WHERE digital_customer_device_id IN :deviceIdsList";
    public static final String LOCK_USER_ACCOUNT =
            "UPDATE digital.digital_customer_profile "
                    + "SET digital_customer_status_type_ref_id = 2,"
                    + "profile_modification_date = CURRENT_TIMESTAMP,"
                    + "profile_modified_by = USER "
                    + "WHERE digital_customer_profile_id = :digitalCustomerProfileId ";

    public static final String EXISTS_BY_PROFILE_ID_AND_STATUS_TYPE =
            SELECT + "CASE WHEN EXISTS ( "
                    + "SELECT 1 "
                    + "FROM digital.digital_customer_profile ddcp "
                    + "JOIN digital.digital_customer_status_type_ref ddcstr "
                    + "ON ddcp.digital_customer_status_type_ref_id = ddcstr.digital_customer_status_type_ref_id "
                    + "WHERE ddcp.digital_customer_profile_id = CAST(?1 AS uniqueidentifier) "
                    + "AND ddcstr.customer_status_type = ?2) "
                    + "THEN 'true' ELSE 'false' END";

    public static final String GET_PAYEE_TRANSACTIONS = "SELECT"
            + " t.transaction_date_time,t.transaction_amount,payee.digital_customer_payee_id,"
            + "payee.payee_name,payee.payee_bank_name,payee.payee_bank_code,"
            + "payee.payee_account_number,payee.payee_nickname,payee.payee_reference,"
            + "sa.account_number as transaction_acct_number from core.core_customer_savings_transaction t"
            + " join core.core_customer_savings_account_link l"
            + " on t.core_customer_savings_account_id = l.core_customer_savings_account_id"
            + " join digital.digital_customer_profile p"
            + " on l.core_customer_profile_id = p.core_customer_profile_id"
            + " join digital.digital_customer_payee payee"
            + " on p.digital_customer_profile_id = payee.digital_customer_profile_id"
            + " join core.core_customer_savings_account sa"
            + " on t.core_customer_savings_account_id=sa.core_customer_savings_account_id"
            + " where payee.digital_customer_payee_id = :digitalPayeeId";
    public static final String UPDATE_BIOMETRIC_PUBLICKEY_NULL = "UPDATE digital.digital_customer_device "
            + "SET device_face_auth_public_key = NULL, "
            + "device_touch_auth_public_key = NULL, "
            + "device_pin_auth_public_key = NULL "
            + "WHERE digital_customer_device_id IN :digitalDeviceIdsList "
            + "SELECT COUNT(*) FROM digital.digital_customer_device "
            + "WHERE digital_customer_device_id IN :digitalDeviceIdsList";
    public static final String GET_BIOMETRIC_STATUS_FOR_DEVICE =
            "SELECT"
                    + " IIF(device_face_auth_public_key IS NOT NULL, 'true', 'false') AS faceId,"
                    + " IIF(device_touch_auth_public_key IS NOT NULL, 'true', 'false') AS touchId"
                    + " FROM digital.digital_customer_device dcd "
                    + " INNER JOIN digital.digital_device_link ddl "
                    + " ON ddl.digital_customer_device_id = dcd.digital_customer_device_id"
                    + " WHERE ddl.digital_customer_profile_id = :digitalCustomerProfileId"
                    + " AND dcd.digital_device_udid = :digitalDeviceUDID";
    public static final String UPDATE_DIGITAL_DEVICE_LINK_FOR_REGISTER_DEVICE =
            "UPDATE digital.digital_device_link "
                    + "SET device_link_registered_flag = 1 "
                    + "WHERE digital_customer_profile_id = :digitalCustomerProfileID "
                    + "AND digital_customer_device_id = ("
                    + "    SELECT digital_customer_device_id "
                    + "    FROM digital.digital_customer_device "
                    + "    WHERE digital_device_udid = :digitalDeviceUUID"
                    + ")";
    public static final String GET_BROADCAST_REFERENCE_ID =
            "select * from digital.fn_GetBroadcastMsg(:digitalCustomerProfile)";
    public static final String UPDATE_ALERT_READ_FLAG_BY_IDS =
            "UPDATE digital.digital_customer_alert "
                    + "SET alert_read_flag = 'true' "
                    + "WHERE digital_customer_profile_id = :customerProfileId "
                    + "  AND alert_read_flag = :alertReadFlag "
                    + "  AND EXISTS ("
                    + "    SELECT 1"
                    + "    FROM digital.digital_docdb_alert_ref ddar"
                    + "    WHERE digital.digital_customer_alert.digital_docdb_alert_ref_id = ddar"
                    + ".digital_docdb_alert_ref_id"
                    + "      AND ddar.digital_alert_key = :digitalAlertKey"
                    + "  );";
    public static final String FETCH_COMPLETED_BROADCAST_MESSAGES = "SELECT ddb.digital_docdb_broadcast_msg_ref_id, "
            + "ddb.broadcast_message_name, ddb.broadcast_message_start_date, ddb.broadcast_message_end_date, "
            + "ddb.broadcast_to_all from "
            + SchemaConstants.DIGITAL
            + ".[digital_docdb_broadcast_msg_ref] ddb LEFT JOIN "
            + SchemaConstants.DIGITAL
            + ".[digital_template_status_ref] dts ON "
            + "ddb.digital_template_status_ref_id = dts.digital_template_status_ref_id "
            + "where dts.template_status_name = 'schedule' AND ddb.broadcast_message_end_date < :currentTime";
    public static final String GET_COUNT_OF_LIVE_BROADCAST_MESSAGES =
            "SELECT COUNT(ddb.digital_docdb_broadcast_msg_ref_id) AS messageCount "
                    + FROM
                    + SchemaConstants.DIGITAL
                    + ".[digital_docdb_broadcast_msg_ref] ddb "
                    + "LEFT JOIN "
                    + SchemaConstants.DIGITAL
                    + ".[digital_template_status_ref] dts "
                    + "ON ddb.digital_template_status_ref_id = dts.digital_template_status_ref_id "
                    + "WHERE dts.template_status_name = 'schedule' "
                    + "AND ddb.broadcast_message_start_date <= :currentTime "
                    + "AND ddb.broadcast_message_end_date >= :currentTime "
                    + "AND ddb.digital_docdb_broadcast_msg_ref_id IN :messageIdsList";
    public static final String UPDATE_LIVE_MESSAGES_STATUS_TO_WITHDRAW =
            "UPDATE "
                    + SchemaConstants.DIGITAL
                    + ".[digital_docdb_broadcast_msg_ref] "
                    + "SET digital_template_status_ref_id = (SELECT digital_template_status_ref_id FROM "
                    + SchemaConstants.DIGITAL
                    + ".[digital_template_status_ref] "
                    + "WHERE template_status_name = :templateStatusName), "
                    + "broadcast_message_modification_date = :modificationDate, "
                    + "broadcast_message_modified_by = :modifiedBy "
                    + "WHERE digital_docdb_broadcast_msg_ref_id IN :messageIdsList";

    public static final String RETRIEVE_COOKIE_PREFERENCE_BY_DEVICE_UDID =
            "SELECT dcd.strictly_acceptance_cookie, dcd.performance_cookie, dcd.functional_cookie "
                    + FROM + SchemaConstants.DIGITAL + " .digital_customer_device dcd "
                    + "WHERE dcd.digital_device_udid = :digitalDeviceUdid";
    public static final String GET_CUSTOMER_TRANSACTIONS =
            SELECT + " ccp.customer_first_name as firstName, ccp.customer_middle_name as middleName,  "
                    + " ccp.customer_last_name as lastName, ccp.customer_email as email, "
                    + " ccp.customer_mobile_no as phone "
                    + " FROM core.core_customer_profile ccp "
                    + " JOIN digital.digital_customer_profile dcp "
                    + " ON ccp.core_customer_profile_id = dcp.core_customer_profile_id "
                    + " WHERE dcp.digital_customer_profile_id = :digitalCustomerProfileId";
    public static final String GET_USER_NAME_INFO =
            SELECT
                    + " ddl.digital_customer_device_id, "
                    + " digital_user_name, "
                    + " CAST ( "
                    + "    CASE "
                    + "        WHEN dcpin.digital_customer_pin_id IS NULL THEN 'false' "
                    + "        ELSE 'true' "
                    + "    END AS VARCHAR(5) "
                    + " ) AS doesPinExists "
                    + " FROM "
                    + " digital.digital_device_link ddl "
                    + " JOIN digital.digital_customer_profile dcp ON ddl.digital_customer_profile_id = "
                    + " dcp.digital_customer_profile_id "
                    + " LEFT JOIN digital.digital_customer_pin dcpin ON dcp.digital_customer_profile_id = "
                    + " dcpin.digital_customer_profile_id "
                    + " WHERE "
                    + " ddl.digital_customer_device_id = :digitalCustomerDeviceId";
    public static final String GET_CUSTOMER_PIN_HISTORY =
            SELECT + "top 3 * "
                    + "FROM digital.digital_customer_pin t "
                    + "WHERE t.digital_customer_profile_id=CAST(?1 AS uniqueidentifier) "
                    + "order by t.digital_customer_pin_id desc";
    public static final String GET_CUSTOMER_DEVICE_DETAILS =
            SELECT + "dcd.digital_customer_device_id as deviceId,dcd.device_type as deviceType, "
                    + " dcd.device_token as deviceToken "
                    + FROM + " digital.digital_customer_device dcd "
                    + " INNER JOIN digital.digital_device_link ddl ON ddl.digital_customer_device_id = "
                    + DIGITAL_CUSTOMER_DEVICE_ID
                    + " WHERE ddl.digital_customer_profile_id = :profileId AND dcd.device_enabled_flag=1 ";
    public static final String GET_CUSTOMER_DEVICE_DETAIL =
            SELECT + "dcd.* "
                    + FROM + " digital.digital_customer_device dcd "
                    + " INNER JOIN digital.digital_device_link ddl ON ddl.digital_customer_device_id = "
                    + DIGITAL_CUSTOMER_DEVICE_ID
                    + " WHERE ddl.digital_customer_profile_id = :profileId AND dcd.device_enabled_flag=1 ";
    public static final String CHECK_PIN_EXIST_BASED_ON_DIGITAL_DEVICE_UDID =
            SELECT + "dcp.pin_set_completed "
                    + " FROM digital.digital_customer_profile dcp "
                    + " JOIN digital.digital_device_link ddl on dcp.digital_customer_profile_id = "
                    + " ddl.digital_customer_profile_id "
                    + " JOIN digital.digital_customer_device dcd on ddl.digital_customer_device_id = "
                    + DIGITAL_CUSTOMER_DEVICE_ID
                    + " WHERE dcd.digital_device_udid = :digitalDeviceUdid";
    public static final String GET_CUSTOMER_STATUS_TYPE =
            SELECT + "TOP 1"
                    + "dcsr.customer_status_type "
                    + FROM
                    + " digital.digital_customer_status_type_ref AS dcsr "
                    + "JOIN digital.digital_customer_profile AS dcp ON dcsr.digital_customer_status_type_ref_id = "
                    + "dcp.digital_customer_status_type_ref_id "
                    + "WHERE "
                    + "dcp.digital_user_name = :digitalUserName";
    public static final String COUNT_UNREAD_ALERTS_BY_PROFILE_ID =
            SELECT
                    + "COUNT(*) "
                    + "FROM digital.digital_customer_alert "
                    + PROFILE_ID_COMPARE_CONSTANT
                    + "AND alert_read_flag = 0";
    public static final String GET_CUSTOMER_DETAILS_FOR_SUPPORT =
            SELECT + "CONCAT_WS(' ', ccp.customer_first_name, ccp.customer_middle_name, ccp.customer_last_name) "
                    + "AS name, "
                    + "dcp.digital_user_name AS username, dcp.digital_customer_profile_id, dcd.device_name, "
                    + "dcp.profile_registration_date As registrationDate, csr.customer_status_type As status, "
                    + "ccp.customer_mobile_no As phone "
                    + "FROM core.core_customer_profile ccp "
                    + "INNER JOIN digital.digital_customer_profile dcp ON dcp.core_customer_profile_id = "
                    + "ccp.core_customer_profile_id "
                    + "INNER JOIN digital.digital_device_link ddl ON ddl.digital_customer_profile_id = "
                    + "dcp.digital_customer_profile_id "
                    + "INNER JOIN digital.digital_customer_device dcd ON dcd.digital_customer_device_id ="
                    + " ddl.digital_customer_device_id "
                    + "INNER JOIN digital.digital_customer_status_type_ref csr ON "
                    + "csr.digital_customer_status_type_ref_id = dcp.digital_customer_status_type_ref_id "
                    + "WHERE (ccp.customer_mobile_no = :searchTerm "
                    + "OR CONCAT_WS(' ', ccp.customer_first_name, ccp.customer_middle_name, "
                    + "ccp.customer_last_name) LIKE CONCAT('%', :searchTerm, '%') "
                    + "OR dcp.digital_user_name= :searchTerm "
                    + "OR dcp.digital_customer_profile_id LIKE CONCAT('%', :searchTerm, '%'))";
    public static final String GET_MARKETING_REF_ID =
            SELECT
                    + "ref.digital_marketing_msg_ref_id "
                    + "from digital.digital_marketing_msg_ref ref "
                    + "join digital.digital_customer_marketing_msg cust on "
                    + "cust.digital_marketing_msg_ref_id=ref.digital_marketing_msg_ref_id "
                    + "and cust.digital_customer_profile_id=:digitalCustomerProfileId "
                    + "and CURRENT_TIMESTAMP between ref.marketing_message_start_date "
                    + "and ref.marketing_message_end_date and cust.customer_marketing_msg_status='Active' "
                    + "and cust.customer_marketing_msg_display_flag=1 "
                    + "and ref.digital_template_status_ref_id=1";
    public static final String CHECK_MFA_STATUS_BASED_ON_DIGITAL_DEVICE_UDID =
            SELECT + "dcp.mfa_activity_completed "
                    + " FROM "
                    + " digital.digital_customer_profile dcp "
                    + " JOIN digital.digital_device_link ddl on dcp.digital_customer_profile_id = "
                    + "ddl.digital_customer_profile_id "
                    + " JOIN digital.digital_customer_device dcd on ddl.digital_customer_device_id = "
                    + "dcd.digital_customer_device_id "
                    + " WHERE "
                    + " dcd.digital_device_udid = :digitalDeviceUdid";
    public static final String GET_USERNAME_BY_DIGITAL_CUSTOMER_PROFILE_ID =
            SELECT + "digital_user_name from digital.digital_customer_profile "
                    + " where digital_customer_profile_id = :digitalCustomerProfileId ";
    public static final String FETCH_OLD_PASSWORDS_QUERY =
            SELECT + " old_pwd "
                    + FROM + " " + SchemaConstants.DIGITAL + ".digital_customer_pwd "
                    + PROFILE_ID_COMPARE_CONSTANT
                    + " AND pwd_creation_date BETWEEN :fromDate AND :toDate";

    public static final String FIND_PROFILE_ID_BY_USERNAME =
            SELECT + " d.digital_customer_profile_Id FROM " + SchemaConstants.DIGITAL
                    + ".digital_customer_profile d WHERE d.digital_user_name = :digitalUserName";

    public static final String FETCH_OLD_PINS_QUERY =
            SELECT + " old_pin "
                    + FROM + " " + SchemaConstants.DIGITAL + ".digital_customer_pin "
                    + PROFILE_ID_COMPARE_CONSTANT
                    + " AND pin_creation_date BETWEEN :fromDate AND :toDate";

    public static final String FIND_PIN_SET_COMPLETED_BY_PROFILE_ID =
            SELECT + " d.pin_set_completed FROM " + SchemaConstants.DIGITAL
                    + ".digital_customer_profile d WHERE d.digital_customer_profile_id = :digitalCustomerProfileId";
    public static final String FETCH_FROM_DIGITAL_CUSTOMER_DEVICE =
            SELECT + " dcd.* FROM digital.digital_customer_device As dcd "
                    + "WHERE dcd.digital_device_udid IN :digitalDeviceIdsList";
    public static final String FETCH_DIGITAL_CUSTOMER_DEVICE_ID =
            SELECT + " ddcd.digital_customer_device_id "
                    + " FROM digital.digital_customer_device As ddcd, digital.digital_device_link As ddl "
                    + " WHERE ddl.digital_customer_device_id=ddcd.digital_customer_device_id "
                    + " AND ddcd.digital_device_udid IN :digitalDeviceIdsList"
                    + " AND ddl.device_link_registered_flag=1";
    public static final String FETCH_FROM_DIGITAL_DEVICE_LINK =
            SELECT + " ddl.* FROM digital.digital_device_link As ddl "
                    + "WHERE ddl.digital_customer_device_id IN :digitalCustomerDeviceIds";
    public static final String FETCH_DIGITAL_CUSTOMER_DEVICE_IDS = SELECT
            + " ddl.digital_customer_device_id from  [digital].[digital_device_link] As ddl "
            + "where ddl.digital_customer_profile_id= :digitalCustomerProfileId "
            + "and ddl.device_link_registered_flag = 1";
    public static final String GET_CUSTOMER_DEVICE_ID =
            SELECT
                    + " dcd.DIGITAL_CUSTOMER_DEVICE_ID "
                    + "FROM [digital].[digital_customer_device] AS dcd "
                    + "JOIN [digital].[digital_device_link] AS ddl "
                    + "ON dcd.[digital_customer_device_id] = ddl.[digital_customer_device_id] "
                    + "WHERE ddl.[digital_customer_profile_id] = :digitalCustomerProfileID "
                    + "AND dcd.[digital_device_udid] = :digitalDeviceUUID";
    public static final String GET_DIGITAL_CUSTOMER_DEVICE_LINK =
            SELECT
                    + "ddl.* "
                    + "FROM [digital].[digital_device_link] AS ddl "
                    + "WHERE ddl.[digital_customer_device_id] = :digitalCustomerDeviceId ";
    public static final String GET_CUSTOMER_EMAIL_AND_MOBILE_NUMBER =
            SELECT
                    + "ccp.customer_email as customerEmail, ccp"
                    + ".customer_mobile_no as customerMobileNo "
                    + "FROM core.core_customer_profile ccp "
                    + "WHERE EXISTS ( "
                    + "SELECT 1 "
                    + "FROM digital.digital_customer_profile ddp "
                    + "WHERE ddp.digital_customer_profile_id = :digitalCustomerProfileID "
                    + "AND ddp.core_customer_profile_id = ccp.core_customer_profile_id "
                    + ");";
    public static final String GET_CUSTOMER_LAST_LOGIN_ACTIVITY_TIME =
            SELECT + "activity_time "
                    + "FROM digital.digital_customer_activity ddca "
                    + "WHERE ddca.digital_customer_profile_id =CAST(?1 AS uniqueidentifier) "
                    + "ORDER by ddca.activity_time desc "
                    + "OFFSET 1 ROWS FETCH NEXT 1 ROW ONLY";
    private static final String JOIN = "JOIN ";
    public static final String FIND_CUSTOMER_RECENT_ACTIVITY_STATUS_BY_ACTIVITY_NAME =
            SELECT + "TOP 1 activity_status "
                    + "FROM digital.digital_customer_activity ddca "
                    + "WHERE ddca.digital_customer_profile_id =CAST(?1 AS uniqueidentifier) "
                    + "AND digital_activity_detail_ref_id = "
                    + "(SELECT ddadr.digital_activity_detail_ref_id "
                    + "FROM digital.digital_activity_detail_ref ddadr "
                    + "WHERE ddadr.digital_activity_name = ?2) "
                    + "AND CONVERT(DATE, ddca.activity_time) > "
                    + "CONVERT(DATE, DATEADD(MONTH, -?3, GETDATE()))"
                    + "ORDER by ddca.activity_time desc";
    public static final String FIND_BY_PROFILE_ID_AND_UDID =
            SELECT + "dcd.* "
                    + FROM + SchemaConstants.DIGITAL + ".digital_customer_device dcd "
                    + JOIN + SchemaConstants.DIGITAL + ".digital_device_link ddl ON dcd.digital_customer_device_id = "
                    + "ddl.digital_customer_device_id "
                    + "WHERE ddl.digital_customer_profile_id = :digitalCustomerProfileId "
                    + "AND dcd.digital_device_udid = :digitalDeviceUdid";
    public static final String FIND_ALERT_IDS_BY_ALERT_KEY =
            "SELECT dca.digital_customer_alert_id "
                    + FROM + SchemaConstants.DIGITAL + ".digital_customer_alert dca "
                    + JOIN + SchemaConstants.DIGITAL + ".digital_docdb_alert_ref ddar "
                    + "ON dca.digital_docdb_alert_ref_id = ddar.digital_docdb_alert_ref_id "
                    + "WHERE dca.digital_customer_profile_id = :customerProfileId "
                    + "AND dca.alert_read_flag = :alertReadFlag "
                    + "AND ddar.digital_alert_key = :digitalAlertKey";
    public static final String FETCH_DIGITAL_DEVICE_UDID = "SELECT dcd.digital_device_udid "
            + FROM
            + SchemaConstants.DIGITAL
            + ".[digital_customer_device] dcd "
            + JOIN
            + SchemaConstants.DIGITAL + ".[digital_device_link] ddl"
            + " ON ddl.digital_customer_device_id = dcd.digital_customer_device_id "
            + "where ddl.digital_customer_profile_id= :digitalCustomerProfileId";
    public static final String CAH_GET_BROADCAST_MESSAGES_BY_STATUS =
            SELECT + "digital_docdb_broadcast_msg_ref_id, broadcast_message_name, "
                    + "broadcast_message_start_date, broadcast_message_end_date, broadcast_to_all, "
                    + "broadcast_message_modification_date "
                    + FROM + SchemaConstants.DIGITAL + ".digital_docdb_broadcast_msg_ref AS bc "
                    + JOIN + SchemaConstants.DIGITAL + ".digital_template_status_ref AS temp "
                    + "ON bc.digital_template_status_ref_id=temp.digital_template_status_ref_id WHERE "
                    + "template_status_name= :status";


    public static final String LIVE_BROADCAST_MESSAGES_QUERY =
            SELECT
                    + "ddb.digital_docdb_broadcast_msg_ref_id, ddb.broadcast_message_name, "
                    + "ddb.broadcast_message_start_date, ddb.broadcast_message_end_date, ddb.broadcast_to_all "
                    + FROM + SchemaConstants.DIGITAL + ".digital_docdb_broadcast_msg_ref ddb "
                    + "LEFT "
                    + JOIN + SchemaConstants.DIGITAL + ".digital_template_status_ref dts "
                    + "ON ddb.digital_template_status_ref_id = dts.digital_template_status_ref_id "
                    + "WHERE dts.template_status_name = 'schedule' "
                    + "AND ddb.broadcast_message_start_date <= :currentTime "
                    + "AND ddb.broadcast_message_end_date >= :currentTime";
    public static final String UPCOMING_BROADCAST_MESSAGES_QUERY =
            SELECT
                    + "ddb.digital_docdb_broadcast_msg_ref_id, ddb.broadcast_message_name, "
                    + "ddb.broadcast_message_start_date, ddb.broadcast_message_end_date, ddb.broadcast_to_all "
                    + FROM
                    + SchemaConstants.DIGITAL + ".digital_docdb_broadcast_msg_ref ddb "
                    + "LEFT "
                    + JOIN
                    + SchemaConstants.DIGITAL + ".digital_template_status_ref dts "
                    + "ON ddb.digital_template_status_ref_id = dts.digital_template_status_ref_id "
                    + "WHERE dts.template_status_name = 'schedule' "
                    + "AND ddb.broadcast_message_start_date > :currentTime";
    private static final String DIGITAL_CUSTOMER_DEVICE_TABLE_AS = ".digital_customer_device AS dcd ";
    public static final String GET_PUBLIC_KEY_FOR_PIN =
            SELECT + "dcd.device_pin_auth_public_key "
                    + FROM + " " + SchemaConstants.DIGITAL + DIGITAL_CUSTOMER_DEVICE_TABLE_AS
                    + " INNER JOIN " + SchemaConstants.DIGITAL + ".digital_device_link AS ddl"
                    + " ON dcd.digital_customer_device_id = ddl.digital_customer_device_id"
                    + " INNER JOIN " + SchemaConstants.DIGITAL + ".digital_customer_profile AS dcp"
                    + " ON ddl.digital_customer_profile_id = dcp.digital_customer_profile_id"
                    + " WHERE dcd.digital_device_udid = :payloadDeviceId"
                    + " AND dcp.digital_user_name = :username";
    private static final String DIGITAL_CUSTOMER_DEVICE_TABLE_CONDITION =
            "ON ddl.digital_customer_device_id = dcd.digital_customer_device_id ";
    public static final String FIND_DIGITAL_CUSTOMER_PROFILE_ID =
            SELECT + "ddl.digital_customer_profile_id "
                    + "FROM [digital].[digital_device_link] ddl "
                    + "JOIN [digital].[digital_customer_device] dcd "
                    + DIGITAL_CUSTOMER_DEVICE_TABLE_CONDITION
                    + "WHERE dcd.digital_device_udid = :digitaldeviceudid ";
    public static final String GET_DIGITAL_CUSTOMER_DEVICE_ID =
            "SELECT dcd.digital_customer_device_id as deviceId "
                    + "FROM digital.digital_customer_device dcd "
                    + "INNER JOIN digital.digital_device_link ddl "
                    + DIGITAL_CUSTOMER_DEVICE_TABLE_CONDITION
                    + "WHERE ddl.digital_customer_profile_id = :digitalCustomerProfileId ";
    public static final String DEVICE_LINK_REGISTERED_FLAG_QUERY =
            SELECT
                    + "ddl.device_link_registered_flag "
                    + "FROM digital.digital_device_link ddl "
                    + "JOIN digital.digital_customer_device dcd "
                    + DIGITAL_CUSTOMER_DEVICE_TABLE_CONDITION
                    + "WHERE ddl.digital_customer_profile_id = :digitalCustomerProfileID "
                    + "AND dcd.digital_device_udid = :digitalDeviceUUID";
    private static final String DIGITAL_CUSTOMER_DEVICE_ID_JOIN_CONDITION =
            "ON dcd.digital_customer_device_id = ddl.digital_customer_device_id ";
    public static final String GET_DEVICEUUID_AND_PUBLIC_KEY =
            SELECT + "dcd.*"
                    + FROM + " digital.digital_customer_device AS dcd "
                    + "JOIN digital.digital_device_link AS ddl "
                    + DIGITAL_CUSTOMER_DEVICE_ID_JOIN_CONDITION
                    + "WHERE ddl.digital_customer_profile_id = :digitalCustomerProfileId";
    public static final String GET_REGISTERED_DEVICES =
            SELECT
                    + "ddl.digital_customer_profile_id, "
                    + "dcd.digital_customer_device_id, "
                    + "dcd.device_name, "
                    + "ddl.device_link_creation_date, "
                    + "ddl.device_link_modification_date, "
                    + "ddl.device_link_registered_flag, "
                    + "dcd.digital_device_udid "
                    + FROM + SchemaConstants.DIGITAL + DIGITAL_CUSTOMER_DEVICE_TABLE_AS
                    + JOIN + SchemaConstants.DIGITAL + ".digital_device_link AS ddl "
                    + DIGITAL_CUSTOMER_DEVICE_ID_JOIN_CONDITION
                    + "WHERE ddl.device_link_registered_flag = 1 AND "
                    + "ddl.digital_customer_profile_id = :digitalCustomerProfileId";
    public static final String GET_DEREGISTERED_DEVICES =
            SELECT
                    + "ddl.digital_customer_profile_id, "
                    + "dcd.digital_customer_device_id, "
                    + "dcd.device_name, "
                    + "ddl.device_link_creation_date, "
                    + "ddl.device_link_modification_date, "
                    + "ddl.device_link_registered_flag, "
                    + "dcd.digital_device_udid "
                    + FROM + SchemaConstants.DIGITAL + DIGITAL_CUSTOMER_DEVICE_TABLE_AS
                    + JOIN + SchemaConstants.DIGITAL + ".digital_device_link AS ddl "
                    + DIGITAL_CUSTOMER_DEVICE_ID_JOIN_CONDITION
                    + "WHERE ddl.device_link_registered_flag = 0 AND "
                    + "ddl.digital_customer_profile_id = :digitalCustomerProfileId";
    public static final String GET_DIGITAL_CUSTOMER_DEVICE =
            SELECT
                    + " dcd.* "
                    + FROM
                    + " digital.digital_customer_device AS dcd "
                    + "JOIN digital.digital_device_link AS ddl "
                    + DIGITAL_CUSTOMER_DEVICE_ID_JOIN_CONDITION
                    + "WHERE ddl.digital_customer_profile_id = :digitalCustomerProfileID "
                    + "AND dcd.digital_device_udid = :digitalDeviceUUID";
    private static final String DIGITAL_DEVICE_WHERE_CONDITION = " WHERE dcd.digital_device_udid = :digitalDeviceUdid ";
    public static final String GET_FACE_AUTH_PUBLIC_KEY =
            SELECT + "dcd.device_face_auth_public_key "
                    + "FROM digital.digital_customer_device AS dcd "
                    + DIGITAL_DEVICE_WHERE_CONDITION;
    public static final String GET_TOUCH_AUTH_PUBLIC_KEY =
            SELECT + "dcd.device_touch_auth_public_key "
                    + "FROM digital.digital_customer_device AS dcd "
                    + DIGITAL_DEVICE_WHERE_CONDITION;

    public static final String CAH_GET_BROADCAST_MESSAGE_BY_ID =
            SELECT + "digital_docdb_broadcast_msg_ref_id, "
                    + "broadcast_message_name, "
                    + "broadcast_message_start_date, broadcast_message_end_date, broadcast_to_all, "
                    + "temp.template_status_name "
                    + FROM + SchemaConstants.DIGITAL + ".digital_docdb_broadcast_msg_ref AS bc "
                    + JOIN + SchemaConstants.DIGITAL + ".digital_template_status_ref AS temp "
                    + "ON bc.digital_template_status_ref_id=temp.digital_template_status_ref_id WHERE "
                    + "bc.digital_docdb_broadcast_msg_ref_id= :id";

    public static final String CAH_GET_COUNT_BROADCAST_MESSAGES_FOR_ID_LIST =
            SELECT + "COUNT(*)"
                    + FROM + SchemaConstants.DIGITAL + ".digital_docdb_broadcast_msg_ref AS bc "
                    + JOIN + SchemaConstants.DIGITAL + ".digital_template_status_ref AS temp "
                    + "ON bc.digital_template_status_ref_id=temp.digital_template_status_ref_id WHERE "
                    + "bc.digital_docdb_broadcast_msg_ref_id IN :ids";

    public static final String FIND_ID_BY_TEMPLATE_STATUS_NAME = SELECT + "d.digital_template_status_ref_id FROM "
            + SchemaConstants.DIGITAL + ".digital_template_status_ref d "
            + "WHERE d.template_status_name = :templateStatusName";

    public static final String FETCH_ALL_LOCALE_CODE_BY_LANGUAGE_ENABLED_FLAG = "SELECT DISTINCT d.locale_code FROM "
            + SchemaConstants.DIGITAL + ".digital_language_ref d WHERE d.language_enabled_flag = 1";


    public static final String DELETE_BROADCAST_MESSAGE_BY_ID_ACCOUNT_TYPE =
            "UPDATE "
                    + SchemaConstants.DIGITAL
                    + ".[digital_docdb_broadcast_msg_ref] "
                    + "SET digital_template_status_ref_id = (SELECT digital_template_status_ref_id FROM "
                    + SchemaConstants.DIGITAL
                    + ".[digital_template_status_ref] "
                    + "WHERE template_status_name = :templateStatusName), "
                    + "broadcast_message_modification_date = :modificationDate, "
                    + "broadcast_message_modified_by = :modifiedBy "
                    + "WHERE digital_docdb_broadcast_msg_ref_id IN :ids";

    public static final String CAH_GET_BROADCAST_MESSAGE_BY_ID_OR_NAME =
            "SELECT bc.digital_docdb_broadcast_msg_ref_id, "
                    + "bc.broadcast_message_name, "
                    + "temp.template_status_name "
                    + "FROM " + SchemaConstants.DIGITAL + ".digital_docdb_broadcast_msg_ref AS bc "
                    + "JOIN " + SchemaConstants.DIGITAL + ".digital_template_status_ref AS temp "
                    + "ON bc.digital_template_status_ref_id = temp.digital_template_status_ref_id "
                    + "WHERE bc.digital_docdb_broadcast_msg_ref_id LIKE :nameOrId OR "
                    + "bc.broadcast_message_name LIKE :nameOrId";
    public static final String GET_REASON_BY_CATEGORY =
            SELECT + "reason_name FROM internal.internal_reason_ref "
            + "WHERE reason_category = :reasonCategory AND enabled_flag = 1";
}
package com.isteer.b2c.config;

import com.isteer.b2c.app.B2CApp;

public class DSRAppConstant {
	
	public static final int OPTYPE_UNKNOWN = -1;
	public static final int OPTYPE_GETALLEVENTS = 50;
	public static final int OPTYPE_GETCUSTCATLIST = 51;
	public static final int OPTYPE_GETCUSTOMERS = 52;
	public static final int OPTYPE_GETSPANCOPLIST = 53;
	public static final int OPTYPE_GETMASTERITEM = 54;
	public static final int OPTYPE_GETITEMMSTR = 55;
	public static final int OPTYPE_GETPROSPECTMSTR = 56;

	public static final String DEFAULT_PASSSCODE = "2015";
	public static final long DEFAULT_ATTENDENCE_INTERVAL = 5*60*1000;
	
	public static final int SYNC_TYPE_NOTHING = -1;
	public static final int SYNC_TYPE_PLANS = 101;
	public static final int SYNC_TYPE_REFRESH = 102;
	
	public static final int SYNC_STATUS_RED = 151;
	public static final int SYNC_STATUS_YELLOW = 152;
	public static final int SYNC_STATUS_GREEN = 153;

	public static final String KEY_COMPANY_CODE = "company_code";
	public static final String KEY_REG_ID = "reg_id";
	public static final String KEY_IMEI_NO = "imei_no";
	public static final String KEY_ANDROID_ID = "android_id";
	public static final String KEY_MODEL_NAME = "model_name";
	
	public static final String datetimeFormat = "yyyy-MM-dd kk:mm";
	public static  final String  datetimeFullFormat = "yyyy-MM-dd kk:mm:ss";
	
	public static final String KEY_INVOICE_NO = "invoice_no";
	public static final String KEY_PARCEL_COUNT = "parcel_count";
	public static final String KEY_CUST_NAME = "customer_name";
	public static final String KEY_CUST_PHONE = "customer_phone";
	public static final String KEY_DELIVERY_ADDRESS = "delivery_address";
	public static final String KEY_COMPANY_NAME = "company_name";

	public static final String KEY_BRANCHES = "branches";
	public static final String KEY_BRANCH_CODE = "brCode";
	public static final String KEY_BRANCH_NAME = "brName";
	
	public static final String KEY_SE_KEY = "sekey";
	public static final String KEY_UNIT_KEY = "unit_key";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_TOKEN = "token";
	
	public static final String KEY_SITE = "site";
	public static final String KEY_SITE2 = "site2";
	public static final String KEY_SUCCESS = "success";
	public static final String KEY_RESULT = "result";

	public static final String KEY_STATUS = "status";
	public static final String KEY_MSG = "msg";
	public static final String KEY_DATA = "data";
    	
	public static final String KEY_USER_KEY = "user_key";
	public static final String KEY_PUNIT_KEY = "p_unit_key";
	public static final String KEY_CM_KEY = "cm_key";

	public static final String KEY_DUMMY_KEY = "dummy_key";
	public static final String KEY_VALUES = "vals";
	public static final String KEY_DETAILS = "details";

	public static final String KEY_LMONTH_SALES = "lastMonthTotalSales";
	public static final String KEY_CMONTH_SALES = "thisMonthTillDateSales";
	public static final String KEY_LDAY_BILL = "yesterdayBillDetails";
	public static final String KEY_PLANNED_CALLS = "thisMonthPlannedCalls";
	public static final String KEY_PENDING_CALLS = "thisMonthPendingCalls";
	public static final String KEY_VISITED_CALLS = "thisMonthVisitedCalls";
	public static final String KEY_COMPLETED_CALLS = "thisMonthCompletedCalls";
	
	public static final String KEY_STATUS_STARTED = "Started";
	public static final String KEY_STATUS_STOPPED = "Stopped";
	public static final String KEY_UPDATE_STATUS_PENDING = "Pending";
	public static final String KEY_UPDATE_STATUS_UPDATED = "Updated";
    	
   	public static final String KEY_LOGIN_CHECK_TIME = "LoginCheckTime";
	public static final String KEY_MAX_LOGIN_TIME = "MaxLoginTime";
	public static final String KEY_MAX_LOGIN_ATTEMPT = "MaxLoginAttempt";
	public static final String KEY_ATT_TIME = "AttendanceTrackingTime";
	public static final String KEY_ALARM_TIME = "AlarmTime";
    
	public static final String KEY_ALL_BRANDS = "All brands";
	
    	
	public static enum EVENT_STATUS {
		
		Pending, Visited, Completed, Cancelled,CheckIn
	};

	public static final int STATUS_PENDING = 0;
	public static final int STATUS_CHECKIN = 4;
	public static final int STATUS_VISITED = 1;
	public static final int STATUS_CONMPLETED = 2;
	public static final int STATUS_CANCELLED = 3;
	
	// API Methods
	public static final String[] VISIT_TYPES = {"Visit", "Meet", "Leave", "Office"};

	/*Options For Product Types*/
	public static final String[] PRODUCT_TYPES = {"Regular", "Project"};

	/*Options For UOM*/
	public static final String[] UOM = {"Ltrs", "Kgs"};
	
	/*Options For UOM*/
	public static final String[] BUYING_FREQUENCY_REGULAR = {"Weekly", "Bi-Weekly","Monthly","Quarterly","Half-yearly","Annually"};
	public static final String[] BUYING_FREQUENCY_PROJECT = {"One-Time Event"};
	
	// API Methods
	public static final String METHOD_LOGIN = "/loginauthentication/";
	public static final String METHOD_VALIDATE_TOKEN = "isTokenValid/";
	public static final String METHOD_GETALLEVENTS = "/getAllEventDetails/";
	public static final String METHOD_SETASVISITED = "/setAsVisited/";
	public static final String METHOD_SETASCANCELLED = "/setAsCancelled/";
	public static final String METHOD_SETASCOMPLETED= "/setAsCompleted/";
	public static final String METHOD_ADD_NEW_EVENT= "/addNewEvent/";
	public static final String METHOD_ADDALL_NEW_EVENTS= "addAllNewEvent/";
	public static final String METHOD_UPDATE_ALL_DATA= "/updateAllEventDetails/";
	public static final String METHOD_GET_CUST_CAT_LIST= "/getEventCategoryAndCustomerList/";
	public static final String METHOD_GET_CUSTOMER_DETAILS= "/iSteer_customer_details/";
	public static final String METHOD_GET_SPANCOPLIST= "/isteer_get_spancoplist/";
	public static final String METHOD_GET_DSR_REPORT= "/dsrReport/";
	public static final String METHOD_GET_AERP_MASTER_ITEM= "/get_aerp_master_item/";
	public static final String METHOD_GET_AERP_ITEM_MSTR= "/get_aerp_item_mstr/";
	public static final String METHOD_GET_AERP_PROSPECT_MSTR= "/get_aerp_prospect_mstr/";
	public static final String METHOD_UPDATE_ALL_EDITED_SPANCOP= "/UpdateAllEditedSpancop/";
	public static final String METHOD_GET_URL_FOR_MOBILEAPP= "/getUrlForMobileWebApp/";
	public static final String METHOD_UPDATE_ATTENDENCE_LOG= "UpdateAttendenceLog/";
	public static final String METHOD_UPDATE_LOCATION_LOG= "UpdateLocationLog/";
	public static final String METHOD_PUT_GCMID = "putRegIDS";
	public static final String METHOD_UPDATE_CUST_LOC = "updateCustomerLoc/";
	public static final String METHOD_GET_DAYANDMONTH_REPORT = "getDayAndMonthlyReport/";

	//http://54.186.232.56/rest/getAllEventDetails
	
	//public static final String URL_REGISTER = "http://54.186.232.56/register.php";
	public static final String URL_REGISTER = "http://appregistration.isteer.co/salesmgt.php";
	
	public static String getAlertUrl()
	{
		return "http://stageserver.amshuhu.com/shellcrm/control" + "?loginName=" + B2CApp.b2cPreference.getUserName() + 
				"&passWord=" + B2CApp.b2cPreference.getUserPass() +
				"&action=base&event=validate&applicationtype=mobile&showscreen=dsrtarget";
	}
	
	public static String getAlertUrlSuffix()
	{
		return "?loginName=" + B2CApp.b2cPreference.getUserName() + 
				"&passWord=" + B2CApp.b2cPreference.getUserPass() +
				"&action=base&event=validate&applicationtype=mobile&showscreen=dsrtarget";
	}
	
}

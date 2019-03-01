package com.isteer.b2c.config;

import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.app.B2CApp;

public class B2CAppConstant {
	public static String alert = "Alert !";

	public static final String PAY_CASH = "Cash";
	public static final String PAY_CHEQUE = "Cheque";
	public static final String PAY_RTGS = "RTGS";
	public static final String BACKTOCOUNTERDETAILS = "backToCounterDetails";
	public static final String BACKTOCOUNTERSCREEN = "backtocounterscreen";
	public static final String BACKTOCUSTOMERSEARCH = "backtocustomersearch";
	public static final String BACKTOCOLLECTIONSUM = "backtocollectionsum";

	public static final String LOADING = "Loading...";

	public static final String[] PAY_MODES = {PAY_CASH, PAY_CHEQUE};
	public static String start_latitude = "start_latitude";
	public static String stop_latitude = "stop_latitude";
	public static String start_longitude = "start_longitude";
	public static String stop_longitude = "stop_longitude";


	public static enum ORDER_TYPE {
		
		Phone, Counter
	};
	
	public static final int MENU_SCREEN	= 1;
	public static final int COLLECTION_SCREEN	= 2;
	public static final int COUNTER_DETAILS	= 3;
	public static final int ADDTOBEATPLAN	= 4;
	public static final int ACTIONORDER	= 5;
	public static final int REPORTORDER	= 6;
	public static final int CUSTOMERSEARCH	= 7;
	public static final int ADDPROGSCREEN	= 8;
	public static final int UNVISITEDCUS	= 9;


	public static final int SCREEN_ADDTO_BEAT	= 31;
	public static final int SCREEN_COLL_ENTRY 	= 32;
	public static final int SCREEN_COLL_SUMM 	= 33;
	public static final int SCREEN_COUNTER_DET	= 34;
	public static final int SCREEN_COUNTERS		= 35;
	public static final int SCREEN_CUST_SEARCH 	= 36;
	public static final int SCREEN_LOCATE	 	= 37;
	public static final int SCREEN_MENU 		= 38;
	public static final int SCREEN_PENDING_ORDER= 39;
	public static final int SCREEN_PLACE_ORDER	= 40;
	public static final int SCREEN_PRODUCT_CAT	= 41;
	public static final int SCREEN_SYNC			= 42;
	
	public static final int OPTYPE_UNKNOWN = -1;
	public static final int OPTYPE_GET_PRODUCTS = 50;
	public static final int OPTYPE_GET_CUST_DET = 51;
	public static final int OPTYPE_GET_CUST_LOC = 52;
	public static final int OPTYPE_GET_PENDINGBILLS = 53;
	public static final int OPTYPE_GET_ALLPENDINGBILLS = 69;
	public static final int OPTYPE_GET_CUSTALLCREDITS = 68;
	public static final int OPTYPE_GET_CUSTCREDITS = 54;
	public static final int OPTYPE_GET_PENDINGORDERS = 55;
	public static final int OPTYPE_GET_ALLPENDINGORDERS = 70;
	public static final int OPTYPE_GET_ALL_COLLECTION = 56;
	public static final int OPTYPE_PLACE_AN_ORDER = 57;
	public static final int OPTYPE_GET_STOCK = 58;
	public static final int OPTYPE_UPDATE_ORDERS = 59;
	public static final int OPTYPE_INSERT_ALL_COLLECTION = 60;
	public static final int OPTYPE_MAP_BILLS = 61;
	public static final int OPTYPE_DELIVERY_INVOICE = 62;
	public static final int OPTYPE_RETURN_OF_GOODS = 63;
	public static final int OPTYPE_OUTSTANDING = 64;
	public static final int OPTYPE_PENDING_BILLS = 65;
	public static final int OPTYPE_ATTENDENCE_LOG = 66;
	public static final int OPTYPE_UPDATE_LOCATION_LOG = 67;
	public static final int OPTYPE_UPDATEALLCUSTOMERLOCATION = 77;

	public static final int SYNC_STATUS_RED = 151;
	public static final int SYNC_STATUS_YELLOW = 152;
	public static final int SYNC_STATUS_GREEN = 153;

	public static final int SYNC_TYPE_EVENT = 201;
	public static final int SYNC_TYPE_CONTACT = 202;
	
	public static final String datetimeFormatInt = "yyyyMMddkkmmss";
	public static final String datetimeFormat = "yyyy-MM-dd kk:mm:ss";
	public static final String datetimeFormat1 = "yyyy-MM-dd kk:mm";
	public static final String dateFormat = "yyyy-MM-dd";
	public static final String dateFormat2 = "yyyyMMdd";
	public static final String dateFormat3 = "dd-MM-yyyy";
	public static final String dateFormat4 = "MM-dd-yyyy";
	public static final String timeFormat = "kk:mm:ss";
	public static final String dateFormat5 = "yyyyMM";

	public static final String KEY_CUST_ID = "cus_id";
	public static final String KEY_UNIT_KEY = "unit_key";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_TOKEN = "Token";
	public static final String KEY_SE_KEY = "se_key";
	public static final String KEY_CUST_KEY = "cus_key";
	public static final String KEY_CUST_KEYS = "cus_keys";
	public static final String KEY_CONTACT_KEY = "contact_key";
	public static final String KEY_AUTH_TOKEN = "auth_token";
	public static final String KEY_SECURITY_TOKEN = "security_token";
	public static final String KEY_BILLING_ADDRESS = "billing_address";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_NO_OF_QTY = "no_of_qty";
	public static final String KEY_MFGR_KEY = "mfgr_key";
	public static final String KEY_SO_ITEM_QTY = "so_item_qty";
	public static final String KEY_SO_ITEM_DISCOUNT_PER = "so_item_discount_per";
	public static final String KEY_SO_ITEM_DISCOUNTED_RATE = "so_item_discounted_rate";
	public static final String KEY_CONTACT_PERSON = "contact_person";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PHONE_NUMBER  = "phone_number";
	
	public static final String KEY_BANK = "bank";
	public static final String KEY_TRANS_DATE = "trans_date";
	public static final String KEY_PAYMENT_MODE = "payment_mode";
	public static final String KEY_CHEQUE_NO = "cheque_no";
	public static final String KEY_CHEQUE_DATE = "cheque_date";
    
	public static final String KEY_BILLS = "bills";
	public static final String KEY_CREDIT_DAYS = "credit_days";
	public static final String KEY_CREDIT_USED = "credit_used";
	public static final String KEY_TYPE = "type";
	public static final String KEY_UNMAPPED_AMOUNT = "unmapped_amount";
	public static final String KEY_SUSPENSE_AMOUNT = "suspense_amount";
	
	public static final String KEY_REMARKS1 = "remarks";

	public static final String KEY_COMPANY_CODE = "company_code";
	public static final String KEY_REG_ID = "reg_id";
	public static final String KEY_IMEI_NO = "imei_no";
	public static final String KEY_ANDROID_ID = "android_id";
	public static final String KEY_MODEL_NAME = "model_name";

	public static final String KEY_SITE1 = "sitename1";
	public static final String KEY_SITE2 = "sitename2";
	public static final String KEY_SITE3 = "sitename3";
	public static final String KEY_SITE4 = "sitename4";
	public static final String KEY_SITE5 = "sitename5";
	public static final String KEY_SUCCESS = "success";

	public static final String IS_SUCCESS = "is_success";
	public static final String KEY_STATUS = "status";
	public static final String KEY_MSG = "msg";
	public static final String KEY_DATA = "data";
	public static final String KEY_ARRAY_DATA = "array_data";
	public static final String KEY_CUSTOMER_KEY = "customer_key";
	public static final String KEY_SALES_ORDER = "sales_order";
	
	public static final String KEY_SO_KEY = "so_key";
	public static final String KEY_SO_ITEM_KEY = "so_item_key";
	public static final String KEY_SUPPLIED_QTY = "supplied_qty";
	public static final String KEY_PUR_ODR_DATE = "pur_odr_date";
	public static final String KEY_PUR_ODR_TYPE = "purchase_order_type";
	public static final String KEY_PAY_COLL_KEY = "pay_coll_key";
	public static final String KEY_SC_LEDGER_KEY = "sc_ledger_key";
	
	public static final String KEY_MI_NAME = "mi_name";
	public static final String KEY_DATE = "date";
	public static final String KEY_ORDERED_QTY = "ordered_qty";
	public static final String KEY_PENDING_QTY = "pending_qty";
	public static final String KEY_PENDING_ORDERS = "pending_orders";

	public static final String KEY_USER_KEY = "user_key";
	public static final String KEY_PUNIT_KEY = "p_unit_key";
	public static final String KEY_CM_KEY = "cm_key";
	public static final String KEY_RESULT = "result";
	public static final String KEY_BRANCHES = "branches";
	public static final String KEY_BRANCH_CODE = "brCode";
	public static final String KEY_BRANCH_NAME = "brName";
	
	public static final String KEY_OBJ_NAME = "objName";
	public static final String KEY_MANU_CODE = "manu_code";
	public static final String KEY_MOD_TIME = "modified_time";
	public static final String KEY_LAST_INDEX = "lastindex";
	public static final String KEY_MI_KEY = "mi_key";
	public static final String KEY_MIKEY = "mikey";
	public static final String KEY_PART_NO = "part_no";
	public static final String KEY_QTY = "qty";
	public static final String KEY_PRICE = "price";
	public static final String KEY_TOTAL = "total";
	public static final String KEY_REMARKS = "remarks";
	public static final String KEY_TAX_PERCENT = "tax_percent";
	public static final String KEY_RECEIPT_NO = "receipt_no";
	public static final String KEY_ENTERED_ON = "entered_on";
	public static final String KEY_ENTERED_DATE_TIME = "entered_date_time";

	public static final String KEY_LOGIN_CHECK_TIME = "LoginCheckTime";
	public static final String KEY_MAX_LOGIN_TIME = "MaxLoginTime";
	public static final String KEY_MAX_LOGIN_ATTEMPT = "MaxLoginAttempt";
	public static final String KEY_ATT_TIME = "AttendanceTrackingTime";
	public static final String KEY_ALARM_TIME = "AlarmTime";
	
	public static final String KEY_CMKEY = "cmkey";
	public static final String KEY_CON_KEY = "con_key";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_ALTITUDE = "altitude";
	
	public static final String KEY_LAST_iNDEX = "lastIndex";
	public static final String KEY_TOTAL_RECORDS = "totalRecords";
    
	public static final String KEY_ALL_BRANDS = "All brands";
	
	public static final String KEY_QUANTITY = "quantity";
	public static final String KEY_UNIT_NAME = "unit_name";
	public static final String KEY_UNIT_LOCATION = "unit_location";
	public static final String KEY_STOCK = "stock";
	public static final String KEY_AVAILABILITY = "availability";
	
	public static final String URL_REGISTER = "http://appregistration.isteer.co/b2cisteer.php";

	// API Methods
	public static final String METHOD_LOGIN = "loginauthentication";
	public static final String METHOD_GET_PRODUCTS = "getMasterItem/";
	//public static final String METHOD_PLACE_AN_ORDER = "PlaceOrderOnly/";
	public static final String METHOD_VALIDATE_TOKEN = "isTokenValid/";
	public static final String METHOD_PUT_GCMID = "putRegIDS";

	public static final String METHOD_GET_CUST_DET = "iSteer_customer_details/";
	public static final String METHOD_GET_CUST_LOC = "getAllCustomerSiteLoc/";

	public static final String METHOD_GET_ALLPENDINGBILLS = "getallPendingBill/";
	public static final String METHOD_GET_PENDINGBILLS = "getPendingBill/";
	public static final String METHOD_GET_CREDITDETAILS = "getCustomerCreditDetails/";
	public static final String METHOD_GET_ALLCREDITDETAILS = "getAllCustomerCreditDetails/";
	public static final String METHOD_GET_PENDINGORDERS = "getPendingSalesOrder/";
	public static final String METHOD_GET_ALLPENDINGORDERS = "getAllPendingSalesOrder/";
	public static final String METHOD_GET_ALL_COLLECTION = "getAllCollection/";
	public static final String METHOD_ADDSALESORDER = "addSalesOrder/";
	public static final String METHOD_PUT_COLLECTION = "insertCollection/";
	public static final String METHOD_GET_STOCK = "getStockAvailability/";
	public static final String METHOD_PLACE_AN_ORDER = "placeOrderEmployee/";
	public static final String METHOD_UPDATE_ALL_ORDERS = "updateAllOrders/";
	public static final String METHOD_INSERT_ALL_COLLECTION = "insertAllCollection/";
	public static final String METHOD_MAP_BILLS = "mapbills";
	public static final String METHOD_DELIVERY_INVOICE = "SI_view";
	public static final String METHOD_RETURN_OF_GOODS = "SIR_view";
	public static final String METHOD_OUTSTANDING_REPORT = "customer_outstanding";
	public static final String METHOD_PENDING_BILLS = "pending_bills";

	public static final String METHOD_UNMAPPED_COLLECTION = "getUnmappedCollection";
	public static final String METHOD_PENDINGBILL = "getPendingBill";
	public static final String METHOD_SAVE_ACCOUNT_DETAILS = "saveAccountDetails";
	public static final String METHOD_GETUNVISITEDCUSTOMER = "getUnvisitedCustomer";


	
	public static String getWebUrl(int opType)
	{

		String urlPrefix = B2CApp.b2cPreference.getBaseUrl5() + "?loginName=" + B2CApp.b2cPreference.getUserName() 
				+ "&passWord=" + B2CApp.b2cPreference.getUserPass() + "&action=base&event=validate&applicationtype=mobile"
				+ "&showscreen=";
			
		String urlSuffix = "";
		
		if(opType==OPTYPE_MAP_BILLS)
			urlSuffix = METHOD_MAP_BILLS;
		else if(opType==OPTYPE_DELIVERY_INVOICE)
			urlSuffix = METHOD_DELIVERY_INVOICE;
		else if(opType==OPTYPE_RETURN_OF_GOODS)
			urlSuffix = METHOD_RETURN_OF_GOODS;
		else if(opType==OPTYPE_OUTSTANDING)
			urlSuffix = METHOD_OUTSTANDING_REPORT;
		else if(opType==OPTYPE_PENDING_BILLS)
			urlSuffix = METHOD_PENDING_BILLS + "&unit_key=" + B2CApp.b2cPreference.getUnitKey() + "&se_key=" + -1
			+ "&manu_key=" + "&cus_key=" + B2CCounterDetailScreen.currentCustomer.getCus_key() + "&group_key=" + -1;
		
		return urlPrefix + urlSuffix;
	}
	public static final String TODAY_VISIT = "today_visit";
	public static final String MONTHLY_VISIT = "monthly_visit";
	public static final String TODAY_COLLECTION = "today_collection";
	public static final String MONTHLY_COLLECTION = "monthly_collection";
	public static final String TODAY_SALES = "today_sales";
	public static final String MONTHLY_SALES = "monthly_sales";

}
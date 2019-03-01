package com.isteer.b2c.preference;

import java.util.HashSet;
import java.util.Set;

import com.isteer.b2c.config.B2CAppConfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class B2CPreference {

	private SharedPreferences mSharedPreferences;

	// B2C
	private static final String APP_VERSION = "app_version";
	private static final String GCM_REG_ID  = "gcm_reg_id";
	
	private static final String COMPANY_CODE = "company_code";

	private static final String LAST_LOGIN = "last_login";

	private static final String USER_NAME = "username";
	private static final String USER_PASS = "password";
		
	private static final String IS_REGISTERED = "is_registered";
	private static final String BASE_URL1 = "site1";
	private static final String BASE_URL2 = "site2";
	private static final String BASE_URL3 = "site3";
	private static final String BASE_URL4 = "site4";
	private static final String BASE_URL5 = "site5";

	private static final String IS_LOGGED_IN = "is_logged_in";
	private static final String USER_USER_ID = "user_id";
	private static final String USER_UNIT_KEY = "unit_key";
	private static final String USER_SEKEY = "sekey";
	private static final String USER_TOKEN = "token";
	
	private static final String IS_LOGIN_FAILED = "is_login_failed";
	private static final String LOGIN_FAIL_COUNT = "login_fail_count";
	private static final String IS_TOKEN_VALID = "is_token_valid";
	private static final String LAST_VALIDATED_TIME = "last_validated_time";
	private static final String BRANCH_CODE = "branch_code";

	private static final String IS_DB_FILLED = "is_db_filled";
	private static final String IS_FILLED_CUSTOMERS = "is_filled_customers";
	private static final String IS_FILLED_CUSTOMERS_INDIVIDUAL = "is_filled_customers_individual";
	private static final String IS_FILLED_COLLECTION_STATUS = "is_filled_collection_status";
	private static final String IS_FILLED_PENDING_BILLS = "is_filled_pending_bills";
	private static final String IS_FILLED_CUSTOMER_CREDITS = "is_filled_customer_credits";
	private static final String IS_FILLED_PENDING_ORDERS = "is_filled_pending_orders";
	private static final String IS_UPDATED_PENDING_ORDERS = "is_updated_pending_orders";
	private static final String IS_FILLED_PRODUCTS = "is_filled_products";
	private static final String DB_FILLED_TIME = "db_filled_time";
	private static final String IS_SYNCED_COLLECTIONS = "is_synced_collections";
	private static final String IS_FILLED_COLLECTION = "is_filled_collection";

	private static final String LAST_INDEX_FILLED = "last_index";

	private static final String LOGIN_CHECK_TIME = "LoginCheckTime";
	private static final String MAX_LOGIN_TIME = "MaxLoginTime";
	private static final String MAX_LOGIN_ATTEMPT = "MaxLoginAttempt";
	private static final String ATT_TIME = "AttendanceTrackingTime";
	private static final String ALARM_TIME = "AlarmTime";
	
	private static final String BR_CODES = "br_codes";
	private static final String BR_NAMES = "br_names";
	
	//DSR
	private static final String NEW_ENTRY_ATTENDANCE = "new_entry_attendance";
	private static final String NEW_ENTRY_COUNT = "new_entry_count";
	private static final String NEW_ENTRY_COUNT_SPANCOP = "new_entry_count_spancop";

	private static final String TEMP_CUSTOMER_LIST = "temp_customer_list";
	
	private static final String DayStarted = "daystarted";
	private static final String PASS_CODE = "passcode";
	public static final String Start_Date_key="startdatekey";
	public static final String SelectedCount="selectedcount";
	private static String IS_DAYSTOPPED_AUTO = "is_daystopped_auto";
	private static String CHECKEDIN = "checkedin";
	private static String CHECKEDINCMKEY = "checkedincmkey";
	private static String CHECKEDINEVENTKEY = "checkedineventkey";

	private static String TODAYOBJ_VISIT = "todayobj_visit";
	private static String TODAYOBJ_SALE = "todayobj_sale";
	private static String TODAYOBJ_COLLECTION = "todayobj_collection";
	private static String TODAYOBJ_CURRENTDATE = "todayobj_currentdate";

	private static String CHECKEDINCUSKEY = "checkedincuskey";

	private static String OPENING_POINTS = "opening_points";

	private static String FULLYSYNCEDTOSERVER = "fullysyncedtoserver";

	private static final String IS_LOGIN_FIRST = "is_login_first";

	public B2CPreference(Context context) {
		mSharedPreferences = context.getSharedPreferences("sem_preference", Context.MODE_PRIVATE);
	}

	public String getString(String pref_name){
		
		return mSharedPreferences.getString(pref_name, null);
	}
		
	//B2C
	
	public void setCompanyCode(String company_code) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(COMPANY_CODE, company_code);
		edit.commit();
	}

	public String getCompanyCode() {
		return mSharedPreferences.getString(COMPANY_CODE, "");
	}
	
	public void setGCMRegID(String gcm_reg_id) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(GCM_REG_ID, gcm_reg_id);
		edit.commit();
	}

	public String getGCMRegID() {
		return mSharedPreferences.getString(GCM_REG_ID, "");
	}
	
	public void setAppVersion(int appVersion) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(APP_VERSION, appVersion);
		edit.commit();
	}

	public int getAppVersion() {
		return mSharedPreferences.getInt(APP_VERSION, -1);
	}
	
	public boolean isRegistered() {
		return mSharedPreferences.getBoolean(IS_REGISTERED, false);
	}
	
	public void setIsRegistered(boolean isRegistered) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_REGISTERED, isRegistered);
		edit.commit();
	}
	
	public boolean isLoggedIn() {
		return mSharedPreferences.getBoolean(IS_LOGGED_IN, false);
	}

	public void setIsLoggedIn(boolean isLoggedIn) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_LOGGED_IN, isLoggedIn);
		edit.commit();
	}
	
	public void setBaseUrl(String url) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(BASE_URL1, url);
		edit.commit();
	}

	public String getBaseUrl() {
		return mSharedPreferences.getString(BASE_URL1, null);
	}
	
	public void setBaseUrl2(String url) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(BASE_URL2, url);
		edit.commit();
	}

	public String getBaseUrl2() {
		return mSharedPreferences.getString(BASE_URL2, null);
	}
	
	public void setBaseUrl3(String url) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(BASE_URL3, url);
		edit.commit();
	}

	public String getBaseUrl3() {
		return mSharedPreferences.getString(BASE_URL3, null);
	}
	
	public void setBaseUrl4(String url) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(BASE_URL4, url);
		edit.commit();
	}

	public String getBaseUrl4() {
		return mSharedPreferences.getString(BASE_URL4, null);
	}
	
	public void setBaseUrl5(String url) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(BASE_URL5, url);
		edit.commit();
	}

	public String getBaseUrl5() {
		return mSharedPreferences.getString(BASE_URL5, null);
	}

	public void setUserId(String user_id) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(USER_USER_ID, user_id);
		edit.commit();
	}

	public String getUserId() {
		return mSharedPreferences.getString(USER_USER_ID, null);
	}
	
	public void setUnitKey(String unit_key) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(USER_UNIT_KEY, unit_key);
		edit.commit();
	}

	public String getUnitKey() {
		return mSharedPreferences.getString(USER_UNIT_KEY, null);
	}
	
	public void setSekey(String sekey) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(USER_SEKEY, sekey);
		edit.commit();
	}

	public String getSekey() {
		return mSharedPreferences.getString(USER_SEKEY, null);
	}
	
	public void setToken(String token) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(USER_TOKEN, token);
		edit.commit();
	}

	public String getToken() {
		return mSharedPreferences.getString(USER_TOKEN, null);
	}
	
	public boolean isDbFilled() {
		return mSharedPreferences.getBoolean(IS_DB_FILLED, false);
	}
	
	public void setIsDbFilled(boolean dbFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_DB_FILLED, dbFilled);
		edit.commit();
	}
	
	public boolean isFilledCustomers() {
		return mSharedPreferences.getBoolean(IS_FILLED_CUSTOMERS, false);
	}
	
	public void setIsFilledCustomers(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_FILLED_CUSTOMERS, isFilled);
		edit.commit();
	}
	
	public boolean isFilledCustomerIndividual() {
		return mSharedPreferences.getBoolean(IS_FILLED_CUSTOMERS_INDIVIDUAL, false);
	}
	
	public void setIsFilledCustomerIndividual(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_FILLED_CUSTOMERS_INDIVIDUAL, isFilled);
		edit.commit();
	}
	
	public boolean isFilledCollectionStatus() {
		return mSharedPreferences.getBoolean(IS_FILLED_COLLECTION_STATUS, false);
	}
	
	public void setIsFilledCollectionStatus(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_FILLED_COLLECTION_STATUS, isFilled);
		edit.commit();
	}
	
	public boolean isFilledPendingBills() {
		return mSharedPreferences.getBoolean(IS_FILLED_PENDING_BILLS, false);
	}
	
	public void setIsFilledPendingBills(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_FILLED_PENDING_BILLS, isFilled);
		edit.commit();
	}
	
	public boolean isFilledCustomerCredits() {
		return mSharedPreferences.getBoolean(IS_FILLED_CUSTOMER_CREDITS, false);
	}
	
	public void setIsFilledCustomerCredits(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_FILLED_CUSTOMER_CREDITS, isFilled);
		edit.commit();
	}
	
	public boolean isUpdatedCollections() {
		return mSharedPreferences.getBoolean(IS_SYNCED_COLLECTIONS, false);
	}
	
	public void setIsUpdatedCollections(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_SYNCED_COLLECTIONS, isFilled);
		edit.commit();
	}
	
	public boolean isFilledPendingOrders() {
		return mSharedPreferences.getBoolean(IS_FILLED_PENDING_ORDERS, false);
	}
	
	public void setIsFilledPendingOrders(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_FILLED_PENDING_ORDERS, isFilled);
		edit.commit();
	}

	public boolean isFilledCollection() {
		return mSharedPreferences.getBoolean(IS_FILLED_COLLECTION, false);
	}

	public void setIsFilledCollection(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_FILLED_COLLECTION, isFilled);
		edit.commit();
	}
	
	public boolean isUpdatedPendingOrders() {
		return mSharedPreferences.getBoolean(IS_UPDATED_PENDING_ORDERS, false);
	}
	
	public void setIsUpdatedPendingOrders(boolean isUpdated) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_UPDATED_PENDING_ORDERS, isUpdated);
		edit.commit();
	}
	
	public boolean isFilledProducts() {
		return mSharedPreferences.getBoolean(IS_FILLED_PRODUCTS, false);
	}
	
	public void setIsFilledProducts(boolean isFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_FILLED_PRODUCTS, isFilled);
		edit.commit();
	}
		
	public String getDBFilledTime() {
		return mSharedPreferences.getString(DB_FILLED_TIME, "");
	}
	
	public void setDBFilledTime(String dbFilledTime) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(DB_FILLED_TIME, dbFilledTime);
		edit.commit();
	}	
		
	public void setLastLogin(long newLastLogin) {
		Editor edit = mSharedPreferences.edit();
		edit.putLong(LAST_LOGIN, newLastLogin);
		edit.commit();
	}

	public long getLastLogin() {
		return mSharedPreferences.getLong(LAST_LOGIN, 0l);
	}
	
	public String getUserName() {
		return mSharedPreferences.getString(USER_NAME, null);
	}
	
	public void setUserName(String username) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(USER_NAME, username);
		edit.commit();
	}
	
	public String getUserPass() {
		return mSharedPreferences.getString(USER_PASS, null);
	}
	
	public void setUserPass(String userpass) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(USER_PASS, userpass);
		edit.commit();
	}
	
	public int getLastIndexFilled() {
		return mSharedPreferences.getInt(LAST_INDEX_FILLED, 0);
	}
	
	public void setLastIndexFilled(int lastIndexFilled) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(LAST_INDEX_FILLED, lastIndexFilled);
		edit.commit();
	}
	
	public boolean isLoginFailed() {
		return mSharedPreferences.getBoolean(IS_LOGIN_FAILED, false);
	}

	public void setIsLoginFailed(boolean isLoginFailed) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_LOGIN_FAILED, isLoginFailed);
		edit.commit();
	}
	
	public int getLoginFailCount() {
		return mSharedPreferences.getInt(LOGIN_FAIL_COUNT, 0);
	}

	public void setLoginFailCount(int loginFailCount) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(LOGIN_FAIL_COUNT, loginFailCount);
		edit.commit();
	}
	
	public boolean isTokenValid() {
		return mSharedPreferences.getBoolean(IS_TOKEN_VALID, false);
	}

	public void setIsTokenValid(boolean isTokenValid) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_TOKEN_VALID, isTokenValid);
		edit.commit();
	}	
	
	public void setLastValidatedTime(long lastValidatedTime) {
		Editor edit = mSharedPreferences.edit();
		edit.putLong(LAST_VALIDATED_TIME, lastValidatedTime);
		edit.commit();
	}

	public long getLastValidatedTime() {
		return mSharedPreferences.getLong(LAST_VALIDATED_TIME, 0l);
	}
	
	public String getBranchCode() {
		return mSharedPreferences.getString(BRANCH_CODE, "");
	}
	
	public void setBranchCode(String brCode) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(BRANCH_CODE, brCode);
		edit.commit();
	}
	
	public void setLoginCheckTime(long loginCheckTime) {
		Editor edit = mSharedPreferences.edit();
		edit.putLong(LOGIN_CHECK_TIME, loginCheckTime);
		edit.commit();
	}

	public long getLoginCheckTime() {
		return mSharedPreferences.getLong(LOGIN_CHECK_TIME, 0l);
	}
	
	public void setMaxLoginTime(long maxLoginTime) {
		Editor edit = mSharedPreferences.edit();
		edit.putLong(MAX_LOGIN_TIME, maxLoginTime);
		edit.commit();
	}

	public long getMaxLoginTime() {
		return mSharedPreferences.getLong(MAX_LOGIN_TIME, 0l);
	}
	
	public void setAttTrackTime(long attTrackTime) {
		Editor edit = mSharedPreferences.edit();
		edit.putLong(ATT_TIME, attTrackTime);
		edit.commit();
	}

	public long getAttTrackTime() {
		return mSharedPreferences.getLong(ATT_TIME, 0l);
	}
	
	public void setMaxLoginAtt(int maxLoginAtt) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(MAX_LOGIN_ATTEMPT, maxLoginAtt);
		edit.commit();
	}

	public int getMaxLoginAtt() {
		return mSharedPreferences.getInt(MAX_LOGIN_ATTEMPT, B2CAppConfig.MAX_LOGIN_ATTEMPT);
	}
	
	public void setAlarmTime(long alarmTime) {
		Editor edit = mSharedPreferences.edit();
		edit.putLong(ALARM_TIME, alarmTime);
		edit.commit();
	}

	public long getAlarmTime() {
		return mSharedPreferences.getLong(ALARM_TIME, 0l);
	}
		
	public void setBRCodes(Set<String> brCodes) {
		Editor edit = mSharedPreferences.edit();
		edit.putStringSet(BR_CODES, brCodes);
		edit.commit();
	}
	
	public Set<String> getBRCodes() {
		return mSharedPreferences.getStringSet(BR_CODES, new HashSet<String>());
	}
	
	public void setBRNames(Set<String> brNames) {
		Editor edit = mSharedPreferences.edit();
		edit.putStringSet(BR_NAMES, brNames);
		edit.commit();
	}
	
	public Set<String> getBRNames() {
		return mSharedPreferences.getStringSet(BR_NAMES, new HashSet<String>());
	}

	public void setNewEntryAttendance(int newEntryAttendance) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(NEW_ENTRY_ATTENDANCE, newEntryAttendance);
		edit.commit();
	}

	public int getNewEntryAttendance() {
		return mSharedPreferences.getInt(NEW_ENTRY_ATTENDANCE, -1);
	}

	public void setNewEntryCount(int newEntryCount) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(NEW_ENTRY_COUNT, newEntryCount);
		edit.commit();
	}

	public int getNewEntryCount() {
		return mSharedPreferences.getInt(NEW_ENTRY_COUNT, -1);
	}
	
	public void setNewEntryCountSpancop(int newEntryCount) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(NEW_ENTRY_COUNT_SPANCOP, newEntryCount);
		edit.commit();
	}

	public int getNewEntryCountSpancop() {
		return mSharedPreferences.getInt(NEW_ENTRY_COUNT_SPANCOP, -1);
	}
		
	public void setTCustomerList(Set<String> tCList) {
		Editor edit = mSharedPreferences.edit();
		edit.putStringSet(TEMP_CUSTOMER_LIST, tCList);
		edit.commit();
	}

	public Set<String> getTCustomerList() {
		return mSharedPreferences.getStringSet(TEMP_CUSTOMER_LIST, new HashSet<String>());
	}


public boolean isDayStarted() {
		return mSharedPreferences.getBoolean(DayStarted, false);
	}
	
	public void setIsDayStarted(boolean isDayStarted) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(DayStarted, isDayStarted);
		edit.commit();
	}
	
	public void setPassCode(String passcode) {
		Editor edit = mSharedPreferences.edit();
		edit.putString(PASS_CODE, passcode);
		edit.commit();
	}

	public String getPassCode() {
		return mSharedPreferences.getString(PASS_CODE, null);
	}
		
	public void setStartDateKey(long key) {
		Editor edit = mSharedPreferences.edit();
		edit.putLong(Start_Date_key, key);
		edit.commit();
	}

	public long getStartDateKey() {
		return mSharedPreferences.getLong(Start_Date_key, 1l);
	}


	public void setSelectedCount(long selectedcount) {
		Editor edit = mSharedPreferences.edit();
		edit.putLong(SelectedCount, selectedcount);
		edit.commit();
	}

	public long getSelectedCount() {
		return mSharedPreferences.getLong(SelectedCount, 0);
	}

	public boolean isDaystoppedAuto() {
		return mSharedPreferences.getBoolean(IS_DAYSTOPPED_AUTO, false);
	}

	public void setIsDaystoppedAuto(boolean value) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(IS_DAYSTOPPED_AUTO, value);
		edit.commit();
	}
	public void setCheckedIn(boolean checkedin) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(CHECKEDIN, checkedin);
		edit.commit();
	}

	public boolean getCheckedIn() {
		return mSharedPreferences.getBoolean(CHECKEDIN, false);
	}

	public void setCHECKEDINCMKEY(int checkedincmkey) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(CHECKEDINCMKEY, checkedincmkey);
		edit.commit();
	}

	public int getCHECKEDINCMKEY() {
		return mSharedPreferences.getInt(CHECKEDINCMKEY, 0);
	}

	public void setCHECKEDINEVENTKEY(int checkedineventkey) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(CHECKEDINEVENTKEY, checkedineventkey);
		edit.commit();
	}

	public int getCHECKEDINEVENTKEY() {
		return mSharedPreferences.getInt(CHECKEDINEVENTKEY, 0);
	}

	public void setTodayobjVisit(int todayobj_visit) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(TODAYOBJ_VISIT, todayobj_visit);
		edit.commit();
	}

	public int getTodayobjVisit() {
		return mSharedPreferences.getInt(TODAYOBJ_VISIT, 0);
	}
	public void setTodayobjSale(int todayobj_sale) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(TODAYOBJ_SALE, todayobj_sale);
		edit.commit();
	}

	public int getTodayobjSale() {
		return mSharedPreferences.getInt(TODAYOBJ_SALE, 0);
	}
	public void setTodayobjCollection(int todayobj_collection) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(TODAYOBJ_COLLECTION, todayobj_collection);
		edit.commit();
	}

	public int getTodayobjCollection() {
		return mSharedPreferences.getInt(TODAYOBJ_COLLECTION, 0);
	}


	public void setTodayobjCurrentdate(int todayobj_currentdate) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(TODAYOBJ_CURRENTDATE, todayobj_currentdate);
		edit.commit();
	}

	public int getTodayobjCurrentdate() {
		return mSharedPreferences.getInt(TODAYOBJ_CURRENTDATE, 0);
	}

	public void setCHECKEDINCUSKEY(int checkedincuskey) {
		Editor edit = mSharedPreferences.edit();
		edit.putInt(CHECKEDINCUSKEY, checkedincuskey);
		edit.commit();
	}

	public int getCHECKEDINCUSKEY() {
		return mSharedPreferences.getInt(CHECKEDINCUSKEY, 0);
	}

	public  int getOpeningPoints() {
		return mSharedPreferences.getInt(OPENING_POINTS,0);
	}

	public  void setOpeningPoints(int openingPoints) {
		Editor editor = mSharedPreferences.edit();
		editor.putInt(OPENING_POINTS,openingPoints);
		editor.commit();
	}


	public boolean getFULLYSYNCEDTOSERVER() {
		return mSharedPreferences.getBoolean(FULLYSYNCEDTOSERVER, false);
	}

	public void setFULLYSYNCEDTOSERVER(boolean fullysyncedtoserver) {
		Editor edit = mSharedPreferences.edit();
		edit.putBoolean(FULLYSYNCEDTOSERVER, fullysyncedtoserver);
		edit.commit();
	}


}

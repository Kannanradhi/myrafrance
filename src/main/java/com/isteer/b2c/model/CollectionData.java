package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.isteer.b2c.config.B2CAppConstant;
@Entity (tableName = "b2c_collections")
public class CollectionData {


	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "pay_coll_key")

	private int pay_coll_key;
    @ColumnInfo(name = "customer_name")
    private String customer_name;
	@ColumnInfo(name = "sc_ledger_key")
	private String sc_ledger_key = "-1";
	@ColumnInfo(name = "cus_key")
	private String cus_key;
	@ColumnInfo(name = "amount")
	private String amount = "0.00";
	@ColumnInfo(name = "trans_date")
	private String trans_date = "";
	@ColumnInfo(name = "entered_date_time")
	private String entered_date_time = "";
	@ColumnInfo(name = "payment_mode")
	private String payment_mode = B2CAppConstant.PAY_CASH;
	@ColumnInfo(name = "receipt_no")
	private String receipt_no = "";
	@ColumnInfo(name = "cheque_no")
	private String cheque_no = "";
	@ColumnInfo(name = "cheque_date")
	private String cheque_date = "";
	@ColumnInfo(name = "bank")
	private String bank = "";
	@ColumnInfo(name = "contact_key")
	private String contact_key;
	@ColumnInfo(name = "remarks")
	private String remarks = "";
	@ColumnInfo(name = "is_synced_to_server")
	private int is_synced_to_server = 1;
	@Ignore
	private String balance_amount = "0.0";

	@Ignore
	private int PAYMENT_MODE ;
	@Ignore
	private String  PAYMENT_TYPE ;

	public int getIs_synced_to_server() {
		return is_synced_to_server;
	}

	public void setIs_synced_to_server(int is_synced_to_server) {
		this.is_synced_to_server = is_synced_to_server;
	}

	public String getCus_key() {
		return cus_key;
	}
	public void setCus_key(String cus_key) {
		this.cus_key = cus_key;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTrans_date() {
		return trans_date;
	}
	public void setTrans_date(String trans_date) {
		this.trans_date = trans_date;
	}
	public String getPayment_mode() {
		return payment_mode;
	}
	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}
	public String getReceipt_no() {
		return receipt_no;
	}
	public void setReceipt_no(String receipt_no) {
		this.receipt_no = receipt_no;
	}
	public String getCheque_no() {
		return cheque_no;
	}
	public void setCheque_no(String cheque_no) {
		this.cheque_no = cheque_no;
	}
	public String getCheque_date() {
		return cheque_date;
	}
	public void setCheque_date(String cheque_date) {
		this.cheque_date = cheque_date;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getContact_key() {
		return contact_key;
	}
	public void setContact_key(String contact_key) {
		this.contact_key = contact_key;
	}
	public String getEntered_date_time() {
		return entered_date_time;
	}
	public void setEntered_date_time(String entered_date_time) {
		this.entered_date_time = entered_date_time;
	}
	public int getPay_coll_key() {
		return pay_coll_key;
	}
	public void setPay_coll_key(int pay_coll_key) {
		this.pay_coll_key = pay_coll_key;
	}
	public String getSc_ledger_key() {
		return sc_ledger_key;
	}
	public void setSc_ledger_key(String sc_ledger_key) {
		this.sc_ledger_key = sc_ledger_key;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getBalance_amount() {
		return balance_amount;
	}

	public void setBalance_amount(String balance_amount) {
		this.balance_amount = balance_amount;
	}

	public int getPAYMENT_MODE() {
		return PAYMENT_MODE;
	}

	public void setPAYMENT_MODE(int PAYMENT_MODE) {
		this.PAYMENT_MODE = PAYMENT_MODE;
	}

	public String getPAYMENT_TYPE() {
		return PAYMENT_TYPE;
	}

	public void setPAYMENT_TYPE(String PAYMENT_TYPE) {
		this.PAYMENT_TYPE = PAYMENT_TYPE;
	}
}

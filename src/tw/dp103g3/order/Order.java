package tw.dp103g3.order;

import org.eclipse.jdt.annotation.Nullable;

public class Order {
	private int order_id;
	private int del_id;
	private int shop_id;
	private int mem_id;
	private int pay_id;
	private int order_status;
	private int sp_id;
	private String order_time;
	private String order_ideal;
	private String order_delivery;
	private int adrs_id;
	private String order_name;
	private String order_phone;
	private int order_ttprice;
	private int order_area;
	
	public Order(int shop_id, int mem_id, int del_id, int pay_id, int sp_id , @Nullable String order_ideal,
			@Nullable String order_delivery, int adrs_id, String order_name, String order_phone, int order_ttprice) {
		super();
		this.del_id = del_id;
		this.shop_id = shop_id;
		this.mem_id = mem_id;
		this.pay_id = pay_id;
		this.sp_id = sp_id;
		this.order_ideal = order_ideal;
		this.order_delivery = order_delivery;
		this.adrs_id = adrs_id;
		this.order_name = order_name;
		this.order_phone = order_phone;
		this.order_ttprice = order_ttprice;
	}
	
	public Order(int order_id, int shop_id, int mem_id,int del_id, int pay_id, int sp_id, @Nullable String order_ideal,
			String order_time ,@Nullable String order_delivery, int adrs_id, String order_name, String order_phone, int order_ttprice, int order_area, int order_status) {
		super();
		this.order_id = order_id;
		this.del_id = del_id;
		this.shop_id = shop_id;
		this.mem_id = mem_id;
		this.pay_id = pay_id;
		this.sp_id = sp_id;
		this.order_ideal = order_ideal;
		this.order_time = order_time;
		this.order_delivery = order_delivery;
		this.adrs_id = adrs_id;
		this.order_name = order_name;
		this.order_phone = order_phone;
		this.order_ttprice = order_ttprice;
		this.order_area = order_area;
		this.order_status = order_status;
	}

	public int getOrder_id() {
		return order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}

	public int getDel_id() {
		return del_id;
	}

	public void setDel_id(int del_id) {
		this.del_id = del_id;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public int getMem_id() {
		return mem_id;
	}

	public void setMem_id(int mem_id) {
		this.mem_id = mem_id;
	}

	public int getPay_id() {
		return pay_id;
	}

	public void setPay_id(int pay_id) {
		this.pay_id = pay_id;
	}

	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public int getSp_id() {
		return sp_id;
	}

	public void setSp_id(int sp_id) {
		this.sp_id = sp_id;
	}

	public String getOrder_time() {
		return order_time;
	}

	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}

	public String getOrder_ideal() {
		return order_ideal;
	}

	public void setOrder_ideal(String order_ideal) {
		this.order_ideal = order_ideal;
	}

	public String getOrder_delivery() {
		return order_delivery;
	}

	public void setOrder_delivery(String order_delivery) {
		this.order_delivery = order_delivery;
	}

	public int getAdrs_id() {
		return adrs_id;
	}

	public void setAdrs_id(int adrs_id) {
		this.adrs_id = adrs_id;
	}

	public String getOrder_name() {
		return order_name;
	}

	public void setOrder_name(String order_name) {
		this.order_name = order_name;
	}

	public String getOrder_phone() {
		return order_phone;
	}

	public void setOrder_phone(String order_phone) {
		this.order_phone = order_phone;
	}

	public int getOrder_ttprice() {
		return order_ttprice;
	}

	public void setOrder_ttprice(int order_ttprice) {
		this.order_ttprice = order_ttprice;
	}

	public int getOrder_area() {
		return order_area;
	}

	public void setOrder_area(int order_area) {
		this.order_area = order_area;
	}
	
	
	
	


}
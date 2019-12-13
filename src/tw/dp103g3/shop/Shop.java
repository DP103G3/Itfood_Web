package tw.dp103g3.shop;

import java.util.Date;

public class Shop {
	private int id;
	private String username;
	private String password;
	private int area;
	private String address;
	private double latitude;
	private double longitude;
	private byte state;
	private String info;
	private Date suspendtime;
	private String email;
	private int tt_score;
	private int ttrate;
	
	public Shop(int id, String username, String password, int area, String address, double latitude, double longitude,
			byte state, String info, Date suspendtime, String email, int tt_score, int ttrate) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.area = area;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.state = state;
		this.info = info;
		this.suspendtime = suspendtime;
		this.email = email;
		this.tt_score = tt_score;
		this.ttrate = ttrate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Date getSuspendtime() {
		return suspendtime;
	}

	public void setSuspendtime(Date suspendtime) {
		this.suspendtime = suspendtime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getTt_score() {
		return tt_score;
	}

	public void setTt_score(int tt_score) {
		this.tt_score = tt_score;
	}

	public int getTtrate() {
		return ttrate;
	}

	public void setTtrate(int ttrate) {
		this.ttrate = ttrate;
	}
	
	
}

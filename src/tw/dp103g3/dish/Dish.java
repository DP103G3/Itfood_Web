package tw.dp103g3.dish;

public class Dish {
	private int id;
	private String name;
	private String info;
	private byte state;
	private int shop_id;
	private int price;
	
	public Dish(int id, String name, String info, byte state, int shop_id, int price) {
		this.id = id;
		this.name = name;
		this.info = info;
		this.state = state;
		this.shop_id = shop_id;
		this.price = price;
	}
	
	public Dish(int id, String name, String info, int shop_id, byte state, int price) {
		this.id = id;
		this.name = name;
		this.info = info;
		this.shop_id = shop_id;
		this.state = state;
		this.price = price;
	}
	
	public Dish(int id, String name, String info, int shop_id, int price) {
		this.id = id;
		this.name = name;
		this.info = info;
		this.shop_id = shop_id;
		this.price = price;
	}
	
	public Dish(int id, byte state) {
		super();
		this.id = id;
		this.state = state;
	}
	
	public Dish(int id, String name, String info) {
		this(id, name, info, 0, 0);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}

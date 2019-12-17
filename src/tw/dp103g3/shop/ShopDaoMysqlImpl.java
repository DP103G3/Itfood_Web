package tw.dp103g3.shop;

import static tw.dp103g3.main.Common.PASSWORD;
import static tw.dp103g3.main.Common.URL;
import static tw.dp103g3.main.Common.USER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ShopDaoMysqlImpl implements ShopDao {
	
	public ShopDaoMysqlImpl() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Shop shop, byte[] image) {
		int count = 0;
		String sql = "";
		if (image != null) {
			sql = "INSERT INTO shop (shop_email, shop_password, shop_name, shop_tax, "
					+ "shop_address, shop_latitude, shop_longitude, shop_area, shop_state, "
					+ "shop_info, shop_ttscore, shop_ttrate, shop_image) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		} else {
			sql = "INSERT INTO shop (shop_email, shop_password, shop_name, shop_tax, "
					+ "shop_address, shop_latitude, shop_longitude, shop_area, shop_state, "
					+ "shop_info, shop_ttscore, shop_ttrate) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		}
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, shop.getEmail());
			ps.setString(2, shop.getPassword());
			ps.setString(3, shop.getName());
			ps.setString(4, shop.getTax());
			ps.setString(5, shop.getAddress());
			ps.setDouble(6, shop.getLatitude());
			ps.setDouble(7, shop.getLongitude());
			ps.setInt(8, shop.getArea());
			ps.setInt(9, shop.getState());
			ps.setString(10, shop.getInfo());
			ps.setInt(11, shop.getTt_score());
			ps.setInt(12, shop.getTtrate());
			if (image != null) {
				ps.setBytes(13, image);
			}
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public int update(Shop shop, byte[] image) {
		int count = 0;
		String sql = "";
		if (image != null) {
			sql = "UPDATE shop SET shop_email = ?, shop_password = ?, shop_name = ?, "
					+ "shop_tax = ?, shop_address = ?, shop_latitude = ?, shop_longitude = ?, "
					+ "shop_area = ?, shop_state = ?, shop_info = ?, shop_suspendtime = ?, "
					+ "shop_ttscore = ?, shop_ttrate = ?, shop_image = ? WHERE shop_id = ?;";
		} else {
			sql = "UPDATE shop SET shop_email = ?, shop_password = ?, shop_name = ?, "
					+ "shop_tax = ?, shop_address = ?, shop_latitude = ?, shop_longitude = ?, "
					+ "shop_area = ?, shop_state = ?, shop_info = ?, shop_suspendtime = ?, "
					+ "shop_ttscore = ?, shop_ttrate = ? WHERE shop_id = ?;";
		}
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, shop.getEmail());
			ps.setString(2, shop.getPassword());
			ps.setString(3, shop.getName());
			ps.setString(4, shop.getTax());
			ps.setString(5, shop.getAddress());
			ps.setDouble(6, shop.getLatitude());
			ps.setDouble(7, shop.getLongitude());
			ps.setInt(8, shop.getArea());
			ps.setByte(9, shop.getState());
			ps.setString(10, shop.getInfo());
			ps.setTimestamp(11, shop.getSuspendtime() == null ? 
					null : new Timestamp(shop.getSuspendtime().getTime()));
			ps.setInt(12, shop.getTt_score());
			ps.setInt(13, shop.getTtrate());
			if (image != null) {
				ps.setBytes(14, image);
				ps.setInt(15, shop.getId());
			} else {
				ps.setInt(14, shop.getId());
			}
			
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * getAll is for backside
	 * @return all columns in shop table
	 */
	@Override
	public List<Shop> getAll() {
		List<Shop> shops = new ArrayList<Shop>();
		String sql = "SELECT shop_id, shop_email, shop_password, shop_name, shop_tax, shop_address, "
				+ "shop_latitude, shop_longitude, shop_area, shop_state, shop_info, shop_jointime, "
				+ "shop_suspendtime, shop_ttscore, shop_ttrate FROM shop;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String email = rs.getString(2);
				String password = rs.getString(3);
				String name = rs.getString(4);
				String tax = rs.getString(5);
				String address = rs.getString(6);
				double latitude = rs.getDouble(7);
				double longitude = rs.getDouble(8);
				int area = rs.getInt(9);
				byte state = rs.getByte(10);
				String info = rs.getString(11);
				Timestamp jointime = rs.getTimestamp(12);
				Timestamp suspendtime = rs.getTimestamp(13);
				int ttscore = rs.getInt(14);
				int ttrate = rs.getInt(15);
				Shop shop = new Shop(id, email, password, name, tax, address, latitude, longitude, area, 
						state, info, jointime, suspendtime, ttscore, ttrate);
				shops.add(shop);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return shops;
	}
	
	/**
	 * getAllShow is for normal user
	 * @return all necessary columns for show
	 */
	@Override
	public List<Shop> getAllShow() { //for user
		List<Shop> shops = new ArrayList<Shop>();
		String sql = "SELECT shop_id, shop_name, shop_address, shop_latitude, shop_longitude, "
				+ "shop_area, shop_state, shop_info, shop_ttscore, shop_ttrate FROM shop "
				+ "WHERE shop_state != 0;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String address = rs.getString(3);
				double latitude = rs.getDouble(4);
				double longitude = rs.getDouble(5);
				int area = rs.getInt(6);
				byte state = rs.getByte(7);
				String info = rs.getString(8);
				int ttscore = rs.getInt(9);
				int ttrate = rs.getInt(10);
				Shop shop = new Shop(id, name, address, latitude, longitude, area, state, info, ttscore, ttrate);
				shops.add(shop);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return shops;
	}

	@Override
	public byte[] getImage(int id) {
		byte[] image = null;
		String sql = "SELECT shop_image FROM shop WHERE id = ?;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				image = rs.getBytes(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return image;
	}

}

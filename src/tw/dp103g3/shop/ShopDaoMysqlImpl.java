package tw.dp103g3.shop;

import static tw.dp103g3.main.Common.CLASS_NAME;
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
import java.util.Date;
import java.util.List;

import tw.dp103g3.member.Member;

public class ShopDaoMysqlImpl implements ShopDao {

	public ShopDaoMysqlImpl() {
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Shop shop, byte[] image) {
		int count = 0;
		String selectEmailSql = "SELECT * FROM `shop` WHERE shop_email = ?;";
		String sql = null;
		if (image != null) {
			sql = "INSERT INTO `shop` (shop_email, shop_password, shop_name, shop_phone, shop_tax, "
					+ "shop_address, shop_latitude, shop_longitude, shop_area, shop_state, "
					+ "shop_info, shop_ttscore, shop_ttrate, shop_image) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		} else {
			sql = "INSERT INTO `shop` (shop_email, shop_password, shop_name, shop_phone, shop_tax, "
					+ "shop_address, shop_latitude, shop_longitude, shop_area, shop_state, "
					+ "shop_info, shop_ttscore, shop_ttrate) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		}
		Connection connection = null;
		PreparedStatement selectEmailPs = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(false);
			selectEmailPs = connection.prepareStatement(selectEmailSql);
			ps = connection.prepareStatement(sql);
			
			selectEmailPs.setString(1, shop.getEmail());
			ResultSet selectEmailRs = selectEmailPs.executeQuery();
			if (!selectEmailRs.next()) {
				ps.setString(1, shop.getEmail());
				ps.setString(2, shop.getPassword());
				ps.setString(3, shop.getName());
				ps.setString(4, shop.getPhone());
				ps.setString(5, shop.getTax());
				ps.setString(6, shop.getAddress());
				ps.setDouble(7, shop.getLatitude());
				ps.setDouble(8, shop.getLongitude());
				ps.setInt(9, shop.getArea());
				ps.setInt(10, shop.getState());
				ps.setString(11, shop.getInfo());
				ps.setInt(12, shop.getTtscore());
				ps.setInt(13, shop.getTtrate());
				if (image != null) {
					ps.setBytes(14, image);
				}
				count = ps.executeUpdate();
				if (count != 0) {
					connection.commit();
				} else {
					connection.rollback();
				}
			} else {
				count = -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	@Override
	public int update(Shop shop, byte[] image) {
		int count = 0;
		String sql = "";
		if (image != null) {
			sql = "UPDATE `shop` SET shop_email = ?, shop_password = ?, shop_name = ?, shop_phone = ?, "
					+ "shop_tax = ?, shop_address = ?, shop_latitude = ?, shop_longitude = ?, "
					+ "shop_area = ?, shop_state = ?, shop_info = ?, shop_suspendtime = ?, "
					+ "shop_ttscore = ?, shop_ttrate = ?, shop_image = ? WHERE shop_id = ?;";
		} else {
			sql = "UPDATE `shop` SET shop_email = ?, shop_password = ?, shop_name = ?, shop_phone = ?, "
					+ "shop_tax = ?, shop_address = ?, shop_latitude = ?, shop_longitude = ?, "
					+ "shop_area = ?, shop_state = ?, shop_info = ?, shop_suspendtime = ?, "
					+ "shop_ttscore = ?, shop_ttrate = ? WHERE shop_id = ?;";
		}
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, shop.getEmail());
			ps.setString(2, shop.getPassword());
			ps.setString(3, shop.getName());
			ps.setString(4, shop.getPhone());
			ps.setString(5, shop.getTax());
			ps.setString(6, shop.getAddress());
			ps.setDouble(7, shop.getLatitude());
			ps.setDouble(8, shop.getLongitude());
			ps.setInt(9, shop.getArea());
			ps.setByte(10, shop.getState());
			ps.setString(11, shop.getInfo());
			ps.setTimestamp(12, shop.getSuspendtime() == null ? null : new Timestamp(shop.getSuspendtime().getTime()));
			ps.setInt(13, shop.getTtscore());
			ps.setInt(14, shop.getTtrate());
			if (image != null) {
				ps.setBytes(15, image);
				ps.setInt(16, shop.getId());
			} else {
				ps.setInt(15, shop.getId());
			}

			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * getAll is for backside
	 * 
	 * @return all columns in shop table
	 */
	@Override
	public List<Shop> getAll() {
		List<Shop> shops = new ArrayList<Shop>();
		String sql = "SELECT shop_id, shop_email, shop_password, shop_name, shop_phone, shop_tax, shop_address, "
				+ "shop_latitude, shop_longitude, shop_area, shop_state, shop_info, shop_jointime, "
				+ "shop_suspendtime, shop_ttscore, shop_ttrate FROM `shop` ORDER BY shop_jointime DESC;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String email = rs.getString(2);
				String password = rs.getString(3);
				String name = rs.getString(4);
				String phone = rs.getString(5);
				String tax = rs.getString(6);
				String address = rs.getString(7);
				double latitude = rs.getDouble(8);
				double longitude = rs.getDouble(9);
				int area = rs.getInt(10);
				byte state = rs.getByte(11);
				String info = rs.getString(12);
				Date jointime = rs.getTimestamp(13);
				Date suspendtime = rs.getTimestamp(14);
				int ttscore = rs.getInt(15);
				int ttrate = rs.getInt(16);
				Shop shop = new Shop(id, email, password, name, phone, tax, address, latitude, longitude, area, state, info,
						jointime, suspendtime, ttscore, ttrate);
				shops.add(shop);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return shops;
	}

	/**
	 * getAllShow is for normal user
	 * 
	 * @return all necessary columns for show
	 */
	@Override
	public List<Shop> getAllShow(int memId) { // for user
		List<Shop> shops = new ArrayList<Shop>();
		List<String> types = new ArrayList<String>();
		String sql = "SELECT `shop`.shop_id, shop_name, shop_address, shop_latitude, shop_longitude, shop_area, "
				+ "shop_state, shop_info, shop_jointime, shop_ttscore, shop_ttrate, type_name, mem_id FROM `shop` "
				+ "LEFT JOIN `shop_type` ON `shop_type`.shop_id = `shop`.shop_id "
				+ "LEFT JOIN `type` ON `type`.type_id = `shop_type`.type_id "
				+ "LEFT JOIN `favorite` ON `favorite`.shop_id = `shop`.shop_id WHERE shop_state != 0 "
				+ "ORDER BY `shop`.shop_id;";
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
				Date jointime = rs.getTimestamp(9);
				int ttscore = rs.getInt(10);
				int ttrate = rs.getInt(11);
				String type = rs.getString(12);
				Shop shop = new Shop(id, name, address, latitude, longitude, area, state, info, jointime, ttscore, ttrate);
				if (!shops.remove(shop)) {
					types = new ArrayList<String>();
				}
				types.add(type);
				shop.setTypes(types);
				shop.setFavorite(memId != 0 ? rs.getInt(13) == memId : false);
				shops.add(shop);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return shops;
	}

	@Override
	public Shop getAccount(int id) {
		String sql = "SELECT shop_id, shop_state FROM `shop` WHERE shop_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		Shop shop = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
			byte state = rs.getByte(2);
			shop = new Shop(id, state);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					// When a Statement object is closed,
					// its current ResultSet object is also closed
					ps.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shop;
	}


	@Override
	public int saveAccount(Shop shop) {
		int count = 0;
		String sql = "UPDATE `shop` SET shop_state = ?  WHERE shop_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setByte(1, shop.getState());
			ps.setInt(2, shop.getId());
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					// When a Statement object is closed,
					// its current ResultSet object is also closed
					ps.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	@Override
	public byte[] getImage(int id) {
		byte[] image = null;
		String sql = "SELECT shop_image FROM `shop` WHERE shop_id = ?;";
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

	@Override
	public Shop getShopById(int id) {
		Shop shop = null;
		List<String> types = new ArrayList<String>();
		String sql = "SELECT shop_name, shop_address, shop_latitude, shop_longitude, shop_area, shop_state, " + 
				"shop_info, shop_jointime, shop_ttscore, shop_ttrate, type_name FROM `shop` " + 
				"JOIN `shop_type` ON `shop_type`.shop_id = `shop`.shop_id " + 
				"JOIN `type` ON `type`.type_id = `shop_type`.type_id " + 
				"WHERE shop_state != 0 AND shop.shop_id = ? ;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				String address = rs.getString(2);
				double latitude = rs.getDouble(3);
				double longitude = rs.getDouble(4);
				int area = rs.getInt(5);
				byte state = rs.getByte(6);
				String info = rs.getString(7);
				Date jointime = rs.getTimestamp(8);
				int ttscore = rs.getInt(9);
				int ttrate = rs.getInt(10);
				String type = rs.getString(11);
				shop = new Shop(id, name, address, latitude, longitude, area, state, info, jointime, ttscore, ttrate);
				types.add(type);
				shop.setTypes(types);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return shop;
	}
	
	/**
	 * getShopAllById is for backside  
	 * 
	 * @return all necessary columns for show
	 */
	
	@Override
	public Shop getShopAllById(int id) {
		String sql = "SELECT shop_id, shop_email, shop_name, shop_phone, shop_tax, shop_address, shop_area, shop_state, shop_info, shop_jointime FROM `shop` WHERE shop_id = ?;";
		Connection conn = null;
		PreparedStatement ps = null;
		Shop shop = null;
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String email = rs.getString(2);
				String name = rs.getString(3);
				String phone = rs.getString(4);
				String tax = rs.getString(5);
				String address = rs.getString(6);
				int area = rs.getInt(7);
				byte state = rs.getByte(8);
				String info = rs.getString(9);
				Date jointime = rs.getTimestamp(10);
				shop = new Shop(id, email, name, phone, tax, address, area, state, info, jointime);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shop;
	}
	
	@Override
	public int login(String email, String password) {
		boolean isValid = false;
		int shopId = 0;
		String sql = "SELECT shop_id, shop_password FROM `shop` WHERE shop_email = ?;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				isValid = rs.getString(2).equals(password);
			}
			if (isValid) {
				shopId = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shopId;
	}
}

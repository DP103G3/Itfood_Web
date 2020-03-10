package tw.dp103g3.favorite;

import static tw.dp103g3.main.Common.CLASS_NAME;
import static tw.dp103g3.main.Common.PASSWORD;
import static tw.dp103g3.main.Common.URL;
import static tw.dp103g3.main.Common.USER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tw.dp103g3.shop.Shop;


public class FavoriteDaoMySqlImpl implements FavoriteDao {

	public FavoriteDaoMySqlImpl() {
		super();
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(int mem_id, int shop_id) {
		int count = 0;
		String sql = "INSERT INTO favorite " + "(mem_id, shop_id) " + "VALUES(?,?);";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(true);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, mem_id);
			ps.setInt(2, shop_id);
			count = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
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
	public int delete(int mem_id, int shop_id) {
		int count = 0;
		String sql = "DELETE FROM favorite WHERE mem_id = ? AND shop_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(true);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, mem_id);
			ps.setInt(2, shop_id);
			count = ps.executeUpdate();
			} catch(SQLException e){
				e.printStackTrace();
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
	public List<Shop> findByMemberId(int mem_id) {
		String sql = "SELECT `shop`.shop_id, shop_name, shop_address, shop_latitude, shop_longitude, "
				+ "shop_area, shop_state, shop_info, shop_jointime, shop_ttscore, shop_ttrate, type_name "
				+ "FROM `favorite` LEFT JOIN `shop` ON `shop`.shop_id = `favorite`.shop_id "
				+ "LEFT JOIN `shop_type` ON `shop_type`.shop_id = `shop`.shop_id "
				+ "LEFT JOIN `type` ON `type`.type_id = `shop_type`.type_id "
				+ "WHERE mem_id = ? AND shop_state != 0 ORDER BY `shop`.shop_id;";
		List<Shop> shops = new ArrayList<Shop>();
		List<String> types = new ArrayList<String>();
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = conn.prepareStatement(sql);) {
			ps.setInt(1, mem_id);
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
				shop.setFavorite(true);
				shops.add(shop);
			}
			return shops;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return shops;
	}

}

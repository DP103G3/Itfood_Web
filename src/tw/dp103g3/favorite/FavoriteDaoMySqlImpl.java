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
import java.util.List;


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
	public List <Favorite> findByMemberId(int mem_id) {
		String sql = "SELECT mem_id, shop_id FROM favorite WHERE mem_id = ?;";
		Connection conn = null;
		PreparedStatement ps = null;
		List <Favorite> favorites = new ArrayList<Favorite>();
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, mem_id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				int memId = rs.getInt(1);
				int shopId = rs.getInt(2);
				Favorite favorite = new Favorite(memId, shopId);
				favorites.add(favorite);
			}
			return favorites;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return favorites;
	}

}

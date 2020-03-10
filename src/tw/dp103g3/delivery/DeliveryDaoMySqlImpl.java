package tw.dp103g3.delivery;

import static tw.dp103g3.main.Common.CLASS_NAME;
import static tw.dp103g3.main.Common.PASSWORD;
import static tw.dp103g3.main.Common.URL;
import static tw.dp103g3.main.Common.USER;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeliveryDaoMySqlImpl implements DeliveryDao {
	
	public DeliveryDaoMySqlImpl() {
		super();
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Delivery delivery) {
		int count = 0;
		String sql = "INSERT INTO `delivery` (del_name, del_password, del_email, del_phone, del_identityid, del_state) VALUES(?, ?, ?, ?, ?, ?);";
		String selectEmailSql = "SELECT * FROM `delivery` WHERE del_email = ?;";
		String selectIdentityIdSql = "SELECT * FROM `delivery` WHERE del_identityid = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		PreparedStatement selectEmailPs = null;
		PreparedStatement selectIdentityIdPs = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			selectEmailPs = connection.prepareStatement(selectEmailSql);
			selectIdentityIdPs = connection.prepareStatement(selectIdentityIdSql);
			
			selectEmailPs.setString(1, delivery.getDelEmail());
			ResultSet selectEmailRs = selectEmailPs.executeQuery();
			if (selectEmailRs.next()) {
				return -1;
			}
			selectIdentityIdPs.setString(1, delivery.getDelIdentityid());
			ResultSet selectIdentityIdRs = selectIdentityIdPs.executeQuery();
			if (selectIdentityIdRs.next()) {
				return -2;
			} else {
				ps.setString(1, delivery.getDelName());
				ps.setString(2, delivery.getDelPassword());
				ps.setString(3, delivery.getDelEmail());
				ps.setString(4, delivery.getDelPhone());
				ps.setString(5, delivery.getDelIdentityid());
				ps.setInt(6, delivery.getDelState());
				count = ps.executeUpdate();
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
		return count;
	}

	@Override
	public int update(Delivery delivery) {
		int count = 0;
		String sql = "UPDATE `delivery` SET del_name = ?, del_password = ?, del_email = ?, del_phone = ?,del_state = ?  WHERE del_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setString(1, delivery.getDelName());
			ps.setString(2, delivery.getDelPassword());
			ps.setString(3, delivery.getDelEmail());
			ps.setString(4, delivery.getDelPhone());
			ps.setInt(5, delivery.getDelState());
			ps.setInt(6, delivery.getDelId());
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
	public Delivery getDataById(int del_id) {
		String sql = "SELECT del_id, del_name, del_password, del_email, del_identityid, del_phone FROM `delivery` WHERE del_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		Delivery delivery = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, del_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String del_name = rs.getString(2);
				String del_password = rs.getString(3);
				String del_email = rs.getString(4);
				String del_identityid = rs.getString(5);
				String del_phone = rs.getString(6);
				delivery = new Delivery(del_id, del_name, del_password, del_email, del_identityid, del_phone);
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
		return delivery;
	}

	@Override
	public Delivery getAccount(int del_id) {
		String sql = "SELECT del_id, del_state FROM `delivery` WHERE del_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		Delivery delivery = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, del_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int del_state = rs.getInt(2);
				delivery = new Delivery(del_id, del_state);
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
		return delivery;
	}

	@Override
	public int saveAccount(Delivery delivery) {
		int count = 0;
		String sql = "UPDATE `delivery` SET del_state = ?  WHERE del_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, delivery.getDelState());
			ps.setInt(2, delivery.getDelId());
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
	public Delivery findById(int del_id) {
		String sql = "SELECT del_id, del_name, del_password, del_email, del_identityid, del_phone, del_area, del_jointime, del_leavetime, del_state FROM `delivery` WHERE del_id = ?;";
		Connection conn = null;
		PreparedStatement ps = null;
		Delivery delivery = null;
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, del_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String del_name = rs.getString(2);
				String del_password = rs.getString(3);
				String del_email = rs.getString(4);
				String del_identityid = rs.getString(5);
				String del_phone = rs.getString(6);
				int del_area = rs.getInt(7);
				Date del_jointime = rs.getTimestamp(8);
				Date del_leavetime = rs.getTimestamp(9);
				int del_state = rs.getInt(10);
				delivery = new Delivery(del_id, del_name, del_password, del_email, del_identityid, del_phone, del_area, del_jointime, del_leavetime, del_state);

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
		return delivery;
	}

	@Override
	public List<Delivery> getAll() {
		String sql = "SELECT del_id, del_name, del_password, del_email, del_identityid, del_phone, del_area, del_jointime, del_leavetime, del_suspendtime, del_state FROM `delivery` ORDER BY del_jointime DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Delivery> deliveryList = new ArrayList<Delivery>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int del_id = rs.getInt(1);
				String del_name = rs.getString(2);
				String del_password = rs.getString(3);
				String del_email = rs.getString(4);
				String del_identityid = rs.getString(5);
				String del_phone = rs.getString(6);
				int del_area = rs.getInt(7);
				Date del_jointime = rs.getTimestamp(8);
				Date del_leavetime = rs.getTimestamp(9);
				Date del_suspendtime = rs.getTimestamp(10);
				int del_state = rs.getInt(11);
				Delivery delivery = new Delivery(del_id, del_name, del_password, del_email, del_identityid, del_phone, del_area, del_jointime, del_leavetime, del_suspendtime, del_state);
				deliveryList.add(delivery);
			}
			
			return deliveryList;
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
		System.out.println("output getAll return sql:" + deliveryList);
		
		return deliveryList;
	}

	@Override
	public Map<String, Integer> login(String email, String password) {
		Map<String, Integer> outcome = new HashMap<String, Integer>();
		int ERROR = 0;
		int OK = 1;
		int WRONG_PASSWORD = 2;
		int SUSPENDED = 3;
		int NOT_FOUND = 4;
		outcome.put("result", ERROR);
		String sql = "SELECT del_id, del_area, del_state, del_password "
				+ " FROM `delivery` WHERE del_email = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				outcome.put("result", NOT_FOUND);
			} else {
				rs.beforeFirst();
			}
			
			while (rs.next()) {
				int del_id = rs.getInt(1);
				int del_area = rs.getInt(2);
				int del_state = rs.getInt(3);
				String del_password = rs.getString(4);
				outcome.put("del_id", del_id);
				outcome.put("del_area", del_area);
				if (del_password.equals(password)) {
					if (del_state == 1) {
						outcome.put("result", OK);
					} else {
						outcome.put("result", SUSPENDED);
					}
				} else {
					outcome.put("result", WRONG_PASSWORD);
				}
			}
			
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
		return outcome;
	}

}
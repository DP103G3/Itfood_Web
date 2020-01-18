package tw.dp103g3.order;

import static tw.dp103g3.main.Common.PASSWORD;
import static tw.dp103g3.main.Common.URL;
import static tw.dp103g3.main.Common.USER;
import static tw.dp103g3.main.Common.CLASS_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class OrderDaoMySqlImpl implements OrderDao {

	public OrderDaoMySqlImpl() {
		super();
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Order order) {
		int count = 0;
		String sql = "INSERT INTO `order` (shop_id, mem_id, del_id, pay_id, sp_id, order_ideal, "
				+ "order_delivery, adrs_id, order_name, order_phone, order_ttprice, order_type, order_state) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, order.getShop_id());
			ps.setInt(2, order.getMem_id());
			ps.setInt(3, order.getDel_id() != 0 ? order.getDel_id() : null);
			ps.setInt(4, order.getPay_id());
			ps.setInt(5, order.getSp_id() != 0 ? order.getSp_id() : null);
			ps.setTimestamp(6, new Timestamp(order.getOrder_ideal().getTime()));
			ps.setTimestamp(7, order.getOrder_delivery() != null ? 
					new Timestamp(order.getOrder_delivery().getTime()) : null);
			ps.setInt(8, order.getAdrs_id());
			ps.setString(9, order.getOrder_name());
			ps.setString(10, order.getOrder_phone());
			ps.setInt(11, order.getOrder_ttprice());
			ps.setInt(12, order.getOrder_type());
			ps.setInt(13, order.getOrder_state());

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
	public int update(Order order) {
		int count = 0;
		String sql = "UPDATE `order` SET shop_id = ?, mem_id = ?, del_id = ?, pay_id = ?, sp_id = ?,"
				+ " order_ideal = ?, order_delivery = ?, adrs_id = ?, order_name = ?, order_phone = ?, "
				+ "order_ttprice = ?, order_state = ?, order_type = ? WHERE order_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, order.getShop_id());
			ps.setInt(2, order.getMem_id());
			ps.setInt(3, order.getDel_id() != 0 ? order.getDel_id() : 0);
			ps.setInt(4, order.getPay_id());
			ps.setInt(5, order.getSp_id() != 0 ? order.getSp_id() : 0);
			ps.setTimestamp(6, new Timestamp(order.getOrder_ideal().getTime()));
			ps.setTimestamp(7, order.getOrder_delivery() != null ? 
					new Timestamp(order.getOrder_delivery().getTime()) : null);
			ps.setInt(8, order.getAdrs_id());
			ps.setString(9, order.getOrder_name());
			ps.setString(10, order.getOrder_phone());
			ps.setInt(11, order.getOrder_ttprice());
			ps.setInt(12, order.getOrder_state());
			ps.setInt(13, order.getOrder_type());
			ps.setInt(14, order.getOrder_id());
			

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
	public List<Order> findByOrderId(int order_id) {
		String sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_state, sp_id, order_time, order_ideal, order_delivery, "
				+ "adrs_id, order_name, order_phone, order_ttpice, order_area, order_type  "
				+ "FROM `order` WHERE order_id = ? ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, order_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				int delId = rs.getInt(4);
				int payId = rs.getInt(5);
				int orderState = rs.getInt(6);
				int spId = rs.getInt(7);
				Date orderTime = rs.getTimestamp(8);
				Date orderIdeal = rs.getTimestamp(9);
				Date orderDelivery = rs.getTimestamp(10);
				int adrsId = rs.getInt(11);
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				int order_type = rs.getInt(16);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderState, order_type);
				orderList.add(order);
			}
			return orderList;
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
		return orderList;
	}

	@Override
	public List<Order> findByCase(int id, String type, int state) {
		String sqlPart = "";
		switch (type) {
			case "member":
				sqlPart = "mem_id";
				break;
			case "shop":
				sqlPart = "shop_id";
				break;
			case "delivery":
				sqlPart = "del_id";
				break;
		}
		String sql = null;
		sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_state, sp_id, order_time, "
				+ "order_ideal, order_delivery, adrs_id, order_name, order_phone, order_ttprice, order_area, order_type "
				+ "FROM `order` WHERE " + sqlPart + " = ? AND order_state = ? ORDER BY order_time DESC;";
		
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setInt(2, state);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				int delId = rs.getInt(4);
				int payId = rs.getInt(5);
				int orderStatus = rs.getInt(6);
				int spId = rs.getInt(7);
				Date orderTime = rs.getTimestamp(8);
				Date orderIdeal = rs.getTimestamp(9);
				Date orderDelivery = rs.getTimestamp(10);
				int adrsId = rs.getInt(11);
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				int order_type = rs.getInt(16);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderStatus, order_type);
				orderList.add(order);
			}
			return orderList;
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
		return orderList;
	}

	@Override
	public List<Order> findByCase(int id, String type) {
		String sqlPart = "";
		switch (type) {
			case "member":
				sqlPart = "mem_id";
				break;
			case "shop":
				sqlPart = "shop_id";
				break;
			case "delivery":
				sqlPart = "del_id";
				break;
			default:
				return null;
		}
		String sql = null;
		sql = "SELECT order_id, shop_id, mem_id, del_id, pay_id, order_state, sp_id, order_time, "
				+ "order_ideal, order_delivery, adrs_id, order_name, order_phone, order_ttprice, order_area , order_type "
				+ "FROM `order` WHERE " + sqlPart + " = ? ORDER BY order_time DESC;";
		
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				int delId = rs.getInt(4);
				int payId = rs.getInt(5);
				int orderState = rs.getInt(6);
				int spId = rs.getInt(7);
				Date orderTime = rs.getTimestamp(8);
				Date orderIdeal = rs.getTimestamp(9);
				Date orderDelivery = rs.getTimestamp(10);
				int adrsId = rs.getInt(11);
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				int order_type = rs.getInt(16);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderState, order_type);
				orderList.add(order);
			}
			return orderList;
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
		return orderList;
	}
}

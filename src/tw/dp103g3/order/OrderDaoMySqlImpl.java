package tw.dp103g3.order;

import static tw.dp103g3.main.Common.PASSWORD;
import static tw.dp103g3.main.Common.URL;
import static tw.dp103g3.main.Common.USER;
import static tw.dp103g3.main.Common.CLASS_NAME;
import static tw.dp103g3.main.Common.dateTimeFormat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
		String sql = "INSERT INTO order"
				+ "(shop_id, mem_id, del_id, pay_id, sp_id, order_ideal, order_delivery, adrs_id, order_name, order_phone, order_ttprice)"
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?);";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, order.getShop_id());
			ps.setInt(2, order.getMem_id());
			if (order.getDel_id() != 0 ) {
				ps.setInt(3, order.getDel_id());
			} else {
				ps.setInt(3, 0);
			}
			if (order.getPay_id() != 0) {
				ps.setInt(4, order.getPay_id());
			} else {
				ps.setInt(4, 0);
			}
			if (order.getSp_id() != 0) {
				ps.setInt(5, order.getSp_id());
			} else {
				ps.setInt(5,0);
			}
			if (order.getOrder_ideal() != null && !order.getOrder_ideal().equals("")) {
				ps.setTimestamp(6,
						java.sql.Timestamp.valueOf(LocalDateTime.parse(order.getOrder_ideal(), dateTimeFormat)));
			} else {
				ps.setTimestamp(6, null);
			}
			if (order.getOrder_delivery() != null && !order.getOrder_delivery().equals("")) {
				ps.setTimestamp(7,
						java.sql.Timestamp.valueOf(LocalDateTime.parse(order.getOrder_delivery(), dateTimeFormat)));
			} else {
				ps.setTimestamp(7, null);
			}
			if (order.getAdrs_id() != 0) {
				ps.setInt(8, order.getAdrs_id());
			} else {
				ps.setInt(8, 0);
			}
			ps.setString(9, order.getOrder_name());
			ps.setString(10, order.getOrder_phone());
			ps.setInt(11, order.getOrder_ttprice());

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
		String sql = "UPDATE order SET shop_id = ?, mem_id = ?, del_id = ?, pay_id = ?, sp_id = ?,"
				+ " order_ideal = ?, order_delivery = ?, adrs_id = ?, order_name = ?, order_phone = ?, order_ttprice = ?, order_status = ? WHERE order_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, order.getShop_id());
			ps.setInt(2, order.getMem_id());
			if (order.getDel_id() != 0) {
				ps.setInt(3, order.getDel_id());
			} else {
				ps.setInt(3, 0);
			}
			if (order.getPay_id() != 0) {
				ps.setInt(4, order.getPay_id());
			} else {
				ps.setInt(4, 0);
			}
			if (order.getSp_id() != 0) {
				ps.setInt(5, order.getSp_id());
			} else {
				ps.setInt(5, 0);
			}
			if (order.getOrder_ideal() != null && !order.getOrder_ideal().equals("")) {
				ps.setTimestamp(6,
						java.sql.Timestamp.valueOf(LocalDateTime.parse(order.getOrder_ideal(), dateTimeFormat)));
			} else {
				ps.setTimestamp(6, null);
			}
			if (order.getOrder_delivery() != null && !order.getOrder_delivery().equals("")) {
				ps.setTimestamp(7,
						java.sql.Timestamp.valueOf(LocalDateTime.parse(order.getOrder_delivery(), dateTimeFormat)));
			} else {
				ps.setTimestamp(7, null);
			}
			if (order.getAdrs_id() != 0) {
				ps.setInt(8, order.getAdrs_id());
			} else {
				ps.setInt(8, 0);
			}
			ps.setString(9, order.getOrder_name());
			ps.setString(10, order.getOrder_phone());
			ps.setInt(11, order.getOrder_ttprice());
			ps.setInt(12, order.getOrder_status());
			ps.setInt(13, order.getOrder_id());

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
		String sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_status, sp_id, order_time, order_ideal, order_delivery, "
				+ "adrs_id, order_name, order_phone, order_ttpice, order_area "
				+ "FROM order WHERE order_id = ? ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, order_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int delId;
				int payId;
				int spId;
				String orderTime;
				String orderIdeal;
				String orderDelivery;
				int adrsId;
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				if (rs.getInt(4) != 0) {
					delId = rs.getInt(4);
				} else {
					delId = 0;
				}
				if (rs.getInt(5) != 0) {
					payId = rs.getInt(5);
				} else {
					payId = 0;
				}
				int orderStatus = rs.getInt(6);
				if (rs.getInt(7) != 0) {
					spId = rs.getInt(7);
				} else {
					spId = 0;
				}
				if (rs.getTimestamp(8) != null) {
					orderTime = rs.getTimestamp(8).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderTime = null;
				}
				if (rs.getTimestamp(9) != null) {
					orderIdeal = rs.getTimestamp(9).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderIdeal = null;
				}
				if (rs.getTimestamp(10) != null) {
					orderDelivery = rs.getTimestamp(10).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderDelivery = null;
				}
				if (rs.getInt(11) != 0) {
					adrsId = rs.getInt(11);
				} else {
					adrsId = 0;
				}
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderStatus);
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
	public List<Order> findByMemId(int mem_id) {
		String sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_status, sp_id, order_time, order_ideal, order_delivery, "
				+ "adrs_id, order_name, order_phone, order_ttpice, order_area "
				+ "FROM order WHERE mem_id = ? ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, mem_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int delId;
				int payId;
				int spId;
				String orderTime;
				String orderIdeal;
				String orderDelivery;
				int adrsId;
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				if (rs.getInt(4) != 0) {
					delId = rs.getInt(4);
				} else {
					delId = 0;
				}
				if (rs.getInt(5) != 0) {
					payId = rs.getInt(5);
				} else {
					payId = 0;
				}
				int orderStatus = rs.getInt(6);
				if (rs.getInt(7) != 0) {
					spId = rs.getInt(7);
				} else {
					spId = 0;
				}
				if (rs.getTimestamp(8) != null) {
					orderTime = rs.getTimestamp(8).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderTime = null;
				}
				if (rs.getTimestamp(9) != null) {
					orderIdeal = rs.getTimestamp(9).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderIdeal = null;
				}
				if (rs.getTimestamp(10) != null) {
					orderDelivery = rs.getTimestamp(10).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderDelivery = null;
				}
				if (rs.getInt(11) != 0) {
					adrsId = rs.getInt(11);
				} else {
					adrsId = 0;
				}
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderStatus);
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
	public List<Order> findByShopId(int shop_id) {
		String sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_status, sp_id, order_time, order_ideal, order_delivery, "
				+ "adrs_id, order_name, order_phone, order_ttpice, order_area "
				+ "FROM order WHERE shop_id = ? ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, shop_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int delId;
				int payId;
				int spId;
				String orderTime;
				String orderIdeal;
				String orderDelivery;
				int adrsId;
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				if (rs.getInt(4) != 0) {
					delId = rs.getInt(4);
				} else {
					delId = 0;
				}
				if (rs.getInt(5) != 0) {
					payId = rs.getInt(5);
				} else {
					payId = 0;
				}
				int orderStatus = rs.getInt(6);
				if (rs.getInt(7) != 0) {
					spId = rs.getInt(7);
				} else {
					spId = 0;
				}
				if (rs.getTimestamp(8) != null) {
					orderTime = rs.getTimestamp(8).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderTime = null;
				}
				if (rs.getTimestamp(9) != null) {
					orderIdeal = rs.getTimestamp(9).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderIdeal = null;
				}
				if (rs.getTimestamp(10) != null) {
					orderDelivery = rs.getTimestamp(10).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderDelivery = null;
				}
				if (rs.getInt(11) != 0) {
					adrsId = rs.getInt(11);
				} else {
					adrsId = 0;
				}
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderStatus);
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
	public List<Order> findByDelId(int del_id) {
		String sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_status, sp_id, order_time, order_ideal, order_delivery, "
				+ "adrs_id, order_name, order_phone, order_ttpice, order_area "
				+ "FROM order WHERE del_id = ? ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, del_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int delId;
				int payId;
				int spId;
				String orderTime;
				String orderIdeal;
				String orderDelivery;
				int adrsId;
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				if (rs.getInt(4) != 0) {
					delId = rs.getInt(4);
				} else {
					delId = 0;
				}
				if (rs.getInt(5) != 0) {
					payId = rs.getInt(5);
				} else {
					payId = 0;
				}
				int orderStatus = rs.getInt(6);
				if (rs.getInt(7) != 0) {
					spId = rs.getInt(7);
				} else {
					spId = 0;
				}
				if (rs.getTimestamp(8) != null) {
					orderTime = rs.getTimestamp(8).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderTime = null;
				}
				if (rs.getTimestamp(9) != null) {
					orderIdeal = rs.getTimestamp(9).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderIdeal = null;
				}
				if (rs.getTimestamp(10) != null) {
					orderDelivery = rs.getTimestamp(10).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderDelivery = null;
				}
				if (rs.getInt(11) != 0) {
					adrsId = rs.getInt(11);
				} else {
					adrsId = 0;
				}
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderStatus);
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
	public List<Order> findMemOrderByStatus(int mem_id, int order_status) {
		String sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_status, sp_id, order_time, order_ideal, order_delivery, "
				+ "adrs_id, order_name, order_phone, order_ttpice, order_area "
				+ "FROM order WHERE mem_id = ? AND order_status = ? ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, mem_id);
			ps.setInt(2, order_status);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int delId;
				int payId;
				int spId;
				String orderTime;
				String orderIdeal;
				String orderDelivery;
				int adrsId;
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				if (rs.getInt(4) != 0) {
					delId = rs.getInt(4);
				} else {
					delId = 0;
				}
				if (rs.getInt(5) != 0) {
					payId = rs.getInt(5);
				} else {
					payId = 0;
				}
				int orderStatus = rs.getInt(6);
				if (rs.getInt(7) != 0) {
					spId = rs.getInt(7);
				} else {
					spId = 0;
				}
				if (rs.getTimestamp(8) != null) {
					orderTime = rs.getTimestamp(8).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderTime = null;
				}
				if (rs.getTimestamp(9) != null) {
					orderIdeal = rs.getTimestamp(9).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderIdeal = null;
				}
				if (rs.getTimestamp(10) != null) {
					orderDelivery = rs.getTimestamp(10).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderDelivery = null;
				}
				if (rs.getInt(11) != 0) {
					adrsId = rs.getInt(11);
				} else {
					adrsId = 0;
				}
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderStatus);
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
	public List<Order> findShopOrderByStatus(int shop_id, int order_status) {
		String sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_status, sp_id, order_time, order_ideal, order_delivery, "
				+ "adrs_id, order_name, order_phone, order_ttpice, order_area "
				+ "FROM order WHERE shop_id = ? AND order_status = ? ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, shop_id);
			ps.setInt(2, order_status);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int delId;
				int payId;
				int spId;
				String orderTime;
				String orderIdeal;
				String orderDelivery;
				int adrsId;
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				if (rs.getInt(4) != 0) {
					delId = rs.getInt(4);
				} else {
					delId = 0;
				}
				if (rs.getInt(5) != 0) {
					payId = rs.getInt(5);
				} else {
					payId = 0;
				}
				int orderStatus = rs.getInt(6);
				if (rs.getInt(7) != 0) {
					spId = rs.getInt(7);
				} else {
					spId = 0;
				}
				if (rs.getTimestamp(8) != null) {
					orderTime = rs.getTimestamp(8).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderTime = null;
				}
				if (rs.getTimestamp(9) != null) {
					orderIdeal = rs.getTimestamp(9).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderIdeal = null;
				}
				if (rs.getTimestamp(10) != null) {
					orderDelivery = rs.getTimestamp(10).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderDelivery = null;
				}
				if (rs.getInt(11) != 0) {
					adrsId = rs.getInt(11);
				} else {
					adrsId = 0;
				}
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderStatus);
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
	public List<Order> findDelOrderByStatus(int del_id, int order_status) {
		String sql = "SELECT  order_id, shop_id, mem_id, del_id, pay_id, order_status, sp_id, order_time, order_ideal, order_delivery, "
				+ "adrs_id, order_name, order_phone, order_ttpice, order_area "
				+ "FROM order WHERE del_id = ? AND order_status = ? ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, del_id);
			ps.setInt(2, order_status);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int delId;
				int payId;
				int spId;
				String orderTime;
				String orderIdeal;
				String orderDelivery;
				int adrsId;
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				int memId = rs.getInt(3);
				if (rs.getInt(4) != 0) {
					delId = rs.getInt(4);
				} else {
					delId = 0;
				}
				if (rs.getInt(5) != 0) {
					payId = rs.getInt(5);
				} else {
					payId = 0;
				}
				int orderStatus = rs.getInt(6);
				if (rs.getInt(7) != 0) {
					spId = rs.getInt(7);
				} else {
					spId = 0;
				}
				if (rs.getTimestamp(8) != null) {
					orderTime = rs.getTimestamp(8).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderTime = null;
				}
				if (rs.getTimestamp(9) != null) {
					orderIdeal = rs.getTimestamp(9).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderIdeal = null;
				}
				if (rs.getTimestamp(10) != null) {
					orderDelivery = rs.getTimestamp(10).toLocalDateTime().format(dateTimeFormat);
				} else {
					orderDelivery = null;
				}
				if (rs.getInt(11) != 0) {
					adrsId = rs.getInt(11);
				} else {
					adrsId = 0;
				}
				String order_name = rs.getString(12);
				String order_phone = rs.getString(13);
				int order_ttprice = rs.getInt(14);
				int order_area = rs.getInt(15);
				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderStatus);
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

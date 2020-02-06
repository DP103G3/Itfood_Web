package tw.dp103g3.order_detail;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tw.dp103g3.dish.Dish;

import static tw.dp103g3.main.Common.CLASS_NAME;
import static tw.dp103g3.main.Common.PASSWORD;
import static tw.dp103g3.main.Common.USER;
import static tw.dp103g3.main.Common.URL;

public class OrderDetailDaoMySqlImpl implements OrderDetailDao {

	public OrderDetailDaoMySqlImpl() {
		super();
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(List<OrderDetail> orderDetails) {
		int count = 0;
		String sql = "INSERT INTO `order_detail` (order_id, dish_id, od_count, od_price, od_message)"
				+ " VALUES (?, ?, ?, ?, ?);";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			for (OrderDetail orderDetail : orderDetails) {
				ps.setInt(1, orderDetail.getOrder_id());
				ps.setInt(2, orderDetail.getDish_id());
				ps.setInt(3, orderDetail.getOd_count());
				ps.setInt(4, orderDetail.getOd_price());
				ps.setString(5, orderDetail.getOd_message());
				ps.addBatch();
			}
			try {
				ps.executeBatch();
				count = 1;
			} catch (BatchUpdateException e) {
				e.printStackTrace();
				count = 0;
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
		return count;
	}

	@Override
	public int update(OrderDetail orderDetail) {
		int count = 0;
		String sql = " UPDATE `order_detail` SET od_id = ?, order_id = ?, dish_id = ?, od_count = ?, od_price = ?, od_message = ? "
				+ "WHERE od_id = ?; ";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, orderDetail.getOd_id());
			ps.setInt(2, orderDetail.getOrder_id());
			ps.setInt(3, orderDetail.getDish().getId());
			ps.setInt(4, orderDetail.getOd_count());
			ps.setInt(5, orderDetail.getOd_price());
			ps.setString(6, orderDetail.getOd_message());

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
	public List<OrderDetail> findByOrderId(int order_id) {
		String sql = "SELECT  od_id, order_id, `order_detail`.dish_id, dish_name, dish_info, od_count, od_price, "
				+ "od_message FROM `order_detail` JOIN `dish` ON `dish`.dish_id = `order_detail`.dish_id "
				+ "WHERE order_id = ? ORDER BY od_id;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, order_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int od_id = rs.getInt(1);
				int orderId = rs.getInt(2);
				int dish_id = rs.getInt(3);
				String dish_name = rs.getString(4);
				String dish_info = rs.getString(5);
				int od_count = rs.getInt(6);
				int od_price = rs.getInt(7);
				String od_message = rs.getString(8);
				OrderDetail orderDetail = new OrderDetail(od_id, orderId, new Dish(dish_id, dish_name, dish_info),
						od_count, od_price, od_message);
				orderDetailList.add(orderDetail);
			}
			return orderDetailList;
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
		return orderDetailList;
	}

	@Override
	public void commit() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void rollback() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}

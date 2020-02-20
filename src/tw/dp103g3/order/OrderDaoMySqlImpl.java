package tw.dp103g3.order;

import static tw.dp103g3.main.Common.PASSWORD;
import static tw.dp103g3.main.Common.URL;
import static tw.dp103g3.main.Common.USER;
import static tw.dp103g3.main.Common.CLASS_NAME;

import java.lang.reflect.Type;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import tw.dp103g3.address.Address;
import tw.dp103g3.address.AddressDao;
import tw.dp103g3.address.AddressDaoMysqlImpl;
import tw.dp103g3.dish.Dish;
import tw.dp103g3.dish.DishDao;
import tw.dp103g3.dish.DishDaoMysqlImpl;
import tw.dp103g3.member.Member;
import tw.dp103g3.member.MemberDao;
import tw.dp103g3.member.MemberDaoMySqlImpl;
import tw.dp103g3.order_detail.OrderDetail;
import tw.dp103g3.order_detail.OrderDetailDao;
import tw.dp103g3.order_detail.OrderDetailDaoMySqlImpl;
import tw.dp103g3.payment.Payment;
import tw.dp103g3.payment.PaymentDao;
import tw.dp103g3.payment.PaymentDaoMySqlImpl;
import tw.dp103g3.shop.Shop;

import java.util.Date;

public class OrderDaoMySqlImpl implements OrderDao {
	private OrderDetailDao orderDetailDao = new OrderDetailDaoMySqlImpl();

	public OrderDaoMySqlImpl() {
		super();
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Order order, String orderDetailsJson) {
		List<OrderDetail> orderDetails = new ArrayList<>();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		int odCount = 0;
		int orderCount = 0;
		int count = 0;
		String sql = "INSERT INTO `order` (shop_id, mem_id, del_id, pay_id, sp_id, order_ideal, "
				+ "order_delivery, adrs_id, order_name, order_phone, order_ttprice, order_type, order_state) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, order.getShop().getId());
			ps.setInt(2, order.getMem_id());
			if (order.getDel_id() != 0) {
				ps.setInt(3, order.getDel_id());
			} else {
				ps.setNull(3, Types.INTEGER);
			}
			if (order.getPay_id() != 0) {
				ps.setInt(4, order.getPay_id());
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			if (order.getSp_id() != 0) {
				ps.setInt(5, order.getSp_id());
			} else {
				ps.setNull(5, Types.INTEGER);
			}
			ps.setTimestamp(6, new Timestamp(order.getOrder_ideal().getTime()));
			ps.setTimestamp(7,
					order.getOrder_delivery() != null ? new Timestamp(order.getOrder_delivery().getTime()) : null);
			if (order.getAdrs_id() != 0) {
				ps.setInt(8, order.getAdrs_id());
			} else {
				ps.setNull(8, Types.INTEGER);
			}
			ps.setString(9, order.getOrder_name());
			ps.setString(10, order.getOrder_phone());
			ps.setInt(11, order.getOrder_ttprice());
			ps.setInt(12, order.getOrder_type());
			ps.setInt(13, order.getOrder_state());
			orderCount = ps.executeUpdate();
			
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					order.setOrder_id(generatedKeys.getInt(1));
					Type listType = new TypeToken<List<JsonObject>>() {}.getType();
					List<JsonObject> orderDetailsJsonObject = gson.fromJson(orderDetailsJson, listType);
					for (JsonObject orderDetailJsonObject : orderDetailsJsonObject) {
						int dish_id = orderDetailJsonObject.get("dish_id").getAsInt();
						int order_id =  order.getOrder_id();
						int od_count =  orderDetailJsonObject.get("od_count").getAsInt();
						int od_price =  orderDetailJsonObject.get("od_price").getAsInt();
						String od_message =  orderDetailJsonObject.get("od_message").getAsString();
						OrderDetail od = new OrderDetail(order_id, dish_id, od_count, od_price, od_message);
						orderDetails.add(od);
					}
					
					String sqlOd = "INSERT INTO `order_detail` (order_id, dish_id, od_count, od_price, od_message)"
							+ " VALUES (?, ?, ?, ?, ?);";
					PreparedStatement psOd = connection.prepareStatement(sqlOd);
					for (OrderDetail orderDetail : orderDetails) {
						psOd.setInt(1, orderDetail.getOrder_id());
						psOd.setInt(2, orderDetail.getDish_id());
						psOd.setInt(3, orderDetail.getOd_count());
						psOd.setInt(4, orderDetail.getOd_price());
						psOd.setString(5, orderDetail.getOd_message());
						psOd.addBatch();
					}
					try {
						psOd.executeBatch();
						odCount = 1;
					} catch (BatchUpdateException e) {
						e.printStackTrace();
						odCount = 0;
					}

				} else {
					throw new SQLException("Creating order failed, no ID obtained.");
				}
			}
			
			if (orderCount + odCount == 2) {
				count = 1;
				connection.commit();
			} else {
				connection.rollback();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (connection != null) {
					connection.setAutoCommit(true);
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
			ps.setInt(1, order.getShop().getId());
			ps.setInt(2, order.getMem_id());
			if (order.getDel_id() != 0) {
				ps.setInt(3, order.getDel_id());
			} else {
				ps.setNull(3, java.sql.Types.INTEGER);
			}
			ps.setInt(4, order.getPay_id());			
			if (order.getSp_id() != 0) {
				ps.setInt(5, order.getSp_id());
			} else {
				ps.setNull(5, java.sql.Types.INTEGER);
			}
			ps.setTimestamp(6, new Timestamp(order.getOrder_ideal().getTime()));
			ps.setTimestamp(7,
					order.getOrder_delivery() != null ? new Timestamp(order.getOrder_delivery().getTime()) : null);
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
		String sql = "SELECT  order_id, shop_id, shop_name, mem_id, del_id, pay_id, order_state, sp_id, order_time, order_ideal, order_delivery, "
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
				String shopName = rs.getString(3);
				int memId = rs.getInt(4);
				int delId = rs.getInt(5);
				int payId = rs.getInt(6);
				int orderState = rs.getInt(7);
				int spId = rs.getInt(8);
				Date orderTime = rs.getTimestamp(9);
				Date orderIdeal = rs.getTimestamp(10);
				Date orderDelivery = rs.getTimestamp(11);
				int adrsId = rs.getInt(12);
				String order_name = rs.getString(13);
				String order_phone = rs.getString(14);
				int order_ttprice = rs.getInt(15);
				int order_area = rs.getInt(16);
				int order_type = rs.getInt(17);
				List<OrderDetail> orderDetails = orderDetailDao.findByOrderId(order_id);
				Order order = new Order(orderId, new Shop(shopId, shopName), memId, delId, payId, spId, orderIdeal,
						orderTime, orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area,
						orderState, order_type, orderDetails);
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
	public List<Order> findByCase(int id, String type, int state, Calendar date, boolean containDay) {
		String sqlPart = "";
		switch (type) {
		case "member":
			sqlPart = "mem_id";
			break;
		case "shop":
			sqlPart = "`shop`.shop_id";
			break;
		case "delivery":
			sqlPart = "del_id";
			break;
		}
		String sql = "SELECT order_id, `shop`.shop_id, shop_name, mem_id, del_id, pay_id, order_state, sp_id, order_time, order_ideal, "
				+ "order_delivery, adrs_id, order_name, order_phone, order_ttprice, order_area, order_type FROM `order` "
				+ "LEFT JOIN `shop` ON `order`.shop_id = `shop`.shop_id " + "WHERE " + sqlPart + " = ? "
				+ (state == -1 ? "" : "AND order_state = ? ") 
				+ (date == null ? "" : "AND order_delivery >= ? "
						+ "AND order_delivery < ? ") 
				+ "ORDER BY order_time DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			if (state != -1) {
				ps.setInt(2, state);
				if (date != null) {
					ps.setTimestamp(3, new Timestamp(date.getTimeInMillis()));
					date.add(containDay ? Calendar.DAY_OF_MONTH : Calendar.MONTH, 1);
					ps.setTimestamp(4, new Timestamp(date.getTimeInMillis()));
				}
			} else {
				if (date != null) {
					ps.setTimestamp(2, new Timestamp(date.getTimeInMillis()));
					date.add(containDay ? Calendar.DAY_OF_MONTH : Calendar.MONTH, 1);
					ps.setTimestamp(3, new Timestamp(date.getTimeInMillis()));
				}
			}
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int orderId = rs.getInt(1);
				int shopId = rs.getInt(2);
				String shopName = rs.getString(3);
				int memId = rs.getInt(4);
				int delId = rs.getInt(5);
				int payId = rs.getInt(6);
				int orderStatus = rs.getInt(7);
				int spId = rs.getInt(8);
				Date orderTime = rs.getTimestamp(9);
				Date orderIdeal = rs.getTimestamp(10);
				Date orderDelivery = rs.getTimestamp(11);
				int adrsId = rs.getInt(12);
				String order_name = rs.getString(13);
				String order_phone = rs.getString(14);
				int order_ttprice = rs.getInt(15);
				int order_area = rs.getInt(16);
				int order_type = rs.getInt(17);
				List<OrderDetail> orderDetails = orderDetailDao.findByOrderId(orderId);
				Order order = new Order(orderId, new Shop(shopId, shopName), memId, delId, payId, spId, orderIdeal,
						orderTime, orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area,
						orderStatus, order_type, orderDetails);
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
		return findByCase(id, type, -1, null, false);
	}

	@Override
	public Cart getCart(List<Integer> dishIds, int mem_id) {
		DishDao dishDao = new DishDaoMysqlImpl();
		PaymentDao paymentDao = new PaymentDaoMySqlImpl();
		AddressDao addressDao = new AddressDaoMysqlImpl();
		MemberDao memberDao = new MemberDaoMySqlImpl();
		List<Dish> dishes = new ArrayList<>();
		List<Payment> payments = new ArrayList<>();
		List<Address> addresses = new ArrayList<>();
		
		
		for (Integer dishId : dishIds) {
			dishes.add(dishDao.getDishById(dishId));
		}
		
		payments = paymentDao.getByMemberId(mem_id, 1);
		addresses = addressDao.getAllShow(mem_id);
		Member member = memberDao.findById(mem_id);
		
		Cart cart = new Cart(dishes, member, payments, addresses);
		
		return cart;
	}
	
	

//	@Override
//	public List<Order> findByCase(int id, String type) {
//		String sqlPart = "";
//		switch (type) {
//			case "member":
//				sqlPart = "mem_id";
//				break;
//			case "shop":
//				sqlPart = "shop_id";
//				break;
//			case "delivery":
//				sqlPart = "del_id";
//				break;
//			default:
//				return null;
//		}
//		String sql = null;
//		sql = "SELECT order_id, shop_id, mem_id, del_id, pay_id, order_state, sp_id, order_time, "
//				+ "order_ideal, order_delivery, adrs_id, order_name, order_phone, order_ttprice, order_area , order_type "
//				+ "FROM `order` WHERE " + sqlPart + " = ? ORDER BY order_time DESC;";
//		
//		Connection connection = null;
//		PreparedStatement ps = null;
//		List<Order> orderList = new ArrayList<Order>();
//		try {
//			connection = DriverManager.getConnection(URL, USER, PASSWORD);
//			ps = connection.prepareStatement(sql);
//			ps.setInt(1, id);
//			ResultSet rs = ps.executeQuery();
//			while (rs.next()) {
//				int orderId = rs.getInt(1);
//				int shopId = rs.getInt(2);
//				int memId = rs.getInt(3);
//				int delId = rs.getInt(4);
//				int payId = rs.getInt(5);
//				int orderState = rs.getInt(6);
//				int spId = rs.getInt(7);
//				Date orderTime = rs.getTimestamp(8);
//				Date orderIdeal = rs.getTimestamp(9);
//				Date orderDelivery = rs.getTimestamp(10);
//				int adrsId = rs.getInt(11);
//				String order_name = rs.getString(12);
//				String order_phone = rs.getString(13);
//				int order_ttprice = rs.getInt(14);
//				int order_area = rs.getInt(15);
//				int order_type = rs.getInt(16);
//				Order order = new Order(orderId, shopId, memId, delId, payId, spId, orderIdeal, orderTime,
//						orderDelivery, adrsId, order_name, order_phone, order_ttprice, order_area, orderState, order_type);
//				orderList.add(order);
//			}
//			return orderList;
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (ps != null) {
//					ps.close();
//				}
//				if (connection != null) {
//					connection.close();
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return orderList;
//	}
}

package tw.dp103g3.dish;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tw.dp103g3.shop.Shop;

import static tw.dp103g3.main.Common.*;

public class DishDaoMysqlImpl implements DishDao {
	
	public DishDaoMysqlImpl() {
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Dish> getAll() {
		List<Dish> dishes = new ArrayList<Dish>();
		String sql = "SELECT dish_id, dish_name, dish_info, dish_state, shop_id, dish_price FROM `dish`;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String info = rs.getString(3);
				byte state = rs.getByte(4);
				int shop_id = rs.getInt(5);
				int price = rs.getInt(6);
				Dish dish = new Dish(id, name, info, state, shop_id, price);
				dishes.add(dish);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dishes;
	}

	@Override
	public List<Dish> getAllShow(int shop_id) {
		List<Dish> dishes = new ArrayList<Dish>();
		String sql = "SELECT dish_id, dish_name, dish_info, dish_price FROM `dish` "
				+ "WHERE dish_state = 1 AND shop_id = ?;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, shop_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String info = rs.getString(3);
				int price = rs.getInt(4);
				Dish dish = new Dish(id, name, info, shop_id, price);
				dishes.add(dish);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dishes;
	}

	@Override
	public int insert(Dish dish, byte[] image) {
		int count = 0;
		String sql = null;
		if (image != null) {
			sql = "INSERT INTO `dish` (dish_name, dish_info, dish_state, shop_id, dish_price, "
					+ "dish_image) VALUES (?, ?, ?, ?, ?, ?);";
		} else {
			sql = "INSERT INTO `dish` (dish_name, dish_info, dish_state, shop_id, dish_price) "
					+ "VALUES (?, ?, ?, ?, ?);";
		}
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, dish.getName());
			ps.setString(2, dish.getInfo());
			ps.setByte(3, dish.getState());
			ps.setInt(4, dish.getShop_id());
			ps.setInt(5, dish.getPrice());
			if (image != null) {
				ps.setBytes(6, image);
			}
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public int update(Dish dish, byte[] image) {
		int count = 0;
		String sql = null;
		if (image != null) {
			sql = "UPDATE `dish` SET dish_name = ?, dish_info = ?, dish_state = ?, shop_id = ?, dish_price = ?, "
					+ "dish_image = ? WHERE dish_id = ?;";
		} else {
			sql = "UPDATE `dish` SET dish_name = ?, dish_info = ?, dish_state = ?, shop_id = ?, dish_price = ? "
					+ "WHERE dish_id = ?;";
		}
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, dish.getName());
			ps.setString(2, dish.getInfo());
			ps.setByte(3, dish.getState());
			ps.setInt(4, dish.getShop_id());
			ps.setInt(5, dish.getPrice());
			if (image != null) {
				ps.setBytes(6, image);
				ps.setInt(7, dish.getId());
			} else {
				ps.setInt(6, dish.getId());
			}
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public Dish getDishById(int id) {
		Dish dish = null;
		String sql = "SELECT dish_name, dish_info, shop_id, dish_price FROM `dish` WHERE dish_id = ?;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String name = rs.getString(1);
				String info = rs.getString(2);
				int shop_id = rs.getInt(3);
				int price = rs.getInt(4);
				dish = new Dish(id, name, info, shop_id, price);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dish;
	}

	@Override
	public List<Dish> getDishByShopId(int shop_id) {
		List<Dish> dishes = new ArrayList<Dish>();
		String sql = "SELECT dish_id, dish_name, dish_info, dish_state, dish_price FROM `dish` WHERE shop_id = ?;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, shop_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String info = rs.getString(3);
				byte state = rs.getByte(4);
				int price = rs.getInt(5);
				Dish dish = new Dish(id, name, info, shop_id,state, price);
				dishes.add(dish);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dishes;
	}
	
	@Override
	public Dish getAccount(int id) {
		String sql = "SELECT dish_id, dish_state FROM `dish` WHERE dish_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		Dish dish = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
			byte state = rs.getByte(2);
			dish = new Dish(id, state);
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
		return dish;
	}
	
	@Override
	public int saveAccount(Dish dish) {
		int count = 0;
		String sql = "UPDATE `dish` SET dish_state = ?  WHERE dish_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setByte(1, dish.getState());
			ps.setInt(2, dish.getId());
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
		String sql = "SELECT dish_image from `dish` WHERE dish_id = ?;";
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

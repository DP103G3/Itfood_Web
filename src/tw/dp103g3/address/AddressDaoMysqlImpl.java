package tw.dp103g3.address;

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
import java.util.Set;

public class AddressDaoMysqlImpl implements AddressDao {
	public AddressDaoMysqlImpl() {
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Address> getAllShow(int mem_id) {
		List<Address> addresses = new ArrayList<Address>();
		String sql = "SELECT adrs_id, adrs_name, adrs_info, adrs_latitude, adrs_longitude "
				+ "FROM `address` WHERE adrs_state = 1 & mem_id = ?;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, mem_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String info = rs.getString(3);
				double latitude = rs.getDouble(4);
				double longitude = rs.getDouble(5);
				Address address = new Address(id, name, info, latitude, longitude);
				addresses.add(address);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addresses;
	}

	@Override
	public int insert(Address address) {
		int count = 0;
		String sql = "INSERT INTO `address` (adrs_id, mem_id, adrs_name, adrs_info, adrs_state, "
				+ "adrs_latitude, adrs_longitude) VALUES (?, ?, ?, ?, ?, ?, ?);";
		if (getAll(address.getMem_id()).stream().anyMatch(v -> v.equals(address))) {
			count = update(address);
		} else {
			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement ps = connection.prepareStatement(sql);) {
				ps.setInt(1, address.getId());
				ps.setInt(2, address.getMem_id());
				ps.setString(3, address.getName());
				ps.setString(4, address.getInfo());
				ps.setByte(5, address.getState());
				ps.setDouble(6, address.getLatitude());
				ps.setDouble(7, address.getLongitude());
				count = ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	@Override
	public int update(Address address) {
		int count = 0;
		String sql = "UPDATE `address` SET adrs_name = ?, adrs_info = ?, adrs_state = ?, adrs_latitude = ?, "
				+ "adrs_longitude = ? WHERE adrs_id = ?;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, address.getName());
			ps.setString(2, address.getInfo());
			ps.setByte(3, address.getState());
			ps.setDouble(4, address.getLatitude());
			ps.setDouble(5, address.getLongitude());
			ps.setInt(6, address.getId());
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public List<Address> getAll(int mem_id) {
		List<Address> addresses = new ArrayList<Address>();
		String sql = "SELECT adrs_id, adrs_name, adrs_info, adrs_state, adrs_latitude, adrs_longitude "
				+ "FROM `address` WHERE mem_id = ?;";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, mem_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String info = rs.getString(3);
				byte state = rs.getByte(4);
				double latitude = rs.getDouble(5);
				double longitude = rs.getDouble(6);
				Address address = new Address(id, mem_id, name, info, state, latitude, longitude);
				addresses.add(address);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addresses;
	}
}
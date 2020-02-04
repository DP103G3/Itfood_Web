package tw.dp103g3.payment;

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

public class PaymentDaoMySqlImpl implements PaymentDao {

	public PaymentDaoMySqlImpl() {
		super();
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Payment payment) {
		int count = 0;
		String sql = "INSERT INTO `payment` (pay_name, mem_id, pay_cardnum"
				+ ", pay_due, pay_holdername, pay_securitycode, pay_phone, pay_state) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setString(1, payment.getPay_name());
			ps.setInt(2, payment.getMember_id());
			ps.setString(3, payment.getPay_cardnum());
			ps.setString(4, payment.getPay_due());
			ps.setString(5, payment.getPay_holdername());
			ps.setString(6, payment.getPay_securitycode());
			ps.setString(7, payment.getPay_phone());
			ps.setInt(8, payment.getPay_state());
			
			count = ps.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null) {
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
	public int update(Payment payment) {
		int count = 0;
		String sql = "UPDATE `payment` pay_state = ? WHERE pay_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, payment.getPay_state());
			ps.setInt(2, payment.getPay_id());
			
			count = ps.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null) {
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
	public List<Payment> getByMemberId(int mem_id, int state) {
		String sql = "SELECT pay_id, pay_name, mem_id, pay_cardnum"
				+ ", pay_due, pay_holdername, pay_securitycode, pay_phone, pay_state "
				+ "FROM `payment` WHERE mem_id = ? AND payment_state = ?";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, mem_id);
			ps.setInt(2, state);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int payId = rs.getInt(1);
				String payName = rs.getString(2);
				int memId = rs.getInt(3);
				String payCardNum = rs.getString(4);
				String payDue = rs.getString(5);
				String payHolderName  = rs.getString(6);
				String paySecurityCode = rs.getString(7);
				String payPhone = rs.getString(8);
				int payState = rs.getInt(9);
				
				Payment payment = new Payment(payId, payName, memId, payCardNum, payDue, payHolderName
						,paySecurityCode, payPhone, payState);
				paymentList.add(payment);
			}
			return paymentList;	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null) {
					ps.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return paymentList;
	}

}

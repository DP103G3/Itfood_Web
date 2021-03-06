package tw.dp103g3.member;

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

import tw.dp103g3.shop.Shop;

public class MemberDaoMySqlImpl implements MemberDao {

	public MemberDaoMySqlImpl() {
		super();
		try {
			Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Member member) {
		int count = 0;
		String selectEmailSql = "SELECT * FROM `member` WHERE mem_email = ?;";
		String sql = "INSERT INTO `member` (mem_name, mem_password, mem_email, mem_phone, mem_state) VALUES(?, ?, ?, ?, ?);";
		String getIdSql = "SELECT last_insert_id();";
		Connection connection = null;
		PreparedStatement selectEmailPs = null;
		PreparedStatement ps = null;
		PreparedStatement getIdPs = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(false);
			selectEmailPs = connection.prepareStatement(selectEmailSql);
			ps = connection.prepareStatement(sql);
			getIdPs = connection.prepareStatement(getIdSql);
			selectEmailPs.setString(1, member.getMemEmail());
			ResultSet selectEmailRs = selectEmailPs.executeQuery();
			if (!selectEmailRs.next()) {
				ps.setString(1, member.getMemName());
				ps.setString(2, member.getMemPassword());
				ps.setString(3, member.getMemEmail());
				ps.setString(4, member.getMemPhone());
				ps.setInt(5, member.getMemState());
				count = ps.executeUpdate();
				if (count != 0) {
					ResultSet getIdRs = getIdPs.executeQuery();
					if (getIdRs.next()) {
						count = getIdRs.getInt(1);
						connection.commit();
					} else {
						count = 0;
						connection.rollback();
					}
				}
			} else {
				count = -1;
			}
		} catch (SQLException e) {
			count = 0;
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
	public int update(Member member) {
		int count = 0;
		String sql = "UPDATE `member` SET mem_name = ?, mem_password = ?, mem_email = ?, mem_phone = ?,mem_state = ?  WHERE mem_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setString(1, member.getMemName());
			ps.setString(2, member.getMemPassword());
			ps.setString(3, member.getMemEmail());
			ps.setString(4, member.getMemPhone());
			ps.setInt(5, member.getMemState());
			ps.setInt(6, member.getMemId());
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
	public int updatePassword(Member member) {
		int count = 0;
		String sql = "UPDATE `member` SET mem_password = ?  WHERE mem_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setString(1, member.getMemPassword());
			ps.setInt(2, member.getMemId());
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
	public Member getAccount(int mem_id) {
		String sql = "SELECT mem_id, mem_name, mem_password, mem_email, mem_phone, mem_state FROM `member` WHERE mem_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		Member member = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, mem_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String mem_name = rs.getString(2);
				String mem_password = rs.getString(3);
				String mem_email = rs.getString(4);
				String mem_phone = rs.getString(5);
				int mem_state = rs.getInt(6);
				member = new Member(mem_id, mem_name, mem_password, mem_email, mem_phone, mem_state);
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
		return member;
	}

	@Override
	public int saveAccount(Member member) {
		int count = 0;
		String sql = "UPDATE `member` SET mem_state = ?  WHERE mem_id = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ps.setInt(1, member.getMemState());
			ps.setInt(2, member.getMemId());
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
	public Member findById(int mem_id) {
		String sql = "SELECT mem_id, mem_name, mem_password, mem_email, mem_phone, mem_joindate, mem_state FROM `member` WHERE mem_id = ?;";
		Connection conn = null;
		PreparedStatement ps = null;
		Member member = null;
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, mem_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String mem_name = rs.getString(2);
				String mem_password = rs.getString(3);
				String mem_email = rs.getString(4);
				String mem_phone = rs.getString(5);
				Date mem_joindate = rs.getTimestamp(6);
				int mem_state = rs.getInt(7);
				member = new Member(mem_id, mem_name, mem_password, mem_email, mem_phone, mem_joindate, mem_state);

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
		return member;
	}

	@Override
	public List<Member> getAll() {
		String sql = "SELECT mem_id, mem_name, mem_password, mem_email, mem_phone, mem_joindate, mem_suspendtime, mem_state FROM `member` ORDER BY mem_joindate DESC;";
		Connection connection = null;
		PreparedStatement ps = null;
		List<Member> memberList = new ArrayList<Member>();
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int mem_id = rs.getInt(1);
				String mem_name = rs.getString(2);
				String mem_password = rs.getString(3);
				String mem_email = rs.getString(4);
				String mem_phone = rs.getString(5);
				Date mem_joindate = rs.getTimestamp(6);
				Date mem_suspendtime = rs.getTimestamp(7);
				int mem_state = rs.getInt(8);
				Member member = new Member(mem_id, mem_name, mem_password, mem_email, mem_phone, mem_joindate,
						mem_suspendtime, mem_state);
				memberList.add(member);
			}
			// System.out.println("output add sql:" + memberList);
			return memberList;
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
		System.out.println("output getAll return sql:" + memberList);
		return memberList;
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
		String sql = "SELECT mem_id, mem_password, mem_state "
				+ " FROM `member` WHERE mem_email = ?;";
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
				int mem_id = rs.getInt(1);
				String mem_password = rs.getString(2);
				System.out.println(mem_password);
				int mem_state = rs.getInt(3);
				outcome.put("id", mem_id);
				if (mem_password.equals(password)) {
					if (mem_state == 1) {
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
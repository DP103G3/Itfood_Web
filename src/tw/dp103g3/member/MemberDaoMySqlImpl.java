package tw.dp103g3.member;

import static tw.dp103g3.main.Common.CLASS_NAME;
import static tw.dp103g3.main.Common.PASSWORD;
import static tw.dp103g3.main.Common.URL;
import static tw.dp103g3.main.Common.USER;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		String sql = "INSERT INTO `member` (mem_name, mem_password, mem_email, mem_phone, mem_state) VALUES(?, ?, ?, ?, ?);";
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
	public Member findById(int mem_id) {
		String sql = "SELECT mem_name, mem_password, mem_email, mem_phone FROM `member` WHERE mem_id = ?;";
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
				Date mem_suspendtime = rs.getTimestamp(7);
				int mem_state = rs.getInt(8);
				member = new Member(mem_id, mem_name, mem_password, mem_email,mem_phone,mem_joindate,mem_suspendtime,mem_state);
				member = new Member(mem_id, mem_name, mem_password, mem_email,mem_phone);
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
				Member member = new Member(mem_id, mem_name, mem_password, mem_email,mem_phone,mem_joindate,mem_suspendtime,mem_state);
				memberList.add(member);
				
			}
			System.out.println("output add sql:" + memberList);
			//System.out.println("output add sql:" + memberList);
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
		//System.out.println("output getAll return sql:" + memberList);
		return memberList;
	}

}

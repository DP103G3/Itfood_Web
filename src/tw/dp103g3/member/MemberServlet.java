package tw.dp103g3.member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import tw.dp103g3.member.Member;
import tw.dp103g3.member.MemberDao;
import tw.dp103g3.member.MemberDaoMySqlImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@SuppressWarnings("serial")
@WebServlet("/MemberServlet")
public class MemberServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	MemberDao memberDao = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		Gson gson = new Gson();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		// 將輸入資料列印出來除錯用
		System.out.println("input: " + jsonIn);

		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if (memberDao == null) {
			memberDao = new MemberDaoMySqlImpl();
		}

		String action = jsonObject.get("action").getAsString();

		if (action.equals("getAll")) {
			List<Member> members = memberDao.getAll();
			writeText(response, gson.toJson(members));
		} else if (action.equals("memberInsert") || action.equals("memberUpdate")) {
			String memberJson = jsonObject.get("member").getAsString();
			System.out.println("memberJson = " + memberJson);
			int count = 0;
			writeText(response, String.valueOf(count));
		} else if (action.equals("memberDelete")) {
			int memberId = jsonObject.get("memberId").getAsInt();
			int count = memberDao.delete(memberId);
			writeText(response, String.valueOf(count));
		} else if (action.equals("findById")) {
			int mem_id = jsonObject.get("mem_id").getAsInt();
			Member member = memberDao.findById(mem_id);
			writeText(response, gson.toJson(member));
		} else {
			writeText(response, "not fun");
		}
	}

	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		// 將輸出資料列印出來除錯用
		System.out.println("output:" + outText);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (memberDao == null) {
			memberDao = new MemberDaoMySqlImpl();
		}
		List<Member> members = memberDao.getAll();
		writeText(response, new Gson().toJson(members));
	}

}

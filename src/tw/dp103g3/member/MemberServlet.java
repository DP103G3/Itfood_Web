package tw.dp103g3.member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import tw.dp103g3.member.Member;
import tw.dp103g3.member.MemberDao;
import tw.dp103g3.member.MemberDaoMySqlImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tw.dp103g3.main.Common.CONTENT_TYPE;

@SuppressWarnings("serial")
@WebServlet("/MemberServlet")
public class MemberServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	MemberDao memberDao = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

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
		List<Member> members = null;
		int count = 0;
		int mem_id = 0;
		Member member = null;
		String memberJson = null;
		String action = jsonObject.get("action").getAsString();

		switch (action) {
		case "insert":
		case "getAll":
			members = memberDao.getAll();
			writeText(response, gson.toJson(members));
			break;
		case "update":
			memberJson = jsonObject.get("member").getAsString();
			member = gson.fromJson(memberJson, Member.class);
			count = memberDao.update(member);
			writeText(response, String.valueOf(count));
			System.out.println("update = " + memberJson);
			break;
		case "findById":
			mem_id = jsonObject.get("mem_id").getAsInt();
			member = memberDao.findById(mem_id);
			writeText(response, gson.toJson(member));
			System.out.println("findById: " + member);
			break;
		case "getAccount":
			mem_id = jsonObject.get("mem_id").getAsInt();
			member = memberDao.getAccount(mem_id);
			writeText(response, gson.toJson(member));
			System.out.println("getAccount: " + member);
			break;
		case "saveAccount":
			memberJson = jsonObject.get("member").getAsString();
			member = gson.fromJson(memberJson, Member.class);
			count = memberDao.saveAccount(member);
			writeText(response, String.valueOf(count));
			System.out.println("saveAccount = " + memberJson);
			break;
		case "login":
			String email = jsonObject.get("mem_email").getAsString();
			String password = jsonObject.get("mem_password").getAsString();
			Map<String, Integer> outcome = new HashMap<>();
			outcome = memberDao.login(email, password);
			Type mapType = new TypeToken<Map<String, Integer>>(){}.getType();
			writeText(response, gson.toJson(outcome, mapType));
			System.out.println("login : " + outcome);
			break;
		case "updatePassword":
			memberJson = jsonObject.get("member").getAsString();
			member = gson.fromJson(memberJson, Member.class);
			count = memberDao.updatePassword(member);
			writeText(response, String.valueOf(count));
			System.out.println("update = " + memberJson);
			break;
		default:
			writeText(response, "not fun");
			break;


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

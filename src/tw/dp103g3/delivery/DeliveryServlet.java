package tw.dp103g3.delivery;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import tw.dp103g3.delivery.Delivery;
import tw.dp103g3.delivery.DeliveryDao;
import tw.dp103g3.delivery.DeliveryDaoMySqlImpl;
import tw.dp103g3.member.Member;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tw.dp103g3.main.Common.CONTENT_TYPE;

@SuppressWarnings("serial")
@WebServlet("/DeliveryServlet")
public class DeliveryServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	DeliveryDao deliveryDao = null;

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
		if (deliveryDao == null) {
			deliveryDao = new DeliveryDaoMySqlImpl();
		}
		List<Delivery> deliverys = null;
		int count = 0;
		int del_id = 0;
		Delivery delivery = null;
		String deliveryJson = null;
		String action = jsonObject.get("action").getAsString();

		switch (action) {
		case "getAll":
			deliverys = deliveryDao.getAll();
			writeText(response, gson.toJson(deliverys));
			break;
		case "insert":
		case "update":
			deliveryJson = jsonObject.get("delivery").getAsString();
			delivery = gson.fromJson(deliveryJson, Delivery.class);
			if (action.equals("insert")) {
				count = deliveryDao.insert(delivery);
			} else {
				count = deliveryDao.update(delivery);
			}
			writeText(response, String.valueOf(count));
			System.out.println("update = " + deliveryJson);
			break;
		case "findById":
			del_id = jsonObject.get("del_id").getAsInt();
			delivery = deliveryDao.findById(del_id);
			writeText(response, gson.toJson(delivery));
			System.out.println("findById: " + delivery);
			break;
		case "getAccount":
			del_id = jsonObject.get("del_id").getAsInt();
			delivery = deliveryDao.getAccount(del_id);
			writeText(response, gson.toJson(delivery));
			System.out.println("getAccount: " + delivery);
			break;
		case "saveAccount":
			deliveryJson = jsonObject.get("delivery").getAsString();
			delivery = gson.fromJson(deliveryJson, Delivery.class);
			count = deliveryDao.saveAccount(delivery);
			writeText(response, String.valueOf(count));
			System.out.println("saveAccount = " + deliveryJson);
			break;
		case "getDataById":
			del_id = jsonObject.get("del_id").getAsInt();
			delivery = deliveryDao.getDataById(del_id);
			writeText(response, gson.toJson(delivery));
			System.out.println("getDataById: " + delivery);
			break;
		case "login":
			String email = jsonObject.get("del_email").getAsString();
			String password = jsonObject.get("del_password").getAsString();
			Map<String, Integer> outcome = new HashMap<>();
			outcome = deliveryDao.login(email, password);
			Type mapType = new TypeToken<Map<String, Integer>>(){}.getType();
			writeText(response, gson.toJson(outcome, mapType));
			System.out.println("login : " + outcome);
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
		if (deliveryDao == null) {
			deliveryDao = new DeliveryDaoMySqlImpl();
		}
		List<Delivery> deliverys = deliveryDao.getAll();
		writeText(response, new Gson().toJson(deliverys));
	}

}

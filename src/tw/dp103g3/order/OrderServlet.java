package tw.dp103g3.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import static tw.dp103g3.main.Common.CONTENT_TYPE;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	OrderDao orderDao = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		
		System.out.println("input: " + jsonIn);

		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if (orderDao == null) {
			orderDao = new OrderDaoMySqlImpl();
		}

		String action = jsonObject.get("action").getAsString();

		if (action.equals("orderInsert") || action.equals("orderUpdate")) {
			String orderJson = jsonObject.get("order").getAsString();
			System.out.println("orderJson = " + orderJson);
			Order order = gson.fromJson(orderJson, Order.class);

			int count = 0;
			if (action.equals("orderInsert")) {
				String orderDetailsJson = jsonObject.get("orderDetailsJson").getAsString();
				count = orderDao.insert(order, orderDetailsJson);
			} else if (action.equals("orderUpdate")) {
				count = orderDao.update(order);
			}
			writeText(response, String.valueOf(count));
		} else if (action.equals("findByOrderId")) {
			int order_id = jsonObject.get("order_id").getAsInt();
			Order order = orderDao.findByOrderId(order_id);
			writeText(response, gson.toJson(order));
		} else if (action.equals("findByCase")) {
			int id = jsonObject.get("id").getAsInt();
			String type = jsonObject.get("type").getAsString();
			JsonElement stateJE = jsonObject.get("state");
			int state = stateJE != null ? stateJE.getAsInt() : -1;
			JsonElement dateMiliJE = jsonObject.get("dateMili");
			Calendar date = dateMiliJE == null ? 
					null : new Calendar.Builder().setInstant(dateMiliJE.getAsLong()).build();
			JsonElement containDayJE = jsonObject.get("containDay");
			boolean containDay = containDayJE == null ? false : containDayJE.getAsBoolean();
			List<Order> orders = orderDao.findByCase(id, type, state, date, containDay);
			writeText(response, gson.toJson(orders));
		} else if (action.equals("getCart")) {
			int mem_id = jsonObject.get("mem_id").getAsInt();
			String dishIdsJson = jsonObject.get("dishIds").getAsString();
			Type listType = new TypeToken<List<Integer>>() {}.getType();
			List<Integer> dishIds = gson.fromJson(dishIdsJson, listType);
			Cart cart = orderDao.getCart(dishIds, mem_id);
			writeText(response, gson.toJson(cart));
		} else if (action.equals("findByDeliveryId")) {
			int del_id = jsonObject.get("del_id").getAsInt();
			List<Order> orders = orderDao.findByDeliveryId(del_id);
			Type listType = new TypeToken<List<Order>>() {}.getType();
			writeText(response, gson.toJson(orders, listType));
			
		}
//		else if (action.equals("findByCaseWithState")) {
//			int id = jsonObject.get("id").getAsInt();
//			String type = jsonObject.get("type").getAsString();
//			int state = jsonObject.get("state").getAsInt();
//			List<Order> orders = orderDao.findByCase(id, type, state);
//			writeText(response, gson.toJson(orders));
//		}
		
		else {
			writeText(response, "");
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		orderDao = new OrderDaoMySqlImpl();
		Calendar date = new Calendar.Builder().setDate(2020, 1, 1).build();
		System.out.println(date.getTimeInMillis());
		List<Order> orders = orderDao.findByCase(1, "delivery", 4, date, false);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		writeText(response, gson.toJson(orders));
	}

	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		// 將輸出資料列印出來除錯用
		System.out.println("output: " + outText);
	}
}

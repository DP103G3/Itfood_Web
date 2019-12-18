package tw.dp103g3.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	OrderDao orderDao = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		Gson gson = new Gson();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}

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
				count = orderDao.insert(order);
			} else if (action.equals("orderUpdate")) {
				count = orderDao.update(order);
			}
			writeText(response, String.valueOf(count));
		} else if (action.equals("findByOrderId")) {
			int order_id = jsonObject.get("order_id").getAsInt();
			List<Order> orders = orderDao.findByOrderId(order_id);
			writeText(response, gson.toJson(orders));
		} else if (action.equals("findByMemId")) {
			int id = jsonObject.get("id").getAsInt();
			String type = jsonObject.get("type").getAsString();
			List<Order> orders = orderDao.findByCase(id, type);
			writeText(response, gson.toJson(orders));
		} else {
			writeText(response, "");
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		// 將輸出資料列印出來除錯用
		System.out.println("output: " + outText);
	}
}

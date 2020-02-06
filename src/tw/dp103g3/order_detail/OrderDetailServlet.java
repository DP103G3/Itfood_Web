package tw.dp103g3.order_detail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;



@WebServlet("/OrderDetailServlet")
public class OrderDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final static String CONTENT_TYPE = "text/html; charset=utf-8";
    OrderDetailDao orderDetailDao = null;
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("utf-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if (orderDetailDao == null) {
			orderDetailDao = new OrderDetailDaoMySqlImpl();
		}
		
		String action = jsonObject.get("action").getAsString();
		
		if (action.equals("orderDetailInsert") || action.equals("orderDetailUpdate")) {
			int count = 0;
			if (action.equals("orderDetailInsert")) {
				List<OrderDetail> orderDetails = new ArrayList<>();
				String orderDetailsJson = jsonObject.get("orderDetailsJson").getAsString();
				System.out.println("orderDetailsJson = " + orderDetailsJson);
				Type listType = new TypeToken<List<JsonObject>>() {}.getType();
				List<JsonObject> orderDetailsJsonList = gson.fromJson(orderDetailsJson, listType);
				for (JsonObject orderDetailJsonObject : orderDetailsJsonList) {
					int dish_id = orderDetailJsonObject.get("dish_id").getAsInt();
					int order_id =  orderDetailJsonObject.get("order_id").getAsInt();
					int od_count =  orderDetailJsonObject.get("od_count").getAsInt();
					int od_price =  orderDetailJsonObject.get("od_price").getAsInt();
					String od_message =  orderDetailJsonObject.get("od_message").getAsString();
					OrderDetail od = new OrderDetail(order_id, dish_id, od_count, od_price, od_message);
					orderDetails.add(od);
				}
				count = orderDetailDao.insert(orderDetails);
			} else if (action.equals("orderDetailUpdate")) {
				OrderDetail orderDetail = gson.fromJson(jsonObject.get("orderDetail").toString(), OrderDetail.class);
				count = orderDetailDao.update(orderDetail);
			}
			writeText(response, String.valueOf(count));
		} else if (action.equals("findByOrderId")) {
			int order_id = jsonObject.get("order_id").getAsInt();
			List<OrderDetail> orderDetails = orderDetailDao.findByOrderId(order_id);
			writeText(response, gson.toJson(orderDetails));
		}
		
		else {
			writeText(response, "");
		}
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		orderDetailDao = new OrderDetailDaoMySqlImpl();
		List<OrderDetail> orderDetails = orderDetailDao.findByOrderId(1);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		writeText(response, gson.toJson(orderDetails));
	}

	
	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		// 將輸出資料列印出來除錯用
		System.out.println("output: " + outText);
	}
}

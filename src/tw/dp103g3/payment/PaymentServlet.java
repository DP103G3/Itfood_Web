package tw.dp103g3.payment;

import static tw.dp103g3.main.Common.CONTENT_TYPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import tw.dp103g3.payment.PaymentDaoMySqlImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	PaymentDao paymentDao = null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		if(paymentDao == null) {
			paymentDao = new PaymentDaoMySqlImpl();
		}
		
		String action = jsonObject.get("action").getAsString();
		
		if (action.equals("insert") || action.equals("update")) {
			String paymentJson = jsonObject.get("payment").getAsString();
			System.out.println("paymentJson = " + paymentJson);
			Payment payment = gson.fromJson(paymentJson, Payment.class);
			
			int count = 0;
			if (action.equals("insert")) {
				count = paymentDao.insert(payment);
			} else if (action.equals("update")) {
				count = paymentDao.update(payment);
			}
			writeText(response, String.valueOf(count));
		} else if (action.equals("getByMemberId")) {
			int mem_id = jsonObject.get("mem_id").getAsInt();
			int state = jsonObject.get("state").getAsInt();
			List <Payment> payments = paymentDao.getByMemberId(mem_id, state);
			writeText(response, gson.toJson(payments));
		}
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		paymentDao = new PaymentDaoMySqlImpl();
		List<Payment> payments = paymentDao.getByMemberId(1, 1);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		writeText(response, gson.toJson(payments));
	}
	
	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		// 將輸出資料列印出來除錯用
		System.out.println("output: " + outText);
	}

}

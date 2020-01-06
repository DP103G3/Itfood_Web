package tw.dp103g3.address;

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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

@SuppressWarnings("serial")
@WebServlet("/AddressServlet")
public class AddressServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	private AddressDao addressDao = null;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = "";
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		System.out.println("input: " + jsonIn.toString());
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if (addressDao == null) {
			addressDao = new AddressDaoMysqlImpl();
		}
		String action = jsonObject.get("action").getAsString();
		int mem_id = 0;
		List<Address> addresses = null;
		Address address = null;
		int count = 0;
		switch (action) {
			case "getAllShow":
			case "getAll":
				mem_id = jsonObject.get("mem_id").getAsInt();
				if (action.equals("getAllShow")) {
					addresses = addressDao.getAllShow(mem_id);
				} else {
					addresses = addressDao.getAll(mem_id);
				}
				writeText(response, gson.toJson(addresses));
				break;
			case "insert":
			case "update":
				String jsonAdrs = jsonObject.get("address").getAsString();
				address = gson.fromJson(jsonAdrs, Address.class);
				if (action.equals("insert")) {
					count = addressDao.insert(address);
				} else {
					count = addressDao.update(address);
				}
				writeText(response, String.valueOf(count));
				break;
			default:
				writeText(response, "");
				break;
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (addressDao == null) {
			addressDao = new AddressDaoMysqlImpl();
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		List<Address> addresses = addressDao.getAll(1);
		writeText(response, gson.toJson(addresses));
	}
	
	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.write(outText);
		System.out.println("output: " + outText);
	}

}

package tw.dp103g3.delivery;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import tw.dp103g3.address.Address;
import tw.dp103g3.address.AddressDao;
import tw.dp103g3.address.AddressDaoMysqlImpl;
import tw.dp103g3.order.Order;
import tw.dp103g3.order.OrderDao;
import tw.dp103g3.order.OrderDaoMySqlImpl;
import tw.dp103g3.shop.Shop;
import tw.dp103g3.shop.ShopDao;
import tw.dp103g3.shop.ShopDaoMysqlImpl;

@ServerEndpoint(value = "/DeliverySocket/{user}")
public class DeliverySocket {
	private final String TAG = "TAG_DeliverySocket: ";
	private static Map<String, Session> sessionsMap = new ConcurrentHashMap<>();
	private static Map<Integer, AreaOrders> areaOrdersMap = new ConcurrentHashMap<>();

	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@OnOpen
	public void onOpen(@PathParam("user") String user, Session userSession) throws IOException {
		sessionsMap.put(user, userSession);

		System.out.println(TAG + "Socket open connection: " + user + " Session ID: " + userSession.getId());
	}

	@OnMessage
	synchronized public void onMessage(Session userSession, String deliveryMessageJson) {
		System.out.println(TAG + "received message: " + deliveryMessageJson);
		DeliveryMessage deliveryMessage = gson.fromJson(deliveryMessageJson, DeliveryMessage.class);
		int areaCode = deliveryMessage.getAreaCode();
		String action = deliveryMessage.getAction().trim();
		System.out.println(TAG + "DelvieryMessage: " + action + " AREA CODE" + areaCode);
		String sender = deliveryMessage.getSender().trim();
		Type orderSetType = new TypeToken<Set<Order>>(){}.getType();

		// MARK: 店家接單向Socket server 發送訊息
		if (action.equalsIgnoreCase("shopPublishOrder")) {
			if (!areaOrdersMap.containsKey(areaCode)) {
				addNewArea(areaCode);
			}
			Order order = deliveryMessage.getOrder();
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Set<String> shopUserStrings = areaOrders.getShopUserStrings();
			Set<Order> orders = areaOrders.getOrders();
			
			//將外送員id 設為-1 , 代表無人接單
			order.setDel_id(-1);
			
			ShopDao shopDao = new ShopDaoMysqlImpl();
			AddressDao addressDao = new AddressDaoMysqlImpl();
			OrderDao orderDao = new OrderDaoMySqlImpl();
			Shop shop = shopDao.getShopByIdDelivery(order.getShop().getId());
			Address address = addressDao.findById(order.getAddress().getId());
			order.setShop(shop);
			order.setAddress(address);
			
			orders.add(order);
			orderDao.update(order);
			
			shopUserStrings.add(sender);
			areaOrders.setOrders(orders);
			areaOrders.setShopUserStrings(shopUserStrings);
			areaOrdersMap.put(areaCode, areaOrders);

			Set<String> deliveryUserStrings = areaOrders.getDeliveryUserStrings();
			Set<String> users = sessionsMap.keySet();
			Set<Session> sessions = new HashSet<>();

			// 將 外送員Session 從 sessionsMap 中過濾出來
			for (String deliveryUserString : deliveryUserStrings) {
				if (users.contains(deliveryUserString)) {
					Session session = sessionsMap.get(deliveryUserString);
					if (session.isOpen()) {
						sessions.add(session);
					} else {
						sessionsMap.remove(deliveryUserString);
					}
				}
			}
			
			Session shopSession = sessionsMap.get(sender);
			if (shopSession.isOpen()) {
				shopSession.getAsyncRemote().sendText(gson.toJson(orders));
			} else {
				try {
					shopSession.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			AreaOrders newAreaOrders = areaOrdersMap.get(areaCode);
			Set<Order> newOrders = newAreaOrders.getOrders();
			
			// 向外送員送 orders
			for (Session session : sessions) {
				session.getAsyncRemote().sendText(gson.toJson(newOrders, orderSetType).toString());
			}
			System.out.println(TAG + "PUBLISHED ORDERS: " + gson.toJson(newOrders).toString());
		}
		else if (action.equalsIgnoreCase("shopDishDone")) {
			System.out.println("BREAK POINT 1");
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Order order = deliveryMessage.getOrder();
			String receiver = deliveryMessage.getReceiver().trim();
			Set<Order> orders = areaOrders.getOrders();
			Set<Order> newOrders = new HashSet<>();
			System.out.println("BREAK POINT 2");

			//replace old order
			for (Order e : orders) {
				if (e.getOrder_id() != order.getOrder_id()) {
					newOrders.add(e);
				}
			}
			newOrders.add(order);
			
			System.out.println("BREAK POINT 3");

			//update server data
			areaOrders.setOrders(newOrders);
			areaOrdersMap.put(areaCode, areaOrders);
			System.out.println("BREAK POINT 4");

			//send message to delivery
			Set<Order> delOrders = areaOrdersMap.get(areaCode).getOrders();
			Session delSession = sessionsMap.get(receiver);
			if (delSession.isOpen() && delSession != null) {
				String ordersJson = gson.toJson(delOrders, orderSetType);
				delSession.getAsyncRemote().sendText(ordersJson);
			} else {
				sessionsMap.remove(receiver);
			}
			System.out.println("BREAK POINT 5");

			
		} 
		//MARK: 店家確認取餐	
		else if (action.equalsIgnoreCase("shopConfirmOrder")) {
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Order order = deliveryMessage.getOrder();
			String receiver = deliveryMessage.getReceiver().trim();
			Set<Order> orders = areaOrders.getOrders();
			Set<Order> newOrders = new HashSet<>();
			
			//replace old order
			for (Order e : orders) {
				if (e.getOrder_id() != order.getOrder_id()) {
					newOrders.add(e);
				}
			}
			newOrders.add(order);
			//update server data
			areaOrders.setOrders(newOrders);
			areaOrdersMap.put(areaCode, areaOrders);
			
			//send message to delivery
			Session delSession = sessionsMap.get(receiver);
			if (delSession.isOpen()) {
				DeliveryMessage dm = new DeliveryMessage("confirmOrder", order, order.getOrder_area(), "", "");
				String dmJson = gson.toJson(dm, DeliveryMessage.class).toString();
				delSession.getAsyncRemote().sendText(dmJson);
			} else {
				sessionsMap.remove(receiver);
			}
		}
		// MARK: 外送員提取 orders
		else if (action.equalsIgnoreCase("deliveryFetchOrders")) {
			if (!areaOrdersMap.containsKey(areaCode)) {
				addNewArea(areaCode);
			}
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Set<String> deliveryUserStrings = areaOrders.getDeliveryUserStrings();
			if (!deliveryUserStrings.contains(sender)) {
				deliveryUserStrings.add(sender);
				areaOrders.setDeliveryUserStrings(deliveryUserStrings);
				areaOrdersMap.put(areaCode, areaOrders);
			}

			Set<Order> orders = areaOrders.getOrders();
			
			Session session = sessionsMap.get(sender);
			session.getAsyncRemote().sendText(gson.toJson(orders, orderSetType).toString());

			System.out.println(TAG + "FETCH ORDERS " + gson.toJson(orders).toString());
		}
		// MARK: 外送員接單
		else if (action.equalsIgnoreCase("deliveryAcceptOrder")) {
			OrderDao orderDao = new OrderDaoMySqlImpl();
			System.out.println(TAG + "DELIVERY ACCEPT ORDER START");
			Order order = deliveryMessage.getOrder();
			String receiver = deliveryMessage.getReceiver().trim();
			
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			
			String[] splitedSender = sender.split("del");

			// 將訂單外送員id填入
			int del_id = Integer.valueOf(splitedSender[1]);
			order.setDel_id(del_id);
			orderDao.update(order);
			
			Set<Order> orders = areaOrders.getOrders();
			Set<Order> newOrders = new HashSet<>();
			// 將舊訂單移除，加入更新後的訂單
			for (Order element : orders) {
				if (element.getOrder_id() != order.getOrder_id()) {
					newOrders.add(element);
				}
			}
			newOrders.add(order);
			
			areaOrders.setOrders(newOrders);
			// 將areaOrdersMap更新
			areaOrdersMap.put(areaCode, areaOrders);
			String ordersJson = gson.toJson(newOrders, orderSetType);

			// 向店家傳送接單消息
			Session shopSession = sessionsMap.get(receiver);
			if (shopSession.isOpen()) {
				shopSession.getAsyncRemote().sendText(ordersJson);
			} else {
				sessionsMap.remove(receiver);
			}
			// 向外送員傳送接單成功
			Session delSession = sessionsMap.get(sender);
			if (delSession.isOpen()) {
				AreaOrders delAreaOrders = areaOrdersMap.get(areaCode);
				Set<Order> orderSet = delAreaOrders.getOrders();
				String delOrdersJson = gson.toJson(orderSet, orderSetType);
				delSession.getAsyncRemote().sendText(delOrdersJson);
				System.out.println("DELIVERY ACCEPT ORDER: " + delOrdersJson);
			} else {
				sessionsMap.remove(sender);
			}
			
			System.out.println(TAG + "DELIVERY ACCEPT ORDER SUCCESS");
		}
		else if (action.equalsIgnoreCase("deliveryCompleteOrder")) {
			OrderDao orderDao = new OrderDaoMySqlImpl();
			System.out.println(TAG + "DELIVERY COMPLETE ORDER START");
			Order order = deliveryMessage.getOrder();
			String receiver = deliveryMessage.getReceiver().trim();
			
			order.setOrder_state(4);
			orderDao.update(order);
			
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Set<Order> orders = areaOrders.getOrders();
			for (Order o : orders) {
				if (o.getOrder_id() == order.getOrder_id()) {
					orders.remove(o);
				}
			}
			areaOrders.setOrders(orders);
			areaOrdersMap.put(areaCode, areaOrders);
			
			Session delSession = sessionsMap.get(sender);
			if (delSession.isOpen()) {
				AreaOrders ao = areaOrdersMap.get(areaCode);
				Set<Order> od = ao.getOrders();
				String odJson = gson.toJson(od, orderSetType);
				delSession.getAsyncRemote().sendText(odJson);
			} else {
				sessionsMap.remove(sender);
			}
		}

		else {
			System.out.println(TAG + "LOGIC ERROR");
		}
		System.out.println(TAG + "CURRENT AreaOrdersMap: " + gson.toJson("Ared Code: " + areaCode + "," + "Area Map: " + areaOrdersMap.get(areaCode)).toString());

	}

	@OnError
	public void onError(Session userSession, Throwable e) {
		System.out.println(TAG + "Error:" + e.toString());
	}

	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		Set<String> userNames = sessionsMap.keySet();
		for (String userName : userNames) {
			if (sessionsMap.get(userName).equals(userSession)) {
				sessionsMap.remove(userName);
				System.out.println(TAG + "Close connection with: " + userName);
				break;
			}
		}

	}

	private void addNewArea(int areaCode) {
		AreaOrders areaOrders = new AreaOrders();
		Set<Order> orders = new HashSet<>();
		Set<String> shopUserStrings = new HashSet<>();
		Set<String> deliveryUserStrings = new HashSet<>();
		areaOrders.setDeliveryUserStrings(deliveryUserStrings);
		areaOrders.setShopUserStrings(shopUserStrings);
		areaOrders.setOrders(orders);

		areaOrdersMap.put(areaCode, areaOrders);
	}

//	private Order convertOrderToDeliveryType(Order order) {
//		ShopDao shopDao = new ShopDaoMysqlImpl();
//		AddressDao addressDao = new AddressDaoMysqlImpl();
//
//		// 如果 order 已經轉換為外送員類型的order, 就回傳原值
//		Address address = null;
//		address = order.getAddress();
//		if (address != null) {
//			return order;
//		}
//
//		int adrs_id = order.getAddress().getId();
//		
//		Shop shop = order.getShop();
//		int shop_id = shop.getId();
//		Shop delShop = shopDao.getShopByIdDelivery(shop_id);
//		address = addressDao.findById(adrs_id);
//		Order delOrder = new Order(order.getOrder_id(), delShop, order.getMem_id(), order.getDel_id(),
//				order.getPay_id(), order.getSp_id(), order.getOrder_ideal(), order.getOrder_time(),
//				order.getOrder_delivery(), address, order.getOrder_name(), order.getOrder_phone(),
//				order.getOrder_ttprice(), order.getOrder_area(), order.getOrder_state(), order.getOrder_type(),
//				order.getOrderDetails());
//		return delOrder;
//	}

//	private Order convertOrderToNormalType(Order order) {
//		int adrs_id = order.getAddress().getId();
//		Order newOrder = new Order(order.getOrder_id(), order.getShop(), order.getMem_id(), order.getDel_id(),
//				order.getPay_id(), order.getSp_id(), order.getOrder_ideal(), order.getOrder_time(),
//				order.getOrder_delivery(), adrs_id, order.getOrder_name(), order.getOrder_phone(),
//				order.getOrder_ttprice(), order.getOrder_area(), order.getOrder_state(), order.getOrder_type(),
//				order.getOrderDetails());
//		return newOrder;
//	}
	

}

package tw.dp103g3.delivery;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

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
	private OrderDao orderDao = new OrderDaoMySqlImpl();
	private ShopDao shopDao = new ShopDaoMysqlImpl();
	private AddressDao addressDao = new AddressDaoMysqlImpl();

	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@OnOpen
	public void onOpen(@PathParam("user") String user, Session userSession) throws IOException {
		sessionsMap.put(user, userSession);

		System.out.println(TAG + "Socket open connection: " + user + " Session ID: " + userSession.getId());
			fetchOrdersFromDataBase();
	}

	@OnMessage
	synchronized public void onMessage(Session userSession, String deliveryMessageJson) {
		System.out.println(TAG + "received message: " + deliveryMessageJson);
		DeliveryMessage deliveryMessage = gson.fromJson(deliveryMessageJson, DeliveryMessage.class);
		int areaCode = deliveryMessage.getAreaCode();
		String action = deliveryMessage.getAction().trim();
		System.out.println(TAG + "DelvieryMessage: " + action + " AREA CODE" + areaCode);
		String sender = deliveryMessage.getSender().trim();
		Type orderSetType = new TypeToken<Set<Order>>() {
		}.getType();

		// MARK: 店家接單向Socket server 發送訊息
		if (action.equalsIgnoreCase("shopPublishOrder")) {
			if (!areaOrdersMap.containsKey(areaCode)) {
				addNewArea(areaCode);
			}
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT1");
			Order order = deliveryMessage.getOrder();
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Set<String> shopUserStrings = areaOrders.getShopUserStrings();
			Set<Order> orders = areaOrders.getOrders();
			if (orders.isEmpty()) {
				orders = new HashSet<>();
			}
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT2");
			// 將外送員id 設為-1 , 代表無人接單
			order.setDel_id(-1);
			Shop shop = order.getShop();
			Address address = order.getAddress();
			shop = shopDao.getShopByIdDelivery(shop.getId());
			address = addressDao.findById(address.getId());
			
			order.setShop(shop);
			order.setAddress(address);
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT3");

			orders.add(order);
			orderDao.update(order);
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT4");
			
			if (shopUserStrings == null) {
				shopUserStrings = new HashSet<String>();
			}
			shopUserStrings.add(sender);
			areaOrders.setOrders(orders);
			areaOrders.setShopUserStrings(shopUserStrings);
			areaOrdersMap.put(areaCode, areaOrders);
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT5");

			Set<String> deliveryUserStrings = areaOrders.getDeliveryUserStrings();
			Set<String> users = sessionsMap.keySet();
			Set<Session> sessions = new HashSet<>();
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT6");

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
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT7");
			Session shopSession = sessionsMap.get(sender);
			if (shopSession != null && shopSession.isOpen()) {
				shopSession.getAsyncRemote().sendText(gson.toJson(orders).toString());
			} else {
				try {
					shopSession.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT8");

			AreaOrders newAreaOrders = areaOrdersMap.get(areaCode);
			Set<Order> newOrders = newAreaOrders.getOrders();
			System.out.println(TAG + "PUBLISH ORDERS BREAKPOINT9");
			// 向外送員送 orders
			for (Session session : sessions) {
				if (session != null && session.isOpen()) {
				session.getAsyncRemote().sendText(gson.toJson(newOrders, orderSetType).toString());
				} 
			}
			System.out.println(TAG + "PUBLISHED ORDERS: " + gson.toJson(newOrders).toString());
		}
		// MARK: 外送員提取 orders
		else if (action.equalsIgnoreCase("deliveryFetchOrders")) {
			if (!areaOrdersMap.containsKey(areaCode)) {
				addNewArea(areaCode);
			}
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Set<String> deliveryUserStrings = areaOrders.getDeliveryUserStrings();
			if (!deliveryUserStrings.contains(sender)) {
				if (deliveryUserStrings == null) {
					deliveryUserStrings = new HashSet<String>();
				}
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
			Shop shop = order.getShop();
			Address address = order.getAddress();
			shop = shopDao.getShopByIdDelivery(shop.getId());
			address = addressDao.findById(address.getId());
			order.setShop(shop);
			order.setAddress(address);
			newOrders.add(order);

			areaOrders.setOrders(newOrders);
			// 將areaOrdersMap更新
			areaOrdersMap.put(areaCode, areaOrders);
			String ordersJson = gson.toJson(newOrders, orderSetType);

			// 向店家傳送接單消息
			Session shopSession = sessionsMap.get(receiver);
			if (shopSession != null && shopSession.isOpen()) {
				shopSession.getAsyncRemote().sendText(ordersJson.toString());
			} else {
				sessionsMap.remove(receiver);
			}
			// 向外送員傳送接單成功
			Session delSession = sessionsMap.get(sender);
			if (delSession != null && delSession.isOpen()) {
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
		// MARK: 店家餐點製作完成
		else if (action.equalsIgnoreCase("shopDishDone")) {
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Order order = deliveryMessage.getOrder();
			String receiver = deliveryMessage.getReceiver().trim();
			Set<Order> orders = areaOrders.getOrders();
			Set<Order> newOrders = new HashSet<>();

			// replace old order
			for (Order e : orders) {
				if (e.getOrder_id() != order.getOrder_id()) {
					newOrders.add(e);
				}
			}
			Shop shop = order.getShop();
			Address address = order.getAddress();
			shop = shopDao.getShopByIdDelivery(shop.getId());
			address = addressDao.findById(address.getId());
				order.setAddress(address);
				order.setShop(shop);
			
			newOrders.add(order);
			
			// update server data
			areaOrders.setOrders(newOrders);
			areaOrdersMap.put(areaCode, areaOrders);

			// send message to delivery
			Set<Order> delOrders = areaOrdersMap.get(areaCode).getOrders();
			Session delSession = sessionsMap.get(receiver);
			if (delSession != null && delSession.isOpen()) {
				String ordersJson = gson.toJson(delOrders, orderSetType);
				delSession.getAsyncRemote().sendText(ordersJson);
				System.out.println(TAG + "send to: " + receiver + ", " + ordersJson);
			} else {
				sessionsMap.remove(receiver);
			}

		}

		// MARK: 店家確認取餐
		else if (action.equalsIgnoreCase("shopConfirmOrder")) {
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Order order = deliveryMessage.getOrder();
			String receiver = deliveryMessage.getReceiver().trim();
			Set<Order> orders = areaOrders.getOrders();
			Set<Order> newOrders = new HashSet<>();

			// replace old order
			for (Order e : orders) {
				if (e.getOrder_id() != order.getOrder_id()) {
					newOrders.add(e);
				}
			}
			Shop shop = order.getShop();
			Address address = order.getAddress();
			shop = shopDao.getShopByIdDelivery(shop.getId());
			address = addressDao.findById(address.getId());
			order.setShop(shop);
			order.setAddress(address);
			newOrders.add(order);
			// update server data
			areaOrders.setOrders(newOrders);
			areaOrdersMap.put(areaCode, areaOrders);

			// send message to delivery
			Session delSession = sessionsMap.get(receiver);
			if (delSession != null && delSession.isOpen()) {
				DeliveryMessage dm = new DeliveryMessage("confirmOrder", order, order.getOrder_area(), "", "");
				String dmJson = gson.toJson(dm, DeliveryMessage.class).toString();
				delSession.getAsyncRemote().sendText(dmJson);
			} else {
				sessionsMap.remove(receiver);
			}
		}

		else if (action.equalsIgnoreCase("deliveryCompleteOrder")) {
			System.out.println("DELIVERY COMPLETE ORDER");
			System.out.println(TAG + "DELIVERY COMPLETE ORDER START");
			Order order = deliveryMessage.getOrder();
			String receiver = deliveryMessage.getReceiver().trim();
			
			Calendar cal = Calendar.getInstance();
			order.setOrder_state(4);
			order.setOrder_delivery(cal.getTime());
			orderDao.update(order);

			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Set<Order> orders = areaOrders.getOrders();
			Set<Order> newOrders = new HashSet<>();
			for (Order o : orders) {
				if (o.getOrder_id() != order.getOrder_id()) {
					newOrders.add(o);
				}
			}
			
			newOrders.add(order);
			areaOrders.setOrders(newOrders);
			areaOrdersMap.put(areaCode, areaOrders);

			Session delSession = sessionsMap.get(sender);
			if (delSession != null && delSession.isOpen()) {
				AreaOrders ao = areaOrdersMap.get(areaCode);
				Set<Order> od = ao.getOrders();
				String odJson = gson.toJson(od, orderSetType);
				delSession.getAsyncRemote().sendText(odJson);
			} else {
				sessionsMap.remove(sender);
			}
			
			Session memberSession = sessionsMap.get(receiver);
			if (memberSession!= null && memberSession.isOpen()) {
				DeliveryMessage dm = new DeliveryMessage("deliveryCompleteOrder", order, order.getOrder_area(), sender, receiver);
				String dmString = gson.toJson(dm, DeliveryMessage.class);
				memberSession.getAsyncRemote().sendText(dmString);
			} else {
				sessionsMap.remove(receiver);
			}
		}

		else {
			System.out.println(TAG + "LOGIC ERROR");
		}
		System.out.println(TAG + "CURRENT AreaOrdersMap: "
				+ gson.toJson("Ared Code: " + areaCode ));
		System.out.println("DELIVERYS: " +  gson.toJson(areaOrdersMap.get(areaCode).getDeliveryUserStrings()).toString());
		System.out.println("SHOPS: " +  gson.toJson(areaOrdersMap.get(areaCode).getShopUserStrings()).toString());

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
	
	private void fetchOrdersFromDataBase() {
		Set<Order> orderSet = orderDao.getAllDeliveryingOrder();
		Map<Integer, Set<Order>> ordersMap = new HashMap<>();
		for (Order order : orderSet) {
			int areaCode = order.getOrder_area();
			if (!ordersMap.containsKey(areaCode)) {
				Set<Order> areaOrdersSet = new HashSet<>();
				ordersMap.put(areaCode, areaOrdersSet);
			} else {
				Set<Order> areaOrdersSet = ordersMap.get(areaCode);
				areaOrdersSet.add(order);
				ordersMap.put(areaCode, areaOrdersSet);
			}
		}
		ordersMap.forEach(new BiConsumer<Integer, Set<Order>>(){
			@Override
			public void accept(Integer t, Set<Order> u) {
				if (!areaOrdersMap.containsKey(t)) {
					AreaOrders areaOrders = new AreaOrders();
					Set<String> delUsers = new HashSet<>();
					Set<String> shopUsers = new HashSet<>();
					areaOrders.setOrders(u);
					areaOrders.setDeliveryUserStrings(delUsers);
					areaOrders.setDeliveryUserStrings(shopUsers);
					areaOrdersMap.put(t, areaOrders);
				} 
//				else {
//					AreaOrders areaOrders = areaOrdersMap.get(t);
//					Set<Order> orders = new HashSet<>();
//					orders.addAll(u);
//					areaOrders.setOrders(orders);
//				}
			}
			
		});
		System.out.println("FETCHED FROM DATABASE: " + areaOrdersMap.toString());
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

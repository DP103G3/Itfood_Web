package tw.dp103g3.delivery;

import java.io.IOException;
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
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	public void onMessage(Session userSession, String deliveryMessageJson) {
		System.out.println(TAG + "received message: " + deliveryMessageJson);
		DeliveryMessage deliveryMessage = gson.fromJson(deliveryMessageJson, DeliveryMessage.class);
		int areaCode = deliveryMessage.getAreaCode();
		String action = deliveryMessage.getAction().trim();
		System.out.println(TAG + "DelvieryMessage: " + action + " AREA CODE" + areaCode);

		String sender = deliveryMessage.getSender().trim();

		// MARK: 店家接單向Socket server 發送訊息
		if (action.equalsIgnoreCase("shopPublishOrder")) {
			Order order = deliveryMessage.getOrder();
			if (!areaOrdersMap.containsKey(areaCode)) {
				addNewArea(areaCode);
			}
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			Set<String> shopUserStrings = areaOrders.getShopUserStrings();
			Set<Order> orders = areaOrders.getOrders();
			orders.add(order);
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
			Set<Order> delOrders = new HashSet<>();
			for (Order element : areaOrders.getOrders()) {
				delOrders.add(convertOrderToDeliveryType(element));
			}
			areaOrders.setOrders(delOrders);
			// 向外送員送 areaOrders
			for (Session session : sessions) {
				session.getAsyncRemote().sendText(gson.toJson(areaOrders).toString());
			}
			System.out.println(TAG + "PUBLISHED ORDERS: " + gson.toJson(areaOrders).toString());

		}
		// MARK: 外送員提取 areaOrders
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

			// 將訂單轉為包含外送員所需資訊的類型
			Set<Order> orders = areaOrders.getOrders();
			Set<Order> convertedOrders = new HashSet<>();
			for (Order order : orders) {
				convertedOrders.add(convertOrderToDeliveryType(order));
			}
			AreaOrders delAreaOrders = areaOrdersMap.get(areaCode);
			delAreaOrders.setOrders(convertedOrders);
			Session session = sessionsMap.get(sender);
			session.getAsyncRemote().sendText(gson.toJson(delAreaOrders).toString());

			System.out.println(TAG + "FETCH ORDERS " + gson.toJson(areaOrders).toString());
		}
		// MARK: 外送員接單
		else if (action.equalsIgnoreCase("deliveryAcceptOrder")) {
			System.out.println(TAG + "DELIVERY ACCEPT ORDER START");
			String receiver = deliveryMessage.getReceiver();
			System.out.println(TAG + areaOrdersMap.toString());
			sessionsMap.forEach(new BiConsumer<String, Session>() {

				@Override
				public void accept(String k, Session v) {
					// TODO Auto-generated method stub
					System.out.println(TAG + k + " : " + v.getId());
				}
				
			});
			Order order = deliveryMessage.getOrder();
			OrderDao orderDao= new OrderDaoMySqlImpl();
			// 將訂單轉為正常型態
			order = convertOrderToNormalType(order);
			AreaOrders areaOrders = areaOrdersMap.get(areaCode);
			// 將訂單狀態改為外送中
			order.setOrder_state(3);
			System.out.println(TAG + "BREAKPOINT1" );
			String[] splitedSender = sender.split("del");

			// 將訂單外送員id填入
			int del_id = Integer.valueOf(splitedSender[1]);
			order.setDel_id(del_id);
			
			orderDao.update(order);
			
			Set<Order> orders = areaOrders.getOrders();
			System.out.println(TAG + "BREAKPOINT2" );
			// 將舊訂單移除，加入更新後的訂單
			for (Order element : orders) {
				if (element.getOrder_id() == order.getOrder_id()) {
					orders.remove(element);
					orders.add(order);
				}
			}
			
			areaOrders.setOrders(orders);

			// 將areaOrdersMap更新
			areaOrdersMap.put(areaCode, areaOrders);
			System.out.println(TAG + "BREAKPOINT3" );
			String areaOrdersJson = gson.toJson(areaOrders, AreaOrders.class);

			// 向店家傳送接單消息
			Session shopSession = sessionsMap.get(receiver);
			if (shopSession.isOpen()) {
				shopSession.getAsyncRemote().sendText(areaOrdersJson);
			} else {
				sessionsMap.remove(receiver);
			}
			System.out.println(TAG + "BREAKPOINT4" );
			// 向外送員傳送接單成功
			Session delSession = sessionsMap.get(sender);
			if (delSession.isOpen()) {
				AreaOrders delAreaOrders = areaOrdersMap.get(areaCode);
				System.out.println(TAG + "BREAKPOINT5" );
				Set<Order> oldOrderSet = delAreaOrders.getOrders();
				Set<Order> newOrderSet = new HashSet<>();
				for (Order e : oldOrderSet) {
					newOrderSet.add(convertOrderToDeliveryType(e));
				}
				System.out.println(TAG + "BREAKPOINT5" );
				delAreaOrders.setOrders(newOrderSet);
				String delAreaOrdersJson = gson.toJson(delAreaOrders, AreaOrders.class);
				delSession.getAsyncRemote().sendText(delAreaOrdersJson);
				System.out.println(TAG + "BREAKPOINT6" );
			} else {
				sessionsMap.remove(sender);
			}
			System.out.println(TAG + "DELIVERY ACCEPT ORDER SUCCESS");
		}

		else {
			System.out.println(TAG + "LOGIC ERROR");
		}

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

	private Order convertOrderToDeliveryType(Order order) {
		ShopDao shopDao = new ShopDaoMysqlImpl();
		AddressDao addressDao = new AddressDaoMysqlImpl();

		// 如果 order 已經轉換為外送員類型的order, 就回傳原值
		Address address = null;
		address = order.getAddress();
		if (address != null) {
			return order;
		}

		int adrs_id = order.getAdrs_id();
		Shop shop = order.getShop();
		int shop_id = shop.getId();
		Shop delShop = shopDao.getShopByIdDelivery(shop_id);
		address = addressDao.findById(adrs_id);
		Order delOrder = new Order(order.getOrder_id(), delShop, order.getMem_id(), order.getDel_id(),
				order.getPay_id(), order.getSp_id(), order.getOrder_ideal(), order.getOrder_time(),
				order.getOrder_delivery(), address, order.getOrder_name(), order.getOrder_phone(),
				order.getOrder_ttprice(), order.getOrder_area(), order.getOrder_state(), order.getOrder_type(),
				order.getOrderDetails());
		return delOrder;
	}

	private Order convertOrderToNormalType(Order order) {
		int adrs_id = order.getAddress().getId();
		Order newOrder = new Order(order.getOrder_id(), order.getShop(), order.getMem_id(), order.getDel_id(),
				order.getPay_id(), order.getSp_id(), order.getOrder_ideal(), order.getOrder_time(),
				order.getOrder_delivery(), adrs_id, order.getOrder_name(), order.getOrder_phone(),
				order.getOrder_ttprice(), order.getOrder_area(), order.getOrder_state(), order.getOrder_type(),
				order.getOrderDetails());
		return newOrder;
	}

}

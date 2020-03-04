package tw.dp103g3.delivery;

import java.util.Set;

import tw.dp103g3.order.Order;

public class AreaOrders {
	private Set<Order> orders;
	private Set<String> shopUserStrings;
	private Set<String> deliveryUserStrings;
	public Set<Order> getOrders() {
		return orders;
	}
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}
	public Set<String> getShopUserStrings() {
		return shopUserStrings;
	}
	public void setShopUserStrings(Set<String> shopUserStrings) {
		this.shopUserStrings = shopUserStrings;
	}
	public Set<String> getDeliveryUserStrings() {
		return deliveryUserStrings;
	}
	public void setDeliveryUserStrings(Set<String> deliveryUserStrings) {
		this.deliveryUserStrings = deliveryUserStrings;
	}
	/**
	 * @param orders
	 * @param shopUserStrings
	 * @param deliveryUserStrings
	 */
	public AreaOrders(Set<Order> orders, Set<String> shopUserStrings, Set<String> deliveryUserStrings) {
		super();
		this.orders = orders;
		this.shopUserStrings = shopUserStrings;
		this.deliveryUserStrings = deliveryUserStrings;
	}
	
	public AreaOrders() {
		
	}
	
	

	
}

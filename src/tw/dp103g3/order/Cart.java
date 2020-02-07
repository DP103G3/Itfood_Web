package tw.dp103g3.order;

import java.util.List;

import tw.dp103g3.address.Address;
import tw.dp103g3.dish.Dish;
import tw.dp103g3.member.Member;
import tw.dp103g3.payment.Payment;

public class Cart {
	private List<Dish> dishes;
	private Member member;
	private List<Payment> payments;
	private List<Address> addresses;
	
	public List<Dish> getDishes() {
		return dishes;
	}
	public void setDishes(List<Dish> dishes) {
		this.dishes = dishes;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public List<Payment> getPayments() {
		return payments;
	}
	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	
	public Cart() {
		// TODO Auto-generated constructor stub
	}
	public Cart(List<Dish> dishes, Member member, List<Payment> payments, List<Address> addresses) {
		super();
		this.dishes = dishes;
		this.member = member;
		this.payments = payments;
		this.addresses = addresses;
	}

}

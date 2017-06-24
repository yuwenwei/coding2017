package com.coderising.ood.srp.goodSrp;

import java.util.List;
import java.util.stream.Collectors;

public class Mail {

	private User user;
	
	public Mail(User u){
		this.user = u;	
	}
	public String getAddress(){
		return user.getEMailAddress();
	}
	public String getSubject(){
		return "您关注的产品降价了";
	}
	public String getBody(){
		
		return "尊敬的 "+user.getName()+", 您关注的产品 " + this.buildProductDescList() + " 降价了，欢迎购买!" ;		
	}
	private String buildProductDescList() {
		List<Product> products = user.getSubscribedProducts();
		//.... 实现略...
		return products.stream().map(Object::toString)
				.collect(Collectors.joining(", "));
	}
}

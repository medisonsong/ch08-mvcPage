package kr.cart.vo;

import java.sql.Date;

import kr.item.vo.ItemVO;

public class CartVO {
	private int cart_num;		//장바구니 번호
	private int item_num;		//상품 번호
	private int order_quantity;	//주문 수량
	private Date reg_date;		//장바구니 등록일
	private int mem_num;		//장바구니에 상품을 담은 회원번호
	private int sub_total;		//단일 상품의 총구매 비용
	
	//상품번호를 가져오긴 했지만 detail 정보를 좀더 활용하기 위해서 ItemVO를 불러올거임
	private ItemVO itemVO; //상품관리 VO

	
	public int getCart_num() {
		return cart_num;
	}
	public void setCart_num(int cart_num) {
		this.cart_num = cart_num;
	}
	public int getItem_num() {
		return item_num;
	}
	public void setItem_num(int item_num) {
		this.item_num = item_num;
	}
	public int getOrder_quantity() {
		return order_quantity;
	}
	public void setOrder_quantity(int order_quantity) {
		this.order_quantity = order_quantity;
	}
	public Date getReg_date() {
		return reg_date;
	}
	public void setReg_date(Date reg_date) {
		this.reg_date = reg_date;
	}
	public int getMem_num() {
		return mem_num;
	}
	public void setMem_num(int mem_num) {
		this.mem_num = mem_num;
	}
	public int getSub_total() {
		return sub_total;
	}
	public void setSub_total(int sub_total) {
		this.sub_total = sub_total;
	}
	public ItemVO getItemVO() {
		return itemVO;
	}
	public void setItemVO(ItemVO itemVO) {
		this.itemVO = itemVO;
	}
}

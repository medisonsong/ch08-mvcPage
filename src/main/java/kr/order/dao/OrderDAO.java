package kr.order.dao;

public class OrderDAO {
	//싱글턴 패턴
	private static OrderDAO instance = new OrderDAO();
	
	public static OrderDAO getInstance() {
		return instance;
	}
	
	private OrderDAO() {}
	
	//주문 등록
	//관리자 - 전체 주문 개수/검색 주문 개수
	//관리자 - 전체 주문 목록/검색 주문 목록
	//사용자 - 전체 주문 개수/검색 주문 개수
	//사용자 - 전체 주문 목록/검색 주문 목록
	//사용자 - 주문 취소
	//개별 상품 목록
	//주문 삭제(삭제시 재고를 원상복귀 시키지 않음, 취소일 때 원상복귀)
	//관리자/사용자 주문상세
	//관리자/사용자 주문수정
	
}

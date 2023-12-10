package kr.order.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.cart.dao.CartDAO;
import kr.cart.vo.CartVO;
import kr.controller.Action;
import kr.item.dao.ItemDAO;
import kr.item.vo.ItemVO;

public class UserOrderFormAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//오동작 안하기 위해 조건체크 많이함
		//로그인 체크
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num == null) {
			return "redirect:/member/loginForm.do";
		}
	
		//호출-post방식만 접근 허용(장바구니에서 넘어오도록 유도)
		if(request.getMethod().toUpperCase().equals("GET")) {
			return "redirect:/item/itemList.do";
		}
		
		CartDAO dao = CartDAO.getInstance();
		//총 주문 금액 
		int all_total = dao.getTotalByMem_num(user_num);
		if(all_total <= 0) { //총 주문 금액이 0 이하일 경우(이중 주문을 막기 위해)
			request.setAttribute("notice_msg", "정상적인 주문이 아니거나 상품의 수량이 부족합니다");
			request.setAttribute("notice_url", request.getContextPath()+"/item/itemList.do");
			return "/WEB-INF/views/common/alert_singleView.jsp"; //스크립트로 alert창 띄우기
		}
		
		//장바구니에 담겨있는 상품정보 호출
		List<CartVO> cartList = dao.getListCart(user_num); //로그인 한 사람의 회원번호 넣기
		ItemDAO itemDao = ItemDAO.getInstance();
		for(CartVO cart : cartList) { //표시/미표시, 재고 수 확인을 위함
			ItemVO item = itemDao.getItem(cart.getItem_num());
			
			if(item.getStatus() == 1) {
				//상품 미표시인 경우
				request.setAttribute("notice_msg", "[" + item.getName() + "]상품 판매 금지");
				request.setAttribute("notice_url", request.getContextPath()+"/cart/list.do");
				return "/WEB-INF/views/common/alert_singleView.jsp";
			}
			if(item.getQuantity() < cart.getOrder_quantity()) {
				//상품 재고 수량이 부족한 경우
				request.setAttribute("notice_msg", "[" + item.getName() + "]재고 수량 부족으로 주문 불가");
				request.setAttribute("notice_url", request.getContextPath()+"/cart/list.do");
				return "/WEB-INF/views/common/alert_singleView.jsp";
			}
		}
		
		request.setAttribute("list", cartList);
		request.setAttribute("all_total", all_total);
		
		return "/WEB-INF/views/order/user_orderForm.jsp";
	}

}

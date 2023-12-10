package kr.cart.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;

import kr.cart.dao.CartDAO;
import kr.cart.vo.CartVO;
import kr.controller.Action;
import kr.item.dao.ItemDAO;
import kr.item.vo.ItemVO;

public class WriteAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> mapAjax = new HashMap<String,String>();
		
		//로그인 체크
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num == null) { //로그인이 되지 않은 경우
			mapAjax.put("result", "logout");
		}else {
			//전송된 데이터 인코딩 처리
			request.setCharacterEncoding("utf-8");
			//자바빈(VO) 생성
			CartVO cart = new CartVO();
			cart.setItem_num(Integer.parseInt(request.getParameter("item_num")));
			cart.setOrder_quantity(Integer.parseInt(request.getParameter("order_quantity")));
			cart.setMem_num(user_num);
			
			//동일 상품인지 아닌지 체크(동일 상품이 아닐 경우 (insert), 동일 상품일 경우 (update))
			CartDAO dao = CartDAO.getInstance();
			CartVO db_cart = dao.getCart(cart);
			if(db_cart == null) { //동일 상품이 없을 경우(insert)
				dao.insertCart(cart);
				mapAjax.put("result","success");
			}else { //동일 상품이 있을 경우(재고수 확인 후 update)
				//재고수를 구하기 위해서 ItemDAO 호출
				ItemDAO itemDao = ItemDAO.getInstance();
				ItemVO item = itemDao.getItem(db_cart.getItem_num());
				
				//구매 수량 합산(기존 장바구니에 저장된 구매 수량 + 새로 입력한 구매 수량 합산)
				int order_quantity = db_cart.getOrder_quantity() + cart.getOrder_quantity();
				if(item.getQuantity() < order_quantity) { 
					//상품 재고 수량보다 장바구니에 담은 구매 수량이 더 많은 경우
					mapAjax.put("result", "overquantity");
				}else { //구매 수량 수정 가능
					cart.setOrder_quantity(order_quantity);
					//구매 수량 수정
					dao.updateCartByItem_num(cart);
					mapAjax.put("result", "success");
				}
			}
		}
		
		//JSON문자열 생성
		ObjectMapper mapper = new ObjectMapper();
		String ajaxData = mapper.writeValueAsString(mapAjax);
		
		request.setAttribute("ajaxData", ajaxData);
		
		return "/WEB-INF/views/common/ajax_view.jsp";
	}

}

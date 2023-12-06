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

public class WriteAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> mapAjax = new HashMap<String,String>();
		
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num==null) {//로그인이 되지 않은 경우
			mapAjax.put("result", "logout");
		}else {//로그인 된 경우
			//전송된 데이터 인코딩 처리
			request.setCharacterEncoding("utf-8");
			//자바빈(VO) 생성
			//데이터 셋팅
			CartVO cart = new CartVO();
			cart.setItem_num(Integer.parseInt(request.getParameter("item_num")));
			cart.setOrder_quantity(Integer.parseInt(request.getParameter("order_quantity")));
			cart.setMem_num(user_num);
			
			//동일 상품이 기존에 장바구니에 등록되어 있는 경우 상품 update/ 아닐 경우 insert
			CartDAO dao = CartDAO.getInstance();
			//cartVO db_cart = dao.getCart(cart);
			//if(db_cart==null) { //동일 상품이 없을 경우
				dao.insertCart(cart);
				mapAjax.put("result", "success");
			//}else { //동일 상품이 있을 경우
				
			//}
			
		}
		//JSON 문자열 생성
		ObjectMapper mapper = new ObjectMapper();
		String ajaxData = mapper.writeValueAsString(mapAjax);
		
		request.setAttribute("ajaxData", ajaxData);
		
		return "/WEB-INF/views/common/ajax_view.jsp";
	}

}

package kr.order.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.controller.Action;
import kr.order.dao.OrderDAO;
import kr.order.vo.OrderVO;
import kr.util.PageUtil;

public class UserOrderListAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num==null) { //로그인이 되지 않은 경우
			return "redirect:/member/loginForm.do";
		}
		
		String pageNum = request.getParameter("pageNum");
		if(pageNum==null) pageNum = "1";
		
		String keyfield = request.getParameter("keyfield");
		String keyword = request.getParameter("keyword");
		
		OrderDAO dao = OrderDAO.getInstance();
		int count = dao.getOrderCountByMem_num(keyfield, keyword, user_num); //사용자 - 전체 주문 개수/검색 주문 개수
		
		//페이지처리
		PageUtil page = new PageUtil(keyfield, keyword, 
				Integer.parseInt(pageNum),count,20,10,"orderList.do");
		//마이페이지에서 구매목록보기 버튼 눌렀을 때 http://localhost:8080/ch08-mvcPage/order/orderList.do 가 떴기 때문에
		
		List<OrderVO> list = null;
		if(count > 0) {
			list = dao.getListOrderByMem_num(page.getStartRow(), page.getEndRow(), keyfield, keyword, user_num);
		}
		
		request.setAttribute("count", count);
		request.setAttribute("list", list);
		request.setAttribute("page", page.getPage());
		
		return "/WEB-INF/views/order/user_list.jsp";
	}

}

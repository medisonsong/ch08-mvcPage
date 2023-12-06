package kr.item.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.controller.Action;
import kr.item.dao.ItemDAO;
import kr.item.vo.ItemVO;
import kr.util.StringUtil;

public class UserDetailAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 상품 번호 반환
		int item_num = Integer.parseInt(request.getParameter("item_num"));
		ItemDAO dao = ItemDAO.getInstance();
		
		//한건의 레코드 읽어오기
		ItemVO item = dao.getItem(item_num);
		
		//줄바꿈 처리
		item.setDetail(StringUtil.useBrHtml(item.getDetail()));
		request.setAttribute("item", item);
		//관리자 혼자 상품/글을 등록하기 때문에 <> 등 태그 못쓰게 막는 구문을 안 넣었는데 
		//사용자가 입력하는 폼은 철저하게 막아야함 
		
		return "/WEB-INF/views/item/user_detail.jsp";
	}

}

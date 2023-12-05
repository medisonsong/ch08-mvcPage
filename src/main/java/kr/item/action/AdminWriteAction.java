package kr.item.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;

import kr.controller.Action;
import kr.item.dao.ItemDAO;
import kr.item.vo.ItemVO;
import kr.util.FileUtil;

public class AdminWriteAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		if(user_num == null) { //로그인이 되지 않은 경우
			return "redirect:/member/loginForm.do";
		}
		
		Integer user_auth = (Integer)session.getAttribute("user_auth");
		if(user_auth != 9) { //관리자로 로그인하지 않은 경우
			return "/WEB-INF/views/common/notice.jsp";
		}
		
		//관리자로 로그인한 경우
		MultipartRequest multi = FileUtil.createFile(request);
		//자바빈(VO) 생성
		ItemVO item = new ItemVO();
		item.setName(multi.getParameter("name"));
		item.setPrice(Integer.parseInt(multi.getParameter("price")));
		item.setQuantity(Integer.parseInt(multi.getParameter("quantity")));
		item.setPhoto1(multi.getFilesystemName("photo1"));
		item.setPhoto2(multi.getFilesystemName("photo2"));
		item.setDetail(multi.getParameter("detail"));
		item.setStatus(Integer.parseInt(multi.getParameter("status")));
		
		//호출
		ItemDAO dao = ItemDAO.getInstance();
		dao.insertItem(item);
		
		//화면 전환 방식 (script, button, redirect 이외 새로운 방법)
		//Refresh 기능 -- client로 전송하면 버튼을 누르지 않아도 화면 이동
		
		//Refresh 정보를 응답 헤더에 추가
		response.addHeader("Refresh", "2;url=adminList.do"); //2초 정도 유지 후 adminList.do로 이동
		
		request.setAttribute("accessMsg", "성공적으로 등록되었습니다.");
		request.setAttribute("accessUrl", "adminList.do");
		
		return "/WEB-INF/views/common/notice.jsp";
	}

}

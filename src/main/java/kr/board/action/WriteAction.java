package kr.board.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;

import kr.board.dao.BoardDAO;
import kr.board.vo.BoardVO;
import kr.controller.Action;
import kr.util.FileUtil;

public class WriteAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		if(user_num==null) { //로그인이 되지 않은 경우
			return "redirect:/member/loginForm.do";
		}
		//로그인 된 경우
		//파일이 포함되어 있기 때문에 MultipartRequest 
		MultipartRequest multi = FileUtil.createFile(request);
		BoardVO board = new BoardVO();
		//전송된거
		board.setTitle(multi.getParameter("title"));
		board.setContent(multi.getParameter("content"));
		// ip는 전송된게 아니라서 뽑아내야함
		board.setIp(request.getRemoteAddr());
		board.setFilename(multi.getFilesystemName("filename"));
		board.setMem_num(user_num);
		
		BoardDAO dao = BoardDAO.getInstance();
		dao.insertBoard(board);
		
		//JSP 경로 반환
		return "/WEB-INF/views/board/write.jsp";
	}

}

package kr.board.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.board.dao.BoardDAO;
import kr.board.vo.BoardVO;
import kr.controller.Action;
import kr.util.StringUtil;

public class UpdateFormAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//작성자가 아닌 사람은 수정하지 못하게 할 것
		
		//1) 로그인이 됐는지 안됐는지 체크
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		if(user_num == null) {
			//로그인 되지 않은 경우
			return "redirect:/member/loginForm.do";
		}
		
		//2) 로그인한 회원번호와 작성자 회원번호 일치하는지 체크
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		BoardDAO dao = BoardDAO.getInstance();
		BoardVO db_board = dao.getBoard(board_num);
		
		if(user_num != db_board.getMem_num()) {
			//로그인한 회원번호와 작성자 회원번호 불일치
			return "/WEB-INF/views/common/notice.jsp";
		}
		
		/*
		 * 큰 따옴표 처리 (수정폼의 input 태그에만 명시)
		 * 게시판에 글 작성 시 input 태그에서 ""를 넣어서 글쓰기를 하면
		 * 정상적으로 글 등록되고 목록 및 상세 페이지에서 정상 표시됨
		 * 
		 *  but 수정폼에서 수정 시 input태그에 데이터가 표시되지 않은 현상이 발생 
		 */
		db_board.setTitle(StringUtil.parseQuot(db_board.getTitle()));
		
		//데이터 저장
		request.setAttribute("board", db_board);
		
		return "/WEB-INF/views/board/updateForm.jsp";
	}

}

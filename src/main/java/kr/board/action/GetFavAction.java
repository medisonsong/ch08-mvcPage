package kr.board.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;

import kr.board.dao.BoardDAO;
import kr.board.vo.BoardFavVO;
import kr.controller.Action;

public class GetFavAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//전송된 데이터 인코딩 처리
		request.setCharacterEncoding("utf-8");
		//전송된 데이터 반환
		//board_num 값이 전송
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		
		Map<String,Object> mapAjax = new HashMap<String,Object>(); // string,integer기 때문에 어쩔수없이 integer는 object로 해야함
		
		//로그인이 됐는지 안됐는지 확인
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		BoardDAO dao = BoardDAO.getInstance();
		if(user_num == null) { //로그인이 되지 않은 경우 
			mapAjax.put("status", "noFav"); //String
		}else { //로그인된경우
			BoardFavVO boardFav = dao.selectFav(new BoardFavVO(board_num,user_num));
			
			if(boardFav!=null) { //좋아요 표시
				mapAjax.put("status", "yesFav");
			}else { //좋아요 미표시
				mapAjax.put("status", "noFav");
			}
		}
		//count는 항상 전달되어야 하니까 else 다 끝나고 맨 아래에 명시
		mapAjax.put("count", dao.selectFavCount(board_num)); //int인데 통합적으로 사용하기 위해 object
		
		//JSON 문자열 생성
		ObjectMapper mapper = new ObjectMapper();
		String ajaxData = mapper.writeValueAsString(mapAjax);
		
		request.setAttribute("ajaxData", ajaxData);
		
		return "/WEB-INF/views/common/ajax_view.jsp";
	}

}

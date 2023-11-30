package kr.board.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.board.vo.BoardFavVO;
import kr.board.vo.BoardVO;
import kr.util.DBUtil;
import kr.util.StringUtil;

public class BoardDAO {
	//싱글턴 패턴
	private static BoardDAO instance = new BoardDAO();
	
	public static BoardDAO getInstance() {
		return instance;
	}
	
	private BoardDAO() {}
	
	//글 등록
	public void insertBoard(BoardVO board)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션을 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			sql = "INSERT INTO zboard (board_num,title,content,filename,ip,mem_num) VALUES "
					+ "(zboard_seq.nextval,?,?,?,?,?)";
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setString(1, board.getTitle());
			pstmt.setString(2, board.getContent());
			pstmt.setString(3, board.getFilename());
			pstmt.setString(4, board.getIp());
			pstmt.setInt(5, board.getMem_num());
			//SQL문 실행
			pstmt.executeUpdate();
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	
	//전체 레코드 수/검색 레코드 수
	public int getBoardCount(String keyfield, String keyword)throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		String sub_sql = "";
		int count = 0;
		
		
		try {
			//커넥션풀로부터 커넥션 할당
			conn = DBUtil.getConnection();
			
			if(keyword!=null && !"".equals(keyword)) {
				//검색 처리
				if(keyfield.equals("1")) sub_sql += "WHERE title LIKE ?";
				else if(keyfield.equals("2")) sub_sql += "WHERE id LIKE ?";
				else if(keyfield.equals("3")) sub_sql += "WHERE content LIKE ?";
			}
			//SQL문 작성
			sql = "SELECT COUNT(*) FROM zboard JOIN zmember USING(mem_num) " + sub_sql;
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			
			//keyfield 검색문에 ? 데이터 처리
			if(keyword!=null && !"".equals(keyword)) {
				pstmt.setString(1, "%"+keyword+"%");
			}
			//SQL문 실행
			rs = pstmt.executeQuery();
			if(rs.next()) {
				count = rs.getInt(1); //목록에서 페이지 처리 시 따라서 할 수 있음
			}
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		return count;
	}
	
	
	//전체 글/검색 글 목록
	public List<BoardVO> getListBoard(int start, int end, String keyfield, String keyword)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardVO> list = null;
		String sql = null;
		String sub_sql = "";
		int cnt = 0;
		
		
		try {
			//커넥션풀로부터 커넥션 할당
			conn = DBUtil.getConnection();
			
			if(keyword!=null && !"".equals(keyword)) {
				//검색 처리 (title id content)
				if(keyfield.equals("1")) sub_sql += "WHERE title LIKE ?";
				else if(keyfield.equals("2")) sub_sql += "WHERE id LIKE ?";
				else if(keyfield.equals("3")) sub_sql += "WHERE content LIKE ?";
			}
			
			//SQL문 작성
			sql = "SELECT * FROM (SELECT a.*, rownum rnum FROM "
					+ "(SELECT * FROM zboard JOIN zmember USING(mem_num) " + sub_sql 
					+ " ORDER BY board_num DESC)a) WHERE rnum >= ? AND rnum <= ?";
			
			//PreparedStatement 객체 작성
			pstmt = conn.prepareStatement(sql);
			
			//?에 데이터 바인딩
			//검색이 없으면 2갠데 검색하면 3개라서 동적으로 조건체크 해야함
			if(keyword!=null && !"".equals(keyword)) {
				pstmt.setString(++cnt, "%"+keyword+"%");
			}
			
			pstmt.setInt(++cnt, start);
			pstmt.setInt(++cnt, end);
			
			//SQL문 실행
			rs = pstmt.executeQuery();
			
			//ArrayList에 담기
			list = new ArrayList<BoardVO>();
			while(rs.next()) {
				BoardVO board = new BoardVO();
				board.setBoard_num(rs.getInt("board_num"));
				//HTML을 허용하지 않음
				board.setTitle(StringUtil.useNoHtml(rs.getString("title")));
				board.setHit(rs.getInt("hit"));
				board.setReg_date(rs.getDate("reg_date"));
				board.setId(rs.getString("id"));
				
				list.add(board);
			}
		}catch(Exception e) {
			throw new Exception(e); 
		}finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		return list;
	}
	
	//글 상세 
	//1) zboard-zmember 조인해서 id가 보이게 함
	//2) 탈퇴 시 zmember_detail가 지워지기 때문에 outerjoin을 하지 않으면 정보를 읽어오지 못함
	//zmember / zmember_detail 과 모두 조인해야함
	
	public BoardVO getBoard(int board_num)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardVO board = null;
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션을 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			//(주의) 회원 탈퇴 시 zmember_detail에는 레코드가 존재하지 않기 때문에
			//외부 조인을 사용해서 데이터 누락 방지
			sql = "SELECT * FROM zboard JOIN zmember USING (mem_num) "
					+ "LEFT OUTER JOIN zmember_detail USING (mem_num) "
					+ "WHERE board_num=?";
			
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setInt(1, board_num);
			
			//SQL문 실행
			rs = pstmt.executeQuery();
			if(rs.next()) {//1건의 레코드만 가져와서 if문임
				board = new BoardVO(); // 보드 객체 생성 후 넣어주려고
				board.setBoard_num(rs.getInt("board_num"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setHit(rs.getInt("hit"));
				board.setReg_date(rs.getDate("reg_date"));
				board.setModify_date(rs.getDate("modify_date"));
				board.setFilename(rs.getString("filename"));
				board.setMem_num(rs.getInt("mem_num"));
				board.setId(rs.getString("id"));
				board.setPhoto(rs.getString("photo")); // 탈퇴시 photo정보가 null이 되어도 기본 이미지를 지정해주면 됨
			}
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		return board;
	}
	
	
	//조회수 증가 (글 상세와 연관 有)
	//1) Database에 기록 (같은 유저는 증가X) 2) 해당 pc의 쿠키 이용 (같은 유저여도 진입할 때마다 count+1) > 두가지 방법 
	public void updateReadcount(int board_num)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션을 할당
			conn = DBUtil.getConnection();
			//SQL문 작성 (상세페이지 진입 시 무조건 +1/ 같은 사람 진입 시에도 누적 // 쿠키 사용)
			sql = "UPDATE zboard SET hit=hit+1 WHERE board_num=?";
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setInt(1, board_num);
			//SQL문 실행
			pstmt.executeUpdate();
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}

	
	//파일 삭제
	public void deleteFile(int board_num)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션을 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			sql = "UPDATE zboard SET filename='' WHERE board_num=?"; //db상에서 update
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			
			//?에 데이터를 바인딩
			pstmt.setInt(1, board_num);
			//SQL문 실행
			pstmt.executeUpdate();
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	
	
	//글 수정
	public void updateBoard(BoardVO board)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		String sub_sql = "";
		int cnt = 0;
		
		try {
			//커넥션풀로부터 커넥션을 할당
			conn = DBUtil.getConnection();
			
			if(board.getFilename()!=null) {
				sub_sql += ",filename=?";
			}
			//SQL문 작성
			sql = "UPDATE zboard SET title=?,content=?,modify_date=SYSDATE,ip=? " + sub_sql +
					" WHERE board_num=?";
			
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			
			//?에 데이터바인딩
			pstmt.setString(++cnt, board.getTitle());
			pstmt.setString(++cnt, board.getContent());
			pstmt.setString(++cnt, board.getIp());
			if(board.getFilename()!=null) {
				pstmt.setString(++cnt, board.getFilename());
			}
			pstmt.setInt(++cnt, board.getBoard_num());
			
			//SQL문 실행
			pstmt.executeUpdate();
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
		
	}
	
	
	
	//글 삭제
	public void deleteBoard(int board_num)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		String sql = null;
		
		
		try {
			//커넥션풀로부터 커넥션 할당
			conn = DBUtil.getConnection();
			//오토커밋 해제 
			conn.setAutoCommit(false);
			
			//좋아요 삭제
			sql = "DELETE FROM zboard_fav WHERE board_num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			pstmt.executeUpdate();
			
			
			//댓글 삭제
			
			//부모글 삭제
			sql = "DELETE FROM zboard WHERE board_num=?";
			pstmt3 = conn.prepareStatement(sql);
			pstmt3.setInt(1, board_num);
			pstmt3.executeUpdate();
			
			//모든 SQL문 실행이 성공하면 
			conn.commit();
		}catch(Exception e) {
			//하나라도 SQL문이 실패하면
			conn.rollback();
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt, null);
			DBUtil.executeClose(null, pstmt2, null);
			DBUtil.executeClose(null, pstmt3, conn);
		}
		
	}
	
	
	
	//좋아요 등록 (행 수로 셈)
	public void insertFav(BoardFavVO favVO)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		
		try {
			//커넥션풀로부터 커넥션 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			sql = "INSERT INTO zboard_fav (board_num,mem_num) "
					+ "VALUES (?,?)";
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setInt(1, favVO.getBoard_num());
			pstmt.setInt(2, favVO.getMem_num());
			//SQL문 실행
			pstmt.executeUpdate();
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
		
	}
	
	
	
	//좋아요 개수 
	public int selectFavCount(int board_num)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int count = 0;
		
		try {
			//커넥션풀로부터 커넥션을 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			sql = "SELECT COUNT(*) FROM zboard_fav WHERE board_num=?";
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setInt(1, board_num);
			//SQL문 실행
			rs = pstmt.executeQuery();
			if(rs.next()) {
				count = rs.getInt(1);
			}
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		return count;
	}
	
	
	//회원번호와 게시물 번호를 이용한 좋아요 정보 (게시물 상세 정보)
	//좋아요 선택 여부
	public BoardFavVO selectFav(BoardFavVO favVO)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardFavVO fav = null;//boardfav vo에 담아서 정보를 처리할거라서 자바빈도 명시
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			sql = "SELECT * FROM zboard_fav WHERE board_num=? AND mem_num=?";
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setInt(1, favVO.getBoard_num());
			pstmt.setInt(2, favVO.getMem_num());
			//SQL문 실행
			//좋아요를 누르면 행이 만들어짐 --> 행이 있으면 좋아요 누른거/ 없으면 좋아요 없는거
			rs = pstmt.executeQuery();
			if(rs.next()) {
				fav = new BoardFavVO(); //vo 넘겨주기
				fav.setBoard_num(rs.getInt("board_num"));
				fav.setMem_num(rs.getInt("mem_num"));
			}
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		
		return fav; //자바빈을 반환
	}
	
	
	//좋아요 삭제
	public void deleteFav(BoardFavVO favVO)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			sql = "DELETE FROM zboard_fav WHERE board_num=? AND mem_num=?";
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setInt(1, favVO.getBoard_num());
			pstmt.setInt(2, favVO.getMem_num());
			//SQL문 실행
			pstmt.executeUpdate();
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
	}
	
	
	
	
	//내가 선택한 좋아요 목록
	
	//댓글 등록
	//댓글 개수
	//댓글 목록
	//댓글 상세(댓글 수정, 삭제시 작성자 회원번호 체크 용도로 사용)
	//댓글 수정
	//댓글 삭제
	
}

package kr.member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import kr.member.vo.MemberVO;
import kr.util.DBUtil;

public class MemberDAO {
	//싱글턴 패턴
	private static MemberDAO instance = new MemberDAO();
	public static MemberDAO getInstance() {
		return instance;
	}
	private MemberDAO() {}
	
	
	//회원가입
	public void insertMember(MemberVO member)throws Exception{
		//pk를 만들어서 다른 테이블과 공유해야함 -> 근데 그러면 테이블이 3개여야함 그래서 트랜잭션 처리할거임
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet rs = null;
		String sql = null;
		int num = 0; // 시퀀스 번호 저장
		
		try {
			//커넥션풀로부터 커넥션 할당
			conn = DBUtil.getConnection();
			//오토 커밋 해제
			conn.setAutoCommit(false);
			
			//1) 회원 번호(mem_num) 생성
			sql = "SELECT zmember_seq.nextval FROM dual"; // 가상테이블(dual)에서 zmember_seq.nextval 실행
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) { //하나의 행이라서 if
				num = rs.getInt(1); // 1=>컬럼 인덱스
			}
			
			//2) zmember 테이블에 데이터 저장
			sql = "INSERT INTO zmember (mem_num,id) VALUES (?,?)";
			pstmt2 = conn.prepareStatement(sql);
			pstmt2.setInt(1, num); //위 회원번호 num값(시퀀스)을 받아서 넣음
			pstmt2.setString(2, member.getId()); //아이디
			pstmt2.executeUpdate();
			
			//3) zmember_detail 테이블에 데이터를 저장
			sql = "INSERT INTO zmember_detail (mem_num,name,passwd,phone,email,zipcode,address1,address2) VALUES "
					+ "(?,?,?,?,?,?,?,?)"; //시퀀스는 위에 미리 생성했기 때문에 ?처리
			pstmt3 = conn.prepareStatement(sql);
			
			pstmt3.setInt(1, num); // 회원번호
			pstmt3.setString(2, member.getName());
			pstmt3.setString(3, member.getPasswd());
			pstmt3.setString(4, member.getPhone());
			pstmt3.setString(5, member.getEmail());
			pstmt3.setString(6, member.getZipcode());
			pstmt3.setString(7, member.getAddress1());
			pstmt3.setString(8, member.getAddress2());
			pstmt3.executeUpdate();
			
			//SQL문 실행 시 모두 성공하면 실행
			conn.commit();
			
		}catch(Exception e) {
			//SQL문이 하나라도 실패하면 rollback
			conn.rollback();
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt3, null);
			DBUtil.executeClose(null, pstmt2, null);
			DBUtil.executeClose(rs, pstmt, conn);
		}
	}
	
	
	//ID 중복체크 및 로그인 처리
	public MemberVO checkMember(String id)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MemberVO member = null;
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션 할당
			conn = DBUtil.getConnection();
			//SQL문 작성 (조인)
			//zmember와 zmember_detail 조인시 zmember의 누락된 데이터가 보여야 id 중복 체크 가능
			sql = "SELECT * FROM zmember LEFT OUTER JOIN zmember_detail USING(mem_num) WHERE id=?"; 
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setString(1, id);
			//SQL문 실행
			rs = pstmt.executeQuery();
			if(rs.next()) {
				member = new MemberVO();
				member.setMem_num(rs.getInt("mem_num"));
				member.setId(rs.getString("id"));
				member.setAuth(rs.getInt("auth"));
				member.setPasswd(rs.getString("passwd"));
				member.setPhoto(rs.getString("photo"));
			}
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		return member;
	}
	
	
	//회원 상세 정보
	public MemberVO getMember(int mem_num)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MemberVO member = null; //여기에 담을거라서
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션을 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			sql = "SELECT * FROM zmember JOIN zmember_detail USING(mem_num) WHERE mem_num=?";
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setInt(1, mem_num);
			//SQL문 실행
			rs = pstmt.executeQuery();
			
			if(rs.next()) { // pk라 결과값 1
				member = new MemberVO();
				member.setMem_num(rs.getInt("mem_num"));
				member.setId(rs.getString("id"));
				member.setAuth(rs.getInt("auth"));
				member.setPasswd(rs.getString("passwd"));
				member.setName(rs.getString("name"));
				member.setPhone(rs.getString("phone"));
				member.setEmail(rs.getString("email"));
				member.setZipcode(rs.getString("zipcode")); // 우편번호가 String인 이유 >> 연산할 수 없기 때문에 문자열로
				member.setAddress1(rs.getString("address1"));
				member.setAddress2(rs.getString("address2"));
				member.setPhoto(rs.getString("photo"));
				member.setReg_date(rs.getDate("reg_date")); //가입일
				member.setModify_date(rs.getDate("modify_date")); //수정일
			}
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(rs, pstmt, conn);
		}
		return member;
	}
	
	
	
	//회원 정보 수정
	//비밀번호 수정
	

	//프로필 사진 수정
	public void updateMyPhoto(String photo,int mem_num)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			//커넥션풀로부터 커넥션을 할당
			conn = DBUtil.getConnection();
			//SQL문 작성
			sql = "UPDATE zmember_detail SET photo=? WHERE mem_num=?"; //프사를 안쓰는 기능은 없고 대체만 가능
			//PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//?에 데이터 바인딩
			pstmt.setString(1, photo);
			pstmt.setInt(2, mem_num);
			//SQL문 실행
			pstmt.executeUpdate();
		}catch(Exception e) {
			throw new Exception(e);
		}finally {
			DBUtil.executeClose(null, pstmt, conn);
		}
		
	}
	
	
	//회원 탈퇴 (회원 정보 삭제)
	
	//관리자
	//전체 내용 개수, 검색 내용 개수
	//목록, 검색 목록
	//회원 등급 수정 (회원 정보 중 등급만 변경 가능)
	
	
}

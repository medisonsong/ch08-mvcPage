<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 상세 정보</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.6.0.min.js"></script>
</head>
<body>
<div>
	<jsp:include page="/WEB-INF/views/common/header.jsp"/>
	<div class="content-main">
		<h2>${board.title}</h2>
		<ul class="detail-info">
			<li>
				<c:if test="${!empty board.photo}"> <%-- photo가 있을 경우 --%>
				<img src="${pageContext.request.contextPath}/upload/${board.photo}" width="40" height="40" class="my-photo">
				</c:if>
				<c:if test="${empty board.photo}"> <%-- photo가 없을 경우 (기본사진) --%>
				<img src="${pageContext.request.contextPath}/images/face.png" width="40" height="40" class="my-photo">
				</c:if>
			</li>
			<li>
				${board.id}<br>
				조회 : ${board.hit}
			</li>
		</ul>
		<hr size="1" noshade="noshade" width="100%">
		
		<c:if test="${!empty board.filename}"> <%-- file이 있을 경우 --%>
		<div class="align-center">
			<img src="${pageContext.request.contextPath}/upload/${board.filename}" class="detail-img">
		</div>
		</c:if>
		<p>
			${board.content}
		</p>
		<hr size="1" noshade="noshade" width="100%">
		
		<ul class="detail-sub">
			<li>
				<%-- 좋아요 처리 --%>
			</li>
			<li>
				<c:if test="${!empty board.modify_date}"> <%-- 수정일이 있다면 수정일도 보이게 --%>
					최근 수정일 : ${board.modify_date}
				</c:if>
				작성일 : ${board.reg_date}
				<%-- 로그인한 회원번호와 작성자 회원번호가 일치해야 수정,삭제 가능 --%>
				<c:if test="${user_num == board.mem_num}"> <%-- session == request 조건 체크 --%>
					<input type="button" value="수정" onclick="location.href='updateForm.do?board_num=${board.board_num}'">
					<input type="button" value="삭제" id="delete_btn">
				<%-- script를 바로 밑에 넣는 이유: 로그인한 사람과 작성자가 일치해야 보이게 하려고 (다르면 안보임) --%>	
				<script type="text/javascript"> 
					let delete_btn = document.getElementById('delete_btn');
					//이벤트 연결
					delete_btn.onclick=function(){
						let choice = confirm('삭제하시겠습니까?');
						if(choice){
							location.replace('delete.do?board_num=${board.board_num}');
						}
					};
				</script>	
				</c:if>
			</li>
		</ul>
		<!-- 댓글 시작 -->
		<!-- 댓글 목록 출력 시작 -->
		<!-- 댓글 목록 출력 끝 -->
		<!-- 댓글 끝 -->
	</div>
</div>
</body>
</html>	
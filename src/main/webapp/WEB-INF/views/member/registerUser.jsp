<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입 완료</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-main">
	<jsp:include page="/WEB-INF/views/common/header.jsp"/> <%-- 메뉴 공통적으로 보이기 --%>
	<div class="content-main">
		<h2>회원가입 완료</h2>
		<div class="result-display">
			<div class="align-center">
				회원가입이 완료되었습니다.
				<p> <%-- p태그를 하나만 쓰면 brbr 쓴거랑 똑같음 --%>
				<input type="button" value="홈으로" onclick="location.href='${pageContext.request.contextPath}/main/main.do'">
			</div>
		</div>
	</div>
</div>
</body>
</html>
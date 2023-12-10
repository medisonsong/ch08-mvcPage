<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>   
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>장바구니</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.6.0.min.js"></script>
</head>
<body>
	<div class="page-main">
		<jsp:include page="/WEB-INF/views/common/header.jsp"/>
		<div class="content-main">
			<h2>장바구니</h2>
			<c:if test="${empty list}">
			<div class="result-display">
				장바구니에 담은 상품이 없습니다
			</div>
			</c:if>
			<c:if test="${!empty list}">
			<form id="cart_order" action="${pageContext.request.contextPath}/order/orderForm.do" method="post">
				<table>
					<tr>
						<th>상품명</th>
						<th>수량</th>
						<th>상품가격</th>
						<th>합계</th> <%-- sub_total --%>
					</tr>
					<c:forEach var="cart" items="${list}">
					<tr>
						<td>
							<a href="${pageContext.request.contextPath}/item/detail.do?item_num=${cart.item_num}">
								<img src="${pageContext.request.contextPath}/upload/${cart.itemVO.photo1}" width="50">
								${cart.itemVO.name} <%-- 원래 : cart.getItemVO().getName() --%>
							</a>						
						</td>
						<td class="align-center">
							<c:if test="${cart.itemVO.status == 1 or cart.itemVO.quantity < cart.order_quantity}">[판매중지]</c:if>
										<%-- 카트에 담긴 아이템이 미표사거나 카드에 담긴 수량보다 카트에 담은 주문 수량이 더 많을 때 판매 중지 --%>
							<c:if test="${cart.itemVO.status == 2 and cart.itemVO.quantity >= cart.order_quantity}">
								<input type="number" name="order_quantity" min="1" max="${cart.itemVO.quantity}" autocomplete="off" value="${cart.order_quantity}" class="quantity-width">
								<br>
								<input type="button" value="변경" class="cart-modify" data-cartnum="${cart.cart_num}" data-itemnum="${cart.item_num}">
							</c:if>
						</td>
						<td class="align-center">
							<fmt:formatNumber value="${cart.itemVO.price}"/>원						
						</td> 
						<td class="align-center">
							<fmt:formatNumber value="${cart.sub_total}"/>원
							<br>
							<input type="button" value="삭제" class="cart-del" data-cartnum="${cart.cart_num}">
						</td>
					</tr>
					</c:forEach>
					<tr>
						<td colspan="3" class="align-center"><b>총 구매 금액</b></td>
						<td class="align-center"><fmt:formatNumber value="${all_total}"/>원</td>
					</tr>
				</table>
				<div class="align-center cart-submit">
					<input type="submit" value="구매하기">
				</div>
			</form>
			</c:if>
		</div>
	</div>
</body>
</html>
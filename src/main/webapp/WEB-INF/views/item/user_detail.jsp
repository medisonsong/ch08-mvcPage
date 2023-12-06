<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 상세 정보</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.6.0.min.js"></script>
<script type="text/javascript">
$(function(){
	$('#order_quantity').on('keyup mouseup',function(){ //2개의 이벤트 동시 처리 시 공백 주면 됨 (keyup mouseup)
		if($('#order_quantity').val()==''){ //초기 화면 시 null 상태일 때
			$('#item_total_txt').text('총주문 금액 : 0원');
			return;
		}
		if($('#order_quantity').val()<=0){ //0이거나 음수일 경우
			$('#order_quantity').val('');
			return;
		}
		if(Number($('#item_quantity').val()) < $('#order_quantity').val()){
			alert('수량이 부족합니다.');
			$('#order_quantity').val('');
			$('#item_total_txt').text('총주문 금액 : 0원');
			return;
		}
		
		//그외는 다 정상적인 경우라 바로 계산
		let total = $('#item_price').val() * $('#order_quantity').val();
		$('#item_total_txt').text('총주문 금액 : ' + total.toLocaleString()+'원'); // toLocaleString > 3자리 끊어서 ,표시
		
	});//end of on
	
	//submit이벤트 연결 (선택한 상품 장바구니 담기)
	//장바구니 담기 이벤트 연결
	$('#item_cart').submit(function(event){ //만약 false일 때 이벤트를 취소해야 데이터가 날아가지 않기 때문에 인자로 추가
		if($('#order_quantity').val()==''){
			alert('수량을 입력하세요.');
			$('#order_quantity').focus();
			return false;
		}
		
		let form_data = $(this).serialize();
		//서버와 통신
		$.ajax({
			url:'${pageContext.request.contextPath}/cart/write.do', //jsp기 때문에 경로 사용 가능(uri방식) / js에서는 사용 불가능
			type:'post',
			data:form_data, //위 변수 지정
			dataType:'json',
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인 후 사용하세요.');
				}else if(param.result == 'success'){
					alert('장바구니에 담았습니다.'); // 담았다고 알려주고 자동이동 or 본인이 장바구니 클릭 후 이동 선택해서 하면 됨
					//지금은 그냥 장바구니로 자동이동하게 만들었음
					location.href='${pageContext.request.contextPath}/cart/list.do';
				}else if(param.result == 'overquantity'){ //주문수량초과
					alert('기존에 주문한 상품입니다. 개수를 추가하면 재고가 부족합니다.');
				}else{
					alert('장바구니 담기 오류');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});//end of ajax
		
		//기본 이벤트 제거
		event.preventDefault();
		
	});//end of submit
});
</script>
</head>
<body>
<div class="page-main">
	<jsp:include page="/WEB-INF/views/common/header.jsp"/>
	<div class="content-main">
		<%-- status:1 일 때 --%>
		<c:if test="${item.status==1}">
		<div class="result-display">
			<div class="align-center">
				본 상품은 판매 중지되었습니다.
				<p>
				<input type="button" value="판매상품 보기" onclick="location.href='itemList.do'"> <%-- 별도의 상품 목록 --%>
			</div>
		</div>
		</c:if>
		<%-- status:2 일 때 --%>
		<c:if test="${item.status==2}">
		<h3 class="align-center">${item.name}</h3>
		<div class="item-image">
			<img src="${pageContext.request.contextPath}/upload/${item.photo2}" width="400"> <%--1이 목록 2가 상세--%>
		</div>
		<div class="item-detail">
			<form id="item_cart">
				<input type="hidden" name="item_num" value="${item.item_num}" id="item_num">
				<input type="hidden" name="item_price" value="${item.price}" id="item_price">
				<input type="hidden" name="item_quantity" value="${item.quantity}" id="item_quantity">
				<ul>
					<li>가격 : <b><fmt:formatNumber value="${item.price}"/>원</b></li>
					<li>재고 : <span><fmt:formatNumber value="${item.quantity}"/></span></li>
					<c:if test="${item.quantity > 0}">
					<li>
						<label for="order_quantity">구매수량</label>
						<input type="number" name="order_quantity" min="1" max="${item.quantity}" autocomplete="off" id="order_quantity" class="quantity-width">
					</li>
					<li>
						<span id="item_total_txt">총주문 금액 : 0원</span>
					</li>
					<li>
						<input type="submit" value="장바구니에 담기">
					</li>
					</c:if>
					
					<%-- 재고가 없는 경우 조건 체크 (0 이하면 무조건 품절) --%>
					<c:if test="${item.quantity <= 0}">
					<li class="align-center">
						<span class="sold-out">품절</span>
					</li>
					</c:if>
				</ul>
			</form>
		</div>
		<hr size="1" noshade="noshade" width="100%">
		<p>
			${item.detail}
		</p>
		</c:if>
	</div>
</div>
</body>
</html>
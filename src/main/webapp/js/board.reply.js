$(function(){
	let rowCount = 10;
	let currentPage;
	let count;
	
	//댓글 목록
	function selectList(pageNum){
		currentPage = pageNum;
		
		//로딩 이미지 노출
		$('#loading').show();
		
		$.ajax({
			url:'listReply.do',
			type:'post',
			data:{pageNum:pageNum,rowCount:rowCount,
			                    board_num:$('#board_num').val()},
			dataType:'json',
			success:function(param){
				//로딩 이미지 감추기
				$('#loading').hide();
				count = param.count;
				
				if(pageNum == 1){
					//처음 호출시는 해당 ID의 div의 내부 내용물을 제거
					$('#output').empty();
				}
				
				$(param.list).each(function(index,item){
					let output = '<div class="item">';
					output += '<h4>' + item.id + '</h4>';
					output += '<div class="sub-item">';
					output += '<p>' + item.re_content + '</p>';
					
					if(item.re_modifydate){//데이터가 있으면 true로 인식 (js라 가능)
              		    output += '<span class="modify-date">최근 수정일 : ' + item.re_modifydate + '</span>';
           		    }else{
           		       output += '<span class="modify-date">등록일 : ' + item.re_date + '</span>';
            	    }
	
					
					//수정 삭제 버튼 만들기 (조건 有)
					//로그인한 회원 번호와 작성자의 회원번호 일치 여부 체크
					if(param.user_num == item.mem_num){//로그인한 회원번호와 작성자 회원번호 일치
					//행단위로 같은 버튼이 만들어지는 거기 때문에 복수의 태그라 class로 설정 (id 설정시 맨 위만 설정이 되기 때문)
						output += ' <input type="button" data-renum="'+item.re_num+'" value="수정" class="modify-btn">'; // "' 사이에 공백 X
															// "" > HTML 태그의 속성값 / '' 자바스크립트의 문자열
						output += ' <input type="button" data-renum="'+item.re_num+'" value="삭제" class="delete-btn">';
					}
					output += '<hr size="1" noshade width="100%">';
					output += '</div>';
					output += '</div>';
					
					//문서 객체에 추가
					$('#output').append(output); //화면에 보여짐
				});
				
				//page button 처리
				if(currentPage>=Math.ceil(count/rowCount)){
					//currentpage가 같거나 크면 다음 페이지가 없다는 것임
					//다음 페이지가 없음
					$('.paging-button').hide();
				}else{
					//다음 페이지가 존재
					$('.paging-button').show();
				}
			},
			error:function(){
				//로딩 이미지 감추기
				$('#loading').hide();
				alert('네트워크 오류 발생');
			}
		});
	}
	//페이지 처리 이벤트 연결(다음 댓글 보기 버튼 클릭시 데이터 추가)
	$('.paging-button input').click(function(){
		selectList(currentPage + 1);
	});
	//댓글 등록
	$('#re_form').submit(function(event){
		if($('#re_content').val().trim()==''){
			alert('내용을 입력하세요');
			$('#re_content').val('').focus();
			return false;
		}
		
		//form 이하의 태그에 입력한 데이터를 모두 읽어옴(쿼리 스트링 형식으로 읽어옴)
		let form_data = $(this).serialize();		
		
		//서버와 통신
		$.ajax({
			url:'writeReply.do',
			type:'post',
			data:form_data,
			dataType:'json',
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 작성할 수 있습니다.');
				}else if(param.result == 'success'){
					//폼 초기화
					initForm();
					//댓글 작성이 성공하면 새로 삽입한 글을 포함해서 첫번째
					//페이지의 게시글을 다시 호출함
					selectList(1);
				}else{
					alert('댓글 등록 오류 발생');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
		//기본 이벤트 제거
		event.preventDefault();
	});
	//댓글 작성 폼 초기화
	function initForm(){
		$('textarea').val('');
		$('#re_first .letter-count').text('300/300');
	}
	//textarea에 내용 입력시 글자수 체크
	$(document).on('keyup','textarea',function(){
		//입력한 글자수 구함
		let inputLength = $(this).val().length;
		
		if(inputLength > 300){//300자를 넘어선 경우
			$(this).val($(this).val().substring(0,300));
		}else{//300자 이하인 경우
			let remain = 300 - inputLength;
			remain += '/300';
			if($(this).attr('id') == 're_content'){
				//등록폼 글자수
				$('#re_first .letter-count').text(remain);
			}else{
				//수정폼 글자수
				$('#mre_first .letter-count').text(remain);
			}
		}
	});
	//댓글 수정 버튼 클릭시 수정폼 노출 (동적으로 만들어야 함)
	$(document).on('click','.modify-btn',function(){
		//item/sub-item/p, 버튼 순으로 구성되어있음
		//댓글 번호 읽어오기
		let re_num = $(this).attr('data-renum');
		//댓글 내용 읽어오기 (p태그(댓글내용) div class="sub-item"(전체적으로 댓글내용/수정,삭제 버튼 감싸고 있음)) / parent()= div/ p태그를 찾고 parent를 찾은거임
		let content = $(this).parent().find('p').html().replace(/<br>/gi, '\n'); //모든 br태그를 찾고 \n으로 바꿔서 줄바꿈 처리
															//g == 지정문자열 모두, i == 대소문자 무시
		//댓글 수정폼 UI
		let modifyUI = '<form id="mre_form">';
		modifyUI += '<input type="hidden" name="re_num" id="mre_num" value="' + re_num + '">';
		modifyUI += '<textarea rows="3" cols="50" name="re_content" id="mre_content" class="rep-content">'+content+'</textarea>';
		
		//글자수 보여지는 부분
		modifyUI += '<div id="mre_first"><span class="letter-count">300/300</span></div>';
		//버튼
		modifyUI += '<div id="mre_second" class="align-right">';
		modifyUI += ' <input type="submit" value="수정">';
		modifyUI += ' <input type="button" value="취소" class="re-reset">';
		modifyUI += '</div>';
		modifyUI += '</form>';
		
		//이전에 이미 수정하는 댓글이 있을 경우 수정 버튼을 클릭하면 숨겨져있는 div(class=sub-item)을 
		//환원시키고 수정폼 초기화 (=삭제)
		initModifyForm();
		
		//수정 버튼 누를 시 기존 댓글 데이터가 안보여지게 함 (sub-item hide)
		//지금 ㄴ클릭해서 수정하고자 하는 데이터는 감추기 (수정버튼을 감싸고 있는 div)
		$(this).parent().hide(); // div class = sub-item
		
		//수정폼을 수정하고자 하는 데이터가 있는 div에 노출
		$(this).parents('.item').append(modifyUI);
		
		//입력한 글자수 셋팅
		let inputLength = $('#mre_content').val().length;
		let remain = 300 - inputLength;
		remain += '/300';
		
		//문서 객체에 반영
		$('#mre_first .letter-count').text(remain);
		
	});
	//수정폼에서 취소 버튼 클릭시 수정폼 초기화
	$(document).on('click', '.re-reset', function(){
		initModifyForm();
	});
	
	//댓글 수정폼 초기화
	function initModifyForm(){
		//+1댓글 수정 시 2댓글 수정 버튼 누르면 1댓글 hide했던 내용 보여지게 처리 2수정폼 열기
		$('.sub-item').show();
		$('#mre_form').remove(); // 배치는 위에서 함
	}
	//댓글 수정
	$(document).on('submit','#mre_form',function(event){ // function에 기본 event를 제거해야 하기 때문에 인자로 event를 넣어줌
		if($('#mre_content').val().trim()==''){
			alert('내용을 입력하세요!');
			$('#mre_content').val('').focus();
			return false;
		}
		//폼에 입력한 데이터 반환
		let form_data = $(this).serialize(); //쿼리 문자열 형태로 정보 가지고 옴
		
		//서버와 통신
		$.ajax({
			url:'updateReply.do',
			type:'post',
			data:form_data, //위 let 변수가 전달됨
			dataType:'json',
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 수정할 수 있습니다.');
				}else if(param.result == 'success'){
					//방법 2가지
					//1) 기존 text를 싹 다 지우고 처음부터 읽어오기
					//2) 수정한 데이터를 다시 읽어올 필요 없이 화면에 display (화면 갱신)
					//근데 댓글 같은 경우는 2번으로, 수정갱신 하는 방법이 제일 좋음
					
					//포맷 읽어오기 (댓글 내용)
					$('#mre_form').parent().find('p').html($('#mre_content').val().replace(/</g,'&lt;').replace(/>/,'&gt;').replace(/\n/g,'<br>'));
					
					//댓글 수정 시간 (1분 전, 1시간 전 이렇게)
					$('#mre_form').parent().find('.modify-date').text('최근 수정일 : 5초미만'); //modify-date: 날짜 표시하는 클래스 / #mre_form의 부모-item
					
					
					//수정폼 삭제 및 초기화
					initModifyForm();
					
				}else if(param.result == 'wrongAccess'){
					alert('타인의 글을 수정할 수 없습니다.');
				}else{
					alert('댓글 수정 오류 발생');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
		//기본 이벤트 제거
		event.preventDefault();
		
	});
	
	//댓글 삭제
	$(document).on('click','.delete-btn',function(){
		//댓글 번호 반환
		let re_num = $(this).attr('data-renum'); //this == 이벤트가 일어나는 버튼/의 data re_num을 읽어옴
		
		$.ajax({
         url:'deleteReply.do',
         type:'post',
         data:{re_num:re_num}, //첫번째 re_num : key, 두번째 re_num: value값(변수)
         dataType:'json',
         success:function(param){
            if(param.result == 'logout'){
               alert('로그인해야 삭제할 수 있습니다.');
            }else if(param.result == 'success'){
               alert('삭제 완료');
               selectList(1);
            }else if(param.result == 'wrongAccess'){
               alert('타인의 글을 삭제할 수 없습니다.');
            }else{
               alert('댓글 삭제 오류 발생');
            }
         },
         error:function(){
            alert('네트워크 오류 발생');   
         }
      });
      
   });
   //초기 데이터(목록) 호출
   selectList(1);

	
});



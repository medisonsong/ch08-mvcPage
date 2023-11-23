--부모테이블 (아이디 남기기)
create table zmember(
 mem_num number not null,
 id varchar2(12) unique not null, --재가입 못하게 방지
 auth number(1) default 2 not null, --회원등급 : 0탈퇴회원,1정지회원,2일반회원,9관리자
 constraint zmember_pk primary key (mem_num)
);

--자식테이블 (개인정보 다 지우기)
create table zmember_detail(
 mem_num number not null,
 name varchar2(30) not null,
 passwd varchar2(35) not null,
 phone varchar2(15) not null,
 email varchar2(50) not null,
 zipcode varchar2(5) not null,
 address1 varchar2(90) not null,
 address2 varchar2(90) not null,
 photo varchar2(150),
 reg_date date default sysdate not null,
 modify_date date, --수정된날짜(첫 등록 시 null 인정/ 변경 시 update)
 constraint zmember_detail_pk primary key (mem_num),
 constraint zmember_detail_fk foreign key (mem_num) references zmember (mem_num)
);
create sequence zmember_seq;
package kr.board.vo;

public class BoardFavVO {
	private int board_num;
	private int mem_num;
	
	//생성자의 인자로 넣기 위해서 생성자도 생성함
	public BoardFavVO() {}
	
	public BoardFavVO(int board_num, int mem_num) {//인자가 전달되는 생성자 만들기 (생성자로도 데이터 셋팅 가능)
		this.board_num = board_num;
		this.mem_num = mem_num;
	}
	
	public int getBoard_num() {
		return board_num;
	}
	public void setBoard_num(int board_num) {
		this.board_num = board_num;
	}
	public int getMem_num() {
		return mem_num;
	}
	public void setMem_num(int mem_num) {
		this.mem_num = mem_num;
	}
}

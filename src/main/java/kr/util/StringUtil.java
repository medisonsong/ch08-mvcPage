package kr.util;

public class StringUtil {
	/*
	 * HTML 태그를 허용하면서 줄바꿈 
	 */
	public static String useBrHtml(String str) {
		if(str == null) return null;  //null일 경우에는 null
		
		return str.replaceAll("\r\n", "<br>") // \r, \n, \r\n 을 br로 대체
				  .replaceAll("\r", "<br>")
				  .replaceAll("\n", "<br>"); 
	}
	
	/*
	 * HTML 태그를 허용하지 않으면서 줄바꿈 (일반적으로 이것 사용) 
	 */
	public static String useBrNoHtml(String str) {
		if(str == null) return null; // null일 경우에는 null
		
		return str.replaceAll("<", "&lt;") // 태그를 참조형태(일반 특수문자)로 변환 -> 태그 무력화시킴
				  .replaceAll(">", "&gt;")
				  .replaceAll("\r\n", "<br>")
				  .replaceAll("\r", "<br>")
				  .replaceAll("\n", "<br>"); 
	}
	
	/*
	 * HTML 태그를 허용하지 않음
	 */
	public static String useNoHtml(String str) {
		if(str == null) return null;
		
		return str.replaceAll("<", "&lt;")
				  .replaceAll(">", "&gt;");
	}
	
	/*
	 * 큰 따옴표 처리 (에러날 수도 있어서 일반 문자로 처리)
	 */
	public static String parseQuot(String str) {
		if(str == null) return null;
		
		return str.replaceAll("\"", "&quot;");
	}
	
	
	/*
	 * 문자열을 지정한 문자열 개수 이후에 ...으로 처리 (jstl의 functionslib 쓸수도 있음 필요할 때 병행하면 됨)
	 * (글이 너무 많을 경우에 말줄임표 표시)
	 */
	public static String shortWords(int length, String content) {
		if(content == null) return null;
		
		if(content.length() > length) {
			return content.substring(0,length) + " ...";
		}
		return content;
	}
	
}

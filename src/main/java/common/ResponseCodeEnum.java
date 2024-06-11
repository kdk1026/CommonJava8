package common;

import java.text.MessageFormat;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 6. 김대광	Javadoc 작성
 * </pre>
 * 
 * <pre>
 * 메시지 조금씩 손좀 볼 필요가 있다.
 * 
 * 코드는 일일이 부여한다는게 보통 내기가 아님...
 * 최근 Java가 아닌 node.js 프로젝트 하면서 타협본거는
 * 코드는 무조건 성공이면 200, 실패면 500
 * 메시지만 다르게 처리 이게 가장 심플한거 같다!! 이래서 많이 보고 참고하는게 중요한데... 어디 좋은 Github 주소 없을려나 ㅋ
 * </pre>
 * @author 김대광
 */
public enum ResponseCodeEnum { 
	
	SUCCESS("0000", "처리 성공"),
	ERROR("9999", "처리 실패 (기타 오류, 알 수 없는 오류)"),
	
	// 입력값 검증
	NO_INPUT("0001", "{0} 입력해 주세요."),
	NO_INPUT_FORMAT("0002", "{0} {1} 형식에 맞게 입력해 주세요."),
	NO_INPUT_INVALID("0003", "{0} 에 유효하지 않은 문자열이 있습니다."),
	NO_INPUT_LENGTH("0004", "{0} - {1} 자리로 입력해 주세요."),
	NO_INPUT_LENGTH_TWO("0005", "{0} - {1} 자리 또는 {2} 자리로 입력해 주세요."),
	NO_INPUT_LENGTH_RANGE("0006", "{0} - {1}~{2} 자리로 입력해 주세요."),

	// Byte 검증
	OVER_BYTE_UTF8("1000", "{0} - 최대 {1} byte를 초과할 수 없습니다.\n(한글 3byte, 그외 1byte"),
	OVER_BYTE_EUCKR("1001", "{0} - 최대 {1} byte를 초과할 수 없습니다.\n(한글 2byte, 그외 1byte"),
	
	// 파일
	FILE_SIZE_LIMIT("2000", "첨부하려는 파일의 크기가 서버에서 허용하는 크기보다 큽니다."),
	FILE_IS_NOT_TYPE("2001", "허용되는 파일의 종류가 아닙니다."),
	
	// 로그인
	LOGIN_INVALID("3000", "아이디 또는 비밀번호를 다시 확인해주세요."),
	ACCOUNT_LOCK("3001", "비밀번호 {0}회 오류로 계정이 잠겼습니다.\n비밀번호 찾기를 진행해주세요."),
	INACTIVE_ACCOUNT("3002", "일정기간 ({0}) 이용하지 않아 휴면상태로 전환되었습니다."),
	
	// DB
	DB_CONNECTION_FAIL("4000", "DB 연결에 실패했습니다.\n잠시 후 다시 시도해주세요.\n지속적인 오류 발생 시 관리자에게 문의바랍니다."),
	DB_TIMEOUT("4001", "일시적으로 DB 연결이 지연되고 있습니다.\n잠시 후 다시 시도해주세요.\n지속적인 오류 발생 시 관리자에게 문의바랍니다."),
	DB_PROCESS_ERROR("4002", "데이터 처리 중 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.\n지속적인 오류 발생 시 관리자에게 문의바랍니다."),
	DB_EMPTY_DATA("4003", "등록된 데이터가 없습니다."),
	
	// 통신
	COM_CONNECTION_FAIL("5000", "{0} 서버 연결에 실패했습니다.\n잠시 후 다시 시도해주세요.\n지속적인 오류 발생 시 관리자에게 문의바랍니다."),
	COM_TIMEOUT("5001", "일시적으로 {0} 서버 연결이 지연되고 있습니다.\n잠시 후 다시 시도해주세요.\n지속적인 오류 발생 시 관리자에게 문의바랍니다."),
	COM_IS_NOT_SEND("5002", "{0} 서버로 데이터 요청에 실패했습니다.\n관리자에게 문의바랍니다."),
	COM_IS_NOT_RECEVED("5003", "{0} 서버로부터 데이터 응답에 실패했습니다.\n관리자에게 문의바랍니다."),
	
	// 암복호화
	ENCRYPT_ERROR("6000", "{0} - 암호화 오류"),
	DECRYPT_ERROR("6001", "{0} - 복호화 오류"),
	SIGN_ERROR("6002", "{0} - 서명 확인 오류"),
	
	// 인증, 인가
	ACCESS_DENIED("7000", "접근 권한이 없습니다."),
	IS_NOT_ALLOWED_IP("7001", "허용되지 않은 IP 입니다."),
	IS_NOT_HTTP_METHOD("7002", "허용되지 않은 HTTP Method 입니다."),
	ACCESS_TOEKN_INVALID("7003", "인증정보가 올바르지 않습니다."),
	ACCESS_TOKEN_EXPIRED("7004", "인증정보가 만료되었습니다."),
	REFRESH_TOKEN_EXPIRED("7005", "인증정보 갱신기간이 만료되었습니다."),
	;
	
	// XXX : 그외 부류는 A000 ~ Z900 으로 처리
	
	private String code;
	private String message;
	
	private ResponseCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}

	public String getMessage(Object ... arguments) {
		return MessageFormat.format(message, arguments);
	}
	
}

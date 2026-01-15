package common.util;

import java.net.InetAddress;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 2. 5.  kdk		최초작성
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영, 제미나이에 의한 일부 코드 개선
 * </pre>
 *
 *
 * @author kdk
 */
public class MaskingUtil {

	private MaskingUtil() {
		super();
	}

	/**
	 * <pre>
	 * 이름 마스킹
	 *  - 한글
	 *   : 2글자 첫자리 제외한 마스킹
	 *   : 첫자리, 마지막자리 제외한 마스킹
	 *  - 영문
	 *   : 4자리 이하 : 앞 2자리 제외하고 마스킹
	 *   : 4자리 이상 : 앞 4자리 제외하고 마스킹
	 * </pre>
	 *
	 * @param name
	 * @return
	 */
	public static String maskName(String name) {
		if (StringUtils.isBlank(name)) {
			return "";
		}

		if (name.length() > 50) {
			return name;
		}

		String regex = "[ㄱ-ㅎㅏ-ㅣ가-힣]+";

		if (name.matches(regex)) {
			return maskKoreanName(name);
		} else {
			return maskEnglishName(name);
		}
	}

	private static String maskKoreanName(String name) {
		int length = name.length();
		char firstChar = name.charAt(0);

	    if (length == 2) {
	    	return firstChar + "*";
	    }

	    char lastChar = name.charAt(length - 1);

	    StringBuilder maskedMiddle = new StringBuilder();
	    for (int i = 0; i < length - 2; i++) {
	        maskedMiddle.append("*");
	    }

	    return firstChar + maskedMiddle.toString() + lastChar;
	}

	private static String maskEnglishName(String name) {
		int length = name.length();
		StringBuilder sb = new StringBuilder();

		if (length <= 4) {
			sb.append(name.substring(0, length -2));
			sb.append("**");
		} else {
			sb.append(name.substring(0, 4));
			for (int i = 0; i < length - 4; i++) {
				sb.append("*");
			}
		}

		return sb.toString();
	}

	/**
	 * <pre>
	 * 주민등록번호 마스킹
	 *  - 뒷자리 마스킹
	 * </pre>
	 *
	 * @param rrn
	 * @param isShowGender
	 * @return
	 */
	public static String maskRRN(String rrn, boolean isShowGender) {
		if (rrn == null || rrn.length() != 14 || rrn.charAt(6) != '-') {
			throw new IllegalArgumentException("Invalid Resident Registration Number");
		}

		if (isShowGender) {
			return rrn.substring(0, 8) + "******"; // 성별 숫자까지 표시
		} else {
			return rrn.substring(0, 7) + "*******"; // 뒷자리 전체 마스킹
		}
	}

	/**
	 * <pre>
	 * 여권번호 마스킹
	 *  - 뒤 4자리 마스킹
	 * </pre>
	 *
	 * @param passportNumber
	 * @return
	 */
	public static String maskPassportNumber(String passportNumber) {
		if (passportNumber == null || passportNumber.length() < 5) {
			throw new IllegalArgumentException("Invalid Passport Number");
		}

		int length = passportNumber.length();
		StringBuilder maskedPart = new StringBuilder();
	    for (int i = 0; i < 4; i++) {
	        maskedPart.append("*");
	    }

		return passportNumber.substring(0, length - 4) + maskedPart.toString();
	}

    /**
     * <pre>
     * 전화번호 마스킹
     *  - 가운데 부분 마스킹
     *
     *  - 일반 전화번호
     *  - 070 인터넷 전화(VoIP)
     *  - 080 수신자 부담 전화
     *  - 030, 050 평생번호 및 안심번호
     *  - 휴대폰 번호
     * </pre>
     * @param phoneNumber
     * @return
     */
    public static String maskPhoneNum(String phoneNumber) {
    	if (StringUtils.isBlank(phoneNumber)) {
            return "";
        }

    	String[] parts = phoneNumber.split("-");

    	if (parts.length == 3) {
    		String middlePart = parts[1];
    		StringBuilder maskedMiddle = new StringBuilder();

    		for (int i = 0; i < middlePart.length(); i++) {
    			maskedMiddle.append("*");
    		}

    		return parts[0] + "-" + maskedMiddle.toString() + "-" + parts[2];
    	}

    	return phoneNumber;
    }

	/**
	 * <pre>
	 * 이메일 마스킹
	 *  - ID 4번재 자리부터 마스킹
	 * </pre>
	 *
	 * @param email
	 * @return
	 */
	public static String maskEmail(String email) {
		if ( StringUtils.isBlank(email) ) {
			return "";
		}

		int atIndex = email.indexOf("@");

		if ( atIndex < 1 ) {
			throw new IllegalArgumentException("Invalid email address");
		}

		String idPart = email.substring(0, atIndex);
		String domainPart = email.substring(atIndex);

		String maskedIdPart = maskId(idPart);

		return maskedIdPart + domainPart;
	}

	/**
	 * <pre>
	 * 아이디 마스킹
	 *  - 4번째 자리부터 마스킹
	 * </pre>
	 *
	 * @param id
	 * @return
	 */
	public static String maskId(String id) {
		if ( StringUtils.isBlank(id) ) {
			return "";
		}

		if (id.length() <= 3) {
			return id; // 아이디가 3글자 이하인 경우 마스킹하지 않음
		} else {
			int repeatCount = id.length() - 3;

			StringBuilder maskedStars = new StringBuilder();
			for (int i = 0; i < repeatCount; i++) {
                maskedStars.append("*");
            }

			return id.substring(0, 3) + maskedStars.toString(); // 앞 3글자만 표시하고 나머지 마스킹
		}
	}

	/**
	 * <pre>
	 * 주소 마스킹
	 *  - 도로명 이하의 건물번호 및 상세주소의 숫자
	 * </pre>
	 *
	 * @param address
	 * @return
	 */
	public static String maskRoadAddress(String address) {
		if ( StringUtils.isBlank(address) ) {
			return "";
		}

		return address.replaceAll("\\d", "*");
	}

    /**
     * <pre>
     * 카드번호 마스킹
     * - startIndex 부터 지정된 길이만큼 마스킹
     * - 일반적으로 16자리 중 7번째부터 12번째 숫자 (혹은 9번째부터 12번째 숫자)를
     * </pre>
     *
     * @param cardNumber 마스킹할 카드번호
     * @param startIndex 마스킹 시작 인덱스 (1부터 시작, 카드 번호의 실제 숫자 위치 기준). 7 또는 9.
     * @return 마스킹된 카드번호
     * @throws IllegalArgumentException 유효하지 않은 startIndex 또는 카드 번호 길이
     */
    public static String maskCardNumber(String cardNumber, int startIndex) {
        if (StringUtils.isBlank(cardNumber)) {
            return "";
        }

        // 입력된 startIndex가 유효한지 검증 (1부터 시작하는 인덱스로 가정)
        if (startIndex != 7 && startIndex != 9) {
            throw new IllegalArgumentException("Invalid start index. It should be either 7 or 9.");
        }

        // 1. 카드 번호에서 모든 하이픈 제거
        String plainCardNumber = cardNumber.replace("-", "");

        // 2. 카드번호 길이 검증
        if (plainCardNumber.length() != 15 && plainCardNumber.length() != 16) {
            throw new IllegalArgumentException("Invalid card number length. It should be either 15 or 16 digits.");
        }

        // 3. 마스킹할 길이 결정 (startIndex에 따라 마스킹할 '*'의 개수가 다름)
        int maskLength;
        if (startIndex == 7) {
            // 16자리 카드: 7번째부터 4자리 마스킹 (xxxx-xx**-****-xxxx)
            // 15자리 카드: 7번째부터 6자리 마스킹 (xxxx-xx**-*****-xxx)
            // 실제 구현에서는 plainCardNumber의 길이에 따라 달라짐
            maskLength = (plainCardNumber.length() == 16) ? 4 : 6;
        } else { // startIndex == 9
            // 9번째부터 4자리 마스킹 (xxxxxxxx-****-xxxx)
            maskLength = 4;
        }

        // 4. 순수 숫자 문자열에 마스킹 적용
        StringBuilder maskedPlainBuilder = new StringBuilder(plainCardNumber);
        // startIndex는 1부터 시작하는 실제 카드번호의 위치이므로, 배열 인덱스로는 startIndex - 1
        for (int i = 0; i < maskLength; i++) {
            // 마스킹 시작 인덱스부터 maskLength만큼 '*'로 대체
            maskedPlainBuilder.setCharAt(startIndex - 1 + i, '*');
        }
        String maskedPlainNumber = maskedPlainBuilder.toString();

        // 5. 원래 카드 번호의 하이픈 위치를 기준으로 마스킹된 번호에 하이픈 재삽입
        StringBuilder finalMaskedCardNumber = new StringBuilder();
        int plainIndex = 0; // plainCardNumber의 현재 인덱스

        for (char originalChar : cardNumber.toCharArray()) {
            if (originalChar == '-') {
                finalMaskedCardNumber.append('-'); // 원래 하이픈 위치에 하이픈 추가
            } else {
                // 하이픈이 아닌 경우, 마스킹된 순수 숫자 문자열에서 해당 숫자/마스크 문자를 가져옴
                finalMaskedCardNumber.append(maskedPlainNumber.charAt(plainIndex));
                plainIndex++;
            }
        }

        return finalMaskedCardNumber.toString();
    }

	/**
	 * <pre>
	 * 계좌번호 마스킹
	 *  - 뒤에서부터 5자리 마스킹
	 * </pre>
	 * @param accountNumber
	 * @return
	 */
	public static String maskAccountNumber(String accountNumber) {
		if ( StringUtils.isBlank(accountNumber) ) {
			return "";
		}

		char[] chars = accountNumber.toCharArray();
		int maskedCount = 0;

		for (int i = chars.length - 1; i >= 0; i--) {
			if (Character.isDigit(chars[i])) {
				chars[i] = '*';
				maskedCount++;
			}
			if (maskedCount == 5) break;
		}

		return new String(chars);
	}

	/**
	 * <pre>
	 * 생년월일 마스킹
	 *  - 년도 마스킹
	 * </pre>
	 * @param birthdate
	 * @return
	 */
	public static String maskBirthdate(String birthdate) {
		if (StringUtils.isBlank(birthdate)) {
			return "";
		}

		// 입력값에 '-' 포함 여부 확인
        boolean hasHyphen = birthdate.contains("-");

		// 숫자만 추출
		String digitsOnly = birthdate.replaceAll("[^\\d]", "");

		// 올바른 형식인지 확인
        if (digitsOnly.length() == 8) {
        	String maskedDate = "****" + digitsOnly.substring(4, 6) + digitsOnly.substring(6, 8);
            return hasHyphen ? maskedDate.substring(0, 4) + "-" + maskedDate.substring(4, 6) + "-" + maskedDate.substring(6, 8) : maskedDate;
        } else {
        	return "";
        }
    }

	/**
	 * <pre>
	 * IP 주소 마스킹
	 *  - 뒤 3자리 마스킹
	 * </pre>
	 * @param ipAddress
	 * @return
	 */
	public static String maskIPAddress(String ipAddress) {
		if (StringUtils.isBlank(ipAddress)) {
			return "";
		}

        // IPv4 형식 확인
		if (ipAddress.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
		    int lastDotIndex = ipAddress.lastIndexOf('.');
		    if (lastDotIndex != -1) {
		        String prefix = ipAddress.substring(0, lastDotIndex + 1);
		        return prefix + "***";
		    }
		}
		return "";
    }

	/**
	 * <pre>
	 * IPv6 주소 마스킹
	 *  - 뒤 2개 블록 마스킹
	 * </pre>
	 * @param ipv6Address
	 * @return
	 */
	public static String maskIPv6Address(String ipv6Address) {
		if (StringUtils.isBlank(ipv6Address)) {
			return "";
		}

        // Scope ID 제거
        String cleanAddress = ipv6Address.split("%")[0];

        try {
        	InetAddress.getByName(cleanAddress);

        	return cleanAddress.replaceAll("(:[0-9a-fA-F]{1,4}){2}$", ":****:****");
        } catch (Exception e) {
            return "";
        }
    }

	/**
	 * <pre>
	 * 학번 마스킹
	 *  - 입학 연도 외 마스킹
	 * </pre>
	 * @param studentID
	 * @return
	 */
	public static String maskStudentID(String studentID) {
		if (StringUtils.isBlank(studentID)) {
			return "";
		}

        // 숫자만 포함된 학번 (예: 202312345)
        if (studentID.matches("\\d{8,9}")) {
            return studentID.substring(0, 4) + "****";
        }
        // 문자와 숫자가 섞인 학번 (예: 23AB1234)
        else if (studentID.matches("\\d{2}[A-Za-z]{2}\\d{4}")) {
            return studentID.substring(0, 2) + "**" + studentID.substring(4, 8);
        }

        return "";
    }

	/**
	 * <pre>
	 * 비밀번호 마스킹
	 * - 계정 비밀번호는 대상 아님
	 * </pre>
	 * @param password
	 * @return
	 */
	public static String maskPassword(String password) {
		if (StringUtils.isBlank(password)) {
			return "";
		}

		char[] c = new char[password.length()];
		Arrays.fill(c, '*');
		return String.valueOf(c);
	}

}

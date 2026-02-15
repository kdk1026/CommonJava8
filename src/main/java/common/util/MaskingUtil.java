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
		if (StringUtils.isBlank(name)) {
			return "";
		}

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
		if (StringUtils.isBlank(name)) {
			return "";
		}

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
	 * 주민등록번호 마스킹 (하이픈 포함)
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
			// 성별 숫자까지 표시
			return rrn.substring(0, 8) + "******";
		} else {
			// 뒷자리 전체 마스킹
			return rrn.substring(0, 7) + "*******";
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

    	String cleanNum = phoneNumber.replace("-", "");
    	int len = cleanNum.length();

    	if (len >= 9) {
    		String last = cleanNum.substring(len - 4);

    		int firstPartEndIdx = cleanNum.startsWith("02") ? 2 : 3;
    		String first = cleanNum.substring(0, firstPartEndIdx);

    		String middlePart = cleanNum.substring(firstPartEndIdx, len - 4);

    		StringBuilder maskedMiddle = new StringBuilder();
    		for (int i = 0; i < middlePart.length(); i++) {
    			maskedMiddle.append("*");
    		}

    		return first + "-" + maskedMiddle.toString() + "-" + last;
    	}

    	return cleanNum;
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

		int length = id.length();

		if (length <= 3) {
			return id; // 아이디가 3글자 이하인 경우 마스킹하지 않음
		} else {
			int repeatCount = length - 3;

			StringBuilder maskedStars = new StringBuilder();
			for (int i = 0; i < repeatCount; i++) {
                maskedStars.append("*");
            }

			return id.substring(0, 3) + maskedStars.toString(); // 앞 3글자만 표시하고 나머지 마스킹
		}
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
     * - startIndex (7 or 9)
     * - 일반적으로 15/16자리 중 7번째부터 12번째 숫자 (혹은 9번째부터 12번째 숫자)를 마스킹
     * </pre>
     *
     * @param cardNumber
     * @param startIndex
     * @return
     */
    public static String maskCardNumber(String cardNumber, int startIndex) {
        if (StringUtils.isBlank(cardNumber)) {
            return "";
        }

        if (startIndex != 7 && startIndex != 9) {
        	throw new IllegalArgumentException("Invalid start index. It should be either 7 or 9.");
        }

        String digitsOnly = cardNumber.replaceAll("[^\\d]", "");
        int length = digitsOnly.length();

        char[] chars = digitsOnly.toCharArray();

        // 마스킹 제한 설정 (7일 때 6개, 9일 때 4개)
        int maskLimit = (startIndex == 7) ? 6 : 4;
        int maskedSoFar = 0;

        for (int i = 0; i < chars.length; i++) {
        	if ((i + 1) >= startIndex && maskedSoFar < maskLimit) {
        		chars[i] = '*';
        		maskedSoFar++;
    		}
        }

        String maskedStr = new String(chars);

        return formatByLength(maskedStr, length);
    }

    private static String formatByLength(String maskedStr, int length) {
    	if (length == 16) {
            return maskedStr.substring(0, 4) + "-" +
                    maskedStr.substring(4, 8) + "-" +
                    maskedStr.substring(8, 12) + "-" +
                    maskedStr.substring(12);
         }
    	 else if (length == 15) {
            return maskedStr.substring(0, 4) + "-" +
                   maskedStr.substring(4, 10) + "-" +
                   maskedStr.substring(10);
        }

        return maskedStr;
    }

	/**
	 * <pre>
	 * 계좌번호 마스킹 (하이픈 포함)
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

        boolean hasHyphen = birthdate.contains("-");

		String digitsOnly = birthdate.replaceAll("[^\\d]", "");

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
        	// 유효한 IP 주소인지 검증
        	InetAddress.getByName(cleanAddress);

        	return cleanAddress.replaceAll("(:[0-9a-fA-F]{1,4}){2}$", ":****:****");
        } catch (Exception e) {
            return "";
        }
    }

	/**
	 * <pre>
	 * 학번 마스킹
	 *  - 숫자 : 입학 연도 외 마스킹
	 *  - 문자 + 숫자 : 문자만 마스킹
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

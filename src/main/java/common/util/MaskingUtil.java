package common.util;

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
	 *  - 3자리 : 가운데 마스킹
	 *  - 4자리 : 가운데 2자리 마스킹
	 * </pre>
	 *
	 * @param name
	 * @return
	 */
	public static String maskName(String name) {
		String regex = ".*?[ㄱ-ㅎㅏ-ㅣ가-힣]+.*";

		if (name.matches(regex)) {
			// 한글 이름 마스킹
			int length = name.length();
			if (length == 2) {
	            return name.charAt(0) + "*";
	        } else if (length == 3) {
				return name.charAt(0) + "*" + name.charAt(2);
			} else if (length == 4) {
				return name.charAt(0) + "**" + name.charAt(3);
			}
		} else {
			// 영문 이름 마스킹
			if ( name.length() <=4 ) {
				StringBuilder maskedName = new StringBuilder();
		        for (int i = 0; i < name.length(); i++) {
		            maskedName.append("*");
		        }
		        return maskedName.toString();
			} else {
				StringBuilder maskedPart = new StringBuilder();
		        for (int i = 0; i < name.length() - 4; i++) {
		            maskedPart.append("*");
		        }
		        return name.substring(0, 4) + maskedPart.toString();
			}
		}

		return "";
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
	 * 연락처 마스킹
	 *  - 가운데 마스킹
	 * </pre>
	 *
	 * @param phoneNumber
	 * @return
	 */
    /**
     * <pre>
     * 연락처 마스킹 (가운데 부분 마스킹)
     * 다양한 전화번호 형식 (유/무선, 지역번호 포함/제외)을 처리하여 가운데 번호를 마스킹합니다.
     * </pre>
     *
     * @param phoneNumber 마스킹할 전화번호
     * @return 마스킹된 전화번호. 유효하지 않은 형식의 경우 빈 문자열 반환.
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return "";
        }

        // 1. 모든 하이픈 제거 (정규화)
        String digitsOnly = phoneNumber.replaceAll("[^\\d]", ""); // 숫자만 추출

        // 2. 다양한 전화번호 형식에 맞는 정규 표현식으로 마스킹 로직 적용
        // 번호 길이에 따라 마스킹 규칙을 적용합니다.
        // 마스킹 규칙은 앞부분 + 마스크 + 뒷부분
        // 예시: 010-1234-5678 -> 010-****-5678
        //       02-1234-5678 -> 02-****-5678
        //       02-123-4567  -> 02-***-4567

        String maskedDigits = "";

        // 휴대전화 (10~11자리): 0XX-ABCD-EFGH 또는 0XX-ABC-EFGH
        // 010, 011, 016, 017, 018, 019로 시작하는 10자리 또는 11자리
        if (digitsOnly.matches("^01[016789]\\d{7,8}$")) {
            // 11자리 (010-ABCD-EFGH) -> 010-****-EFGH
            if (digitsOnly.length() == 11) {
                maskedDigits = digitsOnly.substring(0, 3) + "****" + digitsOnly.substring(7);
            }
            // 10자리 (01X-ABC-DEFG) -> 01X-***-DEFG (옛날 번호)
            else if (digitsOnly.length() == 10) {
                 maskedDigits = digitsOnly.substring(0, 3) + "***" + digitsOnly.substring(6);
            }
        }
        // 서울 지역번호 (02)
        // 9자리: 02-ABC-DEFG -> 02-***-DEFG
        // 10자리: 02-ABCD-EFGH -> 02-****-EFGH
        else if (digitsOnly.startsWith("02") && (digitsOnly.length() == 9 || digitsOnly.length() == 10)) {
            if (digitsOnly.length() == 9) {
                maskedDigits = digitsOnly.substring(0, 2) + "***" + digitsOnly.substring(5);
            } else { // length == 10
                maskedDigits = digitsOnly.substring(0, 2) + "****" + digitsOnly.substring(6);
            }
        }
        // 그 외 지역번호 (0XX) (10~11자리)
        // 03X, 04X, 05X, 06X 로 시작
        else if (digitsOnly.matches("^0[3-6]\\d{8,9}$")) { // 03x~06x로 시작하고 총 10~11자리
            // 10자리 (0XX-ABC-DEFG) -> 0XX-***-DEFG
            if (digitsOnly.length() == 10) {
                maskedDigits = digitsOnly.substring(0, 3) + "***" + digitsOnly.substring(6);
            }
            // 11자리 (0XX-ABCD-EFGH) -> 0XX-****-EFGH
            else if (digitsOnly.length() == 11) {
                maskedDigits = digitsOnly.substring(0, 3) + "****" + digitsOnly.substring(7);
            }
        }
        // 050, 070 (인터넷 전화 등) (11자리)
        else if (digitsOnly.startsWith("050") || digitsOnly.startsWith("070") && digitsOnly.length() == 11) {
             maskedDigits = digitsOnly.substring(0, 3) + "****" + digitsOnly.substring(7);
        }
        // 일반 전화 (7~8자리) (지역번호 없는 번호, 국번+번호)
        else if (digitsOnly.length() >= 7 && digitsOnly.length() <= 8) {
            // 마지막 4자리를 마스킹
            maskedDigits = digitsOnly.substring(0, digitsOnly.length() - 4) + "****";
        }
        // 그 외 알려진 형식에 해당하지 않는 경우 (유효하지 않은 번호로 간주)
        else {
            return "";
        }

        // 3. 원래 하이픈 위치에 맞춰 하이픈 재삽입
        StringBuilder finalMaskedPhoneNumber = new StringBuilder();
        int digitIndex = 0; // 숫자만 있는 문자열의 인덱스

        for (char originalChar : phoneNumber.toCharArray()) {
            if (originalChar == '-') {
                finalMaskedPhoneNumber.append('-');
            } else if (digitIndex < maskedDigits.length()){
                finalMaskedPhoneNumber.append(maskedDigits.charAt(digitIndex));
                digitIndex++;
            } else {
                // 원본에 하이픈은 있는데, 숫자가 더이상 없어서 마스킹된 숫자 문자열을 벗어나는 경우
                // (이런 경우는 거의 없겠지만 안전장치)
                finalMaskedPhoneNumber.append(originalChar);
                digitIndex++;
            }
        }

        return finalMaskedPhoneNumber.toString();
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
		if (atIndex <= 2) {
			throw new IllegalArgumentException("Invalid email address");
		}
		String idPart = email.substring(0, atIndex);
		String domainPart = email.substring(atIndex);

		int repeatCount = idPart.length() - 2;
		if (repeatCount < 0) {
			repeatCount = 0;
		}

		StringBuilder maskedStars = new StringBuilder();
		for (int i = 0; i < repeatCount; i++) {
            maskedStars.append("*");
        }

		String maskedIdPart = idPart.substring(0, 2) + maskedStars.toString();

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

	    // 계좌번호에서 '-' 제거
	    String cleanAccountNumber = accountNumber.replace("-", "");

	    // 계좌번호 길이가 5자리 이하일 경우 예외 처리
	    if (cleanAccountNumber.length() <= 5) {
	    	StringBuilder fullMasked = new StringBuilder();
	    	for (int i = 0; i < cleanAccountNumber.length(); i++) {
                fullMasked.append("*");
            }
            return fullMasked.toString();
	    }

	    // 마스킹할 부분과 남길 부분 분리
	    String visiblePart = cleanAccountNumber.substring(0, cleanAccountNumber.length() - 5);

	    StringBuilder fiveStars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            fiveStars.append("*");
        }
        String maskedPart = fiveStars.toString();

	    // 원래 계좌번호에 '-'가 있을 경우 다시 추가
	    if (accountNumber.contains("-")) {
	    	StringBuilder maskedBuilder = new StringBuilder();
	    	int cleanIndex = 0;
	        for (char ch : accountNumber.toCharArray()) {
	            if (ch == '-') {
	            	maskedBuilder.append('-');
	            } else {
	            	if (cleanIndex < visiblePart.length()) {
	            		maskedBuilder.append(visiblePart.charAt(cleanIndex));
	            	} else {
	            		maskedBuilder.append('*');
	            	}
	            	cleanIndex++;
	            }
	        }
	        return maskedBuilder.toString();
	    } else  {
	    	return visiblePart + maskedPart;
	    }
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
        // Scope ID 제거
        String cleanAddress = ipv6Address.split("%")[0];

        // IPv6 정규식 (축약형 포함)
        String ipv6Regex = "(([0-9a-fA-F]{1,4}:){1,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:))";

        // IPv6 형식 확인
        if (cleanAddress.matches(ipv6Regex)) {
            return cleanAddress.replaceAll("(:[0-9a-fA-F]{1,4}){2}$", ":****:****");
        } else {
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

}

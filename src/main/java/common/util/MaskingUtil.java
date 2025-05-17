package common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 2. 5. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
public class MaskingUtil {

	private MaskingUtil() {
		super();
	}

	private static class LazyHolder {
		private static final MaskingUtil INSTANCE = new MaskingUtil();
	}

	public static MaskingUtil getInstance() {
		return LazyHolder.INSTANCE;
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
	public String maskName(String name) {
		String regex = ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*";

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
			if (name.length() > 4) {
				return name.substring(0, 4) + "*".repeat(name.length() - 4);
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
	public String maskRRN(String rrn, boolean isShowGender) {
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
	public String maskPassportNumber(String passportNumber) {
		if (passportNumber == null || passportNumber.length() < 5) {
			throw new IllegalArgumentException("Invalid Passport Number");
		}

		int length = passportNumber.length();
		return passportNumber.substring(0, length - 4) + "*".repeat(4);
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
	public String maskPhoneNumber(String phoneNumber) {
		if (phoneNumber.contains("-")) {
			// '-'가 있는 경우 가운데를 마스킹
			String[] parts = phoneNumber.split("-");
			if (parts.length == 3) {
				return parts[0] + "-****-" + parts[2];
			} else if (parts.length == 2 && parts[0].length() == 2) {
				return parts[0] + "-***-" + parts[1];
			}
		} else {
			// '-'가 없는 경우 번호 형식에 따라 마스킹
			if (phoneNumber.startsWith("02") && phoneNumber.length() == 9) {
				return phoneNumber.substring(0, 2) + "***" + phoneNumber.substring(5);
			} else if (phoneNumber.startsWith("02") && phoneNumber.length() == 10) {
				return phoneNumber.substring(0, 2) + "****" + phoneNumber.substring(6);
			} else if (phoneNumber.startsWith("050") || phoneNumber.startsWith("070")) {
				return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
			} else if (phoneNumber.startsWith("031") || phoneNumber.startsWith("032") || phoneNumber.startsWith("033")
					|| phoneNumber.startsWith("041") || phoneNumber.startsWith("042") || phoneNumber.startsWith("043")
					|| phoneNumber.startsWith("044") || phoneNumber.startsWith("051") || phoneNumber.startsWith("052")
					|| phoneNumber.startsWith("053") || phoneNumber.startsWith("054") || phoneNumber.startsWith("055")
					|| phoneNumber.startsWith("061") || phoneNumber.startsWith("062") || phoneNumber.startsWith("063")
					|| phoneNumber.startsWith("064")) {
				if (phoneNumber.length() == 10) {
					return phoneNumber.substring(0, 3) + "***" + phoneNumber.substring(6);
				} else if (phoneNumber.length() == 11) {
					return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
				}
			} else if (phoneNumber.startsWith("010") && phoneNumber.length() == 11) {
				return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
			} else if (phoneNumber.length() > 7) {
				return phoneNumber.substring(0, phoneNumber.length() - 4) + "****";
			}
		}

		return "";
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
	public String maskEmail(String email) {
		if ( StringUtils.isBlank(email) ) {
			return "";
		}

		int atIndex = email.indexOf("@");
		if (atIndex <= 2) {
			throw new IllegalArgumentException("Invalid email address");
		}
		String idPart = email.substring(0, atIndex);
		String domainPart = email.substring(atIndex);
		String maskedIdPart = idPart.substring(0, 3) + "*".repeat(idPart.length() - 2);

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
	public String maskId(String id) {
		if ( StringUtils.isBlank(id) ) {
			return "";
		}

		if (id.length() <= 3) {
			return id; // 아이디가 3글자 이하인 경우 마스킹하지 않음
		} else {
			return id.substring(0, 3) + "*".repeat(id.length() - 3); // 앞 3글자만 표시하고 나머지 마스킹
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
	public String maskRoadAddress(String address) {
		if ( StringUtils.isBlank(address) ) {
			return "";
		}

		return address.replaceAll("\\d", "*");
	}

	/**
	 * <pre>
	 * 카드번호 마스킹
	 *  - startIndex 부터 12번째 자리 마스킹
	 * </pre>
	 *
	 * @param cardNumber
	 * @param startIndex (7 or 9)
	 * @return
	 */
	public String maskCardNumber(String cardNumber, int startIndex) {
		if ( StringUtils.isBlank(cardNumber) ) {
			return "";
		}

		if (startIndex != 7 && startIndex != 9) {
			throw new IllegalArgumentException("Invalid start index. It should be either 7 or 9.");
		}

		// 카드 번호의 모든 숫자를 제거한 문자열을 얻음
		String plainCardNumber = cardNumber.replace("-", "");

		// 카드번호가 15자리나 16자리가 아닌 경우 예외 발생
		if (plainCardNumber.length() != 15 && plainCardNumber.length() != 16) {
			throw new IllegalArgumentException("Invalid card number length. It should be either 15 or 16 digits.");
		}

		StringBuilder maskedCardNumber = new StringBuilder();

		// startIndex에 따른 분기 처리
		if (startIndex == 7) {
			if (cardNumber.contains("-")) {
				int maskedIndex = 0;
				for (int i = 0; i < cardNumber.length(); i++) {
					char ch = cardNumber.charAt(i);
					if (ch == '-') {
						maskedCardNumber.append('-');
					} else {
						if (maskedIndex >= startIndex - 1 && maskedIndex < startIndex + 4 + 1) {
							maskedCardNumber.append('*');
						} else {
							maskedCardNumber.append(ch);
						}
						maskedIndex++;
					}
				}
			} else {
				if (plainCardNumber.length() == 16) {
					maskedCardNumber.append(plainCardNumber.substring(0, startIndex - 1));
					maskedCardNumber.append("****");
					maskedCardNumber.append(plainCardNumber.substring(startIndex + 4 + 1));
				} else if (plainCardNumber.length() == 15) {
					maskedCardNumber.append(plainCardNumber.substring(0, startIndex - 1));
					maskedCardNumber.append("******");
					maskedCardNumber.append(plainCardNumber.substring(startIndex + 4 + 1));
				}
			}
		} else if (startIndex == 9) {
			if (cardNumber.contains("-")) {
				int maskedIndex = 0;
				for (int i = 0; i < cardNumber.length(); i++) {
					char ch = cardNumber.charAt(i);
					if (ch == '-') {
						maskedCardNumber.append('-');
					} else {
						if (maskedIndex >= startIndex - 1 && maskedIndex < startIndex + 3) {
							maskedCardNumber.append('*');
						} else {
							maskedCardNumber.append(ch);
						}
						maskedIndex++;
					}
				}
			} else {
				maskedCardNumber.append(plainCardNumber.substring(0, startIndex - 1));
				maskedCardNumber.append("****");
				maskedCardNumber.append(plainCardNumber.substring(startIndex + 3));
			}
		}

		return maskedCardNumber.toString();
	}

	/**
	 * <pre>
	 * 계좌번호 마스킹
	 *  - 뒤에서부터 5자리 마스킹
	 * </pre>
	 * @param accountNumber
	 * @return
	 */
	public String maskAccountNumber(String accountNumber) {
		if ( StringUtils.isBlank(accountNumber) ) {
			return "";
		}

	    // 계좌번호에서 '-' 제거
	    String cleanAccountNumber = accountNumber.replace("-", "");

	    // 계좌번호 길이가 5자리 이하일 경우 예외 처리
	    if (cleanAccountNumber.length() <= 5) {
	        return "*".repeat(cleanAccountNumber.length());
	    }

	    // 마스킹할 부분과 남길 부분 분리
	    String visiblePart = cleanAccountNumber.substring(0, cleanAccountNumber.length() - 5);
	    String maskedPart = "*".repeat(5);

	    // 원래 계좌번호에 '-'가 있을 경우 다시 추가
	    String maskedAccountNumber = visiblePart + maskedPart;
	    if (accountNumber.contains("-")) {
	        maskedAccountNumber = "";
	        int visibleIndex = 0;
	        for (char ch : accountNumber.toCharArray()) {
	            if (ch == '-') {
	                maskedAccountNumber += "-";
	            } else if (visibleIndex < visiblePart.length()) {
	                maskedAccountNumber += visiblePart.charAt(visibleIndex);
	                visibleIndex++;
	            } else {
	                maskedAccountNumber += "*";
	            }
	        }
	    }

	    return maskedAccountNumber;
	}

}

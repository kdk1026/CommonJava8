package common.util.crypto;

import lombok.Getter;
import lombok.ToString;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 6. 1. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Getter
@ToString
public class EncryptResult {

	private String encryptedText;
    private String iv; // Base64 인코딩된 IV 문자열

    public EncryptResult(String encryptedText, String iv) {
        this.encryptedText = encryptedText;
        this.iv = iv;
    }

}

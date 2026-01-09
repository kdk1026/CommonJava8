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

	private String cipherText;
    private String iv; // Base64 인코딩된 IV 문자열

    public EncryptResult(String encryptedText, String iv) {
        this.cipherText = encryptedText;
        this.iv = iv;
    }

}

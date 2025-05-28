package common.tcp.socket;

 /**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 5. 28. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
public class SslSocketTrustStoreVo {

	private String trustStorePath;
	private String trustStorePassword;

	/**
	 * @return the trustStorePath
	 */
	public String getTrustStorePath() {
		return trustStorePath;
	}
	/**
	 * @param trustStorePath the trustStorePath to set
	 */
	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}
	/**
	 * @return the trustStorePassword
	 */
	public String getTrustStorePassword() {
		return trustStorePassword;
	}
	/**
	 * @param trustStorePassword the trustStorePassword to set
	 */
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

}

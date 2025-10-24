package common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	JavaDoc 작성 (SonarLint 지시에 따른 수정)
 * </pre>
 *
 *
 * @author 김대광
 */
public class PagingUtil {

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

		public static String isNegative(String paramName) {
			return String.format("'%s' is negative", paramName);
		}

	}

	/** 페이지당 행수, MySQL LIMIT */
	private int pagePerRow;
	/** 화면당 페이지수 */
	private int pagePerScreen;
	/** 전체 행수(데이터 건수) */
	private int totalCnt;

	/** 현재 페이지 번호 */
	private int currentPage;
	/** 전체 페이지수 */
	private int totalPage;
	/** 시작 페이지 번호 */
	private int firstPage;
	/** 종료 페이지 번호 */
	private int lastPage;

	/** 전체 블럭 */
	private int totalBlock;

	/** 이전 블럭 페이지 번호 */
	private int prevBlockPage;
	/** 다음 블럭 페이지 번호 */
	private int nextBlockPage;

	/** 링크 URL */
	private String linkUrl;

	/**
	 * SQL 시작 행번호<br/>
	 * MySQL의 경우 -1 (0부터 시작) 또는 offSet 사용
	 */
	private int start;
	/**
	 * SQL 종료 행번호<br/>
	 * MySQL의 경우 사용안함
	 */
	private int end;

	/** MySQL OFFSET */
	private int offSet;

	public PagingUtil(int pagePerRow, int pagePerScreen, int totalCnt, String currentPage, String linkUrl) {
		super();

		if ( pagePerRow <= 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("pagePerRow"));
		}

		if ( pagePerScreen <= 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("pagePerScreen"));
		}

		if ( totalCnt <= 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("totalCnt"));
		}

		if ( StringUtils.isBlank(currentPage) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("currentPage"));
		}

		boolean isNumeric = currentPage.matches("\\d+");
		if ( !isNumeric ) {
			throw new IllegalArgumentException("currentPage not number");
		}

		this.pagePerRow = pagePerRow;
		this.pagePerScreen = pagePerScreen;
		this.totalCnt = totalCnt;
		this.setCurrentPage(Integer.valueOf(currentPage));
		this.setLinkUrl(linkUrl);
		this.pagingProcess();
	}

	/**
	 * 페이징 처리 수행
	 */
	private void pagingProcess() {
		int nTotalPage = (int) Math.ceil((double)this.totalCnt / this.pagePerRow);
		this.setTotalPage(nTotalPage);

		if (this.currentPage < 1) {
			this.setCurrentPage(1);
		} else if (this.currentPage > this.totalPage) {
			this.setCurrentPage(this.totalPage);
		}

		if (this.totalPage == 0) {
			this.setCurrentPage(1);
		}

		int nLastPage = (int) Math.ceil((double)this.currentPage / this.pagePerScreen) * this.pagePerScreen;
		nLastPage = (nLastPage > this.totalPage) ? this.totalPage : nLastPage;
		this.setLastPage(nLastPage);

		int nFirstPage = (int) (Math.ceil((float)this.currentPage / this.pagePerScreen) * this.pagePerScreen) - (this.pagePerScreen -1);
		if (nFirstPage <= 0) {
			nFirstPage = 1;
		}
		this.setFirstPage(nFirstPage);

		int nTotalBlock = this.totalPage / this.pagePerScreen;
		this.setTotalBlock(nTotalBlock);

		int nPrevBlockPage = this.firstPage - 1;
		if (nPrevBlockPage < 1) {
			nPrevBlockPage = 1;
		}
		this.setPrevBlockPage(nPrevBlockPage);

		int nNextBlockPage = this.lastPage + 1;
		if (nNextBlockPage > this.totalPage) {
			nNextBlockPage = this.totalPage;
		}
		this.setNextBlockPage(nNextBlockPage);

		this.start = this.calcStart();
		this.end = this.calcEnd();

		int nOffSet = (this.currentPage - 1) * this.pagePerRow;
		this.setOffSet(nOffSet);
	}

	public int calcStart() {
		this.start = (this.currentPage - 1) * this.pagePerRow + 1;
		return this.start;
	}

	public int calcEnd() {
		this.end = this.currentPage * this.pagePerRow;
		return this.end;
	}

	/**
	 * @return the pagePerRow
	 */
	public int getPagePerRow() {
		return pagePerRow;
	}

	/**
	 * @param pagePerRow the pagePerRow to set
	 */
	public void setPagePerRow(int pagePerRow) {
		this.pagePerRow = pagePerRow;
	}

	/**
	 * @return the pagePerScreen
	 */
	public int getPagePerScreen() {
		return pagePerScreen;
	}

	/**
	 * @param pagePerScreen the pagePerScreen to set
	 */
	public void setPagePerScreen(int pagePerScreen) {
		this.pagePerScreen = pagePerScreen;
	}

	/**
	 * @return the totalCnt
	 */
	public int getTotalCnt() {
		return totalCnt;
	}

	/**
	 * @param totalCnt the totalCnt to set
	 */
	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @return the totalPage
	 */
	public int getTotalPage() {
		return totalPage;
	}

	/**
	 * @param totalPage the totalPage to set
	 */
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	/**
	 * @return the firstPage
	 */
	public int getFirstPage() {
		return firstPage;
	}

	/**
	 * @param firstPage the firstPage to set
	 */
	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	/**
	 * @return the lastPage
	 */
	public int getLastPage() {
		return lastPage;
	}

	/**
	 * @param lastPage the lastPage to set
	 */
	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	/**
	 * @return the totalBlock
	 */
	public int getTotalBlock() {
		return totalBlock;
	}

	/**
	 * @param totalBlock the totalBlock to set
	 */
	public void setTotalBlock(int totalBlock) {
		this.totalBlock = totalBlock;
	}

	/**
	 * @return the prevBlockPage
	 */
	public int getPrevBlockPage() {
		return prevBlockPage;
	}

	/**
	 * @param prevBlockPage the prevBlockPage to set
	 */
	public void setPrevBlockPage(int prevBlockPage) {
		this.prevBlockPage = prevBlockPage;
	}

	/**
	 * @return the nextBlockPage
	 */
	public int getNextBlockPage() {
		return nextBlockPage;
	}

	/**
	 * @param nextBlockPage the nextBlockPage to set
	 */
	public void setNextBlockPage(int nextBlockPage) {
		this.nextBlockPage = nextBlockPage;
	}

	/**
	 * @return the linkUrl
	 */
	public String getLinkUrl() {
		return linkUrl;
	}

	/**
	 * @param linkUrl the linkUrl to set
	 */
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @return the offSet
	 */
	public int getOffSet() {
		return offSet;
	}

	/**
	 * @param offSet the offSet to set
	 */
	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}


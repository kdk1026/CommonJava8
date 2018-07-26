package common.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PagingUtil {

	/** 페이지당 행수 */
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
	/** 이전 페이지 번호 */
	private int prevPage;
	/** 다음 페이지 번호 */
    private int nextPage;

    /** 링크 URL */
    private String linkUrl;

    /** SQL 시작 행번호<br/>MySQL의 경우 -1 (0부터 시작) */
    private int start;
	/** SQL 종료 행번호<br/>MySQL의 경우 사용안함 */
    private int end;

    public PagingUtil(int pagePerRow, int pagePerScreen, int totalCnt, String currentPage, String linkUrl) {
		super();
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
    	int nTotalPage = (this.totalCnt + (this.pagePerRow-1)) / this.pagePerScreen;
    	this.setTotalPage(nTotalPage);

		if (this.currentPage < 1) this.setCurrentPage(1);
		else if (this.currentPage > this.totalPage) this.setCurrentPage(this.totalPage);

		int nFirstPage = ((this.currentPage-1) / this.pagePerRow) * this.pagePerScreen + 1;
    	this.setFirstPage(nFirstPage);

    	int nLastPage = (this.firstPage+this.pagePerRow) - 1;
    	nLastPage = (nLastPage > this.totalPage) ? this.totalPage : nLastPage;
    	this.setLastPage(nLastPage);

    	int nPrevPage = this.firstPage - this.pagePerScreen;
    	nPrevPage = (nPrevPage < 1) ? 1 : nPrevPage;
    	this.setPrevPage(nPrevPage);

    	int nNextPage = this.firstPage + this.pagePerScreen;
    	nNextPage = (nNextPage > this.totalPage) ? this.totalPage : nNextPage;
    	this.setNextPage(nNextPage);

		this.start = this.calcStart();
		this.end = this.calcEnd();
    }

    public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPage() {
		return this.totalPage;
	}
    public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getFirstPage() {
    	return this.firstPage;
    }
    public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getLastPage() {
    	return this.lastPage;
    }
    public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public int getPrevPage() {
    	return this.prevPage;
    }
    public void setPrevPage(int prevPage) {
		this.prevPage = prevPage;
	}

	public int getNextPage() {
    	return this.nextPage;
    }
    public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public int calcStart() {
    	this.start = (this.currentPage-1) * this.pagePerRow + 1;
    	return this.start;
    }

	public int calcEnd() {
		this.end = this.currentPage * this.pagePerRow;
    	return this.end;
    }
	
	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}

package com.ycw.fxq.common.page;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @类名称 PageParams.java
 * @类描述 分页参数
 * @作者 yuminjun yuminjun@lexiangbao.com
 * @创建时间 2019年9月11日 下午3:52:27
 * @版本 1.00
 *
 * @修改记录
 *
 *       <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.00 	yuminjun 	2019年9月11日
 *     ----------------------------------------------
 *       </pre>
 */
@ToString
public class PageParams {

	/**
	 * 当前页码
	 */
	private int pageNum;

	/**
	 * 每页条数
	 */
	private int pageSize;

	@SuppressWarnings("unused")
	private PageParams() {}

	public PageParams(int pageNum, int pageSize) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}

package com.ycw.fxq.common.page;

import java.io.Serializable;
import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;

public class PageInfo<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	// 总数
	protected long total;
	// 列表
	protected List<T> list;

	public PageInfo() {
    }

	public PageInfo(List<T> list) {
        this.list = list;
        if(list instanceof Page){
            this.total = ((Page)list).getTotal();
        } else {
            this.total = list.size();
        }
    }

	public static <T> PageSerializable<T> of(List<T> list) {
		return new PageSerializable<T>(list);
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}

package com.ycw.fxq.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.dao.TempDrawDao;
import com.ycw.fxq.task.FindDataTask;

@Service
public class TempDrawService {

	@Autowired
	TempDrawDao dao;

	public String creatstrid(){
    	return dao.creatstrid();
    }

	public List<TempDraw> findall(Map<String, Integer> params){
		return dao.findall(params);
	}

	public List<Node> findname(){
		return dao.findname();
	}

	public List<TempDraw> filterData(Map<String, String> params) {
		return dao.filterData(params);
	}

	public Integer getTotalCount() {
		return dao.getTotalCount();
	}

	/**
	 * 并行查询数据库记录
	 * @return
	 */
	public List<TempDraw> findAll() {
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		Integer count = this.getTotalCount();// 查询总数
		FindDataTask task = new FindDataTask(0, count);
		task.setService(this);
		return forkJoinPool.invoke(task);
	}
}

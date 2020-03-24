package com.ycw.fxq.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import org.springframework.stereotype.Component;

import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.service.TempDrawServiceImpl;

@Component
public class FindDataTask extends RecursiveTask<List<TempDrawVO>> {

	private static final long serialVersionUID = 1L;

	private static final Integer THRESHOLD = 10000;
	private Integer start;
	private Integer end;
	private TempDrawServiceImpl tempDrawService;

	public FindDataTask() {
		super();
	}

	public FindDataTask(Integer start, Integer end) {
		this.start = start;
		this.end = end;
	}


	public void setService(TempDrawServiceImpl service) {
		this.tempDrawService = service;
	}

	@Override
	protected List<TempDrawVO> compute() {
		if (end - start <= THRESHOLD) {
			Map<String, Integer> params = new HashMap<>();
			params.put("index", start);
			params.put("offset", end - start);
			return tempDrawService.findDataByParams(params);
		} else {
			Integer mid = (start + end) / 2;
			FindDataTask task1 = new FindDataTask(start, mid);
			task1.setService(this.tempDrawService);
			task1.fork();
			FindDataTask task2 = new FindDataTask(mid, end);
			task2.setService(this.tempDrawService);
			task2.fork();
			List<TempDrawVO> list = task1.join();
			list.addAll(task2.join());
			return list;
		}
	}

}

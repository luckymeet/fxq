package com.ycw.fxq.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import org.springframework.stereotype.Component;

import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.service.TempDrawService;
import com.ycw.fxq.util.SpringUtils;

@Component
public class ForkJoinTask extends RecursiveTask<List<TempDraw>> {

	private static final long serialVersionUID = 1L;

	private static final Integer THRESHOLD = 10000;
	private Integer start;
	private Integer end;

	public ForkJoinTask() {
		super();
	}

	public ForkJoinTask(Integer start, Integer end) {
		this.start = start;
		this.end = end;
	}

	@Override
	protected List<TempDraw> compute() {
		if (end - start <= THRESHOLD) {
			Map<String, Integer> params = new HashMap<>();
			params.put("index", start);
			params.put("offset", end - start);
			TempDrawService service = (TempDrawService) SpringUtils.getBean(TempDrawService.class);
			return service.findall(params);
		} else {
			Integer mid = (start + end) / 2;
			ForkJoinTask task1 = new ForkJoinTask(start, mid);
			task1.fork();
			ForkJoinTask task2 = new ForkJoinTask(mid, end);
			task2.fork();
			List<TempDraw> list = task1.join();
			list.addAll(task2.join());
			return list;
		}
	}

}

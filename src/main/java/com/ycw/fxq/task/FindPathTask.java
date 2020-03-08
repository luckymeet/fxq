package com.ycw.fxq.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.spark.SparkJob;

import scala.Tuple2;

public class FindPathTask extends RecursiveTask<List<List<String>>> {

	private static final long serialVersionUID = 1L;
	private static final Integer THRESHOLD = 10000;
	private Integer start;
	private Integer end;
	private List<Node> nodeList;
	private JavaPairRDD<String, String> splitRDD;

	public FindPathTask(Integer start, Integer end, List<Node> nodeList, JavaPairRDD<String, String> splitRDD) {
		this.start = start;
		this.end = end;
		this.nodeList = nodeList;
		this.splitRDD = splitRDD;
	}

	public List<List<String>> findAllDubiousPath(List<TempDraw> list, List<Node> nodeList) {
		JavaSparkContext sc = new JavaSparkContext(new SparkConf().setMaster("local").setAppName("DFS"));
		JavaRDD<TempDraw> textRDD = sc.parallelize(list);
		JavaPairRDD<String, String> splitRDD = textRDD.mapToPair(new PairFunction<TempDraw, String, String>() {
			@Override
			public Tuple2<String, String> call(TempDraw t) throws Exception {
				return new Tuple2<>(t.getName1(), t.getName2());
			}
		});
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		FindPathTask task = new FindPathTask(0, nodeList.size() - 1, nodeList, splitRDD);
		return forkJoinPool.invoke(task);
	}

	@Override
	protected List<List<String>> compute() {
		if (end - start <= THRESHOLD) {
			List<List<String>> res = new ArrayList<>();// 结果集
			for (int i = start; i < end; i++) {
				SparkJob sparkJob = new SparkJob(splitRDD);
				String name = nodeList.get(i).getName();
				for (Node node : nodeList) {
					if (name.equals(node.getName())) {
						continue;
					}
					Stack<String> previous = new Stack<>();// 当前路径
					previous.push(name);
					sparkJob.findAllPaths(res, previous, name, node.getName());
				}
			}
			return res;
		} else {
			Integer mid = (start + end) / 2;
			FindPathTask task1 = new FindPathTask(start, mid, this.nodeList, this.splitRDD);
			task1.fork();
			FindPathTask task2 = new FindPathTask(mid, end, this.nodeList, this.splitRDD);
			task2.fork();
			List<List<String>> list = task1.join();
			list.addAll(task2.join());
			return list;
		}
	}

}

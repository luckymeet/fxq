package com.ycw.fxq.spark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class Test {

	static int[][] matrix = {
				{ 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 1, 1, 1, 0 },
				{ 1, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 1 },
				{ 0, 0, 0, 1, 0, 1 },
				{ 0, 0, 0, 0, 1, 0 }
			};

	private static final int LOOP_COUNT = 2;// 最大循环次数
	private static final JavaSparkContext sc = new JavaSparkContext(new SparkConf().setMaster("local").setAppName("DFS"));
	private static JavaPairRDD<Integer, Integer> splitRDD;
	static {
		JavaRDD<String> textRDD = sc.textFile("./dataFile.txt");
		sc.setLogLevel("WARN");
        splitRDD = textRDD.mapToPair(new PairFunction<String, Integer, Integer>() {
			@Override
			public Tuple2<Integer, Integer> call(String t) throws Exception {
				String[] split = t.split(" ");
				return new Tuple2<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
			}
        });

	}

	private static List<Tuple2<Integer, Integer>> getLinkNodes(int node) {
		JavaPairRDD<Integer, Integer> nodeRdd = splitRDD.filter(new Function<Tuple2<Integer,Integer>, Boolean>() {
			@Override
			public Boolean call(Tuple2<Integer, Integer> v1) throws Exception {
				return v1._1 == node;
			}
		});
		return nodeRdd.collect();
	}

	public static void main(String[] args) {
		System.out.println(splitRDD.collect());
		int cur = 17;// 开始节点
		int des = 19;// 目标节点
		List<List<Integer>> res = new ArrayList<>();// 结果集
		Stack<Integer> previous = new Stack<>();// 当前路径
		previous.push(cur);
		findAllPaths(res, previous, cur, des);
		System.out.println("结果数：" + res.size());
		System.out.println(Arrays.toString(res.toArray()));
	}

	/**
	 * 查找有向图节点间路径
	 *
	 * @param res      结果集
	 * @param previous 当前路径
	 * @param cur      当前节点
	 * @param des      目标节点
	 */
	public static void findAllPaths(List<List<Integer>> res, Stack<Integer> previous, int cur, int des) {
		if (cur == des && previous.size() > 1) {
			res.add(new ArrayList<Integer>(previous));
			return;
		}
//		int length = matrix[cur].length;
//		for (int i = 0; i < length; i++) {
//			int val = matrix[cur][i];
//			int count = findCount(previous, i);
//			if (val == 0 || count > LOOP_COUNT) {
//				continue;
//			}
//			previous.push(i);
//			findAllPaths(res, previous, i, des);
//			previous.pop();
//		}
		List<Tuple2<Integer, Integer>> linkNodes = getLinkNodes(cur);
		for (Tuple2<Integer, Integer> tuple : linkNodes) {
			int count = findCount(previous, tuple._2);
			if (count > LOOP_COUNT) {
				return;
			}
			previous.push(tuple._2);
			findAllPaths(res, previous, tuple._2, des);
			previous.pop();
		}
	}

	private static int findCount(Stack<Integer> stack, int e) {
		int count = 0;
		for (Integer i : stack) {
			if (i == e)
				count++;
		}
		return count;
	}

}

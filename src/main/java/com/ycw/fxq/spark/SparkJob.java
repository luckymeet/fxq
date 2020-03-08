package com.ycw.fxq.spark;

import java.util.List;
import java.util.Stack;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

public class SparkJob {

	private int loopCount = 0;
	private JavaPairRDD<String, String> splitRDD;

	public SparkJob(JavaPairRDD<String, String> splitRDD) {
		super();
		this.splitRDD = splitRDD;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	public JavaPairRDD<String, String> getSplitRDD() {
		return splitRDD;
	}

	public void setSplitRDD(JavaPairRDD<String, String> splitRDD) {
		this.splitRDD = splitRDD;
	}

	public void findAllPaths(List<List<String>> res, Stack<String> previous, String curName, String desName) {
		if (curName.equals(desName)) {
			res.add(previous);
			return;
		}
		List<Tuple2<String, String>> linkNodes = getLinkNodes(curName);
		for (Tuple2<String, String> tuple : linkNodes) {
			int count = findCount(previous, tuple._2);
			if (count > loopCount) {
				return;
			}
			previous.push(tuple._2);
			findAllPaths(res, previous, tuple._2, desName);
			previous.pop();
		}
	}

	private List<Tuple2<String, String>> getLinkNodes(String name) {
		JavaPairRDD<String, String> nodeRdd = splitRDD.filter(new Function<Tuple2<String,String>, Boolean>() {
			@Override
			public Boolean call(Tuple2<String, String> v1) throws Exception {
				return v1._1.equals(name);
			}
		});
		return nodeRdd.collect();
	}

	private static int findCount(Stack<String> stack, String name) {
		int count = 0;
		for (String n : stack) {
			if (name.equals(n))
				count++;
		}
		return count;
	}

}

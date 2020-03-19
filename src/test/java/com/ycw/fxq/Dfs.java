package com.ycw.fxq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Dfs {
	static int[][] matrix = {
			{ 0, 1, 0, 0, 0, 0 },
			{ 0, 0, 1, 1, 1, 0 },
			{ 1, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 1 },
			{ 0, 0, 0, 1, 0, 1 },
			{ 0, 0, 0, 0, 1, 0 }
	};

//	static int[][] matrix = {
//			{ 0, 1, 1},
//			{ 0, 0, 0},
//			{ 0, 0, 0,},
//
//	};
	private static final int LOOP_COUNT = 1;// 最大循环次数

	public static void main(String[] args) {
		int cur = 0;// 开始节点
		int des = 0;// 目标节点
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
		int length = matrix[cur].length;
		for (int i = 0; i < length; i++) {
			int val = matrix[cur][i];
//			int count = findCount(previous, i);
//			if (val == 0 || count > LOOP_COUNT) {
			if (val == 0 || previous.contains(i)) {
				continue;
			}
			previous.push(i);
			findAllPaths(res, previous, i, des);
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

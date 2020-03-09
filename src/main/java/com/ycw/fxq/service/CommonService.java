package com.ycw.fxq.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.louvain.LouvainHelper;

import scala.Tuple2;

@Service
public class CommonService {

	Logger logger = LoggerFactory.getLogger(CommonService.class);
	private static final String ROOT_PATH = CommonService.class.getResource("/").getPath();
	private static final String CLUSTER_PATH = ROOT_PATH + "cluster/";
	private static final String IMG_PATH = ROOT_PATH + "static/images/";

	/**
	 * 获取聚类后的节点列表
	 *
	 * @param linkList
	 * @param nameList
	 * @param rootPath
	 * @return
	 */
	public List<Node> getClusterNodeList(List<TempDraw> linkList, List<String> nameList) {
		Map<String, Integer> nameIndexMap = new HashMap<>();
		Map<Integer, String> indexNameMap = new HashMap<>();
		for (int i = 0; i < nameList.size(); i++) {
			nameIndexMap.put(nameList.get(i), i);
			indexNameMap.put(i, nameList.get(i));
		}
		HttpSession session = getSession();
		session.setAttribute("indexNameMap", indexNameMap);
		session.setAttribute("nameIndexMap", nameIndexMap);

		/* 权值计算 */
		List<TempDraw> res = countWeight(linkList, nameList, nameIndexMap, indexNameMap);

		/* 社区划分 */
		try {
			cluster(res, nameList, nameIndexMap, CLUSTER_PATH);
		} catch (Exception e) {
			logger.error("进行社区划分失败", e);
		}

		/* 获取节点列表 */
		List<Node> nodeList = null;
		nodeList = getNodeList(indexNameMap, CLUSTER_PATH, IMG_PATH);
		return nodeList;
	}

	private HttpSession getSession() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request.getSession();
	}

	private List<TempDraw> countWeight(List<TempDraw> linkList, List<String> nameList,
			Map<String, Integer> nameIndexMap, Map<Integer, String> indexNameMap) {
		int node = nameList.size();
		double[][] moneyMatrix = new double[node][node];
		double[][] timeMatrix = new double[node][node];
		double[] allMonkey = new double[node];
		double[] allTime = new double[node];
		for (TempDraw temp_draw : linkList) {
			int index1 = nameIndexMap.get(temp_draw.getName1());
			int index2 = nameIndexMap.get(temp_draw.getName2());
			allMonkey[index1] = allMonkey[index1] + Double.parseDouble(temp_draw.getMoney());
			allMonkey[index2] = allMonkey[index2] + Double.parseDouble(temp_draw.getMoney());
			allTime[index1] = allTime[index1] + 1;
			allTime[index2] = allTime[index2] + 1;
			timeMatrix[index1][index2] = timeMatrix[index1][index2] + 1;
			timeMatrix[index2][index1] = timeMatrix[index2][index1] + 1;
			moneyMatrix[index1][index2] = moneyMatrix[index1][index2] + Double.parseDouble(temp_draw.getMoney());
			moneyMatrix[index2][index1] = moneyMatrix[index2][index1] + Double.parseDouble(temp_draw.getMoney());
		}
		double[][] result = new double[nameList.size()][nameList.size()];
		for (int i = 0; i < nameList.size(); i++) {
			for (int j = 0; j < nameList.size(); j++) {
				result[i][j] = 0.25 * (moneyMatrix[i][j] / allMonkey[i]) + 0.25 * (moneyMatrix[i][j] / allMonkey[j])
						+ 0.25 * (timeMatrix[i][j] / allTime[i]) + 0.25 * (timeMatrix[i][j] / allTime[j]);
			}
		}
		List<TempDraw> res = new LinkedList<>();
		for (int i = 0; i < nameList.size(); i++) {
			for (int j = i; j < nameList.size(); j++) {
				if (!(result[i][j] >= (-1e-6) && result[i][j] <= (1e-6))) {
					TempDraw tempDraw = new TempDraw();
					tempDraw.setName1(indexNameMap.get(i));
					tempDraw.setName2(indexNameMap.get(j));
					tempDraw.setMoney(String.valueOf(result[i][j]));
					res.add(tempDraw);
				}
			}
		}
		return res;
	}

	/**
	 * 获取节点列表
	 *
	 * @param indexNameMap
	 * @param clusterPath
	 * @param imgPath
	 * @return
	 * @throws IOException
	 */
	private List<Node> getNodeList(Map<Integer, String> indexNameMap, String clusterPath, String imgPath) {
		File imgFile = new File(imgPath);// 图片文件夹路径
		String[] imgNamelist = imgFile.list();// 图片名称列表
		List<List<String>> clusterList = getClusterListByClusterFile(clusterPath);
		List<Node> nodeList = makeNodeList(indexNameMap, imgNamelist, clusterList);
		return nodeList;
	}

	/**
	 * 从社区划分后的结果文件读取数据
	 *
	 * @param clusterPath
	 * @return
	 * @throws IOException
	 */
	private List<List<String>> getClusterListByClusterFile(String clusterPath) {
		List<List<String>> clusterList = new ArrayList<>();
		try (BufferedReader bf = new BufferedReader(new FileReader(clusterPath + "circle.txt"))) {
			String str = bf.readLine();
			while (str != null) {
				if (!"".equals(str.trim())) {
					clusterList.add(Arrays.asList(str.trim().split(" ")));
				}
			}
		} catch (IOException e) {
			logger.error("社区划分后的结果文件失败", e);
		}
		return clusterList;
	}

	/**
	 * 组装节点列表
	 *
	 * @param indexNameMap
	 * @param imgNamelist
	 * @param clusterList
	 * @return
	 */
	private List<Node> makeNodeList(Map<Integer, String> indexNameMap, String[] imgNamelist,
			List<List<String>> clusterList) {
		List<Node> nodeList = new ArrayList<>();
		for (int i = 0; i < clusterList.size(); i++) {
			List<String> list = clusterList.get(i);
			for (String num : list) {
				Node node = new Node();
				node.setName(indexNameMap.get(Integer.parseInt(num)));// 设置节点名称
				node.setImgName(imgNamelist[i % (imgNamelist.length)]);// 设置节点图片名称
				nodeList.add(node);
			}
		}
		return nodeList;
	}

	/**
	 * 进行社区划分
	 *
	 * @param linkList
	 * @param nameList
	 * @param indexMap
	 * @param clusterPath
	 * @throws IOException
	 */
	private void cluster(List<TempDraw> linkList, List<String> nameList, Map<String, Integer> indexMap,
			String clusterPath) throws IOException {
		File dir = new File(clusterPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String fileName = "dataFile.txt";
		File file = new File(clusterPath + fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.error("创建数据文件失败", e);
		}
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
			StringBuilder sb = new StringBuilder(nameList.size() + " " + linkList.size() + "\n");
			for (TempDraw drwa : linkList) {
				sb.append(indexMap.get(drwa.getName1())).append(" ").append(indexMap.get(drwa.getName2())).append(" ")
						.append(drwa.getMoney()).append("\n");
			}
			bw.write(sb.toString().trim());
		} catch (IOException e) {
			logger.error("写入数据文件失败", e);
		}
		try {
			LouvainHelper.excute(clusterPath, fileName);
		} catch (IOException e) {
			logger.error("执行Louvain算法失败", e);
		}
	}

	public List<Map<String, Object>> findAllDubiousPath(int num) {
		List<Map<String, Object>> resList = new ArrayList<>();
		JavaPairRDD<Integer, Integer> mapToPairRdd = getDataRdd();
		Map<Integer, Iterable<Integer>> dataRelationMap = mapToPairRdd.groupByKey().collectAsMap();
		List<String[]> circleList = getCircleList();
		for (int i = 0; i < circleList.size(); i++) {
			for (String s1 : circleList.get(i)) {
				for (int j = 0; j < circleList.size(); j++) {
					if (j == i)
						continue;
					for (String s2 : circleList.get(j)) {
						int cur = Integer.parseInt(s1);// 开始节点
						int des = Integer.parseInt(s2);// 目标节点
						List<List<Integer>> res = new ArrayList<>();// 结果集
						Stack<Integer> previous = new Stack<>();// 当前路径
						previous.push(cur);
						findAllPaths(dataRelationMap, res, previous, cur, des, 1);
						if (res.size() >= num) {
							Map<String, Object> map = new HashMap<>();
							map.put("startName", cur);
							map.put("endName", des);
							map.put("pathCount", res.size());
							map.put("pathList", res);
//							JavaPairRDD<Tuple2<Integer, Integer>, Long> filterRdd = zipWithIndex.filter(new Function<Tuple2<Tuple2<Integer,Integer>,Long>, Boolean>() {
//								@Override
//								public Boolean call(Tuple2<Tuple2<Integer, Integer>, Long> v1) throws Exception {
//									Tuple2<Integer, Integer> tuple2 = v1._1;
//									return (tuple2._1 == cur && tuple2._2 == des);
//								}
//							});
							resList.add(map);
						}
					}
				}
			}
		}
		return resList;
	}

	private List<String[]> getCircleList() {
		JavaSparkContext sc = new JavaSparkContext(new SparkConf().setMaster("local").setAppName("circle"));
		JavaRDD<String> javaRDD = sc.textFile("./circle.txt");
		JavaRDD<String[]> mapRdd = javaRDD.map(new Function<String, String[]>() {
			@Override
			public String[] call(String v1) throws Exception {
				return v1.split(" ");
			}
		});
		List<String[]> circleList = mapRdd.collect();
		return circleList;
	}

	private JavaPairRDD<Integer, Integer> getDataRdd() {
		JavaSparkContext sc = new JavaSparkContext(new SparkConf().setMaster("local").setAppName("data"));
		JavaRDD<String> javaRdd = sc.textFile("./dataFile.txt");
		javaRdd.zipWithIndex().filter(item -> item._2 == 0);
		JavaPairRDD<Integer, Integer> mapToPairRdd = javaRdd.mapToPair(new PairFunction<String, Integer, Integer>() {
			@Override
			public Tuple2<Integer, Integer> call(String t) throws Exception {
				String[] split = t.split(" ");
				return new Tuple2<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
			}
		});
		return mapToPairRdd;
	}

	/**
	 * 查找有向图节点间路径
	 *
	 * @param res      结果集
	 * @param previous 当前路径
	 * @param cur      当前节点
	 * @param des      目标节点
	 */
	public static void findAllPaths(Map<Integer, Iterable<Integer>> data, List<List<Integer>> res,
			Stack<Integer> previous, int cur, int des, int loopCount) {
		if (cur == des) {
			res.add(new ArrayList<Integer>(previous));
			return;
		}
		Iterable<Integer> linkNodes = data.get(cur);
		Iterator<Integer> iter = linkNodes.iterator();
		while (iter.hasNext()) {
			int node = iter.next();
			int count = findCount(previous, node);
			if (count > loopCount) {
				return;
			}
			previous.push(node);
			findAllPaths(data, res, previous, node, des, loopCount);
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

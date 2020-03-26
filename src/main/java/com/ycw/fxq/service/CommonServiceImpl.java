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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.louvain.LouvainHelper;
import com.ycw.fxq.service.impl.CommonService;

@Service
public class CommonServiceImpl implements CommonService {

	Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
	private static final String ROOT_PATH = CommonServiceImpl.class.getResource("/").getPath();
	private static final String CLUSTER_PATH = ROOT_PATH + "cluster/";
	private static final String IMG_PATH = ROOT_PATH + "static/img/node/";

	/**
	 * 获取聚类后的节点列表
	 *
	 * @param linkList
	 * @param nameList
	 * @param rootPath
	 * @return
	 */
	@Override
	public List<Node> getClusterNodeList(List<TempDrawVO> linkList, List<String> nameList) {
		Map<String, Integer> nameIndexMap = new HashMap<>();
		Map<Integer, String> indexNameMap = new HashMap<>();
		for (int i = 0; i < nameList.size(); i++) {
			nameIndexMap.put(nameList.get(i), i);
			indexNameMap.put(i, nameList.get(i));
		}

		/* 权值计算 */
		List<TempDraw> res = countWeight(linkList, nameList, nameIndexMap, indexNameMap);

		/* 社区划分 */
		try {
			cluster(res, nameList, nameIndexMap);
		} catch (Exception e) {
			logger.error("进行社区划分失败", e);
		}

		/* 获取节点列表 */
		List<Node> nodeList = null;
		nodeList = getNodeList(indexNameMap);
		return nodeList;
	}

	private List<TempDraw> countWeight(List<TempDrawVO> linkList, List<String> nameList,
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
	 * @param CLUSTER_PATH
	 * @param IMG_PATH
	 * @return
	 * @throws IOException
	 */
	private List<Node> getNodeList(Map<Integer, String> indexNameMap) {
		File imgFile = new File(IMG_PATH);// 图片文件夹路径
		String[] imgNamelist = imgFile.list();// 图片名称列表
		List<List<String>> clusterList = getClusterListByClusterFile();
		List<Node> nodeList = makeNodeList(indexNameMap, imgNamelist, clusterList);
		return nodeList;
	}

	/**
	 * 从社区划分后的结果文件读取数据
	 *
	 * @param CLUSTER_PATH
	 * @return
	 * @throws IOException
	 */
	private List<List<String>> getClusterListByClusterFile() {
		List<List<String>> clusterList = new ArrayList<>();
		try (BufferedReader bf = new BufferedReader(new FileReader(CLUSTER_PATH + "circle.txt"))) {
			String str;
			while ((str = bf.readLine()) != null) {
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
	 * @param CLUSTER_PATH
	 * @throws IOException
	 */
	private void cluster(List<TempDraw> linkList, List<String> nameList, Map<String, Integer> indexMap)
			throws IOException {
		File dir = new File(CLUSTER_PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String fileName = "dataFile.txt";
		File file = new File(CLUSTER_PATH + fileName);
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
			LouvainHelper.excute(CLUSTER_PATH, fileName);
		} catch (IOException e) {
			logger.error("执行Louvain算法失败", e);
		}
	}

	/**
	 * 查找有向图节点间路径
	 *
	 * @param res      结果集
	 * @param previous 当前路径
	 * @param cur      当前节点
	 * @param des      目标节点
	 */
	@Override
	public void findLoops(Map<String, String> data, List<List<String>> res, Stack<String> previous, String cur,
			String des) {
		if (previous.size() > 1 && cur.equals(des)) {
			res.add(new ArrayList<String>(previous));
			return;
		}
		String linkNodes = data.get(cur);
		if (linkNodes == null) {
			return;
		}
		String[] arr = linkNodes.split(",");
		for (String s : arr) {
//			int count = findCount(previous, node);
//			if (count > loopCount) {
			if (previous.indexOf(s) > 0) {
				continue;
			}
			previous.push(s);
			findLoops(data, res, previous, s, des);
			previous.pop();
		}
	}

	private int findCount(Stack<Integer> stack, int e) {
		int count = 0;
		for (Integer i : stack) {
			if (i == e)
				count++;
		}
		return count;
	}

	@Override
	public Map<String, String> createDirectedGraphByMap(List<TempDraw> drawList) {
		Map<String, String> dataMap = new HashMap<>((int) (drawList.size() / 0.75 + 1));
		drawList.stream().forEach(tempDraw -> {
			String card1 = tempDraw.getCard1();
			dataMap.put(card1,
					dataMap.get(card1) == null ? tempDraw.getCard2() : dataMap.get(card1) + "," + tempDraw.getCard2());
		});
		return dataMap;
	}

}

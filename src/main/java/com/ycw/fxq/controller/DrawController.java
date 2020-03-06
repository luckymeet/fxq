package com.ycw.fxq.controller;

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
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.louvain.LouvainHelper;
import com.ycw.fxq.service.TempDrawService;
import com.ycw.fxq.task.ForkJoinTask;

@Controller
//@RequestMapping("draw")
public class DrawController {

	Logger logger = LoggerFactory.getLogger(DrawController.class);

	@Autowired
	private TempDrawService service;

	@GetMapping("/index")
	public void goindex(Model model, Model model2, Model maxmoeny, Model minmoeny, HttpServletRequest request) {
		long start = System.currentTimeMillis();
		List<TempDraw> link = this.findAll();
		logger.info(String.format("================总数据量：%s================", link.size()));
		model.addAttribute("linklist", link);
		maxmoeny.addAttribute("maxmoeny", link.size() > 0 ? link.get(0).getMoney().toString() : "");
		minmoeny.addAttribute("minmoeny", link.size() > 0 ? link.get(link.size() - 1).getMoney().toString() : "");
		model2.addAttribute("nodelist", service.findname());
		logger.info(String.format("================总共用时：%sms================", System.currentTimeMillis() - start));
	}

	/**
	 * 并行查询数据库记录
	 * @return
	 */
	private List<TempDraw> findAll() {
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		Integer count = service.getTotalCount();// 查询总数
		List<TempDraw> list = forkJoinPool.invoke(new ForkJoinTask(0, count));
		return list;
	}

	/**
	 * 根据交易频率和交易金额过滤数据
	 *
	 * @param model
	 * @param model2
	 * @param maxmoeny
	 * @param minmoeny
	 * @param request
	 * @return
	 */
	@GetMapping("/filter")
	public ModelAndView filter(HttpServletRequest request) {
		List<TempDraw> linkList = getLinkList(request);// 根据交易频率和交易金额查询交易数据

		/* 获取节点名称 */
		Set<String> nameSet = linkList.stream().map(item -> item.getName1()).collect(Collectors.toSet());
		nameSet.addAll(linkList.stream().map(item -> item.getName2()).collect(Collectors.toSet()));

		String rootPath = this.getClass().getResource("/").getPath();// WebContent的绝对路径
		List<Node> nodeList = getClusterNodeList(linkList, new ArrayList<>(nameSet), rootPath);// 获取聚类后的节点列表

		/* 渲染页面 */
		ModelAndView mv = new ModelAndView();
		mv.addObject("linklist", linkList);
		mv.addObject("nodelist", nodeList);
		mv.addObject("maxmoeny", linkList.size() > 0 ? linkList.get(0).getMoney().toString() : "");
		mv.addObject("minmoeny", linkList.size() > 0 ? linkList.get(linkList.size() - 1).getMoney().toString() : "");
		mv.setViewName("index");
//		maxmoeny.addAttribute("maxmoeny", linkList.size() > 0 ? linkList.get(0).getMoney().toString() : "");
//		minmoeny.addAttribute("minmoeny", linkList.size() > 0 ? linkList.get(linkList.size() - 1).getMoney().toString() : "");
//		model.addAttribute("linklist", linkList);
//		model2.addAttribute("nodelist", nodeList);
		return mv;
	}

	/**
	 * 获取聚类后的节点列表
	 *
	 * @param linkList
	 * @param nameList
	 * @param rootPath
	 * @return
	 */
	private List<Node> getClusterNodeList(List<TempDraw> linkList, List<String> nameList, String rootPath) {
		String clusterPath = rootPath + "cluster/";// 社区划分文件路径
		String imgPath = rootPath + "static/images";// 图片路径

		Map<String, Integer> nameIndexMap = new HashMap<>();
		Map<Integer, String> indexNameMap = new HashMap<>();
		for (int i = 0; i < nameList.size(); i++) {
			nameIndexMap.put(nameList.get(i), i);
			indexNameMap.put(i, nameList.get(i));
		}

		/*权值计算*/
		int node=nameList.size();
		double[][] moneyMatrix=new double[node][node];
		double[][] timeMatrix=new double[node][node];
		double[] allMonkey=new double[node];
		double[] allTime=new double[node];
		for(TempDraw temp_draw:linkList) {
			int index1 = nameIndexMap.get(temp_draw.getName1());
			int index2 = nameIndexMap.get(temp_draw.getName2());
			allMonkey[index1] = allMonkey[index1]+Double.parseDouble(temp_draw.getMoney());
			allMonkey[index2] = allMonkey[index2]+Double.parseDouble(temp_draw.getMoney());
			allTime[index1]=allTime[index1]+1;
			allTime[index2]=allTime[index2]+1;
			timeMatrix[index1][index2]=timeMatrix[index1][index2]+1;
			timeMatrix[index2][index1]=timeMatrix[index2][index1]+1;
			moneyMatrix[index1][index2]= moneyMatrix[index1][index2]+Double.parseDouble(temp_draw.getMoney());
			moneyMatrix[index2][index1]= moneyMatrix[index2][index1]+Double.parseDouble(temp_draw.getMoney());
		}
		double[][] result=new double[nameList.size()][nameList.size()];
		for(int i=0;i<nameList.size();i++){
			for(int j=0;j<nameList.size();j++) {
				result[i][j] = 0.25 * (moneyMatrix[i][j] / allMonkey[i]) + 0.25 * (moneyMatrix[i][j] / allMonkey[j])
						+ 0.25 * (timeMatrix[i][j] / allTime[i]) + 0.25 * (timeMatrix[i][j] / allTime[j]);
//				System.out.print(result[i][j]+"  ");
			}
		}
		System.out.println();
		List<TempDraw> res =new LinkedList<>();
		for(int i=0;i<nameList.size();i++){
			for(int j=i;j<nameList.size();j++){
				//result[i][j]>=(-1e-6)&&result[i][j]<=(1e-6)
				if(!(result[i][j]>=(-1e-6)&&result[i][j]<=(1e-6))){
					TempDraw temp_draw=new TempDraw();
					temp_draw.setName1(indexNameMap.get(i));
					temp_draw.setName2(indexNameMap.get(j));
					temp_draw.setMoney(String.valueOf(result[i][j]));
					res.add(temp_draw);
				}
			}
		}
//		for (TempDraw drwa : res) {
//			System.out.println(nameIndexMap.get(drwa.getName1())+" "+nameIndexMap.get(drwa.getName2()));
//		}

//		for (TempDraw drwa : linkList) {
//			System.out.println(indexNameMap.get(drwa.getName1())+" "+indexNameMap.get(drwa.getName2()));
//		}

		/* 输出结果展示*/
		/*
		for(int i=0;i<node;i++){
			System.out.print("总过支出"+allMonkey[i]);
		}
		for(int i=0;i<node;i++){
			System.out.print("总过频度"+allTime[i]);
		}
		System.out.println();
		for(int i=0;i<nameList.size();i++){
			for(int j=0;j<nameList.size();j++){
				System.out.print(moneyMatrix[i][j]);
			}
			System.out.println();
		}
		for(int k=0;k<nameList.size();k++){
			for(int m=0;m<nameList.size();m++){
				System.out.print(timeMatrix[k][m]);
			}
			System.out.println();
		}
		*/

		/* 社区划分 */
		try {
			//cluster(linkList, nameList, nameIndexMap, clusterPath);
			long startTime = System.currentTimeMillis();
			cluster(res, nameList, nameIndexMap, clusterPath);
			System.out.println(String.format("\n社区划分用时：%sms", System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			logger.error("进行社区划分失败", e);
		}

		/* 获取节点列表 */
		List<Node> nodeList = null;
		try {
			nodeList = getNodeList(indexNameMap, clusterPath, imgPath);
		} catch (IOException e) {
			logger.error("获取节点列表失败", e);
		}
		return nodeList;
	}

	/**
	 * 根据交易频率和交易金额查询交易数据
	 *
	 * @param request
	 * @return
	 */
	private List<TempDraw> getLinkList(HttpServletRequest request) {
		String frequency = request.getParameter("frequency");// 频率
		String amount = request.getParameter("amount");// 金额
		Map<String, String> params = new HashMap<>();
		params.put("frequency", frequency);
		params.put("amount", amount);
		List<TempDraw> linkList = service.filterData(params);
		return linkList;
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
	private List<Node> getNodeList(Map<Integer, String> indexNameMap, String clusterPath, String imgPath)
			throws IOException {
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
	private List<List<String>> getClusterListByClusterFile(String clusterPath) throws IOException {
		List<List<String>> clusterList = new ArrayList<>();
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(clusterPath + "circle.txt"));
			String str;
			while ((str = bf.readLine()) != null) {
				if (str != null && !"".equals(str.trim())) {
					clusterList.add(Arrays.asList(str.trim().split(" ")));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf != null) {
				bf.close();
			}
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
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("创建数据文件失败", e);
			}
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			StringBuilder sb = new StringBuilder(nameList.size() + " " + linkList.size() + "\n");
			for (TempDraw drwa : linkList) {
				//sb.append(indexMap.get(drwa.getName1())).append(" ").append(indexMap.get(drwa.getName2())).append(" ").append("权重").append("\n");
				sb.append(indexMap.get(drwa.getName1())).append(" ").append(indexMap.get(drwa.getName2())).append(" ").append(drwa.getMoney()).append("\n");
//				System.out.println(indexMap.get(drwa.getName1())+" "+indexMap.get(drwa.getName2()));
			}
			bw.write(sb.toString().trim());
		} catch (IOException e) {
			logger.error("写入数据文件失败", e);
		} finally {
			if (bw != null) {
				bw.flush();
				bw.close();
			}
		}
		try {
			LouvainHelper.excute(clusterPath, fileName);
		} catch (IOException e) {
			logger.error("执行Louvain算法失败", e);
		}
	}

}

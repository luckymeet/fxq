 package com.ycw.fxq.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;

public interface CommonService {

	List<Node> getClusterNodeList(List<TempDraw> linkList, List<String> nameList);

	void findLoops(Map<String, String> data, List<List<String>> res, Stack<String> previous, String cur, String des);

}

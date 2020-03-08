package com.ycw.fxq.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.dao.TempDrawDao;

@Service
public class TempDrawService {

	@Autowired
	TempDrawDao dao;

	public String creatstrid(){
    	return dao.creatstrid();
    }

	public List<TempDraw> findall(Map<String, Integer> params){
		return dao.findall(params);
	}

	public List<Node> findname(){
		return dao.findname();
	}

	public List<TempDraw> findallreal(){
		return dao.findallreal();
	}

	public List<String> findnamereal(){
		return dao.findnamereal();
	}

	public List<TempDraw> filterData(Map<String, String> params) {
		return dao.filterData(params);
	}

	public Integer getTotalCount() {
		return dao.getTotalCount();
	}
}

package com.ycw.fxq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MergeTest {

	private static Map<String, String> nameAccRealMap = new HashMap<>();

	static {
		List<String> nameList = Arrays.asList(",赵一,钱二,孙三,李四,周五,吴六,郑七,王八,陈九,余十".split(","));
		for(int i = 1; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				nameAccRealMap.put(String.valueOf(j * 10 + i), nameList.get(i));
			}
		}
		System.out.println(nameAccRealMap);
	}

	public static void main(String[] args) {
		List<List<String>> accRes = new ArrayList<>();
		accRes.add(Arrays.asList("1,2,3,4".split(",")));
		accRes.add(Arrays.asList("1,12,5".split(",")));
		accRes.add(Arrays.asList("1,6,7,15".split(",")));
		Set<String> mergeAccNo = mergeAccNo(accRes);
		System.out.println(mergeAccNo);
	}

	public static Set<String> mergeAccNo(List<List<String>> accRes){
		Set<String> pathSet = new HashSet<>();
		for (List<String> accPath : accRes) {
			for (int i = 0; i < accPath.size() - 1; i++) {
				pathSet.add(nameAccRealMap.get(accPath.get(i)) + "-" + nameAccRealMap.get(accPath.get(i + 1)));
			}
		}
		return pathSet;
	}

}

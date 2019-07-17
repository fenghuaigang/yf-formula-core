package com.yupont.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.yupont.dag.Graph;
import com.yupont.dag.KahnTopo;
import com.yupont.dag.Node;

public class DAGUtil {

	/**
	 * 图排序
	 * @param graph 拓扑图类
	 * @param asc 排序规则： true 被引用方排前  ;false 引用方排前
	 * @return
	 */
	public static List<Node> sortGraph(Graph graph,boolean asc){
		List<Node> rVal = new ArrayList<>();
		if(graph!=null){
			Set<Node> vertexSet = graph.getVertexSet();
			KahnTopo topo = new KahnTopo(graph);
			topo.process();
			rVal = (List<Node>) topo.getResult();
			vertexSet.removeAll(rVal);
			if(!vertexSet.isEmpty()){
				List<Node> list = vertexSet.stream().collect(Collectors.toList());
				rVal.addAll(list);
			}
			if(!asc){
				Collections.reverse(rVal);
			}
		}
		return rVal;
	}
	
}

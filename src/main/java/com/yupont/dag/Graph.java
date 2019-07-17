package com.yupont.dag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 拓扑图类
 * 
 * @author feng
 *
 */
public class Graph {
	// 图中节点的集合
	public Set<Node> vertexSet = new HashSet<Node>();
	// 相邻的节点，纪录边
	public Map<Node, Set<Node>> adjaNode = new HashMap<Node, Set<Node>>();

	// 将节点加入图中
	public boolean addNode(Node start, Node end) {
		if (!vertexSet.contains(start)) {
			vertexSet.add(start);
		}
		if (!vertexSet.contains(end)) {
			vertexSet.add(end);
		}
		if (adjaNode.containsKey(start) && adjaNode.get(start).contains(end)) {
			return false;
		}
		if (adjaNode.containsKey(start)) {
			adjaNode.get(start).add(end);
		} else {
			Set<Node> temp = new HashSet<Node>();
			temp.add(end);
			adjaNode.put(start, temp);
		}
		end.pathIn++;
		return true;
	}
	/**
	 * 返回节点集合
	 * @return
	 */
	public Set<Node> getVertexSet() {
		return vertexSet;
	}

}

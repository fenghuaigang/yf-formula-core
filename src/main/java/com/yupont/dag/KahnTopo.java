package com.yupont.dag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Kahn算法实现拓扑排序
 * 
 * @author feng
 *
 */
public class KahnTopo {

	private List<Node> result; // 用来存储结果集
	private Queue<Node> setOfZeroIndegree; // 用来存储入度为0的顶点
	private Graph graph;

	// 构造函数，初始化
	public KahnTopo(Graph di) {
		this.graph = di;
		this.result = new ArrayList<Node>();
		this.setOfZeroIndegree = new LinkedList<Node>();
		// 对入度为0的集合进行初始化
		for (Node iterator : this.graph.vertexSet) {
			if (iterator.pathIn == 0) {
				this.setOfZeroIndegree.add(iterator);
			}
		}
	}

	// 拓扑排序处理过程
	public void process() {
		while (!setOfZeroIndegree.isEmpty()) {
			Node v = setOfZeroIndegree.poll();

			// 将当前顶点添加到结果集中
			result.add(v);

			if (this.graph.adjaNode.keySet().isEmpty()) {
				return;
			}

			// 遍历由v引出的所有边
			for (Node w : this.graph.adjaNode.get(v)) {
				// 将该边从图中移除，通过减少边的数量来表示
				w.pathIn--;
				if (0 == w.pathIn) // 如果入度为0，那么加入入度为0的集合
				{
					setOfZeroIndegree.add(w);
				}
			}
			this.graph.vertexSet.remove(v);
			this.graph.adjaNode.remove(v);
		}

		// 如果此时图中还存在边，那么说明图中含有环路
		if (!this.graph.vertexSet.isEmpty()) {
			throw new IllegalArgumentException("拓扑链路含有闭环 !");
		}
	}

	// 结果集
	public Iterable<Node> getResult() {
		return result;
	}
}

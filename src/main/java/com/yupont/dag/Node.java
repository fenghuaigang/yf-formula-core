package com.yupont.dag;

/**
 * 节点
 * 
 * @author feng
 *
 */
public class Node {
	// 记录节点数据
	public Object val;

	public int pathIn = 0; // 入链路数量

	public Node(Object val) {
		this.val = val;
	}

	public Object getVal() {
		return val;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Node)) {
			return false;
		} else {
			Node node = ((Node) obj);
			if (this.val == node.getVal() || !this.val.equals(node.getVal())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return val!=null?this.val.toString().hashCode():"".hashCode();
	}

}

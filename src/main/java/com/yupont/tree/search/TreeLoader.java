package com.yupont.tree.search;

import com.alibaba.fastjson.JSONArray;
import com.yupont.tree.conf.TreeSource;

/**
 * @author fenghuaigang
 * @date 2020/4/9
 */
public interface TreeLoader {
    /** 
     *  设置查询数据源
     * @methodName setSource       
     * @param treeSource
     * @return  
     * @author fenghuaigang 
     * @date 2020/4/9 
     */
    TreeLoader setSource(TreeSource treeSource);
    /** 
     *  查询
     * @methodName search       
     * @param 
     * @return  
     * @author fenghuaigang 
     * @date 2020/4/9 
     */   
    JSONArray search();
}

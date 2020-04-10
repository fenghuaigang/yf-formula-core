package com.yupont.tree.search;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yupont.tree.model.TreeConfig;
import com.yupont.util.DbUtil;
import com.yupont.util.TextUtil;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 当树配置在DB中用此查询
 * @author fenghuaigang
 * @date 2020/4/9
 */
public class CommonTreeLoader extends AbstractTreeLoader{

    private static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

    public CommonTreeLoader(String treeNodeId, String data, TreeConfig treeConfig) {
        super(treeNodeId, data, treeConfig);
    }

    public CommonTreeLoader(String treeNodeId, String data, JSONObject treeNodeConfigs) {
        super(treeNodeId, data, treeNodeConfigs);
    }

    @Override
    protected void parseNode(JSONObject treeNode) {
        if (treeNode.getBoolean("selfIgnore") || treeNode.containsKey("exself")) {
            JSONArray array = new JSONArray();
            String filter = parseSql(treeNode);
            if (TextUtil.isNotEmpty(treeNode.getString("model"))) {
                Connection connection = treeSource.getConnection();
                String select = treeNode.getOrDefault("select","*").toString();
                String sql = " select "+select+" from " + treeNode.getString("model");
                if(StringUtils.isNotBlank(filter)){
                    sql = sql + " where " + filter;
                }
                array = DbUtil.queryJSONArray(connection, sql, null);
                DbUtil.closeConn(connection);
                JSONArray convert = treeNode.getJSONArray("convert");
                if (convert != null) {
                    array.stream().map(o->(JSONObject)o).forEach(o->putConvert(o,convert));
                }
            }
            treeNode.put("valueList", array);
        }
    }

    protected void putConvert(JSONObject obj, JSONArray convert){
        convert.stream().map(o->(JSONObject)o).forEach(o->{
            String name = o.getString("name");
            String field = o.getString("field");
            String tpl  = o.getOrDefault("tpl", "{0}").toString();
            String[] fields = tokenizeToStringArray(field, CONFIG_LOCATION_DELIMITERS);
            for (int i = 0; i < fields.length; i++) {
                String f = fields[i];
                tpl = tpl.replaceAll("\\{"+i+"}",obj.getOrDefault(f,"").toString());
            }
            obj.put(name,tpl);
        });
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(
            String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>(st.countTokens());
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }
    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }

        return collection.toArray(new String[collection.size()]);
    }
}

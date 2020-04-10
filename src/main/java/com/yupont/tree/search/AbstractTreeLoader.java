package com.yupont.tree.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yupont.tree.conf.TreeSource;
import com.yupont.tree.model.TreeConfig;
import com.yupont.util.TextUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author fenghuaigang
 * @date 2020/4/9
 */
public abstract class AbstractTreeLoader implements TreeLoader{

    private static final Logger log = LoggerFactory.getLogger(AbstractTreeLoader.class);
    /**
     * 模式
     */
    private static final String TREEMODE = "mode";
    /**
     * 懒加载模式
     */
    private static final String LAZY = "lazy";
    /**
     * 饥饿式加载模式
     */
    private static final String HUNGRY = "hungry";
    /**
     * 是否是父类型节点
     */
    private static final String IS_PARENT = "isParent";
    /**
     * 父级节点
     */
    private static final String PARENT = "parent";
    /**
     * 子节点集
     */
    private static final String CHILDREN = "children";
    /**
     * 节点元素
     */
    private static final String ELEMENT = "element";
    /**
     * 寄存 节点解析后的 数据
     */
    private static final String VALUE_LIST = "valueList";
    /**
     * 分组解析型节点
     */
    private static final String GROUP = "group";
    /**
     * 分组类解析节点保存的记录值
     */
    private static final String MARK = "mark";
    /**
     * 节点过滤条件
     */
    private static final String FILTER = "filter";
    /**
     * 节点排序规则
     */
    private static final String ORDER_BY = "order";
    /**
     * 节点外键信息
     */
    private static final String FORGIEN_KEYS = "forgienKeys";
    /**
     * 节点模型数据查询来源
     */
    private static final String MODEL = "model";
    /**
     * 是否是自加载模式 true/false, false时是固定不变的节点
     */
    private static final String SELF_IGNORE = "selfIgnore";
    /**
     * 是否初始阶段展开当前节点
     */
    private static final String EXSELF = "exself";
    /**
     * 展开当前节点的模型下标
     */
    private static final String SERIAL = "serial";
    /**
     * 正则 匹配字母
     */
    private static final String A_Z_A_Z = "[a-zA-Z]+";
    /*** 树配置定位标识 */
    protected String treeNodeId;
    /*** 当期节点数据，作父节点数据 */
    protected String data;
    /*** 树配置类型名称*/
    protected String treeType;
    /*** 树配置 */
    protected JSONObject treeNodeConfigs;
    /***
     * 启用树 关键词、自动补全查询缓存(节点数据仍然从 db 查询)
     *  缓存 - 树配置type 节点索引 中文名 英文名
     */
    protected boolean autoComplete;

    protected TreeSource treeSource;

    public AbstractTreeLoader(String treeNodeId, String data, TreeConfig treeConfig) {
        super();
        this.treeNodeId = treeNodeId;
        this.data = data;
        this.treeNodeConfigs = getAllConfig(treeConfig);
    }

    public AbstractTreeLoader(String treeNodeId, String data, JSONObject treeNodeConfigs) {
        super();
        this.treeNodeId = treeNodeId;
        this.data = data;
        this.treeNodeConfigs = treeNodeConfigs;
    }

    @Override
    public TreeLoader setSource(TreeSource treeSource) {
        this.treeSource = treeSource;
        return this;
    }

    @Override
    public JSONArray search() {
        return excuted();
    }

    /**
     * 获得节点配置信息
     * @param treeConfig
     * @return
     */
    protected JSONObject getAllConfig(TreeConfig treeConfig) {
        return JSON.parseObject(treeConfig.getModel());
    }

    protected JSONArray excuted() {
        JSONArray loader = treeNodesLoader(treeNodeId, data, treeNodeConfigs);

        if (!loader.isEmpty()) {
            // fjw：查询时直接排序，文字类节点应按树配置顺序显示
            addParentPulsIcon(loader, treeNodeConfigs.getString(TREEMODE));
            return loader;
        } else {
            return loader;
        }
    }

    private void addParentPulsIcon(JSONArray loader, String mode) {
        loader.stream().filter(j -> Objects.nonNull(((JSONObject) j).getJSONArray(CHILDREN))).forEach(o -> {
            JSONObject j = (JSONObject) o;
            j.put(IS_PARENT, true);
            if (LAZY.equalsIgnoreCase(mode)) {
                j.remove(CHILDREN);
            }
        });

    }

    private JSONArray treeNodesLoader(String treeNodeId, String data, JSONObject treeNodeConfigs) {
        if (treeNodeConfigs.isEmpty()) {
            return new JSONArray();
        }
        JSONObject node = null;
        String mode = treeNodeConfigs.getString(TREEMODE);
        if (!StringUtils.isEmpty(mode)) {
            if (StringUtils.isEmpty(data) && StringUtils.isEmpty(treeNodeId)) {
                // 初始化加载
                node = initLoader(treeNodeConfigs, mode);
            } else {
                // 装载自身及子类信息
                node = selfLoader(treeNodeId, data, treeNodeConfigs, mode);
            }
            // 解析
            if (Objects.nonNull(node)) {
                parseChildrenNodes(node, mode);
                // // 转换
                JSONArray nodes = exchange(treeNodeId, node);
                // hungry模式，递归加载
                if (mode.equalsIgnoreCase(HUNGRY) && !nodes.isEmpty()) {
                    Integer exIndex = node.getInteger(EXSELF);
                    if (exIndex != null) {
                        for (int i = 0; i < nodes.size(); i++) {
                            if (exIndex == i) {
                                JSONObject exObj = nodes.getJSONObject(exIndex);
                                exselfLoder(exObj.getString(SERIAL), exObj, treeNodeConfigs);
                            } else {
                                nodes.getJSONObject(i).put(CHILDREN, new JSONArray());
                            }
                        }
                    }
                }
                return nodes;
            }
        }
        return new JSONArray();
    }

    private void exselfLoder(String treeNodeId, JSONObject exObj, JSONObject treeNodeConfigs) {
        if (exObj != null) {
            // 解析
            parseChildrenNodes(exObj, HUNGRY);
            // 获取children配置
            JSONArray children = exObj.getJSONArray(CHILDREN);
            if (children == null) {
                return;
            }
            JSONArray exChildren = new JSONArray();
            for (int i = 0; i < children.size(); i++) {
                JSONObject child = children.getJSONObject(i);
                JSONArray childChildren = child.getJSONArray(CHILDREN);
                JSONArray values = child.getJSONArray(VALUE_LIST);
                if ((childChildren != null && !childChildren.isEmpty()) || (values != null)) {
                    if (child.getBooleanValue(SELF_IGNORE)) {
                        if (Objects.nonNull(values) && !values.isEmpty()) {
                            values.stream().forEach(v -> {
                                JSONObject vch = (JSONObject) v;
                                vch.put(CHILDREN, child.getJSONArray(CHILDREN));
                                vch.put(SERIAL, child.getString(SERIAL));
                                vch.put(SELF_IGNORE, child.getBoolean(SELF_IGNORE));
                                vch.put(MODEL, child.getString(MODEL));
                                vch.put(FORGIEN_KEYS, child.getString(FORGIEN_KEYS));
                                vch.put(EXSELF, child.getString(EXSELF));
                                vch.put(FILTER, child.getString(FILTER));
                                vch.put(MARK, child.getString(MARK));
                                vch.put(GROUP, child.getString(GROUP));
                                exChildren.add(vch);
                            });
                            exObj.put(CHILDREN, exChildren);
                            Integer exself = child.getInteger(EXSELF);
                            JSONArray exChildChildren = exObj.getJSONArray(CHILDREN);
                            if (exChildChildren != null && exself != null) {
                                if (exself < exChildChildren.size()) {
                                    for (int j = 0; j < exChildChildren.size(); j++) {
                                        JSONObject c = exObj.getJSONArray(CHILDREN).getJSONObject(j);
                                        if (exself != j && c.containsKey(CHILDREN)) {
                                            c.put(CHILDREN, new JSONArray());
                                        }
                                    }
                                    exselfLoder(child.getString(SERIAL),
                                            exObj.getJSONArray(CHILDREN).getJSONObject(exself), treeNodeConfigs);
                                } else {
                                    for (int j = 0; j < exChildChildren.size() - 1; j++) {
                                        JSONObject c = exObj.getJSONArray(CHILDREN).getJSONObject(j);
                                        if (j != exChildChildren.size() - 1 && c.containsKey(CHILDREN)) {
                                            c.put(CHILDREN, new JSONArray());
                                        } else {
                                            exselfLoder(child.getString(SERIAL), exObj.getJSONArray(CHILDREN)
                                                    .getJSONObject(exChildChildren.size() - 1), treeNodeConfigs);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (childChildren != null && !childChildren.isEmpty()) {
                            exselfLoder(child.getString(SERIAL), child, treeNodeConfigs);
                        }
                    }
                } else {
                    exChildren.add(child);
                }
            }
        }
    }

    private JSONArray exchange(String treeNodeId, JSONObject node) {
        JSONArray rootTree = new JSONArray();
        if (!StringUtils.isEmpty(treeNodeId)) {
            JSONArray children = node.getJSONArray(CHILDREN);
            JSONObject p = JSON.parseObject(node.toString());
            p.remove(CHILDREN);
            p.remove(ELEMENT);
            p.remove(VALUE_LIST);
            if (Objects.nonNull(children) && !children.isEmpty()) {
                children.stream().forEach(ch -> {
                    JSONObject j = (JSONObject) ch;
                    if (j.getBooleanValue(SELF_IGNORE)) {
                        JSONArray array = j.getJSONArray(VALUE_LIST);
                        if (Objects.nonNull(array)) {
                            array.parallelStream().forEach(n -> {
                                JSONObject o = (JSONObject) n;
                                nodePut(p, j, o);
                            });
                            rootTree.addAll(array);
                        }
                    } else {
                        j.put(PARENT, p);
                        rootTree.add(j);
                    }
                });
            }
        } else {
            JSONObject par = JSON.parseObject(JSON.toJSONString(node));
            par.remove(CHILDREN);
            par.remove(VALUE_LIST);
            node.getJSONArray(CHILDREN).stream().forEach(t -> {
                JSONObject j = (JSONObject) t;
                if (j.getBoolean(SELF_IGNORE) && Objects.nonNull(j.getJSONArray(VALUE_LIST))) {
                    JSONArray arry = j.getJSONArray(VALUE_LIST);
                    arry.stream().forEach(a -> {
                        JSONObject o = (JSONObject) a;
                        nodePut(par, j, o);
                    });
                    rootTree.addAll(arry);
                } else {
                    j.put(PARENT, par);
                    rootTree.add(j);
                }
            });
        }
        return rootTree;
    }

    private void nodePut(JSONObject par, JSONObject j, JSONObject o) {
        o.put(PARENT, par);
        o.put(MODEL, j.getString(MODEL));
        o.put(SERIAL, j.getString(SERIAL));
        o.put(CHILDREN, j.getJSONArray(CHILDREN));
        o.put(SELF_IGNORE, j.getBoolean(SELF_IGNORE));
        o.put(EXSELF, j.getString(EXSELF));
        o.put(GROUP, j.getString(GROUP));
        o.put(MARK, j.getString(MARK));
    }

    /**
     * 动态加载子节点
     */
    private void parseChildrenNodes(JSONObject treeNode, String mode) {

        JSONArray children = treeNode.getJSONArray(CHILDREN);
        if (null == children || children.isEmpty()) {
            return;
        } else {
            JSONObject parent = JSON.parseObject(JSON.toJSONString(treeNode));
            parent.remove(CHILDREN);
            parent.remove(ELEMENT);
            parent.remove(VALUE_LIST);
            children.parallelStream().filter(Objects::nonNull).forEach(c -> {
                ((JSONObject) c).put(PARENT, parent);
                parseNode((JSONObject) c);
            });
        }
    }

    /**
     * 解析条件并加载子类具体信息;<br>
     * 查询结果存放- treeNode.put("valueList", array);
     *
     * @param treeNode
     */
    protected abstract void parseNode(JSONObject treeNode);

    protected String parseSql(JSONObject treeNode) {
        StringBuffer sf = new StringBuffer();
        if (Objects.nonNull(treeNode.getJSONObject(PARENT))) {
            parseforeignKeys(treeNode, sf);
            parsefilter(treeNode, sf);
        } else {
            parseOwnfilter(treeNode, sf);
        }
        parseOrder(treeNode, sf);
        return sf.toString();
    }

    protected void parseOrder(JSONObject treeNode, StringBuffer sf) {
        String order = treeNode.getOrDefault(ORDER_BY, "").toString();
        if (!StringUtils.isEmpty(order) && !order.toLowerCase().contains("order by")) {
            sf.append(" order by ");
        }
        sf.append(order);
    }

    protected void parsefilter(JSONObject treeNode, StringBuffer sf) {
        String filter = treeNode.getString(FILTER);
        if (!StringUtils.isEmpty(filter)) {
            if (!StringUtils.isEmpty(treeNode.getString(FORGIEN_KEYS))) {
                sf.append(" AND ");
            }
            // 条件拼接
            String pfilter = treeNode.getJSONObject(PARENT).getString(FILTER);
            if (!StringUtils.isEmpty(pfilter) && StringUtils.isEmpty(filter)) {
                log.info("动态节点" + treeNode.getString(SERIAL) + " 无过滤或外键条件");
            } else if (!StringUtils.isEmpty(pfilter) && !StringUtils.isEmpty(filter)) {
                boolean flag = true;
                if (filter.contains("=")) {
                    String[] split = filter.split("=");
                    for (int i = 0; i < split.length; i++) {
                        if (split[i].trim().matches(A_Z_A_Z) && pfilter.contains(split[i].trim())) {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    sf.append(pfilter.trim());
                    sf.append(" AND " + filter.trim());
                } else {
                    sf.append(filter.trim());
                }
            } else {
                sf.append(filter.trim());
            }
        }
    }

    private void parseOwnfilter(JSONObject treeNode, StringBuffer sf) {
        String filter = treeNode.getString(FILTER);
        if (!StringUtils.isEmpty(filter)) {
            sf.append(filter.trim());
        } else {
            log.info("动态节点" + treeNode.getString(SERIAL) + " 无过滤条件");
        }
    }

    /**
     * 非自关联外键解析
     *
     * @param treeNode
     * @param sf
     */
    @SuppressWarnings("unchecked")
    private void parseforeignKeys(JSONObject treeNode, StringBuffer sf) {
        String forgienkeys = treeNode.getString(FORGIEN_KEYS);
        // 自关联
        if (!StringUtils.isEmpty(forgienkeys)) {
            // 解析forgienKeys
            String[] forgienkeyss = forgienkeys.split(",");
            for (int i = 0; i < forgienkeyss.length; i++) {
                String fork = forgienkeyss[i];
                String[] split = fork.split(":");
                if (fork.contains(":") && split.length > 1) {
                    String match = TextUtil.match(split[1], "\\[", "\\]");
                    String rVal = split[1].replace(match, "");
                    if (!StringUtils.isEmpty(match) && !StringUtils.isEmpty(rVal)) {
                        JSONObject parent = topParent(treeNode, rVal);
                        if (Objects.isNull(parent)) {
                            log.info("动态节点" + treeNode.getString(SERIAL) + " 自关联条件对应父类中无属性：" + rVal + "存在");
                            return;
                        }
                        sf.append(split[0]);
                        Object appendVal = parent.get(rVal);
                        switch (match) {
                            case "[INTEGER]":
                                sf.append(" = " + appendVal);
                                break;
                            case "[STRING]":
                                sf.append(" = '" + appendVal + "'");
                                break;
                            case "[LIST]":
                                StringBuffer sfe = new StringBuffer();
                                if (appendVal.toString().contains("[") && appendVal.toString().contains("]")) {
                                    List<String> val = (List<String>) appendVal;
                                    val.forEach(s -> sfe.append(",'" + s + "'"));
                                } else {
                                    sfe.append(",'" + appendVal + "'");
                                }
                                sf.append(" in (" + sfe.substring(1) + ")");
                                break;
                            case "[DATE]":
                                sf.append("'" + DateFormatUtils.format(new Date((long) appendVal),"yyyy-MM-dd HH:mm:ss") + "'");
                                break;
                            default:
                                sf.append(appendVal);
                                break;
                        }
                    } else {
                        log.info("动态节点" + treeNode.getString(SERIAL) + " 自关联条件格式配置有误");
                    }
                } else {
                    log.info("动态节点" + treeNode.getString(SERIAL) + " 自关联条件格式配置有误");
                }
            }


        }
    }

    /**
     * 获取隔代父类中动态生成的父类条件
     *
     * @param treeNode
     * @param regix
     */
    protected JSONObject topParent(JSONObject treeNode, String regix) {
        JSONObject obj = treeNode.getJSONObject(PARENT);
        if (Objects.nonNull(obj)) {
            if (!StringUtils.isEmpty(obj.getString(regix))) {
                return obj;
            } else {
                return topParent(treeNode.getJSONObject(PARENT), regix);
            }
        }
        return null;
    }

    /**
     * 自加载
     *
     * @param data
     * @param treeNodeConfigs
     */
    private JSONObject selfLoader(String treeNodeId, String data, JSONObject treeNodeConfigs, String mode) {
        JSONObject treeNode = JSON.parseObject(data);
        String serial = StringUtils.isEmpty(treeNodeId) ? getSelfSerial(treeNode) : treeNodeId;
        if (Objects.isNull(serial)) {
            return null;
        }
        JSONObject currentConfig = getCurrentConfig(treeNodeConfigs, serial);
        setSelfNode(treeNode, currentConfig, mode);
        return treeNode;
    }

    private String getSelfSerial(JSONObject treeNode) {
        JSONArray children = treeNode.getJSONArray(CHILDREN);
        if (null != children && !children.isEmpty() && !"0000".equals(treeNode.getString(SERIAL))) {
            return getSelfSerial(children.getJSONObject(0));
        } else {
            return treeNode.getString(SERIAL);
        }
    }

    private void setSelfNode(JSONObject treeNode, JSONObject currentNode, String mode) {
        // 当前含有配置
        if (null != currentNode) {
            if (LAZY.equalsIgnoreCase(mode)) {
                if (null == treeNode.getJSONArray(CHILDREN) || treeNode.getJSONArray(CHILDREN).isEmpty()) {
                    JSONArray childrenNode = currentNode.getJSONArray(CHILDREN);
                    if (childrenNode != null && !childrenNode.isEmpty()) {
                        childrenNode.stream().filter(n -> ((JSONObject) n).containsKey(CHILDREN))
                                .forEach(n -> ((JSONObject) n).put(CHILDREN, new JSONArray()));
                        treeNode.put(CHILDREN, childrenNode);
                    }
                } else {
                    setSelfNode(treeNode.getJSONArray(CHILDREN).getJSONObject(0), currentNode, mode);
                }
            } else if (HUNGRY.equalsIgnoreCase(mode)) {
                JSONObject hungryConfig = initLoader(currentNode, mode);
                JSONArray array = hungryConfig.getJSONArray(CHILDREN);
                treeNode.put(CHILDREN, array);
            }
        }
    }

    private JSONObject initLoader(JSONObject treeNodeConfigs, String mode) {

        if (LAZY.equalsIgnoreCase(mode)) {
            treeNodeConfigs.getJSONArray(CHILDREN).stream().forEach(t -> {
                JSONObject j = (JSONObject) t;
                if (j.getJSONArray(CHILDREN) != null) {
                    j.put(CHILDREN, new JSONArray());
                }
            });
        } else if (HUNGRY.equalsIgnoreCase(mode)) {
            JSONArray array = treeNodeConfigs.getJSONArray(CHILDREN);
            if (Objects.isNull(array) || array.isEmpty()) {
                return treeNodeConfigs;
            }
            // 含exself展开元素，其余子元素的exself属性忽略,children置空；
            Integer exIndex = treeNodeConfigs.getInteger(EXSELF);
            if (exIndex != null) {
                if (exIndex < array.size()) {
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject child = array.getJSONObject(i);
                        if (exIndex != null && i != exIndex) {
                            child.put(CHILDREN, new JSONArray());
                        } else {
                            // 对于当前hungry模式加载层进行子类配置递归，剔除子类中不需加载的模块配置
                            initLoader(child, mode);
                        }
                    }
                } else {
                    // 保留最后一个子节点的children 配置
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject child = array.getJSONObject(i);
                        if (i < array.size() - 1) {
                            child.put(CHILDREN, new JSONArray());
                        } else {
                            // 对于当前hungry模式加载层进行子类配置递归，剔除子类中不需加载的模块配置
                            initLoader(child, mode);
                        }
                    }
                }
            }
        } else {
            log.info("模版根配置：mode-" + mode + "有误");
        }
        return treeNodeConfigs;
    }

    /**
     * 定位当前节点的配置信息
     *
     * @param config
     * @param serial
     * @return
     */
    private JSONObject getCurrentConfig(JSONObject config, String serial) {
        if (config.getString(SERIAL).equals(serial)) {
            return config;
        }
        JSONArray children = (JSONArray) config.get(CHILDREN);
        if (children == null) {
            return null;
        }
        for (int i = 0; i < children.size(); i++) {
            JSONObject c = getCurrentConfig(children.getJSONObject(i), serial);
            if (c != null) {
                return c;
            }
        }
        return null;
    }


    public void setTreeNodeId(String treeNodeId) {
        this.treeNodeId = treeNodeId;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTreeNodeConfigs(JSONObject treeNodeConfigs) {
        this.treeNodeConfigs = treeNodeConfigs;
    }


}

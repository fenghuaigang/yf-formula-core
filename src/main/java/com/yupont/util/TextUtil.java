package com.yupont.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 */
public class TextUtil {
	// 简体中文的编码范围从B0A1（45217）一直到F7FE（63486）
	private static final int CHS_BEGIN = 45217;
	private static final int CHS_END = 63486;

	// 按照声 母表示，这个表是在GB2312中的出现的第一个汉字，也就是说“啊”是代表首字母a的第一个汉字。
	// i, u, v都不做声母, 自定规则跟随前面的字母
	private static char[] gbTable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪',
			'期', '然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', };

	// 二十六个字母区间对应二十七个端点
	// GB2312码汉字区间十进制表示
	private static int[] table = new int[27];

	// 对应首字母区间表
	private static char[] initialTable = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'h', 'j', 'k', 'l', 'm', 'n', 'o',
			'p', 'q', 'r', 's', 't', 't', 't', 'w', 'x', 'y', 'z', };

	// 初始化
	static {
		for (int i = 0; i < 26; i++) {
			// 得到GB2312码的首字母区间端点表，十进制。
			table[i] = gbValue(gbTable[i]);
		}
		// 区间表结尾
		table[26] = CHS_END;
	}

	private TextUtil() {
	}

	// ------------------------public方法区------------------------
	/**
	 * 获取中文字符串的字符串首字母字符串，最重要的一个方法，思路如下：一个个字符读入、判断、输出
	 *
	 * @param sourceStr
	 * @return 返回一个汉字拼音首字母
	 */
	public static String cn2py(String sourceStr) {
		StringBuilder result = new StringBuilder();
		int i;

		for (i = 0; i < sourceStr.length(); i++) {
			result.append(toInitial(sourceStr.charAt(i)));
		}

		return result.toString();
	}

	// ------------------------private方法区------------------------
	/**
	 * 输入字符,得到他的声母,英文字母返回对应的大写字母,其他非简体汉字返回 '0'
	 *
	 * @return 返回简体汉字大写声母
	 * @throws UnsupportedEncodingException
	 */
	public static char toInitial(char ch) {
		char ret;
		// 对英文字母的处理：小写字母转换为大写，大写的直接返回
		if (ch >= 'a' && ch <= 'z') {
			ret = (char) (ch - 'a' + 'A');
		} else if (ch >= 'A' && ch <= 'Z') {
			ret = ch;
		} else {
			/**
			 * 对非英文字母的处理：转化为首字母，然后判断是否在码表范围内， 若不是，则直接返回。 若是，则在码表内的进行判断。
			 * 汉字转换GB2312编码
			 */
			int gb = gbValue(ch);

			// 在码表区间之前，直接返回
			ret = ((gb < CHS_BEGIN) || (gb > CHS_END)) ? ch : gb2Initial(gb);
		}
		return ret;
	}

	/**
	 * 取出汉字的编码 cn 汉字十进制表示。
	 */
	public static int gbValue(char ch) {
		String str = Character.toString(ch);
		try {
			byte[] bytes = str.getBytes("GB2312");
			if (bytes.length >= 2) {
				return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * GB编码
	 *
	 * @param gb
	 * @return
	 */
	private static char gb2Initial(int gb) {
		int i;
		if (gb == CHS_END)
			i = 25;
		else {
			// 判断匹配码表区间，匹配到就break,判断区间形如“[,)”
			for (i = 0; i < 26; i++) {
				if ((gb >= table[i]) && (gb < table[i + 1])) {
					break;
				}
			}
		}

		// 补上GB2312区间最右端
		return initialTable[i];
	}

	/**
	 * 通配符匹配
	 *
	 * @param pattern
	 *            含通配符的字符串模式
	 * @param str
	 *            字符串
	 * @return 匹配成功true，匹配失败false
	 */
	public static boolean wildMatch(String pattern, String str) {
		String javaPattern = toJavaPattern(pattern);
		return java.util.regex.Pattern.matches(javaPattern, str);
	}

	/**
	 * 含通配符的字符串模式转化为正则表达式
	 *
	 * @param pattern
	 * @return
	 */
	private static String toJavaPattern(String pattern) {
		StringBuilder result = new StringBuilder("^");
		char[] metachar = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '.', '\\' };
		for (int i = 0; i < pattern.length(); i++) {
			char ch = pattern.charAt(i);
			boolean isMeta = false;
			for (int j = 0; j < metachar.length; j++) {
				if (ch == metachar[j]) {
					result.append("/" + ch);
					isMeta = true;
					break;
				}
			}
			if (!isMeta) {
				result.append((ch == '*' || ch == '?') ? ("." + ch) : ch);
			}
		}
		result.append("$");
		return result.toString();
	}

	/**
	 * 首字母大写
	 *
	 * @param name
	 * @return
	 */
	public static String firstUpper(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * 首字母小写
	 *
	 * @param name
	 * @return
	 */
	public static String firstLower(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public static Boolean toBoolean(Object arg, Boolean defaultValue) {
		if (arg instanceof Boolean) {
			return (Boolean) arg;
		}

		Boolean ret = null;
		String s = String.valueOf(arg);
		if (TextUtil.isEmpty(s)) {
			ret = null;
		}

		if ("true".equalsIgnoreCase(s) || "1".equals(s) || "是".equals(s)) {
			ret = true;
		} else if ("false".equalsIgnoreCase(s) || "0".equals(s) || "否".equals(s)) {
			ret = false;
		}
		return ret == null ? defaultValue : ret;
	}

	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean isNull(Object s) {
		return s == null;
	}

	public static boolean isNotEmpty(String s) {
		return !(isEmpty(s));
	}

	public static boolean isEmpty(String s) {
		if (s == null || s.length() == 0 || "undefined".equals(s) || "null".equals(s)) {
			return true;
		} else {
			return false;
		}
	}


	public static boolean isNotBlank(final CharSequence s){
		return !isBlank(s);
	}

	public static boolean isBlank(final CharSequence s) {
		if (s == null) {
			return true;
		}
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isTrue(String s) {
		return "true".equalsIgnoreCase(s);
	}

	/**
	 * 将数组串联成字符串，用逗号连接
	 *
	 * @param args
	 * @return
	 */
	public static String join(Object[] args) {
		return join(args, ',', true);
	}

	public static String join(Iterable<?> args, char seperator) {
		StringBuilder sb = new StringBuilder();
		if (args != null) {
			Iterator<?> iterator = args.iterator();
			while (iterator.hasNext()) {
				Object next = iterator.next();
				String val = String.valueOf(next);
				if (next == null || TextUtil.isEmpty(val)) {
					break;
				}
				if (sb.length() > 0) {
					sb.append(seperator);
				}
				sb.append(val);
			}
		}
		return sb.toString();
	}

	/**
	 * 将数据串联成字符串，用seperator符号连接
	 *
	 * @param args
	 *            数组
	 * @param seperator
	 *            分隔符号
	 * @return 返回串接后的字符串
	 */
	public static String join(Object[] args, char seperator) {
		return join(args, seperator, true);
	}

	public static String join(Object[] args, char seperator, Boolean ignoreNull) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			String s = String.valueOf(args[i]);
			if (ignoreNull && (args[i] == null || TextUtil.isEmpty(s))) {
				break;
			}
			if (sb.length() > 0) {
				sb.append(seperator);
			}
			sb.append(s);
		}
		return sb.toString();
	}

	public static String match(String txt, String from, String to) {
		Pattern p = Pattern.compile(from + "([\\s\\S]*?)" + to);
		Matcher m = p.matcher(txt);
		while (m.find()) {
			return m.group(0);
		}
		return "";
	}

	public static int matchCount(String txt, String from, String to) {
		Pattern p = Pattern.compile(from + "([\\s\\S]*?)" + to);
		Matcher m = p.matcher(txt);
		int count = 0;
		while (m.find()) {
			count += m.groupCount();
		}
		return count;
	}

	public static int matchCount(String txt, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher match = p.matcher(txt);
		int count = 0;
		while (match.find()) {
			count ++;
		}
		return count;
	}

	public static String matchRegex(String txt, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher match = p.matcher(txt);
		String res = "";
		while (match.find()) {
			res += match.group();
			break;
		}
		return res;
	}

	public static String matchRegexAppend(String txt, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher match = p.matcher(txt);
		String res = "";
		while (match.find()) {
			res += match.group();
		}
		return res;
	}

	/**
	 * 匹配字符串中成对引号中的内容
	 *
	 * @param txt
	 * @return
	 */
	public static List<String> matchSingleQuote(String txt) {
		List<String> q = new ArrayList<>();

		String regstr = "\'[^\']*\'";
		Pattern p = Pattern.compile(regstr);
		Matcher m = p.matcher(txt);
		while (m.find()) {
			q.add(m.group(0));
		}
		return q;
	}

	/**
	 * 切分空字符隔开的数据，引号内的空格不分格
	 *
	 * @param data
	 */
	public static List<String> splitOutOfQuote(String s) {
		// 检查带空格引号的数据，将数据暂时替换成占位符{0} {1}....
		List<String> qs = matchSingleQuote(s);
		if (!qs.isEmpty()) {
			for (int i = 0; i < qs.size(); i++) {
				s = s.replace(qs.get(i), "{" + i + "}");
			}
		}

		// 按空字符分割
		List<String> ss = Arrays.asList(s.split("[\\s]+"));

		// 还原
		if (!qs.isEmpty()) {
			int counter = 0;
			for (int i = 0; i < ss.size(); i++) {
				String token = "{" + counter + "}";
				if (ss.get(i).contains(token)) {
					ss.set(i, ss.get(i).replace(token, qs.get(counter)).replace("\'", ""));
					counter++;
				}
			}
		}
		return ss;
	}

	public static String quote(String s) {
		return "'" + s + "'";
	}

	/** 配置字符串结尾，一旦成功立即返回 */
	public static boolean endsWith(String in, String... strs) {
		for (String s : strs) {
			if (in.endsWith(s)) {
				return true;
			}
		}
		return false;
	}

	/** 字符串末尾子串 */
	public static String last(String in, int len) {
		return in.substring(in.length() - len, in.length());
	}

	/** 比对字符串相等，一旦成功立即返回 */
	public static boolean equal(String in, String... strs) {
		for (String s : strs) {
			if (in.equals(s)) {
				return true;
			}
		}
		return false;
	}

	/** 配置字符串，一旦成功立即返回 */
	public static int indexOf(String in, String... strs) {
		for (String s : strs) {
			int pos = in.indexOf(s);
			if (pos >= 0) {
				return pos;
			}
		}
		return -1;
	}

	/** 逐项匹配字符串结尾，直到匹配成功即删除，首次删除后即返回 */
	public static String removeOnceEnd(String in, String... strs) {
		for (String s : strs) {
			if (in.endsWith(s)) {
				if (in.equals(s)) {
					return "";
				}
				return in.substring(0, in.length() - s.length());
			}
		}
		return in;
	}

	/** 清除匹配的字符串 */
	public static String remove(String in, String... strs) {
		for (String s : strs) {
			in = in.replace(s, "");
		}
		return in;
	}

	/** 逐项匹配字符串，直到匹配成功即删除，首次删除后即返回 */
	public static String removeOnce(String in, String... strs) {
		for (String s : strs) {
			if (in.indexOf(s) >= 0) {
				return in.replace(s, "");
			}
		}
		return in;
	}

	/**
	 * 判断是否含中文字符
	 *
	 * @param str
	 * @return
	 */
	public static boolean isChs(String str) {
		return !str.matches("[^\\u4e00-\\u9fa5]+");
	}

	/**
	 * 判断是否含有英文字母
	 *
	 * @param str
	 * @return
	 */
	public static boolean isEns(String str) {
		return str.matches(".*[a-zA-z].*");
	}

	/**
	 * 返回中文字符
	 *
	 * @param str
	 * @return
	 */
	public static String getChs(String str) {
		String regx = "[^\u4e00-\u9fa5]";
		return str.replaceAll(regx, "");
	}

	/**
	 * 返回英文字母、数字
	 *
	 * @param str
	 * @return
	 */
	public static String getEns(String str) {
		String regx = "[^a-z^A-Z^0-9]";
		return str.replaceAll(regx, "");
	}
	
	/**
	 * 判断是否是数字类型，包括含小数的
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
//		String regx = "^-?\\d+$";
		String regx = "^[+-]?\\d+(\\.\\d+)?$";
		return java.util.regex.Pattern.matches(regx, str);
	}

	public static String remove(String text, String regex) {
		return text.replaceAll(regex, "");
	}

	/**
	 * 提取字符中成串的数字
	 *
	 * @param content
	 * @return
	 */
	public static List<String> matchNumbers(String content) {
		List<String> list = new ArrayList<>();
		char[] array = content.toCharArray();
		int index = 0;
		for (int i = 0; i < array.length; i++) {
			char c = content.charAt(i);
			if (TextUtil.isNotEmpty(String.valueOf(c).replaceAll("[^0-9]", ""))) {
				if (index == 0) {
					index = i;
				}
			} else {
				if (index != 0) {
					list.add(content.substring(index, i));
				}
				index = 0;
			}
		}
		if (index != 0) {
			list.add(content.substring(index));
		}
		return list;
	}

	/**
	 * 判断大写
	 *
	 * @param c
	 * @return
	 */
	public static boolean isUpperCase(char c) {
		return c >= 'A' && c <= 'Z';
	}

}
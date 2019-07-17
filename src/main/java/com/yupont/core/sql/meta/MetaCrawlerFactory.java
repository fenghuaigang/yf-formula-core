package com.yupont.core.sql.meta;

import java.sql.Connection;

/**
 * 爬虫工厂基类
 * @author fjw
 *
 */
public interface MetaCrawlerFactory {
	/**
	 * 根据con包含的数据库信息，生成爬虫
	 * @param con
	 * @return
	 */
	MetaCrawler newInstance(Connection con);
}

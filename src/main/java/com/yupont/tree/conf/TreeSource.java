package com.yupont.tree.conf;


import java.sql.Connection;

/**
 * @author fenghuaigang
 * @date 2020/4/9
 */
public class TreeSource {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}

/*
 * 文件名: DBUtils.java
 * 版权: Copyright 2015 中星宝 Tech. Co. Ltd. All Rights Reserved.
 */
package com.xiaolan.code;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * DBUtil，数据库访问工具类<br/>
 * 对应测试类： {@link DBUtilTest}
 * @preserve all
 */
public class DBUtil {

    public static void main(String[] args) {
        System.out.println(DBUtil.getTableComment("t_camera"));
        List<TableDO> list = listColumnOfTable("t_camera");
        System.out.println(list.size());
    }

    private static Connection con = null;

    /**
     * 所选数据库
     */
    private static String database;

    /**
     * 查询表注释
     * @param table
     * @return
     */
    public static String getTableComment(String table) {

        /**
         * 查看表注释sql
         */
        String sql = " SELECT table_comment FROM TABLES WHERE TABLE_SCHEMA=? AND table_name=? limit 1";
        PreparedStatement preStmt = null;
        ResultSet rs = null;
        try {
            openConnection();
            preStmt = con.prepareStatement(sql);
            preStmt.setObject(1, database);
            preStmt.setObject(2, table);
            rs = preStmt.executeQuery();
            while (null != rs && rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != rs)
                    rs.close();
                if (null != preStmt)
                    preStmt.close();
                closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 查询对应表所有字段详情
     * @param table
     * @return
     */
    public static List<TableDO> listColumnOfTable(String table) {
        /**
         * 查看表字段详情sql
         */
        String sql = "SELECT column_name,data_type,is_nullable,column_comment,column_key,column_default FROM COLUMNS WHERE table_schema=? AND table_name=?";
        List<TableDO> lists = new ArrayList<TableDO>();
        PreparedStatement preStmt = null;
        ResultSet rs = null;
        try {
            openConnection();
            preStmt = con.prepareStatement(sql);
            preStmt.setObject(1, database);
            preStmt.setObject(2, table);
            rs = preStmt.executeQuery();
            TableDO tableDO = null;
            while (null != rs && rs.next()) {
                tableDO = new TableDO();
                tableDO.setAttrName(rs.getString(1));
                tableDO.setColumnName(rs.getString(1));
                tableDO.setDataType(rs.getString(2));
                tableDO.setIsNullAble(rs.getString(3));
                tableDO.setColumnComment(rs.getString(4));
                tableDO.setPrimaryKey(rs.getString(5));
                tableDO.setColumnDefault(rs.getString(6));
                lists.add(tableDO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != rs)
                    rs.close();
                if (null != preStmt)
                    preStmt.close();
                closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lists;
    }

    public static Connection openConnection() throws SQLException, ClassNotFoundException, IOException {
        if (null == con || con.isClosed()) {
            Properties p = new Properties();
            p.load(DBUtil.class.getResourceAsStream("/config.properties"));
            database = p.getProperty("database");// 所选数据库
            Class.forName(p.getProperty("jdbc.driver"));
            con = DriverManager.getConnection(p.getProperty("jdbc.url"), p.getProperty("jdbc.username"), p.getProperty("jdbc.password"));
        }
        return con;
    }

    public static void closeConnection() throws SQLException {
        try {
            if (null != con)
                con.close();
        }
        finally {
            con = null;
            System.gc();
        }
    }

}
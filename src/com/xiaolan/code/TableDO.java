/*
 * 文件名: TableDO.java
 * 版权: Copyright 2015 中星宝 Tech. Co. Ltd. All Rights Reserved.
 */
package com.xiaolan.code;

/**
 * 表对象详情 （字段、类型、注释等）<br>
 * set方法里就作处理 改为驼峰命名法
 * @author wangshiyan
 * @version [<版本号>, 2017年2月23日]
 * @see [<相关类>/<相关方法>]
 * @since [<产品>/<模块版本>]
 */
public class TableDO {
    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 是否 可以为空（yes,no）
     */
    private String isNullAble;

    /**
     * 字段注释
     */
    private String columnComment;

    /**
     * PRI表示主键
     */
    private String primaryKey;

    /**
     * 类属性名
     */
    private String attrName;

    /**
     * 字段默认值 时间默认值： CURRENT_TIMESTAMP，组装sql时就 = CURRENT_TIMESTAMP
     */
    private String columnDefault;

    /**
     * 是否是datetime类型
     */
    private boolean datetime;

    public TableDO() {}

    public boolean isDatetime() {
        return datetime;
    }

    public void setDatetime(boolean datetime) {
        this.datetime = datetime;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDataType() {
        return dataType;
    }

    /**
     * 字段类型转为java 包装类型如： Integer Long,Double,String
     * @param dataType
     */
    public void setDataType(String dataType) {
        dataType = dataType.toLowerCase();
        if (-1 != ",int,tinyint,smallint,".indexOf("," + dataType + ",")) {// integer
            this.dataType = "Integer";
        } else if (-1 != ",float,,".indexOf("," + dataType + ",")) {// float
            this.dataType = "Float";
        } else if (-1 != ",double,decimal,".indexOf("," + dataType + ",")) {// double
            this.dataType = "Double";
        } else if (-1 != ",bigint,".indexOf("," + dataType + ",")) {// double
            this.dataType = "Long";
        } else {// String
            this.dataType = "String";
        }

        // else if (-1 != ",char,".indexOf("," + dataType + ",")) {// char
        // this.dataType = "Character";
        // }
    }

    public String getIsNullAble() {
        return isNullAble;
    }

    public void setIsNullAble(String isNullAble) {
        this.isNullAble = isNullAble.toLowerCase();
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        attrName = attrName.toLowerCase();
        if (attrName.indexOf("_") != -1) {// 字段有下划线 改为驼峰命名规则
            String[] columnArr = attrName.split("_");
            attrName = columnArr[0];
            for (int i = 1; i < columnArr.length; i++) {
                attrName += columnArr[i].substring(0, 1).toUpperCase() + columnArr[i].substring(1);
            }
            this.attrName = attrName;

        } else {
            this.attrName = attrName;
        }
    }

    @Override
    public String toString() {
        return "TableDO [columnName=" + columnName + ", dataType=" + dataType + ", isNullAble=" + isNullAble + ", columnComment=" + columnComment
                + ", primaryKey=" + primaryKey + ", attrName=" + attrName + "]";
    }

}

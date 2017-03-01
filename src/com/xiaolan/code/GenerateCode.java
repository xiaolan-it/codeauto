/*
 * 文件名: GenerateCode.java
 * 版权: Copyright 2015 中星宝 Tech. Co. Ltd. All Rights Reserved.
 */
package com.xiaolan.code;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * mvc代码生成 规则<br>
 * 1、数据类型全部使用包装类型如：Integer、Double，会去掉table前缀t_<br>
 * @author wangshiyan
 * @version [<版本号>, 2017年2月23日]
 * @see [<相关类>/<相关方法>]
 * @since [<产品>/<模块版本>]
 */
public class GenerateCode {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < tableModules.length; i++) {
            generateEntityQuery(tableModules[i]);
            generateEntityDO(tableModules[i]);
            generateEntityVO(tableModules[i]);
            generateDao(tableModules[i]);
            generateService(tableModules[i]);
            generateServiceImpl(tableModules[i]);
            generateSqlXml(tableModules[i]);
            generateController(tableModules[i]);
        }
        System.out.println("1、复制对应代码到项目下");
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 需要处理的表
     */
    static String[] tableModules = null;

    /**
     * module 代码包开始位置 如：com.jifeng.module2
     */
    static String module_package = null;

    /**
     * controller 代码包开始位置 如：com.jifeng.web.controller
     */
    static String controller_package = null;

    /**
     * 是否使用swagger，使用的话就在entity注解属性
     */
    static boolean use_swagger = true;

    /**
     * 代码作者
     */
    static String author = null;

    /**
     * 当前时间 年月日 根据配置格式化
     */
    static String current_date = null;

    /**
     * 代码放路径
     */
    static String path;
    static {
        Properties p = new Properties();
        try {
            p.load(DBUtil.class.getResourceAsStream("/config.properties"));
            tableModules = p.getProperty("tables").split(";");// 需要处理的表
            path = p.getProperty("path");
            module_package = p.getProperty("module_package");
            controller_package = p.getProperty("controller_package");
            use_swagger = Boolean.parseBoolean(p.getProperty("use_swagger"));
            author = p.getProperty("formater_author");
            SimpleDateFormat sdf = new SimpleDateFormat(p.getProperty("formater_date"));
            current_date = sdf.format(new Date());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成实体DO
     * @param tableModule 表名,模块 如：t_camera,testmd
     */
    public static void generateEntityDO(String tableModule) throws Exception {
        if (null != tableModule) {
            Map<String, String> contentMap = new HashMap<String, String>(); // 内容
            String packname = "entity";
            String table = tableModule.split(",")[0]; // 表
            String module = tableModule.split(",")[1]; // 模块
            String className = getClassName(table) + "DO";// 表对应类名
            String packageStr = module_package + "." + module + "." + packname;
            String filePath = path + module_package.replaceAll("\\.", "\\\\") + "\\" + module + "\\" + packname;// 代码目录
            String fileName = className + ".java";// 代码文件名
            String templateFileName = "entity_do_template" + (use_swagger ? "_swagger" : "") + ".txt";// 模板文件
            List<TableDO> list = DBUtil.listColumnOfTable(table);
            StringBuffer attrsSb = new StringBuffer();// 所有属性
            StringBuffer setgetSb = new StringBuffer();// 所有setter/getter方法
            TableDO tableDO = null;
            for (int i = 0; i < list.size(); i++) {
                tableDO = list.get(i);
                if (!"CURRENT_TIMESTAMP".equals(tableDO.getColumnDefault())) {
                    attrsSb.append(getEntityAttrTemplate(tableDO, i == list.size() - 1 ? true : false, true)); // 所有属性
                    setgetSb.append(getEntityGetterTemplate(tableDO));// getter
                    setgetSb.append(getEntitySetterTemplate(tableDO, i == list.size() - 1 ? true : false));// setter
                }
            }
            contentMap = getCommonMapContent(contentMap, tableModule);
            contentMap.put("package", packageStr);// 包字符串
            contentMap.put("className", className);// 类名
            contentMap.put("attrs", attrsSb.toString());
            contentMap.put("setget", setgetSb.toString());
            contentMap.put("toString", getEntityToStringTemplate(list, className, true));
            FileUtils.writeFile(filePath, fileName, templateFileName, contentMap);
        }

    }

    /**
     * 生成实体VO
     * @param tableModule 表名,模块 如：t_camera,testmd
     */
    public static void generateEntityVO(String tableModule) throws Exception {
        if (null != tableModule) {
            Map<String, String> contentMap = new HashMap<String, String>(); // 内容
            String packname = "entity";
            String table = tableModule.split(",")[0]; // 表
            String module = tableModule.split(",")[1]; // 模块
            String className = getClassName(table) + "VO";// 表对应类名
            String packageStr = module_package + "." + module + "." + packname;

            String filePath = path + module_package.replaceAll("\\.", "\\\\") + "\\" + module + "\\" + packname;// 代码目录
            String fileName = className + ".java";// 代码文件名
            String templateFileName = "entity_vo_template" + (use_swagger ? "_swagger" : "") + ".txt";// 模板文件

            List<TableDO> list = DBUtil.listColumnOfTable(table);
            StringBuffer attrsSb = new StringBuffer();// 所有属性
            StringBuffer setgetSb = new StringBuffer();// 所有setter/getter方法
            TableDO tableDO = null;
            for (int i = 0; i < list.size(); i++) {
                tableDO = list.get(i);
                attrsSb.append(getEntityAttrTemplate(tableDO, i == list.size() - 1 ? true : false, false)); // 所有属性
                setgetSb.append(getEntityGetterTemplate(tableDO));// getter
                setgetSb.append(getEntitySetterTemplate(tableDO, i == list.size() - 1 ? true : false));// setter
            }
            contentMap = getCommonMapContent(contentMap, tableModule);
            contentMap.put("package", packageStr);// 包字符串
            contentMap.put("className", className);// 类名
            contentMap.put("attrs", attrsSb.toString());
            contentMap.put("setget", setgetSb.toString());
            contentMap.put("toString", getEntityToStringTemplate(list, className, false));
            FileUtils.writeFile(filePath, fileName, templateFileName, contentMap);
        }

    }

    /**
     * 生成实体query
     * @param tableModule 表名,模块 如：t_camera,testmd
     */
    public static void generateEntityQuery(String tableModule) throws Exception {

        if (null != tableModule) {
            Map<String, String> contentMap = new HashMap<String, String>(); // 内容
            String packname = "query";
            String table = tableModule.split(",")[0]; // 表
            String module = tableModule.split(",")[1]; // 模块
            String className = getClassName(table) + "Query";// 表对应类名
            String packageStr = module_package + "." + module + "." + packname;
            String filePath = path + module_package.replaceAll("\\.", "\\\\") + "\\" + module + "\\" + packname;// 代码目录
            String fileName = className + ".java";// 代码文件名
            String templateFileName = "entity_query_template" + (use_swagger ? "_swagger" : "") + ".txt";// 模板文件
            contentMap = getCommonMapContent(contentMap, tableModule);
            contentMap.put("package", packageStr);// 包字符串
            contentMap.put("className", className);// 类名
            FileUtils.writeFile(filePath, fileName, templateFileName, contentMap);
        }

    }

    /**
     * 生成sql mapper
     * @param tableModule 表名,模块 如：t_camera,testmd
     */
    public static void generateSqlXml(String tableModule) throws Exception {
        if (null != tableModule) {
            Map<String, String> contentMap = new HashMap<String, String>(); // 内容
            String table = tableModule.split(",")[0]; // 表名
            String module = tableModule.split(",")[1]; // 模块
            String tableNameSubstring = getClassName(table); // 根据表名生成类名 ，去掉表前缀t_,首字母大写
            String filePath = path + module_package.replaceAll("\\.", "\\\\") + "\\" + module + "\\dao\\mapper";// 代码目录
            String fileName = tableNameSubstring + "_mapper.xml";// 代码文件名
            String templateFileName = "sql_template.txt";// 模板文件
            List<TableDO> list = DBUtil.listColumnOfTable(table);
            String[] primaryKeyColumnAttr = getEntityPrimaryKey(list); // 主键key对应实体属性
            String primaryKeyColumn = null; // 主键字段
            String primaryKeyAttr = null; // 主键对应实体属性
            if (null != primaryKeyColumnAttr) {
                primaryKeyColumn = primaryKeyColumnAttr[0];
                primaryKeyAttr = primaryKeyColumnAttr[1];
            }
            StringBuffer baseColumn = new StringBuffer();// 除主键外的字段name,age
            StringBuffer insertValues = new StringBuffer();// 除主键外的属性 如#{name},#{age}
            StringBuffer resultMapResults = new StringBuffer();// resultMap 中所有的result
            StringBuffer updateSetColumn = new StringBuffer();// update语句中的 set中的 xx=#{xx}
            StringBuffer selectBaseColumn = new StringBuffer();// 查询单对象的字段匹配属性
            TableDO tableDO = null;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                tableDO = list.get(i);
                selectBaseColumn.append("        t." + tableDO.getColumnName() + "    " + tableDO.getAttrName() + ((i == size - 1) ? "" : ",\n"));
                if (!"PRI".equals(tableDO.getPrimaryKey())) {
                    baseColumn.append("            " + tableDO.getColumnName() + ((i == size - 1) ? "" : ",\n"));//
                    // 时间有默认值得就 CURRENT_TIMESTAMP
                    if ("CURRENT_TIMESTAMP".equals(tableDO.getColumnDefault())) {
                        insertValues.append("            CURRENT_TIMESTAMP" + ((i == size - 1) ? "" : ",\n"));// 最后一个去掉逗号
                        if (!"create_time".equals(tableDO.getColumnName()))
                            updateSetColumn.append("            " + tableDO.getColumnName() + " = CURRENT_TIMESTAMP" + ((i == size - 1) ? "" : ",\n"));
                    } else {
                        insertValues.append("            #{" + tableDO.getAttrName() + "}" + ((i == size - 1) ? "" : ",\n"));// 最后一个去掉逗号
                        updateSetColumn.append(getUpdateSetStr(tableDO.getColumnName(), tableDO.getAttrName()) + ((i == size - 1) ? "" : "\n"));
                    }
                }
                resultMapResults.append("        <result property=\"" + tableDO.getAttrName() + "\" column=\"" + tableDO.getAttrName() + "\" />"
                        + ((i == size - 1) ? "" : "\n"));
            }
            contentMap = getCommonMapContent(contentMap, tableModule);
            contentMap.put("baseColumn", baseColumn.toString());
            contentMap.put("insertValues", insertValues.toString());
            contentMap.put("selectBaseColumn", selectBaseColumn.toString());
            contentMap.put("resultMapResults", resultMapResults.toString());
            contentMap.put("updateSetColumn", updateSetColumn.toString());
            contentMap.put("comment", DBUtil.getTableComment(table));
            contentMap.put("primaryKeyColumn", primaryKeyColumn);
            contentMap.put("primaryKeyAttr", primaryKeyAttr);
            contentMap.put("table", table);
            FileUtils.writeFile(filePath, fileName, templateFileName, contentMap);
        }

    }

    /**
     * 生成DAO接口
     * @param tableModule 表名,模块 如：t_camera,testmd
     */
    public static void generateDao(String tableModule) throws Exception {
        if (null != tableModule) {
            Map<String, String> contentMap = new HashMap<String, String>(); // 内容
            String packname = "dao";
            String table = tableModule.split(",")[0]; // 表名
            String module = tableModule.split(",")[1]; // 模块
            String tableNameSubstring = getClassName(table); // 根据表名生成类名 ，去掉表前缀t_,首字母大写
            // 类名
            String className = tableNameSubstring + "Dao";// 类名
            // 包
            String packageStr = module_package + "." + module + "." + packname;
            String filePath = path + module_package.replaceAll("\\.", "\\\\") + "\\" + module + "\\" + packname;// 代码目录
            String fileName = className + ".java";// 代码文件名
            String templateFileName = "dao_template.txt";// 模板文件
            List<TableDO> list = DBUtil.listColumnOfTable(table);
            String[] primaryKeyColumnAttr = getEntityPrimaryKey(list); // 主键key对应实体属性
            String primaryKeyType = null; // 主键类型
            String primaryKeyAttr = null; // 主键对应实体属性
            if (null != primaryKeyColumnAttr) {
                primaryKeyAttr = primaryKeyColumnAttr[1];
                primaryKeyType = primaryKeyColumnAttr[2];
            }
            contentMap = getCommonMapContent(contentMap, tableModule);
            contentMap.put("package", packageStr);// 包字符串
            contentMap.put("className", className);// 类名
            contentMap.put("primaryKeyType", primaryKeyType);
            contentMap.put("primaryKeyAttr", primaryKeyAttr);
            FileUtils.writeFile(filePath, fileName, templateFileName, contentMap);

        }

    }

    /**
     * 生成service接口
     * @param tableModule 表名,模块 如：t_camera,testmd
     */
    public static void generateService(String tableModule) throws Exception {
        if (null != tableModule) {
            Map<String, String> contentMap = new HashMap<String, String>(); // 内容
            String packname = "service";
            String table = tableModule.split(",")[0]; // 表名
            String module = tableModule.split(",")[1]; // 模块
            String tableNameSubstring = getClassName(table); // 根据表名生成类名 ，去掉表前缀t_,首字母大写
            // 类名
            String className = tableNameSubstring + "Service";// 类名
            // 包
            String packageStr = module_package + "." + module + "." + packname;
            String filePath = path + module_package.replaceAll("\\.", "\\\\") + "\\" + module + "\\" + packname;// 代码目录
            String fileName = className + ".java";// 代码文件名
            String templateFileName = "service_template.txt";// 模板文件
            List<TableDO> list = DBUtil.listColumnOfTable(table);
            String[] primaryKeyColumnAttr = getEntityPrimaryKey(list); // 主键key对应实体属性
            String primaryKeyType = null; // 主键类型
            String primaryKeyAttr = null; // 主键对应实体属性
            if (null != primaryKeyColumnAttr) {
                primaryKeyAttr = primaryKeyColumnAttr[1];
                primaryKeyType = primaryKeyColumnAttr[2];
            }
            contentMap = getCommonMapContent(contentMap, tableModule);
            contentMap.put("package", packageStr);// 包字符串
            contentMap.put("className", className);// 类名
            contentMap.put("primaryKeyType", primaryKeyType);
            contentMap.put("primaryKeyAttr", primaryKeyAttr);
            FileUtils.writeFile(filePath, fileName, templateFileName, contentMap);
        }

    }

    /**
     * 生成service实现类
     * @param tableModule 表名,模块 如：t_camera,testmd
     */
    public static void generateServiceImpl(String tableModule) throws Exception {
        if (null != tableModule) {
            Map<String, String> contentMap = new HashMap<String, String>(); // 内容
            String packname = "service";
            String table = tableModule.split(",")[0]; // 表名
            String module = tableModule.split(",")[1]; // 模块
            String tableNameSubstring = getClassName(table); // 根据表名生成类名 ，去掉表前缀t_,首字母大写
            // 类名
            String className = tableNameSubstring + "ServiceImpl";// 类名
            // 包
            String packageStr = module_package + "." + module + "." + packname + ".impl";
            String filePath = path + module_package.replaceAll("\\.", "\\\\") + "\\" + module + "\\" + packname + "\\impl";// 代码目录
            String fileName = className + ".java";// 代码文件名
            String templateFileName = "service_impl_template.txt";// 模板文件

            List<TableDO> list = DBUtil.listColumnOfTable(table);
            String[] primaryKeyColumnAttr = getEntityPrimaryKey(list); // 主键key对应实体属性
            String primaryKeyType = null; // 主键类型
            String primaryKeyAttr = null; // 主键对应实体属性
            if (null != primaryKeyColumnAttr) {
                primaryKeyAttr = primaryKeyColumnAttr[1];
                primaryKeyType = primaryKeyColumnAttr[2];
            }
            contentMap = getCommonMapContent(contentMap, tableModule);
            contentMap.put("package", packageStr);// 包字符串
            contentMap.put("className", className);// 类名
            contentMap.put("primaryKeyType", primaryKeyType);
            contentMap.put("primaryKeyAttr", primaryKeyAttr);
            FileUtils.writeFile(filePath, fileName, templateFileName, contentMap);
        }

    }

    /**
     * 生成 Controller类
     * @param tableModule 表名,模块 如：t_camera,testmd
     */
    public static void generateController(String tableModule) throws Exception {
        if (null != tableModule) {
            Map<String, String> contentMap = new HashMap<String, String>(); // 内容
            String table = tableModule.split(",")[0]; // 表名
            String module = tableModule.split(",")[1]; // 模块
            String tableNameSubstring = getClassName(table); // 根据表名生成类名 ，去掉表前缀t_,首字母大写
            String packageStr = controller_package + "." + module;
            // 类名
            String className = tableNameSubstring + "Controller";// 类名
            String filePath = path + controller_package.replaceAll("\\.", "\\\\") + "\\" + module;// 代码目录
            String fileName = className + ".java";// 代码文件名
            String templateFileName = "controller_template.txt";// 模板文件

            List<TableDO> list = DBUtil.listColumnOfTable(table);
            String[] primaryKeyColumnAttr = getEntityPrimaryKey(list); // 主键key对应实体属性
            String primaryKeyType = null; // 主键类型
            String primaryKeyAttr = null; // 主键对应实体属性
            if (null != primaryKeyColumnAttr) {
                primaryKeyAttr = primaryKeyColumnAttr[1];
                primaryKeyType = primaryKeyColumnAttr[2];
            }
            String url = table;
            if (url.startsWith("t_")) {
                url = url.substring(2);
            }
            url = url.toLowerCase().replaceAll("_", "-");

            contentMap = getCommonMapContent(contentMap, tableModule);
            contentMap.put("package", packageStr);// 包字符串
            contentMap.put("className", className);// 类名
            contentMap.put("primaryKeyType", primaryKeyType);
            contentMap.put("primaryKeyAttr", primaryKeyAttr);
            contentMap.put("primaryKeyAttrUpper", toUpperCaseOne(primaryKeyAttr));
            contentMap.put("url", url);
            FileUtils.writeFile(filePath, fileName, templateFileName, contentMap);
        }

    }

    /**
     * 获取各层代码公共内容
     * @param contentMap 内容map
     * @param tableModule 表,模块
     * @return
     */
    public static Map<String, String> getCommonMapContent(Map<String, String> contentMap, String tableModule) {
        String table = tableModule.split(",")[0]; // 表名
        String module = tableModule.split(",")[1]; // 模块
        String tableNameSubstring = getClassName(table); // 根据表名生成类名 ，去掉表前缀t_,首字母大写

        // 类名
        String classDO = tableNameSubstring + "DO"; // 实体do
        String classQuery = tableNameSubstring + "Query"; // 实体query
        String classVO = tableNameSubstring + "VO"; // 实体vo
        String classDao = tableNameSubstring + "Dao"; // 实体dao
        String classService = tableNameSubstring + "Service"; // 实体service

        // 包
        String packageDOClass = module_package + "." + module + ".entity." + classDO; // do包,import
        String packageVOClass = module_package + "." + module + ".entity." + classVO;// vo包,import
        String packageQueryClass = module_package + "." + module + ".query." + classQuery;// query包,import
        String packageServiceClass = module_package + "." + module + ".service." + classService;// service接口
        String packageDaoClass = module_package + "." + module + ".dao." + classDao;// dao接口

        contentMap.put("author", author);// 类作者
        contentMap.put("comment", DBUtil.getTableComment(table));// 类注释
        contentMap.put("date", current_date);// 当前时间
        contentMap.put("packageVOClass", packageVOClass);
        contentMap.put("packageDOClass", packageDOClass);
        contentMap.put("packageQueryClass", packageQueryClass);
        contentMap.put("packageDaoClass", packageDaoClass);
        contentMap.put("packageServiceClass", packageServiceClass);
        contentMap.put("classVO", classVO);
        contentMap.put("classDO", classDO);
        contentMap.put("classQuery", classQuery);
        contentMap.put("classDao", classDao);
        contentMap.put("classService", classService);

        return contentMap;
    }

    /**
     * 组装update语句中的设置语句 如：<br>
     * <if test="srcId != null"> SRC_ID = #{srcId}, </if>
     * @param column 表字段
     * @param attr 类属性
     * @return
     */
    private static String getUpdateSetStr(String column, String attr) {
        String str = "            <if test=\"" + attr + " != null\">\n";
        str += "                " + column + " = #{" + attr + "},\n";
        str += "            </if>";
        return str;
    }

    /**
     * 获取entity属性模板<br>
     * 时间有默认值CURRENT_TIMESTAMP的就不用创建属性了
     * @param tableDO 类属性详情
     * @param isLast 是否最后一个，最后一个就少一个\n换行，为了符合代码格式化
     * @return
     */
    private static String getEntityAttrTemplate(TableDO tableDO, boolean isLast, boolean required) {

        String attrTemplate = "    /**\n";
        attrTemplate += "     * " + tableDO.getColumnComment() + "\n";
        attrTemplate += "     */\n";
        if (use_swagger) {
            if (required) {
                attrTemplate += "    @ApiModelProperty(value = \"" + tableDO.getColumnComment() + "\""
                        + ("yes".equals(tableDO.getIsNullAble()) ? "" : ", required = true") + ")\n";
            } else {
                attrTemplate += "    @ApiModelProperty(value = \"" + tableDO.getColumnComment() + "\")\n";
            }
        }
        attrTemplate += "    private " + tableDO.getDataType() + " " + tableDO.getAttrName() + ";\n" + (isLast ? "" : "\n");
        return attrTemplate;
    }

    /**
     * 获取entity属性 set方法模板
     * @param tableDO 类属性详情
     * @param isLast 是否最后一个，最后一个就少一个\n换行，为了符合代码格式化
     * @return
     */
    private static String getEntitySetterTemplate(TableDO tableDO, boolean isLast) {
        String attr = tableDO.getAttrName();
        // 首字母大写
        attr = attr.substring(0, 1).toUpperCase() + attr.substring(1);
        String attrTemplate = "    public void set" + attr + "(" + tableDO.getDataType() + " " + tableDO.getAttrName() + ") {\n";
        attrTemplate += "        this." + tableDO.getAttrName() + " = " + tableDO.getAttrName() + ";\n";
        attrTemplate += "    }\n" + (isLast ? "" : "\n");
        return attrTemplate;
    }

    /**
     * 获取entity属性 get方法模板
     * @param tableDO 类属性详情
     * @return
     */
    private static String getEntityGetterTemplate(TableDO tableDO) {
        String attr = tableDO.getAttrName();
        // 首字母大写
        attr = attr.substring(0, 1).toUpperCase() + attr.substring(1);
        String attrTemplate = "    public " + tableDO.getDataType() + " get" + attr + "() {\n";
        if (tableDO.isDatetime()) { // 日期类型 if不为空就截取2017-02-05 02:55:20.0
            attrTemplate += "        if (null != " + tableDO.getAttrName() + ") {\n";
            attrTemplate += "            if (" + tableDO.getAttrName() + ".length() > 19)\n";
            attrTemplate += "                return " + tableDO.getAttrName() + ".substring(0, 19);\n";
            attrTemplate += "        }\n";
            attrTemplate += "        return " + tableDO.getAttrName() + ";\n";
        } else {
            attrTemplate += "        return " + tableDO.getAttrName() + ";\n";
        }
        attrTemplate += "    }\n\n";
        return attrTemplate;
    }

    /**
     * 获取entity toString方法 return 模板
     * @param list 所有属性集合
     * @param className 类名
     * @param isNotDefaultAttr 是否需要生成有默认值得属性（主要是时间字段）true:不需要，false:需要
     * @return
     */
    private static String getEntityToStringTemplate(List<TableDO> list, String className, boolean isNotDefaultAttr) {
        String toString = "\"" + className + " [";
        TableDO table = null;
        for (int i = 0; i < list.size(); i++) {
            table = list.get(i);
            if (isNotDefaultAttr) {
                if (!"CURRENT_TIMESTAMP".equals(table.getColumnDefault()))
                    toString += table.getAttrName() + "=\" + " + table.getAttrName() + " + \"" + (i != list.size() - 1 ? ", " : "");
            } else {
                toString += table.getAttrName() + "=\" + " + table.getAttrName() + " + \"" + (i != list.size() - 1 ? ", " : "");
            }
        }
        toString += "]\"";
        return toString;
    }

    /**
     * 获取对象的主键及属性
     * @param list 所有属性集合
     * @return 如：数组[0]id,[1]id ,[2]type表示：字段,属性,数据类型
     */
    private static String[] getEntityPrimaryKey(List<TableDO> list) {
        TableDO table = null;
        String[] arr = new String[3];
        for (int i = 0; i < list.size(); i++) {
            table = list.get(i);
            if ("PRI".equals(table.getPrimaryKey())) {
                arr[0] = table.getColumnName();
                arr[1] = table.getAttrName();
                arr[2] = table.getDataType();
                return arr;
            }
        }
        return null;
    }

    /**
     * 根据表名生成类名 ，去掉表前缀t_,首字母大写
     * @param table 表名
     * @return
     */
    public static String getClassName(String table) {
        if (table.startsWith("t_")) {
            table = table.substring(2);
        }
        table = table.toLowerCase();
        if (-1 != table.indexOf("_")) {// 字段有下划线 改为驼峰命名规则
            String[] columnArr = table.split("_");
            table = columnArr[0];
            for (int i = 1; i < columnArr.length; i++) {
                table += columnArr[i].substring(0, 1).toUpperCase() + columnArr[i].substring(1);
            }
        }
        return table.substring(0, 1).toUpperCase() + table.substring(1);
    }

    /**
     * 首字母小写
     * @param str
     * @return
     */
    public static String toLowerCaseOne(String str) {
        if (null != str && !"".equals(str)) {
            str = str.substring(0, 1).toLowerCase() + str.substring(1);
        }
        return str;
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String toUpperCaseOne(String str) {
        if (null != str && !"".equals(str)) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

}

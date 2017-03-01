/*
 * 文件名: FileUtils.java
 * 版权: Copyright 2015 中星宝 Tech. Co. Ltd. All Rights Reserved.
 */
package com.xiaolan.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * <一句话功能描述>
 * <p>
 * <功能详细描述>
 * <p>
 * @author wangshiyan
 * @version [<版本号>, 2017年2月23日]
 * @see [<相关类>/<相关方法>]
 * @since [<产品>/<模块版本>]
 */
public class FileUtils {
    /**
     * 读取模板文件内容<br>
     */
    public static String readFileContent(String fileName) {
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(GenerateCode.class.getResourceAsStream("/template/" + fileName)));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString + "\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {}
            }
        }
        return sb.toString();
    }

    /**
     * 根据模板生成对应代码文件
     * @param filePath 代码目录
     * @param fileName 代码文件名
     * @param templateFileName 模板名
     * @param contentMap 替换的内容
     */
    public static void writeFile(String filePath, String fileName, String templateFileName, Map<String, String> contentMap) {
        try {
            File file = new File(filePath);
            // 1、创建代码目录
            if (!file.exists())
                file.mkdirs();
            /// 2、创建代码文件
            file = new File(filePath + "\\" + fileName);
            if (!file.exists())
                file.createNewFile();
            /// 3、读取对应代码模板内容
            String templateContent = readFileContent(templateFileName);
            /// 4、遍历 替换模板对应的内容
            for (String key : contentMap.keySet()) {
                templateContent = templateContent.replaceAll("\\$\\{\\{" + key + "\\}\\}", contentMap.get(key)+"");
            }
            // 内容写入文件
            FileWriter fileWritter = new FileWriter(file, false);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(templateContent);
            bufferWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

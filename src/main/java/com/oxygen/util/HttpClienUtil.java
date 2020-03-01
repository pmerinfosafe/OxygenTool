package com.oxygen.util;


import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * @program: qxygenTool-通用
 * @description: http转换工具
 * @author: pmer_infoSafe
 * @create: 2019-11-14 14:46
 **/
public class HttpClienUtil {
    public static String getEntityContent(HttpEntity entity) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), entity.getContentEncoding().getValue()))) {
            String str = "";
            StringBuilder sb = new StringBuilder();
            while ((str = reader.readLine()) != null) {
                sb.append(str).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();


        String line = null;

        try {

            while ((line = reader.readLine()) != null) {

                sb.append(line + "/n");

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                is.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

        return sb.toString();
    }
}

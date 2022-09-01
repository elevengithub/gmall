package com.atguigu.starter.cache.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

public class Jsons {

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * 将对象转为Json字符串
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String toStr(Object obj) {
        String s = null;
        try {
            s = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 将Json字符串转换为对应的对象
     *
     * @param str   Json字符串
     * @param clazz 需要转换为的类型
     * @param <T>   转换为的类型
     * @return
     */
    public static <T> T toObj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        T t = null;
        try {
            t = mapper.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T toObj(String str, TypeReference<T> tr) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        T t = null;
        try {

            t = mapper.readValue(str, tr);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

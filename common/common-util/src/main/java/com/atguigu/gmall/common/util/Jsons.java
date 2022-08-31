package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

public class Jsons {

    /**
     * 将对象转为Json字符串
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String toStr(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        String s = null;
        try {
            s = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static<T> T toObj(String str,Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
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
}

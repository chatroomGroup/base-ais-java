package com.cai.ais.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertUtil {

    private static ObjectMapper JSON;

    static {
        JSON = new ObjectMapper();
    }

    public static ObjectMapper getJSON() {
        return JSON;
    }
}

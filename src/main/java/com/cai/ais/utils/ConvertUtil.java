package com.cai.ais.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ConvertUtil {

    private static ObjectMapper JSON;

    static {
        JSON = new ObjectMapper();
    }

    public static ObjectMapper getJSON() {
        return JSON;
    }

    public static byte[] objToBytes(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        bytes = bos.toByteArray ();
        oos.close();
        bos.close();
        return bytes;
    }
}

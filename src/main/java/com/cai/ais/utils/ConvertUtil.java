package com.cai.ais.utils;

import com.cai.ais.config.AisMessage;
import com.cai.ais.core.exception.AisException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

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

    public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
        ObjectInputStream ois = new ObjectInputStream (bis);
        obj = ois.readObject();
        ois.close();
        bis.close();
        return obj;
    }

    public static AisMessage bytesToAisMessage(byte[] body) throws AisException, JsonProcessingException {
        AisMessage dlxMessage = new AisMessage();
        try{
            dlxMessage = (AisMessage) bytesToObject(body);
        }catch (Throwable t){
            if (t instanceof IOException)
                dlxMessage = getJSON().readValue(new String(body), AisMessage.class);
            else
                t.printStackTrace();
        }
        if (dlxMessage == null){
            throw new AisException("转换失败");
        }
        return dlxMessage;
    }
}

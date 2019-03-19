package com.dada.marsframework.network;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by laidayuan on 2018/6/6.
 */

public class HttpClientUtils {

    public static String post(String urlStr) {
        int nTry = 3;
        do {
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                is = conn.getInputStream();

                return InputStreamTOString(is, "utf-8");

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                    if (conn != null)conn.disconnect();
                } catch (IOException e) {
                }
            }
        } while (--nTry > 0);


        return null;
    }


    /**
     * InputStream转换成Byte
     * 注意:流关闭需要自行处理
     * @param in
     * @return byte
     * @throws Exception
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException{

        int BUFFER_SIZE = 4096;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;

        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        byte[] outByte = outStream.toByteArray();
        outStream.close();

        return outByte;
    }

    /**
     * InputStream转换成String
     * 注意:流关闭需要自行处理
     * @param in
     * @param encoding 编码
     * @return String
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in, String encoding) throws IOException{

        return new String(InputStreamTOByte(in), encoding);

    }

}

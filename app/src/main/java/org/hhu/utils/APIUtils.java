package org.hhu.utils;


import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;

public class APIUtils {

    public static final String PORT = "55555";


    /**
     * 方向控制
     */
    public static final String stopCode = "ff 01 04 00 00 33 33 33 33 5a 88";
    public static final String forwardCode = "ff 01 04 00 00 00 00 64 64 6b bb";
    public static final String backwardCode = "ff 01 04 00 00 01 01 64 64 38 ee";
    public static final String leftCode = "ff 01 04 00 00 00 00 40 64 09 dd";
    public static final String rightCode = "ff 01 04 00 00 00 00 64 40 9e 44";

    public static void stop(String ip) {
        String url = "http://" + ip + ":" + PORT;
        doCmdPost(url, stopCode);
    }

    public static void forward(String ip) {
        String url = "http://" + ip + ":" + PORT;
        doCmdPost(url, forwardCode);
    }

    public static void backward(String ip) {
        String url = "http://" + ip + ":" + PORT;
        doCmdPost(url, backwardCode);
    }

    public static void left(String ip) {
        String url = "http://" + ip + ":" + PORT;
        doCmdPost(url, leftCode);
    }

    public static void right(String ip) {
        String url = "http://" + ip + ":" + PORT;
        doCmdPost(url, rightCode);
    }


    /**
     * 大灯
     */
    public static final String lightOnCode = "ff 01 01 00 0f 00 64 98";
    public static final String lightOffCode = "ff 01 01 00 10 00 29 8b";

    public static void lightOn(String ip) {
        String url = "http://" + ip + ":" + PORT;
        doCmdPost(url, lightOnCode);
    }

    public static void lightOff(String ip) {
        String url = "http://" + ip + ":" + PORT;
        doCmdPost(url, lightOffCode);
    }



    /**
     * cmd Post
     */
    private static void doCmdPost(String url, String cmd) {
        try {
            OkHttpUtils
                .post()
                .url(url)
                .addParams("cmd", cmd)
                .build()
                .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

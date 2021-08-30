package com.lzq.web.utils;

import java.util.Random;

public class VerifyCodeUtils {


    public static String getCode() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        String baseNumLetter = "123456789abcdefghijklmnopqrstuvwsyz";

        for(int i = 0; i < 4; ++i) {
            int dot = random.nextInt(baseNumLetter.length());
            buffer.append(baseNumLetter.charAt(dot));
        }

        return buffer.toString();
    }
}

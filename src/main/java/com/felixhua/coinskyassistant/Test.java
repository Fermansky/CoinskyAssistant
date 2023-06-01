package com.felixhua.coinskyassistant;

import com.felixhua.coinskyassistant.constants.Constant;

public class Test {
    public static void main(String[] args) {
        String hash = "1e99e835933b61ab98d610e799b71ec2";
        System.out.println(Constant.COINSKY_IMG_URL + hash.charAt(0) + "/" + hash.charAt(1) + "/" + hash.charAt(2) +
                "/" + hash.charAt(3) + "/" + hash.substring(4, 32) + ".jpg");
    }
}

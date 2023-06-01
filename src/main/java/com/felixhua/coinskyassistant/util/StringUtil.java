package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.constants.Constant;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static List<String> divideImgHash(String imgHash) {
        List<String> dividedImgHashes = new ArrayList<>();

        int length = imgHash.length();
        int startIndex = 0;
        int endIndex = 32;

        while (startIndex < length) {
            if (endIndex > length) {
                endIndex = length;
            }

            String substring = imgHash.substring(startIndex, endIndex);
            dividedImgHashes.add(convertHashToUrl(substring));

            startIndex += 32;
            endIndex += 32;
        }

        return dividedImgHashes;
    }

    private static String convertHashToUrl(String imgHash) {
        return Constant.COINSKY_IMG_URL + imgHash.charAt(0) + "/" + imgHash.charAt(1) + "/" + imgHash.charAt(2) +
                "/" + imgHash.charAt(3) + "/" + imgHash.substring(4, 32) + ".jpg";
    }
}

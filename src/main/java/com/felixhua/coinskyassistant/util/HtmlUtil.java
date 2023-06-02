package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.entity.JQueryResult;
import com.google.gson.Gson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HtmlUtil {
    static Gson gson = new Gson();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static JQueryResult parseJson(String json) {
        return gson.fromJson(json, JQueryResult.class);
    }

    public static String getFormattedTime(long epochSecond) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault());
        return localDateTime.format(formatter);
    }

}

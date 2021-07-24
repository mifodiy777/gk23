package ru.kircoop.gk23.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.Charset;

public class ResponseUtils {
    /**
     * Преобразование коллекции элементов в JSON массив с именем aaData для таблиц
     */
    public static ResponseEntity<String> convertListToJson(Iterable collection) {
        GsonBuilder gson = new GsonBuilder();
        gson.serializeNulls();
        JsonObject obj = new JsonObject();
        obj.add("aaData", gson.create().toJsonTree(collection));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("utf-8")));
        return new ResponseEntity<String>(obj.toString(), responseHeaders, HttpStatus.OK);
    }

}

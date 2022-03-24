package com.jd.mlaas.ump.api.domain.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Create by gouyaoqing on 2018/10/22
 */
@Slf4j
public class HttpUtils {
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build();

    public static JSONObject post(String url, String token, JSONObject requestJson) {
        if (requestJson == null) {
            requestJson = new JSONObject();
        }

        //token
        if (!requestJson.containsKey("token") && !Strings.isNullOrEmpty(token)) {
            requestJson.put("token", token);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), requestJson.toJSONString());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            return JSON.parseObject(response.body().string());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String post(String url, Map<String, String> header, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(null, body);
        return doRequest(new Request.Builder().url(url).post(requestBody), header);
    }

    /**
     * @param url url must been encode by {@code URLEncoder}
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * @param url url must been encode by {@code URLEncoder}
     * @return
     * @throws IOException
     */
    public static String get(String url, Map<String, String> headers) throws IOException {
        return doRequest(new Request.Builder().url(url).get(), headers);
    }


    public static String get(String url, Map<String, String> headers, Map<String, String> param) throws IOException, URISyntaxException {

        if (param != null && param.size() != 0) {
            URIBuilder uriBuilder = new URIBuilder(url);
            param.forEach(uriBuilder::addParameter);
            url = uriBuilder.toString();
        }

        return get(url, headers);
    }

    public static String put(String url, Map<String, String> headers, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(null, body);
        return doRequest(new Request.Builder().url(url).put(requestBody), headers);
    }

    public static String delete(String url, Map<String, String> headers) throws IOException {
        return doRequest(new Request.Builder().url(url).delete(), headers);
    }


    public static String delete(String url, Map<String, String> headers, Map<String, String> param) throws IOException, URISyntaxException {

        if (param != null && param.size() != 0) {
            URIBuilder uriBuilder = new URIBuilder(url);
            param.forEach(uriBuilder::addParameter);
            url = uriBuilder.toString();
        }

        return delete(url, headers);
    }

    private static String doRequest(Request.Builder requestBuilder, Map<String, String> headers) throws IOException {
        if (headers != null && !headers.isEmpty()) {
            requestBuilder = requestBuilder.headers(Headers.of(headers));
        }
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.code() != HttpStatus.OK.value()) {
            throw new IOException("http request for url {" + request.url().toString() + "} not return " + response.code() + " != 200 | 0.message {" + response.message() + "}");
        }
        ResponseBody body = response.body();
        return body != null ? body.string() : null;
    }

    public static Response postNew(String url, Map<String, String> headersMap, JSONObject requestJson) {
        if (requestJson == null) {
            requestJson = new JSONObject();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestJson.toJSONString());
        Request.Builder requestBuilder = new Request.Builder();
        Headers.Builder headersBuilder = new Headers.Builder();
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            headersBuilder.add(entry.getKey(), entry.getValue());
        }
        requestBuilder.headers(headersBuilder.build());
        Request request = requestBuilder.url(url).post(requestBody).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static Response get0(String url) {
        Request request = new Request.Builder().get().url(url).build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest servletRequest, String name) {
        javax.servlet.http.Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null && cookies.length > 0) {

            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName.equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

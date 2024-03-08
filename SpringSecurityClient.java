package org.example;

import okhttp3.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpringSecurityClient {

    public static class MyCookieJar implements CookieJar {
        private List<Cookie> cookies = new ArrayList<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            this.cookies.addAll(cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> validCookies = new ArrayList<>();
            for (Cookie cookie : cookies) {
                if (cookie.matches(url)) {
                    validCookies.add(cookie);
                }
            }
            return validCookies;
        }

        private void setCookies(List<Cookie> cookies){
            this.cookies = cookies;
        }
    }
    public static class Pair {
        private String key;
        private String value;

        public Pair(String key, String value){
            this.key = key;
            this.value = value;
        }

        public String getKey(){
            return key;
        }

        public String getValue(){
            return value;
        }
    }

    private final MyCookieJar cookieJar;
    private final OkHttpClient client_main;

    private SpringSecurityClient(String url, String username, String password){
        try {
            cookieJar = new MyCookieJar();
            client_main = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();

            HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
            queryUrlBuilder
                    .addQueryParameter("username", username)
                    .addQueryParameter("password", password);

            Request request = new Request.Builder()
                    .url(queryUrlBuilder.build())
                    .post(RequestBody.create(null, new byte[0]))
                    .build();
            Response response = client_main.newCall(request).execute();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private SpringSecurityClient(List<Cookie> cookies){
        cookieJar = new MyCookieJar();
        cookieJar.setCookies(cookies);
        client_main = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
    }

    //
    // help methods
    //

    public static SpringSecurityClient createFromCookiesData(byte[] serializedCookies){
        SpringSecurityClient client = null;

        try{
            List<SerializableCookie> serializableCookies = deserializeCookies(serializedCookies);
            List<Cookie> cookies = new ArrayList<>();

            for(SerializableCookie serializableCookie : serializableCookies){
                cookies.add(serializableCookie.toCookie());
            }

            client = new SpringSecurityClient(cookies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return client;
    }

    public static SpringSecurityClient login(String url, String username, String password) {
        try {
            MyCookieJar m_cookieJar = new MyCookieJar();
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(m_cookieJar)
                    .build();
            HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
            queryUrlBuilder
                    .addQueryParameter("username", username)
                    .addQueryParameter("password", password);

            Request request = new Request.Builder()
                    .url(queryUrlBuilder.build())
                    .post(RequestBody.create(null, new byte[0]))
                    .build();
            Response response = client.newCall(request).execute();

            return new SpringSecurityClient(m_cookieJar.cookies);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static SpringSecurityClient login_ns(String url, String username, String password) throws IOException, AuthenticationException {
        MyCookieJar m_cookieJar = new MyCookieJar();
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(m_cookieJar)
                .build();
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder
                .addQueryParameter("username", username)
                .addQueryParameter("password", password);

        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .post(RequestBody.create(null, new byte[0]))
                .build();
        Response response = client.newCall(request).execute();

        if(response.code() != 403){
            throw new AuthenticationException("Can`t authenticate.");
        }

        return new SpringSecurityClient(m_cookieJar.cookies);
    }

    public List<Cookie> getCookies(){
        return cookieJar.cookies;
    }

    public byte[] serializeCookies() {
        List<SerializableCookie> cookies = new ArrayList<>();

        for(Cookie cookie : cookieJar.cookies) {
            cookies.add(new SerializableCookie(cookie));
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(cookies);
            return byteArrayOutputStream.toByteArray();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static List<SerializableCookie> deserializeCookies(byte[] serializedCookies) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedCookies);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (List<SerializableCookie>) objectInputStream.readObject();
        }
    }


    //
    // work methods
    //

    //
    // async
    //

    // default cookies
    public CompletableFuture<String> get_async(String url){
        return CompletableFuture.supplyAsync(() -> get(url));
    }

    public CompletableFuture<String> get_async(String url, List<Pair> params){
        return CompletableFuture.supplyAsync(() -> get(url, params));
    }

    public CompletableFuture<String> post_async(String url, String jsonData){
        return CompletableFuture.supplyAsync(() -> post(url, jsonData));
    }

    // no cookies
    public static CompletableFuture<String> get_nc_async(String url){
        return CompletableFuture.supplyAsync(() -> get_nc(url));
    }

    public static CompletableFuture<String> get_nc_async(String url, List<Pair> params){
        return CompletableFuture.supplyAsync(() -> get_nc(url, params));
    }

    public static CompletableFuture<String> post_nc_async(String url, String jsonData){
        return CompletableFuture.supplyAsync(() -> post_nc(url, jsonData));
    }

    //
    // not async
    //

    // default cookies
    public String get(String url){
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client_main.newCall(request).execute();

            return response.body().string();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String get(String url, List<Pair> params){
        try {
            HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();

            for(Pair param: params){
                queryUrlBuilder.addQueryParameter(param.key, param.value);
            }

            Request request = new Request.Builder()
                    .url(queryUrlBuilder.build())
                    .build();
            Response response = client_main.newCall(request).execute();

            return response.body().string();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String post(String url, String jsonData){
        try{
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSON, jsonData))
                    .build();
            Response response = client_main.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // no cookies
    public static String get_nc(String url){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();

            return response.body().string();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String get_nc(String url, List<Pair> params){
        try {
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
            for(Pair param: params){
                queryUrlBuilder.addQueryParameter(param.key, param.value);
            }

            Request request = new Request.Builder()
                    .url(queryUrlBuilder.build())
                    .build();
            Response response = client.newCall(request).execute();

            return response.body().string();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String post_nc(String url, String jsonData){
        try{
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSON, jsonData))
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

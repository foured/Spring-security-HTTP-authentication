package org.example;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpringSecurityClient implements Serializable {
    private BasicCookieStore cookies;

    public SpringSecurityClient(String url, String username, String password) {
        try {
            cookies = new BasicCookieStore();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            HttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookies).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            HttpResponse response = httpclient.execute(httpPost);
        }
        catch (IOException e){
            System.out.println("Error with logging in at address '" + url + "'.");
        }
    }

    public BasicCookieStore getCookies(){
        return cookies;
    }

    //
    // async
    //

    // default cookies

    public CompletableFuture<String> get_async(String url){
        return CompletableFuture.supplyAsync(() -> {
            return get(url);
        });
    }

    public CompletableFuture<String> get_async(String url, List<NameValuePair> params){
        return CompletableFuture.supplyAsync(() -> {
            return get(url, params);
        });
    }

    public CompletableFuture<String> post_async(String url,  String json){
        return CompletableFuture.supplyAsync(() -> {
            return post(url, json);
        });
    }

    public CompletableFuture<String> post_async(String url,  List<NameValuePair> params){
        return CompletableFuture.supplyAsync(() -> {
            return post(url, params);
        });
    }

    // no cookies

    public static CompletableFuture<String> get_nc_async(String url){
        return CompletableFuture.supplyAsync(() -> {
            return get_nc(url);
        });
    }

    public static CompletableFuture<String> get_nc_async(String url, List<NameValuePair> params){
        return CompletableFuture.supplyAsync(() -> {
            return get_nc(url, params);
        });
    }

    public static CompletableFuture<String> post_nc_async(String url, String json){
        return CompletableFuture.supplyAsync(() -> {
            return post_nc(url, json);
        });
    }

    public static CompletableFuture<String> post_nc_async(String url, List<NameValuePair> params){
        return CompletableFuture.supplyAsync(() -> {
            return post_nc(url, params);
        });
    }

    // custom cookies

    public static CompletableFuture<String> get_async(String url, BasicCookieStore cookieStore){
        return CompletableFuture.supplyAsync(() -> {
            return get(url, cookieStore);
        });
    }

    public static CompletableFuture<String> get_async(String url, List<NameValuePair> params, BasicCookieStore cookieStore){
        return CompletableFuture.supplyAsync(() -> {
            return get(url, params, cookieStore);
        });
    }

    public static CompletableFuture<String> post_async(String url, String json, BasicCookieStore cookieStore){
        return CompletableFuture.supplyAsync(() -> {
            return post(url, json, cookieStore);
        });
    }
    
    public static CompletableFuture<String> post_async(String url, List<NameValuePair> params, BasicCookieStore cookieStore){
        return CompletableFuture.supplyAsync(() -> {
            return post(url, params, cookieStore);
        });
    }

    //
    // not async
    //

    // default cookies

    public String get(String url) {
        return get(url, cookies);
    }

    public String get(String url, List<NameValuePair> params){
        return get(url, params, cookies);
    }

    public String post(String url, String json){
        return post(url, json, cookies);
    }

    public String post(String url, List<NameValuePair> params) {
        return post(url, params, cookies);
    }

    // no cookies

    public static String get_nc(String url){
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        return r2s(get_base(httpGet, httpClient));
    }

    public static String get_nc(String url, List<NameValuePair> params){
        HttpClient httpClient = HttpClients.createDefault();
        String paramsString = null;
        try {
            paramsString = EntityUtils.toString(new UrlEncodedFormEntity(params));
        } catch (IOException e) {
            System.out.println("Error to convert params: " + params.toString());
            return null;
        }
        HttpGet httpGet = new HttpGet(url + "?" + paramsString);
        return r2s(get_base(httpGet, httpClient));
    }

    public static String post_nc(String url, String json){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);
        return r2s(post_base(httpPost, httpClient));
    }

    public static String post_nc(String url,  List<NameValuePair> params){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error to convert params: " + params.toString());
            return null;
        }
        return r2s(post_base(httpPost, httpClient));
    }

    // custom cookies

    public static String get(String url, BasicCookieStore cookieStore){
        HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpGet httpGet = new HttpGet(url);
        return r2s(get_base(httpGet, httpClient));
    }

    public static String get(String url, List<NameValuePair> params, BasicCookieStore cookieStore){
        HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        String paramsString = null;
        try {
            paramsString = EntityUtils.toString(new UrlEncodedFormEntity(params));
        } catch (IOException e) {
            System.out.println("Error to convert params: " + params.toString());
            return null;
        }
        HttpGet httpGet = new HttpGet(url + "?" + paramsString);
        return r2s(get_base(httpGet, httpClient));
    }

    public static String post(String url, String json, BasicCookieStore cookieStore){
        HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);
        return r2s(post_base(httpPost, httpClient));
    }

    public static String post(String url, List<NameValuePair> params, BasicCookieStore cookieStore) {
        HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error to convert params: " + params.toString());
            return null;
        }
        return r2s(post_base(httpPost, httpClient));
    }

    //
    // private
    //

    private static HttpResponse get_base(HttpGet httpGet, HttpClient httpClient){
        String url = httpGet.getURI().toString();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                System.out.println("HTTP status is not ok in 'get' at address '" + url + ": " + statusLine.getStatusCode());
                processStatusCode(response);
            }

            return response;
        }
        catch (IOException e){
            System.out.println("Error with 'get' at address '" + url + ": ");
            System.out.println(e);
            return null;
        }
    }

    private static HttpResponse post_base(HttpPost httpPost,  HttpClient httpClient){
        String url = httpPost.getURI().toString();
        try {
            HttpResponse response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                System.out.println("HTTP status is not ok in 'post' at address '" + url + ": " + statusLine.getStatusCode());
                processStatusCode(response);
            }

            return response;
        }
        catch (IOException e){
            System.out.println("Error with 'post' at address '" + url + ": ");
            System.out.println(e);
            return null;
        }
    }

    private static void processStatusCode(HttpResponse response){
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY){
            Header locationHeader = response.getFirstHeader("Location");
            if (locationHeader != null) {
                String redirectUrl = locationHeader.getValue();
                System.out.println("Redirect URL: " + redirectUrl);
            } else {
                System.out.println("Redirect URL not found in the response.");
            }
        }
    }

    private static String r2s(HttpResponse response){
        try {
            return EntityUtils.toString(response.getEntity());
        }
        catch (IOException e){
            System.out.println("Error to get string from response: ");
            System.out.println(e);
            return null;
        }
    }
}

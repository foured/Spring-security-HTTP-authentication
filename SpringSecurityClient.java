package org.example;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
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

    public CompletableFuture<String> get_async(String url){
        return CompletableFuture.supplyAsync(() -> {
            return get(url);
        });
    }

    public CompletableFuture<String> post_async(String url,  List<NameValuePair> params){
        return CompletableFuture.supplyAsync(() -> {
            return post(url, params);
        });
    }

    public static CompletableFuture<String> get_nc_async(String url){
        return CompletableFuture.supplyAsync(() -> {
            return get_nc(url);
        });
    }

    public static CompletableFuture<String> post_nc_async(String url, List<NameValuePair> params){
        return CompletableFuture.supplyAsync(() -> {
            return post_nc(url, params);
        });
    }

    public static CompletableFuture<String> get_async(String url, BasicCookieStore cookieStore){
        return CompletableFuture.supplyAsync(() -> {
            return get(url, cookieStore);
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

    public String get(String url) {
        return get(url, cookies);
    }

    public String post(String url, List<NameValuePair> params) {
        return post(url, params, cookies);
    }

    public static String get_nc(String url){
        return r2s(get_base(url, HttpClients.createDefault()));
    }

    public static String post_nc(String url,  List<NameValuePair> params){
        return r2s(post_base(url, params, HttpClients.createDefault()));
    }

    public static String get(String url, BasicCookieStore cookieStore)  {
        HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return r2s(get_base(url, httpClient));
    }

    public static String post(String url, List<NameValuePair> params, BasicCookieStore cookieStore) {
        HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return r2s(post_base(url, params, httpClient));
    }

    //
    // private
    //

    private static HttpResponse get_base(String url, HttpClient httpClient){
        try {
            HttpGet httpGet = new HttpGet(url);

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

    private static HttpResponse post_base(String url,  HttpClient httpClient){
        try {
            HttpPost httpPost = new HttpPost(url);

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

    private static HttpResponse post_base(String url, List<NameValuePair> params, HttpClient httpClient){
        try {
            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
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

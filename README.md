# Spring-security-HTTP-authentication
The simpliest spring security HTTP authentication, no csfr.

# Описание
При создании клиента программа получает куки файл с кодом для авторизации, который дальше добавляется во все запросы.
Объект SpringSecurityClient сериализуемый. 

# Подготовка
Сначала надо выкчить csrf:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.requestMatchers("/activities/**", "/operations/**").authenticated())
            .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
            .build();
}
```
# Использование

Создание объекта
```java
// (link, username, password)
SpringSecurityClient client = new SpringSecurityClient("http://localhost:8080/login", "admin", "admin");
```

Get с подставление cookies без асинхронности
```java
String link = "some link"
client.get(link);
```

Post с подставление cookies без асинхронности
```java
String link = "some link"

List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

client.post(link, params);
```

Get без подставление cookies без асинхронности
```java
String link = "some link"
client.get_nc(link);
```

Post без подставление cookies без асинхронности
```java
String link = "some link"

List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

client.post_nc(link, params);
```

Get с подставление своих cookies без асинхронности
```java
BasicCookieStore cookieStore = new BasicCookieStore();
// создание куки 

String link = "some link"
client.get(link, cookieStore)

```

Post с подставление своих cookies без асинхронности
```java
BasicCookieStore cookieStore = new BasicCookieStore();
// создание куки 

String link = "some link"

List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

client.post_nc(link, params);
```

Get с подставление cookies, асинхронный
```java
String link = "some link"

CompletableFuture<String> result = client.get_async(link);

result.thenAccept(res -> {
    System.out.println(res);
});
```

Post с подставление cookies, асинхронный
```java
String link = "some link"

List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

CompletableFuture<String> result = client.post_async(link, params);

result.thenAccept(res -> {
    System.out.println(res);
});
```

Get без подставление cookies, асинхронный
```java
String link = "some link"

CompletableFuture<String> result = client.get_nc_async(link);

result.thenAccept(res -> {
    System.out.println(res);
});
```

Post без подставление cookies, асинхронный
```java
String link = "some link"

List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

CompletableFuture<String> result = client.post_nc_async(link, params);

result.thenAccept(res -> {
    System.out.println(res);
});
```

Get с подставление своих cookies, асинхронный
```java
BasicCookieStore cookieStore = new BasicCookieStore();
// создание куки 

String link = "some link"
CompletableFuture<String> result = client.get_async(link, cookieStore);

result.thenAccept(res -> {
    System.out.println(res);
});

```

Post с подставление своих cookies, асинхронный
```java
BasicCookieStore cookieStore = new BasicCookieStore();
// создание куки 

String link = "some link"
List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

CompletableFuture<String> result = client.post_async(link, params, cookieStore);

result.thenAccept(res -> {
    System.out.println(res);
});
```

# Spring-security-HTTP-authentication
The simpliest spring security HTTP authentication, no csfr.

# Описание
При создании клиента программа получает куки файл с кодом для авторизации, который дальше добавляется во все запросы.
Объект SpringSecurityClient сериализуемый. 

# Подготовка
Сначала надо выкчить csrf в проекте сервера:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.requestMatchers("/activities/**", "/operations/**").authenticated())
            .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
            .build();
}
```
Добавьте зависимость в проект клиента
Maven
```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.13</version>
</dependency>
```
Gradle
```
implementation 'org.apache.httpcomponents:httpclient:4.5'
```

# Использование

Создание объекта
```java
// (link, username, password)
SpringSecurityClient client = new SpringSecurityClient("http://localhost:8080/login", "admin", "admin");
```

Get с подставлением cookies без асинхронности
```java
String link = "some link"
client.get(link);
```

Post с подставлением cookies без асинхронности
```java
String link = "some link"

List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

client.post(link, params);
```

Get без подставлением cookies без асинхронности
```java
String link = "some link"
client.get_nc(link);
```

Post без подставлением cookies без асинхронности
```java
String link = "some link"

List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

client.post_nc(link, params);
```

Get с подставлением своих cookies без асинхронности
```java
BasicCookieStore cookieStore = new BasicCookieStore();
// создание куки 

String link = "some link"
client.get(link, cookieStore)

```

Post с подставлением своих cookies без асинхронности
```java
BasicCookieStore cookieStore = new BasicCookieStore();
// создание куки 

String link = "some link"

List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("value1", "value1"));
params.add(new BasicNameValuePair("value2", "value2"));

client.post_nc(link, params);
```

Get с подставлением cookies, асинхронный
```java
String link = "some link"

CompletableFuture<String> result = client.get_async(link);

result.thenAccept(res -> {
    System.out.println(res);
});
```

Post с подставлением cookies, асинхронный
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

Get без подставлением cookies, асинхронный
```java
String link = "some link"

CompletableFuture<String> result = client.get_nc_async(link);

result.thenAccept(res -> {
    System.out.println(res);
});
```

Post без подставлением cookies, асинхронный
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

Get с подставлением своих cookies, асинхронный
```java
BasicCookieStore cookieStore = new BasicCookieStore();
// создание куки 

String link = "some link"
CompletableFuture<String> result = client.get_async(link, cookieStore);

result.thenAccept(res -> {
    System.out.println(res);
});

```

Post с подставлением своих cookies, асинхронный
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

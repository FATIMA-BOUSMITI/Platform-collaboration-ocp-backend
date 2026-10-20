package com.ocp.organisation_service.security;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import java.util.*;
@Component
public class AuthAccessClient {
 private final RestClient client;
 public record Access(UUID userId,String email,String role,List<String> permissions){}
 public AuthAccessClient(@Value("${auth.service.url:http://localhost:8080}") String url){var factory=new org.springframework.http.client.SimpleClientHttpRequestFactory();factory.setConnectTimeout(java.time.Duration.ofSeconds(3));factory.setReadTimeout(java.time.Duration.ofSeconds(5));client=RestClient.builder().baseUrl(url).requestFactory(factory).build();}
 public Access resolve(String bearer){return client.get().uri("/api/auth/access-context").header("Authorization",bearer).retrieve().body(Access.class);}
}

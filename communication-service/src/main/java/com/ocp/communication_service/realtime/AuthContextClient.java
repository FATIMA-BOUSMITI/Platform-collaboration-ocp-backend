package com.ocp.communication_service.realtime;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import java.util.*;
@Component
public class AuthContextClient {
 private final RestClient client;
 public record Context(UUID userId,String email,String role,List<String> permissions){}
 public AuthContextClient(@Value("${auth.service.url:http://localhost:8080}") String url){var f=new org.springframework.http.client.SimpleClientHttpRequestFactory();f.setConnectTimeout(java.time.Duration.ofSeconds(3));f.setReadTimeout(java.time.Duration.ofSeconds(5));client=RestClient.builder().baseUrl(url).requestFactory(f).build();}
 public Context resolve(String bearer){return client.get().uri("/api/auth/access-context").header("Authorization",bearer).retrieve().body(Context.class);}
}

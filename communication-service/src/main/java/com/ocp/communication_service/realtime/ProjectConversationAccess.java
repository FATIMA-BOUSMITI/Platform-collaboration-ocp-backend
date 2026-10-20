package com.ocp.communication_service.realtime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Component public class ProjectConversationAccess {
 private final RestClient client;
 public ProjectConversationAccess(@Value("${project.service.url:http://localhost:8083}") String url){var factory=new org.springframework.http.client.SimpleClientHttpRequestFactory();factory.setConnectTimeout(java.time.Duration.ofSeconds(3));factory.setReadTimeout(java.time.Duration.ofSeconds(5));client=RestClient.builder().baseUrl(url).requestFactory(factory).build();}
 public void requireRead(UUID projectId){
  var context=(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
  String bearer=context==null?null:context.getRequest().getHeader("Authorization");
  if(bearer==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  try{client.get().uri("/api/projects/{id}",projectId).header("Authorization",bearer).retrieve().toBodilessEntity();}
  catch(org.springframework.web.client.RestClientResponseException e){throw new ResponseStatusException(e.getStatusCode(),"Projet inaccessible");}
  catch(org.springframework.web.client.ResourceAccessException e){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Service projets indisponible");}
 }
}

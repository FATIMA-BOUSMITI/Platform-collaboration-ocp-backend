package com.ocp.project_management_service.service;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@Component public class CollaborationDirectory {
 private final RestClient client;
 public record Person(UUID id,String firstName,String lastName,String email){}
 public CollaborationDirectory(@Value("${user.service.url:http://localhost:8082}")String url){var f=new org.springframework.http.client.SimpleClientHttpRequestFactory();f.setConnectTimeout(java.time.Duration.ofSeconds(3));f.setReadTimeout(java.time.Duration.ofSeconds(5));client=RestClient.builder().baseUrl(url).requestFactory(f).build();}
 public void requireUser(UUID user){var context=(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();String bearer=context==null?null:context.getRequest().getHeader("Authorization");if(bearer==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  try{Person[] people=client.get().uri("/api/users/directory").header("Authorization",bearer).retrieve().body(Person[].class);if(people==null||Arrays.stream(people).noneMatch(p->user.equals(p.id())))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Utilisateur introuvable");}
  catch(org.springframework.web.client.RestClientResponseException e){throw new ResponseStatusException(e.getStatusCode(),"Annuaire inaccessible");}catch(org.springframework.web.client.ResourceAccessException e){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Annuaire indisponible");}
 }
}

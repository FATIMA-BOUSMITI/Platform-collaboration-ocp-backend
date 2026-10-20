package com.example.api_gatway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration public class CollaborationRoutes {
 @Bean RouteLocator collaborationRoutes(RouteLocatorBuilder builder,
   @Value("${collaboration.communication-http-uri:http://localhost:8089}") String http,
   @Value("${collaboration.communication-ws-uri:ws://localhost:8089}") String ws){
  return builder.routes().route("collaboration-websocket",r->r.path("/ws","/ws/**").uri(ws))
   .route("collaboration-notifications",r->r.path("/api/notifications","/api/notifications/**").uri(http)).build();
 }
}

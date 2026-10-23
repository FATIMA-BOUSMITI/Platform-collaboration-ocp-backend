package com.ocp.communication_service.realtime;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.config.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.config.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Configuration @EnableWebSocketMessageBroker @EnableScheduling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 private final javax.crypto.SecretKey key;private final AuthContextClient auth;private final SocketSessions sessions;private final String[] origins;
 public WebSocketConfig(@Value("${jwt.secret}")String secret,AuthContextClient auth,SocketSessions sessions,@Value("${collaboration.allowed-origins:http://localhost:5173,http://127.0.0.1:5173,http://localhost:5174,http://127.0.0.1:5174}")String[] origins){key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.auth=auth;this.sessions=sessions;this.origins=origins;}
 @Override public void registerStompEndpoints(StompEndpointRegistry registry){registry.addEndpoint("/ws").setAllowedOriginPatterns(origins);}
 @Override public void configureMessageBroker(MessageBrokerRegistry registry){registry.enableSimpleBroker("/queue");registry.setUserDestinationPrefix("/user");}
 @Override public void configureWebSocketTransport(WebSocketTransportRegistration registry){registry.setMessageSizeLimit(16384).setSendBufferSizeLimit(131072).setSendTimeLimit(15000);registry.addDecoratorFactory(handler->new WebSocketHandlerDecorator(handler){@Override public void afterConnectionEstablished(WebSocketSession session)throws Exception{sessions.open(session);super.afterConnectionEstablished(session);}@Override public void afterConnectionClosed(WebSocketSession session,CloseStatus status)throws Exception{sessions.close(session.getId());super.afterConnectionClosed(session,status);}});}
 @Override public void configureClientInboundChannel(ChannelRegistration registry){registry.interceptors(new ChannelInterceptor(){@Override public Message<?> preSend(Message<?> message,MessageChannel channel){var a=MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);if(a==null)return message;var command=a.getCommand();if(command==StompCommand.CONNECT){String bearer=a.getFirstNativeHeader("Authorization");if(bearer==null||!bearer.startsWith("Bearer "))throw new AccessDeniedException("Authentification requise");var claims=Jwts.parser().verifyWith(key).build().parseSignedClaims(bearer.substring(7)).getPayload();if(!"ACCESS".equals(claims.get("type",String.class)))throw new AccessDeniedException("Token invalide");var context=auth.resolve(bearer);if(context==null||!context.userId().toString().equals(claims.get("userId",String.class)))throw new AccessDeniedException("Session invalide");a.setUser(new UsernamePasswordAuthenticationToken(context.userId().toString(),null,List.of()));sessions.authenticate(a.getSessionId(),bearer,claims.getExpiration().getTime());}else if(command==StompCommand.SUBSCRIBE){if(a.getUser()==null||!Set.of("/user/queue/messages","/user/queue/notifications").contains(a.getDestination()))throw new AccessDeniedException("Abonnement interdit");}else if(command==StompCommand.SEND)throw new AccessDeniedException("Les messages doivent être enregistrés par l’API");return message;}});}
}

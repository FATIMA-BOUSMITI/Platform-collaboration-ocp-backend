package com.ocp.communication_service.realtime;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.*;
import org.springframework.web.client.RestClientResponseException;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class SocketSessions {
 private final AuthContextClient auth;private final ConcurrentHashMap<String,WebSocketSession> sockets=new ConcurrentHashMap<>();private final ConcurrentHashMap<String,Credentials> credentials=new ConcurrentHashMap<>();
 public SocketSessions(AuthContextClient auth){this.auth=auth;}
 public record Credentials(String bearer,long expiresAt){}
 public void open(WebSocketSession session){sockets.put(session.getId(),session);}public void close(String id){sockets.remove(id);credentials.remove(id);}public void authenticate(String id,String bearer,long expiry){credentials.put(id,new Credentials(bearer,expiry));}
 @Scheduled(fixedDelay=30000)public void revalidate(){credentials.forEach((id,c)->{boolean invalid=c.expiresAt()<=System.currentTimeMillis();if(!invalid)try{auth.resolve(c.bearer());}catch(RestClientResponseException e){invalid=e.getStatusCode().value()==401||e.getStatusCode().value()==403;}catch(RuntimeException unavailable){/* Keep valid JWT connections during a temporary auth outage. */}if(invalid){WebSocketSession s=sockets.get(id);if(s!=null)try{s.close(CloseStatus.POLICY_VIOLATION);}catch(java.io.IOException ignored){}close(id);}});}
}

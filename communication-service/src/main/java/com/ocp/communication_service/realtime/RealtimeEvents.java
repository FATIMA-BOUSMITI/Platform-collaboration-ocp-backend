package com.ocp.communication_service.realtime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import java.util.*;
@Component @RequiredArgsConstructor
public class RealtimeEvents {
 private final ApplicationEventPublisher publisher;private final SimpMessagingTemplate socket;
 public record Signal(List<UUID> users,String queue,Object data){}
 public void send(List<UUID> users,String queue,Object data){publisher.publishEvent(new Signal(users.stream().distinct().toList(),queue,data));}
 @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT,fallbackExecution=true)
 public void committed(Signal event){for(UUID user:event.users())try{socket.convertAndSendToUser(user.toString(),"/queue/"+event.queue(),event.data());}catch(RuntimeException failure){org.slf4j.LoggerFactory.getLogger(RealtimeEvents.class).warn("Realtime delivery deferred to HTTP resynchronization for user {}",user);}}
}

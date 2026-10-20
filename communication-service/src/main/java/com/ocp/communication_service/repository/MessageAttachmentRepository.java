package com.ocp.communication_service.repository;
import com.ocp.communication_service.entity.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment,UUID>{List<MessageAttachment> findByMessageId(UUID messageId);}

package com.ocp.communication_service.repository;
import com.ocp.communication_service.entity.CollaborationNotification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface NotificationRepository extends JpaRepository<CollaborationNotification,UUID>{
 Optional<CollaborationNotification> findByExternalId(UUID externalId);
 @Query("select n from CollaborationNotification n where n.userId=:user and n.deletedAt is null and (:unread=false or n.read=false) and (:allTypes=true or n.type in :types) order by n.createdAt desc,n.id desc")
 List<CollaborationNotification> inbox(@Param("user") UUID user,@Param("unread")boolean unread,@Param("allTypes")boolean allTypes,@Param("types")Collection<String> types,Pageable page);
 long countByUserIdAndReadFalseAndDeletedAtIsNull(UUID userId);
 @Modifying @Query("update CollaborationNotification n set n.read=true,n.version=n.version+1 where n.userId=:user and n.deletedAt is null")void readAll(@Param("user")UUID user);
}

package com.ocp.communication_service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
@Entity @Table(name="notification_preferences") @Data
public class NotificationPreference {@Id private UUID userId;private boolean messages=true;private boolean tasks=true;private boolean deadlines=true;private boolean mentions=true;private boolean invitations=true;}

package com.ocp.auth_service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
@Entity @Table(name="admin_preferences") @Data
public class AdminPreference { @Id private UUID id; private String language="fr"; private String timezone="UTC"; }

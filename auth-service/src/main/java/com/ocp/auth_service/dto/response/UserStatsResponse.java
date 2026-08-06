package com.ocp.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserStatsResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long lockedAccounts;
    private Long totalFailedAttempts;
    private Long usersWithFailedAttempts;
}

package com.example.callbotsms.repository;

import com.im.sso.model.Log;
import com.im.sso.model.enums.ActionStatus;
import com.im.sso.model.enums.ActionType;
import com.im.sso.model.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, UUID> {
    @Query(value = "SELECT l FROM Log l " +
            "WHERE l.tenantId = :tenantId " +
            "AND (:entityType IS NULL OR l.entityType = :entityType) " +
            "AND (:actionStatus IS NULL OR l.actionStatus = :actionStatus) " +
            "AND (:actionType IS NULL OR l.actionType = :actionType) " +
            "AND (cast(:entityId as org.hibernate.type.PostgresUUIDType) IS NULL OR l.entityId = :entityId) " +
            "AND (cast(:userId as org.hibernate.type.PostgresUUIDType) IS NULL OR l.createdBy = :userId) " +
            "AND (l.createdAt BETWEEN COALESCE(:createdAtStartTs, l.createdAt) AND COALESCE(:createdAtEndTs, l.createdAt)) " +
            "AND (convertToNonSigned(l.actionData, :isSearchMatchCase) LIKE CONCAT('%',:searchText,'%') " +
            "OR convertToNonSigned(l.actionFailureDetails, :isSearchMatchCase) LIKE CONCAT('%',:searchText,'%')) "
    )
    Page<Log> findLogs(
            String searchText,
            Boolean isSearchMatchCase,
            EntityType entityType,
            UUID entityId,
            UUID userId,
            ActionStatus actionStatus,
            ActionType actionType,
            LocalDateTime createdAtStartTs,
            LocalDateTime createdAtEndTs,
            UUID tenantId,
            Pageable pageable
    );
}

package com.example.callbotsms.repository;


import com.im.sso.model.AppUser;
import com.im.sso.model.enums.AuthorityType;
import com.im.sso.model.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    @Query(value = "SELECT u FROM AppUser u " +
            "JOIN u.userCredential c " +
            "WHERE u.authority=:authority " +
            "AND u.tenantId = :id " +
            "AND (u.createdAt BETWEEN COALESCE(:startTs, u.createdAt) AND COALESCE(:endTs, u.createdAt)) " +
            "AND (cast(:contactId as org.hibernate.type.PostgresUUIDType) IS NULL OR u.contact.id = :contactId) " +
            "AND (convertToNonSigned(u.email, :isSearchMatchCase) LIKE CONCAT('%',:searchText,'%') " +
            "OR convertToNonSigned(u.phone, :isSearchMatchCase) LIKE CONCAT('%',:searchText,'%') " +
            "OR convertToNonSigned(CONCAT(u.firstName, ' ', u.lastName), :isSearchMatchCase) LIKE CONCAT('%',:searchText,'%')) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:isEnabled IS NULL OR c.enabled = :isEnabled)"
    )
    Page<AppUser> findUsersByTenant(
            @Param("searchText") String searchText,
            @Param("isSearchMatchCase") Boolean isSearchMatchCase,
            @Param("role") RoleType role,
            @Param("contactId") UUID contactId,
            @Param("authority") AuthorityType authority,
            @Param("id") UUID id,
            @Param("startTs") LocalDateTime startTs,
            @Param("endTs") LocalDateTime endTs,
            @Param("isEnabled") Boolean isEnabled,
            Pageable pageable
    );

    @Query(value = "SELECT u FROM AppUser u " +
            "WHERE u.authority=:authority " +
            "AND (convertToNonSigned(u.email, :isSearchMatchCase) LIKE CONCAT('%',:searchText,'%') " +
            "OR convertToNonSigned(u.phone, :isSearchMatchCase) LIKE CONCAT('%',:searchText,'%') " +
            "OR convertToNonSigned(CONCAT(u.firstName, ' ', u.lastName), :isSearchMatchCase) LIKE CONCAT('%',:searchText,'%')) " +
            "AND (u.createdAt BETWEEN COALESCE(:startTs, u.createdAt) AND COALESCE(:endTs, u.createdAt)) " +
            "AND (:isEnabled IS NULL OR u.userCredential.enabled = :isEnabled)"
    )
    Page<AppUser> findUsersBySysAdmin(
            @Param("searchText") String searchText,
            @Param("isSearchMatchCase") Boolean isSearchMatchCase,
            @Param("authority") AuthorityType authority,
            @Param("startTs") LocalDateTime startTs,
            @Param("endTs") LocalDateTime endTs,
            @Param("isEnabled") Boolean isEnabled,
            Pageable pageable
    );

    @Query(value = "SELECT u FROM AppUser u WHERE u.email=:email")
    AppUser findByEmail(String email);

    AppUser findByIdAndTenantId(UUID id, UUID tenantId);

    List<AppUser> findByAuthority(AuthorityType type);

    Optional<AppUser> findByIdAndAuthority(UUID id, AuthorityType authority);

}

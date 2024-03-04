package com.example.callbotsms.repository;

import com.im.sso.model.WhiteLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WhiteLabelRepository extends JpaRepository<WhiteLabel, UUID> {

    Optional<WhiteLabel> findByTenantId(UUID tenantId);
}

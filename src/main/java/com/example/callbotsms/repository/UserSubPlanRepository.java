package com.example.callbotsms.repository;

import com.im.sso.model.AppUser;
import com.im.sso.model.UserSubPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSubPlanRepository extends JpaRepository<UserSubPlan, UUID> {
    Optional<UserSubPlan> findByUserId(UUID userId);

    Optional<UserSubPlan> findByUser(AppUser user);
}

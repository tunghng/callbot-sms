package com.example.callbotsms.repository;

import com.im.sso.model.AccountPlan;
import com.im.sso.model.enums.AccountPlanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountPlanRepository extends JpaRepository<AccountPlan, UUID> {

    Optional<AccountPlan> findByName(AccountPlanType name);

}

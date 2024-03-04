package com.example.callbotsms.config;

import com.im.sso.model.AccountPlan;
import com.im.sso.model.UserSubPlan;
import com.im.sso.model.enums.AccountPlanType;
import com.im.sso.model.enums.AuthorityType;
import com.im.sso.repository.AccountPlanRepository;
import com.im.sso.repository.AppUserRepository;
import com.im.sso.repository.UserSubPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LoadDataConfig {

    private final AppUserRepository userRepository;

    private final UserSubPlanRepository userSubPlanRepository;

    private final AccountPlanRepository accountPlanRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            userRepository.findByAuthority(AuthorityType.TENANT_ADMIN).forEach(user -> {
                UserSubPlan existUserPlan = userSubPlanRepository.findByUserId(user.getId()).orElse(null);
                if (existUserPlan == null) {
                    AccountPlan basic = accountPlanRepository.findByName(
                            AccountPlanType.BASIC
                    ).orElse(null);
                    UserSubPlan userSubPlan = new UserSubPlan();
                    userSubPlan.setUser(user);
                    userSubPlan.setAccountPlan(basic);
                    userSubPlanRepository.saveAndFlush(userSubPlan);
                }
            });
        };
    }
}

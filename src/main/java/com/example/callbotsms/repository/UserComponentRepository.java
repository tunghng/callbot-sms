package com.example.callbotsms.repository;

import com.im.sso.model.AppComponent;
import com.im.sso.model.UserComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserComponentRepository extends JpaRepository<UserComponent, UUID> {
    List<UserComponent> findAllByUserId(UUID userId);

    UserComponent findByAppComponentIdAndUserId(UUID appComponentId, UUID userId);

    List<UserComponent> findByAppComponent(AppComponent appComponentId);

}

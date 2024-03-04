package com.example.callbotsms.repository;

import com.im.sso.model.AppComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppComponentRepository extends JpaRepository<AppComponent, UUID> {

    Page<AppComponent> findAll(Pageable pageable);

    AppComponent findByName(String name);

    @Query(value = "SELECT a FROM AppComponent a WHERE :url LIKE '%' || a.urlBase || '%'")
    AppComponent findByUrlBase(@Param("url") String url);
}

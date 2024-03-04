package com.example.callbotsms.repository;

import com.im.sso.model.DataKv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataKvRepository extends JpaRepository<DataKv, String> {
}

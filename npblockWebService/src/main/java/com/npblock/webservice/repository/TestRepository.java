package com.npblock.webservice.repository;

import com.npblock.webservice.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Integer> {
}

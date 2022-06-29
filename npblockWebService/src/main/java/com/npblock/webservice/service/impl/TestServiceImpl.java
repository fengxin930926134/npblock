package com.npblock.webservice.service.impl;

import com.npblock.webservice.entity.Test;
import com.npblock.webservice.repository.TestRepository;
import com.npblock.webservice.service.TestService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final @NonNull TestRepository testRepository;

    @Override
    public List<Test> findAll() {
        return testRepository.findAll();
    }
}

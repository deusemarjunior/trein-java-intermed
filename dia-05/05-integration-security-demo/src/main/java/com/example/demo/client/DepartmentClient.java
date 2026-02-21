package com.example.demo.client;

import com.example.demo.config.FeignConfig;
import com.example.demo.dto.DepartmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "department-service",
        url = "${department.api.url}",
        configuration = FeignConfig.class
)
public interface DepartmentClient {

    @GetMapping("/api/departments/{id}")
    DepartmentResponse findById(@PathVariable Long id);

    @GetMapping("/api/departments")
    List<DepartmentResponse> findAll();
}

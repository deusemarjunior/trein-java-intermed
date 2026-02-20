package com.example.cleanarchitecture.domain.port.in;

import com.example.cleanarchitecture.adapter.in.web.dto.ProductRequest;
import com.example.cleanarchitecture.adapter.in.web.dto.ProductResponse;

import java.util.List;

/**
 * Port IN — interface que define os casos de uso do domínio.
 * O Controller (adapter in) chama esta interface.
 */
public interface ProductUseCase {

    ProductResponse create(ProductRequest request);

    ProductResponse findById(Long id);

    List<ProductResponse> findAll();

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}

package com.playdata.orderingservice.client;

import com.playdata.orderingservice.common.dto.CommonResDto;
import com.playdata.orderingservice.ordering.dto.ProductResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "http://product-service.default.svc.cluster.local:8083")
public interface ProductServiceClient {

    @GetMapping("/{prodId}/prod")
    CommonResDto<ProductResDto> findById(@PathVariable Long prodId);

    @PostMapping("/updateQuantity")
    ResponseEntity<?> updateQuantity(@RequestBody ProductResDto productResDto);

    @PostMapping("/products/name")
    CommonResDto<List<ProductResDto>> getProducts(@RequestBody List<Long> productIds);

}
















package com.playdata.productservice.product.repository;

import com.playdata.productservice.product.dto.ProductSearchDto;
import com.playdata.productservice.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByIdIn(List<Long> ids);

//    @Query("SELECT p FROM Product p WHERE p.category LIKE '%?%'")
//    void findByCategoryPaging(ProductSearchDto dto, Pageable pageable);
//    @Query("SELECT p FROM Product p WHERE p.name LIKE '%?%'")
//    void findByProdNamePaging(ProductSearchDto dto, Pageable pageable);

}

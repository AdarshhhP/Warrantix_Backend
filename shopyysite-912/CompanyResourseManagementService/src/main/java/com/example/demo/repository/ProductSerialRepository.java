package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ProductSerial;

@Repository
public interface ProductSerialRepository extends JpaRepository<ProductSerial, Long> {

    @Query("SELECT ps FROM ProductSerial ps WHERE ps.Model_No = :modelNo AND ps.serialNo IN :serialNos")
    List<ProductSerial> findByModelNoAndSerialNos(@Param("modelNo") String modelNo, @Param("serialNos") List<String> serialNos);
}

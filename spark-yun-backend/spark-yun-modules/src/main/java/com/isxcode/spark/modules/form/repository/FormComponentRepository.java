package com.isxcode.spark.modules.form.repository;

import com.isxcode.spark.modules.form.entity.FormComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormComponentRepository extends JpaRepository<FormComponentEntity, String> {

    List<FormComponentEntity> findAllByFormId(String formId);

    void deleteByFormId(String formId);
}

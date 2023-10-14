package com.isxcode.star.modules.file.repository;

import com.isxcode.star.modules.file.entity.FileEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/** 只负责数据库查询逻辑. */
@Repository
@CacheConfig(cacheNames = {"sy_file"})
public interface FileRepository extends JpaRepository<FileEntity, String>, JpaSpecificationExecutor<FileEntity> {
  default List<FileEntity> findByFileTypeAndFileName(String fileType, String fileName) {
    return findAll((Specification<FileEntity>) (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (fileType != null) {
        predicates.add(criteriaBuilder.like(root.get("fileType"), "%" + fileType + "%"));
      }

      if (fileName != null) {
        predicates.add(criteriaBuilder.like(root.get("fileName"), "%" + fileName + "%"));
      }

      if (fileType == null && fileName == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
  }

}

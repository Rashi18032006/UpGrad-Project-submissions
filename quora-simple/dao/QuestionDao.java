package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionDao extends JpaRepository<QuestionEntity, Long> {
    Optional<QuestionEntity> findByUuid(String uuid);
    List<QuestionEntity> findAllByOrderByCreatedAtDesc();
    List<QuestionEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
}

package com.upgrad.quora.service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "answers")
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String ans;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getUuid() { return uuid; }
    public String getAns() { return ans; }
    public void setAns(String ans) { this.ans = ans; }
    public QuestionEntity getQuestion() { return question; }
    public void setQuestion(QuestionEntity question) { this.question = question; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public Instant getCreatedAt() { return createdAt; }
}

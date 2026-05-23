package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/question")
@Tag(name = "Question", description = "Question CRUD APIs")
public class QuestionController {

    private final QuestionBusinessService questionBusinessService;

    public QuestionController(QuestionBusinessService questionBusinessService) {
        this.questionBusinessService = questionBusinessService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new question")
    public ResponseEntity<?> createQuestion(@RequestBody Map<String, String> body,
                                            @RequestHeader("Authorization") String authHeader) {
        QuestionEntity q = questionBusinessService.createQuestion(body.get("content"), extractToken(authHeader));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", q.getUuid(), "status", "QUESTION CREATED"));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all questions")
    public ResponseEntity<?> getAllQuestions(@RequestHeader("Authorization") String authHeader) {
        List<QuestionEntity> questions = questionBusinessService.getAllQuestions(extractToken(authHeader));
        List<Map<String, Object>> response = questions.stream()
                .map(q -> Map.of(
                        "id", (Object) q.getUuid(),
                        "content", q.getContent(),
                        "author", q.getUser().getUsername(),
                        "createdAt", q.getCreatedAt().toString()
                )).toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/{questionId}")
    @Operation(summary = "Edit an existing question (owner only)")
    public ResponseEntity<?> editQuestion(@PathVariable String questionId,
                                          @RequestBody Map<String, String> body,
                                          @RequestHeader("Authorization") String authHeader) {
        QuestionEntity q = questionBusinessService.editQuestion(questionId, body.get("content"), extractToken(authHeader));
        return ResponseEntity.ok(Map.of("id", q.getUuid(), "status", "QUESTION EDITED"));
    }

    @DeleteMapping("/delete/{questionId}")
    @Operation(summary = "Delete a question (owner or admin)")
    public ResponseEntity<?> deleteQuestion(@PathVariable String questionId,
                                            @RequestHeader("Authorization") String authHeader) {
        questionBusinessService.deleteQuestion(questionId, extractToken(authHeader));
        return ResponseEntity.ok(Map.of("id", questionId, "status", "QUESTION DELETED"));
    }

    @GetMapping("/all/{userId}")
    @Operation(summary = "Get all questions by a specific user")
    public ResponseEntity<?> getQuestionsByUser(@PathVariable String userId,
                                                @RequestHeader("Authorization") String authHeader) {
        List<QuestionEntity> questions = questionBusinessService.getQuestionsByUser(userId, extractToken(authHeader));
        List<Map<String, Object>> response = questions.stream()
                .map(q -> Map.of(
                        "id", (Object) q.getUuid(),
                        "content", q.getContent(),
                        "createdAt", q.getCreatedAt().toString()
                )).toList();
        return ResponseEntity.ok(response);
    }

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) return header.substring(7);
        return header;
    }
}

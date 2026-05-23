package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/question")
@Tag(name = "Answer", description = "Answer CRUD APIs")
public class AnswerController {

    private final AnswerBusinessService answerBusinessService;

    public AnswerController(AnswerBusinessService answerBusinessService) {
        this.answerBusinessService = answerBusinessService;
    }

    @PostMapping("/{questionId}/answer/create")
    @Operation(summary = "Post an answer to a question")
    public ResponseEntity<?> createAnswer(@PathVariable String questionId,
                                          @RequestBody Map<String, String> body,
                                          @RequestHeader("Authorization") String authHeader) {
        AnswerEntity answer = answerBusinessService.createAnswer(questionId, body.get("answer"), extractToken(authHeader));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", answer.getUuid(), "status", "ANSWER CREATED"));
    }

    @PutMapping("/answer/edit/{answerId}")
    @Operation(summary = "Edit an answer (owner only)")
    public ResponseEntity<?> editAnswer(@PathVariable String answerId,
                                        @RequestBody Map<String, String> body,
                                        @RequestHeader("Authorization") String authHeader) {
        AnswerEntity answer = answerBusinessService.editAnswer(answerId, body.get("answer"), extractToken(authHeader));
        return ResponseEntity.ok(Map.of("id", answer.getUuid(), "status", "ANSWER EDITED"));
    }

    @DeleteMapping("/answer/delete/{answerId}")
    @Operation(summary = "Delete an answer (owner or admin)")
    public ResponseEntity<?> deleteAnswer(@PathVariable String answerId,
                                          @RequestHeader("Authorization") String authHeader) {
        answerBusinessService.deleteAnswer(answerId, extractToken(authHeader));
        return ResponseEntity.ok(Map.of("id", answerId, "status", "ANSWER DELETED"));
    }

    @GetMapping("/{questionId}/answers/all")
    @Operation(summary = "Get all answers for a question")
    public ResponseEntity<?> getAllAnswers(@PathVariable String questionId,
                                          @RequestHeader("Authorization") String authHeader) {
        List<AnswerEntity> answers = answerBusinessService.getAnswersForQuestion(questionId, extractToken(authHeader));
        List<Map<String, Object>> response = answers.stream()
                .map(a -> Map.of(
                        "id", (Object) a.getUuid(),
                        "answer", a.getAns(),
                        "author", a.getUser().getUsername(),
                        "createdAt", a.getCreatedAt().toString()
                )).toList();
        return ResponseEntity.ok(response);
    }

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) return header.substring(7);
        return header;
    }
}

package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.dao.UserDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    private final QuestionDao questionDao;
    private final UserDao userDao;
    private final UserBusinessService userBusinessService;

    public QuestionBusinessService(QuestionDao questionDao, UserDao userDao,
                                   UserBusinessService userBusinessService) {
        this.questionDao = questionDao;
        this.userDao = userDao;
        this.userBusinessService = userBusinessService;
    }

    @Transactional
    public QuestionEntity createQuestion(String content, String token) {
        UserEntity user = userBusinessService.getUserFromToken(token);
        QuestionEntity q = new QuestionEntity();
        q.setContent(content);
        q.setUser(user);
        return questionDao.save(q);
    }

    public List<QuestionEntity> getAllQuestions(String token) {
        userBusinessService.getUserFromToken(token);
        return questionDao.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public QuestionEntity editQuestion(String questionUuid, String newContent, String token) {
        UserEntity user = userBusinessService.getUserFromToken(token);
        QuestionEntity q = questionDao.findByUuid(questionUuid)
                .orElseThrow(() -> new InvalidQuestionException("QUES-001",
                        "Entered question uuid does not exist"));
        if (!q.getUser().getUuid().equals(user.getUuid())) {
            throw new AuthorizationFailedException("ATHR-003",
                    "Only the question owner can edit the question");
        }
        q.setContent(newContent);
        return questionDao.save(q);
    }

    @Transactional
    public void deleteQuestion(String questionUuid, String token) {
        UserEntity user = userBusinessService.getUserFromToken(token);
        QuestionEntity q = questionDao.findByUuid(questionUuid)
                .orElseThrow(() -> new InvalidQuestionException("QUES-001",
                        "Entered question uuid does not exist"));
        boolean isOwner = q.getUser().getUuid().equals(user.getUuid());
        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new AuthorizationFailedException("ATHR-003",
                    "Only the question owner or admin can delete the question");
        }
        questionDao.delete(q);
    }

    public List<QuestionEntity> getQuestionsByUser(String userUuid, String token) {
        userBusinessService.getUserFromToken(token);
        UserEntity targetUser = userDao.findByUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException("USR-001",
                        "User with entered uuid does not exist"));
        return questionDao.findByUserOrderByCreatedAtDesc(targetUser);
    }
}

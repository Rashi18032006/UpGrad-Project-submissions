package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerBusinessService {

    private final AnswerDao answerDao;
    private final QuestionDao questionDao;
    private final UserBusinessService userBusinessService;

    public AnswerBusinessService(AnswerDao answerDao, QuestionDao questionDao,
                                 UserBusinessService userBusinessService) {
        this.answerDao = answerDao;
        this.questionDao = questionDao;
        this.userBusinessService = userBusinessService;
    }

    @Transactional
    public AnswerEntity createAnswer(String questionUuid, String ansText, String token) {
        UserEntity user = userBusinessService.getUserFromToken(token);
        QuestionEntity question = questionDao.findByUuid(questionUuid)
                .orElseThrow(() -> new InvalidQuestionException("QUES-001",
                        "The question entered is invalid"));
        AnswerEntity answer = new AnswerEntity();
        answer.setAns(ansText);
        answer.setQuestion(question);
        answer.setUser(user);
        return answerDao.save(answer);
    }

    @Transactional
    public AnswerEntity editAnswer(String answerUuid, String newAns, String token) {
        UserEntity user = userBusinessService.getUserFromToken(token);
        AnswerEntity answer = answerDao.findByUuid(answerUuid)
                .orElseThrow(() -> new AnswerNotFoundException("ANS-001",
                        "Entered answer uuid does not exist"));
        if (!answer.getUser().getUuid().equals(user.getUuid())) {
            throw new AuthorizationFailedException("ATHR-003",
                    "Only the answer owner can edit the answer");
        }
        answer.setAns(newAns);
        return answerDao.save(answer);
    }

    @Transactional
    public void deleteAnswer(String answerUuid, String token) {
        UserEntity user = userBusinessService.getUserFromToken(token);
        AnswerEntity answer = answerDao.findByUuid(answerUuid)
                .orElseThrow(() -> new AnswerNotFoundException("ANS-001",
                        "Entered answer uuid does not exist"));
        boolean isOwner = answer.getUser().getUuid().equals(user.getUuid());
        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new AuthorizationFailedException("ATHR-003",
                    "Only the answer owner or admin can delete the answer");
        }
        answerDao.delete(answer);
    }

    public List<AnswerEntity> getAnswersForQuestion(String questionUuid, String token) {
        userBusinessService.getUserFromToken(token);
        QuestionEntity question = questionDao.findByUuid(questionUuid)
                .orElseThrow(() -> new InvalidQuestionException("QUES-001",
                        "The question with entered uuid whose details are to be seen does not exist"));
        return answerDao.findByQuestionOrderByCreatedAtAsc(question);
    }
}

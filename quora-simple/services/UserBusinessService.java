package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.JwtTokenProvider;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserBusinessService(UserDao userDao, PasswordEncoder passwordEncoder,
                               JwtTokenProvider jwtTokenProvider) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UserEntity signup(UserEntity user) {
        if (userDao.existsByUsername(user.getUsername())) {
            throw new SignUpRestrictedException("SGR-001",
                    "Try any other Username, this Username has already been taken");
        }
        if (userDao.existsByEmail(user.getEmail())) {
            throw new SignUpRestrictedException("SGR-002",
                    "This user has already been registered, try with any other emailId");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.save(user);
    }

    public String signin(String username, String password) {
        UserEntity user = userDao.findByUsername(username)
                .orElseThrow(() -> new AuthorizationFailedException("ATH-001",
                        "This username does not exist"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthorizationFailedException("ATH-002", "Password failed");
        }
        return jwtTokenProvider.generateToken(username);
    }

    @Transactional
    public void signout(String token) {
        // Stateless JWT: client discards token.
        // Extend with a token blacklist or refresh-token table if needed.
        getUserFromToken(token); // validates token & user existence
    }

    public UserEntity getUserProfile(String uuid, String token) {
        getUserFromToken(token); // any authenticated user may view profiles
        return userDao.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException("USR-001",
                        "User with entered uuid does not exist"));
    }

    @Transactional
    public void deleteUser(String uuid, String token) {
        UserEntity requester = getUserFromToken(token);
        if (!"admin".equalsIgnoreCase(requester.getRole())) {
            throw new AuthorizationFailedException("ATHR-003",
                    "Unauthorized Access, Entered user is not an admin");
        }
        UserEntity target = userDao.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException("USR-001",
                        "User with entered uuid does not exist"));
        userDao.delete(target);
    }

    // --- helpers ---

    public UserEntity getUserFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new AuthorizationFailedException("ATH-003", "Invalid / expired token");
        }
        String username = jwtTokenProvider.getUsernameFromToken(token);
        return userDao.findByUsername(username)
                .orElseThrow(() -> new AuthorizationFailedException("ATH-001",
                        "User has signed out"));
    }
}

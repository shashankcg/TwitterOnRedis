package com.intuiter.auth.service;

import com.intuiter.auth.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}

package com.lemon.oauth2.repository;

import com.lemon.oauth2.entity.User;
import com.lemon.oauth2.repository.base.BaseRepository;

public interface UserRepository extends BaseRepository<User> {

    User findUserByAccount(String account);
}

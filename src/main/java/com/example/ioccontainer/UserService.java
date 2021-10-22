package com.example.ioccontainer;

import org.springframework.beans.factory.annotation.Autowired;

public class UserService {

    @Autowired
    private UserDao userDao;

    public User getUserById(Integer id) {
        return userDao.getUserById(id);
    }
}

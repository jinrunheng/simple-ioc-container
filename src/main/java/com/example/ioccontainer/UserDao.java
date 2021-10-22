package com.example.ioccontainer;

public class UserDao {

    public User getUserById(Integer id) {
        // sql : select * from user where id = " + id
        return new User(1, "Jack");
    }
}

package com.example.ioccontainer;

public class OrderDao {

    public void createOrder(User user) {
        System.out.println(user.getName() + " create an order");
    }
}

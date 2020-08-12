package com.psh.controller;


import com.psh.entity.User;
import com.psh.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void get() {
        User user = userRepository.findUserById( "parksohyun" );
        System.out.println(user.getUserSeq());
    }

    @Test
    public void getAll() {
        List<User> userList = userRepository.findAll();
        userList.forEach( user -> System.out.println(user) );
    }

    @Test
    public void add() {
        List<User> list = new ArrayList<>(  );
        User user = User.builder()
                .id("parksohyun")
                .password("1234")
                .name("박소현")
                .build();
        list.add( user );


        User user1 = User.builder()
                .id("hong")
                .password("1234")
                .name("홍길동")
                .build();
        list.add( user1 );

        User user2 = User.builder()
                .id("kim")
                .password("1234")
                .name("김철수")
                .build();
        list.add( user2 );

        userRepository.saveAll( list );
    }
}
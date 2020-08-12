package com.psh.controller;

import com.psh.entity.User;
import com.psh.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping( "/api" )
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger( UserController.class );

    @Autowired
    private UserService userService;

    @RequestMapping( method = RequestMethod.POST, value = "/login" )
    public ResponseEntity<User> login( @RequestBody final User user, HttpServletResponse response ) {

        logger.info( "method: POST, api: /login, id: {}", user.getId() );

        return userService.get( user, response );
    }

    @RequestMapping( method = RequestMethod.GET, value = "/user/all" )
    public ResponseEntity<List<User>> all() {

        logger.info( "method: GET, api: /user/all" );

        return userService.getAll();

    }
}

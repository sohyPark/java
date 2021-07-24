package com.psh.service;

import com.psh.entity.User;
import com.psh.repository.UserRepository;
import com.psh.util.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<User> get( final User user, final HttpServletResponse response ) {

        final String id = user.getId();
        if ( StringUtils.isEmpty( id) ) {
            logger.error( "Id is empty - id: {}", id );
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "아이디를 입력햊세요.");
        }

        final String password = user.getPassword();
        if ( StringUtils.isEmpty( password) ) {
            logger.error( "Password is empty - id: {}", id );
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "비밀번를 입력햊세요.");
        }

        User getUser;
        try {
            getUser = userRepository.findUserByIdAndPassword( id, password );
        } catch ( Exception e ) {
            logger.error("User findUserByIdAndPassword error : {}, id: {}", e.getMessage(), id);
            return RestResponse.fail( HttpStatus.INTERNAL_SERVER_ERROR, e.getCause().getMessage() );
        }

        if ( getUser == null ) {
            logger.error( "User not found - id: {}", id );
            return RestResponse.fail( HttpStatus.NO_CONTENT, "사용자를 찾을 수 없습니다.");
        }

        String token = null;
        try {
            token = tokenService.createToken( user );
        } catch ( Exception e ) {
            System.out.println();
            logger.error( "create token - user: {}, error: {}", user, e.getMessage() );
            return RestResponse.fail( HttpStatus.INTERNAL_SERVER_ERROR, e.getCause().getMessage() );
        }

        if ( token == null ) {
            logger.error( "created token is null - id: {}", id );
            return RestResponse.fail( HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성이 실패하였습니다." );
        }

        response.setHeader( "jwt-auth-token", token );

        return RestResponse.success( getUser );
    }

    public ResponseEntity getAll() {

        List<User> userList;
        try {
            userList = userRepository.findAll();
        } catch ( Exception e ) {
            logger.error("User findAll error : {}", e.getMessage());
            return RestResponse.fail( HttpStatus.INTERNAL_SERVER_ERROR, e.getCause().getMessage());
        }

        return RestResponse.success( userList );
    }


}

package com.psh.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestResponse {

    public static ResponseEntity fail( final HttpStatus status, final String errorMessage ) {
        return toResponseEntity( status, errorMessage );
    }

    public static ResponseEntity success( final Object obj ) {
        return toResponseEntity( HttpStatus.OK, obj );
    }

    public static ResponseEntity success() {
        return toResponseEntity( HttpStatus.OK, null );
    }

    public static ResponseEntity toResponseEntity( HttpStatus status, Object obj ) {
        if ( null == obj ) {
            return new ResponseEntity( status );
        }

        //return ResponseEntity.status( status ).body( obj );
        return new ResponseEntity<>(obj, status);
    }
}

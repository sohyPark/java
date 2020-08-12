package com.psh.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Getter
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private long userSeq;

    private String id;

    private String password;

    private String name;


    @Builder
    public User( final long userSeq, final String id, final String password, final String name ) {
        this.userSeq = userSeq;
        this.id = id;
        this.password = password;
        this.name = name;
    }
}

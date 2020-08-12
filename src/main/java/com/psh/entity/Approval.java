package com.psh.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Approval extends BasetimeEntity {

    @Id
    @GeneratedValue
    private long approvalSeq;

    private long docSeq;

    private int lineSeq;

    private int status;

    private String comments;

    @Column
    private long userSeq;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name="userSeq", insertable=false, updatable=false)
    private User user;

    @Builder
    public Approval( final long docSeq, final int lineSeq, int status, long userSeq ) {
        this.docSeq = docSeq;
        this.lineSeq = lineSeq;
        this.status = status;
        this.userSeq = userSeq;
    }

    public void updateApproval( final long docSeq, final long userSeq, final int status, final String comments ) {
        this.docSeq = docSeq;
        this.userSeq = userSeq;
        this.status = status;
        this.comments = comments;
    }
}

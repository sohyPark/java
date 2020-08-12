package com.psh.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Doc extends BasetimeEntity{

    @Id
    @GeneratedValue
    private long docSeq;

    private String title;

    private String contents;

    private int type;

    private long userSeq;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name="userSeq", insertable=false, updatable=false)
    private User user;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name="docSeq")
    private List<Approval> approval;

    @Builder
    public Doc( final long docSeq, final String title, final String contents, final int type, final long userSeq ) {
        this.docSeq = docSeq;
        this.title = title;
        this.contents = contents;
        this.type = type;
        this.userSeq = userSeq;
    }
}

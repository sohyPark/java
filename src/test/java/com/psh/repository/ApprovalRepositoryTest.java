package com.psh.repository;

import com.psh.entity.Approval;
import com.psh.entity.Doc;
import com.psh.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


@RunWith( SpringRunner.class)
@SpringBootTest
public class ApprovalRepositoryTest {

    @Autowired
    ApprovalRepository approvalRepository;

    @Autowired
    DocRepository docRepository;

    @Test
    public void findApprovalByUserAndDocSeq() {

        User user = new User().builder()
                .id( "parksohyun" )
                .name( "박소현" )
                .userSeq(2)
                .password( "1234")
                .build();

        Approval approval = approvalRepository.findApprovalByDocSeqAndStatusAndUserSeq( 5, 0, 2 );

        System.out.println(approval);

    }

    @Test
    public void findAllByUserSeqAndStatusIn() {
        List<Integer> status = Arrays.asList(1, 2);

        List<Approval> list = approvalRepository.findAllByUserSeqAndStatusIn( 2, status );
        list.forEach( approval -> System.out.println(approval) );

        PageRequest pageable = PageRequest.of( 1, 2 );
        Page<Doc> docList = docRepository.findDocsByApprovalIn( list, pageable);
        docList.forEach( doc -> System.out.println(doc) );
    }
}
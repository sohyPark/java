package com.psh.service;

import com.psh.code.ApprovalStatusEnum;
import com.psh.entity.Approval;
import com.psh.entity.Doc;
import com.psh.repository.ApprovalRepository;
import com.psh.repository.DocRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocServiceTest {

    @Autowired
    DocRepository docRepository;

    @Autowired
    ApprovalRepository approvalRepository;

    @Test
    public void findById() {
        long docSeq = 5;
        Optional<Doc> doc = docRepository.findById(docSeq);
        System.out.println(doc);
    }

    @Test
    public void findAll() {
        List<Doc> list = docRepository.findAll();
        list.forEach( doc -> System.out.println(doc) );
    }

    @Test
    public void pageFindAll() {
        Pageable pageable = PageRequest.of( 0, 3 );
        Page<Doc> list = docRepository.findAll( pageable );
        System.out.println(list.getTotalPages());
        list.forEach( doc -> System.out.println(doc.getDocSeq()) );
    }

    @Test
    public void update() {

    }

    @Test
    public void get() {

    }

    @Test
    public void add() {

        List<Long> approvalUserSeqList = new ArrayList<>();
        approvalUserSeqList.add( ( long ) 3 );
        approvalUserSeqList.add( ( long ) 4 );

        Doc doc = new Doc().builder()
                .title( "결재 부탁드립니다." )
                .contents( "승인해주세요" )
                .userSeq( 2 )
                .type( 1 )
                .build();

        Doc createdDoc = docRepository.save( doc );

        long docSeq = createdDoc.getDocSeq();

        int lineSeq = 1;
        for ( long userSeq : approvalUserSeqList ) {
            Approval approval = new Approval().builder()
                    //.userSeq( userSeq )
                    .docSeq( docSeq )
                    .lineSeq( lineSeq )
                    .status( ApprovalStatusEnum.WAITING.ordinal() )
                    .build();

            lineSeq++;

            approvalRepository.save( approval );

        }
    }
}
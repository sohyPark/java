package com.psh.repository;

import com.psh.entity.Approval;
import com.psh.entity.Doc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DocRepository extends JpaRepository<Doc, Long> {
    Page<Doc> findAll(Pageable pageable);

    Optional<Doc> findById( Long docSeq );

    @Override
    Doc getOne( Long docSeq );

    // OUTBOX: 내가 생성한 문서 중 결재 진행중인 문서
    Page<Doc> findDocsByUserSeqEquals( long userSeq, Pageable pageable );

    Page<Doc> findDocsByApprovalIn(List<Approval> approval, Pageable pageable );

}

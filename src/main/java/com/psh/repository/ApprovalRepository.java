package com.psh.repository;

import com.psh.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    Approval findApprovalByDocSeqAndStatusAndUserSeq(long docSeq, int status, long userSeq);

    List<Approval> findAllByUserSeqAndStatusIn(long userSeq, List<Integer> status);
}

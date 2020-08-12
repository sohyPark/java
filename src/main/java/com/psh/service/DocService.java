package com.psh.service;

import com.psh.code.ApprovalStatusEnum;
import com.psh.code.DocStatusEnum;
import com.psh.dto.DocDto;
import com.psh.entity.Approval;
import com.psh.entity.Doc;
import com.psh.repository.ApprovalRepository;
import com.psh.repository.DocRepository;
import com.psh.util.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
public class DocService {
    private static final Logger logger = LoggerFactory.getLogger(DocService.class);

    @Autowired
    private DocRepository docRepository;

    @Autowired
    private ApprovalRepository approvalRepository;

    public ResponseEntity findAll( HttpServletRequest request, DocDto.FindAll requestData ) {
        int page = requestData.getPage();
        int size = requestData.getSize();
        int status = requestData.getStatus();

        Pageable pageable = PageRequest.of( page, size, Sort.by( "createdDate" ).descending() );

        final long userSeq = requestData.getUserSeq();

        DocStatusEnum docStatusEnum = DocStatusEnum.getEnum( status );
        List<Integer> statusList = new ArrayList<>();
        List<Doc>docList = new ArrayList<>();

        switch ( docStatusEnum ) {
            case ALL:
                Page<Doc> paging = docRepository.findAll(pageable);
                docList = paging.getContent();
                break;

            // 내가 생성한 문서 중 결재 진행 중인 문서
            case OUTBOX:
                Page<Doc> list = docRepository.findDocsByUserSeqEquals( userSeq, pageable );
                for ( Doc doc : list.getContent() ) {
                    List<Approval> approvalList = doc.getApproval();
                    for ( Approval approval : approvalList ) {
                        if ( 0 == approval.getStatus() ) {
                            docList.add( doc );
                            break;
                        }
                    }
                }
                break;
            // 내가 결재를 해야 할 문서
            case INBOX:
                statusList.add( ApprovalStatusEnum.WAITING.ordinal() );
                break;
            // 내가 관여한 문서 중 결재가 완료(승인 또는 거절)된 문서
            case ARCHIVE:
                statusList.add( ApprovalStatusEnum.APPROVAL.ordinal() );
                statusList.add( ApprovalStatusEnum.REJECT.ordinal() );
                break;
        }


        if ( DocStatusEnum.OUTBOX.ordinal() == status || DocStatusEnum.ARCHIVE.ordinal() == status ) {
            List<Approval> approvalList = approvalRepository.findAllByUserSeqAndStatusIn( userSeq, statusList );
            Page<Doc> paging = docRepository.findDocsByApprovalIn( approvalList, pageable );
            docList = paging.getContent();
        }

        int total = docRepository.findAll().size();

        Map<String, Object> result = new HashMap<>();
        result.put( "total", total );
        result.put( "list", docList );

        return RestResponse.success( result );
    }

    public ResponseEntity get( final long docSeq ) {
        if ( 0 >= docSeq ) {
            logger.error( "docSeq is invalid - docSeq: {}", docSeq );
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "결재 문서 번호가 유효하지 않습니다." );
        }

        Optional<Doc> doc;
        try {
            doc = docRepository.findById( docSeq );
        } catch ( Exception e ) {
            logger.error( "Doc findById error : {}, docSeq: {}", e.getMessage(), docSeq );
            return RestResponse.fail( HttpStatus.INTERNAL_SERVER_ERROR, e.getCause().getMessage() );
        }

        return RestResponse.success( doc.get() );
    }

    public ResponseEntity add( final DocDto.Add docDto ) {

        String title = docDto.getTitle();
        if ( null == title || title.isEmpty() ) {
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "제목을 입력해주세요." );
        }

        String contents = docDto.getContents();
        if ( null == contents || contents.isEmpty() ) {
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "내용을 입력해주세요.");
        }

        int type = docDto.getType();
        if ( 0 >= type ) {
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "결재의 분류를 선택햊세요.");
        }

        List<Long> approvalUserList = docDto.getApprovalUserList();
        if ( approvalUserList.size() < 1 ) {
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "결재받을 사용자를 선택해주세요.");
        }

        Doc doc = new Doc().builder()
                .title( title )
                .contents( contents )
                .type( type )
                .userSeq( docDto.getUserSeq())
                .build();

        Doc createdDoc;
        try {
            createdDoc = docRepository.save( doc );
        } catch ( Exception e ) {
            logger.error( "Doc add failed - error: {}, title: {}, contents: {}, type: {}, approvalUserList: {}", e.getMessage(), title, contents, type, approvalUserList);
            return RestResponse.fail( HttpStatus.INTERNAL_SERVER_ERROR, e.getCause().getMessage() );
        }

        long docSeq = createdDoc.getDocSeq();

        int lineSeq = 1;
        for ( long userSeq : approvalUserList ) {
            Approval approval = new Approval().builder()
                    .docSeq( docSeq )
                    .lineSeq( lineSeq )
                    .userSeq( userSeq )
                    .status( ApprovalStatusEnum.WAITING.ordinal() )
                    .build();

            try {
                approvalRepository.save( approval );
            } catch ( Exception e ) {
                logger.error( "Approval save failed - error: {}, docSeq: {}, userSeq: {}, lineSeq: {}", e.getMessage(), docSeq, userSeq, lineSeq );
                return RestResponse.fail( HttpStatus.INTERNAL_SERVER_ERROR, e.getCause().getMessage() );
            }
        }

        return RestResponse.success();
    }

    public ResponseEntity update( final DocDto.Update reqData ) {
        long docSeq = reqData.getDocSeq();
        long userSeq = reqData.getUserSeq();

        if ( docSeq <= 0 ) {
            logger.error( "docSeq is invalid - docSeq: {}, userSeq: {}", docSeq, userSeq );
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "결재 번호가 유효하지 않습니." );
        }

        if ( userSeq <= 0 ) {
            logger.error( "userSeq is invalid - docSeq: {}, userSeq: {}", docSeq, userSeq );
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "유저 정보 유효하지 않습니." );
        }

        Optional<Doc> doc = docRepository.findById( docSeq );
        if ( !doc.isPresent() ) {
            logger.error( "doc is not found - docSeq: {}, userSeq: {}", docSeq, userSeq );
            return RestResponse.fail( HttpStatus.NO_CONTENT, "결재할 결재문서를 찾을 수 없습니다." );
        }

        List<Approval> userApprovalList = doc.get().getApproval();
        boolean isContain = false;
        for ( Approval approval : userApprovalList ) {
            if ( userSeq == approval.getUser().getUserSeq() ) {
                isContain = true;
                break;
            }
        }

        if ( !isContain ) {
            logger.error( "Doc is not contain approvalList - docSeq: {}, userSeq: {}, userApprovalList: {}", docSeq, userSeq, userApprovalList );
            return RestResponse.fail( HttpStatus.BAD_REQUEST, "결재자에 포함되어있지 않습니다." );
        }

        Approval approval = approvalRepository.findApprovalByDocSeqAndStatusAndUserSeq( docSeq, ApprovalStatusEnum.WAITING.ordinal(), userSeq );
        if ( approval == null ) {
            logger.error( "findApprovalByDocSeqAndStatusAndUserSeq is not found - docSeq: {}, userSeq: {}, status: {}", doc, userApprovalList, 0 );
            return RestResponse.fail( HttpStatus.NO_CONTENT, "결재 라인 정보를 찾을 수 없습니다." );
        }

        approval.updateApproval( docSeq, userSeq, reqData.getIsApproval(), reqData.getComments() );
        approvalRepository.save( approval );

        return RestResponse.success();
    }


}

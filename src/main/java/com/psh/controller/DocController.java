package com.psh.controller;

import com.psh.dto.DocDto;
import com.psh.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping( "/api" )
public class DocController {
    private static final Logger logger = LoggerFactory.getLogger( DocController.class );

    @Autowired
    private DocService docService;

    @RequestMapping( method = RequestMethod.GET, value = "/docs" )
    public ResponseEntity getAll( HttpServletRequest request, final DocDto.FindAll req ) {

        logger.info( "method: GET, api: /doc/all, status: {}", req.getStatus() );

        return docService.findAll( request, req );
    }

    @RequestMapping( method = RequestMethod.GET, value = "/doc" )
    public ResponseEntity get( @RequestParam( value = "docSeq" ) final long docSeq ) {

        logger.info( "method: GET, api: /doc, docSeq: {}", docSeq );

        return docService.get( docSeq );
    }

    @RequestMapping( method = RequestMethod.POST, value = "/doc" )
    public ResponseEntity add( @RequestBody final DocDto.Add docDto ) {

        logger.info( "method: PUT, api: /doc, title: {}, contents: {}, type: {}, approvalUserList: {} ",
                docDto.getTitle(), docDto.getContents(), docDto.getType(), docDto.getApprovalUserList() );

        return docService.add( docDto );

    }

    @RequestMapping( method = RequestMethod.PUT, value = "/doc" )
    public ResponseEntity update( @RequestBody final DocDto.Update docDto ) {

        logger.info( "method: POST, api: /doc, docSeq: {}, userSeq: {}, comments: {}, isApproval: {} ",
                docDto.getDocSeq(), docDto.getUserSeq(), docDto.getComments(), docDto.getIsApproval() );

        return docService.update( docDto );

    }
}

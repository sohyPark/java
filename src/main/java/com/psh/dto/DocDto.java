package com.psh.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DocDto {

    @Getter
    @Setter
    public static class Update {
        private long docSeq;
        private long userSeq;
        private int isApproval; //1:승인, 2:거절
        private String comments;

        @Builder
        public Update( long docSeq, long userSeq, int isApproval, String comments ) {
            this.docSeq = docSeq;
            this.userSeq = userSeq;
            this.isApproval = isApproval;
            this.comments = comments;
        }
    }

    @Getter
    @Setter
    public static class Add {
        private String title;
        private String contents;
        private int type;
        private List<Long> approvalUserList;
        private long userSeq;

        @Builder
        public Add( String title, String contents, int type, List<Long> approvalUserList, long userSeq ) {
            this.title = title;
            this.contents = contents;
            this.type = type;
            this.approvalUserList = approvalUserList;
            this.userSeq = userSeq;
        }
    }

    @Getter
    @Setter
    public static class FindAll {
        private int page;
        private int size;
        private int status;
        private long userSeq;

        @Builder
        public FindAll( int page, int size, int status, long userSeq ) {
            this.page = page;
            this.size = size;
            this.status = status;
            this.userSeq = userSeq;
        }
    }

}

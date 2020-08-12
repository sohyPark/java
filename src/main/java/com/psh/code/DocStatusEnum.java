package com.psh.code;

public enum DocStatusEnum {
    ALL(0),     // 전체
    OUTBOX(1),  // 내가 생성한 문서 중 결재 진행 중인 문서
    INBOX(2),   // 내가 결재를 해야 할 문서
    ARCHIVE(3); // 내가 관여한 문서 중 결재가 완료(승인 또는 거절)된 문서

    private int status;

    DocStatusEnum( int status ) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static DocStatusEnum getEnum( int status ) {
        for ( DocStatusEnum docStatusEnum : DocStatusEnum.values()) {
            if ( status == docStatusEnum.getStatus() ) {
                return docStatusEnum;
            }
        }
        return null;
    }
}

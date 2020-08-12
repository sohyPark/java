<template>
    <div class="doc">
        <div style="float: right">
            <span>{{user.name}}({{user.id}})님이 접속하였습니다.</span>
        </div>

        <div>
            <label style="display: block; font-size: smaller; width: 150px">결재 상태</label>
            <select v-model="filter.status" class="custom-select" style="width: 150px; font-size: smaller">
                <option value=0>ALL</option>
                <option value=1>OUTBOX</option>
                <option value=2>INBOX</option>
                <option value=3>ARCHIVE</option>
            </select>

            <button class="btn btn-dark" style="width: 80px; margin-left: 20px; font-size: smaller" @click="getDocList">조회</button>
        </div>

        <div>
            <button class="btn btn-primary" style="width: 100px; font-size: smaller; float: right" @click="openDialog">결재 생성</button>
        </div>

        <div style="margin-top: 50px; min-height: 500px">
            <SortedTable :values="docList" style="margin-top: 50px">
                <thead style="font-size: smaller">
                <tr>
                    <th scope="col" style="text-align: center">
                        <SortLink name="type">분류</SortLink>
                    </th>
                    <th scope="col" style="text-align: center">
                        <SortLink name="title">제목</SortLink>
                    </th>
                    <th scope="col" style="text-align: center">
                        <SortLink name="regName">등록자</SortLink>
                    </th>
                    <th scope="col" style="text-align: center">
                        <SortLink name="regDatetime">등록일</SortLink>
                    </th>
                    <th scope="col" style="text-align: center">
                        <SortLink name="status">결재 상태</SortLink>
                    </th>
                </tr>
                </thead>
                <tbody slot="body" slot-scope="sort" style="font-size: smaller; text-align: center">
                <tr v-for="item in docList" :key="item.docSeq">
                    <td hidden>{{item.docSeq}}</td>
                    <td>{{item.type|typeFilter}}</td>
                    <td>{{item.title}}</td>
                    <td>{{item.user.name}}({{item.user.id}})</td>
                    <td>{{item.createdDate}}</td>
                    <td>{{item.status|statusFilter}}</td>
                    <td>
                        <button class="btn btn-dark" style="font-size: x-small" @click="openDetailDialog(item)">결재 보기</button>
                    </td>
                </tr>
                </tbody>
            </SortedTable>
        </div>

        <v-pagination v-model="filter.page" :page-count="total" @change="getDocList"
                      style="position: absolute; left: 45%">
        </v-pagination>

        <!--결재 요청-->
        <div v-if="dialogLayer.show" class="modal-mask">
            <div class="modal-wrapper">
                <div class="modal-container">
                    <div class="modal-body">
                        <div>
                            <select v-model="dialogLayer.type" class="custom-select" style="width: 150px; font-size: smaller">
                                <option value=0>결재분류</option>
                                <option value=1>휴가 신청서</option>
                                <option value=2>경비 정산서</option>
                                <option value=3>개인 서류</option>
                            </select>
                            <input class="form-control" v-model="dialogLayer.title"
                                   placeholder="제목을 입력해주세요."
                                   style="margin-top: 20px; font-size: smaller">
                            <textarea class="text-area" v-model="dialogLayer.contents"
                                      placeholder="내용을 입력해주세요."
                                      style="width: 100%; margin-top: 20px; height: 200px; font-size: smaller"></textarea>
                            <multiselect v-model="dialogLayer.selectedUserList" :options="dialogLayer.userList" :multiple="true"
                                         placeholder="결재 승인받을 결재자를 순서대로 선택해주세요."
                                         label="name" track-by="id"
                                         style="margin-top: 10px">
                            </multiselect>

                        </div>
                    </div>

                    <div class="modal-footer" style="display: block">
                        <div style="margin-top: 20px">
                            <button class="btn btn-primary" style="width: 49%; float: left; font-size: smaller" @click="createDoc">결재 요청</button>
                            <button class="btn btn-dark" style="width: 49%; float: right; font-size: smaller" @click="closeDialog">닫기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!--결재 상세-->
        <div v-if="detailLayer.show" class="modal-mask">
            <div class="modal-wrapper">
                <div class="modal-container">
                    <div class="modal-body">
                        <div style="width: 70%; float: left">
                            <span>[{{detailLayer.doc.type}}]{{detailLayer.doc.title}}</span>
                            <textarea class="text-area" v-model="detailLayer.doc.contents" readonly
                                      style="width: 100%; margin-top: 20px; height: 200px; font-size: smaller"></textarea>
                        </div>
                        <div style="width: 30%; float: right; overflow:auto">
                            <SortedTable :values="detailLayer.approvalList" style="margin-top: 50px">
                                <tbody slot="body" slot-scope="sort" style="font-size: smaller">
                                <tr v-for="item in detailLayer.approvalList" :key="item.userSeq">
                                    <td style="vertical-align: middle;">{{item.user.name}}({{item.user.id}})</td>
                                    <td v-if="item.isMine && item.status === 0">
                                        <button class="btn btn-dark" style="font-size: x-small" @click="updateDoc(1, detailLayer.doc.docSeq)">승인</button>
                                    </td>
                                    <td v-if="item.isMine && item.status === 0">
                                        <button class="btn btn-dark" style="font-size: x-small" @click="updateDoc(2, detailLayer.doc.docSeq)">거절</button>
                                    </td>
                                    <td v-if="item.status === 1">
                                        <span style="width: 50px; font-size: smaller" class="btn btn-success">승인</span>
                                    </td>
                                    <td v-if="item.status === 2">
                                        <span style="width: 50px; font-size: smaller" class="btn btn-danger">거절</span>
                                    </td>
                                    <br>
                                    <td v-if="item.isMine && item.status === 0">
                                        <input class="form-control" placeholder="코멘트 입력 가능" v-model="detailLayer.comments">
                                    </td>
                                    <td v-else-if="item.status > 0">
                                        <span>{{item.comments}}</span>
                                    </td>
                                </tr>
                                </tbody>
                            </SortedTable>
                        </div>
                    </div>

                    <div class="modal-footer" style="display: block">
                        <div style="margin-top: 20px">
                            <button class="btn btn-dark" style="width: 100%; font-size: smaller" @click="closeDetailDialog">닫기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


</template>

<script>
import vPagination from 'vue-plain-pagination';
import Multiselect from 'vue-multiselect';
import Vue from 'vue'

Vue.component('multiselect', Multiselect)
Vue.component('vPagination', vPagination)


export default {
    name: 'DocList',

    data: function () {
        return {
            currentPage: 1,
            total: 1,
            docList: [],
            value: null,
            user: {},
            options: ['list', 'of', 'options'],
            approvalList:[],
            filter: {
                status: 0,
                page: 1,
                size: 3
            },
            docDialog: '',
            dialogLayer: {
                selectedUserList: [],
                userList: [],
                show: false,
                title: null,
                contents: null,
                type: 0
            },
            detailLayer: {
                show: false,
                doc: {},
                approvalList: [],
                comments: null
            }
        }
    },
    filters: {
        statusFilter: function (val) {
            if (0 === val) {
                return "전체";
            } else if (1 === val) {
                return "INBOX";
            } else if (2 === val) {
                 return "OUTBOX";
            } else if (3 === val) {
                return "ARCHIVE";
            }
        },
        typeFilter: function (val) {
            if (1 === val) {
                return "휴가 신청서";
            } else if (2 === val) {
                return "경비 정산서";
            } else if (3 === val) {
                return "개인 서류";
            }
        },
    },
    methods: {
        getUserInfo: function() {
            const user = this.$cookie.get('user');
            this.user = JSON.parse(user);
        },
        getDocList: function () {c
            const token = this.$cookie.get('token');
            this.$axios.get('/api/docs', {
                    headers: {'jwt-auth-token': token},
                    params: {
                        userSeq: this.user.userSeq,
                        status: this.filter.status,
                        page: this.filter.page - 1,
                        size: this.filter.size
                    }
                })
                .then((response) => {
                    this.tokenValidationChk(response.data)
                    if (response.status == 200) {
                        const total = response.data.total;
                        const size = this.filter.size;

                        this.total = Math.ceil(total / size);
                        this.docList = response.data.list;
                    }
                })
                .catch((ex) => {
                    console.log(ex);
                })
        },
        getDoc: function (val) {
            const token = this.$cookie.get('token');
            this.$axios.get('/api/doc', {
                headers: {'jwt-auth-token': token},
                params: {docSeq: val}
            })
                .then((response) => {
                    this.tokenValidationChk(response.data);
                    if (response.status == 200) {
                        this.detailLayer.doc = response.data;
                        const approvalList = response.data.approval;

                        for (let idx in approvalList) {
                            const approval = approvalList[idx];

                            if (approval.userSeq == this.user.userSeq) {
                                approval['isMine'] = true;
                            }
                            this.detailLayer.approvalList.push(approval);
                        }
                    }
                })
                .catch((ex) => {
                    console.log(ex);
                })
        },
        openDetailDialog: function(item) {
            this.detailLayer.show = true;
            this.getDoc(item.docSeq);
        },
        closeDetailDialog: function () {
            this.detailLayer.show = false;
            this.detailLayer.doc = {};
            this.detailLayer.approvalList = [];
        },
        openDialog: function () {
            this.dialogLayer.show = true;
            this.getUserList();
        },
        closeDialog: function () {
            this.dialogLayer.show = false;
            this.dialogLayer.selectedUserList = [];
            this.dialogLayer.title = null;
            this.dialogLayer.contents = null;
            this.dialogLayer.type = 0;
        },
        getUserList: function () {
            const token = this.$cookie.get('token');

            this.$axios.get('/api/user/all', {
                    headers: {'jwt-auth-token': token},
                    params: this.filter
                })
                .then((response) => {
                    const status = response.status;
                    const data = response.data;
                    if (status !== 200) {
                        alert(data);
                        return
                    }
                    this.dialogLayer.userList = data;
                })
                .catch((ex) => {
                    console.log(ex);
                })
        },
        createDoc: function () {
            if (!this.dialogLayer.title) {
                alert("결재 제목을 입력해주세요.");
                return;
            }
            if (!this.dialogLayer.contents) {
                alert("결재 내을 입력해주세요.");
                return;
            }
            if (this.dialogLayer.type <= 0) {
                alert("결재 분류를 선택해주세요.");
                return;
            }
            const selectedUserList = this.dialogLayer.selectedUserList;
            if (selectedUserList.length < 1) {
                alert("결재 승인받을 결재자를 최소 한명 이상 선택해주세요.");
                return;
            }
            let approvalUserList = [];
            for (let idx in selectedUserList) {
                const user = selectedUserList[idx];
                approvalUserList.push(user.userSeq);
            }

            const header = {
                headers: {
                    "Content-type": "application/json",
                    "jwt-auth-token": this.$cookie.get('token')
                }
            };
            const params = {
                title: this.dialogLayer.title,
                contents: this.dialogLayer.contents,
                type: this.dialogLayer.type,
                approvalUserList: approvalUserList,
                userSeq: this.user.userSeq
            };

            this.$axios.post('/api/doc', params, header)
                .then((response) => {
                    const status = response.status;
                    if (status === 200) {
                        alert("결재 요청이 성공하였습니다.");
                        this.dialogLayer.show = false;
                        this.getDocList();

                        return;
                    } else {
                        alert(response.data);
                    }
                })
                .catch((ex) => {
                    console.log(ex);
                })
        },
        updateDoc: function (isApproval, docSeq) {
            if (isApproval !== 1 && isApproval !== 2) {
                alert("승인 또는 거절 버튼을 눌러주세요.");
                return;
            }

            let confirmMessage;
            if (isApproval === 1) {
                confirmMessage = "해당 결재를 승인하시겠습니까?";
            } else if (isApproval === 2) {
                confirmMessage = "해당 결재를 거하시겠습니까?";
            }
            if ( !confirm(confirmMessage) ) {
                return;
            }

            const header = {
                headers: {
                    "Content-type": "application/json",
                    "jwt-auth-token": this.$cookie.get('token')
                }
            };
            const params = {
                isApproval: isApproval,
                docSeq: docSeq,
                comments: this.detailLayer.comments,
                userSeq: this.user.userSeq
            };

            this.$axios.put('/api/doc', params, header)
                .then((response) => {
                    this.tokenValidationChk(response.data);
                    const status = response.status;
                    if (status === 200) {
                        let alertMessage;
                        if (isApproval === 1) {
                            alertMessage = "결재 승인이 완료되었습니다.";
                        } else if (isApproval === 2) {
                            alertMessage = "결재 거절이 완료되었습니다.";
                        }
                        alert(alertMessage);
                        this.detailLayer.show = false;
                        this.detailLayer.approvalList = [];
                        this.detailLayer.comments = null;
                        this.getDocList();
                    } else {
                        alert(response.data);
                    }
                })
                .catch((ex) => {
                    console.log(ex);
                })
        },
        tokenValidationChk: function (data) {
            if (data.hasOwnProperty('isToken')) {
                const isToken = data.isToken;
                if ('false' === isToken) {
                    this.$cookie.delete('toke');
                    this.$router.replace('/login');
                }
            }
        }
    },
    created() {
        this.getUserInfo();
        this.getDocList();
    }

}
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style scoped>
    .doc {
        padding: 50px;
    }

    .modal-mask {
        position: fixed;
        z-index: 9998;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, .5);
        display: table;
        transition: opacity .3s ease;
    }

    .modal-wrapper {
        display: table-cell;
        vertical-align: middle;
    }

    .modal-container {
        width: 80%;
        height: 70%;
        margin: 0px auto;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 2px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
        transition: all .3s ease;
        font-family: Helvetica, Arial, sans-serif;
    }

    .modal-header h3 {
        margin-top: 0;
        color: #42b983;
    }

    .modal-body {
        margin: 20px 0;
    }

    .modal-default-button {
        float: right;
    }

    .modal-enter {
        opacity: 0;
    }

    .modal-leave-active {
        opacity: 0;
    }

    .modal-enter .modal-container,
    .modal-leave-active .modal-container {
        -webkit-transform: scale(1.1);
        transform: scale(1.1);
    }


</style>

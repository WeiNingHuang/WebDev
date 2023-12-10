<script>
import { end } from '@popperjs/core';
import axios from 'axios';
export default {
    data() {
        return {
            //分頁用
            perpage: 10, //一頁的資料數
            currentPage: 1,
            flag: 0, //彈框顯示及隱藏
            searchFlag: false,
            postflag: false,
            state: 'add', //紀錄建立"add"、編輯"edit"
            nextState: 'add',
            //搜尋用
            getTitle: "",
            getDescription: "",
            getStartDate: "",
            getEndDate: "",
            getPublised: false,
            getQnId: 0, //獲取、新增、刪除題目用（從setQn方法-> this.getQnId = qn.id)
            obj: { //前端要輸入的項目,準備要送回給後端的，與input綁定
                questionnaire: {
                    title: "",
                    description: "",
                    published: false,
                    startDate: "",
                    endDate: ""
                },
                question_list: [{
                    quId: 0,
                    qnId: 0,
                    qnTitle: "",
                    optionType: "",
                    necessary: false, //先預設false，要綁定radio
                    option: ""
                }]
            },
            newQu: {
                quId: 0,
                qnId: 0,
                qnTitle: "",
                optionType: "",
                necessary: false, //先預設false，要綁定radio
                option: ""
            },
            delflag: false,
            delType: '',//當前刪除類型，用於區分刪除問卷還是題目
            delList: [], //前端欲刪除的問卷的id列表,準備要送回給後端的
            delQnId: 0,
            delQuIdList: [],
            qnList: [], //存放後端回傳的問卷物件 (可放置多個問卷)
            quList: [], //存放後端回傳的問題物件（可放置多個問題）
        };
    },
    computed: {
        totalPage() {
            return Math.ceil(this.qnList.length / this.perpage)
            //Math.ceil()取最小整數
        },
        pageStart() {
            return (this.currentPage - 1) * this.perpage
            //取得該頁第一個值的index
        },
        pageEnd() {
            return this.currentPage * this.perpage
            //取得該頁最後一個值的index
        }
    },
    mounted() {
        this.getQnList(); //重新加載頁面時，獲取問卷
        // this.getQuList();
    },
    methods: {
        searchQn() {
            this.searchFlag = !this.searchFlag; //彈框顯示及隱藏
        },
        getQnList() { //獲取問卷
            axios.get('http://localhost:8080/api/quiz/searchQuestionnaireList', {
                params: { //後端變數：前端變數 =>將前端變數v-model綁定input標籤，作為參數發送給後端
                    title: this.getTitle,
                    description: this.getDescription, //可能可以不用
                    published: this.getPublised,
                    startDate: this.getStartDate,
                    endDate: this.getEndDate,
                }
            }).then(res => {
                console.log(res); //res返回的數據
                this.qnList = res.data.questionnaireList //接受後端返回的數據，給予前端用來存放的qnList，v-for進行畫面渲染
            }).catch(err => {
                console.log(err);
            })
        },
        getQuList() { //獲取問卷的題目
            return axios.get(`http://localhost:8080/api/quiz/searchQuestionList?qnId=${this.getQnId}`).then(res => {
                console.log(res);
                this.quList = res.data.questionList
                console.log(this.quList)
            }).catch(err => {
                console.log(err);
            })
        },
        getStatus(published, startDate, endDate) {
            if (published == false) {
                return "未發布";
            }
            if (published == true) {
                const currentDate = new Date(); //將返回一個包含當前日期和時間的Date對象，然後賦值給currentDate。
                startDate = new Date(startDate); //將startDate和endDate轉換為了Date物件(從後端獲取的日期數據是字串)
                endDate = new Date(endDate);
                if (currentDate < startDate) {
                    return "尚未開始";
                }
                if (currentDate > endDate) {
                    return "已結束";
                } else {
                    return "進行中";
                }
            }
        },
        setQn(type, qn) { //建立或編輯問卷 qn從v-for="(qn, index) in qnList"來的
            this.flag = 1;
            this.state = type; //建立或編輯的狀態變更
            if (this.state == 'add') {
                this.obj.questionnaire = {}; //建立時，欄位一開始應為空
                console.log(this.obj)
            }
            if (this.state == 'edit') { //編輯時，將已有的問卷資料給予
                console.log("edit test")
                this.obj.questionnaire = JSON.parse(JSON.stringify(qn)); //將原有問卷資料qn給予obj。序列化 深拷貝(可以在不影響原始數據情况下進行修改和操作) 
                //this.obj.qnObj = {...qn}; //淺拷貝
                this.nextState = 'edit'; //目的：進入下一頁題目頁面時，獲取其狀態為：編輯，預設狀態為建立
                this.getQnId = qn.id //點選該問卷編輯按鈕以獲得問卷id，目的：給予撈該問卷的題目用
                console.log(this.getQnId)
            }
        },
        setQu(type) { // 題目彈框顯示
            this.flag = 2;
            this.nextState = type;
            if (this.nextState == 'add') {
                this.obj.question_list = []; //建立時，欄位一開始應為空
                console.log(this.obj) //有題目了
            }
            if (this.nextState == 'edit') {
                const result = this.getQuList();
                result.then(() => {
                    this.obj.question_list = JSON.parse(JSON.stringify(this.quList)); //將原有從後端獲取的quList給予obj.question_list。序列化 深拷貝(可以在不影響原始數據情况下進行修改和操作)
                    console.log(this.obj)
                });
            }
        },
        createQu() {  //新增問題
            this.newQu.qnId = this.getQnId
            // this.obj.question_list.push(this.newQu);
            let _quId = this.obj.question_list.length > 0 ? Math.max(...this.obj.question_list.map(item => item.quId)) + 1 : 1; //獲取quId最大值+1
            this.obj.question_list.push({ ...this.newQu, quId: _quId });
            console.log(this.obj.question_list)
            this.newQu = {};  //清空前次輸入內容
        },
        submit(status) { //加入或編輯問卷的傳入資料庫
            this.obj.questionnaire.published = status;
            console.log(this.obj)
            this.obj.question_list.forEach(item => {
                if (item.optionType == 'text') {
                    item.option = "請回答"
                }
            })
            if (this.state == 'add') {
                // if (this.obj.question_list.optionType == 'text') {
                //     this.obj.question_list.option = "請回答"
                // }
                console.log(this.obj)
                // this.postToDB(); //根據點擊按鈕決定發布狀態，重新給予obj
                // console.log(this.obj)
                axios.post('http://localhost:8080/api/quiz/create', this.obj).then(res => {
                    console.log(res);
                    let code = res.data.rtnCode;
                    if (code == 'SUCCESSFUL') {
                        this.getQnList() //調用查詢數據(僅有問卷)
                    }
                }).catch(err => {
                    console.log(err);
                })
            }
            if (this.state == 'edit') {
                axios.post('http://localhost:8080/api/quiz/update', this.obj).then(res => {
                    console.log(res);
                    let code = res.data.rtnCode;
                    if (code == 'SUCCESSFUL') {
                        this.getQnList()
                    }
                }).catch(err => {
                    console.log(err);
                })
            }
            this.postflag = false;
            this.flag = 0;
            this.obj = {}; //清空前次輸入內容??
            // this.getQnList() //送出後，重新撈問卷列表(測試中)
        },
        postTo(qn) {
            this.postflag = true; //顯示發布確認頁
            // this.obj.questionnaire = JSON.parse(JSON.stringify(qn));
        },
        // publishedTo(status) {
        //     this.obj.questionnaire.published = status;
        //     console.log(this.obj)

        //     axios.post('http://localhost:8080/api/quiz/update', this.obj).then(res => {
        //         console.log(res);
        //         let code = res.data.rtnCode;
        //         if (code == 'SUCCESSFUL') {
        //             this.getQnList()
        //         }
        //     }).catch(err => {
        //         console.log(err);
        //     })
        //     this.postflag = false;
        // },
        delQn(qn) { //刪除問卷
            this.delflag = true; //刪除彈框顯示
            this.delType = 'questionnaire'; //當前為刪除問卷
            this.delList.push(qn.id); //將問卷id放入刪除列表
        },
        delQu(qu) { //刪除題目
            this.delflag = true; //刪除彈框顯示
            this.delType = 'questionn'; //當前為刪除題目
            this.delQnId = this.getQnId; //可得到前面點擊編輯的問卷id
            this.delQuIdList.push(qu.quId); //將題目id放入刪除列表??不確定！
            // if (this.nextState == 'add') {
            //     this.quList.splice(quIndex, 1); //bug!!
            // }
        },
        delSubmit() {  //刪除問卷的提交
            if (this.delType == 'questionnaire') {
                axios.post('http://localhost:8080/api/quiz/deleteQuestionnaire', this.delList).then(res => {
                    console.log(res);
                    let code = res.data.rtnCode;
                    if (code == 'SUCCESSFUL') {
                        this.getQnList()
                    }
                }).catch(err => {
                    console.log(err);
                })
                this.delflag = false;
                this.delList = []; //清空前次內容
            }
            if (this.delType == 'questionn') {
                axios.post(`http://localhost:8080/api/quiz/deleteQuestion?qnId=${this.delQnId}&quIdList=${this.delQuIdList}`).then(res => {
                    console.log(res);
                    let code = res.data.rtnCode;
                    if (code == 'SUCCESSFUL') {
                        this.setQu('edit')
                    }
                }).catch(err => {
                    console.log(err);
                })
                this.delflag = false;
                this.delQuIdList = []; //清空前次內容
            }
        },
        delOp(opIndex) { //未使用到!
            this.opList.splice(opIndex, 1);
        },
        setPage(page) {
            if (page <= 0 || page > this.totalPage) {
                return
            }
            this.currentPage = page
        }
    },
};
</script>

<template >
    <!-- 功能：建立問卷 -->
    <div class="addModule">
        <button class="searchBtn btn" @click="searchQn()"><i class="fa-solid fa-magnifying-glass"></i></button>
        <button class="btn" @click="setQn('add', null)">建立問卷</button>
    </div>
    <!-- 搜尋 -->
    <div class="search" v-show="searchFlag">
        <div class="searchTitle">
            <label for="">問卷標題</label>
            <input class="searchText" type="text" name="" id="" placeholder="請輸入問卷標題" v-model="getTitle">
        </div>
        <div class="searchDate">
            <label for="">開始時間</label>
            <input class="searchInputDate" type="date" name="" id="" v-model="getStartDate">
            <label for="">結束時間</label>
            <input class="searchInputDate" type="date" name="" id="" v-model="getEndDate">
            <button class="btn" @click="getQnList()">搜尋</button>
        </div>
    </div>

    <!-- 問卷列表 -->
    <div class="table">
        <table cellpadding="2" style="width:80%">
            <thead>
                <tr>
                    <th width="6%"><input type="checkbox" name="" id=""></th>
                    <th width="6%">編號</th>
                    <th width="29%">問卷標題</th>
                    <th width="8%">狀態</th>
                    <th width="15%">開始時間</th>
                    <th width="15%">結束時間</th>
                    <th width="6%">結果</th>
                    <th width="15%">操作</th>
                </tr>
            </thead>
            <!-- <tbody v-for="(qn, index) in qnList" :key="qn.id"> -->
            <tbody v-for="(qn, index) in qnList.slice(pageStart, pageEnd)" :key="qn.id">
                <tr>
                    <td><input type="checkbox" name="" id=""></td>
                    <td>{{ qn.id }}</td> <!-- {{ index + 1 }}-->
                    <td>{{ qn.title }}</td>
                    <td>{{ getStatus(qn.published, qn.startDate, qn.endDate) }}</td>
                    <td>{{ qn.startDate }}</td>
                    <td>{{ qn.endDate }}</td>
                    <td><a href="">前往</a></td>
                    <td>
                        <button class="delete btn" @click="delQn(qn)"><i
                                class="fa-solid fa-trash-can"></i></button>
                        <button class="edit btn" @click="setQn('edit', qn)"><i
                                class="fa-solid fa-pen-to-square"></i></button>
                        <!-- <button class="post btn" @click="postTo(qn)"><i
                                class="fa-solid fa-arrow-up-from-bracket"></i></button> -->
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <!-- 分頁 -->
    <ul class="pagination" v-if="flag == 0">
        <li class="page-item" @click.prevent="setPage(currentPage - 1)">
            <a class="page-link" href="#" aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>
        <li class="page-item" :class="{ 'active': (currentPage === (n)) }" v-for="(n, index) in totalPage" :key="index"
            @click.prevent="setPage(n)">
            <a class="page-link" href="#">{{ n }}</a>
        </li>
        <li class="page-item" @click.prevent="setPage(currentPage + 1)">
            <a class="page-link" href="#" aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
            </a>
        </li>
    </ul>
    <!-- 建立問卷彈框 -->
    <div class="layer" v-if="flag == 1">
        <div class="mask">
            <div class="head">
                <span>{{ state == 'add' ? '新建問卷' : '編輯問卷' }}</span>
                <button class="clBtn" @click="flag = 0, obj.questionnaire = {}"><i
                        class="fa-solid fa-circle-xmark"></i></button>
            </div>
            <div class="content">
                <label for="">問卷標題</label>
                <input class="text" type="text" name="" id="" placeholder="請輸入問卷標題" v-model="obj.questionnaire.title">
                <label for="">問卷說明</label>
                <textarea name="" id="" placeholder="請輸入問卷說明" v-model="obj.questionnaire.description"></textarea>
                <label for="">開始時間</label>
                <input class="date" type="date" name="" id="" v-model="obj.questionnaire.startDate">
                <label for="">結束時間</label>
                <input class="date" type="date" name="" id="" v-model="obj.questionnaire.endDate">
                <br>
                <button class="btn" @click="flag = 0, obj.questionnaire = {}">取消</button>
                <button class="btn" @click="setQu(nextState)"
                    :disabled="!obj.questionnaire.title || !obj.questionnaire.description || !obj.questionnaire.startDate || !obj.questionnaire.endDate">下一步</button>
                <!-- :disabled 當新增或編輯內容為空時，下一步按鈕禁用 -->
            </div>
        </div>
    </div>


    <!-- 建立題目 -->
    <div class="quLayer" v-if="flag == 2">
        <div class="mask">
            <button class="add btn" @click="createQu()">新增問題</button>
            <div class="question" v-for="(qu, quIndex) in obj.question_list" :key="quIndex">
                <!-- <span>第{{ quIndex + 1 }}題</span> -->
                <label for="">題號:</label>
                <input class="quId" type="text" v-model="qu.quId">
                <label for="">問題:</label>
                <input type="text" placeholder="請輸入問題" v-model="qu.qnTitle">
                <label for="">類型:</label>
                <select name="" id="" v-model="qu.optionType">
                    <option value="single">單選題</option>
                    <option value="multi">多選題</option>
                    <option value="text">簡答題</option>
                </select>
                <input type="checkbox" name="isNecessary" id="" v-model="qu.necessary">必填
                <br>
                <label for="" v-if="qu.optionType != 'text'">選項:</label>
                <input type="text" placeholder="請輸入選項" v-model="qu.option" v-if="qu.optionType != 'text'">

                <button class="delete btn" type="button" @click="delQu(qu)"><i class="fa-solid fa-trash-can"></i></button>
            </div>
            <div class="btnArea">
                <button class="btn" @click="flag = 0">取消</button>
                <button class="btn" @click="setQn()">上一頁</button>
                <button class="btn" @click="postTo()">儲存</button>
            </div>
        </div>
    </div>

    <!-- 是否發布 -->
    <div class="layer" v-show="postflag">
        <div class="delMask">
            <div class="top">
                <button class="clBtn" @click="postflag = false"><i class="fa-solid fa-circle-xmark"></i></button>
            </div>
            <p>是否發布？</p>
            <div class="btnArea">
                <button class="btn" @click="submit(false)">暫不發佈</button>
                <button type="button" class="btn" @click="submit(true)">發佈</button>
            </div>

        </div>
    </div>

    <!-- 刪除彈框 -->
    <div class="layer" v-show="delflag">
        <div class="delMask">
            <div class="top">
                <button class="clBtn" @click="delflag = false"><i class="fa-solid fa-circle-xmark"></i></button>
            </div>
            <p>確定刪除？</p>
            <div class="btnArea">
                <button class="btn" @click="delflag = false">取消</button>
                <button type="button" class="btn" @click="delSubmit()">確定</button>
            </div>

        </div>
    </div>
    <!-- 分頁 -->
</template>

<style lang="scss" scoped>
// 建立問卷
.addModule {
    height: 10vh;
    margin: 0% 10% auto;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .btn {
        width: 120px;
    }

    .searchBtn {
        width: 50px;
    }
}

// 搜尋
.search {
    // max-width: 1260px;
    width: auto;
    height: 25vh;
    margin: 0% 10% 1%;
    padding: 2% 5%;
    background-color: #F7FBFC;
    border-radius: 10px;
    color: #538cce;
    font-size: large;
    font-weight: bolder;

    .searchTitle {
        width: 100%;
        height: 47%;
        margin-bottom: 1%;
        display: flex;
        align-items: center;

        label {
            margin: 0 1%;
            width: 10%;
        }

        input {
            width: 88%;
            height: 80%;
        }
    }

    .searchDate {
        width: 100%;
        height: 47%;
        margin-top: 1%;
        display: flex;
        align-items: center;

        label {
            margin: 0 1%;
            width: 10%;
        }

        input {
            width: 32%;
            height: 80%;
        }

        .btn {
            width: 10%;
            margin-left: 2%;
        }
    }
}

//input樣式
textarea,
input {
    outline-style: none;
    border: 1px solid #ccc;
    border-radius: 10px;
    padding: 5px;
    font-size: medium;
    font-weight: 700;
    color: #769FCD;

    &::placeholder {
        color: #ccc;
    }

    &:hover {
        border-color: #66afe9;
        outline: 0;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 8px rgba(102, 175, 233, .6);
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 8px rgba(102, 175, 233, .6)
    }

    &::-webkit-datetime-edit-text {
        color: #ccc;
    }

    &::-webkit-datetime-edit-year-field {
        color: #ccc;
    }

    &::-webkit-datetime-edit-month-field {
        color: #ccc;
    }

    &::-webkit-datetime-edit-day-field {
        color: #ccc;
    }
}

//按鈕樣式
.btn {
    background-color: #769FCD;
    border: none;
    border-radius: 10px;
    color: white;
    font-size: large;
    // width: 80px;
    // height: 40px;
    font-weight: bolder;

    &:hover {
        background-color: #769fcdb0;
    }
}

// 問卷列表
.table {
    display: flex;
    justify-content: center;

    table {
        width: 80%;
        border-collapse: collapse;
    }

    th,
    td {
        color: #538cce;
        font-size: medium;
        text-align: center; //文字水平居中
        vertical-align: middle; //文字垂直居中
        border: 2px solid #769fcdb0; //表格邊框樣式
    }

    th {
        color: white;
        background-color: #769FCD;
    }

    td {
        background-color: #F7FBFC;
    }

    .btn {
        border-radius: 25%;
        font-size: 15px;
    }

    .delete {
        background-color: #CE5A67;
        margin: 0 4%;
    }

    .edit {
        background-color: #5a85ce;
        margin: 0 4%;
    }

    .post {
        background-color: #6cc1c1;
        margin: 0 4%;
    }
}

//分頁
.pagination {
    justify-content: center;
}


//建立問卷彈框
.layer {
    width: 100%;
    height: 100vh;
    background-color: rgb(0, 0, 0, 0.5);
    //使畫面可以固定最上層
    position: fixed;
    top: 0;
    left: 0;
    display: flex;
    justify-content: center;
    align-items: center;


    .mask {
        width: 700px;
        // height: 450px;
        background-color: #D6E6F2;
        border-radius: 10px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 10px 25px;


        .head {
            width: 100%;
            height: 15%;
            // border: 1px solid blue;
            background-color: rgb(0, 0, 0, 0);
            display: flex;
            justify-content: space-between;
            align-items: center;

            span {
                font-size: 18pt;
                font-weight: bold;
                color: black;
            }

            .clBtn {
                background-color: rgb(0, 0, 0, 0);
                border: none;
                font-size: 25pt;
                color: #769FCD;
                position: relative;

                &:hover {
                    color: #769fcdb0;
                    ;
                    text-shadow: 1px 1px lightgray;
                }
            }
        }

        .content {
            width: 100%;
            height: 80%;
            // border: 1px solid blue;
            background-color: rgb(0, 0, 0, 0);


            .text {
                width: 600px;
                padding: 5pt;
                margin: 5px 0 20px 10px;
                border-radius: 10px;
                border: none;
            }

            textarea {
                width: 600px;
                padding: 5pt;
                margin: 5px 0 20px 10px;
                border-radius: 10px;
                border: none;
            }

            .date {
                width: 200px;
                padding: 5pt;
                margin: 5px 10px 20px 10px;
                border-radius: 10px;
                border: none;
            }

            .btn {
                margin: 0 18%;
            }
        }
    }
}

//建立題目
.quLayer {
    width: 100%;
    height: 100vh;
    background-color: #D6E6F2;
    position: fixed;
    top: 0;
    left: 0;
    overflow-y: auto;
    padding: 2% 10%;

    .mask {
        margin-top: 5%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;


        .question {
            padding: 10px;
            margin: 1%;
            width: 60%;
            height: 20vh;
            border-radius: 10px;
            background-color: #F7FBFC;
        }

        input {
            margin: 0 15px;
        }

        .quId {
            width: 7%;
        }

        .btnArea {
            display: flex;
            justify-content: space-around;
        }

        .btn {
            width: 100px;
            margin: 2%;
        }

        .delete {
            background-color: #CE5A67;
            width: 50px;
            margin-left: 40%;
        }
    }

}

//刪除問卷彈框
.layer {
    z-index: 9999;
    width: 100%;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.5);
    //使畫面可以固定最上層
    position: fixed;
    top: 0;
    left: 0;
    display: flex;
    justify-content: center;
    align-items: center;

    .delMask {
        width: 30%;
        // height: 450px;
        background-color: #D6E6F2;
        border-radius: 10px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 10px 25px;

        .clBtn {
            background-color: rgb(0, 0, 0, 0);
            border: none;
            font-size: 25pt;
            color: #769FCD;
            position: absolute;
            top: 42%;
            right: 36%;

            &:hover {
                color: #769fcdb0;
                ;
                text-shadow: 1px 1px lightgray;
            }
        }

        p {
            font-size: x-large;
            color: #5a85ce;
            font-weight: 700;
        }

        .btn {
            margin: 0 10px;
        }
    }
}
</style>

<script>
import axios from 'axios';
export default {
    data() {
        return {
            flag: false, //彈框顯示及隱藏
            searchFlag: false,
            state: 'add', //紀錄建立"add"、編輯"edit"
            nextState: 'add',
            checkAll: false, //全選狀態 不確定!!
            quflag: false, ////建立題目（測試）
            getTitle: "", //搜尋用
            getDescription: "", //可能可以不用
            getStartDate: "",
            getEndDate: "",
            getQnId: 0,
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
            newOp: {
                text: ""
            },

            delflag: false,
            delType: '',//當前刪除類型，用於區分刪除問卷還是題目
            delList: [], //前端欲刪除的問卷的id列表,準備要送回給後端的
            delQnId: 0,
            delQuIdList: [],
            qnList: [], //存放後端回傳的問卷物件 (可放置多個問卷)
            quList: [], //存放後端回傳的問題物件（可放置多個問題）

            opList:[]
            
        };
    },
    mounted() {
        this.getQnList(); //重新加載頁面時，獲取問卷
        this.getQuList();
    },
    methods: {
        searchQn() {
            this.searchFlag = !this.searchFlag; //彈框顯示
        },
        getQnList() { //獲取問卷
            axios.get('http://localhost:8080/api/quiz/searchQuestionnaireList', {
                params: { //後端變數：前端變數 =>將前端變數v-model綁定input標籤，作為參數發送給後端
                    title: this.getTitle,
                    description: this.getDescription, //可能可以不用
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
        setQn(type, qn) { //建立或編輯問卷 qn從v-for="(qn, index) in qnList"來的
            this.flag = true; //彈框顯示
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
        setQu(type) {
            this.quflag = true; // 題目彈框顯示
            this.nextState = type;
            if (this.nextState == 'add') {
                this.obj.question_list = []; //建立時，，欄位一開始應為空
                console.log(this.obj) //有題目了
            }
            if (this.nextState == 'edit') {
                const result = this.getQuList(); //得到quList
                result.then(() => {
                    this.splitOp(); //將點擊的某一張問卷內所有題目的選項切割
                    // this.obj.question_list = JSON.parse(JSON.stringify(this.quList));
                });
            }
        },
        splitOp(){
            this.quList.forEach(item=> {
                this.opList.push(item.option.split(';'))
            })
            console.log(this.opList) //Ex:['AAA', 'BBB', 'CCC']['10', '20', '30', '40']['ABC']
        },
        submit() { //加入或編輯問卷的提交 (已經不同，無狀態！)
            if (this.state == 'add') {
                console.log(this.obj)
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
            this.quflag = false; //彈框隱藏
            // this.obj = {}; //清空前次輸入內容
            // this.getQnList() //送出後，重新撈問卷列表(測試中)
        },
        delQn(qn) { //刪除問卷
            this.delflag = true; //刪除彈框顯示
            this.delType = 'questionnaire'; //當前為刪除問卷
            this.delList.push(qn.id); //將問卷id放入刪除列表
        },
        delQu(qu) { //刪除題目
            this.delflag = true; //刪除彈框顯示
            this.delType = 'questionn'; //當前為刪除題目
            this.delQnId = this.getQnId; //可得到前面點擊編輯的問卷id
            console.log(this.delQnId)
            this.delQuIdList.push(qu.quId); //將題目id放入刪除列表??不確定！
            console.log(this.delQuIdList)
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
                        this.getQuList()
                    }
                }).catch(err => {
                    console.log(err);
                })
                this.delflag = false;
                this.delQuIdList = []; //清空前次內容
            }

        },
        createQu() {  //新增問題
            this.obj.question_list.push(this.obj.question_list);
            this.newQu = {};  //清空前次輸入內容
        },
        // delQu(quIndex) {
        //     this.quList.splice(quIndex, 1); ////刪除位置及個數。不設定條件至少需留一個問題，因有可能只先建立問卷
        // },
        // creatOp() {
        //     this.opList.push(this.newOp);
        //     this.newOp = {};
        //     // this.quList[quIndex].opList.push(this.newOp);
        //     // const optionTextArray = this.quList[index].qOption.map(option => option.text);
        //     // this.quList[index].optionText = optionTextArray.join(';');
        // },
        // delOp(opIndex) {
        //     this.opList.splice(opIndex, 1);
        // }
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
        <table cellpadding="5" style="width:80%">
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
            <tbody v-for="(qn, index) in qnList" :key="qn.id">
                <tr>
                    <td><input type="checkbox" name="" id=""></td>
                    <td>{{ qn.id }} -> {{ index + 1 }}</td>
                    <td>{{ qn.title }}</td>
                    <td>{{ qn.published }}</td>
                    <td>{{ qn.startDate }}</td>
                    <td>{{ qn.endDate }}</td>
                    <td><a href="">前往</a></td>
                    <td>
                        <button class="delete" @click="delQn(qn)"><i class="fa-solid fa-trash-can"></i></button>
                        <button class="edit" @click="setQn('edit', qn)"><i class="fa-solid fa-pen-to-square"></i></button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <!-- 建立問卷彈框 -->
    <div class="layer" v-show="flag">
        <div class="mask">
            <div class="head">
                <span>{{ state == 'add' ? '新建問卷' : '編輯問卷' }}</span>
                <button class="clBtn" @click="flag = false, obj.questionnaire = {}"><i
                        class="fa-solid fa-circle-xmark"></i></button>
            </div>
            <div class="content" v-for="(qu, quIndex) in obj.question_list">
                <!-- <div class="content"> -->
                <label for="">問卷標題</label>
                <input class="text" type="text" name="" id="" placeholder="請輸入問卷標題" v-model="obj.questionnaire.title">
                <label for="">問卷說明</label>
                <textarea name="" id="" placeholder="請輸入問卷說明" v-model="obj.questionnaire.description"></textarea>
                <label for="">開始時間</label>
                <input class="date" type="date" name="" id="" v-model="obj.questionnaire.startDate">
                <label for="">結束時間</label>
                <input class="date" type="date" name="" id="" v-model="obj.questionnaire.endDate">
                <br>
                <button class="btn" @click="flag = false, obj.questionnaire = {}">取消</button>
                <button class="btn" @click="setQu(nextState), flag = false"
                    :disabled="!obj.questionnaire.title || !obj.questionnaire.description || !obj.questionnaire.startDate || !obj.questionnaire.endDate">下一步</button>
                <!-- :disabled 當新增或編輯內容為空時，下一步按鈕禁用 --> <!-- :下一步要先留存問卷資料？？ -->
            </div>
        </div>
    </div>


    <!-- 建立題目 -->
    <div class="quLayer" v-show="quflag">
        <div class="mask">
            <button @click="createQu()">新增問題</button>
            <div class="question" style="border: 1px solid blue;" v-for="(qu, quIndex) in quList" :key="quIndex">
                <span>第{{ quIndex + 1 }}題</span>
                <input type="text" placeholder="請輸入問題" v-model="qu.qnTitle">
                <label for="">類型</label>
                <select name="" id="" v-model="qu.optionType">
                    <option value="">請選擇</option>
                    <option value="single">單選題</option>
                    <option value="multi">多選題</option>
                    <option value="text">簡答題</option>
                </select>
                <input type="checkbox" name="isNecessary" id="" v-model="qu.necessary">必填

                <div class="options" style="border: 1px solid red;" v-for="(op, opIndex) in opList[quIndex]" :key="opIndex">
                        <div class="radioGroup" v-if="qu.optionType == 'single'">
                            <input type="radio" name="option" id="">
                            <input type="text" placeholder="請輸入選項" v-model="op[opIndex]">
                            <span ></span>
                            <p>{{ op[opIndex] }}</p>
                        </div>
                        <div class="checkboxGroup" v-if="qu.optionType == 'multi'">
                            <input type="checkbox" name="" id="">
                            <input type="text" placeholder="請輸入選項" v-model="op[opIndex]">
                            <p>{{ op[opIndex] }}</p>
                        </div>
                        <textarea class="textGroup" name="" id="" placeholder="簡答文字" v-if="qu.optionType == 'text'" disabled v-model="op[opIndex]"></textarea>
                        <button type="button" @click="delOp(opIndex)"><i class="fa-solid fa-square-minus"
                                style="color: rgb(243, 111, 111);"></i>刪除選項</button>
                </div>
                
                <button type="button" @click="creatOp()"><i class="fa-solid fa-square-plus"></i>新增選項</button>
                <button type="button" @click="delQu(qu)"><i class="fa-solid fa-trash-can"></i>刪除問題</button>
            </div>
            <button class="btn" @click="quflag = false">上一頁尚未有功能</button>
            <button class="btn" @click="submit()">送出</button>
        </div>
    </div>

    <!-- 刪除彈框 -->
    <div class="delLayer" v-show="delflag">
        <div class="delMask">
            <span>提醒</span>
            <button class="clBtn" @click="delflag = false"><i class="fa-solid fa-circle-xmark"></i></button>
            <p>確定刪除？</p>
            <button class="btn" @click="delflag = false">取消</button>
            <button type="button" class="btn" @click="delSubmit()">確定</button>
        </div>
    </div>
    <!-- 分頁 -->
</template>

<style lang="scss" scoped>
// 搜尋
.search {
    // max-width: 1260px;
    width: auto;
    height: 25vh;
    margin: 1% 10%;
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
}

// 建立問卷彈框
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
        }
    }
}

.quLayer {
    width: 100%;
    height: 100vh;
    background-color: #D6E6F2;
    position: fixed;
    top: 0;
    left: 0;
    overflow-y: auto;
    padding: 2% 10%;
}

//刪除問卷彈框
.delLayer {
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
        background-color: white;
    }
}
</style>

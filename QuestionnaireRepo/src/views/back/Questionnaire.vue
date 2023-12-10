<script>
export default {
    data() {
        return {
            flag: false, //彈框顯示及隱藏
            state: 'add', //紀錄建立"add"、編輯"edit"
            checkAll: false, //全選狀態 不確定!!
            quflag: false, ////建立題目（測試）
            questionnaire: {
                title: "",
                description: "",
                startDate: "",
                endDate: "",
                check: false, ////不確定!!
            },
            newQu: {
                qTitle: [],
                optionType: "",
                isNecessary: 0,
                qOption: "", //預計要讓選項全部在這裡  這裡不可為陣列 後端期待資料型態為字串?
            },
            newOp: {
                text: ""
            },
            qnList: [], //問卷 (可放置多個問卷)
            quList: [], //可放置多個問題
            opList: [], //可放置多個選項
        };
    },
    mounted() {
        this.getQnList(); //重新加載頁面時，獲取問卷
    },
    methods: {
        getQnList() { //獲取問卷
            fetch('http://localhost:8080/api/quiz/searchQuestionnaireList', {
                method: 'GET',
                headers: {
                    "Content-Type": "application/json",
                },
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log(data);
                    this.qnList = data.questionnaireList;
                })
                .catch(error => console.error('Error:', error));
        },
        setQn(type, qn) { //建立或編輯問卷
            this.flag = true; //彈框顯示
            this.state = type; //建立或編輯的狀態變更
            if (this.state == 'edit') { //編輯時，將已有的問卷資料給予
                this.questionnaire = JSON.parse(JSON.stringify(qn)); //序列化 深拷貝
                //this.questionnaire = {...qn}; //淺拷貝
            } else {
                this.questionnaire = {};
            }
        },
        submit() { //加入或編輯問卷的提交
            if(this.state == 'add') {
                /*
                問卷id:以id最大值+1。
                (1)...展開運算子，Math.max()獲取最大值，map循環遍歷，取得id值，最後進行+1。
                (2)避免list一開始為空陣列，id+1會報錯，故當list長度>0不為空時，取得id+1，否則直接給予id=1。
                */
                let _id = this.qnList.length > 0 ? Math.max(...this.qnList.map(item => item.id)) + 1 : 1;
                this.qnList.push({ ...this.questionnaire, id: _id }); //獲取id並添加進qnList
            }
            if(this.state == 'edit'){
                this.qnList.forEach((item, index)=>{
                    if(item.id == this.questionnaire.id){
                        this.qnList[index] = this.questionnaire; 
                    }
                })
            }
            this.flag = false; //彈框隱藏
            this.questionnaire = {}; //清空前次輸入內容
            this.quflag = true; ////建立題目 彈框顯示（測試）
        },
        delQn(qn, index) {  //刪除問卷
            //this.qnList.splice(index, 1) //刪除位置及個數
            this.qnList = this.qnList.filter(item => v.id != item.id) //陣列篩選，返回滿足條件的陣列。id相同過濾掉pass，當前點擊的和原本id不相同(將v及index傳遞過來，定義item，如果v的id不等於item的id,進行篩選出來)
        },
        createQu() {  //新增問題
            this.quList.push(this.newQu);  //將問題添加進quList (要放入問卷list嗎？)
            this.newQu = {};  //清空前次輸入內容
        },
        delQu(quIndex) {
            this.quList.splice(quIndex, 1); ////刪除位置及個數。不設定條件至少需留一個問題，因有可能只先建立問卷
        },
        creatOp() {
            this.opList.push(this.newOp);
            this.newOp = {};
            // this.quList[quIndex].opList.push(this.newOp);
            // const optionTextArray = this.quList[index].qOption.map(option => option.text);
            // this.quList[index].optionText = optionTextArray.join(';');
        },
        delOp(opIndex) {
            this.opList.splice(opIndex, 1);
        }
    },
};
</script>

<template >
    <!-- 搜尋 -->
    <div class="search">
        <div class="searchTitle">
            <label for="">問卷標題</label>
            <input class="searchText" type="text" name="" id="" placeholder="請輸入問卷標題">
        </div>
        <div class="searchDate">
            <label for="">開始時間</label>
            <input class="searchInputDate" type="date" name="" id="">
            <label for="">結束時間</label>
            <input class="searchInputDate" type="date" name="" id="">
            <button class="btn">搜尋</button>
        </div>
    </div>
    <!-- 功能：建立問卷 -->
    <div class="addModule">
        <button class="btn" @click="setQn('add', null)">建立問卷</button>
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
                        <button class="delete" @click="delQn(qn, index)"><i class="fa-solid fa-trash-can"></i></button>
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
                <button class="clBtn" @click="flag = false, this.questionnaire = {}"><i
                        class="fa-solid fa-circle-xmark"></i></button>
            </div>
            <div class="content">
                <label for="">問卷標題</label>
                <input class="text" type="text" name="" id="" placeholder="請輸入問卷標題" v-model="questionnaire.title">
                <label for="">問卷說明</label>
                <textarea name="" id="" placeholder="請輸入問卷說明" v-model="questionnaire.description"></textarea>
                <label for="">開始時間</label>
                <input class="date" type="date" name="" id="" v-model="questionnaire.startDate">
                <label for="">結束時間</label>
                <input class="date" type="date" name="" id="" v-model="questionnaire.endDate">
                <br>
                <button class="btn" @click="flag = false, this.questionnaire = {}">取消</button>
                <button class="btn" @click="submit()"
                    :disabled="!questionnaire.title || !questionnaire.description || !questionnaire.startDate || !questionnaire.endDate">{{
                        state == 'add' ? '新建問卷' : '編輯問卷' }}</button>
                <!-- :disabled 當新增或編輯內容為空時，creat按鈕禁用 -->
            </div>
        </div>
    </div>

    <!-- 刪除問卷彈框 -->
    <!-- 分頁 -->

    <!-- 建立題目 -->
    <div class="layer" v-show="quflag">
        <div class="mask">
            <button @click="createQu()">新增問題</button>
            <button class="btn" @click="quflag = false">取消</button> <!-- 暫時先可以關閉，之後要改成上一頁 -->
            <div class="question" style="border: 1px solid blue;" v-for="(qu, quIndex) in quList" :key="quIndex">
                <span>第{{ quIndex + 1 }}題</span>
                <input type="text" placeholder="請輸入問題" v-model="qu.qTitle">
                <label for="">類型</label>
                <select name="" id="" v-model="qu.optionType">
                    <option value="">請選擇</option>
                    <option value="single">單選題</option>
                    <option value="multi">多選題</option>
                    <option value="text">簡答題</option>
                </select>
                <input type="radio" name="isNecessary" id="" v-model="qu.isNecessary">必填
                <div class="options" style="border: 1px solid red;" v-for="(op, opIndex) in opList" :key="opIndex">
                    <div class="radioGroup" v-if="qu.optionType == 'single'">
                        <input type="radio" name="option" id=""> <!-- 不確定v-model要綁哪？ 後臺無作用？只是顯示用的？-->
                        <input type="text" placeholder="請輸入選項內" v-model="op.text">
                    </div>
                    <div class="checkboxGroup" v-if="qu.optionType == 'multi'">
                        <input type="checkbox" name="" id=""> <!-- 不確定v-model要綁哪？ 後臺無作用？只是顯示用的？-->
                        <input type="text" placeholder="請輸入選項" v-model="op.text">
                    </div>
                    <!-- <textarea class="textGroup" name="" id="" placeholder="簡答文字" v-model="qu.qOption" v-if="qu.optionType =='text'" disabled></textarea> 有需要這個input? 或是v-model嗎？因不需要選項？  -->
                    <button type="button" @click="delOp(opIndex)">刪除選項</button>
                </div>
                <button type="button" @click="creatOp()">新增選項</button>
                <button type="button" @click="delQu(quIndex)">刪除問題</button>
            </div>
        </div>
    </div>
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
}

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
    justify-content: end;
    align-items: center;

    .btn {
        width: 120px;
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
        background-color: rgb(224, 163, 163);
        border-radius: 20px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 10px 25px;


        .head {
            width: 100%;
            height: 15%;
            border: 1px solid blue;
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
                color: rgb(255, 0, 0);
                position: relative;

                &:hover {
                    color: #88aab9;
                    text-shadow: 1px 1px lightgray;
                }
            }
        }

        .content {
            width: 100%;
            height: 80%;
            border: 1px solid blue;
            background-color: rgb(0, 0, 0, 0);


            .text {
                width: 600px;
                padding: 5pt;
                margin: 5px 0 20px 10px;
                border-radius: 50px;
                border: none;
            }

            textarea {
                width: 600px;
                padding: 5pt;
                margin: 5px 0 20px 10px;
                border-radius: 50px;
                border: none;
            }

            .date {
                width: 200px;
                padding: 5pt;
                margin: 5px 10px 20px 10px;
                border-radius: 50px;
                border: none;
            }

            .btn {
                width: 70px;
                background-color: red;
                border-radius: 50px;
                border: none;
            }
        }
    }
}</style>

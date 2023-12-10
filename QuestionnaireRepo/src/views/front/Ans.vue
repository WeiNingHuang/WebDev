<script>
import axios from 'axios';
export default {
    data() {
        return {
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
            qnList: [], //存放後端回傳的問卷物件 (可放置多個問卷)
            quList: [], //存放後端回傳的問題物件（可放置多個問題）
        }
    },
    mounted() {
        this.getQnList(); //重新加載頁面時，獲取問卷
        this.getQuList();
    },
    methods: {
        backHome(){
            this.$router.push('/')
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
    }
}
</script>

<template>
    <div class="questionnaire">
        <p>{{ qnList.startDate }}</p>
        <p>{{ "問卷題目" }}</p>
        <p>{{ "問卷說明" }}</p>
    </div>
    <div class="userInfo">
        <ul class="list">
            <li>
                <label for="姓名">姓名：</label>
                <input type="text" name="" id="姓名">
            </li>
            <li>
                <label for="電話">電話：</label>
                <input type="tel" name="" id="電話">
            </li>
            <li>
                <label for="信箱">信箱：</label>
                <input type="email" name="" id="信箱">
            </li>
            <li>
                <label for="年齡">年齡：</label>
                <input type="text" name="" id="年齡">
            </li>
        </ul>
    </div>
    <div class="question">
        <ol>
            <li>
                <p>{{ '題目' }}</p>
                <input type="radio" name="" id="">選項
                <input type="checkbox" name="" id="">選項
                <textarea name="" id="" cols="30" rows="10"></textarea>
            </li>
        </ol>
    </div>
    <div class="btnArea">
        <button class="btn" @click="backHome">取消</button>
        <button class="btn" @click="">送出</button>
    </div>
</template>

<style lang="scss" scoped></style>
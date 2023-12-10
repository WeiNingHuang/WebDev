<script>
import axios from 'axios';
export default {
  data() {
    return {
      //分頁用
      perpage: 10, //一頁的資料數
      currentPage: 1,
      flag: 0, //彈框顯示及隱藏
      searchFlag: false,
      //搜尋用
      getTitle: "",
      getDescription: "",
      getStartDate: "",
      getEndDate: "",
      getPublised: false,
      getQnId: 0, //獲取、新增、刪除題目用（從setQn方法-> this.getQnId = qn.id)
      qnList: [], //存放後端回傳的問卷物件 (可放置多個問卷)
      quList: [], //存放後端回傳的問題物件（可放置多個問題）
      opList: [],
      obj: { //單一張問卷與題目組合，前端要輸入的項目,準備要送回給後端的，與input綁定
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
      response: { //問卷回饋，要送回給DB
        user: {
          name: "",
          phone_number: "",
          email: "",
          age: 0,
          qnId: 0,
          qId: 0,
          ans: ""
        }
      },
      ansList: [],
      preAns: {

      },
      // preAns: {
      //   single_ans: [],
      //   multi_ans: [],
      //   text_ans:[],
      // },
    }
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
  },
  methods: {
    searchQn() {
      this.searchFlag = !this.searchFlag; //彈框顯示及隱藏
    },
    getQnList() { //獲取問卷
      axios.get('http://localhost:8080/api/quiz/searchQuestionnaireList', {
        params: { //後端變數：前端變數 =>將前端變數v-model綁定input標籤，作為參數發送給後端
          title: this.getTitle,
          description: this.getDescription,
          published: this.getPublised,
          startDate: this.getStartDate,
          endDate: this.getEndDate,
        }
      }).then(res => {
        console.log(res); //res返回的數據
        this.qnList = res.data.questionnaireList.filter(item => item.published == true); //接受後端返回的數據，給予前端用來存放的qnList，v-for進行畫面渲染，.filter(過濾只顯示已發布的數據) 
        console.log(this.qnList)
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
    fillOut(qn) { //獲得點擊該問卷，qn從v-for="(qn, index) in qnList"來的
      this.flag = 1; //該問卷顯示
      this.obj.questionnaire = JSON.parse(JSON.stringify(qn)); //將原有問卷資料qn給予obj。序列化 深拷貝(可以在不影響原始數據情况下進行修改和操作) 
      this.getQnId = qn.id //點選該問卷編輯按鈕以獲得問卷id，目的：給予撈該問卷的題目用
      console.log(this.getQnId)

      const result = this.getQuList();
      result.then(() => {
        this.obj.question_list = JSON.parse(JSON.stringify(this.quList)); //將原有從後端獲取的quList給予obj.question_list。序列化 深拷貝(可以在不影響原始數據情况下進行修改和操作)
        console.log("question_list"+this.obj.question_list)
        this.splitOp(); //將選項切割
        this.initializeMultiAnsArray();
      });
    },
    splitOp() {
      this.obj.question_list.forEach(item => {
        this.opList.push(item.option.split(';'))
      })
      console.log("OPList in SplitOP" + this.opList)
    },
    setPage(page) {
      if (page <= 0 || page > this.totalPage) {
        return
      }
      this.currentPage = page
    },
    sendAns() {
    this.ansList.push(this.preAns)
      console.log(this.ansList)
    },
    initializeMultiAnsArray() {
    // 根据 quList 中每个元素的 opArray 长度创建二维数组
    console.log(this.obj.question_list)
    
    // this.preAns.multi_ans = this.obj.question_list
    //   .filter(ql => ql.optionType === 'multi') // 仅筛选出多选类型的问题
    //   .map(qn => new Array(qn.option.split(';').length).fill("")); // 根据 opArray 的长度创建二维数组并初始化为 false
    // },
    // this.preAns = this.obj.question_list
    //   .filter(ql => ql.optionType === 'multi') // 仅筛选出多选类型的问题
    //   .map(qn => new Array(qn.option.split(';').length).fill("")); // 根据 opArray 的长度创建二维数组并初始化为 false
    // },
    this.preAns = this.quList.map(() => []) // 仅筛选出多选类型的问题
       
    },
  },
}
</script>

<template>
  <!-- 功能：搜尋 -->
  <div class="addModule">
    <button class="searchBtn btn" @click="searchQn()"><i class="fa-solid fa-magnifying-glass"></i></button>
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
          <th width="6%">編號</th>
          <th width="29%">問卷標題</th>
          <th width="8%">狀態</th>
          <th width="15%">開始時間</th>
          <th width="15%">結束時間</th>
          <th width="6%">填寫</th>
          <th width="6%">結果</th>
        </tr>
      </thead>
      <!-- <tbody v-for="(qn, index) in qnList" :key="qn.id"> -->
      <tbody v-for="(qn, index) in qnList.slice(pageStart, pageEnd)" :key="qn.id">
        <tr>
          <td>{{ qn.id }}</td> <!-- {{ index + 1 }}-->
          <td>{{ qn.title }}</td>
          <td>{{ getStatus(qn.published, qn.startDate, qn.endDate) }}</td>
          <td>{{ qn.startDate }}</td>
          <td>{{ qn.endDate }}</td>
          <td>
            <!-- <RouterLink to="/ans" class="edit btn"><i class="fa-solid fa-pen"></i></RouterLink> -->
            <button class="edit btn" @click="fillOut(qn)"><i class="fa-solid fa-pen"></i></button>
          </td>
          <td><button class="edit btn" @click=""><i class="fa-solid fa-chart-simple"></i></button></td>
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
  <!-- 填寫問卷 -->
  <div class="userflag" v-if="flag == 1">
    <div class="questionnaire">
      <p class="date">{{ obj.questionnaire.startDate }} ~ {{ obj.questionnaire.endDate }}</p>
      <div class="center">
        <p class="title">{{ obj.questionnaire.title }}</p>
        <p class="description">{{ obj.questionnaire.description }}</p>
      </div>
    </div>
    <div class="infoArea">
      <p class="infoTitle">基本資料</p>
      <div class="info">
        <div class="list">
          <label for="姓名">姓名：</label>
          <input type="text" name="" id="姓名" v-model="response.user.name">
          <label for="年齡">年齡：</label>
          <input type="text" name="" id="年齡" v-model="response.user.age">
        </div>
        <div class="list">
          <label for="電話">電話：</label>
          <input type="tel" name="" id="電話" v-model="response.user.phone_number">
          <label for="信箱">信箱：</label>
          <input type="email" name="" id="信箱" v-model="response.user.email">
        </div>
      </div>
    </div>
    <div class="questionArea">
      <!-- <p class="quTitle">題目</p> -->
      <div class="question" v-for="(qu, quIndex) in obj.question_list" :key="qu.id">
        <div class="top">
          <span class="quId">{{ qu.quId }}{{ '.' }}</span>
          <span class="qnTitle">{{ qu.qnTitle }}</span>
        </div>
        <div class="content" v-for="(opArr, opArrIndex) in opList">
          <div v-for="(op, opIndex) in opArr" v-if="(opArrIndex + 1) == qu.quId">
            <input type="radio" :name="'single_' + opArrIndex" :id="'answer_' + opIndex" :value="op" v-if="qu.optionType == 'single'" v-model="preAns[opArrIndex]">
            <input type="checkbox" :name="'multi_' + opArrIndex" :id="'answer_' + opArrIndex + '_' + opIndex" :value="op" v-if="qu.optionType == 'multi'" v-model="preAns[opArrIndex]" >
            <label :for="'answer_' + opIndex" v-if="qu.optionType != 'text'">{{ op }}</label>
            <textarea name="" id="" cols="15" rows="2" v-if="qu.optionType == 'text'" v-model="preAns.text_ans[opArrIndex]"></textarea>
          </div>
        </div>
      </div>
    </div>
    <div class="btnArea">
      <button class="btn" @click="flag = 0">取消</button>
      <button class="btn" @click="sendAns()">送出</button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
// 建立問卷
.addModule {
  height: 8vh;
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

  .edit {
    background-color: #5a85ce;
    margin: 0 5%;
  }
}

//分頁
.pagination {
  justify-content: center;
}

//填寫問卷
.userflag {
  width: 100%;
  height: 100vh;
  background-color: #D6E6F2;
  position: fixed;
  top: 0;
  left: 0;
  overflow-y: auto;
  padding: 2% 10%;

  .questionnaire {
    width: auto;
    margin: 1% 10%;
    color: #538cce;
    font-size: large;
    font-weight: bolder;
    display: flex;
    flex-direction: column;

    .date {
      margin-left: auto;
      margin-bottom: 1%;
      color: #769fcdb0;
    }

    .center {
      padding: 2% 2%;
      background-color: #F7FBFC;
      border-radius: 10px;
      text-align: center;

      .title {
        font-size: larger;
        font-weight: 700;
      }

      .description {
        color: #769fcdb0;
      }
    }
  }

  .infoArea {
    width: auto;
    margin: 1% 10%;
    color: #538cce;
    font-size: large;
    font-weight: bolder;
    display: flex;
    flex-direction: column;
    padding: 2% 2%;
    background-color: #F7FBFC;
    border-radius: 10px;
    text-align: center;

    .infoTitle {
      margin-right: auto;
      margin-bottom: 10px;
    }

    .info {
      width: 100%;
      display: flex;
      padding: 20px;
      box-shadow: inset 0 0 8px rgba(102, 175, 233, .3);
      border-radius: 10px;

      .list {
        width: 100%;
        list-style: none;
      }

      input {
        margin: 5px 5px;
        width: 70%;
      }
    }
  }

  .questionArea {
    width: auto;
    margin: 1% 10%;
    padding: 2% 2%;
    color: #538cce;
    font-size: large;
    font-weight: bolder;
    display: flex;
    flex-direction: column;
    background-color: #F7FBFC;
    border-radius: 10px;
    text-align: center;

    .quTitle {
      margin-right: auto;
      margin-bottom: 10px;
    }

    .question {
      background-color: #F7FBFC;
      padding: 20px;
      box-shadow: inset 0 0 8px rgba(102, 175, 233, .3);
      border-radius: 10px;
      margin: 10px 0;
      display: flex;
      flex-direction: column;


      .top {
        margin-right: auto;

        .quId {
          margin-right: 10px;
        }
      }

      .content {
        margin-right: auto;
      }
    }
  }

  .btnArea {
    padding: 0 30%;
    display: flex;
    justify-content: space-around;

    .btn {
      width: 100px;
      margin: 10px;
    }
  }
}
</style>
<script>
import { RouterLink } from "vue-router";

</script>

<template>
    <div>
        <span>問卷調查系統</span>
        <RouterLink to="/"><i class="fa-solid fa-house"></i></RouterLink>
        <RouterLink to="/questionnaire2"><i class="fa-solid fa-gear"></i></RouterLink>
    </div>
    
    


</template>

<style lang="scss" scoped>
div{
    position: fixed;
    top: 0;
    height: 9vh;
    width: 100%;
    background-color: #c8dcec;
    padding: 1% 5%;
    box-shadow: 0 0 8px rgba(102, 175, 233, .6);
    display: flex;
    justify-content: left;
    z-index: 99999;

    span{
        font-size: 18pt;
        font-weight: 700;
        color: #538cce;
        margin-right: 800px;
    }
}

i{
    font-size: 23pt;
    color: #769FCD;
    margin: 0 20px;
}


</style>

<template>
  <div id="login">
    <div style="width:300px; display:flex; flex-direction:column;align-items:center;margin:auto;">
      <b-form-input v-model="username"> </b-form-input>
      <b-form-input v-model="password"> </b-form-input>
      <b-button @click="submit">Prijavi se</b-button>
    </div>
  </div>  
</template>

<script>
import {login} from "@/apiCalls.js";

export default {
    name: "Login",
    components: {
      
    },
    data: function () {
      return {
        username:'mihailo',
        password: '1234'
      }
    },
    methods:{
      submit(){
        var self=this;
        login(this.username,this.password).then(function (response) {
            console.log("LOGOVAN",response.data.token);
            localStorage.setItem('token', response.data.token);
            self.$router.push({ name: 'Admin' })
          }).catch(function (error) {
            console.log("ERROR");
            console.log(error); 
            self.greska=true;
            self.poruka=error.response.data.error;
            console.log(self.poruka);
          });
               
      }
    }
}


</script>


<style scoped>

</style>
<template>
  <div class="admin">
    <nav class="navbar nav navbar-expand-sm navbar-dark bg-dark fixed-top pl-2">
      <div class="navbar-brand mr-2">
          <img src="@/assets/logo.png" alt="logo" width="60" height="60" >
      </div>
      <h1> mojSpasilac Admin panel</h1>
    </nav>
    <div class="admin-content">
      <div id="prijave">
        <h1>Lista prijava</h1>
        {{prijave}}
      </div>
      <Map id="map" v-bind:prijave="prijave" v-bind:vozila="vozila" />
      <div id="vozila">
        <h1>Lista vozila</h1>
        {{vozila}}
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import Map from "@/components/Map.vue";
import {getPrijave,getVozila} from "@/apiCalls.js";

export default {
  name: 'Admin',
  components: {
    Map
  },
  data:()=>{
    return{
      prijave:[],
      vozila:[]
    }
  },
  methods:{
    ucitajPrijave(){
      var self=this;
      getPrijave().then(function (response) {
            console.log("Prijave",response.data);
            self.prijave=response.data;
          }).catch(function (error) {
            console.log("ERROR");
            console.log(error); 
            self.greska=true;
            self.poruka=error.response.data.error;
            console.log(self.poruka);
          });
    },
    ucitajVozila(){
      var self=this;
      getVozila().then(function (response) {
            console.log("Vozila",response.data);
            self.vozila=response.data;
          }).catch(function (error) {
            console.log("ERROR");
            console.log(error); 
            self.greska=true;
            self.poruka=error.response.data.error;
            console.log(self.poruka);
          });
    },
  },
  mounted(){
    console.log("USO");
    this.ucitajPrijave();
    this.ucitajVozila();
  }
  
}
</script>

<style scoped>
body{
  padding-top:100px;
}
nav h1{
  color:white;
}
.admin-content{
  margin-top:86px;
  display: flex;
  flex-direction: row;
  height: 100%;
}
#prijave{
  flex-grow: 1;
  max-width: 300px;
}
#map{
  flex-grow:2;
}
#vozila{
  flex-grow:1;
  max-width: 300px;
}

</style>
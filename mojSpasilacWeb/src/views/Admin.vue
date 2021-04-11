<template>
  <div class="admin">
    <nav class="navbar nav navbar-expand-sm navbar-dark bg-dark fixed-top pl-2">
      <div class="navbar-brand mr-2">
          <img src="@/assets/logo.png" alt="logo" width="60" height="60" >
      </div>
      <h1> mojSpasilac Admin panel</h1>
      <div style="margin-left:300px">
        <b-form-select class="selekt" v-model="izbabranaPrijava" :options="prijaveOptions"></b-form-select>
        <b-form-select class="selekt" v-model="izbabranoVozilo" :options="vozilaOptions"></b-form-select>
        <b-button class="selekt" @click="dodeli()">Dodeli</b-button>
      </div>
    </nav>
    <div class="admin-content">
      <div id="prijave">
        <h1>Lista prijava</h1>
        <div v-for="prijava in prijave" :key="prijava.id_prijave*100000+prijava.cekanje" :class="{zauzeto : prijava.dodeljena}" style="border: 1px solid black">
          <p>prijava {{prijava.id_prijave}}</p>
          <p>{{formatirajVreme(prijava.cekanje)}}</p>
        </div>
        <!-- {{prijave}} -->
      </div>
        <Map id='map'  v-bind:prijave="prijave" v-bind:vozila="vozila" />
      <div id="vozila">
        <h1>Lista vozila</h1>
        <div v-for="vozilo in vozila" :key="vozilo.id_vozila" :class="{zauzeto : vozilo.prijava!=null}" style="border: 1px solid black">
          <p>{{vozilo.username}}</p>
          <p>{{vozilo.kapacitet}}</p>
        </div>
        <!-- {{vozila}} -->
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import Map from "@/components/Map.vue";
import {getPrijave,getVozila,dodeliVoziluPrijavu} from "@/apiCalls.js";

export default {
  name: 'Admin',
  components: {
    Map
  },
  data:()=>{
    return{
      prijave:[],
      vozila:[],
      vozilaOptions:[],
      prijaveOptions:[],
      izbabranoVozilo:undefined,
      izbabranaPrijava:undefined
    }
  },
  computed:{
    vreme(){
      return new Date();
    }
  },
  methods:{
    formatirajVreme(razlika){
      var sekunde=razlika%60;
      var minuti=parseInt(razlika/60);
      var sati=parseInt(minuti/60);
      minuti=minuti%60;
      if(sekunde<10)
        sekunde="0"+sekunde;
      if(minuti<10)
        minuti="0"+minuti;
      return sati+":"+minuti+":"+sekunde;
    },
    obradiPrijave(){
      console.log("POZVAO");
      for(var prijava of this.prijave){
        var razlika=new Date()-new Date(prijava.vreme)
        razlika=parseInt(razlika/1000);
        prijava.cekanje=razlika;
        if(prijava.dodeljena==undefined)
          prijava.dodeljena=false;
      }
      this.obradiVozila();
    },
    obradiPrijave2(){
      this.prijave.push();
      //this.prijave.pop();
      for(var prijava of this.prijave){
        prijava.cekanje++;
      }
    },
    obradiVozila(){
      this.vozilaOptions=[];
      this.prijaveOptions=[];
      for(var vozilo of this.vozila){
        if(vozilo.prijava!=null)
        {
          for(var prijava of this.prijave){
            if(prijava.id_prijave==vozilo.prijava.id_prijave)
              prijava.dodeljena=true;
          }
          /*
          console.log(vozilo.prijava.id_prijave);
          console.log(this.prijave.find( prijava => prijava.id_prijave==vozilo.prijava.id_prijave ))
          var p=this.prijave.find( prijava => prijava.id_prijave==vozilo.prijava.id_prijave );
          (this.prijave.find( prijava => prijava.id_prijave==vozilo.prijava.id_prijave )).dodeljena=true;
          console.log(p);
          if(p!=undefined)
          {
            console.log("USO");
            p.dodeljena=true;
          }*/
        }
        else{
          this.vozilaOptions.push({value:vozilo.id_vozila,text:vozilo.username+" "+vozilo.kapacitet})
        }
      }
      for(var p of this.prijave)
      {
        if(p.dodeljena==false)
          this.prijaveOptions.push({value:p.id_prijave,text:"prijava"+p.id_prijave})
      }
    },
    ucitajPrijave(){
      console.log("UcitajPrijave");
      var self=this;
      getPrijave().then(function (response) {
            console.log("Prijave",response.data);
            self.prijave=response.data;
            self.obradiPrijave();
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
            self.obradiVozila();
          }).catch(function (error) {
            console.log("ERROR");
            console.log(error); 
            self.greska=true;
            self.poruka=error.response.data.error;
            console.log(self.poruka);
          });
      },
      
    refresh(){
      this.ucitajPrijave();
      this.ucitajVozila();  
    },
      dodeli(){
        var self=this;
        dodeliVoziluPrijavu(this.izbabranoVozilo,this.izbabranaPrijava).then(function (response) {
            console.log("DODELIO",response);
            self.refresh();
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
    setInterval(()=>{
      this.ucitajPrijave();
      this.ucitajVozila();
    }, 10000);
    setInterval(()=>{this.obradiPrijave2()},1000);
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
  display: flex;
  flex-direction: column;
  flex-grow:2;
}
#vozila{
  flex-grow:1;
  max-width: 300px;
}

.zauzeto{
  background-color:lightseagreen;
}

.selekt{
  width:200px;
  height:50px;
  margin-left: 20px;
}

</style>
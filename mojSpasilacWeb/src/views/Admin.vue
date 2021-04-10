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
      </div>
      <Map id="map"/>
      <div id="vozila">
        <h1>Lista vozila</h1>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import Map from "@/components/Map.vue";

export default {
  name: 'Admin',
  components: {
    Map
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
}
#map{
  flex-grow:1;
}
#vozila{
  flex-grow:1;
}

</style>
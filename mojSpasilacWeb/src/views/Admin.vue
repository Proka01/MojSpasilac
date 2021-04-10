<template>
  <div class="admin">
    <nav class="navbar nav navbar-expand-sm navbar-dark bg-dark fixed-top pl-2">
      <div class="navbar-brand mr-2">
          <img src="@/assets/logo.png" alt="logo" width="60" height="60" >
      </div>
      <h1> mojSpasilac Admin panel</h1>
    </nav>
    <Map />
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
h1{
  color:white;
}
</style>
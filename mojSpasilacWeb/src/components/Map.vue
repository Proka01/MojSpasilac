<template>
  <div class="map">
    <GmapMap
        :center="center"
        :zoom="14"
    >
        <GmapMarker v-for="prijava in prijave" :key="'prijava'+prijava.id_prijave"
            :position="{lat: prijava.lokacija_prijave_x, lng:prijava.lokacija_prijave_y}"
            label="prijava"
            color="blue"
            icon="https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png"
        />
        <GmapMarker v-for="vozilo in vozila" :key="'vozilo'+vozilo.id_vozila"
            :position="{lat: vozilo.lokacija_vozila_x, lng:vozilo.lokacija_vozila_y}"
            label="vozilo"
        />
    </GmapMap>
  </div>
</template>

<script>
import {gmapApi} from 'vue2-google-maps';

export default {
  name: 'Map',
  props:{
    prijave: Array,
    vozila: Array,
  },
  data () {
      return {
          center: {
              lat: 44.82219437635823,
              lng: 20.462618743751147
          },
          markerPosition: {
              lat: 12.9716,
              lng: 77.5946
          }
      }
  },
  computed: {
    google: gmapApi,
  },
  async mounted() {
  },
};
</script>

<style>

.map {
  width: 60%;
  height: 100%;
}
.vue-map-container{
  
  width: 100%;
  height: 70vh;
  max-height: 100%;
}

</style>
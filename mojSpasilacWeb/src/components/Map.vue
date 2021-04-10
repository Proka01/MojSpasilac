<template>
  <div class="map">
    <GmapMap
        :center="center"
        :zoom="14"
    >
        <GmapMarker
            :position="markerPosition"
        />
    </GmapMap>
  </div>
</template>

<script>

export default {
  name: 'Map',
  data () {
      return {
          center: {
              lat: 12.9716,
              lng: 77.5946
          },
          markerPosition: {
              lat: 12.9716,
              lng: 77.5946
          }
      }
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
  height: 80vh;
  max-height: 100%;
}

</style>
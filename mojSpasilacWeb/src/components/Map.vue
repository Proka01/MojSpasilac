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
html,
body {
  margin: 0;
  padding: 0;
}

.map {
  width: 500px;
  height: 500px;
}
</style>
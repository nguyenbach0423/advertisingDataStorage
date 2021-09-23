<template>
  <div class="section">
    <div class="container">

      <UploadFile/>

    </div>
  </div>
</template>

<script>
import UploadFile from './components/UploadFile.vue'

export default {
  name: 'App',
  components: {
    UploadFile
  }
}
</script>

<style>
#app {
  display: grid;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  margin-top: 150px;
}
.container {
  display: grid;
  text-align: center;
  place-items: center;
}
</style>

<template>
  <div v-if="mode === modes.HEADER" style="display: flex; flex-direction: row; align-items: center">
      <input placeholder="username">
      <input placeholder="password">
      <v-btn @click="() => { this.$router.push('/login') }">Login</v-btn>
  </div>
  <div v-else-if="mode === modes.PAGE" style="display: flex; flex-direction: column; align-items: center">
    <input placeholder="username">
    <input placeholder="password">
    <v-btn>Login</v-btn>
  </div>
</template>

<script>

import { Vue, Component, Prop } from 'nuxt-property-decorator'
export default class LoginHeader extends Vue {
  @Prop({default: this.modes.HEADER}) mode

  modes = {
    HEADER: 'header',
    PAGE: 'page'
  }

}
</script>

<style>
input, v-btn {
  max-width: 100px;
  color: white;
  margin-left: 5px;
  margin-right: 5px;
}
</style>
<script setup>
</script>

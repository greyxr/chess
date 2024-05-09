<template>
    <v-card style="display: flex; flex-direction: column;">
      <v-card-title style="align-self: center">
        Please login to continue.
      </v-card-title>
      <login-header />
    </v-card>
</template>

<script>
import {Vue} from "nuxt-property-decorator";

export default class LoginPage extends Vue {
}
</script>

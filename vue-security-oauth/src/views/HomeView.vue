<template>
  <div>
    <h1>Home Page</h1>
<!--    <RouterLink to="/signin">Go to Auth</RouterLink>-->
    <my-button @click="$router.push('/signin')">Go to Auth</my-button>
  </div>
</template>

<script>
import MyButton from "@/components/UI/MyButton.vue";

export default {
  components: {MyButton},
}
</script>

<style lang="scss" scoped>

</style>

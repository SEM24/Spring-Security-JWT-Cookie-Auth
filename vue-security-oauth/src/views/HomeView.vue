<template>
  <div>
    <h2>Home Page</h2>
    <RouterLink to="/signin">Go to Auth</RouterLink>
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

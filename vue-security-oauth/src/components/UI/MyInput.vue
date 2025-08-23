<script>
export default {
  name: "my-input",
  props: {
    modelValue: [String, Number],
  },
  methods: {
    updateInput(e) {
      this.$emit("update:modelValue", e.target.value)
    }
  }
}
</script>

<template>
  <input class="input" type="text" :value="modelValue" @input="updateInput">
</template>

<style scoped>
.input-wrapper {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.input {
  padding: 12px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.2s ease;
  outline: none;
}

.input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
}

.error {
  margin-top: 6px;
  color: #e53e3e;
  font-size: 13px;
}
</style>

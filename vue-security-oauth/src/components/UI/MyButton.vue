<script>
export default {
  name: "my-button"
}
</script>

<template>
  <button class="btn">
    <slot></slot>
  </button>
</template>

<style scoped>
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  transition: all 0.3s ease;
}

.btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #5a67d8, #6b46c1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

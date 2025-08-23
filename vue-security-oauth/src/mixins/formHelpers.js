export default {
  data() {
    return {
      message: "",
      messageType: "",
      errors: {}
    }
  },
  computed: {
    messageClass() {
      return {
        'message-success': this.messageType === 'success',
        'message-error': this.messageType === 'error',
      }
    }
  },
  methods: {
    showMessage(msg, type) {
      this.message = msg
      this.messageType = type
      setTimeout(() => {
        this.clearMessage()
      }, 5000)
    },
    clearErrors() {
      this.errors = {}
    },
    clearMessage() {
      this.message = ""
      this.messageType = ""
    },
  }
}

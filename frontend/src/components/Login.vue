<template>
  <div>
    <div class="login">
      <input class="form-control" style="width: 150px" type="text" v-model="id" placeholder="ID">
      <input class="form-control" style="width: 150px; margin-top: 10px" type="password" v-model="password" placeholder="PASSWORD">
      <button class="btn btn-primary" style="width: 150px; margin-top: 10px" @click="loginSubmit">LOGIN</button>
    </div>

  </div>
</template>

<script>
  const header = {headers: {"Content-type": "application/json"}};

  export default {
  name: 'Login',
  data: function () {
    return {
      id: '',
      password: ''
    }
  },
  methods: {
    loginSubmit: function () {
      if (!this.id) {
        alert("아이디를 입력해주세요.");
        return;
      }
      if (!this.password) {
        alert("비밀번호를 입력해주세요.");
        return;
      }

      const params = {
        id: this.id,
        password: this.password
      }

      this.$axios.post('/api/login', params, header)
        .then((response) => {
          if (response.status === 200) {
            const token = response.headers['jwt-auth-token'];
            this.$cookie.set("token", token);
            const data = response.data;
            this.$cookie.set("user", JSON.stringify(response.data));
            this.$router.replace('/doc');
          } else {
            const result = JSON.stringify(response.data)
            alert(result)
          }
        })
        .catch((ex) => {
          console.log(ex);
        })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .login {
    position: absolute;
    width: 100%;
    top: 50%;
    left: 45%;
  }
</style>

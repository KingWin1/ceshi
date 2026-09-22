<template>
  <div class="login-page">
    <!-- ========== 顶部导航栏 ========== -->
    <div class="login-header">
      <div class="header-left">
        <img class="logo-icon" src="@/assets/xitongtubiao.png" alt="小区通" />
        <span class="logo-divider">|</span>
        <span class="logo-subtitle">智慧社区管理平台</span>
      </div>
    </div>

    <!-- ========== 主体内容 ========== -->
    <div class="login-main">
      <div class="login-card">
        <!-- 左侧蓝色宣传区 -->
        <div class="login-left">
          <!-- 装饰圆形 -->
          <div class="circle circle-top-right"></div>
          <div class="circle circle-center-right"></div>
          <div class="circle circle-bottom-left"></div>

          <h1 class="left-title">智慧社区，美好生活</h1>
          <p class="left-subtitle">一站式物业管理与社区服务 · 多端覆盖 · 智能高效</p>

          <div class="feature-grid">
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="20" height="20">
                  <path d="M3 11L12 3L21 11V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V11Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M9 22V14H15V22" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="feature-text">房产管理</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="20" height="20">
                  <path d="M12 2V22" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
                  <path d="M17 6H9.5C8.83696 6 8.20107 6.26339 7.73223 6.73223C7.26339 7.20107 7 7.83696 7 8.5C7 9.16304 7.26339 9.79893 7.73223 10.2678C8.20107 10.7366 8.83696 11 9.5 11H14.5C15.163 11 15.7989 11.2634 16.2678 11.7322C16.7366 12.2011 17 12.837 17 13.5C17 14.163 16.7366 14.7989 16.2678 15.2678C15.7989 15.7366 15.163 16 14.5 16H5" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="feature-text">财务管理</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="20" height="20">
                  <path d="M14.7 6.3C15.0869 6.68701 15.3936 7.14633 15.6025 7.65148C15.8114 8.15664 15.9184 8.69762 15.9174 9.243C15.9164 9.78838 15.8074 10.329 15.5966 10.8331C15.3858 11.3372 15.0774 11.7978 14.69 12.185L5 21.875L2.125 19L11.815 9.31C12.2022 8.92257 12.6628 8.61421 13.1669 8.40338C13.671 8.19255 14.2116 8.08357 14.757 8.08257C15.3024 8.08157 15.8434 8.18864 16.3485 8.39751C16.8537 8.60638 17.313 8.91306 17.7 9.3L20.3 6.7C19.5242 5.92423 18.5372 5.38288 17.4607 5.14293C16.3842 4.90298 15.2651 4.97498 14.23 5.343L11.5 8.073L14.7 6.3Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="feature-text">工单服务</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="20" height="20">
                  <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="9" cy="7" r="4" stroke="white" stroke-width="1.5"/>
                  <path d="M23 21V19C22.9993 18.1137 22.7044 17.2528 22.1614 16.5523C21.6184 15.8519 20.8581 15.3516 20 15.13" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M16 3.13C16.8604 3.35031 17.623 3.85071 18.1676 4.55232C18.7122 5.25392 19.0078 6.11683 19.0078 7.005C19.0078 7.89318 18.7122 8.75608 18.1676 9.45769C17.623 10.1593 16.8604 10.6597 16 10.88" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="feature-text">居民服务</span>
            </div>
          </div>
        </div>

        <!-- 右侧白色登录区 -->
        <div class="login-right">
          <h2 class="form-title">账号登录</h2>

          <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
            <!-- 用户名 -->
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="admin"
                prefix-icon="el-icon-user-solid"
                class="form-input"
                size="medium"
                @keyup.enter.native="handleLogin"
              />
            </el-form-item>

            <!-- 密码 -->
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="******"
                prefix-icon="el-icon-lock"
                show-password
                class="form-input"
                size="medium"
                @keyup.enter.native="handleLogin"
              />
            </el-form-item>

            <!-- 验证码 -->
            <el-form-item prop="code">
              <div class="captcha-row">
                <el-input
                  v-model="loginForm.code"
                  placeholder="请输入验证码"
                  prefix-icon="el-icon-key"
                  class="captcha-input"
                  size="medium"
                  maxlength="4"
                  @keyup.enter.native="handleLogin"
                />
                <div class="captcha-image" @click="refreshCaptcha">
                  <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                  <span v-else>点击获取</span>
                </div>
              </div>
            </el-form-item>

            <!-- 忘记密码 -->
            <div class="forgot-row">
              <span class="forgot-link">忘记密码？</span>
            </div>

            <!-- 登录按钮 -->
            <el-form-item>
              <el-button
                type="primary"
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <!-- ========== 底部版权 ========== -->
    <div class="login-footer">
      <span>© 2026 小区通 · 智慧社区管理平台</span>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import Api from '@/api'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        code: '',
        uuid: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },
      captchaImage: '',
      loading: false
    }
  },
  created() {
    this.refreshCaptcha()
  },
  methods: {
    ...mapActions('user', ['login']),

    // 刷新验证码
    async refreshCaptcha() {
      try {
        const res = await Api.getCaptcha()
        // 后端返回已带 data:image 前缀则直接使用，否则补齐
        const raw = res.data.image || res.data.captchaImg || ''
        this.captchaImage = raw.startsWith('data:image') ? raw : 'data:image/png;base64,' + raw
        this.loginForm.uuid = res.data.uuid || res.data.uuidKey || ''
      } catch (e) {
        this.$message.error('验证码获取失败')
      }
    },

    // 登录
    handleLogin() {
      // 请求进行中忽略重复触发（如连续回车）
      if (this.loading) return
      this.$refs.loginForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await this.login(this.loginForm)
          this.$message.success('登录成功')
          this.$router.push('/home')
        } catch (e) {
          // 登录失败，刷新验证码
          this.refreshCaptcha()
          this.loginForm.code = ''
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/login/Login.less';
</style>

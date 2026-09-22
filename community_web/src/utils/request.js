import axios from 'axios'
import { Message } from 'element-ui'

// 创建 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 登录失效处理：清除登录态并跳转登录页（hash 路由，需刷新以重置 store）
let redirecting = false
function handleUnauthorized(msg) {
  Message.error(msg || '登录已过期，请重新登录')
  if (redirecting) {
    return
  }
  redirecting = true
  localStorage.removeItem('token')
  localStorage.removeItem('admin')
  localStorage.removeItem('menuIds')
  window.location.hash = '#/login'
  window.location.reload()
}

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 后端 Token 校验失败以 HTTP 200 + code "401" 返回
    if (res.code === '401' || res.code === 401) {
      handleUnauthorized(res.msg)
      return Promise.reject(new Error(res.msg || '登录已过期，请重新登录'))
    }

    // 判断响应码（后端统一返回 "00000" 表示成功，消息字段为 msg）
    if (res.code && res.code !== '00000' && res.code !== 200) {
      Message.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }

    // 返回数据
    return res
  },
  error => {
    console.error('响应错误：', error)

    // 处理 401 未授权
    if (error.response && error.response.status === 401) {
      handleUnauthorized()
    } else {
      Message.error(error.message || '网络异常，请重试')
    }

    return Promise.reject(error)
  }
)

// 导出 request 方法
export function request(config) {
  return service(config)
}

// 导出 get 方法
export function get(url, params = {}) {
  return service({
    method: 'get',
    url,
    params
  })
}

// 导出 post 方法
export function post(url, data = {}) {
  return service({
    method: 'post',
    url,
    data
  })
}

// 导出 put 方法
export function put(url, data = {}) {
  return service({
    method: 'put',
    url,
    data
  })
}

// 导出 delete 方法
export function del(url, params = {}) {
  return service({
    method: 'delete',
    url,
    params
  })
}

// 默认导出
export default service

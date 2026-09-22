<template>
  <el-dialog
    :visible="visible"
    :title="isEdit ? '编辑公告' : '新增公告'"
    width="900px"
    top="5vh"
    append-to-body
    destroy-on-close
    @open="onOpen"
    @close="onClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px" class="notice-form">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" clearable />
      </el-form-item>
      <el-form-item label="封面图">
        <el-upload
          list-type="picture-card"
          accept="image/jpeg,image/png"
          :file-list="coverFileList"
          :limit="1"
          :before-upload="beforeCoverUpload"
          :http-request="doCoverUpload"
          :on-remove="onCoverRemove"
          :on-exceed="onCoverExceed"
        >
          <i class="el-icon-plus" />
        </el-upload>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <div class="editor-wrap">
          <toolbar
            class="editor-toolbar"
            :editor="editorRef"
            :default-config="toolbarConfig"
            mode="default"
          />
          <editor
            v-model="form.content"
            class="editor-body"
            :default-config="editorConfig"
            mode="default"
            @on-created="onEditorCreated"
          />
        </div>
      </el-form-item>
      <el-form-item label="是否置顶">
        <el-switch v-model="form.isTop" :active-value="1" :inactive-value="2" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">已发布</el-radio>
          <el-radio :label="2">已下架</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="发布时间">
        <el-date-picker
          v-model="form.publishTime"
          type="datetime"
          placeholder="选择发布时间"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import Api from '@/api'

// 表单初始值
const emptyForm = () => ({
  noticeId: null,
  title: '',
  content: '',
  coverUrl: '',
  isTop: 2,
  status: 1,
  publishTime: ''
})

export default {
  name: 'NoticeAddOrUpdate',
  components: { Editor, Toolbar },
  props: {
    visible: { type: Boolean, default: false },
    // 编辑时传入公告ID，为空表示新增
    noticeId: { type: Number, default: null }
  },
  data() {
    return {
      submitting: false,
      form: emptyForm(),
      // 封面图文件列表（回显用）
      coverFileList: [],
      // 富文本编辑器实例（组件销毁时需调用 destroy）
      editorRef: null,
      toolbarConfig: {},
      // 编辑器配置：占位提示 + 图片上传走统一文件上传接口
      editorConfig: {
        placeholder: '请输入公告内容...',
        MENU_CONF: {
          uploadImage: {
            customUpload: async (file, insertFn) => {
              try {
                const formData = new FormData()
                formData.append('files', file)
                const res = await Api.uploadFile(formData)
                const url = (res.data || [])[0]
                if (url) {
                  insertFn(url, '', '')
                }
              } catch (e) {
                // 拦截器已提示
              }
            }
          }
        }
      },
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isEdit() {
      return !!this.noticeId
    }
  },
  beforeDestroy() {
    // 组件销毁前必须销毁编辑器实例，防止内存泄漏
    if (this.editorRef) {
      this.editorRef.destroy()
    }
  },
  methods: {
    // 弹窗打开：新增重置表单，编辑调详情接口回显
    onOpen() {
      this.form = emptyForm()
      this.coverFileList = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
      if (this.isEdit) {
        this.fetchDetail()
      }
    },

    // 根据ID查询公告信息（编辑回显）
    async fetchDetail() {
      try {
        const res = await Api.getNoticeById(this.noticeId)
        const data = res.data || {}
        this.form = {
          noticeId: data.noticeId,
          title: data.title || '',
          content: data.content || '',
          coverUrl: data.coverUrl || '',
          isTop: data.isTop || 2,
          status: data.status || 1,
          publishTime: data.publishTime || ''
        }
        if (data.coverUrl) {
          this.coverFileList = [{ name: '封面图', url: data.coverUrl }]
        }
      } catch (e) {
        // 拦截器已提示
      }
    },

    onEditorCreated(editor) {
      this.editorRef = editor
    },

    // 封面图上传前校验类型与大小
    beforeCoverUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].indexOf(file.type) > -1
      if (!isImage) {
        this.$message.warning('只能上传 JPG/PNG 格式的封面图')
        return false
      }
      const isLt5M = file.size / 1024 / 1024 <= 5
      if (!isLt5M) {
        this.$message.warning('封面图不能超过 5MB')
        return false
      }
      return true
    },

    // 自定义上传：调用文件上传接口，成功后记录封面地址
    doCoverUpload({ file }) {
      const formData = new FormData()
      formData.append('files', file)
      return Api.uploadFile(formData)
        .then(res => {
          const paths = res.data || []
          if (paths.length) {
            this.form.coverUrl = paths[0]
            this.coverFileList = [{ name: '封面图', url: paths[0] }]
          }
        })
        .catch(() => {
          // 拦截器已提示
        })
    },

    onCoverRemove() {
      this.form.coverUrl = ''
      this.coverFileList = []
    },

    onCoverExceed() {
      this.$message.warning('封面图最多上传 1 张')
    },

    // 提交：新增调添加公告接口，编辑调修改公告接口
    onSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid || this.submitting) {
          return
        }
        this.submitting = true
        try {
          const data = Object.assign({}, this.form)
          if (!data.publishTime) {
            delete data.publishTime
          }
          if (this.isEdit) {
            await Api.updateNotice(data)
            this.$message.success('修改成功')
          } else {
            delete data.noticeId
            await Api.addNotice(data)
            this.$message.success('添加成功')
          }
          this.$emit('success')
          this.onClose()
        } catch (e) {
          // 拦截器已提示
        } finally {
          this.submitting = false
        }
      })
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/notice/add-or-update.less';
</style>

<template>
  <el-dialog
    :visible="visible"
    :title="isEdit ? '编辑广告' : '新增广告'"
    width="640px"
    append-to-body
    destroy-on-close
    @open="onOpen"
    @close="onClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" clearable />
      </el-form-item>
      <el-form-item label="广告图片">
        <el-upload
          list-type="picture-card"
          accept="image/jpeg,image/png"
          :file-list="fileList"
          :limit="1"
          :before-upload="beforeUpload"
          :http-request="doUpload"
          :on-remove="onRemove"
          :on-exceed="onExceed"
        >
          <i class="el-icon-plus" />
        </el-upload>
      </el-form-item>
      <el-form-item label="广告类型" prop="adType">
        <el-select v-model="form.adType" class="w-full" placeholder="请选择广告类型">
          <el-option v-for="t in adTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="链接类型">
        <el-radio-group v-model="form.linkType">
          <el-radio :label="1">网页</el-radio>
          <el-radio :label="2">App内页</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="链接地址">
        <el-input v-model="form.linkUrl" placeholder="请输入链接地址" maxlength="255" clearable />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sort" :min="0" :max="9999" class="w-full" />
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

// 表单初始值
const emptyForm = () => ({
  adId: null,
  title: '',
  imageUrl: '',
  adType: null,
  linkType: 1,
  linkUrl: '',
  sort: 0
})

export default {
  name: 'AdvertisementAddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    // 编辑时传入广告ID，为空表示新增
    adId: { type: Number, default: null }
  },
  data() {
    return {
      submitting: false,
      form: emptyForm(),
      fileList: [],
      // 广告类型枚举：1-启动页 2-首页轮播图 3-弹窗广告
      adTypeOptions: [
        { value: 1, label: '启动页' },
        { value: 2, label: '首页轮播图' },
        { value: 3, label: '弹窗广告' }
      ],
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        adType: [{ required: true, message: '请选择广告类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    isEdit() {
      return !!this.adId
    }
  },
  methods: {
    // 弹窗打开：新增重置表单，编辑调详情接口回显
    onOpen() {
      this.form = emptyForm()
      this.fileList = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
      if (this.isEdit) {
        this.fetchDetail()
      }
    },

    // 根据ID查询广告信息（编辑回显）
    async fetchDetail() {
      try {
        const res = await Api.getAdvertisementById(this.adId)
        const data = res.data || {}
        this.form = {
          adId: data.adId,
          title: data.title || '',
          imageUrl: data.imageUrl || '',
          adType: data.adType,
          linkType: data.linkType || 1,
          linkUrl: data.linkUrl || '',
          sort: data.sort == null ? 0 : data.sort
        }
        if (data.imageUrl) {
          this.fileList = [{ name: '广告图片', url: data.imageUrl }]
        }
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 上传前校验类型与大小
    beforeUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].indexOf(file.type) > -1
      if (!isImage) {
        this.$message.warning('只能上传 JPG/PNG 格式的图片')
        return false
      }
      const isLt5M = file.size / 1024 / 1024 <= 5
      if (!isLt5M) {
        this.$message.warning('图片不能超过 5MB')
        return false
      }
      return true
    },

    // 自定义上传：调用文件上传接口，成功后记录图片地址
    doUpload({ file }) {
      const formData = new FormData()
      formData.append('files', file)
      return Api.uploadFile(formData)
        .then(res => {
          const paths = res.data || []
          if (paths.length) {
            this.form.imageUrl = paths[0]
            this.fileList = [{ name: '广告图片', url: paths[0] }]
          }
        })
        .catch(() => {
          // 拦截器已提示
        })
    },

    onRemove() {
      this.form.imageUrl = ''
      this.fileList = []
    },

    onExceed() {
      this.$message.warning('广告图片最多上传 1 张')
    },

    // 提交：新增调添加广告接口，编辑调修改广告接口
    onSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid || this.submitting) {
          return
        }
        this.submitting = true
        try {
          const data = Object.assign({}, this.form)
          if (this.isEdit) {
            await Api.updateAdvertisement(data)
            this.$message.success('修改成功')
          } else {
            delete data.adId
            await Api.addAdvertisement(data)
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
@import '@/assets/styles/advertisement/add-or-update.less';
</style>

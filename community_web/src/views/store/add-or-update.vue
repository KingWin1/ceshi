<template>
  <el-dialog
    :visible="visible"
    :title="isEdit ? '编辑门店' : '新增门店'"
    width="800px"
    top="5vh"
    append-to-body
    destroy-on-close
    @open="onOpen"
    @close="onClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="门店名称" prop="storeName">
            <el-input v-model="form.storeName" placeholder="请输入门店名称" maxlength="50" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="门店分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择门店分类" class="w-full">
              <el-option v-for="c in categoryList" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="20" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="营业时间">
            <div class="time-picker-wrap">
              <el-time-picker
                v-model="form.openTime"
                placeholder="开始时间"
                value-format="HH:mm:ss"
                class="time-picker"
              />
              <span class="time-separator">-</span>
              <el-time-picker
                v-model="form.closeTime"
                placeholder="结束时间"
                value-format="HH:mm:ss"
                class="time-picker"
              />
            </div>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="详细地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入详细地址" maxlength="255" clearable />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="经度">
            <el-input-number v-model="form.longitude" :precision="7" :step="0.0000001" class="w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="纬度">
            <el-input-number v-model="form.latitude" :precision="7" :step="0.0000001" class="w-full" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="门店Logo">
        <el-upload
          list-type="picture-card"
          accept="image/jpeg,image/png,image/gif"
          :file-list="logoFileList"
          :limit="1"
          :before-upload="beforeLogoUpload"
          :http-request="doLogoUpload"
          :on-remove="onLogoRemove"
          :on-exceed="onLogoExceed"
        >
          <i class="el-icon-plus" />
        </el-upload>
        <div class="upload-tip">建议尺寸: 200x200px, 支持 .jpg/.png/.gif 格式</div>
      </el-form-item>

      <el-form-item label="门店图片">
        <el-upload
          list-type="picture-card"
          accept="image/jpeg,image/png,image/gif"
          :file-list="imageFileList"
          :limit="5"
          :before-upload="beforeImageUpload"
          :http-request="doImageUpload"
          :on-remove="onImageRemove"
          :on-exceed="onImageExceed"
        >
          <i class="el-icon-plus" />
        </el-upload>
        <div class="upload-tip">最多上传5张, 建议尺寸: 750x420px, 支持 .jpg/.png/.gif 格式, 单张不超过5MB</div>
      </el-form-item>

      <el-form-item label="门店介绍">
        <div class="editor-wrap">
          <toolbar
            class="editor-toolbar"
            :editor="editorRef"
            :default-config="toolbarConfig"
            mode="default"
          />
          <editor
            v-model="form.introduction"
            class="editor-body"
            :default-config="editorConfig"
            mode="default"
            @on-created="onEditorCreated"
          />
        </div>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio :label="1">营业中</el-radio>
              <el-radio :label="2">已下架</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="推荐">
            <el-radio-group v-model="form.isRecommend">
              <el-radio :label="1">推荐</el-radio>
              <el-radio :label="2">普通</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="排序">
            <el-input-number v-model="form.sort" :min="0" :max="9999" class="w-full" />
          </el-form-item>
        </el-col>
      </el-row>
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
  storeId: null,
  storeName: '',
  categoryId: null,
  contactPhone: '',
  address: '',
  openTime: '',
  closeTime: '',
  longitude: null,
  latitude: null,
  logoUrl: '',
  imageUrl: '',
  introduction: '',
  status: 1,
  isRecommend: 2,
  sort: 0
})

export default {
  name: 'StoreAddOrUpdate',
  components: { Editor, Toolbar },
  props: {
    visible: { type: Boolean, default: false },
    // 编辑时传入门店ID，为空表示新增
    storeId: { type: Number, default: null }
  },
  data() {
    return {
      submitting: false,
      form: emptyForm(),
      categoryList: [],
      logoFileList: [],
      imageFileList: [],
      // 已上传完成的图片地址集合
      imageUrls: [],
      editorRef: null,
      toolbarConfig: {},
      editorConfig: {
        placeholder: '请输入门店介绍...',
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
        storeName: [{ required: true, message: '请输入门店名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择门店分类', trigger: 'change' }],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isEdit() {
      return !!this.storeId
    }
  },
  beforeDestroy() {
    if (this.editorRef) {
      this.editorRef.destroy()
    }
  },
  methods: {
    // 弹窗打开：新增重置表单，编辑调详情接口回显
    async onOpen() {
      this.form = emptyForm()
      this.logoFileList = []
      this.imageFileList = []
      this.imageUrls = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
      // 加载分类列表
      await this.fetchCategoryList()
      if (this.isEdit) {
        await this.fetchDetail()
      }
    },

    // 查询全部的门店分类信息
    async fetchCategoryList() {
      try {
        const res = await Api.getStoreCategoryListAll()
        this.categoryList = res.data || []
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 根据门店ID查询门店信息（编辑回显）
    async fetchDetail() {
      try {
        const res = await Api.getStoreById(this.storeId)
        const data = res.data || {}
        this.form = {
          storeId: data.storeId,
          storeName: data.storeName || '',
          categoryId: data.categoryId,
          contactPhone: data.contactPhone || '',
          address: data.address || '',
          openTime: data.openTime || '',
          closeTime: data.closeTime || '',
          longitude: data.longitude,
          latitude: data.latitude,
          logoUrl: data.logoUrl || '',
          imageUrl: data.imageUrl || '',
          introduction: data.introduction || '',
          status: data.status || 1,
          isRecommend: data.isRecommend || 2,
          sort: data.sort == null ? 0 : data.sort
        }
        if (data.logoUrl) {
          this.logoFileList = [{ name: '门店Logo', url: data.logoUrl }]
        }
        if (data.imageUrl) {
          const urls = data.imageUrl.split(',').filter(s => s)
          this.imageFileList = urls.map((url, i) => ({ name: `门店图片${i + 1}`, url }))
          this.imageUrls = urls
        }
      } catch (e) {
        // 拦截器已提示
      }
    },

    onEditorCreated(editor) {
      this.editorRef = editor
    },

    // Logo 上传前校验
    beforeLogoUpload(file) {
      const isImage = ['image/jpeg', 'image/png', 'image/gif'].indexOf(file.type) > -1
      if (!isImage) {
        this.$message.warning('只能上传 JPG/PNG/GIF 格式的图片')
        return false
      }
      const isLt5M = file.size / 1024 / 1024 <= 5
      if (!isLt5M) {
        this.$message.warning('图片不能超过 5MB')
        return false
      }
      return true
    },

    doLogoUpload({ file }) {
      const formData = new FormData()
      formData.append('files', file)
      return Api.uploadFile(formData)
        .then(res => {
          const paths = res.data || []
          if (paths.length) {
            this.form.logoUrl = paths[0]
            this.logoFileList = [{ name: '门店Logo', url: paths[0] }]
          }
        })
        .catch(() => {
          // 拦截器已提示
        })
    },

    onLogoRemove() {
      this.form.logoUrl = ''
      this.logoFileList = []
    },

    onLogoExceed() {
      this.$message.warning('门店Logo最多上传 1 张')
    },

    // 门店图片上传前校验
    beforeImageUpload(file) {
      const isImage = ['image/jpeg', 'image/png', 'image/gif'].indexOf(file.type) > -1
      if (!isImage) {
        this.$message.warning('只能上传 JPG/PNG/GIF 格式的图片')
        return false
      }
      const isLt5M = file.size / 1024 / 1024 <= 5
      if (!isLt5M) {
        this.$message.warning('每张图片不能超过 5MB')
        return false
      }
      return true
    },

    doImageUpload({ file }) {
      const formData = new FormData()
      formData.append('files', file)
      return Api.uploadFile(formData)
        .then(res => {
          const paths = res.data || []
          this.imageUrls = this.imageUrls.concat(paths)
          this.form.imageUrl = this.imageUrls.join(',')
        })
        .catch(() => {
          // 拦截器已提示
        })
    },

    onImageRemove(file) {
      const url = file.url || (file.response && file.response[0])
      const idx = this.imageUrls.indexOf(url)
      if (idx > -1) {
        this.imageUrls.splice(idx, 1)
      }
      this.form.imageUrl = this.imageUrls.join(',')
    },

    onImageExceed() {
      this.$message.warning('门店图片最多上传 5 张')
    },

    // 提交：新增调添加门店信息接口，编辑调修改门店信息接口
    onSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid || this.submitting) {
          return
        }
        this.submitting = true
        try {
          const data = Object.assign({}, this.form)
          // 营业时间、经纬度为空时不提交，避免后端反序列化异常
          if (!data.openTime) {
            delete data.openTime
          }
          if (!data.closeTime) {
            delete data.closeTime
          }
          if (data.longitude === null || data.longitude === undefined) {
            delete data.longitude
          }
          if (data.latitude === null || data.latitude === undefined) {
            delete data.latitude
          }
          if (this.isEdit) {
            await Api.updateStore(data)
            this.$message.success('修改成功')
          } else {
            delete data.storeId
            await Api.addStore(data)
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
@import '@/assets/styles/store/add-or-update.less';
</style>

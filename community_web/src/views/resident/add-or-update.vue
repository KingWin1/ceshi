<template>
  <el-dialog
    :title="isEdit ? '编辑居民' : '新增居民'"
    :visible="visible"
    width="640px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="头像">
        <el-upload
          class="avatar-upload"
          action="/api/file/upload"
          name="files"
          :headers="uploadHeaders"
          :show-file-list="false"
          accept="image/jpeg,image/png"
          :before-upload="beforeAvatarUpload"
          :on-success="onAvatarSuccess"
          :on-error="onAvatarError"
        >
          <img v-if="form.avatarUrl" :src="avatarPreview" class="avatar-img" alt="头像">
          <i v-else class="el-icon-plus avatar-icon" />
        </el-upload>
        <div class="upload-tip">支持jpg/png，不超过2MB</div>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" maxlength="20" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio :label="0">男</el-radio>
          <el-radio :label="1">女</el-radio>
          <el-radio :label="2">未知</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">正常</el-radio>
          <el-radio :label="2">停用</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 关联房间：新增与编辑均可维护多行房间绑定 -->
      <el-form-item label="关联房间">
        <div class="room-list">
          <div v-for="(room, index) in form.rooms" :key="index" class="room-row">
            <el-select
              v-model="room.buildingId"
              class="w-110"
              placeholder="楼栋"
              clearable
              @change="v => onRoomBuildingChange(room, v)"
            >
              <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
            </el-select>
            <el-select
              v-model="room.unitNo"
              class="w-95"
              placeholder="单元"
              clearable
              :disabled="!room.buildingId"
              @change="v => onRoomUnitChange(room, v)"
            >
              <el-option v-for="u in room.unitOptions" :key="u.value" :label="u.label" :value="u.value" />
            </el-select>
            <el-select
              v-model="room.houseNumber"
              class="w-95"
              placeholder="房间"
              clearable
              :disabled="!room.unitNo"
            >
              <el-option v-for="h in room.houseOptions" :key="h" :label="h" :value="h" />
            </el-select>
            <el-select v-model="room.type" class="w-95" placeholder="类型">
              <el-option v-for="t in roomTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
            </el-select>
            <el-button
              class="room-del"
              type="danger"
              icon="el-icon-delete"
              circle
              size="small"
              @click="removeRoom(index)"
            />
          </div>
          <el-button class="btn-add-room" type="primary" icon="el-icon-plus" @click="addRoom">添加房间</el-button>
        </div>
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

// 表单初始值
const emptyForm = () => ({
  residentId: null,
  name: '',
  phone: '',
  idCard: '',
  gender: 2,
  status: 1,
  type: null,
  avatarUrl: '',
  rooms: []
})

// 关联房间行初始值（unitOptions/houseOptions 为该行独立下拉数据源）
const emptyRoom = () => ({
  buildingId: null,
  unitNo: '',
  houseNumber: '',
  type: 1,
  unitOptions: [],
  houseOptions: []
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑回显数据：{ resident, houseList }，新增传 null
    record: { type: Object, default: null },
    // 楼栋下拉数据源
    buildings: { type: Array, default: () => [] },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      roomTypeOptions: [
        { value: 1, label: '业主' },
        { value: 2, label: '家属' },
        { value: 3, label: '租户' }
      ],
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    // 上传请求头：携带 token
    uploadHeaders() {
      return { Authorization: 'Bearer ' + (localStorage.getItem('token') || '') }
    },
    // 头像预览地址
    avatarPreview() {
      const url = this.form.avatarUrl
      if (!url) {
        return ''
      }
      return url.startsWith('http') ? url : window.location.origin + url
    }
  },
  watch: {
    // 弹窗打开时回显数据
    visible(val) {
      if (!val) {
        return
      }
      if (this.isEdit && this.record) {
        const resident = this.record.resident || {}
        this.form = { ...emptyForm(), ...resident }
        this.buildRoomsFromRecord()
      } else {
        this.form = emptyForm()
        // 默认给一行关联房间
        this.form.rooms.push(emptyRoom())
      }
    }
  },
  methods: {
    // 编辑回显：由认证记录+房屋信息组装关联房间行，并加载各行下拉选项
    async buildRoomsFromRecord() {
      const authList = this.record.authList || []
      const houseList = this.record.houseList || []
      const rooms = houseList.map(house => {
        const auth = authList.find(a => a.houseId === house.houseId)
        return {
          ...emptyRoom(),
          buildingId: house.buildingId,
          unitNo: house.unitNo != null ? String(house.unitNo) : '',
          houseNumber: house.houseNumber,
          type: auth && auth.type ? auth.type : 1
        }
      })
      this.form.rooms = rooms.length > 0 ? rooms : [emptyRoom()]
      // 逐行加载单元、房间下拉选项，保证回显文案正确
      for (const room of this.form.rooms) {
        if (room.buildingId) {
          await this.loadUnitOptions(room)
        }
        if (room.unitNo) {
          await this.loadHouseOptions(room)
        }
      }
    },

    // 头像上传前校验
    beforeAvatarUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].includes(file.type)
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) {
        this.$message.error('头像只支持 jpg/png 格式')
      }
      if (!isLt2M) {
        this.$message.error('头像大小不能超过 2MB')
      }
      return isImage && isLt2M
    },

    // 上传成功：后端返回 Result<List<String>>，取第一个文件路径
    onAvatarSuccess(res) {
      if (res && res.code === '00000' && res.data && res.data.length > 0) {
        this.form.avatarUrl = res.data[0]
        this.$message.success('头像上传成功')
      } else {
        this.$message.error((res && res.msg) || '头像上传失败')
      }
    },

    onAvatarError() {
      this.$message.error('头像上传失败')
    },

    // 楼栋-单元二级联动：加载该行单元下拉选项
    async onRoomBuildingChange(room, buildingId) {
      room.unitNo = ''
      room.houseNumber = ''
      room.unitOptions = []
      room.houseOptions = []
      if (!buildingId) {
        return
      }
      await this.loadUnitOptions(room)
    },

    // 加载某行的单元下拉选项
    async loadUnitOptions(room) {
      try {
        const res = await Api.getBuildingById(room.buildingId)
        const count = (res.data && res.data.unitCount) || 0
        room.unitOptions = Array.from({ length: count }, (_, i) => ({
          value: String(i + 1),
          label: `${i + 1}单元`
        }))
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 单元-房间号三级联动：加载该行房间下拉选项
    async onRoomUnitChange(room, unitNo) {
      room.houseNumber = ''
      room.houseOptions = []
      if (!unitNo) {
        return
      }
      await this.loadHouseOptions(room)
    },

    // 加载某行的房间号下拉选项
    async loadHouseOptions(room) {
      try {
        const res = await Api.getHouseByBuildingAndUnit(room.buildingId, room.unitNo)
        const houses = res.data || []
        room.houseOptions = [...new Set(houses.map(h => h.houseNumber))]
      } catch (e) {
        // 拦截器已提示
      }
    },

    addRoom() {
      this.form.rooms.push(emptyRoom())
    },

    removeRoom(index) {
      this.form.rooms.splice(index, 1)
    },

    // 校验通过后组装提交数据交给父组件
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        // 关联房间：过滤掉未选完整的行
        const rooms = this.form.rooms
          .filter(r => r.buildingId && r.unitNo && r.houseNumber)
          .map(r => ({ buildingId: r.buildingId, unitNo: r.unitNo, houseNumber: r.houseNumber, type: r.type }))
        const payload = {
          name: this.form.name,
          phone: this.form.phone,
          idCard: this.form.idCard,
          gender: this.form.gender,
          status: this.form.status,
          avatarUrl: this.form.avatarUrl,
          rooms
        }
        if (this.isEdit) {
          payload.residentId = this.form.residentId
          // 类型不在编辑弹窗中修改，沿用原值
          payload.type = this.form.type
        } else {
          payload.registerWay = 2
          // 居民类型取首个关联房间的认证类型，未关联时默认业主
          const firstRoom = rooms[0]
          payload.type = firstRoom ? firstRoom.type : 1
        }
        this.$emit('submit', payload)
      })
    },

    resetForm() {
      this.$refs.form && this.$refs.form.clearValidate()
      this.form = emptyForm()
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/resident/add-or-update.less';
</style>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import carousel from '../../api/carousel'
import serverUrl from '../../serverUrl'



// 轮播图列表
const carouselList = ref([])
// 上传对话框可见性
const uploadDialogVisible = ref(false)
// 上传的图片文件
const uploadFile = ref(null)
const link = ref('')
// 上传的图片预览
const imageUrl = ref('')
// 加载状态
const loading = ref(false)

// 获取轮播图列表
const getCarouselList = async () => {
  try {
    loading.value = true
    const response = await carousel.getCarouselList()
    carouselList.value = response.data
    console.log('获取轮播图列表',carouselList.value)
  } catch (error) {
    ElMessage.error('获取轮播图列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 上传前的钩子
// 修改函数参数，接收 UploadFile 对象
const beforeUpload = (uploadFileInstance) => {
  // 从 UploadFile 对象中获取原始 File 对象
  const rawFile = uploadFileInstance.raw; 
  
  // 检查 rawFile 是否存在
  if (!rawFile) {
    console.error('无法获取上传的文件');
    return false;
  }

  // 使用 rawFile 进行类型和大小检查
  const isImage = rawFile.type.startsWith('image/')
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }

  // 将原始 File 对象赋值给 uploadFile ref
  uploadFile.value = rawFile 
  const reader = new FileReader()
  // 使用 rawFile 读取数据
  reader.readAsDataURL(rawFile) 
  reader.onload = () => {
    imageUrl.value = reader.result
  }
  return false // 阻止自动上传
}

// 上传图片
const upload = async () => {
  if (!uploadFile.value) {
    ElMessage.warning('请先选择图片')
    return
  }

  try {
    loading.value = true
    const formData = new FormData()
    formData.append('file', uploadFile.value)
    formData.append('link', link.value)
    const response = await carousel.insertCarousel(formData)

    if (response) {
      ElMessage.success('上传成功')
      uploadDialogVisible.value = false
      imageUrl.value = ''
      uploadFile.value = null
      await getCarouselList()
    }
  } catch (error) {
    ElMessage.error('上传失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 删除轮播图
const deleteCarousel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这张轮播图吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    loading.value = true
    await carousel.deleteCarousel(id)
    ElMessage.success('删除成功')
    await getCarouselList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  } finally {
    loading.value = false
  }
}

// 移动轮播图位置
const moveCarousel = async (id, direction) => {
  try {
    loading.value = true
    await carousel.moveCarousel(id, direction)
    await getCarouselList()
    ElMessage.success(direction === 'up' ? '上移成功' : '下移成功')
  } catch (error) {
    ElMessage.error('操作失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取轮播图列表
onMounted(() => {
  getCarouselList()
})
</script>

<template>
  <div class="carousel-manage">
    <div class="header">
      <h2>轮播图管理</h2>
      <el-button type="primary" @click="uploadDialogVisible = true" :icon="Plus">
        添加轮播图
      </el-button>
    </div>

    <el-divider />

    <!-- 轮播图列表 -->
    <div v-loading="loading">
      <div v-if="carouselList.length > 0" class="carousel-list">
        <el-card v-for="(item, index) in carouselList" :key="item.id" class="carousel-item">
          <div class="carousel-image">
            <img :src="`${serverUrl.url}/carousel/images/${item.picture}`" alt="轮播图" />
          </div>
          <div class="carousel-info">
            <div class="carousel-order">顺序: {{ index + 1 }}</div>
            <div class="carousel-actions">
              <el-button 
                type="primary" 
                :icon="ArrowUp" 
                circle 
                :disabled="index === 0"
                @click="moveCarousel(item.id, 'up')"
              ></el-button>
              <el-button 
                type="primary" 
                :icon="ArrowDown" 
                circle 
                :disabled="index === carouselList.length - 1"
                @click="moveCarousel(item.id, 'down')"
              ></el-button>
              <el-button 
                type="danger" 
                :icon="Delete" 
                circle
                @click="deleteCarousel(item.id)"
              ></el-button>
            </div>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暂无轮播图"></el-empty>
    </div>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传轮播图" width="500px">
      <el-upload
        class="upload-container"
        action="#"
        :auto-upload="false"
        :show-file-list="false"
        :on-change="beforeUpload"  
        drag
      >
        <div v-if="!imageUrl" class="upload-placeholder">
          <el-icon><Plus /></el-icon>
          <div class="el-upload__text">
            拖拽图片到此处，或 <em>点击上传</em>
          </div>
          <div class="el-upload__tip">
            只能上传 jpg/png 文件，且不超过 2MB
          </div>
        </div>
        <img v-else :src="imageUrl" class="preview-image" />
      </el-upload>
      <el-input
        v-model="link"
        type="text"
        placeholder="请输入图片链接"
        class="el-input"
        style="margin-top: 10px"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="upload" :loading="loading">
            上传
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.carousel-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.carousel-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.carousel-item {
  transition: all 0.3s;
}

.carousel-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.carousel-image {
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 10px;
}

.carousel-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.carousel-order {
  font-weight: bold;
}

.carousel-actions {
  display: flex;
  gap: 10px;
}

.upload-container {
  width: 100%;
}

.upload-placeholder {
  padding: 30px 0;
}

.preview-image {
  width: 100%;
  max-height: 300px;
  object-fit: contain;
}
</style>
<template>
  <el-card class="feedback-form">
    <el-form :model="form">
      <el-form-item label="反馈内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="联系方式（可选）">
        <el-input v-model="form.contact" placeholder="邮箱/手机号" />
      </el-form-item>
      <el-upload
        ref="uploadRef"
        :action="serverUrl.url + '/api/feedback/upload'"
        :headers="{ Authorization: 'Bearer ' + token }"
        :file-list="uploadFiles"
        multiple
        :on-success="handleUploadSuccess">
        <el-button type="primary">上传截图</el-button>
      </el-upload>
      <el-button type="primary" @click="submit">提交反馈</el-button>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue';
import tokenModule  from '../../assets/token';
import feedback from '../../../api/feedback';
import serverUrl from '../../../serverUrl';

const form = reactive({
  content: '',
  contact: '',
  screenshots: [] 
})

const uploadFiles = ref([])
const uploadRef = ref()

const token = localStorage.getItem(tokenModule.token) || ''; 
const handleUploadSuccess = (response, file, fileList) => {
    console.log('images', response);
  form.screenshots.push(response); 
}
const submit = async () => {
  const formData = new FormData();
  formData.append('content', form.content);
  formData.append('contact', form.contact);
  formData.append('images', form.screenshots);
  try {
    const res = await feedback.insert(formData);
    console.log('反馈提交成功', res); 
  }catch (error) {
    console.error('反馈提交失败', error); 
  }
  form.content = '';
  form.contact = '';
  form.screenshots = [];
  // 重置上传组件
  uploadFiles.value = []
 
}
</script>

<template>
  <div class="article-publish">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入文章标题"></el-input>
      </el-form-item>

      <el-form-item label="分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择分类">
          <el-option
            v-for="item in categories"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="内容" prop="content">
        <div style="border: 1px solid #ccc" class="editor-block">
          <Toolbar
              class="editor-toolbar"
            :editor="editor"
            :defaultConfig="toolbarConfig"
          />
          <Editor
              class="editor-content"
            v-model="form.content"
            :defaultConfig="editorConfig"
            @onCreated="handleEditorCreated"
          />
        </div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm">立即发布</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { ref, reactive, shallowRef } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { ElMessage } from 'element-plus'
import article from '../../api/article.js'// 需要新建API模块

// 编辑器实例
const editor = shallowRef()
const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      server: '/article/upload', // 图片上传接口
      fieldName: 'file',
      maxFileSize: 100 * 1024 * 1024, //
      allowedFileTypes: ['*'],
      customInsert(res, insertFn) {
        insertFn(res.data.url) // 假设返回{ data: { url: '...' } }
      }
    }
  }
}

// 表单数据
const form = reactive({
  title: '',
  category: '',
  content: ''
})

// 验证规则
const rules = {
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '内容不能为空', trigger: 'blur' }]
}

// 分类选项
const categories = [
  { label: '考试通知', value: 'notice' },
  { label: '政策解读', value: 'policy' },
  { label: '备考指南', value: 'guide' }
]

// 提交表单
const submitForm = async () => {
  try {
    await article.publish(form)
    ElMessage.success('发布成功')
    resetForm()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

// 重置表单
const resetForm = () => {
  form.title = ''
  form.category = ''
  form.content = ''
  if (editor.value) {
    editor.value.clear()
  }
}
const handleEditorCreated = (editorInstance) => {
  editor.value = editorInstance  // 确保正确接收编辑器实例
}
</script>
<style scoped>
.editor-block {
  width: 100%;
  height: 500px;
}
.editor-container {
  border: 1px solid #ccc;
  height: 600px;  /* 增加总高度 */
}

.editor-toolbar {
  border-bottom: 1px solid #ccc;
}

.editor-content {
  height: calc(100% - 41px); /* 给工具栏留出空间 */
}
</style>

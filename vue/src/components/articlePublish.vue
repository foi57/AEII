<template>
  <div class="article-publish">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="标题" prop="articleTitle">
        <el-input v-model="form.articleTitle" placeholder="请输入文章标题"></el-input>
      </el-form-item>
      <div class="category-select-block">
        <el-form-item label="分类" prop="articleType">
          <el-select v-model="form.articleType" placeholder="请选择分类" style="width: 150px">
            <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <label>添加关联院校</label>
          <el-autocomplete
              v-model="universityName"
              :fetch-suggestions="querySearchAsync"
              placeholder="请输入院校名称"
              @select="handleSearch"
              style="width: 300px; margin: 20px"
          ></el-autocomplete>
          <el-button type="primary" @click="handleSearch">添加</el-button>
        </el-form-item>
      </div>
      <el-form-item label="关联院校">
        <el-input-tag v-model="form.affiliatedUniversities"
                      clearable
                      :trigger="null"
                      draggable></el-input-tag>
      </el-form-item>
      <el-form-item label="附件">
        <el-upload
            class="upload-demo"
            :action="serverUrl.url + '/api/article/upload/file'"
            :headers="{ Authorization: `Bearer ${token}` }"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :file-list="form.affiliatedFiles"
            :limit="5"
            accept=".pdf,.doc,.docx,.xls,.xlsx"
            multiple
        >
          <el-button type="primary">点击上传</el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持扩展名：.pdf .doc .docx .xls .xlsx，单个文件不超过10MB
            </div>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item >
        <div class="button-block">
        <el-button type="primary" @click="submitForm">立即发布</el-button>
        <el-button @click="resetForm">重置</el-button>
        </div>
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
            v-model="form.articleContent"
            :defaultConfig="editorConfig"
            @onCreated="handleEditorCreated"
              :key="editorKey"
          />
        </div>
      </el-form-item>


    </el-form>
  </div>
</template>

<script setup>

import '@wangeditor/editor/dist/css/style.css'
import {onBeforeUnmount, reactive, ref, shallowRef,nextTick} from 'vue' // 新增生命周期钩子
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import {ElMessage} from 'element-plus'
import article from '../../api/article.js'
import serverUrl from "../../serverUrl.js";
import universityApi from "../../api/university.js";
import {Store} from "../store/index.js";
import {useRoute} from "vue-router";
import articleCategories from '../assets/articleCategories.js'

const route = useRoute()
const userStore = Store();

const token = localStorage.getItem('accessToken')

const handleUploadSuccess = (response, file) => {
  form.affiliatedFiles.push({
    name: file.name,
    url: serverUrl.url + response
  })
  console.log('上传成功',form.affiliatedFiles)
  ElMessage.success('上传成功')
}

const handleUploadError = () => {
  ElMessage.error('上传失败，请重试')
}

const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  return true
}

// 编辑器实例
const editId = route.params.id;
if (editId) {

  article.selectArticleDetail(editId).then(res => {
    document.title = `编辑文章 - ${res.data.articleTitle}`
    form.articleTitle = res.data.articleTitle;
    form.articleType = res.data.articleType;
    form.articleContent = res.data.articleContent;
    for (const item of res.data.affiliatedUniversities){
      form.affiliatedUniversities.push(item.universityName)
    }
    if (res.data.attachments) {
      form.affiliatedFiles = res.data.attachments.map(att => ({
        name: att.name,
        url: serverUrl.url + att.url // 拼接完整URL
      }));
    }
    console.log('编辑文章',res.data)
    console.log('编辑文章',form)
  }).catch(err => {
    console.log(err)
    ElMessage.error('获取文章详情失败')
  })
}
const universityName = ref('');

const editor = shallowRef()

const handleEditorCreated = (editorInstance) => {
  editor.value = editorInstance
}

onBeforeUnmount(() => {
  if (editor.value) {
    editor.value.destroy()
    editor.value = null
  }
})


const toolbarConfig = {
  excludeKeys: [
          // 移除视频相关菜单
    'group-video',
  ],

}


const editorConfig = {
  scroll: true,
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {  // ← 修改这里
        try {
          const formData = new FormData()
          formData.append('file', file)

          // 使用配置好的axios实例
          const res = await article.uploadImage(formData)

          // 根据实际响应结构调整
          insertFn(serverUrl.url+res.data.url)
        } catch (err) {
          console.error('上传失败:', err)
        }
      },
      fieldName: 'file',
      maxFileSize: 3 * 1024 * 1024, // 3M
      allowedFileTypes: ['image/*'],
    },

  }
}

// 表单数据
const form = reactive({
  articleTitle: '',
  articleType: '',
  articleContent: '',
  affiliatedUniversities : [],
  articleSource: userStore.usersId,
  affiliatedFiles: [] // 存储附件信息{name: '文件名', url: '文件URL'},
})

// 验证规则
const rules = {
  articleTitle: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  articleType: [{ required: true, message: '请选择分类', trigger: 'change' }],
  articleContent: [{ required: true, message: '内容不能为空', trigger: 'blur' }]
}

// 分类选项
const categories = articleCategories.categories

// 提交表单
const submitForm = async () => {
  try {

    form.articleContent = editor.value.getHtml()

    const submitData = {
      ...form,
      attachments: form.affiliatedFiles.map(f => ({
        name: f.name,
        url: f.url.replace(serverUrl.url, '') // 存储相对路径
      }))
    }

    if (editId) {
      await article.updateArticle({
        ...submitData,
        articleId: editId
      })
      console.log('更新文章',submitData)
    } else {
      await article.publish(submitData)
      resetForm()
    }

    ElMessage.success(editId ? '更新成功' : '发布成功')

  } catch (error) {
    console.error(error)
    ElMessage.error(error.message || '操作失败')
  }finally {
    if (editor.value) {
      editor.value.restoreSelection() // 保持选区状态
    }
  }
}


const editorKey = ref(0)

const resetForm = () => {
  form.articleTitle = ''
  form.articleType = ''
  form.articleContent = ''
  form.affiliatedFiles = []
  form.affiliatedUniversities = []
  form.universityId = ''
  // if (editor.value) {
  //   nextTick(() => {
  //     editor.value.destroy()
  //     editor.value = null
  //     // 通过 key 变化强制重新创建编辑器
  //     editorKey.value++
  //   })
  // }
}

const querySearchAsync = debounce(async (queryString, cb) => {
  if (queryString) {
    try {
      const res = await universityApi.selectUniversityListByName(universityName.value, '','','','', 1,10)
      let select = '';
      select = res.data.records.map(item => {
        return { value: item.universityName, ...item };
      });
      cb(select);
    } catch (error) {
      console.error('获取建议失败:', error);
      cb([]);
    }
  } else {
    cb([]);
  }
}, 500);

// 实际搜索处理
const handleSearch = async () => {
  const res = await universityApi.selectUniversityListByName(universityName.value, '', '', '', '', 1,10)
  form.affiliatedUniversities.push(res.data.records[0].universityName);
};

function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
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
.category-select-block {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.button-block {
  width: 100%;
  display: flex;
  justify-content: center;
}

.upload-demo {
  width: 100%;
  margin-top: 10px;
}

.el-upload__tip {
  color: #666;
  font-size: 12px;
  margin-top: 5px;
}
</style>
<script setup>
import {h, reactive, ref, resolveComponent,onMounted} from "vue";
import {Store} from "../store/index.js";
import article from "../../api/article.js";
import {ElAutoResizer, ElMessage,ElTableV2 } from "element-plus";
import router from "../router/index.js";
import articleCategories from "../assets/articleCategories.js"

onMounted(() => {
  handleChange()
})
const currentPage3 = ref(1);
const pageSize3 = ref(50);
const count = ref(100);
const disabled = ref(false);
const userStore = Store();
const categories = articleCategories.categories
// 导入格式化函数
const formatArticleType = articleCategories.formatArticleCategories

const form = reactive({
  articleTitle: '',
  articleType: '',
  articleSource: userStore.usersId,
  pageNum: 1,
  pageSize: 50,
})
const handleEdit=(rowData)=>{
  const newWindow = window.open(
      `${window.location.origin}/article/edit/${rowData.articleId}`,
      '_blank'
  )
}
const handleDelete = async () => {
 await article.deleteArticle(articleId.value).then(res => {
   console.log(res)
   handleChange()
   handleDeleteDialog.value = false;
   ElMessage.success('删除成功')
 }).catch(err => {
   console.log(err)
   ElMessage.error('删除失败')
 })

}
const articleId = ref('')
const columns = ref([
  {
    key: 'articleTitle',
    dataKey: 'articleTitle',
    title: '文章标题',
    width: 200,
    cellRenderer: ({ rowData }) => h('span', {
      onClick: () => handleRowClick(rowData),
      style: { cursor: 'pointer', color: '#409eff' }
    }, rowData.articleTitle)
  },
  {
    key: 'articleReleased',
    dataKey: 'articleReleased',
    title: '发布日期',
    width: 150
  },
  {
    key: 'articleType',
    dataKey: 'articleType',
    title: '文章类型',
    width: 150,
    // 修改这里，使用 formatArticleType 函数将英文类型转换为中文显示
    cellRenderer: ({ rowData }) => h('span', {}, formatArticleType(rowData.articleType))
  },
  {
    key: 'userName',
    dataKey: 'userName',
    title: '发布人',
    width: 150
  },
  {
    key: '操作',
    dataKey: '操作',
    title: '操作',
    width: 170,
    cellRenderer: ({ rowData }) => {
      // 仅当发布人ID与当前用户ID一致时显示操作按钮
      if (rowData.articleSource === userStore.usersId || userStore.usersRole === 'seniorAdmin') {
        return h('div', [
          h(resolveComponent('ElButton'), {
            type: 'primary',
            onClick: () => handleEdit(rowData),
            style: { marginRight: '8px' }
          }, '编辑'),
          h(resolveComponent('ElButton'), {
            type: 'danger',
            onClick: () => {
              articleId.value = rowData.articleId
              handleDeleteDialog.value = true;
            }
          }, '删除')
        ])
      }
      return null
    }
  }
]);
let articles = ref([]);
const handleChange = () => {
  form.pageNum = currentPage3.value;
  article.selectArticleList(form).then(res => {
    console.log(res.data.records)
    articles.value = res.data.records
    count.value = res.data.total

  }).catch(err => {
    console.log(err)
    ElMessage.error('获取失败')
  })
}
const handleRowClick = (rowData) => {
  router.push(`/article/detail/${rowData.articleId}`)
}
const handleDeleteDialog = ref(false);

// 带防抖的搜索建议查询
let searchResults = ref([]);
const querySearchAsync = debounce(async (queryString, cb) => {
  if (queryString) {
    try {
      const res = await article.selectArticleList(form)
      console.log('获取建议:', res.data.records);
       searchResults.value = res.data.records.map(item => {
        return { value: item.articleTitle, ...item };
      });
      console.log('推荐搜索结果',searchResults.value)
      cb(searchResults.value);
    } catch (error) {
      console.error('获取建议失败:', error);
      cb([]);
    }
  } else {
    cb([]);
  }
}, 500);



// 防抖函数
function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}

</script>

<template>
  <div class="search-block">
    <el-autocomplete
        v-model="form.articleTitle"
        :fetch-suggestions="querySearchAsync"
        placeholder="请输入资讯标题"
        @select="handleChange"
        style="width: 300px; margin: 20px"
    ></el-autocomplete>
<label>文章类型：</label>
    <el-select v-model="form.articleType" @change="handleChange" style="width: 120px">
      <el-option
          v-for="item in categories"
          :key="item.value"
          :label="item.label"
          :value="item.value"
      >
      </el-option>
    </el-select>
    <el-button type="primary" @click="handleChange">搜索</el-button>
  </div>


  <div style="height: 80vh; width: 100%">  <!-- 改为百分比宽度 -->
  <ElAutoResizer>
    <template #default="{ height, width }">
      <ElTableV2
        :columns="columns"
        :data="articles"
        :width="width"
        :height="height"
        :estimated-row-height="50"  
        row-class="custom-row"  
        border
      />
    </template>
  </ElAutoResizer>
</div>
  <div class="page-block">
    <el-pagination
        v-model:current-page="currentPage3"
        v-model:page-size="pageSize3"
        :size="'default'"
        :disabled="disabled"
        layout="prev, pager, next, jumper"
        :total="count"
        @size-change="handleChange"
        @current-change="handleChange"
    />
  </div>

  <el-dialog title="删除" v-model="handleDeleteDialog">
    <span>是否确认删除？</span>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleDeleteDialog=false">取 消</el-button>
      <el-button type="primary" @click="handleDelete">确 定</el-button>
    </span>
  </el-dialog>
</template>

<style scoped>
.search-block {
  display: flex;
  justify-content: center;
  align-items: center;
}
.page-block {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
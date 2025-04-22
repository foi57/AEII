<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { ElMessage, ElMessageBox, ElTable, ElTableColumn, ElButton, ElDialog, ElInputNumber, ElForm, ElFormItem } from 'element-plus';
import { Plus, Delete, ArrowUp, ArrowDown } from '@element-plus/icons-vue';
import hotArticleApi from '../../api/hotArticle';
import articleApi from '../../api/article'; // 用于获取文章标题

const hotArticleList = ref([]);
const loading = ref(false);
const addDialogVisible = ref(false);
const addForm = ref({ 
    articleTitle: null,
    articleId: null });


// 获取热门文章列表
const getHotArticleList = async () => {
  try {
    loading.value = true;
    const response = await hotArticleApi.getHotArticleList();
    hotArticleList.value = response.data || [];
    console.log('获取热门文章列表成功', hotArticleList.value);
  } catch (error) {
    ElMessage.error('获取热门文章列表失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};



// 添加热门文章
const handleAdd = async () => {
  if (!addForm.value.articleId) {
    ElMessage.warning('请输入文章');
    return;
  }

  try {
    loading.value = true;
    await hotArticleApi.addHotArticle(addForm.value.articleId);
    ElMessage.success('添加成功');
    addDialogVisible.value = false;
    addForm.value.articleId = null; // 重置表单
    await getHotArticleList(); // 刷新列表
  } catch (error) {
    ElMessage.error('添加失败，请检查文章ID是否存在或是否已添加');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 删除热门文章
const deleteHotArticle = async (hotArticleId) => {
  try {
    await ElMessageBox.confirm('确定要移除这篇热门文章吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    loading.value = true;
    await hotArticleApi.deleteHotArticle(hotArticleId);
    ElMessage.success('删除成功');
    await getHotArticleList(); // 刷新列表
  } catch (error) {
    // 如果用户点击取消，会进入 catch，需要区分
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
      console.error(error);
    }
  } finally {
    loading.value = false;
  }
};

// 移动热门文章位置
const moveHotArticle = async (hotArticleId, direction) => {
  try {
    loading.value = true;
    await hotArticleApi.moveHotArticle(hotArticleId, direction);
    await getHotArticleList(); // 刷新列表
    ElMessage.success(direction === 'up' ? '上移成功' : '下移成功');
  } catch (error) {
    ElMessage.error('操作失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  getHotArticleList();
});

const form = reactive({
    articleTitle: '',
})

const searchResults = ref([]);
const querySearchAsync = debounce(async (queryString, cb) => {
  if (queryString) {
    try {
      const res = await articleApi.selectArticleList(form)
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

const handleChange = (value) => {
  if (value) {
    addForm.value.articleId = value.articleId;
    addForm.value.articleTitle = value.articleTitle;
  } else {
    addForm.value.articleId = null;
  }
};
</script>

<template>
  <div class="hot-article-manage">
    <div class="header">
      <h2>热门文章管理</h2>
      <div>
        <el-button type="primary" @click="()=>{
          if(hotArticleList.length>=9){
            ElMessage.warning('热门文章数量已达上限');
            return;
          }
          addDialogVisible = true}" :icon="Plus">
        添加热门文章
      </el-button>
      <span style="font-size: small;color: darkgrey;">上限为9个</span>
      </div>
      
    </div>

    <el-divider />

    <el-table :data="hotArticleList" v-loading="loading" style="width: 100%">
      <el-table-column prop="sortOrder" label="排序" width="80" align="center"></el-table-column>
      <el-table-column prop="articleId" label="文章ID" width="100" align="center"></el-table-column>
      <el-table-column prop="article.articleTitle" label="文章标题" min-width="200">
         <template #default="{ row }">
            <el-link :href="`/article/detail/${row.articleId}`" target="_blank">{{ row.articleTitle }}</el-link>
         </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row, $index }">
          <el-button
            type="primary"
            :icon="ArrowUp"
            circle
            size="small"
            :disabled="$index === 0"
            @click="moveHotArticle(row.id, 'up')"
          ></el-button>
          <el-button
            type="primary"
            :icon="ArrowDown"
            circle
            size="small"
            :disabled="$index === hotArticleList.length - 1"
            @click="moveHotArticle(row.id, 'down')"
          ></el-button>
          <el-button
            type="danger"
            :icon="Delete"
            circle
            size="small"
            @click="deleteHotArticle(row.id)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && hotArticleList.length === 0" description="暂无热门文章"></el-empty>

    <!-- 添加对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加热门文章" width="400px">
      <el-form :model="addForm" label-width="80px">
        <el-form-item>
            <el-autocomplete
        v-model="form.articleTitle"
        :fetch-suggestions="querySearchAsync"
        placeholder="请输入资讯标题"
        @select="handleChange"
        style="width: 300px; margin: 20px"
        >
        
        </el-autocomplete>
        </el-form-item>
        <el-form-item 
            v-if="addForm.articleTitle" label="选中文章">
           <span>{{ addForm.articleTitle }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAdd" :loading="loading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.hot-article-manage {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.el-table .el-button + .el-button {
    margin-left: 8px;
}
</style>
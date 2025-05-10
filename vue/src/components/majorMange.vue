<script setup>
import majorApi from "../../api/major.js";
import {h, ref, resolveComponent} from "vue";
import {ElAutoResizer, ElMessage, ElTableV2} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import major from "../../api/major.js";

const majorList = ref([])
const count = ref(0)
const currentPage3 = ref(1);
const pageSize3 = ref(50);
const disabled = ref(false);
const selectName = ref('');
majorApi.selectMajorListByName('',currentPage3.value,pageSize3.value).then(res => {
  majorList.value = res.data.records;
  count.value = res.data.total;
})
const select=()=>{
  majorApi.selectMajorListByName(selectName.value,currentPage3.value,pageSize3.value).then(res => {
    majorList.value = res.data.records;
    count.value = res.data.total;
    console.log('查询',res.data)
  })
}
const columns = ref([
  {
    key: 'majorCode',
    dataKey: 'majorCode',
    title: '专业代码',
    width: 120,
  },
  {
    key: 'majorName',
    dataKey: 'majorName',
    title: '专业名称',
    width: 180
  },
  {
    key: 'majorCategory',
    dataKey: 'majorCategory',
    title: '专业类别',
    width: 150
  },
  {
    key: 'duration',
    dataKey: 'duration',
    title: '修业年限',
    width: 120
  },
  {
    key: 'majorIntroduction',
    dataKey: 'majorIntroduction',
    title: '专业介绍',
    width: 400,
  },
  {
    key: 'colleges',
    dataKey: 'colleges',
    title: '操作',
    cellRenderer: ({ rowData }) => h('div',[

        h( resolveComponent('ElButton'),
            {
              type: 'primary',
              onClick: () => {
                handleDialog.value = true;
                majorForm.value.majorId = rowData.majorId;
                majorForm.value.majorCode = rowData.majorCode;
                majorForm.value.majorName = rowData.majorName;
                majorForm.value.majorCategory = rowData.majorCategory;
                majorForm.value.duration = rowData.duration;
                majorForm.value.majorIntroduction = rowData.majorIntroduction;
                majorForm.value.majorCourse = rowData.majorCourse;
                majorForm.value.majorEmployment = rowData.majorEmployment;
                toDo.value = 'edit';
              }
            },
            () => '编辑'),
        h( resolveComponent('ElButton'),
            {
              type: 'danger',
              onClick: () => {
                handleDeleteDialog.value = true;
                majorForm.value.majorId = rowData.majorId;
              }
            },
            () => '删除')
        ]),

    width: 300
  }
]);
const handleDialog = ref(false);
const majorForm = ref({
  majorId: '',
  majorCode: '',
  majorName: '',
  majorCategory: '',
  duration: '',
  majorIntroduction: '',
  majorCourse: '',
  majorEmployment: '',
})
const toDo = ref('');
const updateMajor = () => {
  console.log(majorForm.value)
  if (toDo.value === 'insert') {
    majorApi.insertMajor(majorForm.value).then(res => {
      ElMessage.success('添加成功')
      handleDialog.value = false;
      select();
    }).catch(err => {
      ElMessage.error('添加失败')
      console.log(err)
    })
  }
  if (toDo.value === 'edit') {
    majorApi.updateMajor(majorForm.value).then(res => {
      ElMessage.success('编辑成功')
      handleDialog.value = false;
      select();
    }).catch(err => {
      ElMessage.error('编辑失败')
      console.log(err)
    })
  }
}
const rules = {
  majorCode: [
    { required: true, message: '请输入专业代码', trigger: 'blur' },
  ],
  majorName: [
    { required: true, message: '请输入专业名称', trigger: 'blur' },],
  majorCategory: [
    { required: true, message: '请输入专业类别', trigger: 'blur' },
  ],
  duration: [
    { required: true, message: '请输入修业年限', trigger: 'blur' },
  ]
}
const handleDeleteDialog=ref(false);
const handleDelete = () => {
  majorApi.deleteMajorById(majorForm.value.majorId).then(res => {
    ElMessage.success('删除成功')
    select();
  }).catch(error => {
    ElMessage.error('删除失败')
  })
  handleDeleteDialog.value=false;
}
const EstablishmentColleges =() =>{
  window.open(
      `${window.location.origin}/major/university/${majorForm.value.majorId}/${majorForm.value.majorName}`,
      '_blank'
  )
}
</script>

<template>
  <div class="to-do-block">
    <label>搜索专业：</label><el-input :suffix-icon="Search" placeholder="请输入专业名" style="width: 250px" v-model="selectName"></el-input>
    <el-button type="primary" @click="select">搜索</el-button>
    <el-button type="primary" class="insertButton" @click="()=>{handleDialog=true; toDo='insert'; majorForm={majorId: '',
  majorCode: '',
  majorName: '',
  majorCategory: '',
  duration: '',
  majorIntroduction: '',}}">添加专业</el-button>
  </div>
  <div class="major-table-container">
    <ElAutoResizer>
      <template #default="{ height, width }">
        <ElTableV2
          :data="majorList"
          :columns="columns"
          :height="height"
          :width="width"
          :estimated-row-height="100"
          border
          style="width: 100%; height: 100%"
        />
      </template>
    </ElAutoResizer>
  </div>
  <div class="pagination-block">
    <el-pagination
        v-model:current-page="currentPage3"
        v-model:page-size="pageSize3"
        :size="'default'"
        :disabled="disabled"
        layout="prev, pager, next, jumper"
        :total="count"
        @size-change="select"
        @current-change="select"
    />
  </div>
  <el-dialog
      title="添加专业"
      v-model="handleDialog"
      width="600px"
  >
    <el-form
        :model="majorForm"
        :rules="rules"
        ref="formRef"
    ><el-form-item label="id" prop="id" v-if="false" :rules="rules">
        <el-input placeholder="请输入id" v-model="majorForm.majorId" disabled ></el-input>
      </el-form-item>
      <el-form-item label="专业代码" prop="majorCode">
        <el-input placeholder="请输入专业代码" v-model="majorForm.majorCode" prop="majorCode"></el-input>
      </el-form-item>
      <el-form-item label="专业名称" prop="majorName">
        <el-input placeholder="请输入专业名称" v-model="majorForm.majorName" prop="majorName"></el-input>
      </el-form-item>
      <el-form-item label="专业类别" prop="majorCategory">
        <el-input placeholder="请输入专业类别" v-model="majorForm.majorCategory" prop="majorCategory"></el-input>
      </el-form-item>
      <el-form-item label="修业年限" prop="duration">
        <el-input placeholder="请输入修业年限" v-model="majorForm.duration" prop="duration"></el-input>
      </el-form-item>
      <el-form-item label="专业介绍" prop="majorIntroduction">
        <el-input placeholder="请输入专业介绍" v-model="majorForm.majorIntroduction" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="开设课程">
        <el-input placeholder="请输入开设课程" v-model="majorForm.majorCourse"></el-input>
      </el-form-item>
      <el-form-item label="就业方向">
        <el-input placeholder="请输入就业方向" v-model="majorForm.majorEmployment"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="EstablishmentColleges" v-if="toDo!=='insert'">开设院校</el-button>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleDialog=false">取 消</el-button>
      <el-button type="primary" @click="updateMajor()">确 定</el-button>
    </span>
  </el-dialog>
  <el-dialog title="删除" v-model="handleDeleteDialog">
    <span>是否确认删除？</span>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleDeleteDialog=false">取 消</el-button>
      <el-button type="primary" @click="handleDelete">确 定</el-button>
    </span>
  </el-dialog>
</template>

<style scoped>
.major-table-container {
  height: 80vh; /* 确保容器有明确的高度 */
}
.pagination-block {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.to-do-block{
  display: flex;
  justify-content: center;
}
.insertButton{
  position: relative;
  left: 200px
}
</style>
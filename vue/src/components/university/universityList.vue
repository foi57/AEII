<script setup>
import universityApi from "../../../api/university.js";
import {ref} from "vue";
import serverUrl from "../../../serverUrl.js";
import {Store} from "../../store/index.js";
import {ElMessage, ElSelectV2} from "element-plus";
import {useRoute} from "vue-router";
import major_university from "../../../api/major_university.js";


const props = defineProps({
  majorId: {
    type: Number,
    default: null
  }
})

const route = useRoute()
let majorId =route.params.id;
if (majorId === undefined) {
  majorId = props.majorId;
}
const hoverUniversityId = ref(null);
const userStore = Store();
const universityList = ref([])
const currentPage3 = ref(1);
const pageSize3 = ref(50);
const disabled = ref(false);
const count = ref(0);


const universityForm = ref({
  universityId: '',
  universitySchoolBadge: '',
  universityName: '',
  universityArea: '',
  universityLevel: '',
  universityType: '',
  universityFeatures: [],
  universityIntroduction: '',
  // 新增以下字段
  universityPhone: '',
  universityEmail: '',
  universityWebsite: ''
})

const universityFormS = ref({
  universityId: '',
  universitySchoolBadge: '',
  universityName: '',
  universityArea: '',
  universityLevel: '',
  universityType: '',
  universityFeatures: '',
  universityIntroduction: ''
})

const select = async () => {
 if (majorId) {
   universityApi.selectEstablishUniversityList(majorId,universityFormS.value.universityName,universityFormS.value.universityArea,universityFormS.value.universityType,universityFormS.value.universityLevel,
   universityFormS.value.universityFeatures,currentPage3.value,pageSize3.value
   ).then(res => {
     console.log(res.data)
     universityList.value = res.data.records;

     count.value = res.data.total;
   })
 }else {
   console.log('majorId',majorId)
   await universityApi.selectUniversityListByName(universityFormS.value.universityName, universityFormS.value.universityArea,universityFormS.value.universityLevel,universityFormS.value.universityType,universityFormS.value.universityFeatures,currentPage3.value, pageSize3.value).then(res => {
     universityList.value = res.data.records;
     count.value = res.data.total;
   })
 }
}
select();

let editDialog = ref(false);
const clickUniversity = (university) => {
  isInsert.value=false
  if(userStore.usersRole==='admin' || userStore.usersRole==='seniorAdmin' && route.path==='/adminIndex'){
    editDialog.value=true;
    universityForm.value.universityId=university.universityId;
    universityForm.value.universitySchoolBadge=university.universitySchoolbadge;
    universityForm.value.universityName=university.universityName;
    universityForm.value.universityArea=university.universityArea;
    universityForm.value.universityLevel=university.universityLevel;
    universityForm.value.universityType=university.universityType;
    universityForm.value.universityFeatures =
        university.universityFeatures?.split(',')?.filter(Boolean) || [];
    universityForm.value.universityIntroduction=university.universityIntroduction;
    universityForm.value.universityPhone=university.universityPhone;
    universityForm.value.universityEmail=university.universityEmail;
    universityForm.value.universityWebsite=university.universityWeb;
  }else {
    window.open(
        `${window.location.origin}/university/detail/${university.universityId}`,
        '_blank'
    )

  }
}
const handleUploadSuccess = (response) => {
  universityForm.value.universitySchoolBadge = response.data
}
const token = localStorage.getItem('accessToken');
const url = serverUrl.url;
const isInsert = ref(false);
const updateUniversity = () => {

  const formData = {
    ...universityForm.value,
    universityFeatures: universityForm.value.universityFeatures.join(','),
    // 处理空字符串转为null
    universityPhone: universityForm.value.universityPhone || null,
    universityEmail: universityForm.value.universityEmail || null,
    universityWebsite: universityForm.value.universityWebsite || null
  };

if (isInsert.value) {
  universityApi.insert(formData).then(res => {
    ElMessage.success('添加成功')
    editDialog.value = false;
    select();
  }).catch(error => {
    console.error(error);
    ElMessage.error('添加失败')
  })
}else
  {
  universityApi.updateUniversity(formData).then(res => {
    ElMessage.success('编辑成功')
    editDialog.value = false;
    select();
  }).catch(error => {
    console.error(error);
    ElMessage.error('编辑失败')
  })
}
}
const featuresOptions = ref([
  {
    value: '',
    label: '全部'
  },
  {
    value: '985',
    label: '985'
  },
  {
    value: '211',
    label: '211'
  },
  {
    value: '双一流',
    label: '双一流'
  },
  {
    value: '强基计划',
    label: '强基计划'
  },
])
const areaOptions = ref([
  {
    value: '',
    label: '全部'
  },
  {
    value: '安徽',
    label: '安徽'
  },
  {
    value: '澳门',
    label: '澳门'
  },
  {
    value: '北京',
    label: '北京'
  },
  {
    value: '重庆',
    label: '重庆'
  },
  {
    value: '福建',
    label: '福建'
  },
  {
    value: '甘肃',
    label: '甘肃'
  },
  {
    value: '广东',
    label: '广东'
  },
  {
    value: '广西',
    label: '广西'
  },
  {
    value: '贵州',
    label: '贵州'
  },
  {
    value: '海南',
    label: '海南'
  },
  {
    value: '河北',
    label: '河北'
  },
  {
    value: '黑龙江',
    label: '黑龙江'
  },
  {
    value: '河南',
    label: '河南'
  },
  {
    value: '湖北',
    label: '湖北'
  },
  {
    value: '湖南',
    label: '湖南'
  },
  {
    value: '江苏',
    label: '江苏'
  },
  {
    value: '江西',
    label: '江西'
  },
  {
    value: '吉林',
    label: '吉林'
  },
  {
    value: '辽宁',
    label: '辽宁'
  },
  {
    value: '内蒙古',
    label: '内蒙古'
  },
  {
    value: '宁夏',
    label: '宁夏'
  },
  {
    value: '青海',
    label: '青海'
  },
  {
    value: '山西',
    label: '山西'
  },
  {
    value: '山东',
    label: '山东'
  },
  {
    value: '上海',
    label: '上海'
  },
  {
    value: '四川',
    label: '四川'
  },
  {
    value: '台湾',
    label: '台湾'
  },
  {
    value: '香港',
    label: '香港'
  },
  {
    value: '海南',
    label: '海南'
  },
  {
    value: '陕西',
    label: '陕西'
  },
  {
    value: '天津',
    label: '天津'
  },
  {
    value: '西藏',
    label: '西藏'
  },
  {
    value: '新疆',
    label: '新疆'
  },
  {
    value: '云南',
    label: '云南'
  },
  {
    value: '浙江',
    label: '浙江'
  },

])

const levelOptions = ref([
  {
    value: '',
    label: '全部'
  },
  {
    value: '本科',
    label: '本科'
  },
  {
    value: '专科(高职)',
    label: '专科(高职)'
  },

])

const typeOptions = ref([
  {
    value: '',
    label: '全部'
  },
  {
    value: '理工',
    label: '理工'
  },
  {
    value: '综合',
    label: '综合'
  },
  {
    value: '农林',
    label: '农林'
  },
  {
    value: '医药',
    label: '医药'
  },
  {
    value: '师范',
    label: '师范'
  },
  {
    value: '财经',
    label: '财经'
  },
  {
    value: '政法',
    label: '政法'
  },
  {
    value: '艺术',
    label: '艺术'
  },
  {
    value: '体育',
    label: '体育'
  },
  {
    value: '军事',
    label: '军事'
  }
])

const suggestions = ref([]);
// 带防抖的搜索建议查询
const querySearchAsync = (handle)=>  debounce(async (queryString, cb) => {
  if (queryString) {
    const name= ref('')
    try {
      if (handle==='establishmentUniversity') {
        name.value=addEstablishmentUniversityName.value
      }else {name.value=universityFormS.value.universityName}
      console.log('获取建议:', name.value);
        const Features = universityForm.value.universityFeatures.join(',')
        const res = await universityApi.selectUniversityListByName(name.value, universityFormS.value.universityArea, universityFormS.value.universityLevel, universityFormS.value.universityType, Features, currentPage3.value, pageSize3.value)
        console.log('获取建议:', res.data.records);
        suggestions.value = res.data.records.map(item => {
          return {value: item.universityName,
            id:item.universityId,
            ...item};
        });
        cb(suggestions.value);

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
// 实际搜索处理
const handleSearch = () => {
  select();
};

const deleteUniversity = () => {
  handleDeleteDialog=false
  editDialog=false
  universityApi.deleteUniversity(universityForm.value.universityId).then(res => {
    ElMessage.success('删除成功')
    select();
  }).catch(error => {
    console.error(error);
    ElMessage.error('删除失败')
  })
}

let handleDeleteDialog = ref(false);
const deleteEstablishmentUniversityDialog = ref(false);
const currentDeleteId = ref(null);
const deleteEstablishmentUniversity = () => {
  deleteEstablishmentUniversityDialog.value=false
  major_university.deleteEstablishUniversity(majorId,currentDeleteId.value).then(res => {
    ElMessage.success('删除成功')
    select();
  }).catch(error => {
    console.error(error);
    ElMessage.error('删除失败')
  })
}
const addEstablishmentUniversityDialog = ref(false);
const addEstablishmentUniversityName = ref('');
const addEstablishmentUniversityNames = ref([]);
const addEstablishmentUniversityIds = ref([]);
const handleAddEstablishmentUniversityNames = (item) => {
 addEstablishmentUniversityNames.value.push(item.value);
 addEstablishmentUniversityIds.value.push(item.id);
}
const addEstablishmentUniversity = () => {

  addEstablishmentUniversityDialog.value=false

  const requestData = {
    universityIds: addEstablishmentUniversityIds.value,
    majorId:majorId
  };

  major_university.insertEstablishUniversity(requestData).then(res => {
    ElMessage.success('添加成功')
    select();
  }).catch(error => {
    console.error(error);
    ElMessage.error('添加失败')
  })
}
// 在组件中新增验证规则
const rules = {
  universityEmail: [
    {
      type: 'email',
      message: '请输入有效的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  universityPhone: [
    {
      pattern: /^(\d{3,4}-)?\d{7,8}$/,
      message: '请输入有效的电话号码',
      trigger: ['blur', 'change']
    }
  ],
  universityWebsite: [
    {
      pattern: /^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/,
      message: '请输入有效的网址',
      trigger: ['blur', 'change']
    }
  ]
}

</script>

<template>
    <el-autocomplete
        v-model="universityFormS.universityName"
        :fetch-suggestions="querySearchAsync('')"
        placeholder="请输入院校名称"
        @select="handleSearch"
        style="width: 300px; margin: 20px"
    ></el-autocomplete>
    <el-button type="primary" @click="handleSearch">搜索</el-button>
    <el-button type="primary" v-if="majorId!=null && route.path==='/adminIndex'" @click="addEstablishmentUniversityDialog=true">添加开设院校</el-button>
    <el-button type="primary" v-if="route.path==='/adminIndex' && majorId===null" @click="()=>{
      editDialog=true;
      isInsert=true;
      universityForm.universityId=null;
      universityForm.universitySchoolBadge=null;
      universityForm.universityName=null;
      universityForm.universityArea=null;
      universityForm.universityLevel=null;
      universityForm.universityType=null;
      universityForm.universityFeatures=[];
      universityForm.universityIntroduction=null; 
      universityForm.universityPhone=null;
      universityForm.universityEmail=null;
      universityForm.universityWebsite=null;
    }">新增院校</el-button>

<div>
  <el-select-v2
      v-model="universityFormS.universityArea"
      :options="areaOptions"
      placeholder="请选择院校区域"
      style="width: 240px; vertical-align: middle"

      @change="handleSearch"
  />


  <el-select-v2
      v-model="universityFormS.universityLevel"
      :options="levelOptions"
      placeholder="请选择院校等级"
      style="width: 240px; vertical-align: middle"

      @change="handleSearch"
  />

  <el-select-v2
      v-model="universityFormS.universityType"
      :options="typeOptions"
      placeholder="请选择院校类型"
      style="width: 240px; vertical-align: middle"
      @change="handleSearch"
  />

  <el-select-v2
      v-model="universityFormS.universityFeatures"
      :options="featuresOptions"
      placeholder="请选择院校特色"
      style="width: 240px; vertical-align: middle"
      @change="handleSearch"
  />
</div>
<div v-if="universityList" class="university-block">
  <div v-for="university in universityList" class="university-block-children" @click="clickUniversity(university)"
       @mouseenter="hoverUniversityId = university.universityId"
       @mouseleave="hoverUniversityId = null"
  >
    <div v-if="majorId && hoverUniversityId === university.universityId"
         class="delete-button"
         @click.stop="currentDeleteId = university.universityId; deleteEstablishmentUniversityDialog=true">
      ×
    </div>
    <img :src="serverUrl.url + '/images/university/' + university.universitySchoolbadge"
         alt="校徽"
         style="width: 100px; height: 100px"/> <br>
    {{university.universityName}} <br>
    {{university.universityArea}} <br>
    <p>{{university.universityLevel}}  {{university.universityType}} </p>
    {{university.universityFeatures}}

  </div>
</div>

  <div v-else>
    <el-skeleton :rows="50"></el-skeleton>
  </div>
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
<el-dialog title="编辑院校" v-model="editDialog">
  <img :src="serverUrl.url + '/images/university/' + universityForm.universitySchoolBadge"
       alt="校徽"
       style="width: 100px; height: 100px"/> <br>

  <el-form-item label="更换校徽" prop="universitySchoolBadge">
    <el-upload
        :action="url + '/api/university/uploadImg'"
        :headers="{ 'Authorization': 'Bearer '+token,
        }"
        :data="{ universityId: universityForm.universityId ? universityForm.universityId : -1,
                 universitySchoolBadge: universityForm.universitySchoolBadge}"
        :on-success="handleUploadSuccess"
        :show-file-list="false"
    >
      <el-button type="primary">选择新校徽</el-button>
    </el-upload>
  </el-form-item>

  <el-form :model="universityForm">
    <el-form-item label="院校ID" v-if="false" prop="universityId">
      <el-input v-model="universityForm.universityId" placeholder="请输入院校ID"></el-input>
    </el-form-item>
    <el-form-item label="院校名称" prop="universityName">
      <el-input v-model="universityForm.universityName" placeholder="请输入院校名称"></el-input>
    </el-form-item>
    <el-form-item label="院校地址" prop="universityArea">
      <el-input v-model="universityForm.universityArea" placeholder="请输入院校地址"></el-input>
    </el-form-item>
    <el-form-item label="院校等级" prop="universityLevel">
      <el-input v-model="universityForm.universityLevel" placeholder="请输入院校等级"></el-input>
    </el-form-item>
    <el-form-item label="院校类型" prop="universityType">
      <el-input v-model="universityForm.universityType"></el-input>
    </el-form-item>

    <!-- 新增联系信息 -->
    <el-form-item label="联系电话" prop="universityPhone">
      <el-input v-model="universityForm.universityPhone"
                placeholder="请输入联系电话（可选）"></el-input>
    </el-form-item>
    <el-form-item label="联系邮箱" prop="universityEmail">
      <el-input v-model="universityForm.universityEmail"
                placeholder="请输入联系邮箱（可选）"></el-input>
    </el-form-item>
    <el-form-item label="官方网站" prop="universityWebsite">
      <el-input v-model="universityForm.universityWebsite"
                placeholder="请输入官网网址（可选）"></el-input>
    </el-form-item>
    <el-form-item label="院校特色" prop="universityFeatures">
      <el-select-v2
          v-model="universityForm.universityFeatures"
          :options="featuresOptions"
          placeholder="Please select"
          style="width: 240px; vertical-align: middle"
          multiple
          clearable
      />
    </el-form-item>
    <el-form-item label="院校介绍" prop="universityIntroduction">
      <el-input v-model="universityForm.universityIntroduction" placeholder="请输入院校介绍" type="textarea" :autosize="{ minRows: 4, maxRows: 8 }"></el-input>
    </el-form-item>
    <div class="edit-button-block">
      <el-form-item>
        <el-button type="primary" @click="updateUniversity">保存</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="isInsert===false" type="danger" @click="handleDeleteDialog=true" style="margin-left: 100px">删除</el-button>
      </el-form-item>
    </div>
  </el-form>
</el-dialog>
  <el-dialog title="确认删除院校吗？" v-model="handleDeleteDialog">
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleDeleteDialog=false">取消</el-button>
      <el-button type="primary" @click="deleteUniversity">确认</el-button>
    </span>
  </el-dialog>
  <el-dialog title="确认删除专业开设院校吗？" v-model="deleteEstablishmentUniversityDialog">
    <span slot="footer" class="dialog-footer">
      <el-button @click="deleteEstablishmentUniversityDialog=false">取消</el-button>
      <el-button type="primary" @click="deleteEstablishmentUniversity">确认</el-button>
    </span>
  </el-dialog>
  <el-dialog title="添加开设院校" v-model="addEstablishmentUniversityDialog">
    <el-autocomplete
        v-model="addEstablishmentUniversityName"
        :fetch-suggestions="querySearchAsync('establishmentUniversity')"
        placeholder="请输入院校名称"
        @select="handleAddEstablishmentUniversityNames"
        style="width: 300px; margin: 20px"
    ></el-autocomplete>
    <el-button type="primary" @click="handleAddEstablishmentUniversityNames">添加</el-button>
    <el-input-tag v-model="addEstablishmentUniversityNames"
                  clearable
                  :trigger="null"
                  draggable
    ></el-input-tag>
    <el-button type="primary" @click="addEstablishmentUniversity">保存</el-button>
  </el-dialog>
</template>

<style scoped>
.university-block {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  padding: 10px;
}
.university-block-children {
  width: 220px;
  height: 250px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 10px;
  margin-right: 10px;
  padding: 10px;
  box-sizing: border-box;
  transition: transform 0.3s ease-in-out; /* 新增过渡属性 */
  position: relative;
}
.university-block-children:hover {
  cursor: pointer; /* 新增指针样式 */
  transform: scale(1.10); /* 调整缩放值为更合理的幅度 */
  z-index: 1;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1); /* 可选：添加阴影增强立体感 */
}
.el-pagination {
  justify-content: center;
}
.edit-button-block {
  display: flex;
  justify-content: center;
}
.delete-button {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 20px;
  height: 20px;
  background: #f56c6c;
  color: white;
  border-radius: 50%;
  text-align: center;
  line-height: 20px;
  cursor: pointer;
  z-index: 2;
  transition: all 0.3s;
}

</style>
<script setup>
import {ref, onMounted} from 'vue'
import majorApi from '../../api/major.js'
import router from '../router/index.js'
import Header from "../components/header.vue";
import * as echarts from 'echarts';
// 专业列表数据
const majorList = ref([])
const total = ref(0)
const pageSize = ref(12)
const currentPage = ref(1)
const searchName = ref('')
const category = ref('')
// 加载专业数据
const loadMajors = async () => {
  const res = await majorApi.selectMajorListByName(
    searchName.value,
    currentPage.value,
    pageSize.value
  )
  majorList.value = res.data.records
  total.value = res.data.total
}

const loadMajorsByNameAndCategory = async () => {

  const res = await majorApi.selectMajorListByNameAndCategory({ name: searchName.value,
    category: category.value,
    page: currentPage.value,
    size: pageSize.value})
  majorList.value = res.data.records
  total.value = res.data.total
}

// 跳转详情页
const toDetail = (majorName) => {
  const roueData = router.resolve({
    path: `/major/detail/${majorName}`
  })
  window.open(roueData.href, '_blank')
}

// 初始加载


const majorTypeChart = ref(null);

// 树图配置方法
const initMajorTypeChart = async () => {
  try {
    const res = await majorApi.selectMajorTypeCount();
    const data = {
     name: '艺术类专业',
      children: res.data.map(type => ({
        name: type.typeName,
        value: type.typeCount, // 直接使用类型下的专业总数
        children: type.majors.map(major => ({
          name: major.majorName // 只需专业名称
        }))
      }))
    };

    const chart = echarts.init(majorTypeChart.value);
    const option = {
      tooltip: {
        trigger: 'item',
        triggerOn: 'mousemove'
      },
      series: [
        {
          type: 'tree',
          data: [data],
          top: '5%',
          left: '15%',
          bottom: '5%',
          right: '15%',
          symbolSize: 7 ,
          label: {
            position: 'left',
            verticalAlign: 'middle',
            align: 'right',
            fontSize: 14
          },
          leaves: {
            label: {
              position: 'right',
              verticalAlign: 'middle',
              align: 'left'
            }
          },
          expandAndCollapse: true,
          animationDuration: 550,
          animationDurationUpdate: 750
        }
      ]
    };
    chart.setOption(option);
  } catch (error) {
    console.error('加载专业类型分布失败:', error);
  }
};

const majorChartContainer = ref(null);

// 加载并渲染专业院校数量图表的方法
const loadMajorUniversityChart = async () => {
  if (!majorChartContainer.value) {
    console.error("图表容器尚未准备好");
    return;
  }

  try {
    // 假设 majorApi.getMajorUniversityCounts() 返回所需数据
    // 格式: [{ majorName: '专业A', count: 10 }, ...]
    const res = await majorApi.selectMajorListCount();
    const chartData = res.data; // 假设接口返回的数据结构

    if (!chartData || chartData.length === 0) {
      console.warn("没有获取到专业院校数量数据");
      // 可以选择显示一个空状态
      return;
    }

    // 准备 ECharts 配置项
    const majorNames = chartData.map(item => item.majorName);
    const counts = chartData.map(item => item.count);

    const myChart = echarts.init(majorChartContainer.value);
    const option = {
      title: {
        text: '各艺术类专业开设院校数量统计',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        boundaryGap: [0, 0.01]
      },
      yAxis: {
        type: 'category',
        data: majorNames // 专业名称作为 Y 轴
      },
      series: [
        {
          name: '开设院校数量',
          type: 'bar', // 设置为柱状图
          data: counts // 院校数量作为数据
        }
      ]
    };

    myChart.setOption(option);

    // 添加窗口大小调整监听
    window.addEventListener('resize', () => {
      myChart.resize();
    });

  } catch (err) {
    console.error("加载专业院校数量图表失败:", err);
    // 可以显示错误提示
  }
};


// 在生命周期钩子中调用
onMounted(async () => {
  // ... existing code ...
  await initMajorTypeChart();
  await loadMajors();
  await  loadMajorUniversityChart()
});



const categoryOptions = [
  { value: '', label: '全部' },
 { value:'艺术学理论类', label:'艺术学理论类'},
 { value:'音乐与舞蹈学', label:'音乐与舞蹈学类'},
 { value:'戏剧与影视学', label:'戏剧与影视学类'},
 { value:'美术学', label:'美术学类'}, 
 { value:'设计学', label:'设计学类'},
]
</script>

<template>
  <Header/>
  <div class="major-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-select v-model="category" @change="loadMajorsByNameAndCategory" style="width: 100px; margin-right: 20px;margin-bottom: 20px">
        <el-option
          v-for="option in categoryOptions" :key="option.value" :value="option.value" :label="option.label">
        </el-option>
      </el-select>
      <el-input
        v-model="searchName"
        placeholder="输入专业名称搜索"
        @change="loadMajorsByNameAndCategory"
        style="width: 300px; margin-bottom: 20px"
      >
        <template #append>
          <el-button @click="loadMajorsByNameAndCategory">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 专业列表 -->
    <el-row :gutter="20">
      <el-col
        v-for="major in majorList"
        :key="major.majorId"
        :span="6"
        class="major-card"
      >
        <el-card @click="toDetail(major.majorName)">
          <h3 class="major-name">{{ major.majorName }}</h3>
          <div class="major-info">
            <p>专业代码：{{ major.majorCode }}</p>
            <p>专业类型：{{ major.majorCategory}}</p>
            <p>学制年限：{{ major.duration }}</p>
            <p>专业简介：{{ major.majorIntroduction }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分页 -->
    <div class="page-block">
      <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          v-model:current-page="currentPage"
          :page-size="pageSize"
          @current-change="loadMajors"
          style="margin-top: 20px"
      />
    </div>
    </div>

  <el-card class="box-card chart-card" style="margin-top: 20px;">
    <template #header>
      <div class="card-header">
        <span>专业类型分布</span>
      </div>
    </template>
    <div ref="majorTypeChart" style="width: 100%; height: 750px;"></div>
  </el-card>

 <!-- 新增：专业院校数量图表 -->
  <el-card class="box-card chart-card" style="margin-top: 20px;">
           <template #header>
             <div class="card-header">

             </div>
           </template>
           <div ref="majorChartContainer" style="width: 100%; height: 600px;"></div>
        </el-card>

</template>

<style scoped>
.major-container {
  padding: 20px;
}

.major-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.major-card:hover {
  transform: translateY(-5px);
}

.major-name {
  color: #409EFF;
  margin-bottom: 15px;
}

.major-info p {
  text-align: left;
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}
.page-block {
  display: flex;
  justify-content: center;
}

/* 新增：图表卡片样式 */

</style>
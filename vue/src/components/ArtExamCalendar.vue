<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Calendar, Search, School, Document, Edit, Aim, Trophy, Reading, Promotion, Select } from '@element-plus/icons-vue';

const router = useRouter();
const props = defineProps({
  showTitle: {
    type: Boolean,
    default: true
  }
});

// 艺考月历数据
const artExamCalendar = [
  {
    period: '4月-7月',
    title: '搜集信息——确立艺考目标',
    content: '大部分高二生已完成文化课新知识学习，开始准备高三一轮复习。这是新艺考周期的开始，建议：1、与家长、班主任沟通，分析文化课和专业能力优劣势，确定是否走艺考之路；2、确立专业方向，了解并树立目标院校；3、与父母一起考察艺考机构，选择适合的老师。',
    icon: 'Search'
  },
  {
    period: '7月-9月',
    title: '最佳集训时期',
    content: '7月起参加艺考培训机构，及早定位专业考学方向。这个时期以夯实基础为主，根据专业能力调整学习重心；同时保持文化课学习习惯，避免暑期后脱离文化课学习思维。',
    icon: 'School'
  },
  {
    period: '10月',
    title: '关注官方艺考信息发布',
    content: '关注艺术类专业相关政策及省统考大纲，尤其是专业考试内容。各省市将于10月下旬陆续发布统考大纲，注意政策变化、专业考试曲目限定、评分标准等。掌握往年题型并进行重点学习，多练习必考考点，进行模拟考试。',
    icon: 'Document'
  },
  {
    period: '10月-11月',
    title: '各省统考报名——备战统考',
    content: '关注报名时间及考试时间。各省市专业统考大多安排在11月底-1月进行。越来越多院校承认统考成绩录取，且要求必须在省内专业统考过线才可参加校考，所以不要忽视统考的重要性！',
    icon: 'Edit'
  },
  {
    period: '11月-12月',
    title: '统考考试——备战校考',
    content: '依照省份统考时间，突击专业，参加省统考，同时备战校考。艺术类考试大多有一试、二试，进入二试后考生水平相差不大，心态成为关键因素。校考难度更高，要求更高的艺术修养和综合素质。',
    icon: 'Aim'
  },
  {
    period: '1月-3月',
    title: '全力以赴——参加校考',
    content: '制定详细行程规划表。1-3月是校考季，各院校时间不同，要提升专业课并做好时间规划。1、制定校考行程，熟悉各院校报名时间、地点、流程等；2、合理饮食，规范作息，调整心态。',
    icon: 'Trophy'
  },
  {
    period: '3月-5月',
    title: '文化课冲刺',
    content: '校考结束后，全力冲刺文化课。有侧重地复习优势和劣势学科。4月校考成绩集中公布，及时查询。5月是文化课学习最后一个月，查漏补缺，分析强弱科，注意某些学校专业录取的单科限制。',
    icon: 'Reading'
  },
  {
    period: '6月',
    title: '决战高考',
    content: '参加全国高考。考前几天降低学习强度，不要熬夜，注意作息和饮食规律，保持良好心态，轻松上阵，发挥应有水平。',
    icon: 'Promotion'
  },
  {
    period: '6月底开始',
    title: '填报志愿',
    content: '高考成绩公布后，结合已获得的校考合格证和高考分数，以及所属批次进行志愿填报。报志愿等于二次高考，要格外注意。',
    icon: 'Select'
  }
];

// 当前展示的月历详情
const currentCalendarDetail = ref(null);

// 对话框显示状态
const dialogVisible = computed({
  get: () => currentCalendarDetail.value !== null,
  set: (value) => {
    if (!value) {
      currentCalendarDetail.value = null;
    }
  }
});

// 显示月历详情
const showCalendarDetail = (item) => {
  currentCalendarDetail.value = item;
};

// 关闭月历详情
const closeCalendarDetail = () => {
  currentCalendarDetail.value = null;
};

// 获取图标组件
const getIconComponent = (iconName) => {
  const iconMap = {
    'Search': Search,
    'School': School,
    'Document': Document,
    'Edit': Edit,
    'Aim': Aim,
    'Trophy': Trophy,
    'Reading': Reading,
    'Promotion': Promotion,
    'Select': Select
  };
  return iconMap[iconName] || Search;
};
</script>

<template>
  <div class="art-exam-calendar">
    <div v-if="showTitle" class="section-title">
      <el-icon><Calendar /></el-icon>
      <h2>艺考月历</h2>
      <p class="section-subtitle">艺考生全年规划指南，助你合理安排备考时间</p>
    </div>
    
    <div class="timeline-container">
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in artExamCalendar"
          :key="index"
          :type="index % 2 === 0 ? 'primary' : 'success'"
          :hollow="currentCalendarDetail !== item"
          :timestamp="item.period"
        >
          <el-card class="timeline-card" @click="showCalendarDetail(item)">
            <div class="timeline-title">
              <el-icon><component :is="getIconComponent(item.icon)" /></el-icon>
              <span>{{ item.title }}</span>
            </div>
            <div class="timeline-brief">{{ item.content.substring(0, 50) }}...</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
    
    <!-- 月历详情弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentCalendarDetail?.title"
      width="50%"
      @close="closeCalendarDetail"
    >
      <div class="calendar-detail-header">
        <el-tag type="primary">{{ currentCalendarDetail?.period }}</el-tag>
      </div>
      <div class="calendar-detail-content">
        <p>{{ currentCalendarDetail?.content }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeCalendarDetail">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.art-exam-calendar {
  margin: 20px 0;
}

.section-title {
  text-align: center;
  margin-bottom: 30px;
  position: relative;
  padding-bottom: 15px;
}

.section-title h2 {
  font-size: 28px;
  color: #303133;
  margin: 10px 0;
}

.section-title .el-icon {
  font-size: 32px;
  color: #409EFF;
}

.section-subtitle {
  color: #909399;
  font-size: 16px;
  margin: 5px 0 0;
}

.timeline-container {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.timeline-card {
  cursor: pointer;
  transition: all 0.3s;
}

.timeline-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.timeline-title {
  display: flex;
  align-items: center;
  font-weight: bold;
  margin-bottom: 8px;
  color: #409EFF;
}

.timeline-title .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.timeline-brief {
  color: #606266;
  font-size: 14px;
}

.calendar-detail-header {
  margin-bottom: 20px;
}

.calendar-detail-content {
  line-height: 1.8;
  color: #606266;
}

.calendar-detail-content p {
  margin-bottom: 15px;
  text-indent: 2em;
}
</style>
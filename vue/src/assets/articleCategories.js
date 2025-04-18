const formatArticleCategories = (type) => {
    const typeMap = {
      'admissionsInformation': '招生信息',
      'notice': '考试通知',
      'policy': '政策解读',
      'guide': '备考指南'
    };
    return typeMap[type] || '其他';
  }

  const categories = [
    {label: '全部', value: ''},
    {label: '我发布的', value: 'myPublish'},
    { label: '招生信息', value: 'admissionsInformation' },
    { label: '考试通知', value: 'notice' },
    { label: '政策解读', value: 'policy' },
    { label: '备考指南', value: 'guide' }
  ]

  export default {
    categories,
    formatArticleCategories
  };
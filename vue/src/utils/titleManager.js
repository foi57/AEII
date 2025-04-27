// 网站标题管理工具
const siteName = '艺考信息网'


/**
 * 设置页面标题
 * @param {string} pageTitle 页面标题
 * @param {boolean} includeSiteName 是否包含网站名称
 */
export function setTitle(pageTitle, includeSiteName = false) {
  if (includeSiteName) {
    document.title = pageTitle ? `${pageTitle} - ${siteName}` : siteName
  } else {
    document.title = pageTitle || siteName
  }
}

export default {
  setTitle
}
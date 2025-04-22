import request from "../request";

export default {
    /**
     * 获取热门文章列表
     */
    getHotArticleList() {
        return request({
            url: '/hotArticle/list',
            method: 'get'
        });
    },

    /**
     * 添加热门文章
     * @param {number} articleId 文章ID
     */
    addHotArticle(articleId) {
        return request({
            url: '/hotArticle/add',
            method: 'post',
            data: { articleId } // 发送包含 articleId 的对象
        });
    },

    /**
     * 删除热门文章
     * @param {number} hotArticleId 热门文章记录ID
     */
    deleteHotArticle(hotArticleId) {
        return request({
            url: `/hotArticle/delete/${hotArticleId}`,
            method: 'delete'
        });
    },

    /**
     * 移动热门文章排序
     * @param {number} hotArticleId 热门文章记录ID
     * @param {string} direction 移动方向 ('up' 或 'down')
     */
    moveHotArticle(hotArticleId, direction) {
        return request({
            url: `/hotArticle/move/${hotArticleId}`,
            method: 'put', // 使用 PUT 请求
            data: { direction } // 发送包含 direction 的对象
        });
    }
}
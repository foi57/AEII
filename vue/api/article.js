import request from "../request.js";

export default {
    publish(data) {
        return request({
            url: '/article/insert',
            method: 'post',
            data
        })
    },
    uploadImage(formData) {
        console.log('file',formData)
        return request({
            url: '/article/upload/image',
            method: 'post',
            data: formData,
        })
    },
    selectArticleList(data) {
        return request({
            url: '/article/select',
            method: 'post',
            data
        })
    },
    selectArticleDetail(id) {
        return request({
            url: '/article/detail/' + id,
            method: 'get',
        })
    },
    updateArticle(data) {
        return request({
            url: '/article/update',
            method: 'POST',
            data
        })
    },
    deleteArticle(id) {
        return request({
            url: '/article/delete/' + id,
            method: 'delete',
        })
    },
    selectArticlesByCategoryUniversityName(articleType,universityName,pageNum,pageSize) {
        return request({
            url: '/article/selectArticlesByCategoryUniversityName',
            method: 'GET',
            params: {
                articleType,
                universityName,
                pageNum,
                pageSize
            }
        })
    },
    // 高级搜索文章
    advancedSearchArticles(data) {
        return request({
            url: '/article/advancedSearch',
            method: 'get',
            params: {
                keyword: data.keyword,
                type: data.articleType,
                startDate: data.startDate,
                endDate: data.endDate,
                pageNum: data.pageNum,
                pageSize: data.pageSize
            }
        })
    }
}

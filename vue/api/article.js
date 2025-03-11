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
    }
}

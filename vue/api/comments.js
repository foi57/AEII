import request from "../request.js";
export default {
    insertComment(data) {
        return request({
            url: '/comments/insert',
            method: 'post',
            data
        })
    },
    uploadImage(formData) {
        console.log('file',formData)
        return request({
            url: '/comment/upload/image',
            method: 'post',
            data: formData,
        })
    },
    selectCommentList(data) {
        return request({
            url: '/comments/selectListByArticleId',
            method: 'post',
            data
        })
    }
}
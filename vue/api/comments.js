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
    },
    deleteComment(data) {
        return request({
            url: '/comments/delete',
            method: 'post',
            data
        }) 
    },
    // 添加点赞方法
    thumbsUp(data) {
        return request({
            url: '/comments/thumbsUp',
            method: 'post',
            data
        })
    },
    
    // 添加取消点赞方法
    cancelThumbsUp(data) {
        return request({
            url: '/comments/cancelThumbsUp',
            method: 'post',
            data
        })
    }
}
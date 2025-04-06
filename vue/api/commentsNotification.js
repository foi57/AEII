import request from '../request'
export default {
    selectCommentsNotificationByUserIdCategory(data){
        return request({
            url: '/commentsNotification/selectByUsersIdCategory',
            method: 'post',
            data: data
        })
    },
    markAsRead(data) {
        return request({
            url: '/commentsNotification/markAsRead',
            method: 'post',
            data
        })
    },
    countUnRead(data) {
        return request({
            url: '/commentsNotification/countUnRead',
            method: 'post',
            data
        })
    },
    // 新增一键已读所有通知方法
    markAllAsRead(data) {
        return request({
            url: '/commentsNotification/markAllAsRead',
            method: 'post',
            data
        })
    }
}
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
    }
}
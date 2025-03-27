import request from "../request.js";
export default {
    read(data){
        return  request({
            url: '/notificationUsers/read',
            method: 'put',
            data:data
        })
    },
    unread(userId)
    {
        return request({
            url: '/notificationUsers/unread',
            method: 'get',
            params: {
                userId: userId
            }
        })
    }
}
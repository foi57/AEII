import request from "../request";
export default {
    insert(data){
        return request({
            url:'/feedback/insert',
            method:'post',
            data:data
        }) 
    },
    upload(data){
        return request({
            url:'/feedback/upload',
            method:'post',
            data:data  
        })
    },
    select(pageNum, pageSize) {
        return request({
            url: '/feedback/select',
            method: 'get',
            params: { pageNum, pageSize }
        })
    }
}
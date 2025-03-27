import request from "../request.js";
export default {
    selectNotification(id,page,size){
    return  request({
      url: '/notification/select',
      method: 'get',
      params: {
        id: id,
        page: page,
        size: size
      }
    })
  },
}
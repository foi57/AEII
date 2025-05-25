import request from "../request.js";

export default {
    login(userName, password) {
        return request({
            url: '/users/login',
            method: 'post',
            params: {
                userName: userName,
                password: password
            }

        })
    },
    selectUser(role, page, size) {
        return request({
            url: '/users/selectByRole',
            method: 'get',
            params: {
                role: role,
                page: page,
                size: size
            }
        })
    },
    selectUserByName(name) {
        return request({
            url: '/users/selectByName',
            method: 'get',
            params: {
                name: name
            }
        })
    },
    selectUserByNamePage(name, page, size) {
        return request({
            url: '/users/selectByNamePage',
            method: 'get',
            params: {
                name: name,
                page: page,
                size: size
            }
        })
    },
    insertUser(user) {
        console.log(user)
        return request({
            url: '/users/insert',
            method: 'post',
            params:{
                name: user.userName,
                phone: user.phone,
                email: user.email,
                password: user.password,
                role: user.role
            }
        })
    },
    updateUser(user) {
        console.log(user)
        return request({
            url: '/users/update',
            method: 'post',
            params:{
                id: user.id,
                name: user.userName,
                phone: user.phone,
                email: user.email,
                role: user.role,
                changeName: user.changeName
            }
        })
    },
    deleteUser(id) {
        return request({
            url: '/users/delete',
            method: 'post',
            params:{
                id: id
            }
        })
    },
    selectPassword(name){
        return request({
            url: '/users/selectPassword',
            method: 'get',
            params:{
                name: name
            }
        })
    },
    updatePassword(name, password){
        return request({
            url: '/users/updatePassword',
            method: 'post',
            params:{
                name: name,
                password: password
            }
        })
    },
    updateAvatar(formData){

        return request({
            url: '/users/uploadAvatar',
            method: 'post',
            data: formData
        })
    },
    selectById(id){
        return request({
            url: '/users/selectById',
            method: 'get',
            params:{
                id: id
            }
        })
    },
    // 添加以下方法到现有的 user.js 文件中
    
    // 发送验证码
    sendVerificationCode(email) {
        return request({
            url: '/users/sendVerificationCode',
            method: 'post',
            params: {
                email: email
            }
        })
    },
    
    // 重置密码
    resetPassword(data) {
        return request({
            url: '/users/resetPassword',
            method: 'post',
            data
        })
    },
    updateEmail(params) {
        return request({
            url: '/users/updateEmail',
            method: 'post',
            params
        })
    }
}
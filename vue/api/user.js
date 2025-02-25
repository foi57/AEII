import request from "../request.js";

export default {
    adminLogin(userName, password) {
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
}
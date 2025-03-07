import request from "../request.js";

export default {
    selectUniversityListByName(name,area,type,level,features,page, size) {
        return request({
            url: '/university/selectUniversityList',
            method: 'get',
            params: {
                name: name,
                page: page,
                size: size,
                area: area,
                type: type,
                level: level,
                features: features
            }
        })
    },
    updateUniversity(university) {
        return request({
            url: '/university/updateUniversity',
            method: 'put',
            data: university
        })
    },
    deleteUniversity(id) {
        return request({
            url: '/university/deleteUniversity',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
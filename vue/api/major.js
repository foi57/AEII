import request from "../request.js";
export default {
    selectMajorListByName(name, page, size) {
        return request({
            url: '/major/selectMajorListByName',
            method: 'get',
            params: {
                name: name,
                page: page,
                size: size
            }
        })
    },
    updateMajor(major) {
        return request({
            url: '/major/updateMajor',
            method: 'put',
            data: major
        })
    },
    deleteMajorById(id) {
        return request({
            url: '/major/deleteMajor',
            method: 'delete',
            params: {
                id: id
            }
        })
    },
    insertMajor(major) {
        return request({
            url: '/major/insertMajor',
            method: 'post',
            data: major
        })
    },
    selectMajorListByUniversityId(universityId) {
        return request({
            url: '/major/selectMajorListByUniversityId',
            method: 'get',
            params: {
                universityId: universityId,
            }
        })
    },
    selectMajorListCount(){
        return request({
            url: '/major/selectMajorListCount',
            method: 'get',
        })
    }
}
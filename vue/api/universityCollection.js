import request from "../request.js";
export default {
    collect(data) {
        return request({
            url: '/universityCollection/insert',
            method: 'post',
            data
        })
    },
    selectCollectionByUserIdUniversityId(data) {
        return request({
            url: '/universityCollection/selectByUserIdUniversityId',
            method: 'post',
            data
        })
    },
    delete(data) {
        return request({
            url: '/universityCollection/delete',
            method: 'post',
            data
        })
    },
    selectByUserIdAndUniversity(data) {
        return request({
            url: '/universityCollection/selectByUserIdAndUniversityName',
            method: 'post',
            data
        })
    }
}
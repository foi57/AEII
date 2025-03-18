import request from "../request.js";
export default {
    deleteEstablishUniversity(majorId,universityId) {
        return request({
            url: '/universityMajor/deleteUniversityMajor',
            method: 'delete',
            params: {
                majorId,
                universityId
            }
        })
    },
    insertEstablishUniversity(majorNamesDTO) {
        console.log(majorNamesDTO)
        return request({
            url: '/universityMajor/insertUniversityMajor',
            method: 'post',
            data: majorNamesDTO
        })
    }
}
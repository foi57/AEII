import request from "../request.js";

export default {
    publish(data) {
        return request({
            url: '/article/publish',
            method: 'post',
            data
        })
    },
    uploadImage(file) {
        const formData = new FormData()
        formData.append('file', file)
        return request({
            url: '/article/uploadImage',
            method: 'post',
            data: formData,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
    }
}

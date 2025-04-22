import request from "../request";
export default {
    insertCarousel(data) {
        return request({
            url: '/carousel/insert',
            method: 'post',
            data
        })
    },
    getCarouselList() {
        return request({
            url: '/carousel/get',
            method: 'get'
        })
    },
    deleteCarousel(id) {
        return request({
            url: '/carousel/delete/' + id,
            method: 'delete',
            params: {
                id
            }
        })
    },
    moveCarousel(id, direction) {
        return request({
            url: '/carousel/move',
            method: 'put',
            params: {
                id,
                direction
            }
        })
    },
}
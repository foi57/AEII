import axios from 'axios'
import router from "/src/router/index.js";

const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
})
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    const url = request.getUri();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }else if (url.includes('admin')) {
        router.push('/admin');
    }
    return config;
}, error => {
    return  Promise.reject(error)
})
request.interceptors.response.use(response => {
    return response;
},error => {
    const url = request.getUri();
    if (error.response && error.response.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userStore')
        router.push('/adminLogin')
    }else if (url.includes('admin')) {
        router.push('/adminLogin');
    }
    return Promise.reject(error)
})
export default request
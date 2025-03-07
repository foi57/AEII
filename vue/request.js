import axios from 'axios'
import router from "/src/router/index.js";
import serverUrl from "./serverUrl.js";
const request = axios.create({
    baseURL:  serverUrl.url+'/api',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
})
request.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken')
    const url = request.getUri();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }else if (url.includes('admin')) {
        router.push('/adminLogin');
    }
    return config;
}, error => {
    return  Promise.reject(error)
})

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    })
    failedQueue = [];
}

request.interceptors.response.use(response => response,
    async error => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject })
                }).then(() => request(originalRequest))
                    .catch(err => Promise.reject(err))
            }

            originalRequest._retry = true;
            isRefreshing = true;
            const newToken = await refreshToken();
            try {

                if (newToken) {
                    // 更新后续请求头
                    originalRequest.headers.Authorization = `Bearer ${newToken}`;
                    // 重试原始请求
                    return request(originalRequest);
                }
            } catch (refreshError) {
                console.error('Token refresh failed:', refreshError);
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                router.push('/adminLogin');
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
                processQueue(null, newToken);
            }
        }
        return Promise.reject(error);
    });

async function refreshToken() {
    try {
        const response = await axios.post(serverUrl.url+'/auth/refresh', {
            refreshToken: localStorage.getItem('refreshToken')
        });

        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        return response.data.accessToken; // 返回新token用于更新请求头
    } catch (error) {
        throw error;
    }
}

export default request
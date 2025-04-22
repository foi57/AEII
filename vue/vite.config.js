import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0', // 或者 '0.0.0.0' 如果你想让局域网内其他设备访问
    port: 6175 // 修改为你想要的端口号
  }
})

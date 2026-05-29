import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue2'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true
      },
      '/doc.html': {
        target: 'http://localhost:8090',
        changeOrigin: true
      },
      '/swagger-ui': {
        target: 'http://localhost:8090',
        changeOrigin: true
      },
      '/v3/api-docs': {
        target: 'http://localhost:8090',
        changeOrigin: true
      },
      '/swagger-resources': {
        target: 'http://localhost:8090',
        changeOrigin: true
      },
      '/webjars': {
        target: 'http://localhost:8090',
        changeOrigin: true
      }
    }
  }
})

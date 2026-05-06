import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq, req) => {
            console.log('[Vite Proxy] ->', req.method, req.url)
          })
          proxy.on('proxyRes', (proxyRes, req) => {
            console.log('[Vite Proxy] <-', proxyRes.statusCode, req.url)
          })
          proxy.on('error', (err, req) => {
            console.error('[Vite Proxy] Error:', err.message, req.url)
          })
        },
      },
    },
  },
})

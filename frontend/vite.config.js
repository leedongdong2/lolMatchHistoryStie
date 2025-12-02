import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server:{
    proxy : {
       '/riot' : 'http://localhost:8080',
       '/user' : 'http://localhost:8080',
       '/oauth2': 'http://localhost:8080',
       '/board': 'http://localhost:8080',
       '/imagePath/' : 'http://localhost:8080'
    },
    historyApiFallback: true,
  }
})

import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './css/reset.css'
import App from './App.jsx'
import { UserProvider } from './page/reactContext/LoginUserContext.jsx'
//여기서는 app.jsx전체의 전역변수로 할당한다
createRoot(document.getElementById('root')).render(
    <UserProvider>
    <App />
    </UserProvider>
)

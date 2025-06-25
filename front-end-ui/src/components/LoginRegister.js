import React, { useState } from 'react';
import Login from './Login';
import Register from './Register';
import '../styles/LoginRegisterStyles.css';

function LoginRegister() {
  const [isLogin, setIsLogin] = useState(true);

  return (
    <div className="container d-flex justify-content-center align-items-center vh-100">
      <div className="bg-white p-4 rounded shadow">
        {isLogin ? (
          <Login onSwitchToRegister={() => setIsLogin(false)} />
        ) : (
          <Register onSwitchToLogin={() => setIsLogin(true)} />
        )}
      </div>
    </div>
  );
}

export default LoginRegister;
import React, { useState } from 'react';
import { login } from '../services/api';
import Swal from 'sweetalert2';

function Login({ onSwitchToRegister }) {
  const [emailOrUsername, setEmailOrUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = async(e) => {
    e.preventDefault();
    try {
      const data = await login(emailOrUsername, password);
      if (data.token === null) {
        Swal.fire({
          icon: 'error',
          title: 'Login Failed',
          text: 'Please check your email/username and password.',
        });
      } else {
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        window.location.href = '/';
      }
    } catch (error) {
      Swal.fire({
        icon: 'error',
        title: 'Login Failed',
        text: 'Please check your email/username and password.',
      });
    }
  };

  return (
    <form onSubmit={handleLogin} className="needs-validation">
      <h2 className="mb-4">Login</h2>
      <div className="mb-3">
        <input
          type="text"
          className="form-control"
          placeholder="Email / Username"
          value={emailOrUsername}
          onChange={(e) => setEmailOrUsername(e.target.value)}
          required
        />
      </div>
      <div className="mb-3">
        <input
          type={showPassword ? 'text' : 'password'}
          className="form-control"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>
      <div className="mb-3 form-check">
        <input
          type="checkbox"
          className="form-check-input"
          id="showPassword"
          checked={showPassword}
          onChange={() => setShowPassword(!showPassword)}
        />
        <label className="form-check-label" htmlFor="showPassword">Show Password</label>
      </div>
      <button type="submit" className="btn btn-primary w-100 mb-2">Login</button>
      <button type="button" className="btn btn-secondary w-100" onClick={onSwitchToRegister}>
        Register
      </button>
    </form>
  );
}

export default Login;

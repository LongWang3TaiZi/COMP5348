import React, { useState } from 'react';
import Swal from 'sweetalert2';
import { register } from '../services/api';

function Register({ onSwitchToLogin }) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleRegister = async (e) => {
    e.preventDefault();
    if(password.length < 8) {
      Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: 'Passwords length must at least 8!',
      });
      return;
    }
    if (password !== confirmPassword) {
      Swal.fire({
        icon: 'error',
        title: 'Oops...',
        text: 'Passwords do not match!',
      });
      return;
    }
    try {
      const data = await register(username, email, password);

      if (data.result != null && data.result.responseCode === "A0") {
        Swal.fire({
          icon: 'success',
          title: 'Success',
          text: 'Registration successful!',
        });
        // jump back to the login page
        onSwitchToLogin();
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Register Failed',
          text: data.result.message,
        });
      }
    } catch (error) {
      Swal.fire({
        icon: 'error',
        title: 'Registration Failed',
        text: error.message || 'An error occurred during registration.',
      });
    }
  };

  return (
    <form onSubmit={handleRegister} className="needs-validation">
      <h2 className="mb-4">Register</h2>
      <div className="mb-3">
        <input
          type="text"
          className="form-control"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
      </div>
      <div className="mb-3">
        <input
          type="email"
          className="form-control"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
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
      <div className="mb-3">
        <input
          type={showPassword ? 'text' : 'password'}
          className="form-control"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
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
      <button type="submit" className="btn btn-primary w-100 mb-2">Register</button>
      <button type="button" className="btn btn-secondary w-100" onClick={onSwitchToLogin}>
        Back to Login
      </button>
    </form>
  );
}

export default Register;

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../services/api';
import Swal from 'sweetalert2';

function UpdateInformation() {
  const navigate = useNavigate();
  const [user, setUser] = useState({
    email: '',
    username: '',
    password: '',
    address: {
      country: '',
      state: '',
      city: '',
      postCode: '',
      address: ''
    }
  });

  useEffect(() => {
    const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
    setUser({
      ...storedUser,
      password: '',
      address: storedUser.address || {
        country: '',
        state: '',
        city: '',
        postCode: '',
        address: ''
      }
    });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.includes('.')) {
      const [parent, child] = name.split('.');
      setUser(prevUser => ({
        ...prevUser,
        [parent]: {
          ...prevUser[parent],
          [child]: value
        }
      }));
    } else {
      setUser(prevUser => ({
        ...prevUser,
        [name]: value
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await apiClient.put('/user/update', {
        user: {
          email: user.email,
          password: user.password
        },
        address: user.address
      });
      
      if (response.data.result != null && response.data.result.responseCode != null && response.data.result.responseCode === "A4") {
        localStorage.setItem('user', JSON.stringify(response.data.user));
        
        Swal.fire({
          icon: 'success',
          title: 'Success',
          text: 'User information updated successfully',
        });
        navigate('/');
      } else {
        throw new Error(response.data.message || 'Update failed');
      }
    } catch (error) {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: error.message || 'Failed to update user information',
      });
    }
  };

  return (
    <div className="update-container mt-5">
      <h2>Update Information</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="email" className="form-label">Email</label>
          <input type="email" className="form-control" id="email" value={user.email} disabled />
        </div>
        <div className="mb-3">
          <label htmlFor="username" className="form-label">Username</label>
          <input type="text" className="form-control" id="username" value={user.username} disabled />
        </div>
        <div className="mb-3">
          <label htmlFor="country" className="form-label">Country</label>
          <input type="text" className="form-control" id="country" name="address.country" required value={user.address.country} onChange={handleChange} />
        </div>
        <div className="mb-3">
          <label htmlFor="state" className="form-label">State</label>
          <input type="text" className="form-control" id="state" name="address.state" required value={user.address.state} onChange={handleChange} />
        </div>
        <div className="mb-3">
          <label htmlFor="city" className="form-label">City</label>
          <input type="text" className="form-control" id="city" name="address.city" required value={user.address.city} onChange={handleChange} />
        </div>
        <div className="mb-3">
          <label htmlFor="postCode" className="form-label">Post Code</label>
          <input type="text" className="form-control" id="postCode" name="address.postCode" required value={user.address.postCode} onChange={handleChange} />
        </div>
        <div className="mb-3">
          <label htmlFor="address" className="form-label">Address</label>
          <input type="text" className="form-control" id="address" name="address.address" required value={user.address.address} onChange={handleChange} />
        </div>
        <button type="submit" className="btn btn-primary">Update Information</button>
      </form>
    </div>
  );
}

export default UpdateInformation;
import axios from 'axios';
import { isTokenValid } from '../utils/auth';
import Swal from 'sweetalert2';

export const API_BASE_URL = 'http://localhost:8080/comp5348';
export const IMG_API_BASE_URL = 'http://localhost:8080';

// create an axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
    (config) => {
      if (!config.url.includes('/user/login') && !config.url.includes('/user/register') && !config.url.includes('/products')) {
        if (isTokenValid()) {
          const token = localStorage.getItem('token');
          config.headers['Authorization'] = `Bearer ${token}`;
        } else {
          // invalid token, redirect to login page
          window.location.href = '/login';
        }
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
);

// add interceptors
apiClient.interceptors.response.use(
    (response) => {
      return response;
    },
    (error) => {
      if (error.response) {
        // handle 401 error
        if (error.response.status === 401) {
          // clear local storage
          localStorage.removeItem('token');

          // show error message
          Swal.fire({
            icon: 'error',
            title: 'Session Expired',
            text: 'Your session has expired. Please login again.',
            confirmButtonText: 'Go to Login'
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.href = '/login';
            }
          });
        }
        // handle 403 error
        else if (error.response.status === 403) {
          Swal.fire({
            icon: 'error',
            title: 'Access Denied',
            text: 'You do not have permission to perform this action.'
          });
        }
        // handle 404 error
        else if (error.response.status === 404) {
          Swal.fire({
            icon: 'error',
            title: 'Not Found',
            text: 'The requested resource was not found.'
          });
        }
        // handle 500 error
        else if (error.response.status >= 500) {
          Swal.fire({
            icon: 'error',
            title: 'Server Error',
            text: 'An error occurred on the server. Please try again later.'
          });
        }
        // 处理其他错误
        else {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: error.response.data?.message || 'An unexpected error occurred.'
          });
        }
      }
      // handle network error
      else if (error.request) {
        Swal.fire({
          icon: 'error',
          title: 'Network Error',
          text: 'Unable to connect to the server. Please check your internet connection.'
        });
      }
      // handle other error
      else {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'An unexpected error occurred.'
        });
      }

      return Promise.reject(error);
    }
);

export const login = async (emailOrUsername, password) => {
  try {
    const response = await apiClient.post('/user/login', { emailOrUsername, password });
    return response.data;
  } catch (error) {
    throw error; // let interceptors handle the error
  }
};

export const register = async (username, email, password) => {
  try {
    const response = await apiClient.post('/user/register', { username, email, password });
    return response.data;
  } catch (error) {
    throw error; // let interceptors handle the error
  }
};

export const quantity = async (userId, productId, quantity) => {
  try {
    const response = await apiClient.post('/order/create', { userId, productId, quantity });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : new Error('An error occurred during Purchase');
  }
};

export default apiClient;
import React, { useState, useEffect } from 'react';
import {useParams} from 'react-router-dom';
import apiClient, {IMG_API_BASE_URL} from '../services/api';
import { useNavigate } from 'react-router-dom';
import { isTokenValid } from '../utils/auth';
import { quantity } from '../services/api';
import Swal from "sweetalert2";
import data from "bootstrap/js/src/dom/data";

function ProductDetail() {
  const userString = localStorage.getItem('user');
  const user = JSON.parse(userString);
  const userId = user.id;
  const [product, setProduct] = useState(null);
  const { id } = useParams();
  const [amount, setAmount] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await apiClient.get(`/products/${id}`);
        setProduct(response.data.product);
        console.log(response.data.product);
      } catch (error) {
        console.error('Error fetching product details:', error);
      }
    };

    fetchProduct();
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault(); // 阻止表单默认提交行为

    if (!isTokenValid()) {
      navigate('/login');
      return;
    }

    try {
      const amountNum = Number(amount);

      if (amountNum === 0) {
        Swal.fire({
          icon: 'error',
          title: 'Failed',
          text: 'Please enter quantity',
        });
        return;
      }

      if (amountNum <= product.quantity) {
        const userString = localStorage.getItem('user');
        const user = JSON.parse(userString);
        const userId = user.id;

        const response = await quantity(userId, product.id, amountNum);

        if (response?.result?.responseCode === "O0") {
          Swal.fire({
            icon: 'success',
            title: 'Success',
            text: 'Purchase successfully',
          }).then((result) => {
            if (result.isConfirmed) {
              navigate('/order/' + response.order.id);
            }
          });
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Failed',
            text: response?.data?.result?.message || 'Purchase failed. Please try again.',
          });
        }
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Failed',
          text: 'Over stock, please re-enter quantity',
        });
      }
    } catch (error) {
      console.error('Error fetching order details:', error);
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'An error occurred while processing your purchase. Please try again.',
      });
    }
  };

  if (!product) {
    return <div>Loading...</div>;
  }

  return (
      <form onSubmit={handleSubmit} className="product-detail-container">
        <div className="row">
          <div className="col-md-7">
            <img
                src={`${IMG_API_BASE_URL}${product.imageUrl}`}
                alt={product.name}
                className="img-fluid product-image"
            />
          </div>
          <div className="col-md-5">
            <h1 className="product-title">{product.name}</h1>
            <p className="product-price">Price: ${product.price.toFixed(2)}</p>
            <p className="product-description">
              <strong>Description:</strong> {product.description}
            </p>
            <p className="product-quantity">
              <strong>In stock:</strong> {product.quantity}
            </p>

            <div className="mb-3">
              <input
                  type="number"
                  className="form-control"
                  placeholder="Amount"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  min="1"
                  max={product.quantity}
                  required
              />
              <button
                  type="submit"
                  className="btn btn-primary btn-lg purchase-button"
              >
                Purchase
              </button>
            </div>
          </div>
        </div>
      </form>
  );
}

export default ProductDetail;
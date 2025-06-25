import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import apiClient, { IMG_API_BASE_URL } from '../services/api';
import Swal from "sweetalert2";

function Home() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await apiClient.get('/products');
        if (!response.data || !response.data.products || response.data.products.length === 0) {
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'No products found. Please try again later.',
          });
          setProducts([]);
          return;
        }
        setProducts(response.data.products);
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    };

    fetchProducts();
  }, []);

  return (
    <div className="home container-fluid">
      <h1 className="my-4">Product List</h1>
      <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-4">
        {products.map((product) => (
          <div key={product.id} className="col">
            <div className="card h-100">
              <Link to={`/product/${product.id}`}>
                <img src={`${IMG_API_BASE_URL}${product.imageUrl}`} className="card-img-top" alt={product.name} />
              </Link>
              <div className="card-body">
                <h5 className="card-title">{product.name}</h5>
                <p className="card-text">Price: ${product.price.toFixed(2)}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Home;
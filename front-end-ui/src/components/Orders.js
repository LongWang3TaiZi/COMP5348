import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import apiClient, { IMG_API_BASE_URL } from '../services/api';

function Orders() {
    const [orders, setOrders] = useState([]);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const userString = localStorage.getItem('user');
                const user = JSON.parse(userString);
                const userId = user.id;
                const response = await apiClient.get(`/order/all/${userId}`);
                setOrders(response.data.orders);
                console.log(response.data.orders);
            } catch (error) {
                console.error('Error fetching orders:', error);
            }
        };

        fetchOrders();
    }, []);

    return (
        <div className="orders container-fluid">
            <h1 className="my-4">Your Orders</h1>
            {orders.length > 0 ? (
                <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    {orders.map((order) => (
                        <div key={order.id} className="col">
                            <div className="card h-100">
                                <Link to={`/order/${order.id}`}>
                                    <img src={`${IMG_API_BASE_URL}${order.productImgURL}`}
                                         className="card-img-top"
                                         alt={order.productName}/>
                                </Link>
                                <div className="card-body">
                                    <h5 className="card-title">{order.productName}</h5>
                                    <p className="card-text">Quantity: {order.quantity}</p>
                                    <p className="card-text">Order Status: {order.orderStatus}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <p>No orders found.</p>
            )}
        </div>
    );
}

export default Orders;
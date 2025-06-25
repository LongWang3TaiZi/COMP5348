import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import apiClient, { IMG_API_BASE_URL } from '../services/api';
import Swal from "sweetalert2";

function OrderDetail() {
    const [order, setOrder] = useState(null);
    const { id } = useParams();

    useEffect(() => {
        const fetchOrder = async () => {
            try {
                const response = await apiClient.get(`/order/${id}`);
                setOrder(response.data.order);
                console.log(response.data.order);
            } catch (error) {
                console.error('Error fetching order details:', error);
            }
        };

        fetchOrder();
    }, [id]);

    const handlePayment = async () => {
        try {
            const response = await apiClient.post(`/order/pay/${id}`);
            if (response != null && response.data.result != null && response.data.result.responseCode === "T0") {
                Swal.fire({
                    icon: 'success',
                    title: 'Success',
                    text: 'Pay order successfully',
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = `/order/${id}`;
                    }
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Failed',
                    text: response.data.result.message + ' Please try again.',
                });
            }
        } catch (error) {
            console.error('Error processing payment:', error);
        }
    };

    const handleRefund = async () => {
        try {
            const response = await apiClient.put(`/order/cancel/${id}`);
            if (response != null && response.data.result != null && response.data.result.responseCode === "O6") {
                Swal.fire({
                    icon: 'success',
                    title: 'Success',
                    text: 'Cancel order successfully',
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = `/order/${id}`;
                    }
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Failed',
                    text: response.data.result.message + ' Please try again.',
                });
            }
        }catch (error) {
            console.error('Error processing refund:', error);
        }
    };

    if (!order) {
        return <div>Loading...</div>;
    }

    return (
        <div className="order-detail-container">
            <div className="row">
                <div className="col-md-7">
                    <img src={`${IMG_API_BASE_URL}${order.productImgURL}`}
                         alt={order.productName}
                         className="img-fluid order-product-image" />
                </div>
                <div className="col-md-5">
                    <h1 className="product-title">{order.productName}</h1>
                    <p className="order-quantity"><strong>Order Quantity:</strong>{order.quantity}</p>
                    <p className="order-status"><strong>Order Status:</strong> {order.orderStatus}</p>
                    <p className="order-payment-status"><strong>Payment Status:</strong> {order.orderStatus === "CANCELLED" ? "Refund" : order.paymentStatus}</p>
                    <p className="order-delivery-status"><strong>Delivery Status:</strong> {order.deliverStatus}</p>
                    <p className="order-number"><strong>Order Tracking Number:</strong> {order.trackingNumber}</p>
                    <p className="order-created"><strong>Order Create Time:</strong> {order.createTime}</p>
                    <p className="order-updated"><strong>Order Update Time:</strong> {order.updateTime}</p>

                    {order.orderStatus === "PENDING_ON_DELIVERY" ? (
                        <button className="btn btn-danger btn-lg refund-button" onClick={handleRefund}>Cancel Order</button>
                    ) : order.orderStatus === "AWAITING_PAYMENT" ? (
                        <button className="btn btn-primary btn-lg payment-button" onClick={handlePayment}>Pay Now</button>
                    ) : null}

                </div>
            </div>
        </div>
    );
}

export default OrderDetail;
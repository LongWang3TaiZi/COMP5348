import React from 'react';
import {BrowserRouter as Router, Route, Routes, Navigate, useLocation} from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Orders from './components/Orders';
import Delivery from './components/Delivery';
import DeliveryDetail from './components/DeliveryDetail';
import LoginRegister from './components/LoginRegister';
import ProductDetail from './components/ProductDetail';
import OrderDetail from './components/OrderDetail';
import './styles/App.css';
import UpdateInformation from './components/UpdateInformation';
import {isTokenValid} from './utils/auth';

const ProtectedRoute = ({children}) => {
  if (!isTokenValid()) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

const AppContent = () => {
  const location = useLocation();
  const showNavbar = location.pathname !== '/login';

  return (
      <div className="App">
        {showNavbar && <Navbar />}
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/orders" element={
            <ProtectedRoute>
              <Orders />
            </ProtectedRoute>
          } />
          <Route path="/delivery" element={
            <ProtectedRoute>
              <Delivery />
            </ProtectedRoute>
          } />
          <Route path="/deliveryDetail/:id" element={
            <ProtectedRoute>
              <DeliveryDetail />
            </ProtectedRoute>
          } />
          <Route path="/login" element={<LoginRegister />} />
          <Route path="/product/:id" element={<ProductDetail />} />
          <Route path="/order/:id" element={<OrderDetail />} />
          <Route path="/update-info" element={<ProtectedRoute><UpdateInformation /></ProtectedRoute>} />
        </Routes>
      </div>
  );
};

function App() {
  return (
      <Router>
        <AppContent />
      </Router>
  );
}

export default App;
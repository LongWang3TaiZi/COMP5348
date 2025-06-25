import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { isTokenValid } from '../utils/auth';
import { Dropdown } from 'react-bootstrap';

function Navbar() {
  const navigate = useNavigate();
  const [showDropdown, setShowDropdown] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/');
  };

  const handleLogin = () => {
    navigate('/login');
  };

  const handleUpdateInfo = () => {
    // Implement update information logic here
    console.log('Update information');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/">BM Store</Link>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0" style={{ fontSize: '1.2rem' }}>
            <li className="nav-item px-3">
              <Link className="nav-link" to="/">Home</Link>
            </li>
            {isTokenValid() && (
              <>
                <li className="nav-item px-3">
                  <Link className="nav-link" to="/orders">Orders</Link>
                </li>
                <li className="nav-item px-3">
                  <Link className="nav-link" to="/delivery">Delivery</Link>
                </li>
                {/*<li className="nav-item px-3">*/}
                {/*  <Link className="nav-link" to="/transactions">Transactions</Link>*/}
                {/*</li>*/}
              </>
            )}
          </ul>
          
          <Dropdown show={showDropdown} onToggle={(isOpen) => setShowDropdown(isOpen)}>
            <Dropdown.Toggle variant="link" id="dropdown-basic" className="text-white">
              👤
            </Dropdown.Toggle>

            <Dropdown.Menu>
              {isTokenValid() ? (
                <>
                  <Dropdown.Item as={Link} to="/update-info">Update Information</Dropdown.Item>
                  <Dropdown.Item onClick={handleLogout}>Logout</Dropdown.Item>
                </>
              ) : (
                <Dropdown.Item onClick={handleLogin}>Login</Dropdown.Item>
              )}
            </Dropdown.Menu>
          </Dropdown>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LoginModal from './LoginModal';
import SearchBar from './SearchBar';
import './Header.css';

export default function Header() {
  const { isAuthenticated, user, logout } = useAuth();
  const [showLogin, setShowLogin] = useState(false);
  const navigate = useNavigate();

  const handleSearch = (query) => {
    if (query.trim()) {
      navigate(`/search?query=${encodeURIComponent(query.trim())}`);
    }
  };

  return (
    <>
      <header className="header">
        <div className="header-content">
          <Link to="/" className="header-logo">
            <svg width="32" height="32" viewBox="0 0 48 48" fill="none">
              <rect width="48" height="48" rx="8" fill="#6366f1" />
              <path d="M14 16l10 6-10 6V16z" fill="#fff" />
              <path d="M26 16l10 6-10 6V16z" fill="#fff" opacity="0.6" />
            </svg>
            <span>TheMovie</span>
          </Link>

          <SearchBar onSearch={handleSearch} />

          <nav className="header-nav">
            <Link to="/" className="nav-link">Início</Link>
            {isAuthenticated && (
              <Link to="/favorites" className="nav-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                </svg>
                Favoritos
              </Link>
            )}
          </nav>

          <div className="header-auth">
            {isAuthenticated ? (
              <div className="user-menu">
                <span className="user-email">{user?.email}</span>
                <button onClick={logout} className="btn-logout">Sair</button>
              </div>
            ) : (
              <button onClick={() => setShowLogin(true)} className="btn-login">
                Entrar
              </button>
            )}
          </div>
        </div>
      </header>

      {showLogin && <LoginModal onClose={() => setShowLogin(false)} />}
    </>
  );
}

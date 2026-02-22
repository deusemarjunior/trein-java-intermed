import './Footer.css';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-content">
        <p>
          <strong>TheMovie Web</strong> — Projeto de treinamento Java Intermediário
        </p>
        <p className="footer-sub">
          Dados fornecidos por{' '}
          <a href="https://www.themoviedb.org/" target="_blank" rel="noopener noreferrer">
            TheMovieDB API
          </a>
        </p>
      </div>
    </footer>
  );
}

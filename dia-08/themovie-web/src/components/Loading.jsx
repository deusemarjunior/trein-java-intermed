import './Loading.css';

export default function Loading({ message = 'Carregando...' }) {
  return (
    <div className="loading">
      <div className="loading-spinner" />
      <p>{message}</p>
    </div>
  );
}

import { useState } from "react";
import type { FormEvent } from "react";
import api from "./api/api";
import "./App.css";
import Dashboard from "./components/Dashboard";

interface LoginResponse {
  token: string;
  tipo: string;
  usuarioId: number;
  nome: string;
  email: string;
  perfil: "ADMIN" | "GESTOR";
}

function App() {
  const [email, setEmail] = useState("admin@escalafacil.com");
  const [senha, setSenha] = useState("Admin@123");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);
  const [usuario, setUsuario] = useState<LoginResponse | null>(null);

  async function realizarLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");
    setCarregando(true);

    try {
      const resposta = await api.post<LoginResponse>(
        "/auth/login",
        { email, senha }
      );

      localStorage.setItem("token", resposta.data.token);
      localStorage.setItem("usuario", JSON.stringify(resposta.data));
      setUsuario(resposta.data);
    } catch {
      setErro("E-mail ou senha inválidos.");
    } finally {
      setCarregando(false);
    }
  }

  function sair() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    setUsuario(null);
  }

  if (usuario) {
  return <Dashboard usuario={usuario} aoSair={sair} />;
}

  return (
    <main className="login-page">
      <section className="brand-panel">
        <div className="brand-content">
          <div className="brand-icon">🔥</div>
          <h1>EscalaFácil</h1>
          <h2>Bombeiros</h2>
          <p>
            Gestão inteligente, segura e organizada das escalas operacionais.
          </p>
        </div>
      </section>

      <section className="form-panel">
        <form className="login-card" onSubmit={realizarLogin}>
          <div className="mobile-logo">🔥</div>

          <header>
            <span>Acesso ao sistema</span>
            <h2>Bem-vindo</h2>
            <p>Informe suas credenciais para continuar.</p>
          </header>

          <label htmlFor="email">E-mail</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            placeholder="seuemail@exemplo.com"
            autoComplete="email"
            required
          />

          <label htmlFor="senha">Senha</label>
          <input
            id="senha"
            type="password"
            value={senha}
            onChange={(event) => setSenha(event.target.value)}
            placeholder="Digite sua senha"
            autoComplete="current-password"
            required
          />

          {erro && <div className="error-message">{erro}</div>}

          <button className="login-button" type="submit" disabled={carregando}>
            {carregando ? "Entrando..." : "Entrar"}
          </button>

          <small>Uso exclusivo de usuários autorizados.</small>
        </form>
      </section>
    </main>
  );
}

export default App;
import { useEffect, useState } from "react";
import api from "../api/api";
import Bombeiros, { type Bombeiro } from "./Bombeiros";
import BombeiroFormulario from "./BombeiroFormulario";
import "./Dashboard.css";

interface UsuarioLogado {
  nome: string;
  email: string;
  perfil: "ADMIN" | "GESTOR";
}

interface DashboardProps {
  usuario: UsuarioLogado;
  aoSair: () => void;
}

type TelaAtiva =
  | "dashboard"
  | "bombeiros"
  | "formulario-bombeiro"
  | "indisponibilidades"
  | "escalas";

function Dashboard({ usuario, aoSair }: DashboardProps) {
  const [telaAtiva, setTelaAtiva] =
    useState<TelaAtiva>("dashboard");

  const [bombeiroSelecionado, setBombeiroSelecionado] =
    useState<Bombeiro | null>(null);

  const [totalBombeiros, setTotalBombeiros] = useState(0);

  const [
    totalIndisponibilidades,
    setTotalIndisponibilidades,
  ] = useState(0);

  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  async function carregarResumo() {
    try {
      setCarregando(true);
      setErro("");

      const [
        bombeirosResponse,
        indisponibilidadesResponse,
      ] = await Promise.all([
        api.get("/firefighters"),
        api.get("/unavailabilities"),
      ]);

      const bombeiros = bombeirosResponse.data;
      const indisponibilidades =
        indisponibilidadesResponse.data;

      setTotalBombeiros(
        Array.isArray(bombeiros) ? bombeiros.length : 0
      );

      setTotalIndisponibilidades(
        Array.isArray(indisponibilidades)
          ? indisponibilidades.length
          : 0
      );
    } catch {
      setErro(
        "Não foi possível carregar os dados do painel."
      );
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarResumo();
  }, []);

  function abrirDashboard() {
    setBombeiroSelecionado(null);
    setTelaAtiva("dashboard");
  }

  function abrirListaBombeiros() {
    setBombeiroSelecionado(null);
    setTelaAtiva("bombeiros");
  }

  function abrirCadastroBombeiro() {
    setBombeiroSelecionado(null);
    setTelaAtiva("formulario-bombeiro");
  }

  function abrirEdicaoBombeiro(bombeiro: Bombeiro) {
    setBombeiroSelecionado(bombeiro);
    setTelaAtiva("formulario-bombeiro");
  }

  async function concluirFormularioBombeiro() {
    setBombeiroSelecionado(null);
    setTelaAtiva("bombeiros");

    await carregarResumo();
  }

  function cancelarFormularioBombeiro() {
    setBombeiroSelecionado(null);
    setTelaAtiva("bombeiros");
  }

  const menuBombeirosAtivo =
    telaAtiva === "bombeiros" ||
    telaAtiva === "formulario-bombeiro";

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <div className="sidebar-logo">🔥</div>

          <div>
            <strong>EscalaFácil</strong>
            <span>Bombeiros</span>
          </div>
        </div>

        <nav className="sidebar-menu">
          <button
            className={`menu-item ${
              telaAtiva === "dashboard" ? "active" : ""
            }`}
            type="button"
            onClick={abrirDashboard}
          >
            <span>▦</span>
            Visão geral
          </button>

          <button
            className={`menu-item ${
              menuBombeirosAtivo ? "active" : ""
            }`}
            type="button"
            onClick={abrirListaBombeiros}
          >
            <span>♟</span>
            Bombeiros
          </button>

          <button
            className={`menu-item ${
              telaAtiva === "indisponibilidades"
                ? "active"
                : ""
            }`}
            type="button"
            onClick={() =>
              setTelaAtiva("indisponibilidades")
            }
          >
            <span>!</span>
            Indisponibilidades
          </button>

          <button
            className={`menu-item ${
              telaAtiva === "escalas" ? "active" : ""
            }`}
            type="button"
            onClick={() => setTelaAtiva("escalas")}
          >
            <span>▣</span>
            Escalas
          </button>
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <div className="user-avatar">
              {usuario.nome.charAt(0).toUpperCase()}
            </div>

            <div>
              <strong>{usuario.nome}</strong>
              <span>{usuario.perfil}</span>
            </div>
          </div>

          <button
            className="logout-button"
            type="button"
            onClick={aoSair}
          >
            Sair
          </button>
        </div>
      </aside>

      <main className="dashboard-content">
        {telaAtiva === "formulario-bombeiro" ? (
          <BombeiroFormulario
            bombeiro={bombeiroSelecionado}
            aoCancelar={cancelarFormularioBombeiro}
            aoSalvar={concluirFormularioBombeiro}
          />
        ) : telaAtiva === "bombeiros" ? (
          <Bombeiros
            aoCadastrar={abrirCadastroBombeiro}
            aoEditar={abrirEdicaoBombeiro}
          />
        ) : telaAtiva === "indisponibilidades" ? (
          <section>
            <span className="page-label">
              GESTÃO DE DISPONIBILIDADE
            </span>

            <h1>Indisponibilidades</h1>

            <p>
              Esta tela será criada na próxima etapa.
            </p>
          </section>
        ) : telaAtiva === "escalas" ? (
          <section>
            <span className="page-label">
              GESTÃO DE ESCALAS
            </span>

            <h1>Escalas</h1>

            <p>
              Esta tela será criada posteriormente.
            </p>
          </section>
        ) : (
          <>
            <header className="dashboard-header">
              <div>
                <span className="page-label">
                  PAINEL PRINCIPAL
                </span>

                <h1>Visão geral</h1>

                <p>
                  Acompanhe as informações principais do
                  sistema.
                </p>
              </div>

              <div className="header-profile">
                <div className="user-avatar">
                  {usuario.nome.charAt(0).toUpperCase()}
                </div>

                <div>
                  <strong>{usuario.nome}</strong>
                  <span>{usuario.email}</span>
                </div>
              </div>
            </header>

            {erro && (
              <div className="dashboard-error">
                {erro}
              </div>
            )}

            <section className="summary-grid">
              <article className="summary-card">
                <div className="card-icon red">♟</div>

                <div>
                  <span>Total de bombeiros</span>

                  <strong>
                    {carregando ? "..." : totalBombeiros}
                  </strong>

                  <small>Profissionais cadastrados</small>
                </div>
              </article>

              <article className="summary-card">
                <div className="card-icon orange">!</div>

                <div>
                  <span>Indisponibilidades</span>

                  <strong>
                    {carregando
                      ? "..."
                      : totalIndisponibilidades}
                  </strong>

                  <small>Registros encontrados</small>
                </div>
              </article>

              <article className="summary-card">
                <div className="card-icon green">✓</div>

                <div>
                  <span>Status do sistema</span>
                  <strong>Ativo</strong>
                  <small>Serviços funcionando</small>
                </div>
              </article>
            </section>

            <section className="quick-actions">
              <div className="section-title">
                <div>
                  <h2>Acesso rápido</h2>
                  <p>Escolha uma opção para começar.</p>
                </div>
              </div>

              <div className="actions-grid">
                <button
                  type="button"
                  onClick={abrirCadastroBombeiro}
                >
                  <span>＋</span>

                  <div>
                    <strong>Cadastrar bombeiro</strong>
                    <small>
                      Adicionar um novo profissional
                    </small>
                  </div>
                </button>

                <button
                  type="button"
                  onClick={() =>
                    setTelaAtiva("indisponibilidades")
                  }
                >
                  <span>!</span>

                  <div>
                    <strong>
                      Registrar indisponibilidade
                    </strong>

                    <small>
                      Informar férias ou afastamento
                    </small>
                  </div>
                </button>

                <button
                  type="button"
                  onClick={() =>
                    setTelaAtiva("escalas")
                  }
                >
                  <span>▣</span>

                  <div>
                    <strong>Elaborar escala</strong>

                    <small>
                      Gerar uma nova escala operacional
                    </small>
                  </div>
                </button>
              </div>
            </section>
          </>
        )}
      </main>
    </div>
  );
}

export default Dashboard;
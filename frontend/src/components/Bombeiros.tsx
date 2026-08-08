import { useEffect, useMemo, useState } from "react";
import api from "../api/api";
import "./Bombeiros.css";

export interface Bombeiro {
  id: number;
  nomeCompleto: string;
  matricula: string;
  email: string;
  telefone: string | null;
  cargo: string;
  equipe: string | null;
  dataAdmissao: string | null;
  status: "ATIVO" | "INATIVO";
  criadoEm: string;
}

interface BombeirosProps {
  aoCadastrar: () => void;
  aoEditar: (bombeiro: Bombeiro) => void;
}

function Bombeiros({ aoCadastrar, aoEditar }: BombeirosProps) {
  const [bombeiros, setBombeiros] = useState<Bombeiro[]>([]);
  const [pesquisa, setPesquisa] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [alterandoStatusId, setAlterandoStatusId] =
    useState<number | null>(null);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");

  async function carregarBombeiros() {
    try {
      setCarregando(true);
      setErro("");

      const resposta = await api.get<Bombeiro[]>("/firefighters");
      setBombeiros(resposta.data);
    } catch {
      setErro("Não foi possível carregar os bombeiros.");
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarBombeiros();
  }, []);

  const bombeirosFiltrados = useMemo(() => {
    const termo = pesquisa.trim().toLowerCase();

    if (!termo) {
      return bombeiros;
    }

    return bombeiros.filter((bombeiro) => {
      return (
        bombeiro.nomeCompleto.toLowerCase().includes(termo) ||
        bombeiro.matricula.toLowerCase().includes(termo) ||
        bombeiro.email.toLowerCase().includes(termo) ||
        bombeiro.cargo.toLowerCase().includes(termo) ||
        bombeiro.equipe?.toLowerCase().includes(termo)
      );
    });
  }, [bombeiros, pesquisa]);

  async function alterarStatus(bombeiro: Bombeiro) {
    const novoStatus =
      bombeiro.status === "ATIVO" ? "INATIVO" : "ATIVO";

    const confirmou = window.confirm(
      `Deseja alterar o status de ${bombeiro.nomeCompleto} para ${novoStatus}?`
    );

    if (!confirmou) {
      return;
    }

    try {
      setAlterandoStatusId(bombeiro.id);
      setErro("");
      setMensagem("");

      const resposta = await api.patch<Bombeiro>(
        `/firefighters/${bombeiro.id}/status`,
        null,
        {
          params: {
            status: novoStatus,
          },
        }
      );

      setBombeiros((listaAtual) =>
        listaAtual.map((item) =>
          item.id === bombeiro.id ? resposta.data : item
        )
      );

      setMensagem(`Status alterado para ${novoStatus}.`);
    } catch {
      setErro("Não foi possível alterar o status do bombeiro.");
    } finally {
      setAlterandoStatusId(null);
    }
  }

  function formatarData(data: string | null) {
    if (!data) {
      return "Não informada";
    }

    return new Intl.DateTimeFormat("pt-BR", {
      timeZone: "UTC",
    }).format(new Date(`${data}T00:00:00Z`));
  }

  return (
    <section className="firefighters-page">
      <header className="firefighters-header">
        <div>
          <span className="page-label">GESTÃO DE EQUIPE</span>
          <h1>Bombeiros</h1>
          <p>Consulte e gerencie os profissionais cadastrados.</p>
        </div>

        <button
          className="primary-button"
          type="button"
          onClick={aoCadastrar}
        >
          <span>＋</span>
          Novo bombeiro
        </button>
      </header>

      {erro && <div className="feedback-message error">{erro}</div>}

      {mensagem && (
        <div className="feedback-message success">{mensagem}</div>
      )}

      <div className="firefighters-toolbar">
        <div className="search-field">
          <span>⌕</span>

          <input
            type="search"
            value={pesquisa}
            onChange={(event) => setPesquisa(event.target.value)}
            placeholder="Pesquisar por nome, matrícula, e-mail ou equipe..."
          />
        </div>

        <span className="results-count">
          {bombeirosFiltrados.length} resultado(s)
        </span>
      </div>

      <div className="firefighters-table-container">
        {carregando ? (
          <div className="table-state">
            <div className="loading-spinner" />
            <p>Carregando bombeiros...</p>
          </div>
        ) : bombeirosFiltrados.length === 0 ? (
          <div className="table-state">
            <div className="empty-icon">♟</div>
            <h2>Nenhum bombeiro encontrado</h2>
            <p>
              Altere a pesquisa ou cadastre um novo profissional.
            </p>
          </div>
        ) : (
          <table className="firefighters-table">
            <thead>
              <tr>
                <th>Profissional</th>
                <th>Matrícula</th>
                <th>Cargo</th>
                <th>Equipe</th>
                <th>Admissão</th>
                <th>Status</th>
                <th className="actions-column">Ações</th>
              </tr>
            </thead>

            <tbody>
              {bombeirosFiltrados.map((bombeiro) => (
                <tr key={bombeiro.id}>
                  <td>
                    <div className="firefighter-person">
                      <div className="firefighter-avatar">
                        {bombeiro.nomeCompleto.charAt(0).toUpperCase()}
                      </div>

                      <div>
                        <strong>{bombeiro.nomeCompleto}</strong>
                        <span>{bombeiro.email}</span>
                      </div>
                    </div>
                  </td>

                  <td>{bombeiro.matricula}</td>
                  <td>{bombeiro.cargo}</td>
                  <td>{bombeiro.equipe || "Sem equipe"}</td>
                  <td>{formatarData(bombeiro.dataAdmissao)}</td>

                  <td>
                    <span
                      className={`status-badge ${bombeiro.status.toLowerCase()}`}
                    >
                      <span className="status-dot" />
                      {bombeiro.status}
                    </span>
                  </td>

                  <td>
                    <div className="table-actions">
                      <button
                        className="edit-button"
                        type="button"
                        title="Editar bombeiro"
                        onClick={() => aoEditar(bombeiro)}
                      >
                        Editar
                      </button>

                      <button
                        className={
                          bombeiro.status === "ATIVO"
                            ? "status-button deactivate"
                            : "status-button activate"
                        }
                        type="button"
                        disabled={alterandoStatusId === bombeiro.id}
                        onClick={() => alterarStatus(bombeiro)}
                      >
                        {alterandoStatusId === bombeiro.id
                          ? "Aguarde..."
                          : bombeiro.status === "ATIVO"
                            ? "Inativar"
                            : "Ativar"}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </section>
  );
}

export default Bombeiros;
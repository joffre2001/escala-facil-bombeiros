import { useCallback, useEffect, useState } from "react";
import api from "../api/api";
import "./Escalas.css";

export interface ItemEscala {
  id: number;
  bombeiroId: number;
  bombeiroNome: string;
  inicioPlantao: string;
  fimPlantao: string;
  conflito: boolean;
  observacao: string | null;
}

export interface Escala {
  id: number;
  nome: string;
  dataInicio: string;
  dataFim: string;
  status: string;
  criadaEm: string;
  totalDesignacoes: number;
  totalAlertas: number;
  itens: ItemEscala[];
}

interface Props {
  aoGerar: () => void;
  aoAlterar?: () => void | Promise<void>;
}

function formatarData(valor: string) {
  return new Intl.DateTimeFormat("pt-BR").format(
    new Date(`${valor}T00:00:00`)
  );
}

function formatarDataHora(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(valor));
}

function obterMensagemErro(erro: unknown, mensagemPadrao: string) {
  const resposta = erro as {
    response?: {
      data?: {
        message?: string;
      };
    };
  };

  return resposta.response?.data?.message ?? mensagemPadrao;
}

export default function Escalas({
  aoGerar,
  aoAlterar,
}: Props) {
  const [escalas, setEscalas] = useState<Escala[]>([]);
  const [escalaDetalhada, setEscalaDetalhada] =
    useState<Escala | null>(null);

  const [carregando, setCarregando] = useState(true);
  const [processando, setProcessando] =
    useState<number | null>(null);

  const [erro, setErro] = useState("");

  const carregarEscalas = useCallback(async () => {
    setCarregando(true);
    setErro("");

    try {
      const resposta = await api.get<Escala[]>("/schedules");
      setEscalas(resposta.data);
    } catch (error) {
      setErro(
        obterMensagemErro(
          error,
          "Não foi possível carregar as escalas."
        )
      );
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void carregarEscalas();
  }, [carregarEscalas]);

  async function abrirDetalhes(id: number) {
    setErro("");

    try {
      const resposta = await api.get<Escala>(
        `/schedules/${id}`
      );

      setEscalaDetalhada(resposta.data);
    } catch (error) {
      setErro(
        obterMensagemErro(
          error,
          "Não foi possível abrir a escala."
        )
      );
    }
  }

  async function publicar(escala: Escala) {
    if (escala.totalAlertas > 0) {
      setErro(
        "Resolva os alertas antes de publicar esta escala."
      );
      return;
    }

    const confirmou = window.confirm(
      `Deseja publicar a escala "${escala.nome}"?`
    );

    if (!confirmou) {
      return;
    }

    setProcessando(escala.id);
    setErro("");

    try {
      await api.patch(
        `/schedules/${escala.id}/publish`
      );

      setEscalaDetalhada(null);
      await carregarEscalas();
      await aoAlterar?.();
    } catch (error) {
      setErro(
        obterMensagemErro(
          error,
          "Não foi possível publicar a escala."
        )
      );
    } finally {
      setProcessando(null);
    }
  }

  async function excluir(escala: Escala) {
    const confirmou = window.confirm(
      `Deseja excluir a escala "${escala.nome}"?`
    );

    if (!confirmou) {
      return;
    }

    setProcessando(escala.id);
    setErro("");

    try {
      await api.delete(`/schedules/${escala.id}`);

      setEscalaDetalhada(null);
      await carregarEscalas();
      await aoAlterar?.();
    } catch (error) {
      setErro(
        obterMensagemErro(
          error,
          "Não foi possível excluir a escala."
        )
      );
    } finally {
      setProcessando(null);
    }
  }

  return (
    <section className="escalas-page">
      <header className="escalas-header">
        <div>
          <span className="page-label">
            GESTÃO DE PLANTÕES
          </span>

          <h1>Escalas</h1>

          <p>
            Gere, confira e publique as escalas
            operacionais.
          </p>
        </div>

        <button
          className="primary-action"
          type="button"
          onClick={aoGerar}
        >
          + Gerar escala
        </button>
      </header>

      {erro && (
        <div className="escala-feedback erro">
          {erro}
        </div>
      )}

      {carregando ? (
        <div className="escala-empty">
          Carregando escalas...
        </div>
      ) : escalas.length === 0 ? (
        <div className="escala-empty">
          <strong>Nenhuma escala criada.</strong>
          <span>
            Gere a primeira escala para começar.
          </span>
        </div>
      ) : (
        <div className="escala-grid">
          {escalas.map((escala) => (
            <article
              className="escala-card"
              key={escala.id}
            >
              <div className="escala-card-top">
                <h2>{escala.nome}</h2>

                <span
                  className={`status-badge ${escala.status.toLowerCase()}`}
                >
                  {escala.status}
                </span>
              </div>

              <p>
                {formatarData(escala.dataInicio)} até{" "}
                {formatarData(escala.dataFim)}
              </p>

              <div className="escala-stats">
                <span>
                  <strong>
                    {escala.totalDesignacoes}
                  </strong>
                  designações
                </span>

                <span
                  className={
                    escala.totalAlertas > 0
                      ? "com-alerta"
                      : ""
                  }
                >
                  <strong>{escala.totalAlertas}</strong>
                  alertas
                </span>
              </div>

              <div className="escala-actions">
                <button
                  type="button"
                  onClick={() =>
                    void abrirDetalhes(escala.id)
                  }
                >
                  Detalhes
                </button>

                {escala.status !== "PUBLICADA" && (
                  <button
                    type="button"
                    disabled={
                      processando === escala.id ||
                      escala.totalAlertas > 0
                    }
                    onClick={() =>
                      void publicar(escala)
                    }
                  >
                    Publicar
                  </button>
                )}

                {escala.status !== "PUBLICADA" && (
                  <button
                    className="danger"
                    type="button"
                    disabled={
                      processando === escala.id
                    }
                    onClick={() =>
                      void excluir(escala)
                    }
                  >
                    Excluir
                  </button>
                )}
              </div>
            </article>
          ))}
        </div>
      )}

      {escalaDetalhada && (
        <div
          className="escala-modal-backdrop"
          onMouseDown={() =>
            setEscalaDetalhada(null)
          }
        >
          <section
            className="escala-modal"
            onMouseDown={(event) =>
              event.stopPropagation()
            }
          >
            <header>
              <div>
                <span className="page-label">
                  DETALHES DA ESCALA
                </span>

                <h2>{escalaDetalhada.nome}</h2>
              </div>

              <button
                className="close-button"
                type="button"
                onClick={() =>
                  setEscalaDetalhada(null)
                }
              >
                ×
              </button>
            </header>

            <div className="modal-summary">
              <span>
                {formatarData(
                  escalaDetalhada.dataInicio
                )}
                {" – "}
                {formatarData(
                  escalaDetalhada.dataFim
                )}
              </span>

              <span>
                {escalaDetalhada.totalDesignacoes}{" "}
                designações
              </span>

              <span>
                {escalaDetalhada.totalAlertas} alertas
              </span>
            </div>

            <div className="plantao-list">
              {escalaDetalhada.itens.length === 0 ? (
                <p>Nenhum plantão gerado.</p>
              ) : (
                escalaDetalhada.itens.map((item) => (
                  <article
                    className={
                      item.conflito
                        ? "plantao conflito"
                        : "plantao"
                    }
                    key={item.id}
                  >
                    <div>
                      <strong>
                        {item.bombeiroNome}
                      </strong>

                      <span>
                        {formatarDataHora(
                          item.inicioPlantao
                        )}
                        {" até "}
                        {formatarDataHora(
                          item.fimPlantao
                        )}
                      </span>
                    </div>

                    <div>
                      {item.conflito && (
                        <span className="alert-badge">
                          Conflito
                        </span>
                      )}

                      {item.observacao && (
                        <small>
                          {item.observacao}
                        </small>
                      )}
                    </div>
                  </article>
                ))
              )}
            </div>
          </section>
        </div>
      )}
    </section>
  );
}
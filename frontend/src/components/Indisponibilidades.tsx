import { useEffect, useMemo, useState } from "react";
import api from "../api/api";
import "./Indisponibilidades.css";

export type TipoIndisponibilidade =
  | "FERIAS"
  | "FOLGA"
  | "LICENCA"
  | "AFASTAMENTO"
  | "COMPROMISSO"
  | "OUTRO";

export interface Indisponibilidade {
  id: number;
  bombeiroId: number;
  nomeBombeiro: string;
  tipo: TipoIndisponibilidade;
  dataInicio: string;
  dataFim: string;
  negociavel: boolean;
  motivo: string | null;
  criadoEm: string;
}

interface Props {
  aoCadastrar: () => void;
  aoEditar: (item: Indisponibilidade) => void;
  aoAlterar?: () => void | Promise<void>;
}

const nomesTipo: Record<TipoIndisponibilidade, string> = {
  FERIAS: "Férias",
  FOLGA: "Folga",
  LICENCA: "Licença",
  AFASTAMENTO: "Afastamento",
  COMPROMISSO: "Compromisso",
  OUTRO: "Outro",
};

function formatarData(data: string) {
  return new Intl.DateTimeFormat("pt-BR", { timeZone: "UTC" }).format(
    new Date(`${data}T00:00:00Z`)
  );
}

function obterMensagemErro(error: unknown) {
  const resposta = error as {
    response?: { data?: { message?: string; mensagem?: string } };
  };
  return (
    resposta.response?.data?.message ??
    resposta.response?.data?.mensagem ??
    "Não foi possível concluir a operação."
  );
}

export default function Indisponibilidades({
  aoCadastrar,
  aoEditar,
  aoAlterar,
}: Props) {
  const [itens, setItens] = useState<Indisponibilidade[]>([]);
  const [busca, setBusca] = useState("");
  const [tipo, setTipo] = useState<"TODOS" | TipoIndisponibilidade>("TODOS");
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  async function carregar() {
    try {
      setCarregando(true);
      setErro("");
      const response = await api.get<Indisponibilidade[]>("/unavailabilities");
      setItens(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      setErro(obterMensagemErro(error));
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  const filtrados = useMemo(() => {
    const termo = busca.trim().toLocaleLowerCase("pt-BR");
    return itens.filter((item) => {
      const correspondeTipo = tipo === "TODOS" || item.tipo === tipo;
      const correspondeBusca =
        !termo ||
        item.nomeBombeiro.toLocaleLowerCase("pt-BR").includes(termo) ||
        (item.motivo ?? "").toLocaleLowerCase("pt-BR").includes(termo);
      return correspondeTipo && correspondeBusca;
    });
  }, [busca, itens, tipo]);

  async function excluir(item: Indisponibilidade) {
    if (!window.confirm(`Excluir a indisponibilidade de ${item.nomeBombeiro}?`)) {
      return;
    }

    try {
      setErro("");
      await api.delete(`/unavailabilities/${item.id}`);
      await carregar();
      await aoAlterar?.();
    } catch (error) {
      setErro(obterMensagemErro(error));
    }
  }

  return (
    <section className="unavailability-page">
      <header className="unavailability-header">
        <div>
          <span className="page-label">GESTÃO DE DISPONIBILIDADE</span>
          <h1>Indisponibilidades</h1>
          <p>Registre férias, folgas, licenças e outros impedimentos.</p>
        </div>
        <button className="unavailability-primary" type="button" onClick={aoCadastrar}>
          + Nova indisponibilidade
        </button>
      </header>

      {erro && <div className="unavailability-error">{erro}</div>}

      <div className="unavailability-filters">
        <input
          type="search"
          placeholder="Buscar por bombeiro ou motivo..."
          value={busca}
          onChange={(event) => setBusca(event.target.value)}
        />
        <select
          value={tipo}
          onChange={(event) =>
            setTipo(event.target.value as "TODOS" | TipoIndisponibilidade)
          }
        >
          <option value="TODOS">Todos os tipos</option>
          {Object.entries(nomesTipo).map(([valor, nome]) => (
            <option key={valor} value={valor}>{nome}</option>
          ))}
        </select>
      </div>

      <div className="unavailability-table-wrap">
        <table className="unavailability-table">
          <thead>
            <tr>
              <th>Bombeiro</th>
              <th>Tipo</th>
              <th>Período</th>
              <th>Negociável</th>
              <th>Motivo</th>
              <th aria-label="Ações" />
            </tr>
          </thead>
          <tbody>
            {carregando ? (
              <tr><td colSpan={6} className="unavailability-empty">Carregando...</td></tr>
            ) : filtrados.length === 0 ? (
              <tr><td colSpan={6} className="unavailability-empty">Nenhum registro encontrado.</td></tr>
            ) : (
              filtrados.map((item) => (
                <tr key={item.id}>
                  <td><strong>{item.nomeBombeiro}</strong></td>
                  <td><span className={`type-badge type-${item.tipo.toLowerCase()}`}>{nomesTipo[item.tipo]}</span></td>
                  <td>{formatarData(item.dataInicio)} a {formatarData(item.dataFim)}</td>
                  <td><span className={item.negociavel ? "deal-yes" : "deal-no"}>{item.negociavel ? "Sim" : "Não"}</span></td>
                  <td className="reason-cell">{item.motivo || "—"}</td>
                  <td>
                    <div className="row-actions">
                      <button type="button" onClick={() => aoEditar(item)}>Editar</button>
                      <button className="danger" type="button" onClick={() => excluir(item)}>Excluir</button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
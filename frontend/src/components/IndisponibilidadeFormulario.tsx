import { useEffect, useState, type FormEvent } from "react";
import api from "../api/api";
import {
  type Indisponibilidade,
  type TipoIndisponibilidade,
} from "./Indisponibilidades";
import "./IndisponibilidadeFormulario.css";

interface BombeiroResumo {
  id: number;
  nomeCompleto: string;
  matricula?: string;
  status?: string;
}

interface Props {
  indisponibilidade: Indisponibilidade | null;
  aoCancelar: () => void;
  aoSalvar: () => void | Promise<void>;
}

const tipos: Array<{ valor: TipoIndisponibilidade; nome: string }> = [
  { valor: "FERIAS", nome: "Férias" },
  { valor: "FOLGA", nome: "Folga" },
  { valor: "LICENCA", nome: "Licença" },
  { valor: "AFASTAMENTO", nome: "Afastamento" },
  { valor: "COMPROMISSO", nome: "Compromisso" },
  { valor: "OUTRO", nome: "Outro" },
];

function obterMensagemErro(error: unknown) {
  const resposta = error as {
    response?: { data?: { message?: string; mensagem?: string } };
  };
  return (
    resposta.response?.data?.message ??
    resposta.response?.data?.mensagem ??
    "Não foi possível salvar a indisponibilidade."
  );
}

export default function IndisponibilidadeFormulario({
  indisponibilidade,
  aoCancelar,
  aoSalvar,
}: Props) {
  const [bombeiros, setBombeiros] = useState<BombeiroResumo[]>([]);
  const [bombeiroId, setBombeiroId] = useState(
    indisponibilidade?.bombeiroId.toString() ?? ""
  );
  const [tipo, setTipo] = useState<TipoIndisponibilidade>(
    indisponibilidade?.tipo ?? "FOLGA"
  );
  const [dataInicio, setDataInicio] = useState(indisponibilidade?.dataInicio ?? "");
  const [dataFim, setDataFim] = useState(indisponibilidade?.dataFim ?? "");
  const [negociavel, setNegociavel] = useState(
    indisponibilidade?.tipo === "FERIAS" ? false : indisponibilidade?.negociavel ?? true
  );
  const [motivo, setMotivo] = useState(indisponibilidade?.motivo ?? "");
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  useEffect(() => {
    api.get<BombeiroResumo[]>("/firefighters")
      .then(({ data }) => {
        const lista = Array.isArray(data) ? data : [];
        setBombeiros(
          lista.filter(
            (bombeiro) =>
              bombeiro.status === undefined ||
              bombeiro.status === "ATIVO" ||
              bombeiro.id === indisponibilidade?.bombeiroId
          )
        );
      })
      .catch((error) => setErro(obterMensagemErro(error)));
  }, [indisponibilidade?.bombeiroId]);

  function alterarTipo(novoTipo: TipoIndisponibilidade) {
    setTipo(novoTipo);
    if (novoTipo === "FERIAS") setNegociavel(false);
  }

  async function enviar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");

    if (!bombeiroId || !dataInicio || !dataFim) {
      setErro("Preencha todos os campos obrigatórios.");
      return;
    }
    if (dataFim < dataInicio) {
      setErro("A data final não pode ser anterior à data inicial.");
      return;
    }

    const payload = {
      bombeiroId: Number(bombeiroId),
      tipo,
      dataInicio,
      dataFim,
      negociavel: tipo === "FERIAS" ? false : negociavel,
      motivo: motivo.trim() || null,
    };

    try {
      setSalvando(true);
      if (indisponibilidade) {
        await api.put(`/unavailabilities/${indisponibilidade.id}`, payload);
      } else {
        await api.post("/unavailabilities", payload);
      }
      await aoSalvar();
    } catch (error) {
      setErro(obterMensagemErro(error));
    } finally {
      setSalvando(false);
    }
  }

  return (
    <section className="unavailability-form-page">
      <header className="unavailability-form-header">
        <div>
          <span className="page-label">GESTÃO DE DISPONIBILIDADE</span>
          <h1>{indisponibilidade ? "Editar indisponibilidade" : "Nova indisponibilidade"}</h1>
          <p>Informe o período em que o bombeiro não estará disponível.</p>
        </div>
        <button className="form-back" type="button" onClick={aoCancelar} disabled={salvando}>Voltar</button>
      </header>

      {erro && <div className="unavailability-form-error">{erro}</div>}

      <form className="unavailability-form" onSubmit={enviar}>
        <div className="unavailability-form-title">
          <h2>Dados do período</h2>
          <p>Campos marcados com * são obrigatórios.</p>
        </div>

        <div className="unavailability-form-grid">
          <label className="field-full">
            <span>Bombeiro *</span>

            <select
              value={bombeiroId}
              onChange={(event) =>
                setBombeiroId(event.target.value)
              }
              required
              style={{
                color: "#111827",
                backgroundColor: "#ffffff",
                WebkitTextFillColor: "#111827",
              }}
            >
              <option
                value=""
                style={{
                  color: "#111827",
                  backgroundColor: "#ffffff",
                }}
              >
                Selecione um bombeiro
              </option>

              {bombeiros.map((bombeiro) => (
                <option
                  key={bombeiro.id}
                  value={bombeiro.id}
                  style={{
                    color: "#111827",
                    backgroundColor: "#ffffff",
                  }}
                >
                  {bombeiro.nomeCompleto}
                  {bombeiro.matricula
                    ? ` — ${bombeiro.matricula}`
                    : ""}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Tipo *</span>
            <select value={tipo} onChange={(e) => alterarTipo(e.target.value as TipoIndisponibilidade)} disabled={salvando}>
              {tipos.map((item) => <option key={item.valor} value={item.valor}>{item.nome}</option>)}
            </select>
          </label>

          <div className="negotiable-field">
            <span>Negociável</span>
            <label className="switch-line">
              <input type="checkbox" checked={negociavel} onChange={(e) => setNegociavel(e.target.checked)} disabled={salvando || tipo === "FERIAS"} />
              <span>{tipo === "FERIAS" ? "Férias são inegociáveis" : negociavel ? "Sim, pode ser negociado" : "Não negociável"}</span>
            </label>
          </div>

          <label>
            <span>Data inicial *</span>
            <input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} disabled={salvando} required />
          </label>

          <label>
            <span>Data final *</span>
            <input type="date" min={dataInicio} value={dataFim} onChange={(e) => setDataFim(e.target.value)} disabled={salvando} required />
          </label>

          <label className="field-full">
            <span>Motivo</span>
            <textarea maxLength={255} rows={4} value={motivo} onChange={(e) => setMotivo(e.target.value)} placeholder="Inclua uma observação, se necessário" disabled={salvando} />
            <small>{motivo.length}/255 caracteres</small>
          </label>
        </div>

        <div className="unavailability-form-actions">
          <button className="cancel" type="button" onClick={aoCancelar} disabled={salvando}>Cancelar</button>
          <button className="save" type="submit" disabled={salvando}>{salvando ? "Salvando..." : indisponibilidade ? "Salvar alterações" : "Cadastrar"}</button>
        </div>
      </form>
    </section>
  );
}
import { useState } from "react";
import type { FormEvent } from "react";
import api from "../api/api";
import type { Escala } from "./Escalas";
import "./GerarEscalaFormulario.css";

interface Props {
  aoCancelar: () => void;
  aoGerar: (
    escala: Escala
  ) => void | Promise<void>;
}

function obterDataAtual() {
  return new Date().toISOString().slice(0, 10);
}

export default function GerarEscalaFormulario({
  aoCancelar,
  aoGerar,
}: Props) {
  const [nome, setNome] = useState("");

  const [dataInicio, setDataInicio] = useState(
    obterDataAtual()
  );

  const [dataFim, setDataFim] = useState(
    obterDataAtual()
  );

  const [
    quantidadePorPlantao,
    setQuantidadePorPlantao,
  ] = useState(1);

  const [erro, setErro] = useState("");
  const [gerando, setGerando] = useState(false);

  async function enviar(
    event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();
    setErro("");

    if (dataFim < dataInicio) {
      setErro(
        "A data final não pode ser anterior à data inicial."
      );
      return;
    }

    setGerando(true);

    try {
      const resposta = await api.post<Escala>(
        "/schedules/generate",
        {
          nome: nome.trim(),
          dataInicio,
          dataFim,
          quantidadePorPlantao,
        }
      );

      await aoGerar(resposta.data);
    } catch (error) {
      const erroApi = error as {
        response?: {
          data?: {
            message?: string;
            validationErrors?: Record<
              string,
              string
            >;
          };
        };
      };

      const validacoes =
        erroApi.response?.data?.validationErrors;

      if (validacoes) {
        setErro(
          Object.values(validacoes).join(" ")
        );
      } else {
        setErro(
          erroApi.response?.data?.message ??
            "Não foi possível gerar a escala."
        );
      }
    } finally {
      setGerando(false);
    }
  }

  return (
    <section className="gerar-page">
      <header>
        <span className="page-label">
          GERAÇÃO AUTOMÁTICA
        </span>

        <h1>Gerar nova escala</h1>

        <p>
          Defina o período e a equipe necessária
          em cada plantão de 24 horas.
        </p>
      </header>

      <form
        className="gerar-card"
        onSubmit={enviar}
      >
        {erro && (
          <div className="form-feedback">
            {erro}
          </div>
        )}

        <label>
          Nome da escala

          <input
            value={nome}
            onChange={(event) =>
              setNome(event.target.value)
            }
            maxLength={120}
            placeholder="Ex.: Escala operacional — setembro"
            required
          />
        </label>

        <div className="form-row">
          <label>
            Data inicial

            <input
              type="date"
              min={obterDataAtual()}
              value={dataInicio}
              onChange={(event) => {
                const novaData =
                  event.target.value;

                setDataInicio(novaData);

                if (dataFim < novaData) {
                  setDataFim(novaData);
                }
              }}
              required
            />
          </label>

          <label>
            Data final

            <input
              type="date"
              min={dataInicio}
              value={dataFim}
              onChange={(event) =>
                setDataFim(event.target.value)
              }
              required
            />
          </label>
        </div>

        <label>
          Bombeiros por plantão

          <input
            type="number"
            min={1}
            max={20}
            value={quantidadePorPlantao}
            onChange={(event) =>
              setQuantidadePorPlantao(
                Number(event.target.value)
              )
            }
            required
          />

          <small>
            O sistema distribuirá os plantões
            considerando disponibilidade,
            equilíbrio e conflitos.
          </small>
        </label>

        <div className="form-actions">
          <button
            className="secondary"
            type="button"
            onClick={aoCancelar}
            disabled={gerando}
          >
            Cancelar
          </button>

          <button
            className="primary-action"
            type="submit"
            disabled={gerando}
          >
            {gerando
              ? "Gerando..."
              : "Gerar escala"}
          </button>
        </div>
      </form>
    </section>
  );
}
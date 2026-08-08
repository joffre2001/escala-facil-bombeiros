import { useEffect, useState, type FormEvent } from "react";
import api from "../api/api";
import type { Bombeiro } from "./Bombeiros";
import "./BombeiroFormulario.css";

interface BombeiroFormularioProps {
  bombeiro: Bombeiro | null;
  aoCancelar: () => void;
  aoSalvar: () => void;
}

interface DadosFormulario {
  nomeCompleto: string;
  matricula: string;
  email: string;
  telefone: string;
  cargo: string;
  equipe: string;
  dataAdmissao: string;
}

const formularioVazio: DadosFormulario = {
  nomeCompleto: "",
  matricula: "",
  email: "",
  telefone: "",
  cargo: "",
  equipe: "",
  dataAdmissao: "",
};

function BombeiroFormulario({
  bombeiro,
  aoCancelar,
  aoSalvar,
}: BombeiroFormularioProps) {
  const [formulario, setFormulario] =
    useState<DadosFormulario>(formularioVazio);

  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  const modoEdicao = bombeiro !== null;

  useEffect(() => {
    if (bombeiro) {
      setFormulario({
        nomeCompleto: bombeiro.nomeCompleto,
        matricula: bombeiro.matricula,
        email: bombeiro.email,
        telefone: bombeiro.telefone ?? "",
        cargo: bombeiro.cargo,
        equipe: bombeiro.equipe ?? "",
        dataAdmissao: bombeiro.dataAdmissao ?? "",
      });
    } else {
      setFormulario(formularioVazio);
    }
  }, [bombeiro]);

  function alterarCampo(
    campo: keyof DadosFormulario,
    valor: string
  ) {
    setFormulario((dadosAtuais) => ({
      ...dadosAtuais,
      [campo]: valor,
    }));
  }

  function validarFormulario() {
    if (!formulario.nomeCompleto.trim()) {
      return "Informe o nome completo.";
    }

    if (!formulario.matricula.trim()) {
      return "Informe a matrícula.";
    }

    if (!formulario.email.trim()) {
      return "Informe o e-mail.";
    }

    if (!formulario.email.includes("@")) {
      return "Informe um e-mail válido.";
    }

    if (!formulario.cargo.trim()) {
      return "Informe o cargo.";
    }

    return "";
  }

  async function enviarFormulario(event: FormEvent) {
    event.preventDefault();

    const erroValidacao = validarFormulario();

    if (erroValidacao) {
      setErro(erroValidacao);
      return;
    }

    const dados = {
      nomeCompleto: formulario.nomeCompleto.trim(),
      matricula: formulario.matricula.trim(),
      email: formulario.email.trim(),
      telefone: formulario.telefone.trim() || null,
      cargo: formulario.cargo.trim(),
      equipe: formulario.equipe.trim() || null,
      dataAdmissao: formulario.dataAdmissao || null,
    };

    try {
      setSalvando(true);
      setErro("");

      if (bombeiro) {
        await api.put(`/firefighters/${bombeiro.id}`, dados);
      } else {
        await api.post("/firefighters", dados);
      }

      aoSalvar();
    } catch (error: any) {
      const resposta = error.response?.data;

      if (resposta?.validationErrors) {
        const validacoes = Object.values(
          resposta.validationErrors
        ).join(" ");

        setErro(validacoes);
      } else if (resposta?.message) {
        setErro(resposta.message);
      } else {
        setErro(
          modoEdicao
            ? "Não foi possível atualizar o bombeiro."
            : "Não foi possível cadastrar o bombeiro."
        );
      }
    } finally {
      setSalvando(false);
    }
  }

  return (
    <section className="firefighter-form-page">
      <header className="firefighter-form-header">
        <div>
          <span className="page-label">GESTÃO DE EQUIPE</span>

          <h1>
            {modoEdicao
              ? "Editar bombeiro"
              : "Cadastrar bombeiro"}
          </h1>

          <p>
            {modoEdicao
              ? "Atualize as informações do profissional."
              : "Preencha os dados do novo profissional."}
          </p>
        </div>

        <button
          className="back-button"
          type="button"
          onClick={aoCancelar}
          disabled={salvando}
        >
          ← Voltar
        </button>
      </header>

      {erro && (
        <div className="form-feedback-error">{erro}</div>
      )}

      <form
        className="firefighter-form"
        onSubmit={enviarFormulario}
      >
        <div className="form-section-title">
          <h2>Informações pessoais</h2>
          <p>Campos marcados com * são obrigatórios.</p>
        </div>

        <div className="form-grid">
          <div className="form-group form-group-full">
            <label htmlFor="nomeCompleto">
              Nome completo *
            </label>

            <input
              id="nomeCompleto"
              type="text"
              maxLength={150}
              value={formulario.nomeCompleto}
              onChange={(event) =>
                alterarCampo(
                  "nomeCompleto",
                  event.target.value
                )
              }
              placeholder="Ex.: João da Silva"
              disabled={salvando}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="matricula">Matrícula *</label>

            <input
              id="matricula"
              type="text"
              maxLength={30}
              value={formulario.matricula}
              onChange={(event) =>
                alterarCampo(
                  "matricula",
                  event.target.value
                )
              }
              placeholder="Ex.: BM-001"
              disabled={salvando}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="dataAdmissao">
              Data de admissão
            </label>

            <input
              id="dataAdmissao"
              type="date"
              max={new Date().toISOString().split("T")[0]}
              value={formulario.dataAdmissao}
              onChange={(event) =>
                alterarCampo(
                  "dataAdmissao",
                  event.target.value
                )
              }
              disabled={salvando}
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">E-mail *</label>

            <input
              id="email"
              type="email"
              maxLength={150}
              value={formulario.email}
              onChange={(event) =>
                alterarCampo("email", event.target.value)
              }
              placeholder="nome@email.com"
              disabled={salvando}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="telefone">Telefone</label>

            <input
              id="telefone"
              type="tel"
              maxLength={30}
              value={formulario.telefone}
              onChange={(event) =>
                alterarCampo(
                  "telefone",
                  event.target.value
                )
              }
              placeholder="(49) 99999-9999"
              disabled={salvando}
            />
          </div>

          <div className="form-group">
            <label htmlFor="cargo">Cargo *</label>

            <input
              id="cargo"
              type="text"
              maxLength={80}
              value={formulario.cargo}
              onChange={(event) =>
                alterarCampo("cargo", event.target.value)
              }
              placeholder="Ex.: Soldado"
              disabled={salvando}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="equipe">Equipe</label>

            <input
              id="equipe"
              type="text"
              maxLength={80}
              value={formulario.equipe}
              onChange={(event) =>
                alterarCampo("equipe", event.target.value)
              }
              placeholder="Ex.: Equipe Alfa"
              disabled={salvando}
            />
          </div>
        </div>

        <div className="form-actions">
          <button
            className="cancel-button"
            type="button"
            onClick={aoCancelar}
            disabled={salvando}
          >
            Cancelar
          </button>

          <button
            className="save-button"
            type="submit"
            disabled={salvando}
          >
            {salvando
              ? "Salvando..."
              : modoEdicao
                ? "Salvar alterações"
                : "Cadastrar bombeiro"}
          </button>
        </div>
      </form>
    </section>
  );
}

export default BombeiroFormulario;
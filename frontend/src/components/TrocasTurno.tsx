import { useCallback, useEffect, useState } from "react";
import api from "../api/api";
import "./TrocasTurno.css";

export interface SolicitacaoTroca { id:number; escalaNome:string; itemEscalaId:number; inicioPlantao:string; solicitanteId:number; solicitanteNome:string; substitutoId:number; substitutoNome:string; status:string; motivo:string|null; criadoEm:string }
const nomes:Record<string,string>={AGUARDANDO_ACEITE:"Aguardando substituto",AGUARDANDO_APROVACAO:"Aguardando aprovação",APROVADA:"Aprovada",RECUSADA:"Recusada",CANCELADA:"Cancelada"};
const dataHora=(v:string)=>new Intl.DateTimeFormat("pt-BR",{dateStyle:"short",timeStyle:"short"}).format(new Date(v));

export default function TrocasTurno(){
  const [itens,setItens]=useState<SolicitacaoTroca[]>([]); const [erro,setErro]=useState(""); const [carregando,setCarregando]=useState(true);
  const carregar=useCallback(async()=>{try{setCarregando(true);setItens((await api.get<SolicitacaoTroca[]>("/shift-exchanges")).data)}catch{setErro("Não foi possível carregar as solicitações.")}finally{setCarregando(false)}},[]);
  useEffect(()=>{void carregar()},[carregar]);
  async function decidir(item:SolicitacaoTroca,aprovar:boolean){const motivo=window.prompt(aprovar?"Informe a justificativa da aprovação:":"Informe o motivo da recusa:");if(!motivo?.trim())return;try{await api.patch(`/shift-exchanges/${item.id}/decision`,{aprovar,motivo:motivo.trim()});await carregar()}catch(e:any){setErro(e.response?.data?.message??"Não foi possível registrar a decisão.")}}
  return <section className="exchanges-page"><header><div><span className="page-label">GESTÃO DE TROCAS</span><h1>Solicitações de troca</h1><p>Acompanhe o aceite do substituto e aprove ou recuse a alteração.</p></div></header>{erro&&<div className="exchange-error">{erro}</div>}{carregando?<div className="exchange-empty">Carregando...</div>:itens.length===0?<div className="exchange-empty">Nenhuma solicitação de troca.</div>:<div className="exchange-list">{itens.map(item=><article key={item.id}><div><strong>{item.solicitanteNome} → {item.substitutoNome}</strong><span>{item.escalaNome} · {dataHora(item.inicioPlantao)}</span><small>{item.motivo||"Sem justificativa"}</small></div><div className="exchange-side"><span className={`exchange-status ${item.status.toLowerCase()}`}>{nomes[item.status]??item.status}</span>{item.status==="AGUARDANDO_APROVACAO"&&<div><button onClick={()=>void decidir(item,true)}>Aprovar</button><button className="reject" onClick={()=>void decidir(item,false)}>Recusar</button></div>}</div></article>)}</div>}</section>
}

/*
Copyright 2012-2017 Jose Robson Mariano Alves

This file is part of bgfinancas.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

 */
package badernageral.bgfinancas.modelo;

import badernageral.bgfinancas.biblioteca.banco.Banco;
import badernageral.bgfinancas.biblioteca.contrato.Modelo;
import badernageral.bgfinancas.biblioteca.banco.Coluna;
import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.Grafico;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Funcao;
import badernageral.bgfinancas.biblioteca.utilitario.Datas;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

public final class Despesa extends Banco<Despesa> implements Modelo, Grafico {

    private static final String MODULO = RAIZ + "/modulo/despesa";

    public static final String FXML = MODULO + "/Despesa.fxml";
    public static final String FXML_CADASTRO_MULTIPLO = MODULO + "/DespesaCadastroMultiplo.fxml";
    public static final String FXML_DESPESAS_AGENDADAS = MODULO + "/DespesasAgendadas.fxml";
    public static final String FXML_FORMULARIO = MODULO + "/DespesaFormulario.fxml";
    public static final String FXML_MODAL_DESPESA = MODULO + "/ModalDespesa.fxml";

    public static final String TABELA = "despesas";

    private final Coluna idDespesa = new Coluna(TABELA, "id_despesa");
    private final Coluna idConta = new Coluna(TABELA, "id_conta");
    private final Coluna idItem = new Coluna(TABELA, "id_item");
    private final Coluna quantidade = new Coluna(TABELA, "quantidade");
    private final Coluna valor = new Coluna(TABELA, "valor");
    private final Coluna data = new Coluna(TABELA, "data");
    private final Coluna hora = new Coluna(TABELA, "hora");
    private final Coluna agendada = new Coluna(TABELA, "agendada");
    private final Coluna parcela = new Coluna(TABELA, "parcela");
    private final Coluna idCartaoCredito = new Coluna(TABELA, "id_cartao_credito");

    private final Coluna idContaInner = new Coluna(Conta.TABELA, "id_conta");
    private final Coluna nomeConta = new Coluna(Conta.TABELA, "nome", "", "nome_conta");

    private final Coluna idCartaoCreditoLeft = new Coluna(CartaoCredito.TABELA, "id_cartao_credito");
    private final Coluna nomeCartaoCredito = new Coluna(CartaoCredito.TABELA, "nome", "", "nome_cartao_credito");

    private final Coluna idItemInner = new Coluna(DespesaItem.TABELA, "id_item");
    private final Coluna idCategoria = new Coluna(DespesaItem.TABELA, "id_categoria");
    private final Coluna nomeItem = new Coluna(DespesaItem.TABELA, "nome", "", "nome_item");

    private final Coluna idCategoriaInner = new Coluna(DespesaCategoria.TABELA, "id_categoria");
    private final Coluna nomeCategoria = new Coluna(DespesaCategoria.TABELA, "nome", "", "nome_categoria");

    private final Coluna sumValor = new Coluna(TABELA, "valor", "sum_valor", Funcao.SOMAR);
    private final Coluna sumQuantidade = new Coluna(TABELA, "quantidade", "sum_quantidade", Funcao.SOMAR);
    private final Coluna semana = new Coluna(TABELA, "data", "semana", Funcao.SEMANA);
    private final Coluna mes = new Coluna(TABELA, "data", "mes", Funcao.MES);
    private final Coluna ano = new Coluna(TABELA, "data", "ano", Funcao.ANO);

    private Boolean somenteAgendamento = false;
    private Boolean especificarMesAno = false;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    
    private String tipo = idioma.getMensagem("despesa");
    private String status = idioma.getMensagem("efetuado");

    public Despesa() {
    }

    public Despesa(String idItem, String nomeItem, String nomeCategoria, String quantidade, String valor) {
        this.idItem.setValor(idItem);
        this.nomeItem.setValor(nomeItem);
        this.nomeCategoria.setValor(nomeCategoria);
        this.quantidade.setValor(quantidade);
        this.valor.setValor(valor);
        this.agendada.setValor("0");
    }

    public Despesa(String idDespesa, String idConta, String idItem, String quantidade, String valor, LocalDate data, String hora, String agendada, String cartao_credito) {
        this.idDespesa.setValor(idDespesa);
        this.idConta.setValor(idConta);
        this.idItem.setValor(idItem);
        this.quantidade.setValor(quantidade);
        this.valor.setValor(valor);
        this.data.setValor(Datas.toSqlData(data));
        this.hora.setValor(hora);
        this.agendada.setValor(agendada);
        this.idCartaoCredito.setValor(cartao_credito);
    }

    @Override
    protected Despesa instanciar(ResultSet rs) throws SQLException {
        Despesa d = new Despesa(
                rs.getString(idDespesa.getColuna()),
                rs.getString(idConta.getColuna()),
                rs.getString(idItem.getColuna()),
                rs.getString(quantidade.getColuna()),
                rs.getString(valor.getColuna()),
                rs.getDate(data.getColuna()).toLocalDate(),
                rs.getString(hora.getColuna()),
                rs.getString(agendada.getColuna()),
                rs.getString(idCartaoCredito.getColuna())
        );
        d.setParcela(rs.getString(parcela.getColuna()));
        d.setNomeConta(rs.getString(nomeConta.getAliasColuna()));
        d.setNomeItem(rs.getString(nomeItem.getAliasColuna()));
        d.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
        d.setNomeCartaoCredito(rs.getString(nomeCartaoCredito.getAliasColuna()));
        return d;
    }

    public Despesa getClone() {
        return new Despesa(
                idItem.getValor(),
                nomeItem.getValor(),
                nomeCategoria.getValor(),
                quantidade.getValor(),
                valor.getValor()
        );
    }

    @Override
    public boolean cadastrar() {
        return this.insert(idConta, idItem, quantidade, valor, data, hora, agendada, parcela, idCartaoCredito).commit();
    }

    @Override
    public boolean alterar() {
        return this.update(idConta, quantidade, valor, data, agendada, idCartaoCredito).where(idDespesa, "=").commit();
    }

    @Override
    public boolean excluir() {
        return this.delete(idDespesa, "=").commit();
    }

    @Override
    public Usuario consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }

    @Override
    public ObservableList<Despesa> listar() {
        try {
            this.select(idDespesa, idConta, idItem, quantidade, valor, data, hora, agendada, parcela, idCartaoCredito, nomeConta, nomeItem, nomeCategoria, nomeCartaoCredito);
            this.inner(idConta, idContaInner);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            this.left(idCartaoCredito, idCartaoCreditoLeft);
            this.where(nomeConta, "LIKE", "(");
            this.or(nomeItem, "LIKE");
            this.or(valor, "=");
            this.or(data, "=");
            this.or(nomeCategoria, "LIKE", ")");
            if (idItem.getValor() != null) {
                this.and(idItem, "=");
            }
            if (idCategoria.getValor() != null) {
                this.and(idCategoria, "=");
            }
            if (idConta.getValor() != null) {
                this.and(idConta, "=");
            }
            if (idCartaoCredito.getValor() != null) {
                if (idCartaoCredito.getValor().equals("NULL")) {
                    this.andIsNull(idCartaoCredito);
                } else if (idCartaoCredito.getValor().equals("NOTNULL")) {
                    this.andIsNotNull(idCartaoCredito);
                } else {
                    this.and(idCartaoCredito, "=");
                }
            }
            agendada.setValor("1");
            if (somenteAgendamento) {
                this.and(agendada, "=");
            } else {
                this.and(agendada, "<>");
            }
            if (especificarMesAno) {
                data.setValor(Datas.toSqlData(dataInicio));
                this.and(data, ">=");
                data.setValor(Datas.toSqlData(dataFim));
                this.and(data, "<=");
            }
            if (somenteAgendamento) {
                this.orderByAsc(data, nomeItem);
            } else {
                this.orderByDesc(data, hora);
            }
            ResultSet rs = this.query();
            if (rs != null) {
                List<Despesa> Linhas = new ArrayList<>();
                while (rs.next()) {
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Despesa> resultado = FXCollections.observableList(Linhas);
                return resultado;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Janela.showException(ex);
            return null;
        }
    }

    public boolean isDespesasAtrasadas() {
        try {
            this.select(idDespesa);
            agendada.setValor("1");
            this.where(agendada, "=");
            data.setValor(Datas.toSqlData(LocalDate.now()));
            this.and(data, "<=");
            data.setValor(Configuracao.getPropriedade("data_notificacao"));
            this.and(data, ">");
            this.orderByDesc(data, hora);
            ResultSet rs = this.query();
            if (rs != null) {
                return rs.next();
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Janela.showException(ex);
            return false;
        }
    }

    public String getIdDespesa() {
        return idDespesa.getValor();
    }

    public String getIdConta() {
        return idConta.getValor();
    }

    public String getIdItem() {
        return idItem.getValor();
    }

    public BigDecimal getQuantidade() {
        return new BigDecimal(quantidade.getValor());
    }

    public BigDecimal getValor() {
        return new BigDecimal(valor.getValor());
    }

    public LocalDate getData() {
        return Datas.getLocalDate(data.getValor());
    }

    public LocalTime getHora() {
        return Datas.getLocalTime(hora.getValor());
    }

    public LocalDateTime getDataHora() {
        return Datas.getLocalDateTime(getData(), getHora());
    }
    
    public String getAgendada() {
        return agendada.getValor();
    }

    public String getNomeConta() {
        return nomeConta.getValor();
    }

    public String getIdCartaoCredito() {
        return idCartaoCredito.getValor();
    }

    public String getNomeCartaoCredito() {
        return nomeCartaoCredito.getValor();
    }

    public String getNomeItem() {
        if (agendada.getValor() != null && agendada.getValor().equals("1") && parcela.getValor() != null) {
            return nomeItem.getValor() + " - " + parcela.getValor();
        } else {
            return nomeItem.getValor();
        }
    }

    public String getNomeCategoria() {
        return nomeCategoria.getValor();
    }

    public void setValor(String valor) {
        this.valor.setValor(valor);
    }

    public Despesa setIdItem(String id_item) {
        this.idItem.setValor(id_item);
        return getThis();
    }

    public void setQuantidade(String quantidade) {
        this.quantidade.setValor(quantidade);
    }

    public Despesa setIdConta(Categoria conta) {
        this.idConta.setValor(conta.getIdCategoria());
        return getThis();
    }

    public Despesa setIdCartaoCredito(Categoria cartao_credito) {
        this.idCartaoCredito.setValor(cartao_credito.getIdCategoria());
        return getThis();
    }

    public void setData(String data) {
        this.data.setValor(data);
    }

    public void setHora(String hora) {
        this.hora.setValor(hora);
    }

    public Despesa setAgendada(String agendada) {
        this.agendada.setValor(agendada);
        return getThis();
    }

    public void setParcela(String parcela) {
        this.parcela.setValor(parcela);
    }

    public void setNomeConta(String conta) {
        this.nomeConta.setValor(conta);
    }

    public void setNomeCartaoCredito(String cartao_credito) {
        this.nomeCartaoCredito.setValor(cartao_credito);
    }

    public void setNomeItem(String item) {
        this.nomeItem.setValor(item);
    }

    public void setNomeCategoria(String categoria) {
        this.nomeCategoria.setValor(categoria);
    }

    public Despesa setIdCategoria(String idCategoria) {
        this.idCategoria.setValor(idCategoria);
        return this;
    }

    public Despesa setSomenteAgendamento() {
        this.somenteAgendamento = true;
        return getThis();
    }

    public Despesa setMesAno(int mes, int ano) {
        this.especificarMesAno = true;
        this.dataInicio = LocalDate.of(ano, mes, 1);
        this.dataFim = dataInicio.withDayOfMonth(dataInicio.lengthOfMonth());
        return getThis();
    }

    public Despesa setFiltro(String filtro) {
        nomeConta.setValor(filtro);
        nomeItem.setValor(filtro);
        nomeCategoria.setValor(filtro);
        try {
            LocalDate filtroData = LocalDate.parse(filtro, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            data.setValor(Datas.toSqlData(filtroData));
        } catch (DateTimeParseException e) {
        }
        try {
            Validar.decimal(filtro, null);
            valor.setValor(filtro);
        } catch (Erro e) {
        }
        return this;
    }
    
    public String getSumValor(LocalDate hoje) {
        try {
            if (idCategoria.getValor() != null) {
                LocalDate inicio = hoje.withDayOfMonth(1);
                LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
                this.select(sumValor);
                this.inner(idItem, idItemInner);
                this.inner(idCategoria, idCategoriaInner);
                this.where(idCategoria, "=");
                data.setValor(Datas.toSqlData(inicio));
                this.and(data, ">=");
                data.setValor(Datas.toSqlData(fim));
                this.and(data, "<=");
                agendada.setValor("1");
                this.and(agendada, "<>");
                ResultSet rs = this.query();
                if (rs.next()) {
                    String resultado = rs.getString(sumValor.getAliasColuna());
                    if (resultado != null) {
                        return resultado;
                    }
                }
            }
            return "0.0";
        } catch (SQLException ex) {
            Janela.showException(ex);
            return "0.0";
        }
    }
    
    public ObservableList<Extrato> getExtrato(LocalDate inicio, LocalDate fim) {
        try {
            this.select(idItem, data, hora, nomeCategoria, nomeItem, valor, agendada);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.where(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            this.orderByAsc(data,hora);
            ResultSet rs = this.query();
            if (rs != null) {
                List<Extrato> objetos = new ArrayList<>();
                while (rs.next()) {
                    String status = idioma.getMensagem("efetuado");
                    if(rs.getString(agendada.getColuna()).equals("1")){
                        status = idioma.getMensagem("agendado");
                    }
                    Extrato e = new Extrato(
                            idioma.getMensagem("despesa"),
                            rs.getString(data.getColuna()),
                            rs.getString(hora.getColuna()),
                            rs.getString(nomeCategoria.getAliasColuna()),
                            rs.getString(nomeItem.getAliasColuna()),
                            rs.getString(valor.getColuna()),
                            status
                    );
                    objetos.add(e);
                }
                ObservableList<Extrato> resultado = FXCollections.observableList(objetos);
                return resultado;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Janela.showException(ex);
            return null;
        }
    }
    
    public ObservableList<Despesa> listarPeriodoAgrupado(LocalDate inicio, LocalDate fim) {
        try {
            this.select(idItem, nomeCategoria, nomeItem, sumQuantidade, sumValor);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.where(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            agendada.setValor("1");
            this.and(agendada, "<>");
            this.groupBy(idItem,nomeCategoria,nomeItem);
            this.orderByAsc(nomeCategoria);
            this.orderByDesc(sumValor);
            ResultSet rs = this.query();
            if (rs != null) {
                List<Despesa> Linhas = new ArrayList<>();
                while (rs.next()) {
                    Despesa d = new Despesa();
                    d.setNomeCategoria(rs.getString(nomeCategoria.getColuna()));
                    d.setNomeItem(rs.getString(nomeItem.getAliasColuna()));
                    d.setQuantidade(rs.getString(sumQuantidade.getAliasColuna()));
                    d.setValor(rs.getString(sumValor.getAliasColuna()));
                    Linhas.add(d);
                }
                ObservableList<Despesa> resultado = FXCollections.observableList(Linhas);
                return resultado;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Janela.showException(ex);
            return null;
        }
    }

    public ObservableList<Despesa> getRelatorioMensal(LocalDate hoje) {
        return getRelatorioMensal(hoje, false);
    }

    public ObservableList<Despesa> getRelatorioMensal(LocalDate hoje, Boolean somente_agendamento) {
        try {
            LocalDate inicio = hoje.withDayOfMonth(1);
            LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
            this.select(sumValor, nomeCategoria);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            agendada.setValor("1");
            if (somente_agendamento) {
                this.and(agendada, "=");
            } else {
                this.and(agendada, "<>");
            }
            this.groupBy(nomeCategoria);
            this.orderByAsc(nomeCategoria);
            ResultSet rs = this.query();
            if (rs != null) {
                List<Despesa> linhas = new ArrayList<>();
                while (rs.next()) {
                    Despesa d = new Despesa();
                    d.setValor(rs.getString(sumValor.getAliasColuna()));
                    d.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
                    linhas.add(d);
                }
                ObservableList<Despesa> resultado = FXCollections.observableList(linhas);
                return resultado;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Janela.showException(ex);
            return null;
        }
    }

    @Override
    public List<XYChart.Series<String, Number>> getRelatorioMensalBarras(LocalDate inicio, LocalDate fim, String nome_categoria, String id_categoria, Integer tipo_categoria) {
        try {
            Coluna coluna = nomeCategoria;
            if (nome_categoria != null) {
                coluna = nomeItem;
                nomeCategoria.setValor(nome_categoria);
            }
            this.select(sumValor, sumQuantidade, coluna);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            if (agendada.getValor().equals("1")) {
                this.and(agendada, "=");
            } else {
                agendada.setValor("1");
                this.and(agendada, "<>");
                agendada.setValor("0");
            }
            if (nome_categoria != null) {
                this.and(nomeCategoria, "=");
            }
            if (id_categoria != null) {
                if (tipo_categoria == 1) {
                    idConta.setValor(id_categoria);
                    this.and(idConta, "=");
                } else if (tipo_categoria == 2) {
                    idCartaoCredito.setValor(id_categoria);
                    this.and(idCartaoCredito, "=");
                }
            } else if (tipo_categoria == 4) {
                this.andIsNotNull(idCartaoCredito);
            } else if (tipo_categoria == 3) {
                this.andIsNull(idCartaoCredito);
            }
            this.groupBy(coluna);
            this.orderByAsc(coluna);
            ResultSet rs = this.query();
            if (rs != null) {
                List<XYChart.Series<String, Number>> categorias = new ArrayList<>();
                while (rs.next()) {
                    XYChart.Series<String, Number> categoria = new XYChart.Series<>();
                    StringBuilder descricao = new StringBuilder(rs.getString(coluna.getAliasColuna()));
                    if (nome_categoria != null) {
                        descricao.insert(0, rs.getString(sumQuantidade.getAliasColuna()) + " ");
                    }
                    categoria.setName(descricao.toString());
                    categoria.getData().add(new XYChart.Data<>("", rs.getDouble(sumValor.getAliasColuna())));
                    categorias.add(categoria);
                }
                return categorias;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Janela.showException(ex);
            return null;
        }
    }

    public void preencherRelatorioMensalLinhas(LineChart<String, Number> graficoLinhas, LocalDate inicio, LocalDate fim, List<String> categoriasSelecionadas, Categoria conta, String tipo) {
        try {
            if (tipo.equals(idioma.getMensagem("semanal"))) {
                this.select(sumValor, nomeCategoria, ano, mes, semana);
            } else if (tipo.equals(idioma.getMensagem("mensal"))) {
                this.select(sumValor, nomeCategoria, ano, mes);
            } else {
                this.select(sumValor, nomeCategoria, ano);
            }
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            agendada.setValor("1");
            this.and(agendada, "<>");
            if (categoriasSelecionadas.size() > 0) {
                this.andIN(idCategoria, categoriasSelecionadas);
            }
            if (conta.getIdCategoria() != null) {
                idConta.setValor(conta.getIdCategoria());
                this.and(idConta, "=");
            }
            if (tipo.equals(idioma.getMensagem("semanal"))) {
                this.groupBy(ano, mes, semana, nomeCategoria);
                this.orderByAsc(ano, mes, semana);
            } else if (tipo.equals(idioma.getMensagem("mensal"))) {
                this.groupBy(ano, mes, nomeCategoria);
                this.orderByAsc(ano, mes);
            } else {
                this.groupBy(ano, nomeCategoria);
                this.orderByAsc(ano);
            }
            ResultSet rs = this.query();
            BigDecimal valor_total = new BigDecimal("0.0");
            List<XYChart.Series<String, Number>> categorias = new ArrayList<>();
            if (rs != null) {
                Set<String> datas = new LinkedHashSet<>();
                XYChart.Series<String, Number> categoria = new XYChart.Series<>();
                while (rs.next()) {
                    String nome_categoria = rs.getString(nomeCategoria.getAliasColuna());
                    try {
                        categoria = categorias.stream().filter(c -> c.getName().equals(nome_categoria)).findFirst().get();
                    } catch (NoSuchElementException ex) {
                        categoria = new XYChart.Series<>();
                        categoria.setName(nome_categoria);
                        categorias.add(categoria);
                    }
                    String descricao = "";
                    if (tipo.equals(idioma.getMensagem("semanal"))) {
                        descricao = rs.getString(semana.getAliasColuna()) + "/" + idioma.getNomeMes(rs.getInt(mes.getAliasColuna())).substring(0, 3) + "/" + rs.getString(ano.getAliasColuna());
                    } else if (tipo.equals(idioma.getMensagem("mensal"))) {
                        descricao = idioma.getNomeMes(rs.getInt(mes.getAliasColuna())).substring(0, 3) + "/" + rs.getString(ano.getAliasColuna());
                    } else {
                        descricao = rs.getString(ano.getAliasColuna());;
                    }
                    datas.add(descricao);
                    valor_total = valor_total.add(new BigDecimal(rs.getString(sumValor.getAliasColuna())));
                    categoria.getData().add(new XYChart.Data<>(descricao, rs.getDouble(sumValor.getAliasColuna())));
                }
                // Bug JavaFX
                if (categorias.size() > 0) {
                    categoria = categorias.get(0);
                    for (String d : datas) {
                        try {
                            categoria.getData().stream().filter(c -> c.getXValue().equals(d)).findFirst().get();
                        } catch (NoSuchElementException ex) {
                            categoria.getData().add(new XYChart.Data<>(d, 0));
                        }
                    }
                    if (tipo.equals(idioma.getMensagem("semanal"))) {
                        categoria.getData().sort((Data<String, Number> a, Data<String, Number> b) -> {
                            String[] A = a.getXValue().split("/");
                            String[] B = b.getXValue().split("/");
                            if (Integer.parseInt(A[2]) > Integer.parseInt(B[2])) {
                                return 1;
                            } else if (Integer.parseInt(A[2]) < Integer.parseInt(B[2])) {
                                return -1;
                            } else if (Integer.parseInt(A[0]) > Integer.parseInt(B[0])) {
                                return 1;
                            } else {
                                return -1;
                            }
                        });
                    }
                }
            }
            graficoLinhas.setTitle(idioma.getMensagem("categorias") + " - " + idioma.getMensagem("moeda") + " " + valor_total);
            graficoLinhas.getData().setAll(categorias);
        } catch (SQLException ex) {
            Janela.showException(ex);
        }
    }

    @Override
    protected Despesa getThis() {
        return this;
    }

}

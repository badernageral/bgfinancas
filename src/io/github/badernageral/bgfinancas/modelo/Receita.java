/*
Copyright 2012-2018 Jose Robson Mariano Alves

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
package io.github.badernageral.bgfinancas.modelo;

import io.github.badernageral.bgfinancas.biblioteca.banco.Banco;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Modelo;
import io.github.badernageral.bgfinancas.biblioteca.banco.Coluna;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Grafico;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Funcao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Datas;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
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
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public final class Receita extends Banco<Receita> implements Modelo, Grafico {
    
    private static final String MODULO = RAIZ+"/modulo/receita";
    
    public static final String FXML = MODULO+"/Receita.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/ReceitaFormulario.fxml";

    public static final String TABELA = "receitas";

    private final Coluna idReceita = new Coluna(TABELA, "id_receita");
    private final Coluna idConta = new Coluna(TABELA, "id_conta");
    private final Coluna idItem = new Coluna(TABELA, "id_item");
    private final Coluna descricao = new Coluna(TABELA, "descricao", "");
    private final Coluna valor = new Coluna(TABELA, "valor");
    private final Coluna data = new Coluna(TABELA, "data");
    private final Coluna hora = new Coluna(TABELA, "hora");
    private final Coluna agendada = new Coluna(TABELA, "agendada");
    private final Coluna parcela = new Coluna(TABELA, "parcela");
    
    private final Coluna idContaInner = new Coluna(Conta.TABELA, "id_conta");
    private final Coluna nomeConta = new Coluna(Conta.TABELA, "nome", "", "nome_conta");

    private final Coluna idItemInner = new Coluna(ReceitaItem.TABELA, "id_item");
    private final Coluna idCategoria = new Coluna(ReceitaItem.TABELA, "id_categoria");
    private final Coluna nomeItem = new Coluna(ReceitaItem.TABELA, "nome", "", "nome_item");
    
    private final Coluna idCategoriaInner = new Coluna(ReceitaCategoria.TABELA, "id_categoria");
    private final Coluna nomeCategoria = new Coluna(ReceitaCategoria.TABELA, "nome", "", "nome_categoria");
    
    private final Coluna sumValor = new Coluna(TABELA, "valor", "sum_valor", Funcao.SOMAR);
    
    private String tipo = idioma.getMensagem("receita");
    private String status = idioma.getMensagem("efetuado");
    
    private Boolean somenteAgendamento = false;
    private Boolean especificarMesAno = false;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    
    public Receita(){ }
    
    public Receita(String idItem, String nomeItem, String nomeCategoria, String descricao, String valor){
        this.idItem.setValor(idItem);
        this.nomeItem.setValor(nomeItem);
        this.nomeCategoria.setValor(nomeCategoria);
        this.descricao.setValor(descricao);
        this.valor.setValor(valor);
        this.agendada.setValor("0");
    }   

    public Receita(String idReceita, String idConta, String idItem, String descricao, String valor, LocalDate data, String hora, String agendada){
        this.idReceita.setValor(idReceita);
        this.idConta.setValor(idConta);
        this.idItem.setValor(idItem);
        this.descricao.setValor(descricao);
        this.valor.setValor(valor);
        this.data.setValor(Datas.toSqlData(data));
        this.hora.setValor(hora);
        this.agendada.setValor(agendada);
    }
    
    @Override
    protected Receita instanciar(ResultSet rs) throws SQLException{
        Receita r = new Receita(
            rs.getString(idReceita.getColuna()),
            rs.getString(idConta.getColuna()),
            rs.getString(idItem.getColuna()),
            rs.getString(descricao.getColuna()),
            rs.getString(valor.getColuna()),
            rs.getDate(data.getColuna()).toLocalDate(),
            rs.getString(hora.getColuna()),
            rs.getString(agendada.getColuna())
        );
        r.setParcela(rs.getString(parcela.getColuna()));
        r.setNomeConta(rs.getString(nomeConta.getAliasColuna()));
        r.setNomeItem(rs.getString(nomeItem.getAliasColuna()));
        r.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
        return r;
    }
      
    @Override
    public boolean cadastrar(){
        return this.insert(idConta, idItem, descricao, valor, data, hora, agendada, parcela).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(idConta, descricao, valor, data, agendada).where(idReceita, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idReceita, "=").commit();
    }
    
    @Override
    public Usuario consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public ObservableList<Receita> listar(){
        try{
            this.select(idReceita, idConta, idItem, descricao, valor, data, hora, agendada, parcela, nomeConta, nomeItem, nomeCategoria);
            this.inner(idConta, idContaInner);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            this.where(descricao, "LIKE", "(");
            this.or(nomeConta, "LIKE");
            this.or(nomeItem, "LIKE");
            this.or(data, "=");
            this.or(valor, "=");
            this.or(nomeCategoria, "LIKE", ")");
            if(idItem.getValor() != null){
                this.and(idItem, "=");
            }
            if(idCategoria.getValor() != null){
                this.and(idCategoria, "=");
            }
            if(idConta.getValor() != null){
                this.and(idConta, "=");
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
            if(rs != null){
                List<Receita> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Receita> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public boolean isReceitasAtrasadas() {
        try {
            this.select(idReceita);
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
    
    public String getIdReceita() {
        return idReceita.getValor();
    }
    
    public String getIdConta() {
        return idConta.getValor();
    }
    
    public String getIdItem() {
        return idItem.getValor();
    }
    
    public String getDescricao() {
        return descricao.getValor();
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
    
    public Receita setIdItem(String id_item) {
        this.idItem.setValor(id_item);
        return getThis();
    }
    
    public void setValor(String valor) {
        this.valor.setValor(valor);
    }
    
    public void setDescricao(String descricao) {
        this.descricao.setValor(descricao);
    }
    
    public Receita setIdConta(Categoria conta) {
        this.idConta.setValor(conta.getIdCategoria());
        return getThis();
    }
    
    public Receita setIdConta(String id_conta) {
        this.idConta.setValor(id_conta);
        return getThis();
    }
        
    public void setData(String data) {
        this.data.setValor(data);
    }
    
    public void setHora(String hora) {
        this.hora.setValor(hora);
    }
    
    public Receita setAgendada(String agendada) {
        this.agendada.setValor(agendada);
        return getThis();
    }
    
    public void setParcela(String parcela) {
        this.parcela.setValor(parcela);
    }
    
    public void setNomeConta(String conta) {
        this.nomeConta.setValor(conta);
    }
    
    public void setNomeItem(String item) {
        this.nomeItem.setValor(item);
    }
    
    public void setNomeCategoria(String categoria) {
        this.nomeCategoria.setValor(categoria);
    }
    
    public Receita setIdCategoria(String idCategoria) {
        this.idCategoria.setValor(idCategoria);
        return this;
    }
    
    public Receita setSomenteAgendamento() {
        this.somenteAgendamento = true;
        return getThis();
    }
    
    public Receita setMesAno(int mes, int ano) {
        this.especificarMesAno = true;
        this.dataInicio = LocalDate.of(ano, mes, 1);
        this.dataFim = dataInicio.withDayOfMonth(dataInicio.lengthOfMonth());
        return getThis();
    }
    
    public Receita setFiltro(String filtro){
        descricao.setValor(filtro);
        nomeConta.setValor(filtro);
        nomeItem.setValor(filtro);
        nomeCategoria.setValor(filtro);
        try{
            LocalDate filtroData = LocalDate.parse(filtro, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            data.setValor(Datas.toSqlData(filtroData));
        }catch(DateTimeParseException e){}
        try{
            Validar.decimal(filtro, null);
            valor.setValor(filtro);
        }catch(Erro e){}
        return this;
    }
    
    public ObservableList<Receita> getRelatorioMensal(LocalDate hoje, Boolean somente_agendamento){
        try{
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
            if(rs != null){
                List<Receita> linhas = new ArrayList<>();
                while(rs.next()){
                    Receita r = new Receita();
                    r.setValor(rs.getString(sumValor.getAliasColuna()));
                    r.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
                    linhas.add(r);
                }
                ObservableList<Receita> resultado = FXCollections.observableList(linhas);
                return resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public ObservableList<Receita> getRelatorioMensal(LocalDate hoje) {
        return getRelatorioMensal(hoje, false);
    }
    
    @Override
    public List<XYChart.Series<String,Number>> getRelatorioMensalBarras(LocalDate inicio, LocalDate fim, String nome_categoria, String id_categoria, Integer tipo_categoria){
        try{
            Coluna coluna = nomeCategoria;
            if(nome_categoria != null){
                coluna = nomeItem;
                nomeCategoria.setValor(nome_categoria);
            }
            if(id_categoria != null){
                idConta.setValor(id_categoria);
            }
            this.select(sumValor, coluna);
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
            if(nome_categoria != null){
                this.and(nomeCategoria, "=");
            }
            if(id_categoria != null){
                this.and(idConta, "=");
            }
            this.groupBy(coluna);
            this.orderByAsc(coluna);
            ResultSet rs = this.query();
            if(rs != null){
                List<XYChart.Series<String,Number>> categorias = new ArrayList<>();
                while(rs.next()){
                    XYChart.Series<String,Number> categoria = new XYChart.Series<>();
                    categoria.setName(rs.getString(coluna.getAliasColuna()));
                    categoria.getData().add(new XYChart.Data<>("",rs.getDouble(sumValor.getAliasColuna())));
                    categorias.add(categoria);
                }
                return categorias;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }

    @Override
    protected Receita getThis() {
        return this;
    }

    public ObservableList<Extrato> getExtrato(LocalDate inicio, LocalDate fim, Categoria conta) {
        try {
            this.select(idItem, data, hora, nomeCategoria, nomeItem, parcela, valor, agendada);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.where(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            if (conta.getIdCategoria() != null) {
                idConta.setValor(conta.getIdCategoria());
                this.and(idConta, "=");
            }
            this.orderByAsc(data,hora);
            ResultSet rs = this.query();
            if (rs != null) {
                List<Extrato> objetos = new ArrayList<>();
                while (rs.next()) {
                    String status = idioma.getMensagem("efetuado");
                    if(rs.getString(agendada.getColuna()).equals("1")){
                        status = idioma.getMensagem("agendado");
                    }
                    String nomeReceita = rs.getString(nomeItem.getAliasColuna());
                    if(rs.getString(parcela.getColuna())!=null){
                        nomeReceita += " - "+rs.getString(parcela.getColuna());
                    }
                    Extrato e = new Extrato(
                            idioma.getMensagem("receita"),
                            rs.getString(data.getColuna()),
                            rs.getString(hora.getColuna()),
                            rs.getString(nomeCategoria.getAliasColuna()),
                            nomeReceita,
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

}

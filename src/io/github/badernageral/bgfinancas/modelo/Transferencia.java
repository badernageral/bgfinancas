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

public final class Transferencia extends Banco<Transferencia> implements Modelo, Grafico {
    
    private static final String MODULO = RAIZ+"/modulo/transferencia";
    
    public static final String FXML = MODULO+"/Transferencia.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/TransferenciaFormulario.fxml";

    public static final String TABELA = "transferencias";

    private final Coluna idTransferencia = new Coluna(TABELA, "id_transferencia");
    private final Coluna idContaOrigem = new Coluna(TABELA, "id_conta1");
    private final Coluna idContaDestino = new Coluna(TABELA, "id_conta2");
    private final Coluna idItem = new Coluna(TABELA, "id_item");
    private final Coluna descricao = new Coluna(TABELA, "descricao");
    private final Coluna valor = new Coluna(TABELA, "valor");
    private final Coluna data = new Coluna(TABELA, "data");
    private final Coluna hora = new Coluna(TABELA, "hora");
    
    private final Coluna idContaOrigemInner = new Coluna(Conta.TABELA, "id_conta", null, null, "conta_origem");
    private final Coluna nomeOrigemConta = new Coluna(Conta.TABELA, "nome", "", "nome_conta_origem", "conta_origem");
    
    private final Coluna idContaDestinoInner = new Coluna(Conta.TABELA, "id_conta", null, null, "conta_destino");
    private final Coluna nomeDestinoConta = new Coluna(Conta.TABELA, "nome", "", "nome_conta_destino", "conta_destino");

    private final Coluna idItemInner = new Coluna(TransferenciaItem.TABELA, "id_item");
    private final Coluna idCategoria = new Coluna(TransferenciaItem.TABELA, "id_categoria");
    private final Coluna nomeItem = new Coluna(TransferenciaItem.TABELA, "nome", "", "nome_item");
    
    private final Coluna idCategoriaInner = new Coluna(TransferenciaCategoria.TABELA, "id_categoria");
    private final Coluna nomeCategoria = new Coluna(TransferenciaCategoria.TABELA, "nome", "", "nome_categoria");
    
    private final Coluna sumValor = new Coluna(TABELA, "valor", "sum_valor", Funcao.SOMAR);
    
    public Transferencia(){ }
    
    public Transferencia(String idItem, String nomeItem, String nomeCategoria, String descricao, String valor){
        this.idItem.setValor(idItem);
        this.nomeItem.setValor(nomeItem);
        this.nomeCategoria.setValor(nomeCategoria);
        this.descricao.setValor(descricao);
        this.valor.setValor(valor);
    }   

    public Transferencia(String idTransferencia, String idContaOrigem, String idContaDestino, String idItem, String descricao, String valor, LocalDate data, String hora){
        this.idTransferencia.setValor(idTransferencia);
        this.idContaOrigem.setValor(idContaOrigem);
        this.idContaDestino.setValor(idContaDestino);
        this.idItem.setValor(idItem);
        this.descricao.setValor(descricao);
        this.valor.setValor(valor);
        this.data.setValor(Datas.toSqlData(data));
        this.hora.setValor(hora);
    }
    
    @Override
    protected Transferencia instanciar(ResultSet rs) throws SQLException{
        Transferencia d = new Transferencia(
            rs.getString(idTransferencia.getColuna()),
            rs.getString(idContaOrigem.getColuna()),
            rs.getString(idContaDestino.getColuna()),
            rs.getString(idItem.getColuna()),
            rs.getString(descricao.getColuna()),
            rs.getString(valor.getColuna()),
            rs.getDate(data.getColuna()).toLocalDate(),
            rs.getString(hora.getColuna())
        );
        d.setNomeContaOrigem(rs.getString(nomeOrigemConta.getAliasColuna()));
        d.setNomeContaDestino(rs.getString(nomeDestinoConta.getAliasColuna()));
        d.setNomeItem(rs.getString(nomeItem.getAliasColuna()));
        d.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
        return d;
    }
       
    @Override
    public boolean cadastrar(){
        return this.insert(idContaOrigem, idContaDestino, idItem, descricao, valor, data, hora).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(idContaOrigem, idContaDestino, descricao, valor, data).where(idTransferencia, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idTransferencia, "=").commit();
    }
    
    @Override
    public Transferencia consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public ObservableList<Transferencia> listar(){
        try{
            this.select(idTransferencia, idContaOrigem, idContaDestino, idItem, descricao, valor, data, hora, nomeOrigemConta, nomeDestinoConta, nomeItem, nomeCategoria);
            this.inner(idContaOrigem, idContaOrigemInner);
            this.inner(idContaDestino, idContaDestinoInner);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            this.where(nomeOrigemConta, "LIKE", "(");
            this.or(nomeDestinoConta, "LIKE");
            this.or(nomeItem, "LIKE");
            this.or(descricao, "LIKE");
            this.or(data, "=");
            this.or(valor, "=");
            this.or(nomeCategoria, "LIKE", ")");
            if(idItem.getValor() != null){
                this.and(idItem, "=");
            }
            if(idCategoria.getValor() != null){
                this.and(idCategoria, "=");
            }
            if(idContaOrigem.getValor() != null){
                this.and(idContaOrigem, "=");
            }
            if(idContaDestino.getValor() != null){
                this.and(idContaDestino, "=");
            }
            this.orderByDesc(data, hora);
            ResultSet rs = this.query();
            if(rs != null){
                List<Transferencia> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Transferencia> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public String getIdTransferencia() {
        return idTransferencia.getValor();
    }
    
    public String getIdContaOrigem() {
        return idContaOrigem.getValor();
    }
    
    public String getIdContaDestino() {
        return idContaDestino.getValor();
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
    
    public String getNomeContaOrigem() {
        return nomeOrigemConta.getValor();
    }
    
    public String getNomeContaDestino() {
        return nomeDestinoConta.getValor();
    }
    
    public String getNomeItem() {
        return nomeItem.getValor();
    }
    
    public String getNomeCategoria() {
        return nomeCategoria.getValor();
    }
    
    public Transferencia setIdItem(String id_item) {
        this.idItem.setValor(id_item);
        return getThis();
    }
    
    public void setValor(String valor) {
        this.valor.setValor(valor);
    }
    
    public void setDescricao(String descricao) {
        this.descricao.setValor(descricao);
    }
    
    public Transferencia setIdContaOrigem(Categoria conta) {
        this.idContaOrigem.setValor(conta.getIdCategoria());
        return getThis();
    }
    
    public Transferencia setIdContaDestino(Categoria conta) {
        this.idContaDestino.setValor(conta.getIdCategoria());
        return getThis();
    }
        
    public void setData(String data) {
        this.data.setValor(data);
    }
    
    public void setHora(String hora) {
        this.hora.setValor(hora);
    }
    
    public void setNomeContaOrigem(String conta) {
        this.nomeOrigemConta.setValor(conta);
    }
    
    public void setNomeContaDestino(String conta) {
        this.nomeDestinoConta.setValor(conta);
    }
    
    public void setNomeItem(String item) {
        this.nomeItem.setValor(item);
    }
    
    public void setNomeCategoria(String categoria) {
        this.nomeCategoria.setValor(categoria);
    }
    
    public Transferencia setIdCategoria(String idCategoria) {
        this.idCategoria.setValor(idCategoria);
        return this;
    }
    
    public Transferencia setFiltro(String filtro){
        nomeOrigemConta.setValor(filtro);
        nomeDestinoConta.setValor(filtro);
        nomeItem.setValor(filtro);
        nomeCategoria.setValor(filtro);
        descricao.setValor(filtro);
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
    
    public ObservableList<Transferencia> getRelatorioMensal(LocalDate hoje){
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
            this.groupBy(nomeCategoria);
            this.orderByAsc(nomeCategoria);
            ResultSet rs = this.query();
            if(rs != null){
                List<Transferencia> linhas = new ArrayList<>();
                while(rs.next()){
                    Transferencia t = new Transferencia();
                    t.setValor(rs.getString(sumValor.getAliasColuna()));
                    t.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
                    linhas.add(t);
                }
                ObservableList<Transferencia> resultado = FXCollections.observableList(linhas);
                return resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
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
                idContaOrigem.setValor(id_categoria);
                idContaDestino.setValor(id_categoria);
            }
            this.select(sumValor, coluna);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            if(nome_categoria != null){
                this.and(nomeCategoria, "=");
            }
            if(id_categoria != null){
                this.and(idContaOrigem, "=", "(");
                this.or(idContaDestino, "=", ")");
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
    protected Transferencia getThis() {
        return this;
    }
    
}

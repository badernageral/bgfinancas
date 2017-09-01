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

import io.github.badernageral.bgfinancas.biblioteca.utilitario.Lista;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Modelo;
import io.github.badernageral.bgfinancas.biblioteca.banco.Coluna;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Operacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public final class Conta extends Categoria<Conta> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/conta";
    
    public static final String FXML = MODULO+"/Conta.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/ContaFormulario.fxml";
    
    public static final String TABELA = "contas";
    
    private final Coluna valor = new Coluna(TABELA, "valor");
    private final Coluna ativada = new Coluna(TABELA, "ativada");
    private final Coluna saldoTotal = new Coluna(TABELA, "saldo_total");
    
    public Conta(){ this(null, null, null, null, null); }
    
    public Conta(String idCategoria, String nome, String valor, Object ativada, Object saldoTotal){
        this.idCategoria = new Coluna(TABELA, "id_conta", idCategoria);
        this.nome = new Coluna(TABELA, "nome", nome);
        this.valor.setValor(valor);
        this.ativada.setValor(Lista.getSimNao(ativada));
        this.saldoTotal.setValor(Lista.getPoupancaCredito(saldoTotal));
    }
    
    @Override
    protected Conta instanciar(String idCategoria, String nome) {
        return new Conta(idCategoria, nome, null, null, null);
    }
    
    @Override
    protected Conta instanciar(ResultSet rs) throws SQLException{
        return new Conta(
            rs.getString(idCategoria.getColuna()),
            rs.getString(nome.getColuna()),
            rs.getString(valor.getColuna()),
            rs.getString(ativada.getColuna()),
            rs.getString(saldoTotal.getColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(nome, valor, ativada, saldoTotal).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(nome, valor, ativada, saldoTotal).where(idCategoria, "=").commit();
    }
    
    private boolean alterarSaldo(){
        return this.update(valor).where(idCategoria, "=").commit();
    }
    
    @Override
    public boolean excluir() throws Erro{
        ObservableList<Despesa> despesas = new Despesa().setIdConta(this).listar();
        ObservableList<Receita> receitas = new Receita().setIdConta(this).listar();
        ObservableList<Transferencia> transferencias_origem = new Transferencia().setIdContaDestino(this).listar();
        ObservableList<Transferencia> transferencias_destino = new Transferencia().setIdContaOrigem(this).listar();
        if(despesas.size()>0 || receitas.size()>0 || transferencias_origem.size()>0 || transferencias_destino.size()>0){
            throw new Erro();
        }else{
            return this.delete(idCategoria, "=").commit();
        }
    }
    
    @Override
    public Conta consultar() {
        try{
            this.select(idCategoria, nome, valor, ativada, saldoTotal);
            if(idCategoria.getValor() != null){
                this.where(idCategoria, "=");
            }else if(nome.getValor() != null){
                this.where(nome, "=");
            }
            ResultSet rs = this.query();
            if(rs != null && rs.next()){
                return instanciar(rs);
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    @Override
    public ObservableList<Conta> listar(){
        try{
            this.select(idCategoria, nome, valor, ativada, saldoTotal);
            if(nome.getValor() != null){
                this.where(nome, "LIKE");
            }else if(ativada.getValor() != null){
                this.where(ativada, "=");
            }
            this.orderByAsc(nome);
            ResultSet rs = this.query();
            if(rs != null){
                List<Conta> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Conta> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public void alterarSaldo(Operacao operacao, String id_conta, String valor){
        idCategoria.setValor(id_conta);
        Conta c = consultar();
        BigDecimal novoValor = c.getValor();
        if(operacao==Operacao.INCREMENTAR){
            novoValor = novoValor.add(new BigDecimal(valor));
        }else{
            novoValor = novoValor.subtract(new BigDecimal(valor));
        }
        c.setValor(novoValor.toString());
        c.alterarSaldo();
    }
    
    @Override
    public void montarSelectCategoria(ComboBox<Categoria> combo){
        try{
            combo.getItems().clear();
            combo.setPromptText(idioma.getMensagem("selecione"));
            ativada.setValor("1");
            this.select(idCategoria, nome, valor, ativada, saldoTotal).where(ativada, "=").orderByAsc(nome);
            ResultSet rs = this.query();
            if(rs != null){
                while(rs.next()){
                    combo.getItems().add(instanciar(rs));
                }
            }
        }catch(SQLException ex){
            Janela.showException(ex);
        }
    }
    
    public BigDecimal getValor() {
        return new BigDecimal(valor.getValor());
    }
    
    public String getAtivada() {
        return ativada.getValor();
    }
    
    public String getSaldoTotal() {
        return saldoTotal.getValor();
    }
    
    public void setValor(String valor) {
        this.valor.setValor(valor);
    }
    
    public Conta setAtivada(String ativada) {
        this.ativada.setValor(ativada);
        return getThis();
    }
    
    @Override
    protected Conta getThis() { 
        return this;
    }
    
}
/*
Copyright 2012-2015 Jose Robson Mariano Alves

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

import badernageral.bgfinancas.biblioteca.contrato.Modelo;
import badernageral.bgfinancas.biblioteca.banco.Coluna;
import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public final class CartaoCredito extends Categoria<CartaoCredito> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/cartao_credito";
    
    public static final String FXML = MODULO+"/CartaoCredito.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/CartaoCreditoFormulario.fxml";
    
    public static final String TABELA = "cartao_credito";
    
    private final Coluna limite = new Coluna(TABELA, "limite");
    private final Coluna vencimento = new Coluna(TABELA, "vencimento");
    
    private final Coluna saldo = new Coluna(TABELA, "saldo");
    
    public CartaoCredito(){ this(null, null, null, null); }
    
    public CartaoCredito(String idCategoria, String nome, String limite, String vencimento){
        this.idCategoria = new Coluna(TABELA, "id_cartao_credito", idCategoria);
        this.nome = new Coluna(TABELA, "nome", nome);
        this.limite.setValor(limite);
        this.vencimento.setValor(vencimento);
    }
    
    @Override
    protected CartaoCredito instanciar(String idCategoria, String nome) {
        return new CartaoCredito(idCategoria, nome, null, null);
    }
    
    @Override
    protected CartaoCredito instanciar(ResultSet rs) throws SQLException{
        return new CartaoCredito(
            rs.getString(idCategoria.getColuna()),
            rs.getString(nome.getColuna()),
            rs.getString(limite.getColuna()),
            rs.getString(vencimento.getColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(nome, limite, vencimento).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(nome, limite, vencimento).where(idCategoria, "=").commit();
    }
    
    private boolean alterarSaldo(){
        return this.update(limite).where(idCategoria, "=").commit();
    }
    
    @Override
    public boolean excluir() throws Erro{
        ObservableList<Despesa> despesas = new Despesa().setIdCartaoCredito(this).listar();
        if(despesas.size()>0){
            throw new Erro();
        }else{
            return this.delete(idCategoria, "=").commit();
        }
    }
    
    @Override
    public CartaoCredito consultar() {
        try{
            this.select(idCategoria, nome, limite, vencimento);
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
    public ObservableList<CartaoCredito> listar(){
        try{
            this.select(idCategoria, nome, limite, vencimento);
            if(nome.getValor() != null){
                this.where(nome, "LIKE");
            }
            this.orderby(nome);
            ResultSet rs = this.query();
            if(rs != null){
                List<CartaoCredito> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<CartaoCredito> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public ObservableList<CartaoCredito> getRelatorio(String filtro){
        ObservableList<CartaoCredito> grupos = new CartaoCredito().setNome(filtro).listar();
        for(CartaoCredito cc : grupos){
            BigDecimal _saldo = new BigDecimal(cc.getLimite());
            ObservableList<Despesa> despesas = new Despesa().setSomenteAgendamento().setIdCartaoCredito(cc).listar();
            for(Despesa d : despesas){
                _saldo = _saldo.subtract(new BigDecimal(d.getValor()));
            }
            cc.setSaldo(_saldo.toString());
        }
        return grupos;
    }
    
    @Override
    public void montarSelectCategoria(ComboBox<Categoria> combo){
        try{
            combo.getItems().clear();
            combo.setPromptText(idioma.getMensagem("selecione"));
            this.select(idCategoria, nome, limite, vencimento).orderby(nome);
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
    
    public String getLimite() {
        return limite.getValor();
    }
    
    public String getVencimento() {
        return vencimento.getValor();
    }
    
    public void setLimite(String limite) {
        this.limite.setValor(limite);
    }
    
    public void setVencimento(String vencimento) {
        this.vencimento.setValor(vencimento);
    }
    
    public String getSaldo() {
        return saldo.getValor();
    }
    
    public void setSaldo(String saldo) {
        this.saldo.setValor(saldo);
    }
    
    @Override
    protected CartaoCredito getThis() { 
        return this;
    }
    
}
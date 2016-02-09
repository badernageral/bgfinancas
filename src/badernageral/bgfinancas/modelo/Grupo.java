/*
Copyright 2015 Jose Robson Mariano Alves

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

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.contrato.Modelo;
import badernageral.bgfinancas.biblioteca.banco.Coluna;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public final class Grupo extends Categoria<Grupo> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/grupo";
    
    public static final String FXML_FORMULARIO = MODULO+"/GrupoFormulario.fxml";
    
    public static final String TABELA = "relatorios_grupos";
    
    private final Coluna valor = new Coluna(TABELA, "valor");
    
    private final Coluna saldo = new Coluna(TABELA, "saldo");
    
    public Grupo(){ 
        this(null, null, null);
    }
    
    public Grupo(String idCategoria, String nome, String valor){
        this.idCategoria = new Coluna(TABELA, "id_relatorios_grupos", idCategoria);
        this.nome = new Coluna(TABELA, "nome", nome);
        this.valor.setValor(valor);
    }
    
    @Override
    protected Grupo instanciar(String idCategoria, String nome) {
        return new Grupo(idCategoria, nome, null);
    }
    
    @Override
    protected Grupo instanciar(ResultSet rs) throws SQLException {
        return new Grupo(
            rs.getString(idCategoria.getColuna()),
            rs.getString(nome.getColuna()),
            rs.getString(valor.getColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(nome,valor).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(nome,valor).where(idCategoria, "=").commit();
    }
    
    @Override
    public boolean excluir() throws Erro{
        ObservableList<GrupoItem> itens = new GrupoItem().setIdCategoria(idCategoria.getValor()).listar();
        if(itens.size()>0){
            throw new Erro();
        }else{
            return this.delete(idCategoria, "=").commit();
        }
    }
    
    @Override
    public Grupo consultar(){
        try{
            this.select(idCategoria,nome,valor);
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
    public ObservableList<Grupo> listar(){
        try{
            this.select(idCategoria,nome,valor);
            if(nome.getValor() != null){
                this.where(nome, "LIKE");
            }
            this.orderby(nome);
            ResultSet rs = this.query();            
            if(rs != null){
                List<Grupo> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Grupo> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    @Override
    public void montarSelectCategoria(ComboBox<Categoria> Combo){
        try{
            Combo.getItems().clear();
            Combo.setPromptText(idioma.getMensagem("selecione"));
            this.select(idCategoria,nome,valor).orderby(nome);
            ResultSet rs = this.query();
            if(rs != null){
                while(rs.next()){
                    Combo.getItems().add(instanciar(rs));
                }
            }
        }catch(SQLException ex){
            Janela.showException(ex);
        }
    }
    
    public String getValor() {
        return valor.getValor();
    }
    
    public String getSaldo() {
        return saldo.getValor();
    }
    
    public void setSaldo(String saldo) {
        this.saldo.setValor(saldo);
    }
    
    public ObservableList<Grupo> getRelatorio(LocalDate hoje, String filtro){
        ObservableList<Grupo> grupos = new Grupo().setNome(filtro).listar();
        for(Grupo g : grupos){
            BigDecimal saldo = new BigDecimal(g.getValor());
            ObservableList<GrupoItem> _itens = new GrupoItem().setIdCategoria(g.getIdCategoria()).listar();
            for(GrupoItem i : _itens){
                saldo = saldo.subtract(new BigDecimal(new Despesa().setIdCategoria(i.getIdItem()).getSumValor(hoje)));
            }
            g.setSaldo(saldo.toString());
        }
        return grupos;
    }
    
    @Override
    protected Grupo getThis() { 
        return this; 
    }

}
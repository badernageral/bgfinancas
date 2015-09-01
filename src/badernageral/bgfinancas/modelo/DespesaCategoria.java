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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class DespesaCategoria extends Categoria<DespesaCategoria> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/despesa/categoria";
    
    public static final String FXML = MODULO+"/DespesaCategoria.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/DespesaCategoriaFormulario.fxml";
    
    public static final String TABELA = "despesas_categorias";
    
    private final SimpleBooleanProperty selecionado = new SimpleBooleanProperty(false);
    
    public DespesaCategoria(){ 
        this(null, null);
    }
    
    public DespesaCategoria(String idCategoria, String nome){
        this.idCategoria = new Coluna(TABELA, "id_categoria", idCategoria);
        this.nome = new Coluna(TABELA, "nome", nome);
    }
    
    @Override
    protected DespesaCategoria instanciar(String idCategoria, String nome) {
        return new DespesaCategoria(idCategoria, nome);
    }
    
    @Override
    protected DespesaCategoria instanciar(ResultSet rs) throws SQLException {
        return new DespesaCategoria(
            rs.getString(idCategoria.getColuna()),
            rs.getString(nome.getColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(nome).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(nome).where(idCategoria, "=").commit();
    }
    
    @Override
    public boolean excluir() throws Erro{
        ObservableList<DespesaItem> itens = new DespesaItem().setIdCategoria(idCategoria.getValor()).listar();
        if(itens.size()>0){
            throw new Erro();
        }else{
            return this.delete(idCategoria, "=").commit();
        }
    }
    
    @Override
    public DespesaCategoria consultar(){
        try{
            this.select(idCategoria, nome);
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
    public ObservableList<DespesaCategoria> listar(){
        try{
            this.select(idCategoria, nome);
            if(nome.getValor() != null){
                this.where(nome, "LIKE");
            }
            this.orderby(nome);
            ResultSet rs = this.query();            
            if(rs != null){
                List<DespesaCategoria> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<DespesaCategoria> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public SimpleBooleanProperty getSelecao() {
        return selecionado;
    }
    
    public boolean isSelecionado() {
        return selecionado.get();
    }

    @Override
    protected DespesaCategoria getThis() { 
        return this; 
    }
    
}
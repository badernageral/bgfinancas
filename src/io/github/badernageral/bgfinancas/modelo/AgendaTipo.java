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

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Modelo;
import io.github.badernageral.bgfinancas.biblioteca.banco.Coluna;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class AgendaTipo extends Categoria<AgendaTipo> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/agenda/tipo";
    
    public static final String FXML = MODULO+"/AgendaTipo.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/AgendaTipoFormulario.fxml";
    
    public static final String TABELA = "agenda_tipos";
    
    public AgendaTipo(){ 
        this(null, null);
    }
    
    public AgendaTipo(String idCategoria, String nome){
        this.idCategoria = new Coluna(TABELA, "id_tipo", idCategoria);
        this.nome = new Coluna(TABELA, "nome", nome);
    }
    
    @Override
    protected AgendaTipo instanciar(String idCategoria, String nome) {
        return new AgendaTipo(idCategoria, nome);
    }
    
    @Override
    protected AgendaTipo instanciar(ResultSet rs) throws SQLException {
        return new AgendaTipo(
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
        ObservableList<Agenda> itens = new Agenda().setIdCategoria(idCategoria.getValor()).listar();
        if(itens.size()>0){
            throw new Erro();
        }else{
            return this.delete(idCategoria, "=").commit();
        }
    }
    
    @Override
    public AgendaTipo consultar(){
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
    public ObservableList<AgendaTipo> listar(){
        try{
            this.select(idCategoria, nome);
            if(nome.getValor() != null){
                this.where(nome, "LIKE");
            }
            this.orderByAsc(nome);
            ResultSet rs = this.query();            
            if(rs != null){
                List<AgendaTipo> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<AgendaTipo> Resultado = FXCollections.observableList(Linhas);
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
    protected AgendaTipo getThis() { 
        return this; 
    }

}
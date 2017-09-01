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

package io.github.badernageral.bgfinancas.biblioteca.contrato;

import io.github.badernageral.bgfinancas.biblioteca.banco.Banco;
import io.github.badernageral.bgfinancas.biblioteca.banco.Coluna;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

public abstract class Categoria<T extends Categoria<T>> extends Banco<T> {
    
    protected Coluna idCategoria;
    protected Coluna nome;
    
    public void montarSelectCategoria(ComboBox<Categoria> combo){
        try{
            combo.getItems().clear();
            combo.setPromptText(idioma.getMensagem("selecione"));
            this.select(idCategoria, nome).orderByAsc(nome);
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
    
    public void montarSelectCategoria(ChoiceBox<Categoria> Choice){
        try{
            Choice.getItems().clear();
            T Todas = instanciar("todas", idioma.getMensagem("todas"));
            Choice.getItems().add(Todas);
            this.select(idCategoria, nome).orderByAsc(nome);
            ResultSet rs = this.query();
            if(rs != null){
                while(rs.next()){
                    Choice.getItems().add(instanciar(rs));
                }
            }
            Choice.getSelectionModel().selectFirst();
        }catch(SQLException ex){
            Janela.showException(ex);
        }
    }
    
    public String getIdCategoria() {
        return idCategoria.getValor();
    }
    
    public T setIdCategoria(String idCategoria){
        this.idCategoria.setValor(idCategoria);
        return getThis();
    }
    
    public String getNome() {
        return nome.getValor();
    }
    
    public T setNome(String nome){
        this.nome.setValor(nome);
        return getThis();
    }
    
    @Override
    protected abstract T instanciar(ResultSet rs) throws SQLException;
    
    protected abstract T instanciar(String idCategoria, String nome);
    
    @Override
    protected abstract T getThis();
    
    @Override
    public String toString(){
        return getNome();
    }
    
}
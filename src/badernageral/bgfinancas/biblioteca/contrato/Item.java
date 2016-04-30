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

package badernageral.bgfinancas.biblioteca.contrato;

import badernageral.bgfinancas.biblioteca.banco.Coluna;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

public abstract class Item<T extends Item<T>> extends Categoria<T> {
    
    protected Coluna idItem;
    
    protected Coluna idCategoriaInner;
    protected Coluna nomeCategoria;
    
    public void montarSelectItem(ComboBox<Item> combo){
        try{
            combo.getItems().clear();
            if(combo.getPromptText().equals("")){
                combo.setPromptText(idioma.getMensagem("selecione"));
            }
            this.select(idItem, idCategoria, nome, nomeCategoria).inner(idCategoria, idCategoriaInner).orderby(nome);
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
    
    public void montarSelectItem(ChoiceBox<Item> Choice){
        try{
            Choice.getItems().clear();
            T Todas = instanciar("todas", idioma.getMensagem("todas"));
            Choice.getItems().add(Todas);
            this.select(idItem, idCategoria, nome, nomeCategoria).inner(idCategoria, idCategoriaInner).orderby(nome);
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
    
    @Override
    protected abstract T instanciar(ResultSet rs) throws SQLException;
    
    @Override
    protected abstract T instanciar(String idItem, String nome);
    
    public String getIdItem() {
        return idItem.getValor();
    }
    
    public T setIdItem(String idItem){
        this.idItem.setValor(idItem);
        return getThis();
    }
    
    public String getNomeCategoria() {
        if(nomeCategoria!=null){
            return nomeCategoria.getValor();
        }else{
            return null;
        }
    }
    
    public T setNomeCategoria(String nomeCategoria){
        this.nomeCategoria.setValor(nomeCategoria);
        return getThis();
    }
    
    @Override
    protected abstract T getThis();
    
    @Override
    public String toString(){
        if(getNomeCategoria()!=null){
            return getNome()+" ("+getNomeCategoria()+")";
        }else{
            return getNome();
        }
    }
    
    public static String[] getNomes(String descricao){
        String[] texto = descricao.split(" \\(");
        if(texto.length>1){
            texto[1] = texto[1].substring(0,texto[1].length()-1);
            return texto;
        }else{
            return null;
        }
    }
    
}

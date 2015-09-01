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

import badernageral.bgfinancas.biblioteca.banco.Coluna;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.contrato.Modelo;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class TransferenciaItem extends Item<TransferenciaItem> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/transferencia/item";
    
    public static final String FXML = MODULO+"/TransferenciaItem.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/TransferenciaItemFormulario.fxml";
    
    public static final String TABELA = "transferencias_itens";
    
    public TransferenciaItem(){
        this(null, null, "", "");
    }

    public TransferenciaItem(String idItem, String idCategoria, String nome, String categoria){
        this.idItem = new Coluna(TABELA, "id_item", idItem);
        this.idCategoria = new Coluna(TABELA, "id_categoria", idCategoria);
        this.nome = new Coluna(TABELA, "nome", nome);
        this.idCategoriaInner = new Coluna(TransferenciaCategoria.TABELA, "id_categoria", idCategoria);
        this.nomeCategoria = new Coluna(TransferenciaCategoria.TABELA, "nome", categoria, "nome_categoria");
    }
    
    @Override
    protected TransferenciaItem instanciar(String idItem, String nome) {
        return new TransferenciaItem(idItem, null, nome, null);
    }
    
    @Override
    protected TransferenciaItem instanciar(ResultSet rs) throws SQLException {
        return new TransferenciaItem(
                rs.getString(idItem.getColuna()),
                rs.getString(idCategoria.getColuna()),
                rs.getString(nome.getColuna()),
                rs.getString(nomeCategoria.getAliasColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(idCategoria, nome).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(idCategoria, nome).where(idItem, "=").commit();
    }
    
    @Override
    public boolean excluir() throws Erro{
        ObservableList<Transferencia> itens = new Transferencia().setIdItem(idItem.getValor()).listar();
        if(itens.size()>0){
            throw new Erro();
        }else{
            return this.delete(idItem, "=").commit();
        }
    }
    
    @Override
    public TransferenciaItem consultar() {
        try{
            this.select(idItem, idCategoria, nome, nomeCategoria);
            this.inner(idCategoria, idCategoriaInner);
            if(idItem.getValor() != null){
                this.where(idItem, "=");
            }else if(nome.getValor() != null){
                this.where(nome, "=");
                if(!nomeCategoria.getValor().equals("")){
                    this.and(nomeCategoria, "=");
                }
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
    public ObservableList<TransferenciaItem> listar(){
        try{
            this.select(idItem, idCategoria, nome, nomeCategoria);
            this.inner(idCategoria, idCategoriaInner);
            this.where(nome, "LIKE", "(").or(nomeCategoria, "LIKE", ")");
            if(idCategoria.getValor() != null){
                this.and(idCategoria, "=");
            }
            this.orderby(nomeCategoria, nome);
            ResultSet rs = this.query();
            if(rs != null){
                List<TransferenciaItem> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<TransferenciaItem> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public TransferenciaItem setFiltro(String filtro){
        nome.setValor(filtro);
        nomeCategoria.setValor(filtro);
        return this;
    }

    @Override
    protected TransferenciaItem getThis() {
        return this;
    }

}

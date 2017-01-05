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

package badernageral.bgfinancas.modelo;

import badernageral.bgfinancas.biblioteca.banco.Banco;
import badernageral.bgfinancas.biblioteca.banco.Coluna;
import badernageral.bgfinancas.biblioteca.contrato.Modelo;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class GrupoItem extends Banco<GrupoItem> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/grupo";
    
    public static final String FXML = MODULO+"/GrupoItem.fxml";
    
    public static final String TABELA = "relatorios_grupos_itens";
    
    private final Coluna idItem = new Coluna(TABELA, "id_despesas_categorias");
    private final Coluna idCategoria = new Coluna(TABELA, "id_relatorios_grupos");
    
    private final Coluna idItemInner = new Coluna(DespesaCategoria.TABELA, "id_categoria");
    private final Coluna nomeItem = new Coluna(DespesaCategoria.TABELA, "nome", "", "nome_item");
    
    public GrupoItem(){ 
        this(null, null, null);
    }
    
    public GrupoItem(String idItem, String idCategoria, String nomeItem){
        this.idItem.setValor(idItem);
        this.idCategoria.setValor(idCategoria);
        this.nomeItem.setValor(nomeItem);
    }
    
    @Override
    protected GrupoItem instanciar(ResultSet rs) throws SQLException {
        return new GrupoItem(
                rs.getString(idItem.getColuna()),
                rs.getString(idCategoria.getColuna()),
                rs.getString(nomeItem.getAliasColuna())
        );
    }

    @Override
    public boolean cadastrar(){
        return this.insert(idItem, idCategoria).commit();
    }
    
    @Override
    public boolean alterar(){
        System.out.println(idioma.getMensagem("nao_implementado"));
        return false;
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idItem, "=").and(idCategoria, "=").commit();
    }
    
    @Override
    public Usuario consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    
    @Override
    public ObservableList<GrupoItem> listar(){
        try{
            this.select(idItem, idCategoria, nomeItem);
            this.inner(idItem, idItemInner);
            if(idCategoria.getValor() != null){
                this.where(idCategoria, "=");
            }
            ResultSet rs = this.query();
            if(rs != null){
                List<GrupoItem> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<GrupoItem> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public String getIdItem() {
        return idItem.getValor();
    }
    
    public String getIdCategoria() {
        return idCategoria.getValor();
    }
    
    public String getNomeItem() {
        return nomeItem.getValor();
    }
    
    public GrupoItem setIdCategoria(String idCategoria) {
        this.idCategoria.setValor(idCategoria);
        return getThis();
    }
    
    @Override
    protected GrupoItem getThis() {
        return this;
    }
    
}

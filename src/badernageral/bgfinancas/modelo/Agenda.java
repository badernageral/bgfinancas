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
import badernageral.bgfinancas.biblioteca.utilitario.Datas;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Agenda extends Item<Agenda> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/agenda";
    
    public static final String FXML = MODULO+"/Agenda.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/AgendaFormulario.fxml";
    
    public static final String TABELA = "agenda";
    
    private final Coluna data = new Coluna(TABELA, "data");
    private final Coluna valor = new Coluna(TABELA, "valor");
    
    public Agenda(){ this(null, null, "", null, null, ""); }
    
    public Agenda(String idItem, String idCategoria, String nome, LocalDate data, String valor, String nomeCategoria){
        this.idItem = new Coluna(TABELA, "id_agenda", idItem);
        this.idCategoria = new Coluna(TABELA, "id_tipo", idCategoria);
        this.nome = new Coluna(TABELA, "descricao", nome);
        this.idCategoriaInner = new Coluna(AgendaTipo.TABELA, "id_tipo");
        this.nomeCategoria = new Coluna(AgendaTipo.TABELA, "nome", nomeCategoria, "nome_categoria");
        this.data.setValor(Datas.toSqlData(data));
        this.valor.setValor(valor);
    }
    
    @Override
    protected Agenda instanciar(String idItem, String nome) {
        return new Agenda(idItem, null, nome, null, null, null);
    }
    
    @Override
    protected Agenda instanciar(ResultSet rs) throws SQLException {
        return new Agenda(
                rs.getString(idItem.getColuna()),
                rs.getString(idCategoria.getColuna()),
                rs.getString(nome.getColuna()),
                rs.getDate(data.getColuna()).toLocalDate(),
                rs.getString(valor.getColuna()),
                rs.getString(nomeCategoria.getAliasColuna())
        );
    }

    @Override
    public boolean cadastrar(){
        return this.insert(idCategoria, nome, valor, data).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(idCategoria, nome, valor, data).where(idItem, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idItem, "=").commit();
    }
    
    @Override
    public Usuario consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    
    @Override
    public ObservableList<Agenda> listar(){
        try{
            this.select(idItem, idCategoria, nome, valor, data, nomeCategoria);
            this.inner(idCategoria, idCategoriaInner);
            this.where(nome, "LIKE", "(").or(nomeCategoria, "LIKE", ")");
            if(idCategoria.getValor() != null){
                this.and(idCategoria, "=");
            }
            this.orderby(data, nomeCategoria, nome);
            ResultSet rs = this.query();
            if(rs != null){
                List<Agenda> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Agenda> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public String getValor() {
        return valor.getValor();
    }
    
    public String getData() {
        return Datas.getDataExibicao(data.getValor());
    }
    
    public LocalDate getDataLocal() {
        return Datas.getLocalDate(data.getValor());
    }
    
    public Agenda setFiltro(String filtro){
        nome.setValor(filtro);
        nomeCategoria.setValor(filtro);
        return this;
    }

    @Override
    protected Agenda getThis() {
        return this;
    }
    
}

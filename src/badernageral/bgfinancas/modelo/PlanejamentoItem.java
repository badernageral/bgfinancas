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

import badernageral.bgfinancas.biblioteca.contrato.Modelo;
import badernageral.bgfinancas.biblioteca.banco.Coluna;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.modulo.planejamento.ModalPlanejamentoItemControlador;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public final class PlanejamentoItem extends Item<PlanejamentoItem> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/planejamento/item";
    
    public static final String FXML = MODULO+"/PlanejamentoItem.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/PlanejamentoItemFormulario.fxml";

    public static final String TABELA = "planejamento_itens";
    
    public PlanejamentoItem(){
        this.nome = new Coluna(TABELA, "nome", "");
        this.idItem = new Coluna(TABELA, "id_item");
 }
    
    public PlanejamentoItem(String idPlajenamento, String nome){
        this();
        this.idItem.setValor(idPlajenamento);
        this.nome.setValor(nome);
    }
    
    @Override
    protected PlanejamentoItem instanciar(String idItem, String nome) {
        return new PlanejamentoItem(idItem,nome);
    }
        
    @Override
    protected PlanejamentoItem instanciar(ResultSet rs) throws SQLException{
        return new PlanejamentoItem(
            rs.getString(idItem.getColuna()),
            rs.getString(nome.getColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
            return this.insert(nome).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(nome).where(idItem, "=").commit();
    }
    
    @Override
    public boolean excluir() throws Erro{
        ObservableList<PlanejamentoComponente> planejamento = new PlanejamentoComponente().setIdItem(this.getIdItem()).listar();
        if(planejamento.size()>0){
            throw new Erro();
        }else{
            return this.delete(idItem, "=").commit();
        }
    }
    
    @Override
    public PlanejamentoItem consultar() {
        try{
            this.select(idItem, nome);
            if(idItem.getValor() != null){
                this.where(idItem, "=");
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
    public ObservableList<PlanejamentoItem> listar(){
        try{
            this.select(idItem, nome);
            if(!nome.getValor().equals("")){
                this.where(nome, "LIKE");
            }
            this.orderby(nome);
            ResultSet rs = this.query();
            if(rs != null){
                List<PlanejamentoItem> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<PlanejamentoItem> Resultado = FXCollections.observableList(Linhas);
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
    public void montarSelectItem(ComboBox<Item> Combo){
        try{
            Combo.getItems().clear();
            Combo.setPromptText(idioma.getMensagem("selecione"));
            this.select(idItem, nome).orderby(nome);
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
    
    public String modal(Acao acao, String campo, String valor){
        ModalPlanejamentoItemControlador janela = Janela.abrir(PlanejamentoComponente.FXML_MODAL_ITEM, idioma.getMensagem("planejamento"), true);
        if(acao == Acao.CADASTRAR){
            janela.setTitulo(idioma.getMensagem("adicionar_item"));
        }else{
            janela.setTitulo(idioma.getMensagem("alterar_item"));
        }
        janela.setMensagem(campo);
        janela.setValor(valor);
        Kernel.palcoModal.showAndWait();
        return janela.getResultado();
    }
    
    @Override
    protected PlanejamentoItem getThis() {
        return this;
    }
    
    @Override
    public String toString(){
        return getNome();
    }

}

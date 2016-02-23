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

package badernageral.bgfinancas.biblioteca.utilitario;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.idioma.Linguagem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class AutoFiltro<T extends Categoria> implements EventHandler<KeyEvent> {

    private final ComboBox<T> comboBox;
    private ObservableList<T> itens;
    private Boolean moverCursor = false;
    private int posicaoCursor;

    public AutoFiltro(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        this.comboBox.setPromptText(Linguagem.getInstance().getMensagem("autofiltro"));
        itens = this.comboBox.getItems();
        this.comboBox.setEditable(true);
        this.comboBox.setOnAction(e -> {
            String texto = this.comboBox.getEditor().getText();
            if(!texto.equals("") && !texto.contains("(")){
                if(this.getItem()!=null){
                    this.comboBox.getEditor().setText(this.getItem().toString());
                }
            }
        });
        this.comboBox.setOnKeyPressed((KeyEvent ke) -> {
            this.comboBox.hide();
        });
        this.comboBox.setOnHiding((event) -> {
            atualizar(false);
        });
        this.comboBox.setOnKeyReleased(AutoFiltro.this);
        this.comboBox.getStyleClass().add("AutoFiltro");
    }
    
    public void atualizarItens(){
        itens = this.comboBox.getItems();
    }
    
    public T getItem(){
        if(comboBox.getSelectionModel().getSelectedItem() instanceof Item){
            T item = comboBox.getSelectionModel().getSelectedItem();
            if(item != null){
                return item;
            }else{
                return null;
            }
        }else{
            if(comboBox.getItems().size()>0){
                T item = null;
                if(!comboBox.getEditor().getText().equals("")){
                    item = comboBox.getItems().get(0);
                }
                if(item != null){
                    return item;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }
    }

    @Override
    public void handle(KeyEvent eventoTecla) {
        if (eventoTecla.getCode() == KeyCode.RIGHT || eventoTecla.getCode() == KeyCode.LEFT
                || eventoTecla.getCode() == KeyCode.HOME || eventoTecla.getCode() == KeyCode.END
                || eventoTecla.getCode() == KeyCode.TAB || eventoTecla.isControlDown()
                || eventoTecla.getCode() == KeyCode.ENTER) {
            return;
        }else if(eventoTecla.getCode() == KeyCode.UP || eventoTecla.getCode() == KeyCode.DOWN) {
            if(!comboBox.isShowing()) {
                comboBox.show();
            }
            posicaoCursor = -1;
            moverCursor(comboBox.getEditor().getText().length());
            return;
        } else if(eventoTecla.getCode() == KeyCode.BACK_SPACE || eventoTecla.getCode() == KeyCode.DELETE) {
            moverCursor = true;
            posicaoCursor = comboBox.getEditor().getCaretPosition();
        }
        atualizar(true);
    }
    
    public void atualizar(boolean exibirLista) {
        String filtro = Outros.removerAcentos(comboBox.getEditor().getText().toLowerCase());
        ObservableList<T> lista_itens = FXCollections.observableArrayList();
        itens.stream().filter(item -> Outros.removerAcentos(item.toString().toLowerCase()).contains(filtro)).forEach(item -> {
            lista_itens.add(item);
        });
        lista_itens.sort((a, b)  -> {
            Item iA = (Item) a;
            Item iB = (Item) b;
            String nomeA = Outros.removerAcentos(iA.getNome().toLowerCase());
            String nomeB = Outros.removerAcentos(iB.getNome().toLowerCase());
            if(nomeA.contains(filtro) && !nomeB.contains(filtro)) {
                return -1;
            }
            if(!nomeA.contains(filtro) && nomeB.contains(filtro)) {
                return 1;
            }
            return Outros.removerAcentos(iA.toString()).compareTo(Outros.removerAcentos(iB.toString()));
        });
        comboBox.setItems(lista_itens);
        comboBox.getEditor().setText(comboBox.getEditor().getText());
        if(!moverCursor) {
            posicaoCursor = -1;
        }
        moverCursor(filtro.length());
        if(!lista_itens.isEmpty() && exibirLista) {
            comboBox.show();
        }
    }

    private void moverCursor(int posicao) {
        if(posicaoCursor == -1) {
            comboBox.getEditor().positionCaret(posicao);
        } else {
            comboBox.getEditor().positionCaret(posicaoCursor);
        }
        moverCursor = false;
    }

}
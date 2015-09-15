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

package badernageral.bgfinancas.biblioteca.sistema;

import badernageral.bgfinancas.biblioteca.contrato.Modelo;
import badernageral.bgfinancas.idioma.Linguagem;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Tabela<T extends Modelo> {

    public void prepararTabela(TableView<T> tabela, int numTabela){
        tabela.setRowFactory((TableView<T> paramP) -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getClickCount()==2 && (!row.isEmpty())) {
                    Kernel.controlador.acaoAlterar(numTabela);
                }
            });
            return row;
        });
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tabela.setPlaceholder(new Label(Linguagem.getInstance().getMensagem("tabela_vazia")));
        tabela.setFocusTraversable(false);
    }
    
    public void prepararTabela(TableView<T> tabela){
        prepararTabela(tabela, 1);
    }
    
    public void prepararTabelaSelecao(TableView<T> tabela, int numTabela){
        prepararTabela(tabela, numTabela);
        tabela.setOnKeyReleased(event -> {
            Kernel.controlador.acaoAlterar(numTabela+10);
        });
        tabela.setRowFactory((TableView<T> paramP) -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getClickCount()==2 && (!row.isEmpty())) {
                    Kernel.controlador.acaoAlterar(numTabela);
                }
                if (mouseEvent.getClickCount()==1 && (!row.isEmpty())) {
                    Kernel.controlador.acaoAlterar(numTabela+10);
                }
            });
            return row;
        });
    }

    public TableColumn<T,String> adicionarColuna(TableView<T> tabela, String titulo, String atributo){
        TableColumn<T,String> coluna = new TableColumn<>(titulo);
        tabela.getColumns().add(coluna);
        coluna.setCellValueFactory((TableColumn.CellDataFeatures<T,String> p) -> new PropertyValueFactory<T,String>(atributo).call(p));
        return coluna;
    }

    public void setColunaDinheiro(TableColumn<T,String> coluna, Boolean cores){
        coluna.setCellFactory((TableColumn<T, String> param) -> {
            if(cores){
                return new ColunaDinheiroColorida<>();
            }else{
                return new ColunaDinheiroNormal<>();
            }
            
        });
    }
    
    private static class ColunaDinheiroNormal<T extends Modelo> extends TableCell<T, String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : Linguagem.getInstance().getMensagem("moeda")+" "+item);
        }
    }
    
    private static class ColunaDinheiroColorida<T extends Modelo> extends TableCell<T, String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : Linguagem.getInstance().getMensagem("moeda")+" "+item);
            if (item != null) {
                double value = Double.parseDouble(item);
                setTextFill(value == 0 ? Color.BLACK : value < 0 ? Color.RED : Color.GREEN);
            }
        }
    }

}
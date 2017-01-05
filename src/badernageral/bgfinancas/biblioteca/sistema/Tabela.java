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

package badernageral.bgfinancas.biblioteca.sistema;

import badernageral.bgfinancas.idioma.Linguagem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Tabela<T> {

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
        tabela.getStyleClass().add("semFoco");
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
        coluna.setCellValueFactory(p -> new PropertyValueFactory<T,String>(atributo).call(p));
        return coluna;
    }
    
    public TableColumn<T,BigDecimal> adicionarColunaNumero(TableView<T> tabela, String titulo, String atributo){
        TableColumn<T,BigDecimal> coluna = new TableColumn<>(titulo);
        tabela.getColumns().add(coluna);
        coluna.setCellValueFactory(p -> new PropertyValueFactory<T,BigDecimal>(atributo).call(p));
        return coluna;
    }
    
    public TableColumn<T,LocalDate> adicionarColunaData(TableView<T> tabela, String titulo, String atributo){
        TableColumn<T,LocalDate> coluna = new TableColumn<>(titulo);
        tabela.getColumns().add(coluna);
        coluna.setCellValueFactory(p -> new PropertyValueFactory<T,LocalDate>(atributo).call(p));
        coluna.setCellFactory(e -> new TableCell<T, LocalDate>(){
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(item==null ? null : item.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
            }
        });
        return coluna;
    }
    
    public TableColumn<T,LocalDateTime> adicionarColunaDataHora(TableView<T> tabela, String titulo, String atributo){
        TableColumn<T,LocalDateTime> coluna = new TableColumn<>(titulo);
        tabela.getColumns().add(coluna);
        coluna.setCellValueFactory(p -> new PropertyValueFactory<T,LocalDateTime>(atributo).call(p));
        coluna.setCellFactory(e -> new TableCell<T, LocalDateTime>(){
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(item==null ? null : item.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
            }
        });
        return coluna;
    }
    
    public void setColunaColorida(TableColumn<T, BigDecimal> coluna){
        coluna.setCellFactory(e -> new TableCell<T, BigDecimal>(){
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(item==null ? null : item.toString());
                if (item != null) {
                    setTextFill(item.compareTo(BigDecimal.ZERO) == 0 ? Color.BLACK : item.compareTo(BigDecimal.ZERO) == -1 ? Color.RED : Color.GREEN);
                }
            }
        });
    }

}
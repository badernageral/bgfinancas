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
package badernageral.bgfinancas.biblioteca.utilitario;

import badernageral.bgfinancas.biblioteca.contrato.Controlador;
import badernageral.bgfinancas.idioma.Linguagem;
import badernageral.bgfinancas.modelo.Despesa;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

public class AreaTransferencia {

    private LocalDate data = LocalDate.now();
    private final Linguagem idioma = Linguagem.getInstance();
    private final ObservableList<Despesa> areaTransferencia = FXCollections.observableList(new ArrayList<Despesa>());

    public void criarMenu(Controlador controlador, TableView<Despesa> tabela, boolean cadastrar) {
        ContextMenu menu = new ContextMenu();
        MenuItem copiar = new MenuItem(idioma.getMensagem("copiar"));
        MenuItem colar = new MenuItem(idioma.getMensagem("colar"));
        copiar.disableProperty().bind(Bindings.isEmpty(tabela.getSelectionModel().getSelectedItems()));
        colar.disableProperty().bind(Bindings.isEmpty(areaTransferencia));
        copiar.setOnAction(event -> {
            areaTransferencia.clear();
            tabela.getSelectionModel().getSelectedItems().stream().forEach(d -> {
                areaTransferencia.add(d);
            });
        });
        colar.setOnAction(event -> {
            if (cadastrar) {
                try {
                    areaTransferencia.stream().forEach(d -> {
                        d.setData(Datas.toSqlData(Datas.getLocalDate(d.getData().getDayOfMonth(), data.getMonthValue(), data.getYear())));
                        d.cadastrar();
                    });
                } catch (Exception e) {

                }
                controlador.acaoFiltrar(true);
            } else {
                areaTransferencia.stream().forEach(d -> {
                    tabela.getItems().add(d.getClone());
                });
                controlador.acaoFiltrar(false);
            }
        });
        menu.getItems().addAll(copiar, colar);
        tabela.setContextMenu(menu);
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

}

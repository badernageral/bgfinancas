/*
Copyright 2012-2018 Jose Robson Mariano Alves

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

package io.github.badernageral.bgfinancas.template.modulo;

import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Tabela;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import io.github.badernageral.bgfinancas.modelo.Conta;
import io.github.badernageral.bgfinancas.modulo.conta.ContaFormularioControlador;
import javafx.scene.layout.GridPane;

public final class ListaConta implements Initializable {
    
    @FXML private GridPane listaConta;
    @FXML private TableView<Conta> tabelaListaConta;
    @FXML private Label labelContas;
    private final Tabela<Conta> tabelaConta = new Tabela<>();
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Linguagem idioma = Linguagem.getInstance();
        tabelaConta.prepararTabela(tabelaListaConta, 2);
        tabelaConta.adicionarColuna(tabelaListaConta, idioma.getMensagem("nome"), "nome");
        tabelaConta.setColunaColorida(tabelaConta.adicionarColunaNumero(tabelaListaConta, idioma.getMensagem("saldo"), "valor"));
        labelContas.setText(idioma.getMensagem("contas"));
    }
    
    public void acaoContas(){
        Kernel.principal.acaoConta();
    }
    
    public void atualizarTabela(boolean animacao){
        tabelaListaConta.setItems(new Conta().setAtivada("1").listar());
        if(animacao){
            Animacao.fadeOutIn(tabelaListaConta);
        }
    }
    
    public void alterarConta(String titulo){
        ObservableList<Conta> itens = tabelaListaConta.getSelectionModel().getSelectedItems();
        ContaFormularioControlador Controlador = Janela.abrir(Conta.FXML_FORMULARIO, titulo);
        Controlador.alterar(itens.get(0));
    }
    
    public GridPane getGridPane(){
        return listaConta;
    }
       
}
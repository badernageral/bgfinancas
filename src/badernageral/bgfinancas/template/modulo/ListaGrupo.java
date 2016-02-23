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

package badernageral.bgfinancas.template.modulo;

import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.sistema.Tabela;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.idioma.Linguagem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import badernageral.bgfinancas.modelo.Grupo;
import badernageral.bgfinancas.modulo.grupo.GrupoFormularioControlador;
import java.time.LocalDate;
import javafx.scene.layout.GridPane;

public final class ListaGrupo implements Initializable {
    
    @FXML private GridPane listaGrupo;
    @FXML private TableView<Grupo> tabelaListaGrupo;
    @FXML private Label labelGrupos;
    private final Tabela<Grupo> tabelaGrupo = new Tabela<>();
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Linguagem idioma = Linguagem.getInstance();
        tabelaGrupo.prepararTabela(tabelaListaGrupo, 3);
        tabelaGrupo.adicionarColuna(tabelaListaGrupo, idioma.getMensagem("cota"), "nome");
        tabelaGrupo.setColunaDinheiro(tabelaGrupo.adicionarColuna(tabelaListaGrupo, idioma.getMensagem("valor"), "valor"), false);
        tabelaGrupo.setColunaDinheiro(tabelaGrupo.adicionarColuna(tabelaListaGrupo, idioma.getMensagem("saldo"), "saldo"), true);
        labelGrupos.setText(idioma.getMensagem("cotas_despesas"));
    }
    
    public void acaoGrupos(){
        Kernel.principal.acaoGrupoItem();
    }
    
    public void atualizarTabela(boolean animacao){
        LocalDate hoje = LocalDate.now();
        tabelaListaGrupo.setItems(new Grupo().getRelatorio(hoje,null));
        if(animacao){
            Animacao.fadeOutIn(tabelaListaGrupo);
        }
    }
    
    public void gerenciarGrupo(){
        Kernel.principal.acaoGrupoItem();
    }
    
    public void alterarGrupo(String titulo){
        ObservableList<Grupo> itens = tabelaListaGrupo.getSelectionModel().getSelectedItems();
        GrupoFormularioControlador Controlador = Janela.abrir(Grupo.FXML_FORMULARIO, titulo);
        Controlador.alterar(itens.get(0));
    }
    
    public GridPane getGridPane(){
        return listaGrupo;
    }
       
}
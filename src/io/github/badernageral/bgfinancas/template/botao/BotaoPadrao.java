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

package io.github.badernageral.bgfinancas.template.botao;

import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public final class BotaoPadrao implements Initializable {
    
    @FXML private HBox painelBotoes;
    @FXML private Button cadastrar;
    @FXML private Button cadastrar_2;
    @FXML private Button alterar;
    @FXML private Button excluir;
    @FXML private Button voltar;
    @FXML private Label titulo;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Botao.prepararBotao(new Button[]{cadastrar}, alterar, excluir, voltar);
        painelBotoes.getChildren().remove(cadastrar_2);
    }
    
    public void adicionarNovoBotaoCadastrar(){
        painelBotoes.getChildren().add(cadastrar_2);
        Botao.prepararBotaoCadastrar(cadastrar_2, 2);
        cadastrar_2.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("cadastro_multiplo")+" (CTRL+SHIFT+I)"));
        alterar.toFront();
        excluir.toFront();
    }
    
    public Button getBotaoCadastrar(){
        return cadastrar;
    }
    
    public Button getBotaoCadastrar2(){
        return cadastrar_2;
    }
    
    public Button getBotaoAlterar(){
        return alterar;
    }
    
    public Button getBotaoExcluir(){
        return excluir;
    }
    
    public Button getBotaoVoltar(){
        return voltar;
    }
    
    public Label getLabelTitulo(){
        return titulo;
    }
    
}
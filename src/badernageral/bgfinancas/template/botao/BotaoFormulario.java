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

package badernageral.bgfinancas.template.botao;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public final class BotaoFormulario implements Initializable {
    
    @FXML private StackPane formularioBotao;
    @FXML private HBox grupoBotao;
    @FXML private Button finalizar;
    @FXML private Button cancelar;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Olá, eu sou nothing
    }
    
    public void setTextBotaoFinalizar(String texto){
        finalizar.setText(texto);
    }
    
    public void setTextBotaoCancelar(String texto){
        cancelar.setText(texto);
    }
    
    public Button getBotaoFinalizar(){
        return finalizar;
    }
    
    public Button getBotaoCancelar(){
        return cancelar;
    }
    
    public StackPane getStackPane(){
        return formularioBotao;
    }
    
    public HBox getGrupoBotao(){
        return grupoBotao;
    }
    
}
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

package badernageral.bgfinancas.template.barra;

import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public final class Filtro implements Initializable {
       
    @FXML private TextField filtro;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filtro.setOnKeyReleased((KeyEvent ke) -> {
            Kernel.controlador.acaoFiltrar(false);
        });
    }
    
    public TextField getFiltro(){
        return filtro;
    }
    
}
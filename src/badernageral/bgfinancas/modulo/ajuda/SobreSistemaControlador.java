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

package badernageral.bgfinancas.modulo.ajuda;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;

public final class SobreSistemaControlador implements Initializable, ControladorFormulario {
       
    @FXML private TitledPane formulario;
    @FXML private Text desenvolvedor;
    @FXML private Text site;
    @FXML private Button ok;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("sobre_sistema"));
        desenvolvedor.setText(idioma.getMensagem("desenvolvedor")+": José Robson Mariano Alves");
        site.setText(idioma.getMensagem("website_oficial")+": http://bgfinancas.sourceforge.net");
        ok.setText(idioma.getMensagem("ok"));
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
    }
    
    @Override
    public void acaoFinalizar(){
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoCadastrar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboItem(int combo, Item item) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}

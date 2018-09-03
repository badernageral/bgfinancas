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

package io.github.badernageral.bgfinancas.modulo.utilitario;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Calculadora;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public final class CalculadoraFormularioControlador implements Initializable, ControladorFormulario {
       
    @FXML private GridPane formulario;
    @FXML private TextField calculo;
    @FXML private Label ajuda;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Calculadora.preparar(calculo, ajuda);
    }
    
    @Override
    public void acaoFinalizar(){
        try {
            Validar.textFieldDecimal(calculo);
        } catch (Erro ex) {
            //
        }
    }
    
    @Override
    public void acaoCancelar() {
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

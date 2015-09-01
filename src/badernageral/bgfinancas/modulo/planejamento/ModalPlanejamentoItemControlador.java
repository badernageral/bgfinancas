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

package badernageral.bgfinancas.modulo.planejamento;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.idioma.Linguagem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.Stage;
    
public final class ModalPlanejamentoItemControlador implements Initializable, ControladorFormulario {
    
    @FXML private TitledPane formulario;
    @FXML private Text campo;
    @FXML private TextField entrada;
    @FXML private Button ok;
    @FXML private Button cancelar;
    private String resultado;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ok.setText(Linguagem.getInstance().getMensagem("ok"));
        cancelar.setText(Linguagem.getInstance().getMensagem("cancelar"));
        ok.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("ok")+" (ALT+ENTER)"));
        cancelar.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("cancelar")+" (ESCAPE)"));
    }
    
    public void setTitulo(String titulo){
        formulario.setText(titulo);
    }
    
    public void setMensagem(String campo){
        this.campo.setText(campo);
    }
    
    public void setValor(String valor){
        this.entrada.setText(valor);
    }
    
    @FXML
    @Override
    public void acaoFinalizar() {
        try{
            Validar.textFieldDecimal(entrada);
            resultado = entrada.getText();
            Animacao.fadeInOutClose(formulario);
        }catch(Erro e){ }
    }
    
    public String getResultado(){
        return resultado;
    }
    
    @FXML
    @Override
    public void acaoCancelar() {
        Stage palco = (Stage) formulario.getScene().getWindow();
        palco.setTitle("");
        Animacao.fadeInOutClose(formulario);
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

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

package badernageral.bgfinancas.modulo.utilitario;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.modelo.Configuracao;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import badernageral.bgfinancas.template.botao.BotaoFormulario;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;

public final class ConfiguracaoFormularioControlador implements Initializable, ControladorFormulario {
       
    @FXML private TitledPane formulario;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelIdioma;
    @FXML private Label labelMoeda;
    @FXML private ComboBox<String> idiomas;
    @FXML private TextField moeda;
    
    private boolean primeiroAcesso = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Botao.prepararBotaoModal(this, botaoController);
        idiomas.setItems(idioma.getListaIdiomas());
        idiomas.getSelectionModel().select(idioma.getIdiomaSistema());
        mudarIdioma();
    }
    
    public void setPrimeiroAcesso(){
        primeiroAcesso = true;
        mudarIdioma();
    }
    
    public void mudarIdioma(){
        if(idiomas.getSelectionModel().getSelectedItem().equals("English")){
            moeda.setText("$");
            formulario.setText("Settings");
            labelIdioma.setText("Language:");
            labelMoeda.setText("Currency:");
            if(primeiroAcesso){
                botaoController.setTextBotaoFinalizar("Next");
                botaoController.setTextBotaoCancelar("Exit");
            }else{
                botaoController.setTextBotaoFinalizar("Edit");
                botaoController.setTextBotaoCancelar("Cancel");
            }
        }else{
            moeda.setText("R$");
            formulario.setText("Configurações");
            labelIdioma.setText("Idioma:");
            labelMoeda.setText("Moeda:");
            if(primeiroAcesso){
                botaoController.setTextBotaoFinalizar("Próximo");
                botaoController.setTextBotaoCancelar("Sair");
            }else{
                botaoController.setTextBotaoFinalizar("Alterar");
                botaoController.setTextBotaoCancelar("Cancelar");
            }
        }
    }
    
    @Override
    public void acaoCancelar() {
        if(primeiroAcesso){
            System.exit(0);
        }else{
            Animacao.fadeInOutClose(formulario);
        }
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            Configuracao cIdioma = new Configuracao().setNome("idioma").consultar();
            Configuracao cMoeda = new Configuracao().setNome("moeda").consultar();
            if(cIdioma!=null && cMoeda!=null){
                cIdioma.setValor(idioma.idiomaToLocale(idiomas.getSelectionModel().getSelectedItem()));
                cIdioma.alterar();
                cMoeda.setValor(moeda.getText());
                cMoeda.alterar();
                if(primeiroAcesso){
                    Kernel.main.continuarPrimeiroAcesso();
                }else{
                    Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                    Animacao.fadeInOutClose(formulario);
                    Kernel.main.reiniciar();
                }
            }else{
                Janela.showMensagem(Status.ERRO, idioma.getMensagem("erro"));
            }
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.comboBox(idiomas);
            Validar.textField(moeda);
            return true;
        } catch (Erro ex) {
            return false;
        }
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

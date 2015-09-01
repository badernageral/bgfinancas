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

package badernageral.bgfinancas.biblioteca.utilitario;

import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.idioma.Linguagem;
import java.math.BigDecimal;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public final class Validar {
    
    private static final Linguagem idioma = Linguagem.getInstance();
    
    private Validar(){ }
    
    public static void textField(TextField campo) throws Erro{
        if(campo.getText().equals("")){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campo_nao_informado"), campo, Duracao.CURTA);
            campo.requestFocus();
            throw new Erro();
        }
    }
    
    public static void datePicker(DatePicker campo) throws Erro{
        if(campo.getValue() == null){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campo_nao_informado"), campo, Duracao.CURTA);
            campo.requestFocus();
            throw new Erro();
        }
    }
    
    public static void comboBox(ComboBox campo) throws Erro{
        if(campo.getSelectionModel().getSelectedItem()==null){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campo_nao_informado"), campo, Duracao.CURTA);
            campo.requestFocus();
            throw new Erro();
        }
    }
    
    public static void igualdade(ComboBox campo_1, ComboBox campo_2) throws Erro{
        if(campo_1.getSelectionModel().getSelectedItem().toString().equals(campo_2.getSelectionModel().getSelectedItem().toString())){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campos_iguais"), campo_1, Duracao.CURTA);
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campos_iguais"), campo_2, Duracao.CURTA);
            campo_1.requestFocus();
            throw new Erro();
        }
    }
    
    public static void passwordField(PasswordField senha) throws Erro{
        if(senha.getText().equals("")){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campo_nao_informado"), senha, Duracao.CURTA);
            senha.requestFocus();
            throw new Erro();
        }
    }
    
    public static void passwordField(PasswordField senha, PasswordField senha_confirmar) throws Erro{
        Validar.passwordField(senha);
        if(!senha.getText().equals(senha_confirmar.getText())){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("senhas_diferentes"), senha, Duracao.CURTA);
            senha.requestFocus();
            throw new Erro();
        }
    }
    
    public static void tableView(TableView tabela, Control control) throws Erro{
        if(tabela.getItems().size()<1){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("restricao_tabela"), control, Duracao.CURTA);
            tabela.requestFocus();
            throw new Erro();
        }
    }
    
    public static void textFieldDecimal(TextField campo) throws Erro{
        try{
            BigDecimal valor = new BigDecimal(campo.getText());
        }catch(NumberFormatException ex){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("use_ponto_decimais"), campo, Duracao.NORMAL);
            campo.requestFocus();
            throw new Erro();
        }
    }
    
    public static void decimal(String numero, Control campo) throws Erro{
        try{
            BigDecimal valor = new BigDecimal(numero);
        }catch(NumberFormatException ex){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("use_ponto_decimais"), campo, Duracao.NORMAL);
            throw new Erro();
        }
    }
    
    public static void autoFiltro(AutoFiltro autoFiltro, ComboBox campo) throws Erro{
        if(autoFiltro.getItem()==null){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("validar_autofiltro"), campo, Duracao.LONGA);
            campo.requestFocus();
            throw new Erro();
        }
    }
    
    public static boolean alteracao(ObservableList<?> lista, Button alterar){
        if(lista.size()<1 || lista.get(0)==null){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("selecione_item_alterar"), alterar, Duracao.NORMAL);
            return false;
        }else if(lista.size()>1){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("restricao_alteracao_multipla"), alterar, Duracao.NORMAL);
            return false;
        }else{
            return true;
        }
    }
    
    public static boolean exclusao(ObservableList<?> lista, Button excluir, Boolean pergunta){
        if(lista.size()<=0 || lista.get(0)==null){
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("selecione_item_excluir"), excluir, Duracao.NORMAL);
            return false;
        }else{
            if(pergunta){
                return Janela.showPergunta(idioma.getMensagem("tem_certeza"));
            }else{
                return true;
            }
        }
    }
    
    public static boolean exclusao(ObservableList<?> lista, Button excluir){
        return exclusao(lista, excluir, true);
    }
    
}

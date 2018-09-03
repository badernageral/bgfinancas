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

package io.github.badernageral.bgfinancas.biblioteca.sistema;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Controlador;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Configuracao;
import io.github.badernageral.bgfinancas.principal.Main;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import io.github.badernageral.bgfinancas.principal.PrincipalControlador;

public final class Kernel {
    
    public static final String RAIZ = "/io/github/badernageral/bgfinancas";
    
    public static final String CSS_PRINCIPAL = RAIZ+"/recursos/css/principal.css";
    public static final String CSS_AJUDA = RAIZ+"/recursos/css/ajuda.css";
    public static final String CSS_TOOLTIP = RAIZ+"/recursos/css/tooltip.css";
    public static final String CSS_JANELA = RAIZ+"/recursos/css/janela.css";
    
    public static final String FXML_LOGIN = RAIZ+"/principal/Login.fxml";
    public static final String FXML_PRINCIPAL = RAIZ+"/principal/Principal.fxml";
    public static final String FXML_HOME = RAIZ+"/principal/Home.fxml";
    
    private static final String TITULO = Linguagem.getInstance().getMensagem("sistema");
    
    public static Main main;
    public static Stage palco;
    public static Stage palcoModal;
    public static Scene cena;
    public static Controlador controlador;
    public static PrincipalControlador principal;
    public static BorderPane layoutGeral;
    public static StackPane layoutCentro;
    
    private Kernel(){ }
    
    public static void setTitulo(String titulo){
        palco.setTitle(TITULO+" "+Configuracao.getPropriedade("versao")+" / "+titulo);
    }
    
}
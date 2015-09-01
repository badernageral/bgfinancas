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

import badernageral.bgfinancas.idioma.Linguagem;
import javafx.scene.control.ComboBox;

public final class Lista {
    
    private Lista(){ }
    
    private static final Linguagem idioma = Linguagem.getInstance();
    
    public static void setSimNao(ComboBox<String> combo){
        combo.setPromptText(idioma.getMensagem("selecione"));
        combo.getItems().add(idioma.getMensagem("sim"));
        combo.getItems().add(idioma.getMensagem("nao"));
    }
    
    public static String getSimNao(String valor) {
        String sim = idioma.getMensagem("sim");
        String nao = idioma.getMensagem("nao");
        if(valor.length()>1){
            return valor.equals(sim) ? "1" : "0";
        }else{
            return valor.equals("1") ? sim : nao;
        }
    }
    
    public static String getSimNao(Object valor) {
        if(valor!=null){
            return Lista.getSimNao(valor.toString());
        }else{
            return null;
        }
    }
    
    public static void setPoupancaCredito(ComboBox<String> combo){
        combo.setPromptText(idioma.getMensagem("selecione"));
        combo.getItems().add(idioma.getMensagem("poupanca"));
        combo.getItems().add(idioma.getMensagem("credito"));
    }

    public static String getPoupancaCredito(String valor) {
        String sim = idioma.getMensagem("poupanca");
        String nao = idioma.getMensagem("credito");
        if(valor.length()>1){
            return valor.equals(sim) ? "1" : "0";
        }else{
            return valor.equals("1") ? sim : nao;
        }
    }
    
    public static String getPoupancaCredito(Object valor) {
        if(valor!=null){
            return Lista.getPoupancaCredito(valor.toString());
        }else{
            return null;
        }
    }
    
}
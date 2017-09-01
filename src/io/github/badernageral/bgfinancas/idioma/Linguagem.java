/*
Copyright 2012-2017 Jose Robson Mariano Alves

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

package io.github.badernageral.bgfinancas.idioma;

import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.modelo.Configuracao;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Linguagem {
    
   private static final Linguagem instance = new Linguagem(); 
   private final List<String> listaIdiomas = Arrays.asList("Portugues Brasileiro","English");
   private final List<String> listaLocales = Arrays.asList("pt_BR","en_US");
   private ResourceBundle resourceBundle;
   private Locale locale;
   private String moeda;
  
   private Linguagem(){
        carregarIdioma();
   }
   
   public final void carregarIdioma(){
        Configuracao.verificar();
        try {
            String[] idioma = Configuracao.getPropriedade("idioma").split("_");
            locale = new Locale(idioma[0],idioma[1]);
        }catch (Exception ex) {
            Janela.showException(ex);
            locale = new Locale("en","US");
        }
        resourceBundle = ResourceBundle.getBundle("io/github/badernageral/bgfinancas/idioma/Linguagem", locale);
        moeda = Configuracao.getPropriedade("moeda");
   }
  
   public String getMensagem(String mensagem){  
        if(mensagem.equals("moeda")){
            return moeda;
        }else{
            try{
                return resourceBundle.getString(mensagem);
            }catch(MissingResourceException ex){
                Janela.showException(ex);
                return "?????";
            }
        }
   }
   
   public ObservableList<String> getListaIdiomas(){
        ObservableList<String> idiomas = FXCollections.observableList(listaIdiomas);
        return idiomas;
   }
   
   public String idiomaToLocale(String idioma){
       return listaLocales.get(listaIdiomas.indexOf(idioma));
   }
   
   public String locateToIdioma(String locale){
       return listaIdiomas.get(listaLocales.indexOf(locale));
   }
   
   public String getIdiomaSistema(){
       String idioma = Configuracao.getPropriedade("idioma");
       return locateToIdioma(idioma);
   }
   
   public static Linguagem getInstance(){  
        return instance;  
   }
   
   public String getNomeMes(int mes){
        switch(mes){
            case 1:
                return getMensagem("janeiro");
            case 2:
                return getMensagem("fevereiro");
            case 3:
                return getMensagem("marco");
            case 4:
                return getMensagem("abril");
            case 5:
                return getMensagem("maio");
            case 6:
                return getMensagem("junho");
            case 7:
                return getMensagem("julho");
            case 8:
                return getMensagem("agosto");
            case 9:
                return getMensagem("setembro");
            case 10:
                return getMensagem("outubro");
            case 11:
                return getMensagem("novembro");
            case 12:
                return getMensagem("dezembro");
            default:
                return getMensagem("inexistente");
        }
    }
   
}

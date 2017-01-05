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

package badernageral.bgfinancas.biblioteca.sistema;

import java.util.ArrayList;
import java.util.List;

public final class PilhaVoltar {
    
    private static List<String> acaoVoltar = new ArrayList<>();
    
    private PilhaVoltar(){ }
    
    public static void adicionar(String arquivoFXML){
        if(arquivoFXML.equals(Kernel.FXML_HOME)){
            acaoVoltar = new ArrayList<>();
        }else{
            if(acaoVoltar.size()>0){
                String voltar = acaoVoltar.get(acaoVoltar.size()-1);
                if(!voltar.equals(arquivoFXML)){
                    acaoVoltar.add(arquivoFXML);
                }
            }else{
                if(!arquivoFXML.equals(Kernel.FXML_HOME)){
                    acaoVoltar.add(arquivoFXML);
                }
            }
        }
    }
    
    public static String voltar(){        
        if(acaoVoltar.size()>0){
            acaoVoltar.remove(acaoVoltar.size()-1);
            if(acaoVoltar.size()>0){
                return acaoVoltar.get(acaoVoltar.size()-1);
            }else{
                return Kernel.FXML_HOME;
            }
        }else{
            return Kernel.FXML_HOME;
        }
    }
    
}
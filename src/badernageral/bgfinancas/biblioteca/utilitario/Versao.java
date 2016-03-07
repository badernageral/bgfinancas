/*
Copyright 2012-2015 Jose Robson Mariano Alves

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

public final class Versao {
    
    private Versao(){ }
    
    public static Boolean isAtualizada(String sistema, String atual){
        String[] vSistema = sistema.split("\\.");
        String[] vAtual = atual.split("\\.");
        int i = 0;
        while (i < vSistema.length && i < vAtual.length && vSistema[i].equals(vAtual[i])){
          i++;
        }
        if (i < vSistema.length && i < vAtual.length){
            return Integer.parseInt(vSistema[i])>=Integer.parseInt(vAtual[i]);
        }else{
            return vSistema.length>=vAtual.length;
        }
    }
    
}

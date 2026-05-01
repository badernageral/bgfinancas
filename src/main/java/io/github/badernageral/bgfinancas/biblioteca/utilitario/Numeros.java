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

package io.github.badernageral.bgfinancas.biblioteca.utilitario;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Numeros {
    
    private Numeros(){ }
    
    public static String arredondar(String numero){
        return Numeros.arredondar(new BigDecimal(numero));
    }
    
    public static String arredondar(Double numero){
        return Numeros.arredondar(new BigDecimal(numero));
    }
    
    public static String arredondar(BigDecimal numero){
        return numero.setScale(2, RoundingMode.HALF_UP).toString();
    }
    
    public static boolean isNumero(String numero){
        try{
            BigDecimal valor = new BigDecimal(numero);
        }catch(Exception ex){
            return false;
        }
        return true;
    }
    
}

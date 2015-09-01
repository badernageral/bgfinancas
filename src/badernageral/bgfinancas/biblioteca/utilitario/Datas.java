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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public final class Datas {
    
    private Datas(){ }
    
    public static String getDataSqlHoje(){
        return Datas.toSqlData(LocalDate.now());
    }
    
    public static String toSqlData(LocalDate data){
        if(data != null){
            String mes = String.format("%02d", data.getMonthValue());
            String dia = String.format("%02d", data.getDayOfMonth());
            return data.getYear()+"-"+mes+"-"+dia;
        }else{
            return "";
        }
    }
    
    public static String getDataExibicao(String data){
        LocalDate dataLocal = Datas.getLocalDate(data);
        return dataLocal.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    }
    
    public static LocalDate getLocalDate(String data){
        String[] arrData = data.split("-");
        LocalDate dataFinal = LocalDate.of(Integer.parseInt(arrData[0]), Integer.parseInt(arrData[1]), Integer.parseInt(arrData[2]));
        return dataFinal;
    }
    
    public static String getHoraAtual(){
        LocalTime agora = LocalTime.now();
        return agora.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }
    
}

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

package badernageral.bgfinancas.biblioteca.utilitario;

import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.idioma.Linguagem;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    
    public static LocalDate getLocalDate(String data){
        try{
            return LocalDate.parse(data);
        }catch(Exception ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public static LocalTime getLocalTime(String hora){
        try{
            if(hora.length()>7){
                return LocalTime.parse(hora);
            }else{
                return LocalTime.parse("0"+hora);
            }
        }catch(Exception ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public static LocalDateTime getLocalDateTime(LocalDate data, LocalTime hora) {
        try{
            return LocalDateTime.of(data, hora);
        }catch(Exception ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public static LocalDate getLocalDate(int dia, int mes, int ano){
        LocalDate data = LocalDate.of(ano, mes, 1);
        try{
            data = data.withDayOfMonth(dia);
        }catch(DateTimeException ex){
            Janela.showMensagem(Status.ERRO, Linguagem.getInstance().getMensagem("dia_mes_invalido"));
            data = data.withDayOfMonth(dia-3);
        }
        return data;
    }
    
    public static String getHoraAtual(){
        LocalTime agora = LocalTime.now();
        return agora.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }
    
}

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
package io.github.badernageral.bgfinancas.modelo;

import io.github.badernageral.bgfinancas.biblioteca.utilitario.Datas;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Extrato {
    
    private String tipo;
    private String data;
    private String hora;
    private String nomeCategoria;
    private String nomeItem;
    private String valor;
    private String status;
    
    public Extrato(String tipo, String data, String hora, String nomeCategoria, String nomeItem, String valor, String status){
        this.tipo = tipo;
        this.data = data;
        this.hora = hora;
        this.nomeCategoria = nomeCategoria;
        this.nomeItem = nomeItem;
        this.valor = valor;
        this.status = status;
    }
    
    public Extrato(String tipo, String data, String hora, String nomeCategoria, String nomeItem, String valor){
        this(tipo,data,hora,nomeCategoria,nomeItem,valor,Linguagem.getInstance().getMensagem("efetuado"));
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public LocalDate getData() {
        return Datas.getLocalDate(data);
    }

    public LocalTime getHora() {
        return Datas.getLocalTime(hora);
    }
    
    public LocalDateTime getDataHora() {
        return Datas.getLocalDateTime(getData(), getHora());
    }
    
    public String getNomeCategoria() {
        return nomeCategoria;
    }
    
    public String getNomeItem() {
        return nomeItem;
    }
    
    public BigDecimal getValor() {
        return new BigDecimal(valor);
    }
    
    public String getStatus() {
        return status;
    }
    
}

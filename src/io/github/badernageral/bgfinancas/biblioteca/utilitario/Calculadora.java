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

package io.github.badernageral.bgfinancas.biblioteca.utilitario;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public final class Calculadora {

    private Calculadora() {}

    public static void preparar(TextField campo, Label imagem) {
        campo.getStyleClass().add("calculadora");
        Ajuda.estilizarBotaoDica(campo, imagem, Linguagem.getInstance().getMensagem("tuto_calculadora"), Duracao.MUITO_LONGA);
    }
    
    public static void calcular(TextField campo) throws Erro{
        Validar.textField(campo);
        if (campo.getText().contains(",")) {
            Janela.showTooltip(Status.ADVERTENCIA, Linguagem.getInstance().getMensagem("use_ponto_decimais"), campo, Duracao.NORMAL);
        } else {
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine engine = sem.getEngineByName("JavaScript");
            String texto = campo.getText();
            try {
                campo.setText(Numeros.arredondar(engine.eval(texto).toString()));
            } catch (Exception ex) {
                Janela.showTooltip(Status.ADVERTENCIA, Linguagem.getInstance().getMensagem("operacao_matematica_invalida"), campo, Duracao.NORMAL);
            }
        }
        throw new Erro();
    }

}

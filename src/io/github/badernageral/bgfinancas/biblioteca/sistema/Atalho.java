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

package io.github.badernageral.bgfinancas.biblioteca.sistema;

import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public final class Atalho {
    
    private Atalho(){ }
    
    public static void setAtalhos(){
        ObservableMap<KeyCombination, Runnable> aceleradores = Kernel.cena.getAccelerators();
        aceleradores.put(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN), (Runnable) () -> {
            try {
                Kernel.controlador.acaoCadastrar(1);
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
        aceleradores.put(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), (Runnable) () -> {
            try {
                Kernel.controlador.acaoCadastrar(2);
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
        aceleradores.put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), (Runnable) () -> {
            try {
                Kernel.controlador.acaoAlterar(1);
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
        aceleradores.put(new KeyCodeCombination(KeyCode.DELETE), (Runnable) () -> {
            try {
                Kernel.controlador.acaoExcluir(1);
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
        aceleradores.put(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN), (Runnable) () -> {
            try {
                Kernel.controlador.acaoGerenciar(1);
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
        aceleradores.put(new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.ALT_DOWN), (Runnable) () -> {
            try {
                Kernel.controlador.acaoVoltar();
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
    }
    
    public static void setAtalhosModal(ControladorFormulario controlador, Scene cena){
        cena.getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), (Runnable) () -> {
            try {
                controlador.acaoFinalizar();
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
        cena.getAccelerators().put(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN), (Runnable) () -> {
            try {
                controlador.acaoCadastrar(1);
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
        cena.getAccelerators().put(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), (Runnable) () -> {
            try {
                controlador.acaoCadastrar(2);
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
        cena.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), (Runnable) () -> {
            try {
                controlador.acaoCancelar();
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        });
    }
    
}
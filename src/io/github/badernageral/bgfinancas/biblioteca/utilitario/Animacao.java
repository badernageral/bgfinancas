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

import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public final class Animacao {
    
    private Animacao(){ }
    
    private static final double DURACAO = 250;
    private static final double INVISIVEL = 0.25;
    private static final double TRANSPARENTE = 0.3;
    private static final double VISIVEL = 1.0;
    
    private static FadeTransition fadeIn(Node node, Boolean invisivel, Boolean play){
        FadeTransition fadeIn = new FadeTransition(Duration.millis(DURACAO), node);
        if(invisivel){
            fadeIn.setFromValue(INVISIVEL);
        }else{
            fadeIn.setFromValue(TRANSPARENTE);
        }
        fadeIn.setToValue(VISIVEL);
        if(play){
            fadeIn.play();
        }
        return fadeIn;
    }
    
    private static FadeTransition fadeOut(Node node, Boolean invisivel, Boolean play){
        FadeTransition fadeOut = new FadeTransition(Duration.millis(DURACAO), node);
        fadeOut.setFromValue(VISIVEL);
        if(invisivel){
            fadeOut.setToValue(INVISIVEL);
        }else{
            fadeOut.setToValue(TRANSPARENTE);
        }
        if(play){
            fadeOut.play();
        }
        return fadeOut;
    }
    
    private static FadeTransition fadeOutReplace(StackPane painel, Node node){
        FadeTransition fadeOut = Animacao.fadeOut(painel, false, false);
        fadeOut.setOnFinished(actionEvent -> painel.getChildren().setAll(node));
        return fadeOut;
    }
    
    private static void fadeTransicao(FadeTransition fadeOut, FadeTransition fadeIn){
        SequentialTransition transicao = new SequentialTransition(fadeOut, fadeIn);
        transicao.play();
    }
    
    public static void fadeInOutClose(Node node){
        FadeTransition fadeIn = Animacao.fadeIn(Kernel.layoutGeral, false, false);
        FadeTransition fadeOut = Animacao.fadeOut(node, false, false);
        fadeOut.setOnFinished(actionEvent -> node.getScene().getWindow().hide());
        fadeOut.play();
        fadeIn.play();
    }
    
    public static void fadeOutClose(Node node){
        FadeTransition fadeOut = Animacao.fadeOut(node, false, false);
        fadeOut.setOnFinished(actionEvent -> node.getScene().getWindow().hide());
        fadeOut.play();
    }
    
    public static void fadeOutIn(Node node){
        Animacao.fadeOut(node, false, true);
        Animacao.fadeIn(node, false, true);
    }
    
    public static void fadeOutIn(Node node_out, Node node_in){
        Animacao.fadeOut(node_out, false, true);
        Animacao.fadeIn(node_in, false, true);
    }
    
    public static void fadeOutInReplace(StackPane painel, Node node){
        FadeTransition fadeOut = Animacao.fadeOutReplace(painel, node);
        FadeTransition fadeIn = Animacao.fadeIn(painel, false, false);
        Animacao.fadeTransicao(fadeOut, fadeIn);
    }
    
    public static void fadeInInvisivel(Node node_foco, Node node_formulario){
        node_foco.requestFocus();
        Animacao.fadeIn(node_formulario, true, true);
        node_formulario.getScene().getWindow().setOpacity(1);
    }
    
    public static void fadeOutInvisivel(Node node_foco, Node node_formulario){
        Animacao.fadeOut(node_formulario, true, true);
        node_formulario.getScene().getWindow().setOpacity(0);
        node_foco.requestFocus();
    }
    
    public static void fadeOutMultiplo(List<Node> nodes){
        nodes.stream().forEach(node -> {
            if(node.getOpacity()>INVISIVEL){
                Animacao.fadeOut(node, true, true);
            }
        });
    }
    
    public static void fadeInMultiplo(List<Node> nodes){
        nodes.stream().forEach(node -> {
            if(node.getOpacity()<VISIVEL){
                Animacao.fadeIn(node, true, true);
            }
        });
    }
    
}

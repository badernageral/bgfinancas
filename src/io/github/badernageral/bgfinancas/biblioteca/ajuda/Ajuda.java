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

package io.github.badernageral.bgfinancas.biblioteca.ajuda;

import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Posicao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.template.barra.BarraPadrao;
import io.github.badernageral.bgfinancas.template.cena.CenaItem;
import io.github.badernageral.bgfinancas.template.cena.CenaMovimento;
import io.github.badernageral.bgfinancas.template.cena.CenaPadrao;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.stage.Window;

public final class Ajuda {
    
    private static final Ajuda instance = new Ajuda();
    private final List<Node> objetos = new ArrayList<>();
    private final List<Capitulo> capitulos = new ArrayList<>();
    private final Tooltip mensagem = new Tooltip();
    private int capituloAtual = -1;
    private ProgressIndicator progresso;
    
    private final Font fonte = Font.loadFont(getClass().getResource(Kernel.RAIZ+"/recursos/fonte/Ubuntu-L.ttf").toExternalForm(), 14);
    
    private Ajuda(){
        mensagem.getStyleClass().add("mensagemAjuda");
        mensagem.setWrapText(true);
    }
    
    public void setBotaoProgresso(ProgressIndicator progresso){
        this.progresso = progresso;
    }
    
    public void setObjetos(Node... nodes){
        objetos.addAll(Arrays.asList(nodes));
    }
    
    public void setObjetos(BarraPadrao barra, Node... nodes){
        objetos.addAll(Arrays.asList(
            barra.getBotaoVoltar(),
            barra.getLabelTitulo(),
            barra.getBotaoCadastrar(),
            barra.getBotaoAlterar(),
            barra.getBotaoExcluir(),
            barra.getFiltro()
        ));
        objetos.addAll(Arrays.asList(nodes));
    }
    
    public void setObjetos(CenaPadrao cena, Node tabela){
        objetos.addAll(Arrays.asList(
            cena.getBotaoVoltar(),
            cena.getLabelTitulo(),
            cena.getBotaoCadastrar(),
            cena.getBotaoAlterar(),
            cena.getBotaoExcluir(),
            cena.getFiltro(),
            tabela
        ));
    }
    
    public void setObjetos(CenaItem cena, Node tabela){
        objetos.addAll(Arrays.asList(
            cena.getBotaoVoltar(),
            cena.getLabelTitulo(),
            cena.getBotaoCadastrar(),
            cena.getBotaoAlterar(),
            cena.getBotaoExcluir(),
            cena.getLabelCategoria(),
            cena.getChoiceCategoria(),
            cena.getBotaoGerenciarCategoria(),
            cena.getFiltro(),
            tabela
        ));
    }
    
    public void setObjetos(CenaMovimento cena, Node... nodes){
        objetos.addAll(Arrays.asList(
            cena.getBotaoVoltar(),
            cena.getLabelTitulo(),
            cena.getBotaoCadastrar(),
            cena.getBotaoAlterar(),
            cena.getBotaoExcluir(),
            cena.getLabelCategoria(),
            cena.getChoiceCategoria(),
            cena.getLabelItem(),
            cena.getBotaoGerenciarItem(),
            cena.getBotaoGerenciarCategoria(),
            cena.getFiltro(),
            cena.getGridPaneContas()
        ));
        objetos.addAll(Arrays.asList(nodes));
    }
    
    public void capitulo(Posicao posicao, String mensagem){
        capitulos.add(new Capitulo(posicao, mensagem));
    }
    
    public void capitulo(Node node, Posicao posicao, String mensagem){
        capitulos.add(new Capitulo(node, posicao, mensagem));
    }
    
    public void capitulo(List<Node> nodes, Posicao posicao, String mensagem){
        capitulos.add(new Capitulo(nodes, posicao, mensagem));
    }
    
    public void apresentarProximo(){
        capituloAtual++;
        apresentar();
    }
    
    public void apresentarAnterior(){
        capituloAtual--;
        apresentar();
    }
    
    private void updateProgresso(){
        if(progresso != null){
            double porcentagem;
            int qtd = capitulos.size();
            if(capituloAtual==(qtd-1)){
                porcentagem = 100;
            }else{
                porcentagem = (100/qtd)*(capituloAtual+1);
            }
            progresso.setProgress(porcentagem/100);
        }
    }
    
    private void apresentar(){
        try{
            updateProgresso();
            Capitulo c = capitulos.get(capituloAtual);
            Animacao.fadeOutMultiplo(objetos.stream().filter(n -> !n.equals(c.getNode())).collect(Collectors.toList()));
            Scene cena;
            Window janela;
            double largura, x, y;
            if(c.getNode()!=null){
                Animacao.fadeInMultiplo(Arrays.asList(c.getNode()));
                cena = c.getNode().getScene();
                janela = c.getNode().getScene().getWindow();
                Point2D coordenada = c.getNode().localToScene(0.0, 0.0);
                x = coordenada.getX() + cena.getX() + janela.getX();
                y = coordenada.getY() + cena.getY() + janela.getY();
                largura = c.getNode().getBoundsInLocal().getWidth();
                largura = (largura<350) ? 350 : largura;
                if(c.getPosicao()==Posicao.BAIXO){
                    y += c.getNode().getBoundsInLocal().getHeight()+8;
                }else if(c.getPosicao()==Posicao.TOPO){
                    FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(fonte);
                    double linhas = fm.computeStringWidth(c.getMensagem())/largura;
                    y -= ((Math.ceil(linhas)*15)+30);
                }else{ // CENTRO
                    double centroX = c.getNode().getBoundsInLocal().getWidth()/2;
                    double centroY = c.getNode().getBoundsInLocal().getHeight()/2;
                    largura = 400;
                    x = (x + centroX) - (largura/2);
                    y = y + centroY;
                }
            }else{
                if(c.getNodes()!=null){
                    Animacao.fadeInMultiplo(c.getNodes());
                }
                Node node = Kernel.layoutCentro;
                janela = node.getScene().getWindow();
                cena = node.getScene();
                if(c.getPosicao()==Posicao.TOPO){
                    largura = node.getBoundsInLocal().getWidth();
                    Point2D coordenada = node.localToScene(0.0, 0.0);
                    x = coordenada.getX() + cena.getX() + janela.getX();
                    y = coordenada.getY() + cena.getY() + janela.getY();
                }else if(c.getPosicao()==Posicao.BAIXO){
                    largura = node.getBoundsInLocal().getWidth();
                    Point2D coordenada = node.localToScene(0.0, 0.0);
                    x = coordenada.getX() + cena.getX() + janela.getX();
                    y = coordenada.getY() + cena.getY() + janela.getY() + node.getBoundsInLocal().getHeight();
                    FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(fonte);
                    double linhas = fm.computeStringWidth(c.getMensagem())/largura;
                    y -= ((Math.ceil(linhas)*15)+15);
                }else{ // CENTRO
                    double centroX = node.getBoundsInLocal().getWidth()/2;
                    double centroY = node.getBoundsInLocal().getHeight()/2;
                    largura = 400;
                    x = (cena.getX() + janela.getX() + centroX) - (largura/2);
                    y = cena.getY() + janela.getY() + centroY;
                }
            }
            updateMensagem(c.getMensagem(), largura, x, y);
            mensagem.show(janela);
        }catch(IndexOutOfBoundsException ex){
            Kernel.principal.desativarAjuda();
        }
    }
    
    public void updateMensagem(String texto, double largura, double x, double y){
        mensagem.setText(texto);
        mensagem.setMinWidth(largura);
        mensagem.setMaxWidth(largura);
        mensagem.setX(x);
        mensagem.setY(y);
    }
    
    public void desativarAjuda(){
        Animacao.fadeInMultiplo(objetos.stream().filter(n -> n.getOpacity()<1).collect(Collectors.toList()));
        mensagem.hide();
        objetos.clear();
        capitulos.clear();
        capituloAtual = -1;
    }
    
    public static Ajuda getInstance(){
        return instance;  
    }
    
    public static void estilizarBotaoDica(Control campo, Label ajuda, String dica, Duracao duracao){
        ajuda.getStyleClass().add("labelAjuda");
        ajuda.setPrefWidth(24);
        ajuda.setPrefHeight(24);
        ajuda.setOnMouseClicked(e -> { 
            Janela.showTooltip(Status.ADVERTENCIA, dica, campo, duracao);
        });
    }
    
}
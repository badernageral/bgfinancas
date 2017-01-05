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

import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.idioma.Linguagem;
import badernageral.bgfinancas.modulo.despesa.DespesaCadastroMultiploControlador;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import badernageral.bgfinancas.template.botao.BotaoFormulario;
import badernageral.bgfinancas.template.botao.BotaoListaCategoria;
import badernageral.bgfinancas.template.botao.BotaoListaItem;

public final class Botao {
    
    private static final Linguagem idioma = Linguagem.getInstance();
    
    private Botao(){ }
    
    private static void prepararBotaoCadastrar(Button[] cadastrar){
        if(cadastrar != null){
            for (int i=0; i<cadastrar.length; i++) {
                if(i==0){
                    cadastrar[i].setTooltip(new Tooltip(idioma.getMensagem("cadastrar")+" (CTRL+I)"));
                }else if(i==2){
                    cadastrar[i].setTooltip(new Tooltip(idioma.getMensagem("cadastrar")+" (CTRL+SHIFT+I)"));
                }else{
                    cadastrar[i].setTooltip(new Tooltip(idioma.getMensagem("cadastrar")));
                }
                final int botao = i+1;
                cadastrar[i].setOnAction((ActionEvent e) -> {
                    Kernel.controlador.acaoCadastrar(botao);
                });
            }
        }
    }
    
    public static void prepararBotaoCadastrar(Button cadastrar, int botao){
        if(cadastrar != null){
            if(botao==1){
                cadastrar.setTooltip(new Tooltip(idioma.getMensagem("cadastrar")+" (CTRL+I)"));
            }else if(botao==2){
                cadastrar.setTooltip(new Tooltip(idioma.getMensagem("cadastrar")+" (CTRL+SHIFT+I)"));
            }else{
                cadastrar.setTooltip(new Tooltip(idioma.getMensagem("cadastrar")));
            }
            cadastrar.setOnAction((ActionEvent e) -> {
                Kernel.controlador.acaoCadastrar(botao);
            });
        }
    }
    
    private static void prepararBotaoAlterar(Button alterar){
        if(alterar != null){
            alterar.setTooltip(new Tooltip(idioma.getMensagem("alterar")+" (CTRL+E)"));
            alterar.setOnAction((ActionEvent e) -> {
                Kernel.controlador.acaoAlterar(1);
            });
        }
    }
    
    public static void prepararBotaoExcluir(Button excluir, int botao){
        if(excluir != null){
            excluir.setTooltip(new Tooltip(idioma.getMensagem("excluir")+" (DELETE)"));
            excluir.setOnAction((ActionEvent e) -> {
                Kernel.controlador.acaoExcluir(botao);
            });
        }
    }
    
    public static void prepararBotaoVoltar(Button voltar){
        if(voltar != null){
            voltar.setTooltip(new Tooltip(idioma.getMensagem("voltar")+" (ALT+BACKSPACE)"));
            voltar.setOnAction((ActionEvent e) -> {
                Kernel.controlador.acaoVoltar();
            });
        }
    }
    
    public static void prepararBotaoGerenciar(Button[] gerenciar){
        if(gerenciar != null){
            for (int i=0; i<gerenciar.length; i++) {
                if(gerenciar[i]!=null){
                    if(i==0){
                        gerenciar[i].setTooltip(new Tooltip(idioma.getMensagem("gerenciar")+" (CTRL+M)"));
                    }else{
                        gerenciar[i].setTooltip(new Tooltip(idioma.getMensagem("gerenciar")));
                    }
                    final int botao = i+1;
                    gerenciar[i].setOnAction((ActionEvent e) -> {
                        Kernel.controlador.acaoGerenciar(botao);
                    });
                }
            }
        }
    }
    
    public static void prepararBotaoGerenciar(int indice, Button gerenciar, String descricao){
        if(gerenciar != null){
            gerenciar.setTooltip(new Tooltip(descricao));
            gerenciar.setOnAction((ActionEvent e) -> {
                Kernel.controlador.acaoGerenciar(indice+1);
            });
        }
    }
    
    public static void prepararBotaoFinalizar(Button finalizar, DespesaCadastroMultiploControlador controlador){
        if(finalizar != null){
            finalizar.setTooltip(new Tooltip("(ENTER)"));
            finalizar.setOnAction((ActionEvent e) -> {
                controlador.acaoFinalizar();
            });
        }
    }
    
    public static void prepararBotao(Button[] cadastrar, Button alterar, Button excluir, Button voltar){
        Botao.prepararBotaoCadastrar(cadastrar);
        Botao.prepararBotaoAlterar(alterar);
        Botao.prepararBotaoExcluir(excluir, 1);
        Botao.prepararBotaoVoltar(voltar);
    }
    
    public static void prepararBotaoModal(ControladorFormulario controlador, Button finalizar, Button cancelar, Button[] cadastrar){
        if(finalizar != null){
            finalizar.setTooltip(new Tooltip("(ENTER)"));
            finalizar.setOnAction((ActionEvent e) -> {
                controlador.acaoFinalizar();
            });
        }
        if(cancelar != null){
            cancelar.setText(idioma.getMensagem("cancelar"));
            cancelar.setTooltip(new Tooltip("(ESCAPE)"));
            cancelar.setOnAction((ActionEvent e) -> {
                controlador.acaoCancelar();
            });
        }
        if(cadastrar != null){
            for (int i=0; i<cadastrar.length; i++) {
                if(i==0){
                    cadastrar[i].setTooltip(new Tooltip(idioma.getMensagem("cadastrar")+" (CTRL+I)"));
                }else if(i==1){
                    cadastrar[i].setTooltip(new Tooltip(idioma.getMensagem("cadastrar")+" (CTRL+SHIFT+I)"));
                }else{
                    cadastrar[i].setTooltip(new Tooltip(idioma.getMensagem("cadastrar")));
                }
                final int botao = i+1;
                cadastrar[i].setOnAction((ActionEvent e) -> {
                    controlador.acaoCadastrar(botao);
                });
            }
        }
    }
    
    public static void prepararBotaoModal(ControladorFormulario controlador, BotaoFormulario botaoController){
        Botao.prepararBotaoModal(controlador, botaoController.getBotaoFinalizar(), botaoController.getBotaoCancelar(), null);
    }
    
    public static void prepararBotaoModal(ControladorFormulario controlador, BotaoFormulario botaoController, BotaoListaCategoria categoriaController){
        Botao.prepararBotaoModal(controlador, botaoController.getBotaoFinalizar(), botaoController.getBotaoCancelar(), new Button[]{categoriaController.getBotaoCadastrar()});
    }
    
    public static void prepararBotaoModal(ControladorFormulario controlador, BotaoFormulario botaoController, BotaoListaItem itemController){
        Botao.prepararBotaoModal(controlador, botaoController.getBotaoFinalizar(), botaoController.getBotaoCancelar(), new Button[]{itemController.getBotaoCadastrar()});
    }
    
    public static void prepararBotaoModal(ControladorFormulario controlador, BotaoFormulario botaoController, BotaoListaCategoria categoriaController, BotaoListaCategoria categoriaSecundariaController){
        Botao.prepararBotaoModal(controlador, botaoController.getBotaoFinalizar(), botaoController.getBotaoCancelar(), new Button[]{categoriaController.getBotaoCadastrar(), categoriaSecundariaController.getBotaoCadastrar()});
    }
    
    public static void prepararBotaoModal(ControladorFormulario controlador, BotaoFormulario botaoController, Button botaoCadastrar, BotaoListaCategoria categoriaController){
        Botao.prepararBotaoModal(controlador, botaoController.getBotaoFinalizar(), botaoController.getBotaoCancelar(), new Button[]{botaoCadastrar, categoriaController.getBotaoCadastrar()});
    }
    
    public static void prepararBotaoModal(ControladorFormulario controlador, BotaoFormulario botaoController, BotaoListaItem itemController, BotaoListaCategoria categoriaController){
        Botao.prepararBotaoModal(controlador, botaoController.getBotaoFinalizar(), botaoController.getBotaoCancelar(), new Button[]{itemController.getBotaoCadastrar(), categoriaController.getBotaoCadastrar()});
    }
    
}
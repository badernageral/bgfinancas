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
package io.github.badernageral.bgfinancas.principal;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Atualizacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.PilhaVoltar;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Datas;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Agenda;
import io.github.badernageral.bgfinancas.modelo.AgendaTipo;
import io.github.badernageral.bgfinancas.modelo.CartaoCredito;
import io.github.badernageral.bgfinancas.modelo.Configuracao;
import io.github.badernageral.bgfinancas.modelo.Conta;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import io.github.badernageral.bgfinancas.modelo.DespesaCategoria;
import io.github.badernageral.bgfinancas.modelo.DespesaItem;
import io.github.badernageral.bgfinancas.modelo.GrupoItem;
import io.github.badernageral.bgfinancas.modelo.Receita;
import io.github.badernageral.bgfinancas.modelo.ReceitaCategoria;
import io.github.badernageral.bgfinancas.modelo.ReceitaItem;
import io.github.badernageral.bgfinancas.modelo.Relatorio;
import io.github.badernageral.bgfinancas.modelo.Transferencia;
import io.github.badernageral.bgfinancas.modelo.TransferenciaCategoria;
import io.github.badernageral.bgfinancas.modelo.TransferenciaItem;
import io.github.badernageral.bgfinancas.modelo.Usuario;
import io.github.badernageral.bgfinancas.modelo.Utilitario;
import io.github.badernageral.bgfinancas.modulo.despesa.DespesasAgendadasControlador;
import io.github.badernageral.bgfinancas.modulo.utilitario.Backup;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class PrincipalControlador implements Initializable {

    private final Linguagem idioma = Linguagem.getInstance();

    private boolean ajuda = false;

    @FXML
    private BorderPane layoutGeral;
    @FXML
    private StackPane layoutCentro;

    @FXML
    private Menu menuGerenciar;
    @FXML
    private Menu menuGerenciarCategorias;
    @FXML
    private Menu menuGerenciarItens;
    @FXML
    private Menu menuMovimentar;
    @FXML
    private Menu menuUtilitarios;
    @FXML
    private Menu menuAjuda;
    @FXML
    private MenuItem gerenciarContas;
    @FXML
    private MenuItem gerenciarAgenda;
    @FXML
    private MenuItem gerenciarGrupos;
    @FXML
    private MenuItem gerenciarCartoesCredito;
    @FXML
    private MenuItem gerenciarDespesasAgendadas;
    @FXML
    private MenuItem gerenciarUsuarios;
    @FXML
    private MenuItem gerenciarCategoriasDespesas;
    @FXML
    private MenuItem gerenciarCategoriasReceitas;
    @FXML
    private MenuItem gerenciarCategoriasTransferencias;
    @FXML
    private MenuItem gerenciarCategoriasAgenda;
    @FXML
    private MenuItem gerenciarItensDespesas;
    @FXML
    private MenuItem gerenciarItensReceitas;
    @FXML
    private MenuItem gerenciarItensTransferencias;
    @FXML
    private MenuItem movimentarDespesa;
    @FXML
    private MenuItem movimentarReceita;
    @FXML
    private MenuItem movimentarTransferencia;
    @FXML
    private MenuItem movimentarGuia;
    @FXML
    private MenuItem utilitariosConfiguracoes;
    @FXML
    private MenuItem utilitariosRelatorios;
    @FXML
    private MenuItem utilitariosExportarBackup;
    @FXML
    private MenuItem utilitariosImportarBackup;
    @FXML
    private MenuItem ajudaTutorial;
    @FXML
    private MenuItem ajudaSobreSistema;
    @FXML
    private MenuItem ajudaVerificarAtualizacoes;

    @FXML
    private Button botaoHome;
    @FXML
    private Button botaoDespesas;
    @FXML
    private Button botaoReceitas;
    @FXML
    private Button botaoTransferencias;
    @FXML
    private Button botaoDespesasAgendadas;
    @FXML
    private Button botaoAgenda;
    @FXML
    private Button botaoGrupo;
    @FXML
    private Button botaoCartaoCredito;
    @FXML
    private Button botaoContas;
    @FXML
    private Button botaoUsuarios;
    @FXML
    private Button botaoRelatorios;
    @FXML
    private Button botaoCalculadora;
    @FXML
    private Button botaoGuia;

    @FXML
    private Button botaoAjuda;
    @FXML
    private Button botaoAjudaProximo;
    @FXML
    private Button botaoAjudaAnterior;
    @FXML
    private ProgressIndicator ajudaProgresso;

    private void traduzirMenu() {
        menuGerenciar.setText(idioma.getMensagem("gerenciar"));
        menuGerenciarCategorias.setText(idioma.getMensagem("categorias"));
        menuGerenciarItens.setText(idioma.getMensagem("itens"));
        menuMovimentar.setText(idioma.getMensagem("movimentar"));
        menuUtilitarios.setText(idioma.getMensagem("utilitarios"));
        menuAjuda.setText(idioma.getMensagem("ajuda"));
        gerenciarContas.setText(idioma.getMensagem("contas"));
        gerenciarAgenda.setText(idioma.getMensagem("lembretes"));
        gerenciarGrupos.setText(idioma.getMensagem("cotas_despesas"));
        gerenciarDespesasAgendadas.setText(idioma.getMensagem("despesas_agendadas"));
        gerenciarCartoesCredito.setText(idioma.getMensagem("cartoes_credito"));
        gerenciarUsuarios.setText(idioma.getMensagem("usuarios"));
        gerenciarCategoriasDespesas.setText(idioma.getMensagem("despesas"));
        gerenciarCategoriasReceitas.setText(idioma.getMensagem("receitas"));
        gerenciarCategoriasTransferencias.setText(idioma.getMensagem("transferencias"));
        gerenciarCategoriasAgenda.setText(idioma.getMensagem("lembretes"));
        gerenciarItensDespesas.setText(idioma.getMensagem("despesas"));
        gerenciarItensReceitas.setText(idioma.getMensagem("receitas"));
        gerenciarItensTransferencias.setText(idioma.getMensagem("transferencias"));
        movimentarDespesa.setText(idioma.getMensagem("despesas"));
        movimentarReceita.setText(idioma.getMensagem("receitas"));
        movimentarTransferencia.setText(idioma.getMensagem("transferencias"));
        movimentarGuia.setText(idioma.getMensagem("guia"));
        utilitariosConfiguracoes.setText(idioma.getMensagem("configuracoes"));
        utilitariosRelatorios.setText(idioma.getMensagem("relatorios"));
        utilitariosExportarBackup.setText(idioma.getMensagem("exportar_backup"));
        utilitariosImportarBackup.setText(idioma.getMensagem("importar_backup"));
        ajudaTutorial.setText(idioma.getMensagem("tutorial"));
        ajudaSobreSistema.setText(idioma.getMensagem("sobre_sistema"));
        ajudaVerificarAtualizacoes.setText(idioma.getMensagem("verificar_atualizacoes"));
    }

    private void tooltipBotoes() {
        botaoHome.setTooltip(new Tooltip(idioma.getMensagem("principal")));
        botaoDespesas.setTooltip(new Tooltip(idioma.getMensagem("despesas")));
        botaoReceitas.setTooltip(new Tooltip(idioma.getMensagem("receitas")));
        botaoTransferencias.setTooltip(new Tooltip(idioma.getMensagem("transferencias")));
        botaoDespesasAgendadas.setTooltip(new Tooltip(idioma.getMensagem("despesas_agendadas")));
        botaoAgenda.setTooltip(new Tooltip(idioma.getMensagem("lembretes")));
        botaoGrupo.setTooltip(new Tooltip(idioma.getMensagem("cotas_despesas")));
        botaoCartaoCredito.setTooltip(new Tooltip(idioma.getMensagem("cartoes_credito")));
        botaoContas.setTooltip(new Tooltip(idioma.getMensagem("contas")));
        botaoUsuarios.setTooltip(new Tooltip(idioma.getMensagem("usuarios")));
        botaoRelatorios.setTooltip(new Tooltip(idioma.getMensagem("relatorios")));
        botaoCalculadora.setTooltip(new Tooltip(idioma.getMensagem("calculadora")));
        botaoGuia.setTooltip(new Tooltip(idioma.getMensagem("guia")));
        botaoAjuda.setTooltip(new Tooltip(idioma.getMensagem("ajuda")));
        botaoAjudaProximo.setTooltip(new Tooltip(idioma.getMensagem("proximo")));
        botaoAjudaAnterior.setTooltip(new Tooltip(idioma.getMensagem("anterior")));
    }

    public void acaoGuia() {
        Janela.abrir(Utilitario.FXML_GUIA, idioma.getMensagem("guia"));
    }

    public void acaoAjuda() {
        if (ajuda) {
            desativarAjuda();
        } else {
            ativarAjuda();
            Kernel.controlador.acaoAjuda();
        }
    }

    public void acaoAjudaAnterior() {
        Ajuda.getInstance().apresentarAnterior();
    }

    public void acaoAjudaProximo() {
        Ajuda.getInstance().apresentarProximo();
    }

    public void desativarAjuda() {
        ajuda = false;
        botaoAjuda.getStyleClass().remove("botaoAjudaSair");
        botaoAjuda.getStyleClass().add("botaoAjuda");
        botaoAjudaProximo.setVisible(false);
        ajudaProgresso.setVisible(false);
        botaoAjudaAnterior.setVisible(false);
        botaoAjuda.setTooltip(new Tooltip(idioma.getMensagem("ajuda")));
        Ajuda.getInstance().desativarAjuda();
    }

    private void ativarAjuda() {
        ajuda = true;
        botaoAjuda.getStyleClass().remove("botaoAjuda");
        botaoAjuda.getStyleClass().add("botaoAjudaSair");
        botaoAjudaProximo.setVisible(true);
        ajudaProgresso.setVisible(true);
        botaoAjudaAnterior.setVisible(true);
        botaoAjuda.setTooltip(new Tooltip(idioma.getMensagem("sair")));
    }

    public void acaoSobreSistema() {
        Janela.abrir(Kernel.RAIZ + "/modulo/ajuda/SobreSistema.fxml", idioma.getMensagem("sobre_sistema"));
    }

    public void acaoVerificarAtualizacoes() {
        Atualizacao task = new Atualizacao(true, ajudaVerificarAtualizacoes);
    }

    private void verificarAtualizacoes() {
        if (DAYS.between(Datas.getLocalDate(Configuracao.getPropriedade("data_atualizacao")), LocalDate.now()) >= 30) {
            Configuracao.setPropriedade("data_atualizacao", Datas.toSqlData(LocalDate.now()));
            Atualizacao task = new Atualizacao(false, ajudaVerificarAtualizacoes);
        }
    }

    public void acaoConfiguracao() {
        Janela.abrir(Configuracao.FXML_FORMULARIO, idioma.getMensagem("configuracoes"));
    }

    public void acaoRelatorios() {
        carregarJanela(Relatorio.FXML);
    }

    public void acaoExportarBackup() {
        new Backup().exportarBackup();
    }

    public void acaoImportarBackup() {
        new Backup().importarBackup();
    }

    public void acaoHome() {
        carregarJanela(Kernel.FXML_HOME);
    }

    public void acaoCartaoCredito() {
        carregarJanela(CartaoCredito.FXML);
    }

    public void acaoConta() {
        carregarJanela(Conta.FXML);
    }

    public void acaoAgenda() {
        carregarJanela(Agenda.FXML);
    }

    public void acaoAgendaTipo() {
        carregarJanela(AgendaTipo.FXML);
    }

    public void acaoGrupoItem() {
        carregarJanela(GrupoItem.FXML);
    }

    public void acaoDespesa() {
        carregarJanela(Despesa.FXML);
    }

    public void acaoDespesaCadastroMultiplo() {
        carregarJanela(Despesa.FXML_CADASTRO_MULTIPLO);
    }

    public void acaoDespesaItem() {
        carregarJanela(DespesaItem.FXML);
    }

    public void acaoDespesaCategoria() {
        carregarJanela(DespesaCategoria.FXML);
    }

    public void acaoReceita() {
        carregarJanela(Receita.FXML);
    }

    public void acaoReceitaItem() {
        carregarJanela(ReceitaItem.FXML);
    }

    public void acaoReceitaCategoria() {
        carregarJanela(ReceitaCategoria.FXML);
    }

    public void acaoTransferencia() {
        carregarJanela(Transferencia.FXML);
    }

    public void acaoTransferenciaItem() {
        carregarJanela(TransferenciaItem.FXML);
    }

    public void acaoTransferenciaCategoria() {
        carregarJanela(TransferenciaCategoria.FXML);
    }

    public void acaoDespesasAgendadas() {
        carregarJanela(Despesa.FXML_DESPESAS_AGENDADAS);
    }

    public void acaoCartoesCredito() {
        carregarJanela(CartaoCredito.FXML);
    }

    public void acaoDespesasAgendadas(int mes, int ano) {
        DespesasAgendadasControlador c = carregarJanela(Despesa.FXML_DESPESAS_AGENDADAS);
        c.setData(mes, ano);
    }

    public void acaoUsuario() {
        carregarJanela(Usuario.FXML);
    }

    public void acaoVoltar() {
        carregarJanela(PilhaVoltar.voltar());
    }

    public void acaoCalculadora() {
        try {
            Pane painel = new FXMLLoader().load(getClass().getResourceAsStream(Utilitario.FXML_CALCULADORA));
            Scene cena = new Scene(painel);
            cena.getStylesheets().add(getClass().getResource(Kernel.CSS_PRINCIPAL).toExternalForm());
            cena.getStylesheets().add(getClass().getResource(Kernel.CSS_AJUDA).toExternalForm());
            cena.getStylesheets().add(getClass().getResource(Kernel.CSS_TOOLTIP).toExternalForm());
            Stage palco = new Stage();
            palco.setScene(cena);
            palco.setTitle(idioma.getMensagem("calculadora"));
            palco.getIcons().add(new Image(Kernel.RAIZ+"/recursos/imagem/outros/calculadora.png"));
            palco.initStyle(StageStyle.UTILITY);
            palco.show();
            palco.requestFocus();
        } catch (IOException ex) {
            Janela.showException(ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        traduzirMenu();
        tooltipBotoes();
        desativarAjuda();
        Kernel.principal = this;
        Kernel.layoutGeral = this.layoutGeral;
        Kernel.layoutCentro = this.layoutCentro;
        carregarJanela(Kernel.FXML_HOME);
        verificarAtualizacoes();
    }

    private <T> T carregarJanela(String arquivoFXML) {
        try {
            if(Kernel.controlador==null || Kernel.controlador.sair()){
                desativarAjuda();
                PilhaVoltar.adicionar(arquivoFXML);
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Janela.class.getResource(arquivoFXML));
                Node node = loader.load(Janela.class.getResourceAsStream(arquivoFXML));
                Animacao.fadeOutInReplace(layoutCentro, node);
                Kernel.controlador = loader.getController();
                Ajuda.getInstance().setBotaoProgresso(ajudaProgresso);
                return loader.getController();
            }else{
                return null;
            }
        } catch (Exception ex) {
            Janela.showException(ex);
            return null;
        }
    }

}

/*
Copyright 2012-2015 Jose Robson Mariano Alves

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

package badernageral.bgfinancas.principal;

import badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.PilhaVoltar;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.biblioteca.utilitario.Versao;
import badernageral.bgfinancas.idioma.Linguagem;
import badernageral.bgfinancas.modelo.Agenda;
import badernageral.bgfinancas.modelo.AgendaTipo;
import badernageral.bgfinancas.modelo.Configuracao;
import badernageral.bgfinancas.modelo.Conta;
import badernageral.bgfinancas.modelo.Despesa;
import badernageral.bgfinancas.modelo.DespesaCategoria;
import badernageral.bgfinancas.modelo.DespesaItem;
import badernageral.bgfinancas.modelo.GrupoItem;
import badernageral.bgfinancas.modelo.Receita;
import badernageral.bgfinancas.modelo.ReceitaCategoria;
import badernageral.bgfinancas.modelo.ReceitaItem;
import badernageral.bgfinancas.modelo.Relatorio;
import badernageral.bgfinancas.modelo.Transferencia;
import badernageral.bgfinancas.modelo.TransferenciaCategoria;
import badernageral.bgfinancas.modelo.TransferenciaItem;
import badernageral.bgfinancas.modelo.Usuario;
import badernageral.bgfinancas.modelo.Utilitario;
import badernageral.bgfinancas.modulo.despesa.DespesasAgendadasControlador;
import badernageral.bgfinancas.modulo.utilitario.Backup;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public final class PrincipalControlador implements Initializable {
    
    private final Linguagem idioma = Linguagem.getInstance();
    
    private boolean ajuda = false;
    
    @FXML private BorderPane layoutGeral;
    @FXML private StackPane layoutCentro;
    
    @FXML private Menu menuGerenciar;
    @FXML private Menu menuMovimentar;
    @FXML private Menu menuUtilitarios;
    @FXML private Menu menuAjuda;
    @FXML private MenuItem gerenciarContas;
    @FXML private MenuItem gerenciarAgenda;
    @FXML private MenuItem gerenciarGrupos;
    @FXML private MenuItem gerenciarDespesasAgendadas;
    @FXML private MenuItem gerenciarUsuarios;
    @FXML private MenuItem movimentarDespesa;
    @FXML private MenuItem movimentarReceita;
    @FXML private MenuItem movimentarTransferencia;
    @FXML private MenuItem movimentarGuia;
    @FXML private MenuItem utilitariosConfiguracoes;
    @FXML private MenuItem utilitariosRelatorios;
    @FXML private MenuItem utilitariosExportarBackup;
    @FXML private MenuItem utilitariosImportarBackup;
    @FXML private MenuItem ajudaTutorial;
    @FXML private MenuItem ajudaSobreSistema;
    @FXML private MenuItem ajudaVerificarAtualizacoes;
    
    @FXML private Button botaoHome;
    @FXML private Button botaoDespesas;
    @FXML private Button botaoReceitas;
    @FXML private Button botaoTransferencias;
    @FXML private Button botaoDespesasAgendadas;
    @FXML private Button botaoAgenda;
    @FXML private Button botaoGrupo;
    @FXML private Button botaoContas;
    @FXML private Button botaoUsuarios;
    @FXML private Button botaoRelatorios;
    @FXML private Button botaoGuia;
    
    @FXML private Button botaoAjuda;
    @FXML private Button botaoAjudaProximo;
    @FXML private Button botaoAjudaAnterior;
    @FXML private ProgressIndicator ajudaProgresso;
    
    private void traduzirMenu(){
        menuGerenciar.setText(idioma.getMensagem("gerenciar"));
        menuMovimentar.setText(idioma.getMensagem("movimentar"));
        menuUtilitarios.setText(idioma.getMensagem("utilitarios"));
        menuAjuda.setText(idioma.getMensagem("ajuda"));
        gerenciarContas.setText(idioma.getMensagem("contas"));
        gerenciarAgenda.setText(idioma.getMensagem("lembretes"));
        gerenciarGrupos.setText(idioma.getMensagem("cotas_despesas"));
        gerenciarDespesasAgendadas.setText(idioma.getMensagem("despesas_agendadas"));
        gerenciarUsuarios.setText(idioma.getMensagem("usuarios"));
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
    
    private void tooltipBotoes(){
        botaoHome.setTooltip(new Tooltip(idioma.getMensagem("principal")));
        botaoDespesas.setTooltip(new Tooltip(idioma.getMensagem("despesas")));
        botaoReceitas.setTooltip(new Tooltip(idioma.getMensagem("receitas")));
        botaoTransferencias.setTooltip(new Tooltip(idioma.getMensagem("transferencias")));
        botaoDespesasAgendadas.setTooltip(new Tooltip(idioma.getMensagem("despesas_agendadas")));
        botaoAgenda.setTooltip(new Tooltip(idioma.getMensagem("lembretes")));
        botaoGrupo.setTooltip(new Tooltip(idioma.getMensagem("cotas_despesas")));
        botaoContas.setTooltip(new Tooltip(idioma.getMensagem("contas")));
        botaoUsuarios.setTooltip(new Tooltip(idioma.getMensagem("usuarios")));
        botaoRelatorios.setTooltip(new Tooltip(idioma.getMensagem("relatorios")));
        botaoGuia.setTooltip(new Tooltip(idioma.getMensagem("guia")));
        botaoAjuda.setTooltip(new Tooltip(idioma.getMensagem("ajuda")));
        botaoAjudaProximo.setTooltip(new Tooltip(idioma.getMensagem("proximo")));
        botaoAjudaAnterior.setTooltip(new Tooltip(idioma.getMensagem("anterior")));
    }
    
    public void acaoGuia(){
        Janela.abrir(Utilitario.FXML_GUIA, idioma.getMensagem("guia"));
    }
    
    public void acaoAjuda(){
        if(ajuda){
            desativarAjuda();
        }else{
            ativarAjuda();
            Kernel.controlador.acaoAjuda();
        }
    }
    
    public void acaoAjudaAnterior(){
        Ajuda.getInstance().apresentarAnterior();
    }
    
    public void acaoAjudaProximo(){
        Ajuda.getInstance().apresentarProximo();
    }
    
    public void desativarAjuda(){
        ajuda = false;
        botaoAjuda.getStyleClass().remove("botaoAjudaSair");
        botaoAjuda.getStyleClass().add("botaoAjuda");
        botaoAjudaProximo.setVisible(false);
        ajudaProgresso.setVisible(false);
        botaoAjudaAnterior.setVisible(false);
        botaoAjuda.setTooltip(new Tooltip(idioma.getMensagem("ajuda")));
        Ajuda.getInstance().desativarAjuda();
    }
    
    private void ativarAjuda(){
        ajuda = true;
        botaoAjuda.getStyleClass().remove("botaoAjuda");
        botaoAjuda.getStyleClass().add("botaoAjudaSair");
        botaoAjudaProximo.setVisible(true);
        ajudaProgresso.setVisible(true);
        botaoAjudaAnterior.setVisible(true);
        botaoAjuda.setTooltip(new Tooltip(idioma.getMensagem("sair")));
    }
    
    public void acaoSobreSistema() {
        Janela.abrir(Kernel.RAIZ+"/modulo/ajuda/SobreSistema.fxml", idioma.getMensagem("sobre_sistema"));
    }
    
    public void acaoVerificarAtualizacoes() {
        try {
            URL url = new URL("http://badernageral.github.io/ultima_versao_bgfinancas.txt");
            BufferedReader arquivo = new BufferedReader(new InputStreamReader(url.openStream()));
            String versao_atual = arquivo.readLine();
            arquivo.close();
            if(Versao.isAtualizada(Configuracao.getPropriedade("versao"), versao_atual)){
                Janela.showMensagem(Status.SUCESSO, idioma.getMensagem("sistema_atualizado"));
            }else{
                if(Janela.showPergunta(idioma.getMensagem("sistema_desatualizado"))){
                    Desktop.getDesktop().browse(new URI("https://github.com/badernageral/bgfinancas/releases"));
                }
            }
        } catch (UnknownHostException ex){
            Janela.showMensagem(Status.ERRO, idioma.getMensagem("sem_internet"));
        } catch (Exception ex) {
            Janela.showException(ex);
        }
    }
    
    public void acaoConfiguracao() {
        Janela.abrir(Configuracao.FXML_FORMULARIO, idioma.getMensagem("configuracoes"));
    }
    
    public void acaoRelatorios(){
        carregarJanela(Relatorio.FXML);
    }
    
    public void acaoExportarBackup(){
        new Backup().exportarBackup();
    }
    
    public void acaoImportarBackup(){
        new Backup().importarBackup();
    }
    
    public void acaoHome() {
        carregarJanela(Kernel.FXML_HOME);
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        traduzirMenu();
        tooltipBotoes();
        desativarAjuda();
        Kernel.principal = this;
        Kernel.layoutGeral = this.layoutGeral;
        Kernel.layoutCentro = this.layoutCentro;
        carregarJanela(Kernel.FXML_HOME);
    }

    private <T> T carregarJanela(String arquivoFXML) {
        try {
            desativarAjuda();
            PilhaVoltar.adicionar(arquivoFXML);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Janela.class.getResource(arquivoFXML));
            Node node = loader.load(Janela.class.getResourceAsStream(arquivoFXML));
            Animacao.fadeOutInReplace(layoutCentro, node);
            Kernel.controlador = loader.getController();
            Ajuda.getInstance().setBotaoProgresso(ajudaProgresso);
            return loader.getController();
        } catch (Exception ex) {
            Janela.showException(ex);
            return null;
        }
    }
    
}

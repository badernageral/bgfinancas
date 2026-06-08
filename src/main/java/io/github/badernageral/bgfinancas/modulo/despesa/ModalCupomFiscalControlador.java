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

package io.github.badernageral.bgfinancas.modulo.despesa;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Datas;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public final class ModalCupomFiscalControlador implements Initializable, ControladorFormulario {

    @FXML private TitledPane formulario;
    @FXML private HBox hboxArquivo;
    @FXML private Text mensagem_1;
    @FXML private Text mensagem_2;
    @FXML private Button pegar_itens;
    @FXML private Label valor_total;
    @FXML private TableView<Despesa> lista_itens;
    @FXML private TableColumn<String, Despesa> coluna_1;
    @FXML private TableColumn<ComboBox<Item>, Despesa> coluna_2;
    @FXML private TableColumn<BigDecimal, Despesa> coluna_3;
    @FXML private TableColumn<BigDecimal, Despesa> coluna_4;
    @FXML private Button ok;
    @FXML private Button cancelar;

    private final CheckBox checkFiltroData = new CheckBox();
    private final DatePicker filtroData = new DatePicker();
    private final TableColumn<Despesa, String> colunaData = new TableColumn<>();

    private final List<Despesa> transacoesOFX = new ArrayList<>();
    private List<Despesa> resultado = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("importar_ofx"));
        mensagem_1.setText(idioma.getMensagem("arquivo_ofx") + ":");
        mensagem_2.setText(idioma.getMensagem("itens_cupom"));
        coluna_1.setText(idioma.getMensagem("item_cupom"));
        coluna_2.setText(idioma.getMensagem("item"));
        coluna_3.setText(idioma.getMensagem("quantidade"));
        coluna_4.setText(idioma.getMensagem("valor_total"));
        valor_total.setText(idioma.getMensagem("valor_total") + ": " + idioma.getMensagem("moeda") + " 0.0");
        ok.setText(Linguagem.getInstance().getMensagem("ok"));
        cancelar.setText(Linguagem.getInstance().getMensagem("cancelar"));
        ok.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("ok") + " (ALT+ENTER)"));
        cancelar.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("cancelar") + " (ESCAPE)"));
        pegar_itens.setText(idioma.getMensagem("selecionar_arquivo"));
        pegar_itens.setTooltip(new Tooltip(idioma.getMensagem("selecionar_arquivo")));
        pegar_itens.setOnAction((ActionEvent e) -> acaoCadastrar(1));

        coluna_1.setCellValueFactory(new PropertyValueFactory<>("nomeItem"));
        coluna_2.setCellValueFactory(new PropertyValueFactory<>("comboItem"));
        coluna_3.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        coluna_4.setCellValueFactory(new PropertyValueFactory<>("valor"));

        colunaData.setText(idioma.getMensagem("data"));
        colunaData.setCellValueFactory(cell -> {
            LocalDate d = cell.getValue().getData();
            if (d == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(d.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        });
        colunaData.setMaxWidth(100);
        colunaData.setMinWidth(80);
        lista_itens.getColumns().add(0, colunaData);

        checkFiltroData.setText(idioma.getMensagem("data_inicial_ofx"));
        filtroData.setPrefWidth(140);
        filtroData.setVisible(false);
        filtroData.setManaged(false);
        checkFiltroData.setOnAction(e -> {
            boolean marcado = checkFiltroData.isSelected();
            filtroData.setVisible(marcado);
            filtroData.setManaged(marcado);
            if (marcado && filtroData.getValue() == null) {
                filtroData.setValue(LocalDate.now().withDayOfMonth(1));
            }
            aplicarFiltroOFX();
        });
        filtroData.valueProperty().addListener((obs, anterior, novo) -> aplicarFiltroOFX());
        HBox.setMargin(checkFiltroData, new javafx.geometry.Insets(0, 0, 0, 10));
        hboxArquivo.getChildren().addAll(checkFiltroData, filtroData);
    }

    @FXML
    @Override
    public void acaoFinalizar() {
        ObservableList<Despesa> itens = lista_itens.getItems();
        List<Despesa> novoResultado = new ArrayList<>();
        for (int i = 0; i < itens.size(); i++) {
            Despesa d = itens.get(i);
            if (d.getItemAutoFiltro() == null || d.getItemAutoFiltro().getItem() == null) {
                lista_itens.scrollTo(i);
                lista_itens.getSelectionModel().select(i);
                Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("validar_autofiltro"), ok, Duracao.LONGA);
                return;
            }
            novoResultado.add(new Despesa(
                d.getItemAutoFiltro().getItem().getIdItem(),
                d.getItemAutoFiltro().getItem().getNome(),
                d.getItemAutoFiltro().getItem().getNomeCategoria(),
                d.getQuantidade().toString(),
                d.getValor().toString()
            ));
        }
        resultado = novoResultado;
        Animacao.fadeInOutClose(formulario);
    }

    public List<Despesa> getResultado() {
        return resultado;
    }

    @Override
    public void acaoCadastrar(int botao) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(idioma.getMensagem("arquivo_ofx"));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("OFX", "*.ofx"),
            new FileChooser.ExtensionFilter(idioma.getMensagem("todos_arquivos"), "*.*")
        );
        File arquivoSelecionado = fileChooser.showOpenDialog(Kernel.palco);
        if (arquivoSelecionado != null) {
            parsearOFX(arquivoSelecionado);
        }
    }

    private void parsearOFX(File arquivo) {
        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(arquivo), Charset.forName("windows-1252")))) {
                String linha;
                boolean dentroXml = false;
                while ((linha = reader.readLine()) != null) {
                    if (!dentroXml && linha.trim().startsWith("<OFX>")) {
                        dentroXml = true;
                    }
                    if (dentroXml) {
                        sb.append(linha).append("\n");
                    }
                }
            }
            if (sb.length() == 0) {
                throw new Exception(idioma.getMensagem("arquivo_ofx_invalido"));
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(sb.toString())));
            NodeList transacoes = doc.getElementsByTagName("STMTTRN");
            transacoesOFX.clear();
            for (int i = 0; i < transacoes.getLength(); i++) {
                Node transacao = transacoes.item(i);
                if (transacao.getNodeType() != Node.ELEMENT_NODE) continue;
                NodeList nos = transacao.getChildNodes();
                String memo = null;
                BigDecimal valor = null;
                LocalDate dataTransacao = null;
                for (int j = 0; j < nos.getLength(); j++) {
                    Node no = nos.item(j);
                    if ("MEMO".equals(no.getNodeName()) && no.getFirstChild() != null) {
                        memo = no.getFirstChild().getNodeValue();
                    } else if ("TRNAMT".equals(no.getNodeName()) && no.getFirstChild() != null) {
                        valor = new BigDecimal(no.getFirstChild().getNodeValue());
                    } else if ("DTPOSTED".equals(no.getNodeName()) && no.getFirstChild() != null) {
                        dataTransacao = parsearDataOFX(no.getFirstChild().getNodeValue());
                    }
                }
                if (memo != null && valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
                    Despesa d = new Despesa();
                    d.setNomeItem(memo);
                    d.setQuantidade("1");
                    d.setValor(valor.abs().toPlainString());
                    if (dataTransacao != null) {
                        d.setData(Datas.toSqlData(dataTransacao));
                    }
                    transacoesOFX.add(d);
                }
            }
            aplicarFiltroOFX();
        } catch (Exception ex) {
            Janela.showException(ex);
        }
    }

    private void aplicarFiltroOFX() {
        LocalDate dataFiltro = checkFiltroData.isSelected() ? filtroData.getValue() : null;
        List<Despesa> filtrados = transacoesOFX.stream()
            .filter(d -> dataFiltro == null || d.getData() == null || !d.getData().isBefore(dataFiltro))
            .collect(Collectors.toList());
        lista_itens.getItems().setAll(filtrados);
        BigDecimal total = filtrados.stream()
            .map(Despesa::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        valor_total.setText(idioma.getMensagem("valor_total") + ": " + idioma.getMensagem("moeda") + " " + total.toPlainString());
    }

    private LocalDate parsearDataOFX(String dtposted) {
        try {
            String s = dtposted.trim();
            return LocalDate.of(
                Integer.parseInt(s.substring(0, 4)),
                Integer.parseInt(s.substring(4, 6)),
                Integer.parseInt(s.substring(6, 8))
            );
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    @Override
    public void acaoCancelar() {
        Stage palco = (Stage) formulario.getScene().getWindow();
        palco.setTitle("");
        Animacao.fadeInOutClose(formulario);
    }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboItem(int combo, Item item) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}

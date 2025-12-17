package com.openwarehouses.views.visualization;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Pasillo;
import com.openwarehouses.services.LabelGenerationService;
import com.openwarehouses.utils.FooterFactory;
import com.openwarehouses.utils.GridLoader;
import com.openwarehouses.utils.HeaderUtils;
import com.openwarehouses.utils.HierarchyButtonFactory;
import com.openwarehouses.utils.StageUtils;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * Vista que permite seleccionar uno o varios pasillos de un almacén para la
 * impresión de etiquetas.
 *
 * <p>
 * Desde esta vista es posible navegar al nivel de estanterías de un pasillo
 * concreto o imprimir
 * directamente las etiquetas asociadas.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class SeleccionPasilloEtiquetasView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Almacén al que pertenecen los pasillos mostrados. */
  private final Almacen almacen;

  /** Servicio encargado de la generación e impresión de etiquetas. */
  private final LabelGenerationService labelGenerationService;

  /** Contenedor gráfico donde se muestran los botones de pasillos. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen pasillos disponibles. */
  private Label emptyMessage;

  /** Lista de pasillos seleccionados para la impresión de etiquetas. */
  private List<Pasillo> selectedPasillos = new ArrayList<>();

  /**
   * Constructor de la vista de selección de pasillos.
   *
   * @param primaryStage escenario principal de la aplicación
   * @param almacen      almacén del que se mostrarán los pasillos
   */
  public SeleccionPasilloEtiquetasView(Stage primaryStage, Almacen almacen) {
    this.primaryStage = primaryStage;
    this.almacen = almacen;
    this.labelGenerationService = new LabelGenerationService();

    crearHeader();
    crearGridPasillos();
    crearFooter();
    cargarPasillos();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea y configura el encabezado de la vista. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, () -> volverAtras());
  }

  /** Inicializa el grid donde se mostrarán los pasillos disponibles. */
  private void crearGridPasillos() {
    emptyMessage = new Label("No hay pasillos disponibles");
    buttonGrid = GridLoader.createStandardGrid(this, emptyMessage);
  }

  /** Carga los pasillos del almacén y los representa gráficamente. */
  private void cargarPasillos() {
    GridLoader.<Pasillo>load(
        buttonGrid, almacen.getPasillos(), emptyMessage, this::crearBotonPasillo);
  }

  /** Crea el pie de página con la acción de impresión de etiquetas. */
  private void crearFooter() {
    FooterFactory.createPrintFooter(this, () -> handlePrintPasillos());
  }

  /**
   * Crea un botón asociado a un pasillo concreto.
   *
   * @param pasillo pasillo a representar
   * @return botón configurado para el pasillo
   */
  private Button crearBotonPasillo(Pasillo pasillo) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        "Pasillo " + pasillo.getNumero(), pasillo, selectedPasillos, p -> abrirEstanterias(p));
  }

  /**
   * Gestiona la impresión de etiquetas de los pasillos seleccionados.
   *
   * <p>
   * Si no hay ningún pasillo seleccionado, se muestra un mensaje de aviso.
   */
  private void handlePrintPasillos() {
    labelGenerationService.genericPrint(
        selectedPasillos,
        "Por favor selecciona al menos un pasillo para imprimir etiquetas.",
        p -> labelGenerationService.generateLabelsForPasillo(p));
  }

  /**
   * Abre la vista de selección de estanterías del pasillo indicado.
   *
   * @param pasillo pasillo seleccionado para visualizar sus estanterías
   */
  private void abrirEstanterias(Pasillo pasillo) {
    SeleccionEstanteriaEtiquetasView view = new SeleccionEstanteriaEtiquetasView(primaryStage, pasillo, almacen);
    view.show();
  }

  /** Vuelve a la vista de selección de almacenes. */
  private void volverAtras() {
    SeleccionAlmacenEtiquetasView view = new SeleccionAlmacenEtiquetasView(primaryStage);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(primaryStage, this, "Seleccionar Pasillos - " + almacen.getNombre());
  }
}

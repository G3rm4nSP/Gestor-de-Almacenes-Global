package com.openwarehouses.views.visualization;

import java.util.ArrayList;
import java.util.List;

import com.openwarehouses.controllers.AlmacenController;
import com.openwarehouses.models.Almacen;
import com.openwarehouses.services.LabelGenerationService;
import com.openwarehouses.utils.FooterFactory;
import com.openwarehouses.utils.GridLoader;
import com.openwarehouses.utils.HeaderUtils;
import com.openwarehouses.utils.HierarchyButtonFactory;
import com.openwarehouses.utils.StageUtils;
import com.openwarehouses.views.InicioView;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * Vista que permite seleccionar uno o varios almacenes para imprimir etiquetas
 * de los pasillos
 * asociados.
 *
 * <p>
 * También permite navegar a la vista de pasillos de un almacén concreto
 * mediante interacción
 * directa con el botón correspondiente.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class SeleccionAlmacenEtiquetasView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Controlador encargado de la gestión de almacenes. */
  private final AlmacenController controller;

  /** Servicio responsable de la generación e impresión de etiquetas. */
  private final LabelGenerationService labelGenerationService;

  /** Contenedor gráfico donde se muestran los botones de almacenes. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen almacenes disponibles. */
  private Label emptyMessage;

  /** Lista de almacenes seleccionados para la impresión de etiquetas. */
  private List<Almacen> selectedAlmacenes = new ArrayList<>();

  /**
   * Constructor de la vista de selección de almacenes.
   *
   * @param primaryStage escenario principal de la aplicación
   */
  public SeleccionAlmacenEtiquetasView(Stage primaryStage) {
    this.primaryStage = primaryStage;
    this.controller = new AlmacenController();
    this.labelGenerationService = new LabelGenerationService();

    crearHeader();
    crearGridAlmacenes();
    crearFooter();
    cargarAlmacenes();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea y configura el encabezado de la vista. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, () -> volverAtras());
  }

  /** Inicializa el grid donde se mostrarán los almacenes disponibles. */
  private void crearGridAlmacenes() {
    emptyMessage = new Label("No hay almacenes disponibles");
    buttonGrid = GridLoader.createStandardGrid(this, emptyMessage);
  }

  /** Carga los almacenes desde el controlador y los representa gráficamente. */
  private void cargarAlmacenes() {
    GridLoader.<Almacen>load(
        buttonGrid, controller.getAllAlmacenes(), emptyMessage, this::crearBotonAlmacen);
  }

  /** Crea el pie de página con la acción de impresión. */
  private void crearFooter() {
    FooterFactory.createPrintFooter(this, () -> handlePrintAlmacenes());
  }

  /**
   * Crea un botón asociado a un almacén concreto.
   *
   * @param almacen almacén a representar
   * @return botón configurado para el almacén
   */
  private Button crearBotonAlmacen(Almacen almacen) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        almacen.getNombre(), almacen, selectedAlmacenes, a -> abrirPasillos(a));
  }

  /**
   * Gestiona la impresión de etiquetas de los almacenes seleccionados.
   *
   * <p>
   * Si no hay ningún almacén seleccionado, se muestra un mensaje de aviso.
   */
  private void handlePrintAlmacenes() {
    labelGenerationService.genericPrint(
        selectedAlmacenes,
        "Por favor selecciona al menos un almacén para imprimir etiquetas.",
        a -> labelGenerationService.generateLabelsForPasillos(a.getPasillos()));
  }

  /** Vuelve a la vista de inicio de la aplicación. */
  private void volverAtras() {
    InicioView view = new InicioView(primaryStage);
    view.show();
  }

  /**
   * Abre la vista de selección de pasillos del almacén indicado.
   *
   * @param almacen almacén seleccionado para visualizar sus pasillos
   */
  private void abrirPasillos(Almacen almacen) {
    SeleccionPasilloEtiquetasView view = new SeleccionPasilloEtiquetasView(primaryStage, almacen);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(primaryStage, this, "Seleccionar Almacenes");
  }
}

package com.openwarehouses.views.visualization;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Estanteria;
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
 * Vista que permite seleccionar una o varias estanterías pertenecientes a un
 * pasillo concreto para
 * la impresión de etiquetas.
 *
 * <p>
 * Desde esta vista es posible continuar la navegación al nivel de alturas o
 * imprimir
 * directamente las etiquetas asociadas a las estanterías seleccionadas.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class SeleccionEstanteriaEtiquetasView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Almacén al que pertenece el pasillo actual. */
  private final Almacen almacen;

  /** Pasillo cuyos estanterías se muestran en la vista. */
  private final Pasillo pasillo;

  /** Servicio encargado de la generación e impresión de etiquetas. */
  private final LabelGenerationService labelGenerationService;

  /** Contenedor gráfico donde se muestran los botones de estanterías. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen estanterías disponibles. */
  private Label emptyMessage;

  /** Lista de estanterías seleccionadas para la impresión de etiquetas. */
  private List<Estanteria> selectedEstanterias = new ArrayList<>();

  /**
   * Constructor de la vista de selección de estanterías.
   *
   * @param primaryStage escenario principal de la aplicación
   * @param pasillo      pasillo del que se mostrarán las estanterías
   * @param almacen      almacén al que pertenece el pasillo
   */
  public SeleccionEstanteriaEtiquetasView(Stage primaryStage, Pasillo pasillo, Almacen almacen) {

    this.primaryStage = primaryStage;
    this.almacen = almacen;
    this.pasillo = pasillo;
    this.labelGenerationService = new LabelGenerationService();

    crearHeader();
    crearGridEstanterias();
    crearFooter();
    cargarEstanterias();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea y configura el encabezado de la vista. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, () -> volverAtras());
  }

  /** Inicializa el grid donde se mostrarán las estanterías disponibles. */
  private void crearGridEstanterias() {
    emptyMessage = new Label("No hay estanterías disponibles");
    buttonGrid = GridLoader.createStandardGrid(this, emptyMessage);
  }

  /** Carga las estanterías del pasillo y las representa gráficamente. */
  private void cargarEstanterias() {
    GridLoader.<Estanteria>load(
        buttonGrid, pasillo.getEstanterias(), emptyMessage, this::crearBotonEstanteria);
  }

  /** Crea el pie de página con la acción de impresión de etiquetas. */
  private void crearFooter() {
    FooterFactory.createPrintFooter(this, () -> handlePrintEstanterias());
  }

  /**
   * Crea un botón asociado a una estantería concreta.
   *
   * @param estanteria estantería a representar
   * @return botón configurado para la estantería
   */
  private Button crearBotonEstanteria(Estanteria estanteria) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        "Estanteria " + estanteria.getNumero(),
        estanteria,
        selectedEstanterias,
        e -> abrirAlturas(e));
  }

  /**
   * Gestiona la impresión de etiquetas de las estanterías seleccionadas.
   *
   * <p>
   * Si no hay ninguna estantería seleccionada, se muestra un mensaje de aviso.
   */
  private void handlePrintEstanterias() {
    labelGenerationService.genericPrint(
        selectedEstanterias,
        "Por favor selecciona al menos una estantería para imprimir etiquetas.",
        e -> labelGenerationService.generateLabelsForEstanteria(pasillo.getNumero(), e));
  }

  /**
   * Abre la vista de selección de alturas para la estantería indicada.
   *
   * @param estanteria estantería seleccionada
   */
  private void abrirAlturas(Estanteria estanteria) {
    SeleccionAlturaEtiquetasView view = new SeleccionAlturaEtiquetasView(primaryStage, pasillo, estanteria, almacen);
    view.show();
  }

  /** Vuelve a la vista de selección de pasillos. */
  private void volverAtras() {
    SeleccionPasilloEtiquetasView view = new SeleccionPasilloEtiquetasView(primaryStage, almacen);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(
        primaryStage,
        this,
        "Seleccionar Estanterias - " + almacen.getNombre() + " - Pasillo " + pasillo.getNumero());
  }
}

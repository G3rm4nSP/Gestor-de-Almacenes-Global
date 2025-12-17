package com.openwarehouses.views.visualization;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Altura;
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
 * Vista que permite seleccionar una o varias alturas pertenecientes a una
 * estantería concreta para
 * la impresión de etiquetas.
 *
 * <p>
 * Desde esta vista es posible continuar la navegación al nivel de posiciones o
 * imprimir
 * directamente las etiquetas asociadas a las alturas seleccionadas.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class SeleccionAlturaEtiquetasView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Almacén al que pertenece la estantería actual. */
  private final Almacen almacen;

  /** Pasillo al que pertenece la estantería. */
  private final Pasillo pasillo;

  /** Estantería cuyas alturas se muestran en la vista. */
  private final Estanteria estanteria;

  /** Servicio encargado de la generación e impresión de etiquetas. */
  private final LabelGenerationService labelGenerationService;

  /** Contenedor gráfico donde se muestran los botones de alturas. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen alturas disponibles. */
  private Label emptyMessage;

  /** Lista de alturas seleccionadas para la impresión de etiquetas. */
  private List<Altura> selectedAlturas = new ArrayList<>();

  /**
   * Constructor de la vista de selección de alturas.
   *
   * @param primaryStage escenario principal de la aplicación
   * @param pasillo      pasillo al que pertenece la estantería
   * @param estanteria   estantería de la que se mostrarán las alturas
   * @param almacen      almacén al que pertenece la estantería
   */
  public SeleccionAlturaEtiquetasView(
      Stage primaryStage, Pasillo pasillo, Estanteria estanteria, Almacen almacen) {

    this.primaryStage = primaryStage;
    this.almacen = almacen;
    this.pasillo = pasillo;
    this.estanteria = estanteria;
    this.labelGenerationService = new LabelGenerationService();

    crearHeader();
    crearGridAlturas();
    crearFooter();
    cargarAlturas();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea y configura el encabezado de la vista. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, () -> volverAtras());
  }

  /** Inicializa el grid donde se mostrarán las alturas disponibles. */
  private void crearGridAlturas() {
    emptyMessage = new Label("No hay alturas disponibles");
    buttonGrid = GridLoader.createStandardGrid(this, emptyMessage);
  }

  /** Carga las alturas de la estantería y las representa gráficamente. */
  private void cargarAlturas() {
    GridLoader.<Altura>load(
        buttonGrid, estanteria.getAlturas(), emptyMessage, this::crearBotonAltura);
  }

  /** Crea el pie de página con la acción de impresión de etiquetas. */
  private void crearFooter() {
    FooterFactory.createPrintFooter(this, () -> handlePrintAlturas());
  }

  /**
   * Crea un botón asociado a una altura concreta.
   *
   * @param altura altura a representar
   * @return botón configurado para la altura
   */
  private Button crearBotonAltura(Altura altura) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        "Altura " + altura.getNumero(), altura, selectedAlturas, a -> abrirPosiciones(a));
  }

  /**
   * Gestiona la impresión de etiquetas de las alturas seleccionadas.
   *
   * <p>
   * Si no hay ninguna altura seleccionada, se muestra un mensaje de aviso.
   */
  private void handlePrintAlturas() {
    labelGenerationService.genericPrint(
        selectedAlturas,
        "Por favor selecciona al menos una altura para imprimir etiquetas.",
        a -> labelGenerationService.generateLabelsForAltura(
            pasillo.getNumero(), estanteria.getNumero(), a));
  }

  /**
   * Abre la vista de selección de posiciones para la altura indicada.
   *
   * @param altura altura seleccionada
   */
  private void abrirPosiciones(Altura altura) {
    SeleccionPosicionEtiquetasView view = new SeleccionPosicionEtiquetasView(primaryStage, pasillo, estanteria, altura,
        almacen);
    view.show();
  }

  /** Vuelve a la vista de selección de estanterías. */
  private void volverAtras() {
    SeleccionEstanteriaEtiquetasView view = new SeleccionEstanteriaEtiquetasView(primaryStage, pasillo, almacen);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(
        primaryStage,
        this,
        "Seleccionar Alturas - "
            + almacen.getNombre()
            + " - Pasillo "
            + pasillo.getNumero()
            + " - Estantería "
            + estanteria.getNumero());
  }
}

package com.openwarehouses.views.visualization;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Altura;
import com.openwarehouses.models.Estanteria;
import com.openwarehouses.models.Pasillo;
import com.openwarehouses.models.Posicion;
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
 * Vista encargada de la selección de posiciones dentro de una altura concreta
 * para la generación e
 * impresión de etiquetas. Permite al usuario seleccionar una o varias
 * posiciones y generar
 * etiquetas asociadas a su jerarquía (pasillo, estantería y altura).
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class SeleccionPosicionEtiquetasView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Almacén al que pertenecen las posiciones. */
  private final Almacen almacen;

  /** Pasillo actual seleccionado. */
  private final Pasillo pasillo;

  /** Estantería actual seleccionada. */
  private final Estanteria estanteria;

  /** Altura actual seleccionada. */
  private final Altura altura;

  /** Servicio encargado de la generación e impresión de etiquetas. */
  private final LabelGenerationService labelGenerationService;

  /** Contenedor visual para los botones de posiciones. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen posiciones disponibles. */
  private Label emptyMessage;

  /** Lista de posiciones seleccionadas por el usuario. */
  private List<Posicion> selectedPosiciones = new ArrayList<>();

  /**
   * Constructor de la vista de selección de posiciones para etiquetas.
   *
   * @param primaryStage Escenario principal de la aplicación
   * @param pasillo      Pasillo seleccionado
   * @param estanteria   Estantería seleccionada
   * @param altura       Altura seleccionada
   * @param almacen      Almacén al que pertenece la jerarquía
   */
  public SeleccionPosicionEtiquetasView(
      Stage primaryStage, Pasillo pasillo, Estanteria estanteria, Altura altura, Almacen almacen) {
    this.primaryStage = primaryStage;
    this.almacen = almacen;
    this.pasillo = pasillo;
    this.estanteria = estanteria;
    this.altura = altura;
    this.labelGenerationService = new LabelGenerationService();

    crearHeader();
    crearGridPosiciones();
    crearFooter();
    cargarPosiciones();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea el encabezado de la vista con opción de navegación hacia atrás. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, () -> volverAtras());
  }

  /** Inicializa la cuadrícula donde se muestran las posiciones. */
  private void crearGridPosiciones() {
    emptyMessage = new Label("No hay posiciones disponibles");
    buttonGrid = GridLoader.createStandardGrid(this, emptyMessage);
  }

  /** Carga las posiciones de la altura actual en la cuadrícula. */
  private void cargarPosiciones() {
    GridLoader.<Posicion>load(
        buttonGrid, altura.getPosiciones(), emptyMessage, this::crearBotonPosicion);
  }

  /** Crea el pie de la vista con la opción de imprimir etiquetas. */
  private void crearFooter() {
    FooterFactory.createPrintFooter(this, () -> handlePrintPosiciones());
  }

  /**
   * Crea un botón jerárquico para una posición concreta.
   *
   * @param posicion Posición asociada al botón
   * @return Botón configurado para la posición
   */
  private Button crearBotonPosicion(Posicion posicion) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        "Posicion " + posicion.getNumero(), posicion, selectedPosiciones, p -> abrirPosicion(p));
  }

  /** Gestiona la impresión de etiquetas para las posiciones seleccionadas. */
  private void handlePrintPosiciones() {
    labelGenerationService.genericPrint(
        selectedPosiciones,
        "Por favor selecciona al menos una posición para imprimir etiquetas.",
        p -> labelGenerationService.generateLabelsForPosiciones(
            pasillo.getNumero(),
            estanteria.getNumero(),
            altura.getNumero(),
            selectedPosiciones));
  }

  /**
   * Genera e imprime la etiqueta de una única posición seleccionada.
   *
   * @param posicion Posición seleccionada
   */
  private void abrirPosicion(Posicion posicion) {
    List<Posicion> singlePosiciones = new ArrayList<>();
    singlePosiciones.add(posicion);

    labelGenerationService.genericPrint(
        singlePosiciones,
        "Por favor selecciona al menos una posición para imprimir etiquetas.",
        p -> labelGenerationService.generateLabelsForPosiciones(
            pasillo.getNumero(), estanteria.getNumero(), altura.getNumero(), singlePosiciones));
  }

  /** Vuelve a la vista de selección de alturas. */
  private void volverAtras() {
    SeleccionAlturaEtiquetasView view = new SeleccionAlturaEtiquetasView(primaryStage, pasillo, estanteria, almacen);
    view.show();
  }

  /** Muestra la vista de selección de posiciones en el escenario principal. */
  public void show() {
    StageUtils.showView(
        primaryStage,
        this,
        "Seleccionar Posiciones - "
            + almacen.getNombre()
            + " - Pasillo "
            + pasillo.getNumero()
            + " - Estantería "
            + estanteria.getNumero()
            + " - Altura "
            + altura.getNumero());
  }
}

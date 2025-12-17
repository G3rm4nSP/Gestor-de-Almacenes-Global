package com.openwarehouses.views.edicion;

import com.openwarehouses.controllers.PasilloController;
import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Pasillo;
import com.openwarehouses.services.StorageService;
import com.openwarehouses.services.ValidationService;
import com.openwarehouses.utils.DialogUtils;
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
 * Vista para la gestión de pasillos de un almacén.
 *
 * <p>
 * Permite crear, editar y eliminar pasillos asociados a un almacén concreto,
 * así como navegar a
 * la gestión de las estanterías de cada pasillo.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class EdicionPasilloView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Almacén al que pertenecen los pasillos gestionados en la vista. */
  private final Almacen almacen;

  /** Controlador encargado de la lógica de negocio de los pasillos. */
  private final PasilloController controller;

  /** Contenedor gráfico donde se muestran los botones de pasillos. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen pasillos disponibles. */
  private Label emptyMessage;

  /** Lista de pasillos seleccionados en la vista. */
  private List<Pasillo> selectedPasillos = new ArrayList<>();

  /** Lista de almacenes disponibles en el sistema. */
  private List<Almacen> almacenes;

  /** Servicio de persistencia de datos. */
  private StorageService storageService;

  /**
   * Constructor de la vista de edición de pasillos.
   *
   * @param primaryStage   escenario principal de la aplicación
   * @param almacen        almacén cuyos pasillos se gestionan
   * @param almacenes      lista de almacenes disponibles
   * @param storageService servicio de persistencia
   */
  public EdicionPasilloView(
      Stage primaryStage, Almacen almacen, List<Almacen> almacenes, StorageService storageService) {

    this.primaryStage = primaryStage;
    this.almacen = almacen;
    this.almacenes = almacenes;
    this.storageService = storageService;
    this.controller = new PasilloController(almacen, almacenes, storageService);

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
   * Crea el pie de página con las acciones de creación, edición y eliminación.
   */
  private void crearFooter() {
    FooterFactory.createEditFooter(
        this,
        this::handleCrearPasillo,
        this::handleEditarPasillo,
        this::handleEliminarPasillos,
        selectedPasillos,
        "Debes seleccionar exactamente un pasillo para editar.",
        "Debes seleccionar al menos un pasillo para eliminar.");
  }

  /**
   * Gestiona la creación de nuevos pasillos.
   *
   * <p>
   * Solicita al usuario un número válido y crea el pasillo si la validación es
   * correcta.
   */
  private void handleCrearPasillo() {
    Integer res = DialogUtils.createRange(
        "Crear Pasillos",
        "Ingrese el número del pasillo",
        "pasillo",
        almacen,
        ValidationService::isPasilloNumeroValid,
        (n, a) -> controller.crearPasillo(n));

    if (res != null) {
      cargarPasillos();
    }
  }

  /**
   * Gestiona la edición de un pasillo existente.
   *
   * @param pasillo pasillo a editar
   * @return {@code true} si la edición se realizó correctamente
   */
  private Boolean handleEditarPasillo(Pasillo pasillo) {
    return DialogUtils.edit(
        "Editar pasillo",
        "Nuevo número del pasillo:",
        String.valueOf(pasillo.getNumero()),
        Integer::parseInt,
        nuevoNumero -> controller.editarPasillo(pasillo, nuevoNumero),
        () -> {
          selectedPasillos.clear();
          cargarPasillos();
        },
        "El número no es válido o ya existe.");
  }

  /**
   * Gestiona la eliminación de uno o varios pasillos seleccionados.
   *
   * @param pasillos lista de pasillos a eliminar
   * @return {@code true} si la eliminación se completó correctamente
   */
  private Boolean handleEliminarPasillos(List<Pasillo> pasillos) {
    return DialogUtils.confirmDelete(
        "Confirmar eliminación",
        "Vas a eliminar " + pasillos.size() + " pasillo(s).",
        pasillos,
        controller::eliminarPasillo,
        () -> {
          selectedPasillos.clear();
          cargarPasillos();
        });
  }

  /** Vuelve a la vista de edición de almacenes. */
  private void volverAtras() {
    EdicionAlmacenView view = new EdicionAlmacenView(primaryStage);
    view.show();
  }

  /**
   * Abre la vista de edición de estanterías del pasillo indicado.
   *
   * @param pasillo pasillo seleccionado
   */
  private void abrirEstanterias(Pasillo pasillo) {
    EdicionEstanteriaView view = new EdicionEstanteriaView(primaryStage, pasillo, almacen, almacenes, storageService);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(primaryStage, this, "Gestión de Pasillos - Almacén " + almacen.getNombre());
  }
}

package com.openwarehouses.views.edicion;

import java.util.ArrayList;
import java.util.List;

import com.openwarehouses.controllers.AlmacenController;
import com.openwarehouses.models.Almacen;
import com.openwarehouses.services.StorageService;
import com.openwarehouses.utils.DialogUtils;
import com.openwarehouses.utils.FooterFactory;
import com.openwarehouses.utils.GridLoader;
import com.openwarehouses.utils.HeaderUtils;
import com.openwarehouses.utils.HierarchyButtonFactory;
import com.openwarehouses.utils.StageUtils;
import com.openwarehouses.utils.HeaderUtils.HeaderMode;
import com.openwarehouses.views.InicioView;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * Vista para la gestión de almacenes.
 *
 * <p>
 * Permite crear, editar y eliminar almacenes existentes, así como navegar a la
 * edición de los
 * pasillos asociados a un almacén concreto.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class EdicionAlmacenView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Controlador encargado de la lógica de negocio de los almacenes. */
  private final AlmacenController controller;

  /** Contenedor gráfico donde se muestran los botones de almacenes. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen almacenes disponibles. */
  private Label emptyMessage;

  /** Servicio de persistencia de almacenes. */
  private final StorageService storageService;

  /** Lista completa de almacenes cargados desde almacenamiento. */
  private final List<Almacen> almacenes;

  /** Lista de almacenes seleccionados en la vista. */
  private List<Almacen> selectedAlmacenes = new ArrayList<>();

  /**
   * Constructor de la vista de edición de almacenes.
   *
   * @param primaryStage escenario principal de la aplicación
   */
  public EdicionAlmacenView(Stage primaryStage) {
    this.storageService = new StorageService();
    this.almacenes = storageService.loadAlmacenes();
    this.primaryStage = primaryStage;
    this.controller = new AlmacenController(storageService, almacenes);

    crearHeader();
    crearGridAlmacenes();
    crearFooter();
    cargarAlmacenes();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea y configura el encabezado de la vista. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, this::volverAtras, HeaderMode.EDITION);
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

  /**
   * Crea un botón asociado a un almacén concreto.
   *
   * @param almacen almacén a representar
   * @return botón configurado para el almacén
   */
  private Button crearBotonAlmacen(Almacen almacen) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        almacen.getNombre(), almacen, selectedAlmacenes, a -> abrirPasillos(almacen));
  }

  /**
   * Crea el pie de página con las acciones de creación, edición y eliminación.
   */
  private void crearFooter() {
    FooterFactory.createEditFooter(
        this,
        this::handleCrearAlmacen,
        this::handleEditarAlmacen,
        this::handleEliminarAlmacenes,
        selectedAlmacenes,
        "Debes seleccionar exactamente un almacén para editar.",
        "Debes seleccionar al menos un almacén para eliminar.");
  }

  /**
   * Gestiona la creación de un nuevo almacén.
   *
   * <p>
   * Solicita al usuario un nombre válido y, si la validación es correcta, crea el
   * almacén y
   * recarga la vista.
   */
  private void handleCrearAlmacen() {
    DialogUtils.createTextValidated(
        "Crear Nuevo Almacén",
        "Ingrese el nombre del almacén",
        "Nombre del almacén",
        nombre -> !nombre.isBlank() && controller.getAlmacenByNombre(nombre) == null,
        "El nombre es obligatorio y no puede repetirse")
        .ifPresent(
            nombre -> {
              controller.crearAlmacen(nombre);
              cargarAlmacenes();
            });
  }

  /**
   * Gestiona la edición de un almacén existente.
   *
   * @param almacen almacén a editar
   * @return {@code true} si la edición se realizó correctamente
   */
  private Boolean handleEditarAlmacen(Almacen almacen) {
    return DialogUtils.edit(
        "Editar almacén",
        "Nuevo nombre del almacén:",
        almacen.getNombre(),
        nombre -> nombre,
        nuevoNombre -> controller.editarAlmacen(almacen, nuevoNombre),
        () -> {
          selectedAlmacenes.clear();
          cargarAlmacenes();
        },
        "El nombre no es válido o ya existe.");
  }

  /**
   * Gestiona la eliminación de uno o varios almacenes seleccionados.
   *
   * @param almacens lista de almacenes a eliminar
   * @return {@code true} si la eliminación se completó correctamente
   */
  private Boolean handleEliminarAlmacenes(List<Almacen> almacens) {
    return DialogUtils.confirmDelete(
        "Confirmar eliminación",
        "Vas a eliminar " + almacens.size() + " almacén(es).",
        almacens,
        controller::eliminarAlmacen,
        () -> {
          selectedAlmacenes.clear();
          cargarAlmacenes();
        });
  }

  /** Vuelve a la vista de inicio de la aplicación. */
  private void volverAtras() {
    InicioView view = new InicioView(primaryStage);
    view.show();
  }

  /**
   * Abre la vista de edición de pasillos del almacén indicado.
   *
   * @param almacen almacén seleccionado
   */
  private void abrirPasillos(Almacen almacen) {
    EdicionPasilloView view = new EdicionPasilloView(primaryStage, almacen, almacenes, storageService);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(primaryStage, this, "Gestión de Almacenes");
  }
}

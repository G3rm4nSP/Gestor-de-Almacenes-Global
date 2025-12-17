package com.openwarehouses.views.edicion;

import com.openwarehouses.controllers.EstanteriaController;
import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Estanteria;
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
 * Vista para la gestión de estanterías de un pasillo.
 *
 * <p>
 * Permite crear, editar y eliminar estanterías asociadas a un pasillo concreto,
 * así como navegar
 * a la gestión de las alturas de cada estantería.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class EdicionEstanteriaView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Almacén al que pertenece el pasillo gestionado. */
  private final Almacen almacen;

  /** Pasillo al que pertenecen las estanterías gestionadas. */
  private final Pasillo pasillo;

  /** Controlador encargado de la lógica de negocio de las estanterías. */
  private final EstanteriaController controller;

  /** Contenedor gráfico donde se muestran los botones de estanterías. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen estanterías disponibles. */
  private Label emptyMessage;

  /** Lista de estanterías seleccionadas en la vista. */
  private List<Estanteria> selectedEstanterias = new ArrayList<>();

  /** Lista de almacenes disponibles en el sistema. */
  private List<Almacen> almacenes;

  /** Servicio de persistencia de datos. */
  private StorageService storageService;

  /**
   * Constructor de la vista de edición de estanterías.
   *
   * @param primaryStage   escenario principal de la aplicación
   * @param pasillo        pasillo cuyas estanterías se gestionan
   * @param almacen        almacén al que pertenece el pasillo
   * @param almacenes      lista de almacenes disponibles
   * @param storageService servicio de persistencia
   */
  public EdicionEstanteriaView(
      Stage primaryStage,
      Pasillo pasillo,
      Almacen almacen,
      List<Almacen> almacenes,
      StorageService storageService) {

    this.primaryStage = primaryStage;
    this.pasillo = pasillo;
    this.almacen = almacen;
    this.almacenes = almacenes;
    this.storageService = storageService;
    this.controller = new EstanteriaController(pasillo, almacenes, storageService);

    crearHeader();
    crearGridEstanterias();
    crearFooter();
    cargarEstanterias();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea y configura el encabezado de la vista. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, this::volverAtras);
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

  /**
   * Crea un botón asociado a una estantería concreta.
   *
   * @param estanteria estantería a representar
   * @return botón configurado para la estantería
   */
  private Button crearBotonEstanteria(Estanteria estanteria) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        "Estantería " + estanteria.getNumero(),
        estanteria,
        selectedEstanterias,
        e -> abrirAlturas(e));
  }

  /**
   * Crea el pie de página con las acciones de creación, edición y eliminación.
   */
  private void crearFooter() {
    FooterFactory.createEditFooter(
        this,
        this::handleCrearEstanteria,
        this::handleEditarEstanteria,
        this::handleEliminarEstanterias,
        selectedEstanterias,
        "Debes seleccionar exactamente una estantería para editar.",
        "Debes seleccionar al menos una estantería para eliminar.");
  }

  /**
   * Gestiona la creación de nuevas estanterías.
   *
   * <p>
   * Solicita al usuario un número válido y crea la estantería si la validación es
   * correcta.
   */
  private void handleCrearEstanteria() {
    Integer res = DialogUtils.createRange(
        "Crear Estanterías",
        "Ingrese el número de la estantería",
        "estantería",
        pasillo,
        ValidationService::isEstanteriaNumeroValid,
        (n, p) -> controller.crearEstanteria(n));

    if (res != null) {
      cargarEstanterias();
    }
  }

  /**
   * Gestiona la edición de una estantería existente.
   *
   * @param estanteria estantería a editar
   * @return {@code true} si la edición se realizó correctamente
   */
  private Boolean handleEditarEstanteria(Estanteria estanteria) {
    return DialogUtils.edit(
        "Editar estantería",
        "Nuevo número de la estantería:",
        String.valueOf(estanteria.getNumero()),
        Integer::parseInt,
        nuevoNumero -> controller.editarEstanteria(estanteria, nuevoNumero),
        () -> {
          selectedEstanterias.clear();
          cargarEstanterias();
        },
        "El número no es válido o ya existe.");
  }

  /**
   * Gestiona la eliminación de una o varias estanterías seleccionadas.
   *
   * @param estanterias lista de estanterías a eliminar
   * @return {@code true} si la eliminación se completó correctamente
   */
  private Boolean handleEliminarEstanterias(List<Estanteria> estanterias) {
    return DialogUtils.confirmDelete(
        "Confirmar eliminación",
        "Vas a eliminar " + estanterias.size() + " estantería(s).",
        estanterias,
        controller::eliminarEstanteria,
        () -> {
          selectedEstanterias.clear();
          cargarEstanterias();
        });
  }

  /** Vuelve a la vista de edición de pasillos. */
  private void volverAtras() {
    EdicionPasilloView view = new EdicionPasilloView(primaryStage, almacen, almacenes, storageService);
    view.show();
  }

  /**
   * Abre la vista de edición de alturas de la estantería indicada.
   *
   * @param estanteria estantería seleccionada
   */
  private void abrirAlturas(Estanteria estanteria) {
    EdicionAlturaView view = new EdicionAlturaView(
        primaryStage, estanteria, pasillo, almacen, almacenes, storageService);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(
        primaryStage,
        this,
        "Gestión de Estanterías - Almacen "
            + almacen.getNombre()
            + " - Pasillo "
            + pasillo.getNumero());
  }
}

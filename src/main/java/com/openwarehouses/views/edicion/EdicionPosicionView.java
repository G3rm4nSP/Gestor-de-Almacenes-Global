package com.openwarehouses.views.edicion;

import com.openwarehouses.controllers.PosicionController;
import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Altura;
import com.openwarehouses.models.Estanteria;
import com.openwarehouses.models.Pasillo;
import com.openwarehouses.models.Posicion;
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
 * Vista para la gestión de posiciones.
 *
 * <p>
 * Permite crear, editar y eliminar posiciones asociadas a una altura concreta.
 * Esta vista
 * representa el último nivel de la jerarquía del almacén.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class EdicionPosicionView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Almacén al que pertenece la posición. */
  private final Almacen almacen;

  /** Pasillo al que pertenece la posición. */
  private final Pasillo pasillo;

  /** Estantería al que pertenece la posición. */
  private final Estanteria estanteria;

  /** Altura al que pertenece la posición. */
  private final Altura altura;

  /** Controlador encargado de la lógica de negocio de las posiciones. */
  private final PosicionController controller;

  /** Contenedor gráfico donde se muestran las posiciones. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen posiciones. */
  private Label emptyMessage;

  /** Lista de posiciones seleccionadas por el usuario. */
  private List<Posicion> selectedPosiciones = new ArrayList<>();

  /** Lista de almacenes disponibles en el sistema. */
  private List<Almacen> almacenes;

  /** Servicio de persistencia. */
  private StorageService storageService;

  /**
   * Constructor de la vista de edición de posiciones.
   *
   * @param primaryStage   escenario principal
   * @param altura         altura actual
   * @param estanteria     estantería actual
   * @param pasillo        pasillo actual
   * @param almacen        almacén actual
   * @param almacenes      lista de almacenes disponibles
   * @param storageService servicio de persistencia
   */
  public EdicionPosicionView(
      Stage primaryStage,
      Altura altura,
      Estanteria estanteria,
      Pasillo pasillo,
      Almacen almacen,
      List<Almacen> almacenes,
      StorageService storageService) {

    this.primaryStage = primaryStage;
    this.altura = altura;
    this.estanteria = estanteria;
    this.pasillo = pasillo;
    this.almacen = almacen;
    this.almacenes = almacenes;
    this.storageService = storageService;
    this.controller = new PosicionController(altura, almacenes, storageService);

    controller.setAlturaActual(
        altura, pasillo.getNumero(), estanteria.getNumero(), altura.getNumero());

    crearHeader();
    crearGridPosiciones();
    crearFooter();
    cargarPosiciones();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea el encabezado de la vista. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, this::volverAtras);
  }

  /** Inicializa el grid de posiciones. */
  private void crearGridPosiciones() {
    emptyMessage = new Label("No hay posiciones disponibles");
    buttonGrid = GridLoader.createStandardGrid(this, emptyMessage);
  }

  /** Carga las posiciones asociadas a la altura. */
  private void cargarPosiciones() {
    GridLoader.<Posicion>load(
        buttonGrid, altura.getPosiciones(), emptyMessage, this::crearBotonPosicion);
  }

  /**
   * Crea un botón representativo de una posición.
   *
   * @param posicion posición a representar
   * @return botón configurado
   */
  private Button crearBotonPosicion(Posicion posicion) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        "Posición " + posicion.getCodigo(), posicion, selectedPosiciones, null);
  }

  /** Crea el pie de página con acciones de edición. */
  private void crearFooter() {
    FooterFactory.createEditFooter(
        this,
        this::handleCrearPosicion,
        this::handleEditarPosicion,
        this::handleEliminarPosiciones,
        selectedPosiciones,
        "Debes seleccionar exactamente una posición para editar.",
        "Debes seleccionar al menos una posición para eliminar.");
  }

  /** Gestiona la creación de nuevas posiciones. */
  private void handleCrearPosicion() {
    Integer res = DialogUtils.createRange(
        "Crear Nueva Posición",
        "Ingrese el número de la posición",
        "posición",
        altura,
        ValidationService::isPosicionNumeroValid,
        (n, a) -> controller.crearPosicion(n));

    if (res != null) {
      cargarPosiciones();
    }
  }

  /**
   * Gestiona la edición de una posición existente.
   *
   * @param posicion posición a editar
   * @return {@code true} si la edición fue correcta
   */
  private Boolean handleEditarPosicion(Posicion posicion) {
    return DialogUtils.edit(
        "Editar posición",
        "Nuevo número de la posición:",
        String.valueOf(altura.getNumero()),
        Integer::parseInt,
        nuevoNumero -> controller.editarPosicion(
            altura.getPosicionByNumero(posicion.getNumero()), nuevoNumero),
        () -> {
          selectedPosiciones.clear();
          cargarPosiciones();
        },
        "El número no es válido o ya existe.");
  }

  /**
   * Gestiona la eliminación de posiciones seleccionadas.
   *
   * @param posiciones posiciones a eliminar
   * @return {@code true} si la eliminación fue confirmada
   */
  private Boolean handleEliminarPosiciones(List<Posicion> posiciones) {
    return DialogUtils.confirmDelete(
        "Confirmar eliminación",
        "Vas a eliminar " + posiciones.size() + " posición(es).",
        posiciones,
        controller::eliminarPosicion,
        () -> {
          selectedPosiciones.clear();
          cargarPosiciones();
        });
  }

  /** Vuelve a la vista de edición de alturas. */
  private void volverAtras() {
    EdicionAlturaView view = new EdicionAlturaView(
        primaryStage, estanteria, pasillo, almacen, almacenes, storageService);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(
        primaryStage,
        this,
        "Gestión de Posiciones - Almacen "
            + almacen.getNombre()
            + " - Pasillo "
            + pasillo.getNumero()
            + " - Estanteria "
            + estanteria.getNumero()
            + " - Altura "
            + altura.getNumero());
  }
}

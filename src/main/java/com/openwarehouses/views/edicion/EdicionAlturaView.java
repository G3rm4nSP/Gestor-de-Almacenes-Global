package com.openwarehouses.views.edicion;

import java.util.ArrayList;
import java.util.List;

import com.openwarehouses.controllers.AlturaController;
import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Altura;
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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * Vista para la gestión de alturas de una estantería.
 *
 * <p>
 * Permite crear, editar y eliminar alturas asociadas a una estantería concreta,
 * así como navegar
 * a la gestión de posiciones.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class EdicionAlturaView extends BorderPane {

  /** Escenario principal de la aplicación. */
  private final Stage primaryStage;

  /** Almacén al que pertenece la estantería. */
  private final Almacen almacen;

  /** Pasillo al que pertenece la estantería. */
  private final Pasillo pasillo;

  /** Estantería cuyas alturas se gestionan. */
  private final Estanteria estanteria;

  /** Controlador encargado de la lógica de negocio de las alturas. */
  private final AlturaController controller;

  /** Contenedor gráfico donde se muestran los botones de alturas. */
  private FlowPane buttonGrid;

  /** Mensaje mostrado cuando no existen alturas disponibles. */
  private Label emptyMessage;

  /** Lista de alturas seleccionadas en la vista. */
  private List<Altura> selectedAlturas = new ArrayList<>();

  /** Lista de almacenes disponibles en el sistema. */
  private List<Almacen> almacenes;

  /** Servicio de persistencia de datos. */
  private StorageService storageService;

  /**
   * Constructor de la vista de edición de alturas.
   *
   * @param primaryStage   escenario principal de la aplicación
   * @param estanteria     estantería cuyas alturas se gestionan
   * @param pasillo        pasillo al que pertenece la estantería
   * @param almacen        almacén al que pertenece la estantería
   * @param almacenes      lista de almacenes disponibles
   * @param storageService servicio de persistencia
   */
  public EdicionAlturaView(
      Stage primaryStage,
      Estanteria estanteria,
      Pasillo pasillo,
      Almacen almacen,
      List<Almacen> almacenes,
      StorageService storageService) {

    this.primaryStage = primaryStage;
    this.estanteria = estanteria;
    this.pasillo = pasillo;
    this.almacen = almacen;
    this.almacenes = almacenes;
    this.storageService = storageService;
    this.controller = new AlturaController(estanteria, almacenes, storageService);

    crearHeader();
    crearGridAlturas();
    crearFooter();
    cargarAlturas();

    this.setStyle("-fx-background-color: #ecf0f1;");
  }

  /** Crea y configura el encabezado de la vista. */
  private void crearHeader() {
    HeaderUtils.createHeader(this, primaryStage, this::volverAtras);
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

  /**
   * Crea un botón asociado a una altura concreta.
   *
   * @param altura altura a representar
   * @return botón configurado para la altura
   */
  private Button crearBotonAltura(Altura altura) {
    return HierarchyButtonFactory.crearBotonJerarquia(
        "Altura " + altura.getNumero(), altura, selectedAlturas, this::abrirPosiciones);
  }

  /**
   * Crea el pie de página con las acciones de creación, edición y eliminación.
   */
  private void crearFooter() {
    FooterFactory.createEditFooter(
        this,
        this::handleCrearAltura,
        this::handleEditarAltura,
        this::handleEliminarAlturas,
        selectedAlturas,
        "Debes seleccionar exactamente una altura para editar.",
        "Debes seleccionar al menos una altura para eliminar.");
  }

  /** Gestiona la creación de nuevas alturas. */
  private void handleCrearAltura() {
    Integer res = DialogUtils.createRange(
        "Crear Alturas",
        "Ingrese el número de la altura",
        "altura",
        estanteria,
        ValidationService::isAlturaNumeroValid,
        (n, e) -> controller.crearAltura(n));

    if (res != null) {
      cargarAlturas();
    }
  }

  /**
   * Gestiona la edición de una altura existente.
   *
   * @param altura altura a editar
   * @return {@code true} si la edición se realizó correctamente
   */
  private Boolean handleEditarAltura(Altura altura) {
    return DialogUtils.edit(
        "Editar altura",
        "Nuevo número de la altura:",
        String.valueOf(altura.getNumero()),
        Integer::parseInt,
        nuevoNumero -> controller.editarAltura(altura, nuevoNumero),
        () -> {
          selectedAlturas.clear();
          cargarAlturas();
        },
        "El número no es válido o ya existe.");
  }

  /**
   * Gestiona la eliminación de una o varias alturas seleccionadas.
   *
   * @param alturas lista de alturas a eliminar
   * @return {@code true} si la eliminación se completó correctamente
   */
  private Boolean handleEliminarAlturas(List<Altura> alturas) {
    return DialogUtils.confirmDelete(
        "Confirmar eliminación",
        "Vas a eliminar " + alturas.size() + " altura(s).",
        alturas,
        controller::eliminarAltura,
        () -> {
          selectedAlturas.clear();
          cargarAlturas();
        });
  }

  /** Vuelve a la vista de edición de estanterías. */
  private void volverAtras() {
    EdicionEstanteriaView view = new EdicionEstanteriaView(primaryStage, pasillo, almacen, almacenes, storageService);
    view.show();
  }

  /**
   * Abre la vista de edición de posiciones de la altura indicada.
   *
   * @param altura altura seleccionada
   */
  private void abrirPosiciones(Altura altura) {
    EdicionPosicionView view = new EdicionPosicionView(
        primaryStage, altura, estanteria, pasillo, almacen, almacenes, storageService);
    view.show();
  }

  /** Muestra la vista en el escenario principal. */
  public void show() {
    StageUtils.showView(
        primaryStage,
        this,
        "Gestión de Alturas - Estantería "
            + estanteria.getNumero()
            + " - Pasillo "
            + pasillo.getNumero()
            + " - Estanteria "
            + estanteria.getNumero());
  }
}

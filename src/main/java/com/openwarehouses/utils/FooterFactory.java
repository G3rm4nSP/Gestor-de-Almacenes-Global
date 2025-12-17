package com.openwarehouses.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Fábrica de footers reutilizables para las vistas.
 *
 * <p>
 * Permite crear footers estándar con botones de acción como:
 * <ul>
 * <li>Crear / Editar / Eliminar</li>
 * <li>Imprimir etiquetas</li>
 * </ul>
 *
 * <p>
 * Los footers se añaden directamente al {@link BorderPane} de la vista.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class FooterFactory {

  /** Padding horizontal estándar para los HBox del footer. */
  private static int hBoxNum = 15;

  /**
   * Constructor privado para evitar instanciación.
   */
  private FooterFactory() {
  }

  /**
   * Crea un footer con un botón para imprimir etiquetas y lo añade al
   * {@link BorderPane}.
   *
   * @param parent      BorderPane donde se añadirá el footer
   * @param printAction acción que se ejecuta al pulsar el botón de imprimir
   * @return el {@link HBox} que contiene el footer creado
   */
  public static HBox createPrintFooter(BorderPane parent, Runnable printAction) {
    Button botonImprimir = new Button("IMPRIMIR ETIQUETAS");
    botonImprimir.setStyle(
        "-fx-padding: 15; -fx-font-size: 18; -fx-background-color: #27ae60; -fx-text-fill: white;");

    botonImprimir.setOnAction(e -> printAction.run());

    HBox footer = new HBox(botonImprimir);
    footer.setPadding(new Insets(hBoxNum));
    footer.setAlignment(Pos.CENTER);

    parent.setBottom(footer);

    return footer;
  }

  /**
   * Crea un footer con botones para crear, editar y eliminar elementos,
   * gestionando
   * automáticamente las restricciones de selección y mostrando alertas si no se
   * cumple.
   *
   * <p>
   * El botón de editar requiere que haya exactamente un elemento seleccionado.
   * El botón de eliminar requiere que haya al menos un elemento seleccionado.
   *
   * @param parent              BorderPane donde se añadirá el footer
   * @param onCrear             acción que se ejecuta al pulsar el botón "CREAR"
   * @param onEditar            acción que se ejecuta al pulsar el botón "EDITAR",
   *                            recibe el elemento seleccionado
   * @param onEliminar          acción que se ejecuta al pulsar el botón
   *                            "ELIMINAR", recibe la lista de elementos
   *                            seleccionados
   * @param selectedItems       lista de elementos seleccionados, usada para
   *                            habilitar o mostrar alertas
   * @param singleSelectWarning mensaje de advertencia si no hay exactamente un
   *                            elemento seleccionado al editar
   * @param multiSelectWarning  mensaje de advertencia si no hay elementos
   *                            seleccionados al eliminar
   * @param <T>                 tipo de los elementos gestionados
   * @return el {@link HBox} que contiene el footer creado
   */
  public static <T> HBox createEditFooter(
      BorderPane parent,
      Runnable onCrear,
      Function<T, Boolean> onEditar,
      Function<List<T>, Boolean> onEliminar,
      List<T> selectedItems,
      String singleSelectWarning,
      String multiSelectWarning) {

    Button btnCrear = new Button("CREAR");
    Button btnEditar = new Button("EDITAR");
    Button btnEliminar = new Button("ELIMINAR");

    styleButton(btnCrear, "#27ae60");
    styleButton(btnEditar, "#e67e22");
    styleButton(btnEliminar, "#c0392b");

    // ACCIONES
    btnCrear.setOnAction(e -> onCrear.run());

    btnEditar.setOnAction(
        e -> {
          if (selectedItems.size() != 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selección inválida");
            alert.setHeaderText(null);
            alert.setContentText(singleSelectWarning);
            alert.showAndWait();
            return;
          }
          onEditar.apply(selectedItems.get(0));
        });

    btnEliminar.setOnAction(
        e -> {
          if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selección inválida");
            alert.setHeaderText(null);
            alert.setContentText(multiSelectWarning);
            alert.showAndWait();
            return;
          }
          onEliminar.apply(new ArrayList<>(selectedItems));
        });

    HBox footerBox = new HBox(hBoxNum, btnCrear, btnEditar, btnEliminar);
    footerBox.setStyle("-fx-padding: 15; -fx-background-color: #ecf0f1;");
    parent.setBottom(footerBox);

    return footerBox;
  }

  /**
   * Aplica estilo estándar a un {@link Button} del footer.
   *
   * @param btn   botón a estilizar
   * @param color color de fondo en formato hexadecimal (ej. "#27ae60")
   */
  private static void styleButton(Button btn, String color) {
    btn.setStyle(
        "-fx-background-color: "
            + color
            + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-min-width: 150;");
  }
}

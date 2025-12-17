package com.openwarehouses.utils;

import java.util.List;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Utilidad para cargar elementos en un contenedor tipo grid de manera genérica.
 *
 * <p>
 * Proporciona métodos para:
 * <ul>
 * <li>Cargar elementos en un {@link Pane} usando una función que genere los
 * nodos.</li>
 * <li>Crear un {@link FlowPane} estándar con scroll, estilo homogéneo y mensaje
 * vacío.</li>
 * </ul>
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public final class GridLoader {

  /** Espaciado horizontal y vertical estándar para los grids. */
  private static int gap = 20;

  /** Constructor privado para evitar instanciación. */
  private GridLoader() {
  }

  /**
   * Carga elementos en un contenedor genérico y crea un nodo para cada elemento
   * usando la función
   * proporcionada.
   *
   * <p>
   * Si la lista de elementos está vacía o es nula, se añade el nodo
   * {@code emptyMessage}.
   *
   * @param container    contenedor donde se agregarán los elementos
   * @param items        lista de elementos a cargar (por ejemplo,
   *                     {@code Almacen}, {@code Pasillo}, etc.)
   * @param emptyMessage nodo que se mostrará si no hay elementos
   * @param creator      función que convierte un elemento {@code T} en un nodo
   *                     (por ejemplo, un botón)
   * @param <T>          tipo de los elementos
   */
  public static <T> void load(
      Pane container, List<T> items, Node emptyMessage, Function<T, Node> creator) {
    container.getChildren().clear();

    if (items == null || items.isEmpty()) {
      container.getChildren().add(emptyMessage);
      return;
    }

    items.forEach(item -> container.getChildren().add(creator.apply(item)));
  }

  /**
   * Crea un grid estándar con scroll dentro de un {@link BorderPane}, con estilo
   * homogéneo y
   * mensaje para cuando no haya elementos.
   *
   * <p>
   * El grid se organiza como un {@link FlowPane} con espaciado predefinido y
   * alineación al
   * inicio. Se envuelve en un {@link ScrollPane} para permitir scroll vertical si
   * hay muchos
   * elementos.
   *
   * @param parent       {@link BorderPane} donde se añadirá el grid
   * @param emptyMessage {@link Label} que se mostrará si no hay elementos
   * @return el {@link FlowPane} que actuará como grid de elementos (botones,
   *         nodos, etc.)
   */
  public static FlowPane createStandardGrid(BorderPane parent, Label emptyMessage) {

    // Crear grid
    FlowPane buttonGrid = new FlowPane();
    buttonGrid.setHgap(gap);
    buttonGrid.setVgap(gap);
    buttonGrid.setPadding(new Insets(gap));
    buttonGrid.setAlignment(Pos.TOP_LEFT);

    // Estilo del mensaje vacío
    emptyMessage.setStyle("-fx-font-size: 24; -fx-text-fill: #7f8c8d;");

    // Scroll contenedor
    ScrollPane scroll = new ScrollPane(buttonGrid);
    scroll.setFitToWidth(true);

    VBox wrapper = new VBox(scroll);
    wrapper.setStyle("-fx-background-color: #ecf0f1;");

    parent.setCenter(wrapper);

    return buttonGrid;
  }
}

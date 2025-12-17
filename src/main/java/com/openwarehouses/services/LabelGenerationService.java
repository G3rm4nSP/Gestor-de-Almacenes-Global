package com.openwarehouses.services;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Altura;
import com.openwarehouses.models.Estanteria;
import com.openwarehouses.models.Label;
import com.openwarehouses.models.Pasillo;
import com.openwarehouses.models.Posicion;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javafx.scene.control.Alert;

/**
 * Servicio para generar etiquetas de posiciones según el nivel de navegación.
 * Permite crear
 * etiquetas para impresión desde cualquier punto de la jerarquía. Cada método
 * genera etiquetas para
 * un nivel específico (almacén, pasillo, estantería, altura, posición). También
 * incluye un método
 * genérico para imprimir etiquetas basadas en una lista de elementos
 * seleccionados.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class LabelGenerationService {

  /**
   * Genera etiquetas para todas las posiciones de un almacén.
   *
   * @param almacen Almacén del cual generar etiquetas
   * @return Lista de etiquetas de todas las posiciones del almacén
   */
  public List<Label> generateLabelsForAlmacen(Almacen almacen) {
    List<Label> labels = new ArrayList<>();
    for (Pasillo pasillo : almacen.getPasillos()) {
      labels.addAll(generateLabelsForPasillo(pasillo));
    }
    return labels;
  }

  /**
   * Genera etiquetas para todas las posiciones de una lista de pasillos.
   *
   * @param pasillos Lista de pasillos
   * @return Lista de etiquetas de todas las posiciones de los pasillos
   */
  public List<Label> generateLabelsForPasillos(List<Pasillo> pasillos) {
    List<Label> labels = new ArrayList<>();
    for (Pasillo pasillo : pasillos) {
      labels.addAll(generateLabelsForPasillo(pasillo));
    }
    return labels;
  }

  /**
   * Genera etiquetas para todas las posiciones de un pasillo.
   *
   * @param pasillo Pasillo del cual generar etiquetas
   * @return Lista de etiquetas de todas las posiciones del pasillo
   */
  public List<Label> generateLabelsForPasillo(Pasillo pasillo) {
    List<Label> labels = new ArrayList<>();
    for (Estanteria estanteria : pasillo.getEstanterias()) {
      labels.addAll(generateLabelsForEstanteria(pasillo.getNumero(), estanteria));
    }
    return labels;
  }

  /**
   * Genera etiquetas para todas las posiciones de una lista de estanterías.
   *
   * @param numeroPasillo Número del pasillo
   * @param estanterias   Lista de estanterías
   * @return Lista de etiquetas de todas las posiciones de las estanterías
   */
  public List<Label> generateLabelsForEstanterias(int numeroPasillo, List<Estanteria> estanterias) {
    List<Label> labels = new ArrayList<>();
    for (Estanteria estanteria : estanterias) {
      labels.addAll(generateLabelsForEstanteria(numeroPasillo, estanteria));
    }
    return labels;
  }

  /**
   * Genera etiquetas para todas las posiciones de una estantería.
   *
   * @param numeroPasillo Número del pasillo
   * @param estanteria    Estantería del cual generar etiquetas
   * @return Lista de etiquetas de todas las posiciones de la estantería
   */
  public List<Label> generateLabelsForEstanteria(int numeroPasillo, Estanteria estanteria) {
    List<Label> labels = new ArrayList<>();
    for (Altura altura : estanteria.getAlturas()) {
      labels.addAll(generateLabelsForAltura(numeroPasillo, estanteria.getNumero(), altura));
    }
    return labels;
  }

  /**
   * Genera etiquetas para todas las posiciones de una lista de alturas.
   *
   * @param numeroPasillo    Número del pasillo
   * @param numeroEstanteria Número de la estantería
   * @param alturas          Lista de alturas
   * @return Lista de etiquetas de todas las posiciones de las alturas
   */
  public List<Label> generateLabelsForAlturas(
      int numeroPasillo, int numeroEstanteria, List<Altura> alturas) {
    List<Label> labels = new ArrayList<>();
    for (Altura altura : alturas) {
      labels.addAll(generateLabelsForAltura(numeroPasillo, numeroEstanteria, altura));
    }
    return labels;
  }

  /**
   * Genera etiquetas para todas las posiciones de una altura.
   *
   * @param numeroPasillo    Número del pasillo
   * @param numeroEstanteria Número de la estantería
   * @param altura           Altura del cual generar etiquetas
   * @return Lista de etiquetas de todas las posiciones de la altura
   */
  public List<Label> generateLabelsForAltura(
      int numeroPasillo, int numeroEstanteria, Altura altura) {
    List<Label> labels = new ArrayList<>();
    for (Posicion posicion : altura.getPosiciones()) {
      labels.add(
          generateLabelForPosicion(numeroPasillo, numeroEstanteria, altura.getNumero(), posicion));
    }
    return labels;
  }

  /**
   * Genera etiquetas para una lista de posiciones.
   *
   * @param numeroPasillo    Número del pasillo
   * @param numeroEstanteria Número de la estantería
   * @param numeroAltura     Número de la altura
   * @param posiciones       Lista de posiciones
   * @return Lista de etiquetas de las posiciones
   */
  public List<Label> generateLabelsForPosiciones(
      int numeroPasillo, int numeroEstanteria, int numeroAltura, List<Posicion> posiciones) {
    List<Label> labels = new ArrayList<>();
    for (Posicion posicion : posiciones) {
      labels.add(generateLabelForPosicion(numeroPasillo, numeroEstanteria, numeroAltura, posicion));
    }
    return labels;
  }

  /**
   * Genera una etiqueta para una posición individual.
   *
   * @param numeroPasillo    Número del pasillo
   * @param numeroEstanteria Número de la estantería
   * @param numeroAltura     Número de la altura
   * @param posicion         Posición para generar etiqueta
   * @return Etiqueta de la posición
   */
  public Label generateLabelForPosicion(
      int numeroPasillo, int numeroEstanteria, int numeroAltura, Posicion posicion) {
    String codigo = numeroPasillo + "." + numeroEstanteria + "." + numeroAltura + "." + posicion.getNumero();
    return new Label(codigo, numeroPasillo, numeroEstanteria, numeroAltura, posicion.getNumero());
  }

  /**
   * Método genérico para imprimir etiquetas basadas en una lista de elementos
   * seleccionados.
   *
   * @param selectedItems         Lista de elementos seleccionados
   * @param emptySelectionMessage Mensaje a mostrar si no hay elementos
   *                              seleccionados
   * @param labelGenerator        Función que genera etiquetas a partir de un
   *                              elemento
   * @param <T>                   Tipo de los elementos seleccionados
   */
  public <T> void genericPrint(
      List<T> selectedItems,
      String emptySelectionMessage,
      Function<T, List<Label>> labelGenerator) {

    if (selectedItems.isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Sin selección");
      alert.setHeaderText(null);
      alert.setContentText(emptySelectionMessage);
      alert.showAndWait();
      return;
    }

    List<Label> allLabels = new ArrayList<>();

    for (T item : selectedItems) {
      allLabels.addAll(labelGenerator.apply(item));
    }

    if (allLabels.isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Sin posiciones");
      alert.setHeaderText(null);
      alert.setContentText("No se han encontrado posiciones para imprimir.");
      alert.showAndWait();
      return;
    }
  }
}

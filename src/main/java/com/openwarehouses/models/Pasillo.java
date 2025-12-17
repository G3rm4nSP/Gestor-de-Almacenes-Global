package com.openwarehouses.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo que representa un Pasillo dentro de un Almacén. Un Pasillo contiene múltiples Estanterías.
 *
 * @author German
 * @version 1.0
 * @since 2024-12-16
 */
public class Pasillo {
  /** Número del pasillo. */
  private int numero;

  /** Lista de estanterías en el pasillo. */
  private List<Estanteria> estanterias;

  /** Constructor vacío para JSON serialization. */
  public Pasillo() {
    this.estanterias = new ArrayList<>();
  }

  /**
   * Constructor con número.
   *
   * @param numero número del pasillo
   */
  public Pasillo(int numero) {
    this.numero = numero;
    this.estanterias = new ArrayList<>();
  }

  /**
   * Obtiene el número del pasillo.
   *
   * @return número del pasillo
   */
  public int getNumero() {
    return numero;
  }

  /**
   * Establece el número del pasillo.
   *
   * @param numero número del pasillo
   */
  public void setNumero(int numero) {
    this.numero = numero;
  }

  /**
   * Obtiene las estanterías del pasillo.
   *
   * @return lista de estanterías
   */
  public List<Estanteria> getEstanterias() {
    return estanterias;
  }

  /**
   * Establece las estanterías del pasillo.
   *
   * @param estanterias lista de estanterías
   */
  public void setEstanterias(List<Estanteria> estanterias) {
    this.estanterias = estanterias;
  }

  /**
   * Agrega una estantería al pasillo.
   *
   * @param estanteria estantería a agregar
   */
  public void addEstanteria(Estanteria estanteria) {
    if (!estanterias.contains(estanteria)) {
      estanterias.add(estanteria);
    }
  }

  /**
   * Obtiene una estantería por su número.
   *
   * @param num número de la estantería
   * @return estantería encontrada o null si no existe
   */
  public Estanteria getEstanteriaByNumero(int num) {
    return estanterias.stream().filter(e -> e.getNumero() == num).findFirst().orElse(null);
  }

  /**
   * Elimina una estantería del pasillo.
   *
   * @param estanteria estantería a eliminar
   */
  public void removeEstanteria(Estanteria estanteria) {
    estanterias.remove(estanteria);
  }

  @Override
  /**
   * Compara dos objetos Pasillo por su número.
   *
   * @param o objeto a comparar
   * @return true si son iguales, false en caso contrario
   */
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pasillo pasillo = (Pasillo) o;
    return numero == pasillo.numero;
  }

  @Override
  /**
   * Genera un código hash basado en el número del pasillo.
   *
   * @return código hash del pasillo
   */
  public int hashCode() {
    return Objects.hash(numero);
  }

  @Override
  /**
   * Representación en cadena del pasillo.
   *
   * @return cadena representando el pasillo
   */
  public String toString() {
    return "Pasillo " + numero;
  }
}

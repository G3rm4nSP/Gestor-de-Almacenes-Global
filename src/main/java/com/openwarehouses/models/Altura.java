package com.openwarehouses.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo que representa una altura dentro de una estantería. Una altura contiene múltiples
 * posiciones.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */

public class Altura {
  /** Número de la altura. */
  private int numero;

  /** Lista de posiciones en la altura. */
  private List<Posicion> posiciones;

  /** Constructor vacío para JSON serialization. */
  public Altura() {
    this.posiciones = new ArrayList<>();
  }

  /**
   * Constructor con número.
   *
   * @param numero Número de la altura
   */
  public Altura(int numero) {
    this.numero = numero;
    this.posiciones = new ArrayList<>();
  }

  /**
   * Obtiene el número de la altura.
   *
   * @return Número de la altura
   */
  public int getNumero() {
    return numero;
  }

  /**
   * Establece el número de la altura.
   *
   * @param numero Número de la altura
   */
  public void setNumero(int numero) {
    this.numero = numero;
  }

  /**
   * Obtiene las posiciones de la altura.
   *
   * @return Lista de posiciones
   */
  public List<Posicion> getPosiciones() {
    return posiciones;
  }

  /**
   * Establece las posiciones de la altura.
   *
   * @param posiciones Lista de posiciones
   */
  public void setPosiciones(List<Posicion> posiciones) {
    this.posiciones = posiciones;
  }

  /**
   * Añade una posición a la altura.
   *
   * @param posicion Posición a añadir
   */
  public void addPosicion(Posicion posicion) {
    if (!posiciones.contains(posicion)) {
      posiciones.add(posicion);
    }
  }

  /**
   * Obtiene una posición por su número.
   *
   * @param num Número de la posición
   * @return Posición encontrada o null si no existe
   */
  public Posicion getPosicionByNumero(int num) {
    return posiciones.stream().filter(p -> p.getNumero() == num).findFirst().orElse(null);
  }

  /**
   * Elimina una posición de la altura.
   *
   * @param posicion Posición a eliminar
   */
  public void removePosicion(Posicion posicion) {
    posiciones.remove(posicion);
  }

  @Override
  /**
   * Compara dos alturas por su número.
   *
   * @param o Objeto a comparar
   * @return true si son iguales, false en caso contrario
   */
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Altura altura = (Altura) o;
    return numero == altura.numero;
  }

  @Override
  /**
   * Genera un hashcode basado en el número de la altura.
   *
   * @return Hashcode de la altura
   */
  public int hashCode() {
    return Objects.hash(numero);
  }

  @Override
  /**
   * Representación en cadena de la altura.
   *
   * @return Cadena representando la altura
   */
  public String toString() {
    return "Altura " + numero;
  }
}

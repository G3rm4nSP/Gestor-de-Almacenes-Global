package com.openwarehouses.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo que representa una estantería dentro de un pasillo. Una estantería contiene múltiples
 * alturas.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class Estanteria {
  /** Número de la estantería. */
  private int numero;

  /** Lista de alturas en la estantería. */
  private List<Altura> alturas;

  /** Constructor vacío para JSON serialization. */
  public Estanteria() {
    this.alturas = new ArrayList<>();
  }

  /**
   * Constructor con número.
   *
   * @param numero Número de la estantería
   */
  public Estanteria(int numero) {
    this.numero = numero;
    this.alturas = new ArrayList<>();
  }

  /**
   * Obtiene el número de la estantería.
   *
   * @return Número de la estantería
   */
  public int getNumero() {
    return numero;
  }

  /**
   * Establece el número de la estantería.
   *
   * @param numero Número de la estantería
   */
  public void setNumero(int numero) {
    this.numero = numero;
  }

  /**
   * Obtiene las alturas de la estantería.
   *
   * @return Lista de alturas
   */
  public List<Altura> getAlturas() {
    return alturas;
  }

  /**
   * Establece las alturas de la estantería.
   *
   * @param alturas Lista de alturas
   */
  public void setAlturas(List<Altura> alturas) {
    this.alturas = alturas;
  }

  /**
   * Añade una altura a la estantería.
   *
   * @param altura Altura a añadir
   */
  public void addAltura(Altura altura) {
    if (!alturas.contains(altura)) {
      alturas.add(altura);
    }
  }

  /**
   * Obtiene una altura por su número.
   *
   * @param num Número de la altura
   * @return Altura encontrada o null si no existe
   */
  public Altura getAlturaByNumero(int num) {
    return alturas.stream().filter(a -> a.getNumero() == num).findFirst().orElse(null);
  }

  /**
   * Elimina una altura de la estantería.
   *
   * @param altura Altura a eliminar
   */
  public void removeAltura(Altura altura) {
    alturas.remove(altura);
  }

  @Override
  /**
   * Compara dos estanterías por su número.
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
    Estanteria estanteria = (Estanteria) o;
    return numero == estanteria.numero;
  }

  @Override
  /**
   * Genera un hashcode basado en el número de la estantería.
   *
   * @return Hashcode de la estantería
   */
  public int hashCode() {
    return Objects.hash(numero);
  }

  @Override
  /**
   * Representación en cadena de la estantería.
   *
   * @return Cadena representando la estantería
   */
  public String toString() {
    return "Estantería " + numero;
  }
}

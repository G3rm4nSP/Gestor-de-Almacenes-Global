package com.openwarehouses.models;

import java.util.Objects;

/**
 * Modelo que representa una Posición dentro de una Altura. Una Posición es el nivel más bajo de la
 * jerarquía. Genera un código identificador basado en la jerarquía:
 * pasillo.estanteria.altura.posicion
 *
 * @author German
 * @version 1.0
 * @since 2024-12-16
 */
public class Posicion {

  /** Número de la posición. */
  private int numero;

  /** Código de la posición (formato: pasillo.estanteria.altura.posicion). */
  private String codigo;

  /** Constructor vacío para JSON serialization. */
  public Posicion() {}

  /**
   * Constructor con número.
   *
   * @param numero número de la posición
   */
  public Posicion(int numero) {
    this.numero = numero;
  }

  /**
   * Obtiene el número de la posición.
   *
   * @return número de la posición
   */
  public int getNumero() {
    return numero;
  }

  /**
   * Establece el número de la posición.
   *
   * @param numero número de la posición
   */
  public void setNumero(int numero) {
    this.numero = numero;
  }

  /**
   * Obtiene el código de la posición.
   *
   * @return código de la posición
   */
  public String getCodigo() {
    return codigo;
  }

  /**
   * Establece el código de la posición.
   *
   * @param codigo código de la posición
   */
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  @Override
  /**
   * Compara dos objetos Posicion por su número.
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
    Posicion posicion = (Posicion) o;
    return numero == posicion.numero;
  }

  @Override
  /**
   * Genera un código hash basado en el número de la posición.
   *
   * @return código hash de la posición
   */
  public int hashCode() {
    return Objects.hash(numero);
  }

  @Override
  /**
   * Representación en cadena de la posición.
   *
   * @return cadena representando la posición
   */
  public String toString() {
    return "Posición " + numero + (codigo != null ? " (" + codigo + ")" : "");
  }
}

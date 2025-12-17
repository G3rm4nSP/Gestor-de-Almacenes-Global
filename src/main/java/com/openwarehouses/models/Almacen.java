package com.openwarehouses.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo que representa un almacén en la jerarquía. Un almacén contiene
 * múltiples pasillos.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class Almacen {
  /** Nombre del almacén. */
  private String nombre;

  /** Lista de pasillos en el almacén. */
  private List<Pasillo> pasillos;

  /** Constructor vacío para JSON serialization. */
  public Almacen() {
    this.pasillos = new ArrayList<>();
  }

  /**
   * Constructor con nombre.
   *
   * @param nombre Nombre del almacén
   */
  public Almacen(String nombre) {
    this.nombre = nombre;
    this.pasillos = new ArrayList<>();
  }

  /**
   * Obtiene el nombre del almacén.
   *
   * @return Nombre del almacén
   */
  public String getNombre() {
    return nombre;
  }

  /**
   * Establece el nombre del almacén.
   *
   * @param nombre Nombre del almacén
   */
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  /**
   * Obtiene los pasillos del almacén.
   *
   * @return Lista de pasillos
   */
  public List<Pasillo> getPasillos() {
    return pasillos;
  }

  /**
   * Establece los pasillos del almacén.
   *
   * @param pasillos Lista de pasillos del almacén
   */
  public void setPasillos(List<Pasillo> pasillos) {
    this.pasillos = pasillos;
  }

  /**
   * Añade un pasillo al almacén.
   *
   * @param pasillo Pasillo a añadir
   */
  public void addPasillo(Pasillo pasillo) {
    if (!pasillos.contains(pasillo)) {
      pasillos.add(pasillo);
    }
  }

  /**
   * Obtiene un pasillo por su número.
   *
   * @param numero Número del pasillo
   * @return Pasillo encontrado o null si no existe
   */
  public Pasillo getPasilloByNumero(int numero) {
    return pasillos.stream().filter(p -> p.getNumero() == numero).findFirst().orElse(null);
  }

  /**
   * Elimina un pasillo del almacén.
   *
   * @param pasillo Pasillo a eliminar
   */
  public void removePasillo(Pasillo pasillo) {
    pasillos.remove(pasillo);
  }

  @Override
  /**
   * Comprueba si dos almacenes son iguales basándose en su nombre.
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
    Almacen almacen = (Almacen) o;
    return Objects.equals(nombre, almacen.nombre);
  }

  @Override
  /**
   * Genera un código hash basado en el nombre del almacén.
   *
   * @return Código hash del almacén
   */
  public int hashCode() {
    return Objects.hash(nombre);
  }

  @Override
  /**
   * Representación en cadena del almacén.
   *
   * @return Nombre del almacén
   */
  public String toString() {
    return nombre;
  }
}

package com.openwarehouses.controllers;

import java.util.List;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Altura;
import com.openwarehouses.models.Posicion;
import com.openwarehouses.services.StorageService;
import com.openwarehouses.services.ValidationService;

/**
 * Controlador para gestionar operaciones de Posiciones. Maneja creación,
 * edición y eliminación de
 * posiciones dentro de una altura.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class PosicionController {
  /** Servicio de almacenamiento para persistencia de datos. */
  private final StorageService storageService;

  /** Altura actual. */
  private Altura alturaActual;

  /** Números del paillo. */
  private int numeroPasillo;

  /** Número de la estantería. */
  private int numeroEstanteria;

  /** Número de la altura. */
  private int numeroAltura;

  /** Lista de almacenes */
  private List<Almacen> almacenes;

  /**
   * Constructor que inicializa el controlador con servicio de almacenamiento y
   * lista de almacenes.
   *
   * @param alturaActual   Altura en la que se encuentra esta Posicion
   * @param almacenes      Lista de almacenes a gestionar
   * @param storageService Servicio de almacenamiento para persistencia de datos
   */
  public PosicionController(
      Altura alturaActual, List<Almacen> almacenes, StorageService storageService) {
    this.alturaActual = alturaActual;
    this.almacenes = almacenes;
    this.storageService = storageService;
  }

  /**
   * Establece la altura actual y los números de la jerarquía.
   *
   * @param altura        Altura a trabajar
   * @param numPasillo    Número del pasillo
   * @param numEstanteria Número de la estantería
   * @param numAltura     Número de la altura
   */
  public void setAlturaActual(Altura altura, int numPasillo, int numEstanteria, int numAltura) {
    this.alturaActual = altura;
    this.numeroPasillo = numPasillo;
    this.numeroEstanteria = numEstanteria;
    this.numeroAltura = numAltura;
  }

  /**
   * Obtiene la altura actual.
   *
   * @return Altura actual
   */
  public Altura getAlturaActual() {
    return alturaActual;
  }

  /**
   * Crea una nueva posición en la altura actual.
   *
   * @param numero Número de la posición
   * @return true si se creó exitosamente, false si el número ya existe
   */
  public boolean crearPosicion(int numero) {
    if (!ValidationService.isNumeroValido(numero)) {
      return false;
    }

    if (!ValidationService.isPosicionNumeroValid(numero, alturaActual)) {
      return false;
    }

    Posicion nuevaPosicion = new Posicion(numero);
    String codigo = ValidationService.generateCodigo(numeroPasillo, numeroEstanteria, numeroAltura, numero);
    nuevaPosicion.setCodigo(codigo);
    alturaActual.addPosicion(nuevaPosicion);
    guardar();
    return true;
  }

  /**
   * Edita el número de una posición existente.
   *
   * @param posicionActual Posición actual
   * @param nuevoNumero    Nuevo número
   * @return true si se editó exitosamente, false en caso contrario
   */
  public boolean editarPosicion(Posicion posicionActual, int nuevoNumero) {
    if (!ValidationService.isNumeroValido(nuevoNumero)) {
      return false;
    }

    if (nuevoNumero == posicionActual.getNumero()) {
      return true; // Sin cambios
    }

    if (!ValidationService.isPosicionNumeroValid(nuevoNumero, alturaActual)) {
      return false; // El número ya existe
    }

    posicionActual.setNumero(nuevoNumero);
    String codigo = ValidationService.generateCodigo(
        numeroPasillo, numeroEstanteria, numeroAltura, nuevoNumero);
    posicionActual.setCodigo(codigo);
    guardar();
    return true;
  }

  /**
   * Elimina una posición de la altura actual.
   *
   * @param posicion Posición a eliminar
   */
  public void eliminarPosicion(Posicion posicion) {
    alturaActual.removePosicion(posicion);
    guardar();
  }

  /**
   * Obtiene una posición por su número.
   *
   * @param numero Número de la posición
   * @return Posición encontrada o null
   */
  public Posicion getPosicionByNumero(int numero) {
    if (alturaActual == null) {
      return null;
    }
    return alturaActual.getPosicionByNumero(numero);
  }

  /** Guarda los datos en el almacenamiento. */
  private void guardar() {
    storageService.saveAlmacenes(almacenes);
  }
}

package com.openwarehouses.controllers;

import com.openwarehouses.models.Almacen;
import com.openwarehouses.models.Pasillo;
import com.openwarehouses.services.StorageService;
import com.openwarehouses.services.ValidationService;
import java.util.List;

/**
 * Controlador para gestionar operaciones de Pasillos. Maneja creación, edición
 * y eliminación de
 * pasillos dentro de un almacén.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class PasilloController {
  /** Servicio de almacenamiento para persistencia de datos. */
  private final StorageService storageService;

  /** Almacén actual. */
  private Almacen almacenActual;

  /** Lista de todos los almacenes. */
  private final List<Almacen> almacenes;

  /**
   * Constructor que inicializa el controlador con servicio de almacenamiento y
   * lista de almacenes.
   *
   * @param almacenActual  Almacen en el que se encuentra este Pasillo
   * @param almacenes      Lista de almacenes a gestionar
   * @param storageService Servicio de almacenamiento para persistencia de datos
   */
  public PasilloController(
      Almacen almacenActual, List<Almacen> almacenes, StorageService storageService) {
    this.almacenActual = almacenActual;
    this.almacenes = almacenes;
    this.storageService = storageService;
  }

  /**
   * Obtiene el almacén actual.
   *
   * @return Almacén actual
   */
  public Almacen getAlmacenActual() {
    return almacenActual;
  }

  /**
   * Crea un nuevo pasillo en el almacén actual.
   *
   * @param numero Número del pasillo
   * @return true si se creó exitosamente, false si el número ya existe
   */
  public boolean crearPasillo(int numero) {
    if (!ValidationService.isNumeroValido(numero)) {
      return false;
    }

    if (!ValidationService.isPasilloNumeroValid(numero, almacenActual)) {
      return false;
    }

    Pasillo nuevoPasillo = new Pasillo(numero);
    almacenActual.addPasillo(nuevoPasillo);
    guardar();
    return true;
  }

  /**
   * Edita el número de un pasillo existente.
   *
   * @param pasilloActual Pasillo actual
   * @param nuevoNumero   Nuevo número
   * @return true si se editó exitosamente, false en caso contrario
   */
  public boolean editarPasillo(Pasillo pasilloActual, int nuevoNumero) {
    if (!ValidationService.isNumeroValido(nuevoNumero)) {
      return false;
    }

    if (nuevoNumero == pasilloActual.getNumero()) {
      return true; // Sin cambios
    }

    if (!ValidationService.isPasilloNumeroValid(nuevoNumero, almacenActual)) {
      return false; // El número ya existe
    }

    pasilloActual.setNumero(nuevoNumero);
    guardar();
    return true;
  }

  /**
   * Elimina un pasillo del almacén actual.
   *
   * @param pasillo Pasillo a eliminar
   */
  public void eliminarPasillo(Pasillo pasillo) {
    almacenActual.removePasillo(pasillo);
    guardar();
  }

  /**
   * Obtiene un pasillo por su número.
   *
   * @param numero Número del pasillo
   * @return Pasillo encontrado o null
   */
  public Pasillo getPasilloByNumero(int numero) {
    if (almacenActual == null) {
      return null;
    }
    return almacenActual.getPasilloByNumero(numero);
  }

  /** Guarda los datos en el almacenamiento. */
  private void guardar() {
    storageService.saveAlmacenes(almacenes);
  }
}

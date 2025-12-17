package com.openwarehouses.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.openwarehouses.models.Almacen;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de persistir y cargar datos de almacenes en JSON. Los datos se guardan en
 * Documentos/Almacenes o en la carpeta ejecutable. También maneja la configuración del PIN de
 * acceso.
 *
 * @author German
 * @version 1.0
 * @since 2025-12-16
 */
public class StorageService {
  /** Carpeta por defecto para almacenar los datos. */
  private static final String DEFAULT_FOLDER = "Almacenes";

  /** Nombre del archivo JSON donde se guardan los datos. */
  private static final String FILENAME = "almacenes.json";

  /** Instancia de Gson para serialización/deserialización JSON. */
  private final Gson gson;

  /** Directorio donde se almacenan los datos. */
  private final File storageDir;

  /** Clase interna para manejar la configuración (PIN). */
  private static class Config {
    private String pin;

    Config(String pin) {
      this.pin = pin;
    }
  }

  /** Clase interna para envolver la configuración y la lista de almacenes. */
  private static class StorageWrapper {
    private Config config;
    private List<Almacen> almacenes;

    StorageWrapper(Config config, List<Almacen> almacenes) {
      this.config = config;
      this.almacenes = almacenes;
    }
  }

  /**
   * Constructor que inicializa el servicio de almacenamiento. Intenta crear la carpeta en
   * Documentos. Si falla, usa la carpeta ejecutable.
   */
  public StorageService() {
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    this.storageDir = initializeStorageDirectory();
  }

  /**
   * Inicializa el directorio de almacenamiento. Intenta usar Documentos/Almacenes, si no, usa la
   * carpeta ejecutable.
   *
   * @return Directorio donde se guardarán los datos
   */
  private File initializeStorageDirectory() {
    // Intenta primero en Documentos
    String userHome = System.getProperty("user.home");
    File documentsFolder = new File(userHome, "Documents");
    File almacenesFolder = new File(documentsFolder, DEFAULT_FOLDER);

    if (documentsFolder.exists()) {
      if (!almacenesFolder.exists()) {
        almacenesFolder.mkdirs();
      }
      return almacenesFolder;
    }

    // Si no existe Documentos, usa la carpeta ejecutable
    File executableFolder = new File(System.getProperty("user.dir"), DEFAULT_FOLDER);
    if (!executableFolder.exists()) {
      executableFolder.mkdirs();
    }
    return executableFolder;
  }

  /**
   * Guarda la lista de almacenes en JSON.
   *
   * @param almacenes Lista de almacenes a guardar
   */
  public void saveAlmacenes(List<Almacen> almacenes) {
    try {
      File file = new File(storageDir, FILENAME);
      // Preserve existing config (pin) if present
      StorageWrapper wrapper = loadWrapper();
      Config cfg = wrapper != null && wrapper.config != null ? wrapper.config : new Config("1234");
      StorageWrapper toSave = new StorageWrapper(cfg, almacenes);
      try (FileWriter writer = new FileWriter(file)) {
        gson.toJson(toSave, writer);
      }
    } catch (IOException e) {
      System.err.println("Error guardando almacenes: " + e.getMessage());
    }
  }

  /**
   * Carga la lista de almacenes desde JSON. Si el archivo no existe o está vacío, devuelve una
   * lista vacía.
   *
   * @return Lista de almacenes cargados desde JSON
   */
  public List<Almacen> loadAlmacenes() {
    StorageWrapper wrapper = loadWrapper();
    if (wrapper == null || wrapper.almacenes == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(wrapper.almacenes);
  }

  /** Carga el objeto envolvente (config + almacenes) si existe. */
  private StorageWrapper loadWrapper() {
    try {
      File file = new File(storageDir, FILENAME);
      if (!file.exists() || file.length() == 0) {
        return null;
      }

      // Leer contenido como texto para detectar formato previo (array) y migrarlo si
      // hace falta
      String content = Files.readString(file.toPath());
      String trimmed = content.trim();
      if (trimmed.isEmpty()) {
        return null;
      }

      if (trimmed.charAt(0) == '[') {
        // Formato antiguo: lista JSON de almacenes. Migramos a StorageWrapper con PIN
        // por defecto
        Almacen[] almacenes = gson.fromJson(trimmed, Almacen[].class);
        StorageWrapper wrapper =
            new StorageWrapper(new Config("1234"), new ArrayList<>(List.of(almacenes)));
        return wrapper;
      }

      try (FileReader reader = new FileReader(file)) {
        StorageWrapper wrapper = gson.fromJson(reader, StorageWrapper.class);
        return wrapper;
      }
    } catch (IOException e) {
      System.err.println("Error cargando wrapper: " + e.getMessage());
      return null;
    }
  }

  /**
   * Obtiene el PIN guardado en la configuración. Si no existe, devuelve "1234".
   * @return Devuelve el pin correcto en formato String
   */
  public String loadPin() {
    StorageWrapper wrapper = loadWrapper();
    if (wrapper != null
        && wrapper.config != null
        && wrapper.config.pin != null
        && !wrapper.config.pin.isBlank()) {
      return wrapper.config.pin;
    }
    return "1234";
  }

  /**
   * Guarda el PIN en la configuración JSON, preservando los almacenes existentes.
   * @param pin El pin nuevo que se va a guardar
   * */
  public void savePin(String pin) {
    try {
      List<Almacen> current = loadAlmacenes();
      StorageWrapper toSave = new StorageWrapper(new Config(pin), current);
      File file = new File(storageDir, FILENAME);
      try (FileWriter writer = new FileWriter(file)) {
        gson.toJson(toSave, writer);
      }
    } catch (IOException e) {
      System.err.println("Error guardando PIN: " + e.getMessage());
    }
  }

  /**
   * Obtiene el directorio de almacenamiento.
   *
   * @return Directorio de almacenamiento
   */
  public File getStorageDirectory() {
    return storageDir;
  }

  /** Limpia todos los datos (borra el archivo JSON). */
  public void clearData() {
    File file = new File(storageDir, FILENAME);
    if (file.exists()) {
      file.delete();
    }
  }

  /**
   * Verifica si existe algún almacén guardado.
   *
   * @return true si hay almacenes, false en caso contrario
   */
  public boolean hasAlmacenes() {
    return !loadAlmacenes().isEmpty();
  }
}

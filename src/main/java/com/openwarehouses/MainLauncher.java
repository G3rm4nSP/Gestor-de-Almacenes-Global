package com.openwarehouses;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Launcher que carga todos los jars de la carpeta "lib" y arranca la aplicación principal.
 * Compatible con Java 9+ (Java 21 incluido).
 */
public final class MainLauncher {

    /** Constructor privado para evitar instanciación */
    private MainLauncher() {
        // Constructor privado para evitar instanciación
    }
    public static void main(String[] args) {
        try {
            // Obtener carpeta del JAR principal
            File appDir = new File(MainLauncher.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParentFile();

            // Carpeta lib
            File libDir = new File(appDir, "lib");
            if (!libDir.exists() || !libDir.isDirectory()) {
                throw new RuntimeException("No se encontró la carpeta lib en: " + libDir.getAbsolutePath());
            }

            // Crear un nuevo URLClassLoader con todos los jars de lib
            File[] jars = libDir.listFiles((d, name) -> name.endsWith(".jar"));
            URL[] urls = new URL[jars.length];
            for (int i = 0; i < jars.length; i++) {
                urls[i] = jars[i].toURI().toURL();
            }

            URLClassLoader loader = new URLClassLoader(urls, MainLauncher.class.getClassLoader());

            // Cargar la clase principal
            Class<?> appClass = Class.forName("com.openwarehouses.App", true, loader);

            // Invocar el main de la clase principal
            Method main = appClass.getMethod("main", String[].class);
            main.invoke(null, (Object) args);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

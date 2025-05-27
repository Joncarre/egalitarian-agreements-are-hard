# Sistema de Monitoreo del Algoritmo Genético

## Descripción
El sistema de monitoreo permite seguir la evolución del algoritmo genético sin afectar la lógica principal del algoritmo.

## Arquitectura

### Clases principales:
- **`Algorithm.java`**: Contiene solo la lógica del algoritmo genético, sin código de monitoreo
- **`AlgorithmMonitor.java`**: Clase separada que maneja todo el monitoreo y reportes
- **`Engine.java`**: Utiliza opcionalmente el monitor para seguir la evolución

## Formas de uso

### 1. Monitoreo completo (por defecto)
```java
Engine engine = new Engine(); // Monitoreo habilitado
engine.start();
```
- Muestra reportes detallados cada 500 generaciones
- Muestra estado rápido cada 100 generaciones
- Incluye estadísticas de fitness, diversidad, fase del algoritmo, etc.

### 2. Ejecución silenciosa
```java
Engine engine = new Engine(false); // Monitoreo deshabilitado
engine.start();
```
- No muestra reportes de evolución
- Solo muestra resultados finales esenciales
- Ideal para ejecuciones en producción o cuando ya has verificado que funciona

### 3. Monitoreo personalizado
```java
Engine engine = new Engine(true);
engine.getMonitor().setIntervals(1000, 250); // Detallado cada 1000, rápido cada 250
engine.start();
```

### 4. Control dinámico
```java
Engine engine = new Engine(true);
// Durante ejecución:
engine.getMonitor().setEnabled(false); // Deshabilitar monitoreo
engine.getMonitor().setEnabled(true);  // Volver a habilitar
```

## Información que muestra el monitor

### Reportes detallados (cada 500 generaciones por defecto):
- TOP 5 individuos con mejor fitness
- Estadísticas de la población:
  - Mejor fitness histórico
  - Fitness promedio, máximo y mínimo actual
  - Score de diversidad de la población
  - Tasa de mutación actual
  - Contador de generaciones sin mejora
  - Fase actual (exploración vs explotación)
  - Avisos de estancamiento

### Reportes rápidos (cada 100 generaciones por defecto):
- Número de generación
- Mejor fitness actual
- Tasa de mutación actual

## Ventajas de este diseño

1. **Separación de responsabilidades**: El algoritmo no está contaminado con código de monitoreo
2. **Flexibilidad**: Fácil activar/desactivar sin modificar el código principal
3. **Configurabilidad**: Intervalos de reporte personalizables
4. **Reutilización**: El monitor se puede usar con diferentes versiones del algoritmo
5. **Mantenimiento**: Cambios en el monitoreo no afectan la lógica del algoritmo

## Ejemplo de uso en Main.java

```java
public static void main(String[] args) {
    try {
        // Para testing y desarrollo
        Engine engine = new Engine(true);
        
        // Para producción o cuando ya no necesites los reportes
        // Engine engine = new Engine(false);
        
        engine.start();
    } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
    }
}
```

## Archivos de ejemplo

- **`MonitoringExamples.java`**: Muestra diferentes formas de usar el sistema de monitoreo
- **`Main.java`**: Ejemplo básico de uso

Este diseño te permite tener lo mejor de ambos mundos: monitoreo detallado cuando lo necesitas para debugging y desarrollo, y ejecución limpia cuando ya no lo necesitas.

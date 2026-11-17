# AcopioGT - Lab 4 Polimorfismo (CC2008, UVG)

Aplicación de consola en Java para la cooperativa AcopioGT: recepción de lotes con pre-liquidación, bodegas, pedidos y despachos, vencimientos y reportes. Los datos viven solo en memoria.

## Requisitos

- JDK 11 o superior (probado con JDK 21).

## Compilar y ejecutar (Git Bash, Linux o macOS)

Desde la carpeta raíz del repositorio:

```bash
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

En PowerShell o CMD de Windows, reemplace `src/*.java` por `src\*.java`.

## Estructura

Todos los archivos `.java` están en la carpeta `src`, sin paquetes. La clase `Main` solo inicializa el programa: crea la cooperativa, carga los datos iniciales (`DatosIniciales`) y cede el control a `Menu`.

## Datos iniciales

- Fecha de operación inicial: 2026-11-15.
- Bodegas: `SEC-01` (seca, 1500 kg), `SEC-02` (seca, 800 kg) y `FRIA-01` (cámara fría, 500 kg).
- Dos lotes de cada categoría, cargados sin pagos ni cobros. Incluyen un café con humedad de 13.4 %, un empate en el orden de salida resuelto por calidad (`CP-002` antes que `CP-001`) y lotes a punto de vencer (`HT-001` vence el 2026-11-15 y `CD-002` el 2026-11-16).

## Uso del menú

1. Pre-liquidar y recibir un lote: muestra pago y vencimiento sin registrar nada; solo si el productor acepta se pide la bodega y se registra el lote y el pago.
2. Registrar una bodega.
3. Registrar un pedido.
4. Despachar un pedido (de todas las bodegas, en orden de salida).
5. Avanzar la fecha de operación (formato `aaaa-mm-dd`); descarta los lotes vencidos.
6. Consultar lotes y bodegas, siempre en orden de salida.
7. Reportes: existencias, lotes por vencer en 3 días y totales por categoría.
0. Salir.

Toda operación inválida se rechaza con un mensaje y sin modificar el estado previo; las entradas con formato incorrecto no terminan el programa.

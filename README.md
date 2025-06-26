# CuentasClaritas

## Descripción del Proyecto
CuentasClaritas es una aplicación Android nativa desarrollada en Kotlin, diseñada para simplificar la división de gastos a partir de tickets de compra. La aplicación permite a los usuarios capturar una foto de un ticket, extrae automáticamente la información de los productos y precios mediante OCR, y facilita la división de la cuenta entre múltiples personas.

El proyecto sigue las mejores prácticas de desarrollo moderno de Android, utilizando Jetpack Compose para la interfaz de usuario, una arquitectura MVVM limpia (Clean Architecture) para una estructura modular y escalable, y Dagger Hilt para la inyección de dependencias.

## Objetivo Principal
El objetivo principal de CuentasClaritas es ofrecer una herramienta intuitiva y eficiente para:

- **Capturar Tickets:** Permitir al usuario tomar una foto de un ticket de compra.
- **Extracción OCR:** Utilizar Google ML Kit Text Recognition para extraer el texto del ticket.
- **Procesamiento de Datos:** Interpretar el texto extraído para identificar productos, cantidades y precios.
- **Edición y Visualización:** Mostrar una lista editable de los productos y sus costos.
- **División de Cuentas:** Facilitar la asignación de productos a diferentes personas y calcular el total por cada una.

## Características (Actuales y En Desarrollo)
- Captura de tickets mediante la cámara del dispositivo.
- Reconocimiento óptico de caracteres (OCR) para extraer texto de las imágenes.
- Análisis inteligente del texto para identificar artículos, cantidades y precios.
- Interfaz para revisar y editar los productos extraídos.
- **Flujo de reparto avanzado:**
  - Alta de usuarios tras guardar un ticket.
  - Asignación visual e intuitiva de productos a usuarios (cards, checkboxes, validación visual).
  - Validación: no se puede guardar el reparto si hay productos sin asignar.
  - Persistencia de la relación producto-usuario-ticket en base de datos (Room).
  - Edición posterior del reparto.
- Almacenamiento local de tickets procesados para consulta futura.
- Interfaz de usuario moderna, minimalista y accesible.

## Arquitectura y Tecnologías
El proyecto está construido siguiendo los principios de Clean Architecture, separando las responsabilidades en tres capas principales:

- **Presentation:** Interfaz de usuario (Jetpack Compose) y lógica de presentación (ViewModels).
- **Domain:** Casos de uso y lógica de negocio central, independiente de Android y frameworks.
- **Data:** Implementación de repositorios y fuentes de datos (Room, ML Kit, CameraX).

### Stack Tecnológico Principal
- **Lenguaje:** Kotlin
- **Interfaz de Usuario:** Jetpack Compose
- **Arquitectura:** MVVM con Clean Architecture
- **Inyección de Dependencias:** Dagger Hilt
- **Cámara:** CameraX
- **OCR:** Google ML Kit Text Recognition
- **Base de Datos Local:** Room (con migraciones reales y relaciones entre tickets, productos y usuarios)
- **SDK Mínimo:** API 24 (Android 7.0 Nougat)

## Principios de Desarrollo
- **Código Limpio:** Escribir código simple, legible y mantenible.
- **Modularidad:** Diseño modular para facilitar la escalabilidad y el mantenimiento.
- **Testeabilidad:** Código estructurado para ser fácilmente testeable.
- **Buenas Prácticas:** Adhesión a las guías de estilo y patrones de diseño recomendados.
- **UI Moderna:** Interfaz de usuario minimalista, intuitiva y accesible.

## Estado del Proyecto
- El flujo de reparto de productos entre usuarios está implementado y validado.
- Migraciones reales de Room implementadas para mantener la integridad de los datos.
- El proyecto evoluciona activamente y este README se actualizará a medida que se añadan nuevas funcionalidades.

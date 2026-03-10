# 📑 Documentación Técnica: Aplicación "Mis Contactos"

---

## 👤 Información del Autor
- **Autor:** Jesus Alberto Moreno Cortes
- **Grupo:** 8_01
- **Carrera:** Ingeniería en Software
- **Materia:** Programación Móvil
- **Docente:** Miguel Angel Montoya Cerro

---

## 🚀 Resumen del Proyecto
Este proyecto consiste en el desarrollo de una **agenda de contactos profesional** diseñada para ofrecer una experiencia de usuario fluida y moderna. Se enfocó en crear una interfaz intuitiva donde gestionar personas sea rápido y agradable. La aplicación permite registrar contactos con validaciones de seguridad, organizarlos automáticamente por orden alfabético, marcarlos como favoritos y eliminarlos con animaciones de alto nivel. Técnicamente, es una app robusta que utiliza una base de datos interna para que la información nunca se pierda, incluso sin internet, y emplea las últimas tecnologías de Google para garantizar un rendimiento óptimo sin trabas ni lagueos.

---

## 🏗️ Arquitectura y Tecnologías
La aplicación utiliza el patrón **MVVM (Model-View-ViewModel)**, separando la lógica del diseño.
- **Lenguaje:** Kotlin
- **Interfaz:** Jetpack Compose (Moderno y Declarativo)
- **Base de Datos:** Room SQLite (Persistencia local)
- **Gestión de Estado:** StateFlow (Reactividad en tiempo real)

---

## 📸 Pantallas y Funcionalidades Principales

### 1. Visualización de la Lista de Contactos (`ContactListScreen`)
Es el núcleo de la aplicación. Muestra todos los contactos registrados organizados por iniciales en una lista infinita altamente optimizada.
- **Función Clave:** Utiliza un sistema de filtrado en tiempo real que permite buscar por nombre o teléfono de manera instantánea.
- **Código:** Se implementa `remember` y `StateFlow` para garantizar que la lista solo se actualice cuando sea necesario, manteniendo 60 FPS.

```kotlin
val filteredContacts = remember(contacts, searchQuery) {
    contacts.filter { it.name.contains(searchQuery, ignoreCase = true) }
        .sortedBy { it.name.lowercase() }
}
```

### 2. Crear Registro de Contacto (`ContactFormScreen`)
Pantalla dedicada a la captura de datos con un diseño limpio que evita la fatiga visual.
- **Función Clave:** Implementa restricciones de seguridad que impiden caracteres especiales y números en los nombres, además de un selector de país interactivo.
- **Código:** Lógica de validación profesional para asegurar que los datos guardados sean correctos.

```kotlin
fun filterInput(input: String): String {
    // Solo permite letras y espacios, máximo 50 caracteres
    return input.filter { it.isLetter() || it.isWhitespace() }.take(50)
}
```

### 3. Visualización del Contacto (`ContactDetailScreen`)
Presenta el perfil completo del usuario con un diseño de alta jerarquía visual y botones de acción rápida.
- **Función Clave:** Los iconos de Llamar y Correo están solapados entre secciones para dar una sensación de profundidad 3D.
- **Código:** Animación de salida espectacular al eliminar el contacto.

```kotlin
// Efecto de solapamiento en la línea divisoria
Row(modifier = Modifier.fillMaxWidth().offset(y = (-35).dp)) {
    ActionButton(icon = Icons.Default.Phone, label = "Llamar")
    // ...
}
```

---

## 🛠️ Tecnologías de Ingeniería Avanzada

### Animación Espectacular de Eliminación (Fly-out)
Al borrar un contacto, este "vuela" y rota hacia afuera de la pantalla antes de desaparecer de la base de datos.
```kotlin
val translationX by transition.animateFloat(
    transitionSpec = { tween(durationMillis = 600, easing = CubicBezierEasing(0.36f, 0f, 0.66f, -0.56f)) },
    label = "flyOut"
) { if (it) 1200f else 0f }
```

### Selector de País Inteligente
Interfaz que permite seleccionar el origen del teléfono y ajusta el prefijo automáticamente.
```kotlin
val countries = listOf("MX +52" to "🇲🇽", "CO +57" to "🇨🇴", "ES +34" to "🇪🇸", ...)
// ... actualización dinámica de bandera y código
```

---

## 🎨 Detalles de Diseño "Premium"
- **Sombras de Impacto:** Uso de `spotColor` azulado para profundidad 3D real.
- **Tipografía:** Jerarquía visual con fuentes `ExtraBold` y `Black` para una lectura rápida.
- **Fluidez:** Optimización técnica para evitar el "lag" en dispositivos de gama media.

---

## 🏁 Conclusión Final
El desarrollo de la aplicación **"Mis Contactos"** representa la culminación de un proceso de ingeniería de software enfocado en la excelencia. A través de este proyecto, se ha demostrado que es posible combinar la potencia de la persistencia de datos local con una interfaz de usuario dinámica y animada de primer nivel. 

La implementación de patrones de diseño modernos (**MVVM**), la optimización de recursos mediante el uso inteligente de estados en **Jetpack Compose** y la atención al detalle en las microinteracciones, posicionan a esta herramienta como una solución profesional para la gestión de información personal. El resultado final no es solo una aplicación funcional, sino un producto digital robusto, escalable y visualmente impactante, diseñado bajo los estándares más exigentes del desarrollo móvil actual.

---
*Documentación generada para la presentación del proyecto final.*

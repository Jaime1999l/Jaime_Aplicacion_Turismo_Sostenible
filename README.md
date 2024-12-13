Jaime López Díaz

https://github.com/Jaime1999l/Jaime_Aplicacion_Turismo_Sostenible.git

# Aplicación de Turismo Sostenible

Esta aplicación de Turismo permite a los usuarios visualizar en un mapa diferentes tipos de ubicaciones como **parques**, **centros de salud**, **policía** y **puntos de reciclaje**. La app también incluye funcionalidades avanzadas como **filtrado** y **búsqueda**, además de centrar el mapa en la posición actual del usuario al iniciar.

---

## **Características Principales**

1. **Visualización de Ubicaciones en el Mapa:**
   - Utiliza la API de **Overpass** para obtener ubicaciones en tiempo real.
   - Muestra ubicaciones categorizadas como:
     - Parques
     - Centros de Salud (clínicas y hospitales)
     - Estaciones de Policía
     - Puntos de Reciclaje

2. **Ubicación Actual del Usuario:**
   - Al iniciar, el mapa centra la vista en la **posición actual** del usuario utilizando el servicio de **Fused Location Provider** de Google.

3. **Filtrado de Ubicaciones:**
   - El usuario puede seleccionar un tipo de ubicación específico mediante un **Spinner** (menú desplegable):
     - Parques
     - Centros de Salud
     - Estaciones de Policía
     - Puntos de Reciclaje
     - Todos

4. **Búsqueda de Ubicaciones:**
   - Implementa una **barra de búsqueda** para encontrar ubicaciones específicas.
   - Permite buscar por **nombre** o **descripción** de la ubicación.

5. **Interacción con Marcadores:**
   - Al pulsar sobre un marcador, se muestra información adicional de la ubicación, como su **nombre** o **descripción**.

---

## **Detalles Técnicos del MapaActivity**

### **Tecnologías Usadas:**
- **Google Maps API** para mostrar y gestionar mapas.
- **Overpass API** para obtener datos de ubicaciones.
- **Fused Location Provider** para obtener la ubicación actual del usuario.
- **Gson** para parsear JSON de la API.
- **UI Widgets:**
   - `Spinner` para filtrar ubicaciones.
   - `AutoCompleteTextView` como barra de búsqueda.
   - `Toast` para mostrar información adicional al pulsar en los marcadores.

---

### **Flujo del Activity:**
1. **Cargar Ubicaciones:**
   - Se obtienen los datos desde la **Overpass API** y se almacenan localmente en una lista (`allLocations`).
   - **No se muestran todas las ubicaciones al iniciar** para evitar saturación visual.

2. **Mostrar Ubicaciones:**
   - Al seleccionar una categoría en el **Spinner**, se filtran las ubicaciones correspondientes y se muestran en el mapa.
   - La búsqueda dinámica muestra ubicaciones filtradas por **nombre** o **descripción**.

3. **Ubicación del Usuario:**
   - Al iniciar el mapa, se centra en la posición actual del usuario con un nivel de zoom adecuado.

4. **Interacción con los Marcadores:**
   - Al hacer clic en un marcador, se muestra un **Toast** con información adicional sobre la ubicación.

Manual de Usuario del Sistema de Gestión de Inventario de Libros BookFlow
Versión 1.1
Octubre 2025
1. Introducción al Sistema
1.1. Objetivo del Manual
Esta es una guía práctica para ayudar a los usuarios (Administradores,
Trabajadores y Clientes) a utilizar eficientemente el sistema web BookFlow,
cubriendo el acceso, la navegación y las tareas específicas de cada rol.
1.2. Roles de Usuario y Acceso
Está diseñado con tres roles distintos, cada uno con un conjunto específico de
permisos y funcionalidades.
Rol Panel de Acceso Funcionalidades Principales
ADMIN Panel de
Administración
Gestión total de usuarios, roles, catálogo,
inventario y reportes avanzados.
TRABAJADOR Dashboard de
Inventario
Gestión de libros, editoriales, proveedores,
categorías y monitoreo de stock.
CLIENTE Panel del Cliente Consulta del catálogo, gestión de carrito,
historial de compras y promociones.
2. Acceso y Navegación Básica
2.1. Iniciar y Cerrar Sesión
1. Acceder: Abra su navegador y diríjase a la URL de BookFlow.
localhost Ingrese sus credenciales. El sistema lo dirigirá automáticamente
a su panel de control.
2. Cerrar Sesión (General): En la barra de navegación superior (Navbar),
busque el botón "Cerrar sesión"
 Nota: El sistema le pedirá confirmar la acción antes de salir
definitivamente.
2.2. Estructura del Menú de Navegación
Todos los paneles utilizan un menú lateral (Sidebar) para acceder a los módulos
principales.
Panel de Administración (ADMIN)
El menú lateral está dividido en tres secciones para una gestión completa:
• Gestión de Datos Maestros: Usuarios, Roles, Categorías, Autores,
Editoriales, Proveedores, Libros.
• Reportes: Reporte Ventas, Reporte Libros, Reporte Usuarios.
• Sistema: Configuración.
Panel de Inventario (TRABAJADOR)
El menú se centra en la gestión del inventario físico:
• Control: Panel (Inicio).
• Gestión de Inventario: Gestión de Libros, Editoriales, Proveedores,
Categorías.
• Perfil: Configuración del Perfil.
3. Módulo: Panel de Administración (ADMIN)
El Panel de Admin ofrece una vista completa del rendimiento y control del
sistema.
3.1. Métricas Clave.
Esta área muestra un resumen instantáneo del estado del negocio:
Métrica Descripción
Usuarios Registrados Cantidad total de usuarios en el sistema.
Libros Disponibles Total, de libros únicos en el catálogo.
Roles Cantidad de roles de usuario definidos.
Ventas Totales (S/) Suma total de las ventas registradas.
3.2. Gestión de Inventario (Tabla de Libros)
La tabla "Libros Registrados en el Sistema" muestra el inventario clave.
Columna Observación Importante
Stock Alerta de Stock Bajo: Si el stock es menor a 10, el valor se muestra
en rojo y negrita, acompañado por un icono de advertencia para
una acción rápida.
3.3. Tareas Exclusivas del ADMIN
• Gestión de Usuarios y Roles: Acceda a Usuarios y Roles en el menú para
crear, modificar, o eliminar cuentas de usuario y definir sus permisos.
• Reportes Avanzados: Acceda a la sección Reportes para generar informes
detallados de ventas y actividad de usuarios en formatos PDF y Excel.
4. Módulo: Panel de Inventario (TRABAJADOR)
El panel del Trabajador está optimizado para el manejo diario del inventario.
4.1. Resumen de Inventario (Métricas Principales)
Métrica Descripción
Libros en Catálogo Cantidad total de títulos registrados.
Editoriales Registradas Cantidad de editoriales activas.
Proveedores Activos Cantidad de proveedores para pedidos de reposición.
4.2. Alertas de Stock Crítico (Prioridad)
Esta sección indica qué libros requieren reposición inmediata (stock bajo un
umbral predefinido).
1. Notificación Inicial: Al iniciar sesión, puede ver una alerta (SweetAlert2) si
hay libros con stock bajo.
2. Tabla de Alertas: Muestra la lista de libros con stock bajo:
o Si hay libros, se listan por título y stock actual.
o Utilice el botón "Reponer” para ir directamente a la pantalla de
edición del libro y actualizar su stock.
4.3. Gestión Detallada
Use el menú lateral para realizar las operaciones CRUD (Crear, Leer, Actualizar,
Eliminar) en las siguientes entidades: Libros, Editoriales, Proveedores, y
Categorías.
5. Módulo: Panel del Cliente (CLIENTE)
El Panel del Cliente es el centro de actividad del usuario final.
5.1. Resumen Personalizado (Sección Superior)
Muestra un saludo personalizado y métricas centradas en su actividad:
Métrica Descripción Acción Rápida
Carrito Cantidad de artículos actualmente en el
carrito.
Botón "Ver
carrito"
Compras Total, de pedidos realizados por el cliente. Botón "Ver
historial"
Promociones Cantidad de promociones o descuentos
activos.
Botón "Ver
ofertas"
5.2. Libros Recomendados
Esta sección muestra tarjetas con portadas, títulos, autores y precios de libros
sugeridos.
• Añadir al Carrito: Use el campo de cantidad y el botón "Agregar" para
añadir unidades.
 El botón se deshabilita si el stock del libro es cero.
5.3. Catálogo de Libros
Para buscar y ver el catálogo completo, use el enlace de navegación
correspondiente. Como Cliente, solo puede consultar y añadir al carrito; no
puede modificar los datos del sistema.

# PreferenceScreen
Aplicación de configuración con dos Activities. Crea una aplicación que permita personalizar la visualización de texto según el usuario modifique ajustes de color, fondo, estilo, transparencia y rotación.

## Demo
Se puede ver la demo del proyecto en la carpeta de este zip como [demo_preferenceScreen.mp4](demo_sharedPreferences.mp4). Cualquier problema con la versión entregada por Moodle (tanto del proyecto como del README) se puede usar el repositorio donde se encuentra alojada la práctica: [https://github.com/solsolet/PreferenceScreen.git](https://github.com/solsolet/PreferenceScreen.git)

## Implementación
Primero se creó la base de la aplicación con _layouts_ para las diferentes pantallas con los elementos requeridos. La aplicación consta de 2 actividades:
- `MAinActivity`: permite introducir una cadena de texto y pulsar los botones `Visualizar`o `Cerrar` para hacer dichas acciones.
- `SettingsActivity`: permite modificar los ajustes del texto para que se visualice de manera distinta. Esta actividad se ha hecho también con `SettingsFragment`.

Se puede navegar entre las dos pantallas con el botón de arriba a la izquierda, en la _Action Bar_.

### Navegación
Para cambiar entre actividades se ha usado _DrawerLayout_ en los archivos xml. El _Component Tree_ queda tal que:
- drawer_layout
    - LinearLayout (contiene toda la pantalla)
        - toolbar
        - LinearLayout (contiene los elementos que vemos como botones, editText, TextView...)
    - NavigationView (contiene el menú, `drawer_menu`)

El **menú** tiene 2 ítems donde:
- Al pulsar la pantalla en la que estamos de cierra el _drawer_
- Al pulsar la otra navegamos con intent hacia ella.

Para que funcione se han sobrecargado los métodos:
- `onOptionsItemSelected`: permite que se pueda pulsar el botón de menú hamburguesa
- `onNavigationItemSelected`: permite definir que pasa al pulsar cada ítem del menú, en este caso navegar.

### Configuración
Se ha elegido que el fragmento de _settings_ esté embebido en la actividad `SettingsActivity`. Así se hizo en el método `onCreate`:
```kotlin
// Insert preference fragment
if (savedInstanceState == null) {
    supportFragmentManager
        .beginTransaction()
        .replace(R.id.settings_container, SettingsFragment())
        .commit()
}
```
Y el _layout_:
```xml
<FrameLayout
    android:id="@+id/settings_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

El **fragmento** consta de la sobrecarga de los métodos:
- `onCreatePreferences` (cargamos el XML de preferencias, `root_preferences`)
- `onPreferenceTreeClick` (creamos un nuevo fragment para poder acceder a las `PreferenceScreen`)
- `onResume` y `onPause`
- `onSharedPreferenceChanged`

`root_preferences` a su vez tiene las dos `PreferenceScreen` para la configuración básica y la avanzada y cada una de ellas tiene los elementos necesarios, que han sido declarados en `array.xml` y `strings.xml`.

### Mostrar resultado y recuperar preferencias
Al pulsar el botón `Visualizar` en _main_ vemos el resultado de lo que hemos escrito con el estilo que hemos configurado en los ajustes.

Esto se hace en `actualizarVistaPrevia`, donde:
```kotlin
// Cogemos las Preferences
val prefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)

// Cogemos cada una individualmente
val textSize = prefs.getString("font_size", "18")?.toFloatOrNull() ?: 18f

// Aplicar formato al TextView
binding.tvResultadotext = texto
// Para el tamaño
binding.tvResultado.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
```

Y lo haríamos así con todas las preferencias que hayamos declarado.

## Problemas
El primer problema que me encontré es que me resultó difícil implementar la navegación con el `Drawer`. Tuve que leer mucho, consultar el API, tutoriales, vídeos... y aunque ponía las mismas líneas de código en la parte de la _clase_ y en el _layout_ no terminaba de funcionar.

Al principio no conseguía que se viese el botón. y una vez lo solucioné no respondía al pulsarlo.

Lo fui solucionando poco a poco, por ejemplo, que no se viese el botón fue por que no declaraba todas las cosas necesarias para el `drawer` y la `toolbar` en el método `onCreate`, lo hacía después y por tanto nunca se veía.

Y los demás problemas se solucionaron al añadir:
```kotlin
drawerToggle = ActionBarDrawerToggle(
    this@MainActivity,
    binding.drawerLayout,
    binding.toolbar, // ⬅️ Me faltaba esta
    R.string.abrir,
    R.string.cerrar
)
```

Otro problema también relacionado con la _Action Bar_ fue por culpa del tema. Resulta que en el `AndroidManifest` lo tenía declarado sustituyendo al tema en `res/themes.xml` y hasta que me di cuenta no entendía por que algunas cosas de la barra no funcionaban.

Una vez resuelto todo lo relacionado con la _Action Bar_ me tocó pelearme por que al navegar con el _drawer_ me saltaba una `FATAL EXCEPTION` el intentar entrar en `Settings`. Leyendo el error tenía que ver con el _inflado_ y ahí es cuando me di cuenta que me faltaba poner el mismo código que tenia en _Main_ el inflado del _layout_ y todas las cosas relacionadas con el _toolbar_ y el _drawer_ también en `activity_settings.xml`.

Con todos estos errores al principio, me preocupé primero de intentar resolverlos antes de ponerme con las `PreferenceScreen` y hacer la pantalla de `Settings` en detalle. Resultó ser sencillo. Es curioso que lo que más difícil me ha parecido de la práctica no tiene que ver con su tema.

El único problema que me encontré que tampoco es culpa de las `PreferenceScreen` es que al hacer el fragmento donde vemos que podemos acceder a la configuración básica o a la avanzada no conseguía que navegase a pesar de estar todo en el `root_preferences`.

Era más cosa que no había preparado un _fragment_ para que se viese la "sub-pantalla". Lo declaré por código rápidamente para comprobar que funcionase y así se quedó. Es el método `onPreferenceTreeClick`.
package es.upm.miw.ficheros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class FicherosActivity extends AppCompatActivity {

    private String RUTA_FICHERO;

    /**
     * SD card
     **/
    EditText lineaTexto;
    Button botonAniadir;
    TextView contenidoFichero;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private boolean storeInSDCard;


    @Override
    protected void onStart() {
        super.onStart();
        mostrarContenido(contenidoFichero);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheros);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.refreshFilePath(preferences);

        lineaTexto = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir = (Button) findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);

        /** SD card **/
        listener = generateOnSharedPreferenceChangeListener();
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener generateOnSharedPreferenceChangeListener() {
        return new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                Log.i("CHANGED_PREFERENCE", key);
                FicherosActivity.this.refreshFilePath(prefs);
                FicherosActivity.this.refreshMenuStorageIcons();
            }
        };
    }

    private void refreshFilePath(SharedPreferences preferences) {
        storeInSDCard = preferences.getBoolean("sd_card_storage", false);
        String fileName = preferences.getString("file_name_storage", "miFichero.txt");

        Log.i("STORAGE_PATH", Boolean.toString(storeInSDCard));
        Log.i("STORAGE_FILE", fileName);

        if (storeInSDCard) {
            RUTA_FICHERO = getExternalFilesDir(null) + "/" + fileName;
        } else {
            RUTA_FICHERO = getFilesDir() + "/" + fileName;
        }

        Log.i("FULL_STORAGE_PATH", RUTA_FICHERO);
    }

    private void refreshMenuStorageIcons() {
        invalidateOptionsMenu();
    }

    /**
     * Al pulsar el botón añadir -> añadir al fichero.
     * Después de añadir -> mostrarContenido()
     *
     * @param v Botón añadir
     */
    public void accionAniadir(View v) {
        /** Comprobar estado SD card **/
        String estadoTarjetaSD = Environment.getExternalStorageState();

        if (!isExternalStorageSelectedAndMounted()) {
            Toast.makeText(this, R.string.txtErrorSD, Toast.LENGTH_SHORT).show();
            return; // Clausula de guarda (Guard Clause)
        }

        try {  // Añadir al fichero
            /** SD card **/
            FileOutputStream fos = new FileOutputStream(RUTA_FICHERO, true);
            fos.write(lineaTexto.getText().toString().getBytes());
            fos.write('\n');
            fos.close();
            mostrarContenido(contenidoFichero);
            Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Se pulsa sobre el textview -> mostrar contenido del fichero
     * Si está vacío -> mostrar un Toast
     *
     * @param textviewContenidoFichero TextView contenido del fichero
     */
    public void mostrarContenido(View textviewContenidoFichero) {
        boolean hayContenido = false;
        File fichero = new File(RUTA_FICHERO);
        String estadoTarjetaSD = Environment.getExternalStorageState();
        contenidoFichero.setText("");

        if (!fichero.exists()) {
            return; // Clausula de guarda (Guard Clause)
        }

        if (!isExternalStorageSelectedAndMounted()) {
            Toast.makeText(this, R.string.txtErrorSD, Toast.LENGTH_SHORT).show();
            return; // Clausula de guarda (Guard Clause)
        }

        try {
            /** SD card **/
            BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
            String linea = fin.readLine();
            while (linea != null) {
                hayContenido = true;
                contenidoFichero.append(linea + '\n');
                linea = fin.readLine();
            }
            fin.close();
            Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Vaciar el contenido del fichero, la línea de edición y actualizar
     */
    public void borrarContenido() {
        String estadoTarjetaSD = Environment.getExternalStorageState();

        if (!isExternalStorageSelectedAndMounted()) {
            Toast.makeText(this, R.string.txtErrorSD, Toast.LENGTH_SHORT).show();
            return; // Clausula de guarda (Guard Clause)
        }

        try {  // Vaciar el fichero
            /** SD card **/
            // FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
            FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
            fos.close();
            Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
            lineaTexto.setText(""); // limpio la linea de edición
            mostrarContenido(contenidoFichero);
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Funcion que devuelve false en caso de que se seleccione almacenamiento SD pero la tarjeta
     * no este montada.
     */
    private boolean isExternalStorageSelectedAndMounted() {
        /** Comprobar estado SD card **/
        String estadoTarjetaSD = Environment.getExternalStorageState();
        if (storeInSDCard) {
            if (!estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                return false;
            }
        }
        return true;
    }

    @Override
    /**
     * Añade el menú con la opcion de vaciar el fichero
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionExternalStorage = menu.findItem(R.id.actionExternalStorage);
        MenuItem actionLocalStorage = menu.findItem(R.id.actionLocalStorage);

        if (storeInSDCard) {
            actionLocalStorage.setVisible(false);
            actionExternalStorage.setVisible(true);
        } else {
            actionLocalStorage.setVisible(true);
            actionExternalStorage.setVisible(false);
        }

        Log.i("MENU", "called onPrepare. Updating menu.");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accionVaciar:
                borrarContenido();
                break;
            case R.id.accionConfigurar:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }

        return true;
    }

}

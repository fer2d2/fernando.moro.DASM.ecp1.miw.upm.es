package es.upm.miw.ficheros;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilesManagerActivity extends AppCompatActivity implements RecyclerViewClickListener {

    ExpandableListAdapter listAdapter;
    ExpandableListView storedFilesListView;
    List<String> listDataHeader;
    HashMap<String, List<File>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_manager);

        storedFilesListView = (ExpandableListView) findViewById(R.id.storedFilesList);
        populateFileLists();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, this);
        storedFilesListView.setAdapter(listAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_files_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.accionEliminarFicheros:
                removeAllFiles();
                reloadListAdapter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateFileLists() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        populateLocalFilesList();
        populateExternalStorageFilesList();
    }

    private void populateExternalStorageFilesList() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            listDataHeader.add(getString(R.string.opcionAlmacenamientoExterno));
            String externalStoragePath = getExternalFilesDir(null).getPath();
            List<File> sdCardFiles = new ArrayList<>();

            if (this.getFiles(externalStoragePath) != null) {
                sdCardFiles = this.getFiles(externalStoragePath);
            }

            listDataChild.put(listDataHeader.get(listDataHeader.indexOf(getString(R.string.opcionAlmacenamientoExterno))), sdCardFiles);
        }
    }

    private void populateLocalFilesList() {
        listDataHeader.add(getString(R.string.opcionAlmacenamientoLocal));
        String localStoragePath = getFilesDir().getPath();
        List<File> localStorageFiles = new ArrayList<>();

        if (this.getFiles(localStoragePath) != null) {
            localStorageFiles = this.getFiles(localStoragePath);
        }

        listDataChild.put(listDataHeader.get(listDataHeader.indexOf(getString(R.string.opcionAlmacenamientoLocal))), localStorageFiles);
    }

    private ArrayList<File> getFiles(String path) {
        ArrayList<File> foundFiles = new ArrayList<>();
        File[] allFiles = new File(path).listFiles();

        if (allFiles.length == 0) {
            return null;
        } else {
            for (File file : allFiles) {
                foundFiles.add(file);
            }
        }
        return foundFiles;
    }

    private void removeFile(int groupNumber, int elementNumber) {
        File chosenFile = (File) this.listAdapter.getChild(groupNumber, elementNumber);

        if (!chosenFile.delete()) {
            Toast.makeText(this, R.string.fileAlreadyDeleted, Toast.LENGTH_SHORT).show();
        }
    }

    private void removeAllFiles() {
        for (int groupNumber = 0; groupNumber < listDataHeader.size(); groupNumber++) {
            for (int elementNumber = 0; elementNumber < listDataChild.get(listDataHeader.get(groupNumber)).size(); elementNumber++) {
                this.removeFile(groupNumber, elementNumber);
            }
        }
    }

    /**
     * Metodo que utilizamos para captar el listener de una vista anidada, el cual se ejecuta desde
     * el Adapter correspondiente.
     */
    @Override
    public void recyclerViewListClicked(View v) {
        Pair<Integer, Integer> chosenFile = (Pair<Integer, Integer>) v.getTag();
        this.removeFile(chosenFile.first, chosenFile.second);
        reloadListAdapter();
    }

    private void reloadListAdapter() {
        this.populateFileLists();
        this.listAdapter.setListDataChild(this.listDataChild);
        this.listAdapter.notifyDataSetChanged();
    }
}

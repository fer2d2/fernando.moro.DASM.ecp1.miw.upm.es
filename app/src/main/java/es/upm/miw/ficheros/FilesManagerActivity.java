package es.upm.miw.ficheros;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateFileLists() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<File>>();

        listDataHeader.add(getString(R.string.opcionAlmacenamientoExterno));
        listDataHeader.add(getString(R.string.opcionAlmacenamientoLocal));

        String externalStoragePath = getExternalFilesDir(null).getPath();
        String localStoragePath = getFilesDir().getPath();

        List<File> sdCardFiles = new ArrayList<>();
        List<File> localStorageFiles = new ArrayList<>();

        if(this.getFiles(externalStoragePath) != null) {
            sdCardFiles = this.getFiles(externalStoragePath);
        }

        if(this.getFiles(localStoragePath) != null) {
            localStorageFiles = this.getFiles(localStoragePath);
        }

        listDataChild.put(listDataHeader.get(0), sdCardFiles);
        listDataChild.put(listDataHeader.get(1), localStorageFiles);
    }

    private void removeFile(int groupNumber, int elementNumber) {
        File chosenFile = (File) this.listAdapter.getChild(groupNumber, elementNumber);

        if(!chosenFile.delete()) {
            Toast.makeText(this, "El fichero ya ha sido eliminado", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void recyclerViewListClicked(View v) {
        Pair<Integer, Integer> chosenFile = (Pair<Integer, Integer>) v.getTag();
        this.removeFile(chosenFile.first, chosenFile.second);
        this.populateFileLists();

        this.listAdapter.update(this.listDataHeader, this.listDataChild);
        this.listAdapter.notifyDataSetChanged();
    }
}

package es.upm.miw.ficheros;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilesManagerActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView storedFilesListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_manager);

        storedFilesListView = (ExpandableListView) findViewById(R.id.storedFilesList);
        populateFileLists();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        storedFilesListView.setAdapter(listAdapter);

        storedFilesListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();

                return false;
            }
        });

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
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add(getString(R.string.opcionAlmacenamientoExterno));
        listDataHeader.add(getString(R.string.opcionAlmacenamientoLocal));

        String externalStoragePath = getExternalFilesDir(null).getPath();
        String localStoragePath = getFilesDir().getPath();

        List<String> sdCardFiles = this.getFiles(externalStoragePath);
        List<String> localStorageFiles = this.getFiles(localStoragePath);

        listDataChild.put(listDataHeader.get(0), sdCardFiles);
        listDataChild.put(listDataHeader.get(1), localStorageFiles);
    }

    private ArrayList<String> getFiles(String path) {
        ArrayList<String> foundFiles = new ArrayList<String>();
        File[] allFiles = new File(path).listFiles();

        if (allFiles.length == 0) {
            return null;
        } else {
            for (File file : allFiles) {
                foundFiles.add(file.getName());
            }
        }
        return foundFiles;
    }
}

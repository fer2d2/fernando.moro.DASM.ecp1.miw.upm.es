package es.upm.miw.ficheros;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<File>> listDataChild;
    private RecyclerViewClickListener clickListener;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<File>> listDataChild, RecyclerViewClickListener clickListener) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.clickListener = clickListener;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        File childFile = (File) getChild(groupPosition, childPosition);
        final String childText = childFile.getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.storedFilesListItem);

        txtListChild.setText(childText);

        ImageButton buttonRemoveChild = (ImageButton) convertView.findViewById(R.id.actionRemoveFile);
        buttonRemoveChild.setFocusable(false);
        buttonRemoveChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BOTON", Integer.toString(childPosition));
                Pair<Integer, Integer> element = new Pair<>(groupPosition, childPosition);
                v.setTag(element);
                clickListener.recyclerViewListClicked(v);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView storedFilesListHeader = (TextView) convertView
                .findViewById(R.id.storedFilesListHeader);
        storedFilesListHeader.setTypeface(null, Typeface.BOLD);
        storedFilesListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setListDataChild(HashMap<String, List<File>> listDataChild) {
        this.listDataChild = listDataChild;
    }
}

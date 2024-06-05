package consulta;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.sucen.chagas.NotificacaoFragment;
import com.sucen.chagas.R;

/**
 * Created by henrique on 25/05/2016.
 */
public class ConsultaListAdapter extends BaseExpandableListAdapter {
    private final SparseArray<Grupo> groups;
    public LayoutInflater inflater;
    public Activity activity;
    int statusChild;

    public ConsultaListAdapter(Activity act, SparseArray<Grupo> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition).getTexto();
    }

    public String getNome(int groupPosition){
        String[] matriz = groups.get(groupPosition).string.split("-");
        return matriz[0];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition).getId();
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        final Long id = getChildId(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.consulta_detalhe, null);
        }
        text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(children);
        if (statusChild == 0){
            text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.editavel, 0);
        } else {
            text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.enviado, 0);
        }
       // Log.w("id",id.toString());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaEdicao(id);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.consulta_grupo, null);
        }
        Grupo group = (Grupo) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        statusChild = group.status;
        if (group.status == 0){
            ((CheckedTextView) convertView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.editavel,0,0,0);
        } else {
            ((CheckedTextView) convertView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.enviado,0,0,0);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void chamaEdicao(Long id){
        Fragment frag = new NotificacaoFragment();

        // Creating a Bundle object
        Bundle data = new Bundle();

        // Setting the index of the currently selected item of mDrawerList
        data.putLong("id_inseto", id);

        // Setting the id
        frag.setArguments(data);
        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = activity.getFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // Adding a fragment to the fragment transaction
        ft.replace(R.id.content_frame, frag);

        // Committing the transaction
        ft.commit();
    }
}

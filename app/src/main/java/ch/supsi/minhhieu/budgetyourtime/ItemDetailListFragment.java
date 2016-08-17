package ch.supsi.minhhieu.budgetyourtime;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.ItemAdapter;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDetailListFragment extends Fragment {


    @BindView(R.id.item_detaillistview)
    ListView itemList;
    public long budgetID;
    public long itemID;
    private Item item = new Item();
    private DBHelper db;
    private ItemAdapter itemAdapter;
    private String budgetName;

    public ItemDetailListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(this.getContext());
        Bundle bundle = getArguments();
        if(bundle!= null) {
            budgetID = bundle.getLong("budgetID");
            budgetName = bundle.getString("budgetName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail_list, container, false);
        ButterKnife.bind(this,view);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Budget Detail: "+budgetName);
        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.add_new_activity);
        fab.hide();
        final List<Item> list = db.getItemsByBudget(budgetID);
        if (list.size()>0) {
            itemAdapter = new ItemAdapter(this.getContext(), list, db, budgetName);
            itemList.setAdapter(itemAdapter);
        }
        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                item = list.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String[] itemEdit = {"Edit activity","Delete activity"};
                builder.setTitle("Activity Edit Options:");
                builder.setItems(itemEdit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                editItem();
                                break;
                            case 1:
                                showDeletionDialog();
                                break;
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
        return view;
    }

    public void editItem(){
        Intent itemIntent = new Intent(getActivity(), AddEditItemActivity.class);
        itemIntent.putExtra("typeOfDialog",AddEditItemActivity.EDIT_ITEM);
        itemIntent.putExtra("itemID",item.getId());
        startActivityForResult(itemIntent,AddEditItemActivity.EDIT_ITEM);
    }

    public void showDeletionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Activity");
        builder.setMessage("Do you want to delete this activity?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            boolean returnval = false;
            public void onClick(DialogInterface dialog, int which) {
                returnval = db.deleteItem(item);
                if (returnval == true){
                    Toast.makeText(getActivity(), R.string.delete_item_ok, Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("budgetName",budgetName);
                    bundle.putLong("budgetID",budgetID);
                    openItemListFragment(bundle);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), R.string.delete_item_failed, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case AddEditItemActivity.EDIT_ITEM:
                Bundle bundle = new Bundle();
                bundle.putString("budgetName",budgetName);
                bundle.putLong("budgetID",budgetID);
                openItemListFragment(bundle);
                break;
        }
    }

    public void openItemListFragment(Bundle args){
        ItemDetailListFragment newFragment = new ItemDetailListFragment();
        newFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

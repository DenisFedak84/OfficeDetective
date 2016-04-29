package criminalintent.android.bignerdranch.com.criminalintent.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import criminalintent.android.bignerdranch.com.criminalintent.R;
import criminalintent.android.bignerdranch.com.criminalintent.model.Crime;
import criminalintent.android.bignerdranch.com.criminalintent.model.CrimeLab;

public class CrimeListFragment extends ListFragment {
    private static final String TAG = "CrimeListFragment";
    private ArrayList<Crime> mCrimes;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    // Обязательный интерфейс для активности - хоста
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // теперь у CrimeListFragment имеется механизм вызова методов активности-хоста
        // Не важно какая активность является хостом
        Activity activity = (Activity) context;
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mSubtitleVisible = false;

        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getmCrimes();

        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        // вспомогательный метод ListFragment назначает адаптер ListView
        setListAdapter(adapter);
    }

    public void updateUI() {
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //getListAdapter()- вспомогательный метод ListFragment возвращает адаптер
        Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
        mCallbacks.onCrimeSelected(c);
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.fragment_crime_list,menu);
//        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
//        if(mSubtitleVisible && showSubtitle != null){
//            showSubtitle.setTitle(R.string.hide_subtitle);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        // ListView listView = (ListView)v.findViewById(R.id.lv_list);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        ListView listView = (ListView) v.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mSubtitleVisible) {
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // контекстное меню для Froyo и Gringerbread
            registerForContextMenu(listView);
        } else {
            // контекстная панель действий для Honeycomb и выше
            // множественный выбор элементов списка
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_crime:
                            CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
                            CrimeLab crimeLab = CrimeLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                // если элемент выбран удаляем его
                                if (getListView().isItemChecked(i)) {
                                    crimeLab.deleteCrime(adapter.getItem(i));
                                }
                            }
                            mode.finish();
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
                mCallbacks.onCrimeSelected(crime);
            }
        });
        return v;
    }

//    @TargetApi(11)
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menu_item_new_crime:
//                Crime crime = new Crime();
//                CrimeLab.get(getActivity()).addCrime(crime);
//                ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
//                mCallbacks.onCrimeSelected(crime);
//                return true;
//            case R.id.menu_item_show_subtitle:
//                if(getActivity().getActionBar().getSubtitle() == null) {
//                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
//                    mSubtitleVisible = true;
//                    item.setTitle(R.string.hide_subtitle);
//                }else{
//                    getActivity().getActionBar().setSubtitle(null);
//                    mSubtitleVisible = false;
//                    item.setTitle(R.string.show_subtitle);
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
        Crime crime = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Если мы не получили представление, заполняем его
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
            }
            // Настройка представления для объекта Crime
            Crime c = getItem(position);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());
            TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            dateTextView.setText(df.format("EEEE, MMM dd, yyyy", c.getDate()));
//            dateTextView.setText(c.getDate().toString());
            CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solved_CheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }
}

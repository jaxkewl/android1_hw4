package com.marshong.homework4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.marshong.homework4.data.DBHelper;
import com.marshong.homework4.model.Task;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MyTag";


    List<Task> tasks;
    ListView mListView;
    ArrayAdapter<Task> mArrayAdapterTask;

    DBHelper dbHelper;


    private void populateDB() {
        dbHelper = new DBHelper(this);
        for (int i = 0; i < 10; i++) {
            dbHelper.insertTask("task" + i, " descr " + i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate MainActivity");


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMainTaskList, new PlaceholderFragment())
                    .commit();
        }

        //populateDB();

        //init();
        Log.d(TAG, "onCreate MainActivity2");

    }


    private void init() {
        Log.d(TAG, "canErase()");
        dbHelper = new DBHelper(this);

        //get our list of Tasks
        tasks = dbHelper.getTasks();

        //get the list view
        mListView = (ListView) findViewById(R.id.list_view_task_items);

        //setup the array adapter.
        mArrayAdapterTask = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_activated_1, tasks);


        mListView.setAdapter(mArrayAdapterTask);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);


        mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Log.d(TAG, "item selection page " + mListView.getCheckedItemCount());
                // Capture total checked items
                final int checkedCount = mListView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                //mArrayAdapterTask.toggleSelection(position);

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // handle the add task icon
        if (id == R.id.add_task_icon) {
            // start add task activity
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public static final String TAG = "MyTag";

        List<Task> tasks;
        ListView mListView;
        ArrayAdapter<Task> mArrayAdapterTask;

        DBHelper dbHelper;

        public PlaceholderFragment() {
        }


        private void init(final View rootView) {

            Log.d(TAG, "canErase()");
            dbHelper = new DBHelper(rootView.getContext());

            //get our list of Tasks
            tasks = dbHelper.getTasks();

            //get the list view
            mListView = (ListView) rootView.findViewById(R.id.list_view_task_items);

            //setup the array adapter.
            mArrayAdapterTask = new ArrayAdapter<Task>(rootView.getContext(), android.R.layout.simple_list_item_activated_1, tasks);


            mListView.setAdapter(mArrayAdapterTask);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);


            mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    Log.d(TAG, "item selection page " + mListView.getCheckedItemCount());
                    // Capture total checked items
                    final int checkedCount = mListView.getCheckedItemCount();
                    // Set the CAB title according to total checked items
                    mode.setTitle(checkedCount + " Selected");
                    // Calls toggleSelection method from ListViewAdapter Class
                    //mArrayAdapterTask.toggleSelection(position);

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    if (R.id.delete_task_icon == item.getItemId()) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(rootView.getContext());
                        alert.setTitle("Confirm Delete");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "confirmed delete");
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "declined delete");
                            }
                        });
                        alert.show();

                        //get list of selected items
                        SparseBooleanArray checked = mListView.getCheckedItemPositions();
                        int size = checked.size();

                        Log.d(TAG, "clicked on delete icon " + size);
                        for (int i = 0; i < size; i++) {
                            int key = checked.keyAt(i);
                            boolean value = checked.get(key);
                            if (value) {
                                Task tempTask = tasks.get(key);
                                Log.d(TAG, "position: " + i + " key: " + key + " _ID: " + tempTask.getId());

                                dbHelper.deleteTask((Integer.toString(tempTask.getId())));
                            }
                        }


                    }

                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Log.d(TAG, "onCreateView Main Fragment");

            init(rootView);

            return rootView;
        }
    }

    private class TaskListAdapter extends ArrayAdapter<Task> {

        private TaskListAdapter(Context context, int resource, ArrayList<Task> taskList) {
            super(context, resource, taskList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }


}

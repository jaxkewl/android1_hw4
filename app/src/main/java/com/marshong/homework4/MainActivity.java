package com.marshong.homework4;

import android.app.AlertDialog;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.marshong.homework4.data.DBHelper;
import com.marshong.homework4.model.Task;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load up the fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMainTaskList, new TaskListFragment())
                    .commit();
        }
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
    public static class TaskListFragment extends Fragment {

        public static final String TAG = TaskListFragment.class.getSimpleName();

        List<Task> tasks;       //contains all the tasks
        ListView mListView;     //reference to the list view
        ArrayAdapter<Task> mArrayAdapterTask;   //our simple array adapter
        DBHelper dbHelper;      //our db connection

        public TaskListFragment() {
        }


        //this method sets up all the lists
        //we need a reference to the rootView because we are inside a fragment
        private void init(final View rootView) {
            Log.d(TAG, "init()");

            //setup our Database helper
            dbHelper = new DBHelper(rootView.getContext());

            //get our list of Tasks
            tasks = dbHelper.getTasks();

            //get the list view
            mListView = (ListView) rootView.findViewById(R.id.list_view_task_items);

            //setup the array adapter.
            //use the simple list item activated so each selected item remains highlighted
            mArrayAdapterTask = new ArrayAdapter<Task>(rootView.getContext(), android.R.layout.simple_list_item_activated_1, tasks);
            mArrayAdapterTask.setNotifyOnChange(true);

            //set the adapter and the choice mode for multi select
            mListView.setAdapter(mArrayAdapterTask);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

            //set the listener for selecting an item
            mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    Log.d(TAG, "onItemCheckedStateChanged " + mListView.getCheckedItemCount());

                    // get the total # of checked items
                    final int checkedCount = mListView.getCheckedItemCount();

                    //set the title that will appear on the context bar
                    mode.setTitle(checkedCount + " Selected");
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    //here is where we will make the delete icon appear when the user
                    //does multi-select on the list.
                    mode.getMenuInflater().inflate(R.menu.menu_delete, menu);   //menu_delete is separate from menu_main.xml
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    //process a delete request
                    if (R.id.delete_task_icon == item.getItemId()) {
boolean confirmedDelete = false;

                        //confirm user deletion with a modal box.
                        AlertDialog.Builder alert = new AlertDialog.Builder(rootView.getContext());
                        alert.setTitle("Confirm Delete");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "confirmed delete");

                                //get list of selected items
                                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                                int size = checked.size();

                                //iterate through each checked item.
                                for (int i = 0; i < size; i++) {
                                    int key = checked.keyAt(i);
                                    boolean value = checked.get(key);
                                    if (value) {
                                        Task tempTask = tasks.get(key);
                                        Log.d(TAG, "position: " + i + " key: " + key + " _ID: " + tempTask.getId());

                                        dbHelper.getTask(Integer.toString(tempTask.getId()));
                                        dbHelper.deleteTask((Integer.toString(tempTask.getId())));
                                    }
                                }

                                //after deletion, do another query to get all the data
                                tasks = dbHelper.getTasks();

                                //setup a new adapter to reflect the new query
                                mArrayAdapterTask = new ArrayAdapter<Task>(rootView.getContext(), android.R.layout.simple_list_item_activated_1, tasks);
                                mArrayAdapterTask.setNotifyOnChange(true);  //tells list view you have new data
                                mListView.setAdapter(mArrayAdapterTask);    //set the adapter

                                Log.d(TAG, "invalidating options menu");
                                mode.finish();
                                //getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);

                                //getActivity().invalidateOptionsMenu();
                            }
                        });

                        //for now don't do anything if the user changes their mind about deleting.
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "declined delete");
                                Toast.makeText(rootView.getContext(), "Nothing deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alert.show();   //dont forget to show!


                        Log.d(TAG, "Finishing the action");
                        mode.finish();
                    }

                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    Log.d(TAG, "onDestroy()");
                    mArrayAdapterTask.notifyDataSetChanged();
                }
            });
        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            Log.d(TAG, "onCreateOptionsMenu for TaskListFragment");
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            setHasOptionsMenu(true);
            Log.d(TAG, "onCreateView Main Fragment");

            init(rootView); //do all the work in this method

            return rootView;
        }
    }

}

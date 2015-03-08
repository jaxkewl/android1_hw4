package com.marshong.homework4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.marshong.homework4.data.DBHelper;


public class AddTaskActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //load up the fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMainTaskList, new AddTaskFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class AddTaskFragment extends Fragment {
        public static final String TAG = AddTaskFragment.class.getSimpleName();

        DBHelper dbHelper;          //this class's reference to the database, it could have been passed in
        EditText editTextTaskName;
        EditText editTextTaskDescr;

        public AddTaskFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_add_task, container, false);

            dbHelper = new DBHelper(rootView.getContext());

            editTextTaskName = (EditText) rootView.findViewById(R.id.edit_text_task_title);
            editTextTaskDescr = (EditText) rootView.findViewById(R.id.edit_text_task_descr);

            Button buttonAddTask = (Button) rootView.findViewById(R.id.button_add_task);
            buttonAddTask.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //get the name and description and verify
                    String taskName = editTextTaskName.getText().toString().trim();
                    String taskDescr = editTextTaskDescr.getText().toString().trim();

                    //determine if both task name and task description is filled out.
                    boolean validTaskName = !taskName.equals("");
                    boolean validTaskDescr = !taskDescr.equals("");
                    if (!(validTaskDescr && validTaskName)) {
                        Log.d(TAG, "invalid taskName or taskDescr: " + taskName + ": " + taskDescr);

                        //let the user know something is wrong with their task
                        if (!validTaskName) {
                            editTextTaskName.setError("Enter a valid task name");
                        }
                        if (!validTaskDescr) {
                            editTextTaskDescr.setError("Enter a valid task descr");
                        }
                    } else {

                        Log.d(TAG, "inserting task into DB: " + taskName + ": " + taskDescr);

                        //add the task to the Database
                        dbHelper.insertTask(taskName, taskDescr);

                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });

            return rootView;
        }
    }
}

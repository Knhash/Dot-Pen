package knhash.K_Note;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.melnykov.fab.FloatingActionButton;
import com.afollestad.materialdialogs.MaterialDialog;


public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int COUNT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 2;
    private static final int EDIT_ID = Menu.FIRST + 1;

    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setHomeButtonEnabled(true);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        final ListView listview = (ListView) findViewById(R.id.list);
        fillData(listview);
        registerForContextMenu(listview);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                createNote();
            }

        });*/

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(MainActivity.this, NoteEdit.class);
                i.putExtra(NotesDbAdapter.KEY_ROWID, id);
                startActivityForResult(i, ACTIVITY_EDIT);

                /*//View v = (View)view.getParent();
                TextView txt2 = (TextView) view.findViewById(R.id.text2);
                String s = txt2.getText().toString();
                int count = Integer.parseInt(s);
                count++;
                txt2.setText(String.valueOf(count));

                txt2.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);*/
            }
        });

        /*listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                mDbHelper.deleteNote(id);
                ListView listview = (ListView) findViewById(R.id.list);
                fillData(listview);
                return true;
            }
        });*/
    }

    private void fillData(ListView listview) {
        // Get all of the rows from the database and create the item list
        Cursor mNotesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(mNotesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE,NotesDbAdapter.KEY_COUNT};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1,R.id.text2};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor, from, to);
        listview.setAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

        //super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        //return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, COUNT_ID, 0, R.string.menu_count);
        menu.add(2, DELETE_ID, 2, R.string.menu_delete);
        menu.add(1, EDIT_ID, 1, R.string.menu_edit);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case COUNT_ID:
                Toast.makeText(this, "ToDo : Counter", Toast.LENGTH_SHORT).show();
                return true;
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                ListView listview = (ListView) findViewById(R.id.list);
                fillData(listview);
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent i = new Intent(MainActivity.this, NoteEdit.class);
                i.putExtra(NotesDbAdapter.KEY_ROWID, info.id);
                startActivityForResult(i, ACTIVITY_EDIT);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        ListView listview = (ListView) findViewById(R.id.list);
        fillData(listview);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.INSERT_QUICK) {
            //createNote();
            new MaterialDialog.Builder(this)
                    .title("Quicknote")
                    .content("Add a new note")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            mDbHelper.open();
                            String title = input.toString();
                            mDbHelper.createNote(title, "", "");
                            ListView listview = (ListView) findViewById(R.id.list);
                            fillData(listview);
                        }
                    })
                    .show();
            return true;
        }

        if (id == R.id.INSERT_NOTE) {
            createNote();
            return true;
        }

        if (id == R.id.ABOUT) {
            new MaterialDialog.Builder(this)
                    .title(R.string.abtitle)
                    .content(R.string.abcontent)
                    .titleGravity(GravityEnum.START)
                    .contentGravity(GravityEnum.END)
                    .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}

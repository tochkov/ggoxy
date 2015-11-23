package com.goxapps.goxreader.filechooser.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.goxapps.goxreader.R;
import com.goxapps.goxreader.filechooser.FileManager;
import com.goxapps.goxreader.filechooser.UpdateFilesService;
import com.goxapps.goxreader.reader.FullReaderActivity;

public class FileChooserActivity extends AppCompatActivity {

    private ExtractedFilesFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            Log.e("XXX", "bambam: " + uri.getPath());
            Log.e("XXX", "getScheme: " + uri.getScheme());

            Intent intent = new Intent(this, FullReaderActivity.class);
            intent.putExtra(FullReaderActivity.KEY_SELECTED_FILE_PATH, uri.getPath());
            startActivity(intent);

        }


        fragment = (ExtractedFilesFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentExtractedFiles);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UpdateFilesService.isRunning())
            startService(new Intent(this, UpdateFilesService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_sort_by_date:
                FileManager.getInstance().sortFilesByCriteria(FileManager.SORT_BY_DATE);
                fragment.notifyDataSetChanged();
                return true;

            case R.id.action_sort_by_name:
                FileManager.getInstance().sortFilesByCriteria(FileManager.SORT_BY_NAME);
                fragment.notifyDataSetChanged();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

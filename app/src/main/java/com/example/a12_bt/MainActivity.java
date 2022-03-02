
package com.example.a12_bt;

import static com.example.a12_bt.R.*;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.a12_bt.databinding.ActivityMainBinding;
import android.view.View;
import android.widget.Toast;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Authors Giantte, Jean, Sam
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.floatingActionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        Context ctx = MainActivity.this;
                        Toast.makeText(ctx, string.FABToast, Toast.LENGTH_LONG).show();

                    }
                });
        binding.button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        binding.textView.setText(string.COSC426Greeting);
                    }
                });
    }

    // Overriding onCreateoptionMenu() to make Option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflating menu by overriding inflate() method of MenuInflater class.
        //Inflating here means parsing layout XML to views.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    //Overriding onOptionsItemSelected to perform event on menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        binding.textView.setText(menuItem.getTitle() + " Selected");
        return true;
    }
}
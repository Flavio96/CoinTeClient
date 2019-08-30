package flavio.com.cointeclient;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public static final String COIN_ID = "coin_id";
    public static final String COIN_nation = "nation";
    public static final String COIN_year = "year";
    public static final String COIN_type = "type";
    public static final String COIN_path = "path";
    public static final String COIN_desc = "description";

    private Coin coin;

    GridView gridView;
    Context ctx;
    static Dialog d;

    ArrayMap<String, File> localTmpFileArray = new ArrayMap<String, File>();

    ArrayMap<String, File> imageList = new ArrayMap<>();
    List<Coin> coinList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FirebaseApp.initializeApp(this);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();

        final StorageReference storageRef;
        storageRef = FirebaseStorage.getInstance().getReference();

        ctx = MainActivity.this;
        gridView = findViewById(R.id.gridview);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);

        db.collection("coins").orderBy(COIN_type, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                coin = new Coin();
                                coin.setId((String) document.get(COIN_ID));
                                coin.setImgPath((String) document.get(COIN_path));
                                Long type = (Long)document.get(COIN_type);
                                coin.setValue(type.intValue());
                                coin.setDescription((String) document.get(COIN_desc));
                                coin.setNation((String) document.get(COIN_nation));
                                Long year  = (Long) document.get(COIN_year);
                                coin.setYear(year.intValue());
                                coinList.add(coin);

                                if(coin.getValue() == 0){
                                    String[] y = coin.getImgPath().split("&&");
                                    String[] x = new String[2];
                                    if (y.length < 2){
                                        x[0] = y[0];
                                        x[1] = "";
                                    }else{
                                        x[0] = y[0];
                                        x[1] = y[1];
                                    }

                                    String filename = x[0].substring(x[0].lastIndexOf("/") + 1);

                                    try {
                                        imageList.put(filename, File.createTempFile("coins", ".png"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if(!x[1].equals("")){
                                        String filename2 = x[1].substring(x[1].lastIndexOf("/") + 1);
                                        try {
                                            imageList.put(filename2, File.createTempFile("coins", ".png"));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }else {
                                    String filename = coin.getImgPath().substring(coin.getImgPath().lastIndexOf("/") + 1);
                                    filename = filename.replaceAll("&&", "");
                                    try {
                                        imageList.put(filename, File.createTempFile("coins", ".png"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            for (Coin c : coinList){
                                if(c.getValue() == 0) {
                                    String[] y = c.getImgPath().split("&&");
                                    String[] x = new String[2];
                                    if (y.length < 2) {
                                        x[0] = y[0];
                                        x[1] = "";
                                        String filename = x[0].substring(x[0].lastIndexOf("/") + 1);
                                        filename = filename.replaceAll("&&", "");
                                        populateImageView(filename, storageRef);
                                    } else {
                                        x[0] = y[0];
                                        x[1] = y[1];
                                        String filename = x[0].substring(x[0].lastIndexOf("/") + 1);
                                        filename = filename.replaceAll("&&", "");
                                        populateImageView(filename, storageRef);
                                        String filename2 = x[1].substring(x[1].lastIndexOf("/") + 1);
                                        filename2 = filename2.replaceAll("&&", "");
                                        populateImageView(filename2, storageRef);
                                    }
                                }else {
                                    String filename = c.getImgPath().substring(c.getImgPath().lastIndexOf("/") + 1);
                                    filename = filename.replaceAll("&&", "");
                                    populateImageView(filename, storageRef);
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        d = new Dialog(this);
        d.setContentView(R.layout.activity_detail);

    }
    private void populateImageView(final String serverSideImageName, final StorageReference storageRef) {

        File imageLocalTmpFile = null;

        try {
            imageLocalTmpFile = File.createTempFile("coin", "jpg");
        } catch (IOException e) {
            Log.w("ops", e);
        }
        File localFile = null;
        localTmpFileArray.put(serverSideImageName, imageLocalTmpFile);
        storageRef.child("coins" + File.separator + serverSideImageName).getFile(imageList.get(serverSideImageName))
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                if(serverSideImageName.equals(imageList.keyAt(imageList.size()-1))) {

                    progressBar.setVisibility(View.INVISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                    CoinAdapter coinsAdapter = new CoinAdapter(getApplicationContext(), coinList, imageList);
                    gridView.setAdapter(coinsAdapter);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Context ctx = getApplicationContext();
        if (id == R.id.refresh) {
            Toast.makeText(getApplicationContext(),"refreshing...", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.removeFilter) {

            CoinAdapter coinsAdapter = new CoinAdapter(MainActivity.this, coinList, imageList);
            gridView.setAdapter(coinsAdapter);
            return true;
        }
        if (id == R.id.filter) {final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.filter_diaolog);
            dialog.setTitle("Filter");
            final EditText criteria = dialog.findViewById(R.id.filter_criteria);
            final Spinner filterCoinType = dialog.findViewById(R.id.filter_type);
            Button filterConfirm = dialog.findViewById(R.id.confirm_filter);
            final ArrayList<Coin> filteredList = new ArrayList<Coin>();

            filterCoinType.setSelection(0);
            filterConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    int type = 0;
                    filteredList.removeAll(filteredList);
                    switch(filterCoinType.getSelectedItemPosition()){
                        case 0: type = 0;
                            break;
                        case 1: type = 200;
                            break;
                        case 2: type = 100;
                            break;
                        case 3: type = 50;
                            break;
                        case 4: type = 20;
                            break;
                        case 5: type = 10;
                            break;
                        case 6: type = 5;
                            break;
                        case 7: type = 2;
                            break;
                        case 8: type = 1;
                            break;
                    }
                    for (Coin c : coinList){
                        if(type!=0) {
                            if ((
                                    (c.getDescription() != null && c.getDescription().contains(criteria.getText()))
                                    || (c.getNation() != null && c.getNation().contains(criteria.getText()))
                                    || (c.getYear() != null && c.getYear().toString().contains(criteria.getText()))
                            )&& c.getValue() == type) {
                                filteredList.add(c);
                            }
                        }else{
                            if ((c.getDescription() != null && c.getDescription().contains(criteria.getText()))
                                    || (c.getNation() != null && c.getNation().contains(criteria.getText()))
                                    || (c.getYear() != null && c.getYear().toString().contains(criteria.getText()))) {
                                filteredList.add(c);
                            }
                        }
                    }
                    CoinAdapter coinsAdapter = new CoinAdapter(MainActivity.this, filteredList, imageList);
                    gridView.setAdapter(coinsAdapter);
                }
            });
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

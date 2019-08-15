package flavio.com.cointeclient;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CoinAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Coin> coins;
    private Drawable coinBack, coinFront;
    private ArrayMap<String, File> imageList;

    // 1
    public CoinAdapter(Context mContext, List<Coin> coins, ArrayMap<String, File> imageList) {
        this.mContext = mContext;
        this.coins = coins;
        this.imageList = imageList;
    }

    // 2
    @Override
    public int getCount() {
        return coins.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // 1
        final Coin coin = coins.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.coin_grid_element, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.grid_coin_img);

        final TextView txt = (TextView) convertView.findViewById(R.id.coinType);
        TextView txtId = (TextView) convertView.findViewById(R.id.coinId);

        txtId.setText("#"+coin.getId());

        switch(coin.getValue()) {
            case 200: txt.setText("2 euro");
                break;
            case 100: txt.setText("1 euro");
                break;
            case 50: txt.setText("50 cent");
                break;
            case 20: txt.setText("20 cent");
                break;
            case 10: txt.setText("10 cent");
                break;
            case 5: txt.setText("5 cent");
                break;
            case 2: txt.setText("2 cent");
                break;
            case 1: txt.setText("1 cent");
                break;
            default: txt.setText("Altro");
                break;
        }

        // 4

        String filename=coin.getImgPath().substring(coin.getImgPath().lastIndexOf("/")+1);
        filename = filename.replaceAll("&&", "");
        File localFile = null;
        try {
            localFile = File.createTempFile("coins", ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File temp = imageList.get(filename);
        imageView.setImageURI(Uri.fromFile(temp));


        final View view = convertView;

        return convertView;
    }



}

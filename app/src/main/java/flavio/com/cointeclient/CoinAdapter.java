package flavio.com.cointeclient;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

            File temp = imageList.get(filename);
            imageView.setImageURI(Uri.fromFile(temp));
            /*if(!x[1].equals("")){
                String filename2 = x[1].substring(x[1].lastIndexOf("/") + 1);

                File temp2 = imageList.get(filename2);
                coinBack = new BitmapDrawable(MainActivity.d.getContext().getResources(), BitmapFactory.decodeFile(temp2.getAbsolutePath()));
            }*/

        }else {
            setCoinImg(coin, imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ImageView img = MainActivity.d.findViewById(R.id.coin_img);
                final TextView imgPath = MainActivity.d.findViewById(R.id.txt_coin_img);
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

                    imgPath.setText(""+x[0]);

                    File temp = imageList.get(filename);
                    img.setImageURI(Uri.fromFile(temp));
                    /*if(!x[1].equals("")){
                        String filename2 = x[1].substring(x[1].lastIndexOf("/") + 1);

                        File temp2 = imageList.get(filename2);
                        coinBack = new BitmapDrawable(MainActivity.d.getContext().getResources(), BitmapFactory.decodeFile(temp2.getAbsolutePath()));
                    }*/
                }else {
                    setCoinImg(coin, img);
                    imgPath.setText(coin.getImgPath());
                }

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img.setRotationY(0f);
                        img.animate().rotationY(90f).setListener(new Animator.AnimatorListener()
                        {

                            @Override
                            public void onAnimationStart(Animator animation)
                            {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation)
                            {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation)
                            {
                                switch(coin.getValue()){
                                    case 200:
                                        setImgRes(imgPath, coin, img, R.drawable.two_euros);
                                        break;
                                    case 100:
                                        setImgRes(imgPath, coin, img, R.drawable.one_euro);
                                        break;
                                    case 50:
                                        setImgRes(imgPath, coin, img, R.drawable.fifty_cent);
                                        break;
                                    case 20:
                                        setImgRes(imgPath, coin, img, R.drawable.twenty_cent);
                                        break;
                                    case 10:
                                        setImgRes(imgPath, coin, img, R.drawable.ten_cent);
                                        break;
                                    case 5:
                                        setImgRes(imgPath, coin, img, R.drawable.five_cent);
                                        break;
                                    case 2:
                                        setImgRes(imgPath, coin, img, R.drawable.two_cent);
                                        break;
                                    case 1:
                                        setImgRes(imgPath, coin, img, R.drawable.one_cent);
                                        break;
                                    case 0:
                                        String[] x = coin.getImgPath().split("&&");
                                    if(imgPath.getText().equals(x[0])) {
                                        if (x[1].isEmpty()){
                                            img.setImageResource(R.drawable.not_found);
                                            imgPath.setText("");
                                        }
                                        else {
                                            String filename = x[1].substring(x[1].lastIndexOf("/") + 1);
                                            File temp = imageList.get(filename);
                                            img.setImageURI(Uri.fromFile(temp));
                                            imgPath.setText(x[1]);
                                        }
                                    }
                                    else {
                                        String filename = x[0].substring(x[0].lastIndexOf("/") + 1);
                                        File temp = imageList.get(filename);
                                        img.setImageURI(Uri.fromFile(temp));
                                        imgPath.setText(x[0]);
                                    }
                                    break;
                                }

                                img.setRotationY(270f);
                                img.animate().rotationY(360f).setListener(null);

                            }

                            @Override
                            public void onAnimationCancel(Animator animation)
                            {
                            }
                        });
                    }
                });

                TextView txtID = MainActivity.d.findViewById(R.id.txt_coin_id);
                txtID.setText("#"+coin.getId());
                TextView txtType = MainActivity.d.findViewById(R.id.txt_coin_type);
                txtType.setText(txt.getText());
                TextView txtDesc = MainActivity.d.findViewById(R.id.txt_coin_description);
                txtDesc.setText(coin.getDescription());
                MainActivity.d.show();
            }
        });


        final View view = convertView;


        return convertView;
    }

    public void setCoinImg(Coin coin, ImageView img){
        String filename = coin.getImgPath().substring(coin.getImgPath().lastIndexOf("/") + 1);
        filename = filename.replaceAll("&&", "");
        File temp = imageList.get(filename);
        img.setImageURI(Uri.fromFile(temp));

    }

    public void setImgRes(TextView imgPath, Coin coin, ImageView img, int resId){
        if(imgPath.getText().equals(coin.getImgPath())) {
            img.setImageResource(resId);
            imgPath.setText("back");
        }else {
            setCoinImg(coin, img);
            imgPath.setText(coin.getImgPath());
        }
    }



}

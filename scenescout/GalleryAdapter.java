package com.example.mena.scenescout;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mena.scenescout.Acitivties.CreatePostActivity;
import com.example.mena.scenescout.Acitivties.MenuActivity;
import com.example.mena.scenescout.Acitivties.PostDetailActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter{

    private ArrayList<Bitmap> items;
    private SparseBooleanArray selectedItems;
    private Context context;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    public GalleryAdapter(ArrayList<Bitmap> imageList) {
        if (imageList == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = imageList;
        selectedItems = new SparseBooleanArray();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_container,parent,false);
        return new GalleryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((GalleryListViewHolder) holder).bindVIew(position);
    }


    private class GalleryListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title;
        private TextView city;
        private ImageView image, expandedImage;

        public GalleryListViewHolder(View listview)
        {
            super(listview);
            context = itemView.getContext();
            image = listview.findViewById(R.id.imageGallery);
            //expandedImage = listview.findViewById(R.id.expanded_image);
            title = listview.findViewById(R.id.imageTitle);
            //city = (TextView) listview.findViewById(R.id.address);
            //listview.setOnClickListener(this);
            listview.setOnClickListener(this);
            Log.d("check listener", "Added listener");
        }

        public void bindVIew(int position)
        {
            final Bitmap currentImage = items.get(position);
            //String[] images = currentPost.getImageUrls();
            //List<String> images = new ArrayList<String>();
            //images.add(currentImage.toString());
            //new DownLoadImageTask(image).execute(images.get(position));
            Log.i("gallerycheck", currentImage.toString());
            //image.setImageResource(image[1]);
            int num = position +1;
            title.setText("Image " + num);
            image.setImageBitmap(currentImage);
            //expandedImage.setImageBitmap(currentImage);
            //city.setText(currentPost.getCity());

        }

        @Override
        public void onClick(View view) {
            Log.d("Onclick", "Called");
            //Intent myintent = new Intent(view.getContext(),MenuActivity.class);
            //context.startActivity(myintent);
            Toast.makeText(context,"clicked", Toast.LENGTH_SHORT).show();
            image.setVisibility(View.GONE);

        }
    }

    private class DownLoadImageTask extends AsyncTask<Bitmap,Void,Bitmap>{
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(Bitmap...urls){
            Bitmap urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                //InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                //logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

}

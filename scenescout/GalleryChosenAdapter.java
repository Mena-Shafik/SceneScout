package com.example.mena.scenescout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GalleryChosenAdapter extends RecyclerView.Adapter {

    private ArrayList<String> items;
    private SparseBooleanArray selectedItems;
    private Context context;
    private TextView title;
    private ImageView image;

    public GalleryChosenAdapter(ArrayList<String> imageList) {
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //((GalleryChosenAdapter.GalleryListViewHolder) holder).bindView(position);
        ((GalleryListViewHolder) holder).bindView(position);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_container,parent,false);
        return new GalleryChosenAdapter.GalleryListViewHolder(view);
    }

    private class GalleryListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public GalleryListViewHolder(View listview)
        {
            super(listview);
            context = itemView.getContext();
            image = (ImageView)listview.findViewById(R.id.imageGallery);
            title = (TextView) listview.findViewById(R.id.imageTitle);
            //city = (TextView) listview.findViewById(R.id.address);
            //listview.setOnClickListener(this);
        }

        public void bindView(int position)
        {
            String currentImage = items.get(position);
            //String[] images = currentPost.getImageUrls();
            //ArrayList<String> images = new ArrayList<String>();
            //images.add(currentImage.toString());
            Log.i("display gallery check", currentImage.toString());
            //new GalleryChosenAdapter.DownloadImageTask(image).execute(images.get(position));
            new DownLoadImageTask(image).execute(currentImage);
            int num = position +1;
            //image.setImageURI(Uri.parse(currentImage));
            title.setText("Image " + num);



        }

        @Override
        public void onClick(View view) {

        }
    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap>{
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap bitmap = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                bitmap = BitmapFactory.decodeStream(is);

            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return bitmap;
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

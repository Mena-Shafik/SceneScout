package com.example.mena.scenescout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mena.scenescout.Acitivties.PostDetailActivity;
import com.example.mena.scenescout.Model.LocationSpec;
import com.example.mena.scenescout.Model.Post;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PostReAdapter extends RecyclerView.Adapter implements Filterable{


    private List<Post> items;
    private List<Post> postListFiltered;
    private SparseBooleanArray selectedItems;
    private Context context;
    private Bundle bundle = new Bundle();
    Random r = new Random();
    Post currentPost;

    public PostReAdapter(ArrayList<Post> postList) {
        if (postList == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = postList;
        postListFiltered = postList;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_container,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public void setFilter(ArrayList<Post> newList)
    {
        postListFiltered = new ArrayList<>();
        postListFiltered.addAll(newList);
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                items = (List<Post>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Post> postFilter = new ArrayList<Post>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < postListFiltered.size(); i++) {
                    Post aPost = postListFiltered.get(i);
                    if (aPost.getTitle().toLowerCase().startsWith(constraint.toString())|aPost.getCity().toLowerCase().startsWith(constraint.toString()))  {
                        postFilter.add(aPost);
                    }
                }

                results.count = postFilter.size();
                results.values = postFilter;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }


    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title;
        private TextView city;
        private ImageView image;
        private RatingBar rate;

        public ListViewHolder(View listview)
        {
            super(listview);
            context = itemView.getContext();
            image = listview.findViewById(R.id.imageView_location);
            rate = listview.findViewById(R.id.listRatingBar);
            title = listview.findViewById(R.id.title);
            city = listview.findViewById(R.id.address);
            listview.setOnClickListener(this);
        }

        public void bindView(int position)
        {
            currentPost = items.get(position);
            //String[] images = currentPost.getImageUrls();
            List<String> images = new ArrayList<String>();
            images = currentPost.getImageUrls();
            new DownLoadImageTask(image).execute(images.get(0));

            //image.setImageResource(image[1]);
            title.setText(currentPost.getTitle());
            city.setText(currentPost.getCity());
            rate.setRating(currentPost.getRating());
        }

        @Override
        public void onClick(View view) {
            Intent myintent = new Intent(view.getContext(),PostDetailActivity.class);

            bundle.putString("title", items.get(getLayoutPosition()).getTitle());
            bundle.putString("street",items.get(getLayoutPosition()).getStreet().toString());
            //Log.i("Street", items.get(getLayoutPosition()).getCostRate());
            bundle.putString("city", items.get(getLayoutPosition()).getCity());
            bundle.putString("prov", items.get(getLayoutPosition()).getProvince());
            bundle.putString("desc", items.get(getLayoutPosition()).getPostDesc());
            bundle.putInt("starRating",items.get(getLayoutPosition()).getRating());
            bundle.putString("price",items.get(getLayoutPosition()).getCost());
            bundle.putString("rate",items.get(getLayoutPosition()).getCostRate().toString());
            bundle.putStringArrayList("tags",items.get(getLayoutPosition()).getTagList());
            bundle.putString("lati", items.get(getLayoutPosition()).getLatitude());
            bundle.putString("long", items.get(getLayoutPosition()).getLongitude());

            LocationSpec spec = items.get(getLayoutPosition()).getSpec();
            bundle.putBoolean("eating", spec.getEatingArea());
            bundle.putBoolean("elec", spec.getElectricity());
            bundle.putBoolean("food", spec.getFoodResit());
            bundle.putBoolean("garage", spec.getGarage());
            bundle.putBoolean("kitchen", spec.getKitchen());
            bundle.putBoolean("parking", spec.getParking());
            bundle.putBoolean("pets", spec.getPets());
            bundle.putBoolean("smoking", spec.getSmoking());
            bundle.putBoolean("wheel", spec.getWheelchair());
            bundle.putBoolean("wifi", spec.getWifi());
            bundle.putBoolean("restroom",spec.getRestroom());

            bundle.putStringArrayList("imglist",items.get(getLayoutPosition()).getImageUrls());
            myintent.putExtras(bundle);
            context.startActivity(myintent);
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
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
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


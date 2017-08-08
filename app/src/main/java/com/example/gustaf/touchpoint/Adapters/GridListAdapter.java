package com.example.gustaf.touchpoint.Adapters;

/**
 * Created by Gustaf on 16-08-04.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustaf.touchpoint.BaseActivity;
import com.example.gustaf.touchpoint.DetailsActivity;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.HelpClasses.Holder;
import com.example.gustaf.touchpoint.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 */
public class GridListAdapter extends RecyclerView.Adapter<Holder> {

    private final int mDefaultSpanCount;
    private List<CityObject>  mItemList;
    private final int      SCREEN_WIDTH;
    private final int     SCREEN_HEIGHT;
    private Context             context;
    private String tmpImage = "https://upload.wikimedia.org/wikipedia/en/7/78/Small_scream.png";

    public GridListAdapter(List<CityObject> itemList, GridLayoutManager gridLayoutManager, int defaultSpanCount, int width, int height, Context context) {
        mItemList = itemList;
        mDefaultSpanCount = defaultSpanCount;
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isHeaderType(position) ? mDefaultSpanCount : 1;
            }
        });
        this.SCREEN_HEIGHT = height;
        this.SCREEN_WIDTH = width;
        this.context = context;

    }

    private boolean isHeaderType(int position) {
        return position == 0 ? true : false;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;

        if(viewType == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_item_layout, viewGroup, false);

        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item_layout, viewGroup, false);

        }
        return new Holder(view);
    }


    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if(position == 0) {
            bindHeaderItem(holder, position);
        } else {
            bindGridItem(holder, position);
        }
    }

    /**
     * This method is used to bind grid item value
     *
     * @param holder
     * @param position
     */
    private void bindGridItem(Holder holder, final int position) {
        final View container = holder.itemView;

        ImageView imgView;
        imgView = (ImageView)container.findViewById(R.id.grid_image);





        final CityObject item = (CityObject) mItemList.get(position);
        TextView txtView = (TextView)container.findViewById(R.id.gridTextTitle);
        txtView.setText(item.getName());

        TextView txtView2 = (TextView)container.findViewById(R.id.distanceTextView);
        txtView2.setText(item.getDistance());

        container.setLayoutParams(new FrameLayout.LayoutParams(SCREEN_WIDTH/2, SCREEN_WIDTH/2));
        imgView.setLayoutParams(new FrameLayout.LayoutParams(SCREEN_WIDTH/2, SCREEN_WIDTH/2));

        //imgView.setImageResource(item.getImage().get(0));

        Picasso.with(context).load(item.getImgs().get(0)).into(imgView);


        container.setOnClickListener(gridPress);
    }



    View.OnClickListener gridPress = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView txtView = (TextView)view.findViewById(R.id.gridTextTitle);

                    int pos = -1;
                    String title = "";
                    String itemText = txtView.getText().toString();
                    for (int i = 0; i<mItemList.size(); i++){
                        if (mItemList.get(i).getName() == itemText){
                            title = mItemList.get(i).getName();
                            pos = i;
                        }
                    }

                    CityObject item = mItemList.get(pos);

                    final BaseActivity activity = (BaseActivity) context;

                    ImageView sharedImage = pos == 0 ? (ImageView)view.findViewById(R.id.header_image) :
                            (ImageView)view.findViewById(R.id.grid_image);
                    sharedImage.setTransitionName("myImage");
                    //activity.postponeEnterTransition();
                    Intent i = new Intent(context, DetailsActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, sharedImage, "myImage");

                    i.putExtra("cityobject", item);
                    context.startActivity(i, options.toBundle());


                }
    };

    /**
     * This method is used to bind the header with the corresponding item position information
     *
     * @param holder
     * @param position
     */
    private void bindHeaderItem(Holder holder, int position) {
        final CityObject itm = mItemList.get(position);
        View container = holder.itemView;


        TextView title = (TextView) container.findViewById(R.id.gridTextTitle);
        title.setText(itm.getName());
        ImageView imgView = (ImageView)container.findViewById(R.id.header_image);

        imgView.setLayoutParams(new FrameLayout.LayoutParams(SCREEN_WIDTH, SCREEN_HEIGHT/2-200));
        holder.itemView.setLayoutParams(new FrameLayout.LayoutParams(SCREEN_WIDTH, SCREEN_HEIGHT/2-200));
        //imgView.setImageResource(itm.getImage().get(0));

        Picasso.with(context).load(itm.getImgs().get(0)).into(imgView);


        ImageView online = (ImageView)container.findViewById(R.id.online_indicator);
        TextView txtView2 = (TextView) container.findViewById(R.id.distanceTextViewHeader);

        if (itm.isOnline()){
            online.setVisibility(View.VISIBLE);
            txtView2.setVisibility(View.INVISIBLE);

        }
        else {
            online.setVisibility(View.INVISIBLE);
            txtView2.setVisibility(View.VISIBLE);
            txtView2.setText(itm.getDistance());
        }

        container.setOnClickListener(gridPress);

    }


    public void swapItems(CityObject item1, CityObject item2) {
        //Collections.swap(mItemList, mItemList.indexOf(item2), mItemList.indexOf(item1));
        notifyDataSetChanged();
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    /**
     * This method is used to add an item into the recyclerview list
     *
     * @param item
     */
    public void addItem(CityObject item) {
        mItemList.add(item);
        notifyDataSetChanged();
    }

    /**
     * This method is used to remove items from the list
     *
     * @param item {@link CityObject}
     */
    public void removeItem(CityObject item) {
        mItemList.remove(item);
        notifyDataSetChanged();
    }


}
package com.example.gustaf.touchpoint.Adapters;

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
 * This class is used to make the grid work as it does. If the user gets closer to another
        * cityobject, it will become a headerItem and therefore move to the top of the grid.
        */
public class GridListAdapter extends RecyclerView.Adapter<Holder> {

    private final int mDefaultSpanCount;
    private final List<CityObject>  mItemList;
    private final int      SCREEN_WIDTH;
    private final int     SCREEN_HEIGHT;
    private final Context             context;

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
        return position == 0;
    }

    /**
     * Inflates the header or the item depending on the position.
     */
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


    /**
     * Binds the header if the position is 0, otherwise binds the item.
     */
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
     */
    private void bindGridItem(Holder holder, final int position) {
        final View container = holder.itemView;

        ImageView imgView;
        imgView = (ImageView)container.findViewById(R.id.grid_image);


        final CityObject item = mItemList.get(position);
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


    /**
     * Sets the clickListener to the appropriate grid item. Defines the shared element transition
     * for the specified grid item.
     */
    private final View.OnClickListener gridPress = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView txtView = (TextView)view.findViewById(R.id.gridTextTitle);

                    int pos = -1;
                    String itemText = txtView.getText().toString();
                    for (int i = 0; i<mItemList.size(); i++){
                        if (mItemList.get(i).getName().equals(itemText)){
                            mItemList.get(i).getName();
                            pos = i;
                        }
                    }

                    CityObject item = mItemList.get(pos);

                    final BaseActivity activity = (BaseActivity) context;

                    ImageView sharedImage = pos == 0 ? (ImageView)view.findViewById(R.id.header_image) :
                            (ImageView)view.findViewById(R.id.grid_image);
                    sharedImage.setTransitionName("myImage");
                    Intent i = new Intent(context, DetailsActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, sharedImage, "myImage");

                    i.putExtra("cityobject", item);
                    context.startActivity(i, options.toBundle());


                }
    };

    /**
     * This method is used to bind the header with the corresponding item position information
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
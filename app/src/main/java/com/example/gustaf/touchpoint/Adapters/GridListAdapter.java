package com.example.gustaf.touchpoint.Adapters;

/**
 * Created by Gustaf on 16-08-04.
 */

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustaf.touchpoint.BaseActivity;
import com.example.gustaf.touchpoint.Fragment.InfoFragment;
import com.example.gustaf.touchpoint.HelpClasses.Holder;
import com.example.gustaf.touchpoint.HelpClasses.TouchPoint;
import com.example.gustaf.touchpoint.R;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class GridListAdapter extends RecyclerView.Adapter<Holder> {

    private final int mDefaultSpanCount;
    private List<TouchPoint> mItemList;
    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;
    Context context;

    public GridListAdapter(List<TouchPoint> itemList, GridLayoutManager gridLayoutManager, int defaultSpanCount, int width, int height, Context context) {
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





        final TouchPoint item = (TouchPoint) mItemList.get(position);
        TextView txtView = (TextView)container.findViewById(R.id.gridTextTitle);
        txtView.setText(item.getName());

        TextView txtView2 = (TextView)container.findViewById(R.id.distanceTextView);
        txtView2.setText(item.getDistance());

        container.setLayoutParams(new FrameLayout.LayoutParams(SCREEN_WIDTH/2, SCREEN_WIDTH/2));
        imgView.setLayoutParams(new FrameLayout.LayoutParams(SCREEN_WIDTH/2, SCREEN_WIDTH/2));
        imgView.setImageResource(item.getImage());

        container.setOnClickListener(gridPress);
    }

    View.OnClickListener gridPress = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final BaseActivity activity = (BaseActivity) context;
                    InfoFragment infoFragment = new InfoFragment();
                    android.support.v4.app.FragmentManager fr = activity.getSupportFragmentManager();
                    fr.beginTransaction()
                            .add(android.R.id.content, infoFragment).commit();
                }
    };

    /**
     * This method is used to bind the header with the corresponding item position information
     *
     * @param holder
     * @param position
     */
    private void bindHeaderItem(Holder holder, int position) {
        final TouchPoint itm = mItemList.get(position);
        View container = holder.itemView;


        TextView title = (TextView) container.findViewById(R.id.gridTextTitle);
        title.setText(itm.getName());
        ImageView imgView = (ImageView)container.findViewById(R.id.header_image);
        imgView.setLayoutParams(new FrameLayout.LayoutParams(SCREEN_WIDTH, SCREEN_HEIGHT/2-200));
        holder.itemView.setLayoutParams(new FrameLayout.LayoutParams(SCREEN_WIDTH, SCREEN_HEIGHT/2-200));
        imgView.setImageResource(itm.getImage());

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


    public void swapItems(TouchPoint item1, TouchPoint item2) {
        Collections.swap(mItemList, mItemList.indexOf(item2), mItemList.indexOf(item1));
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
    public void addItem(TouchPoint item) {
        mItemList.add(item);
        notifyDataSetChanged();
    }

    /**
     * This method is used to remove items from the list
     *
     * @param item {@link TouchPoint}
     */
    public void removeItem(TouchPoint item) {
        mItemList.remove(item);
        notifyDataSetChanged();
    }


}
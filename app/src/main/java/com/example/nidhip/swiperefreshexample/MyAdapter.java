package com.example.nidhip.swiperefreshexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import static android.R.id.list;
import static com.example.nidhip.swiperefreshexample.R.id.iv_avatar;

/**
 * Created by Nidhip on 08-03-2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

private List<Contributor> contributorList;
private Context mContext;


public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView iv_avatar;
    TextView tv_name, tv_contributed_repos, tv_contributors;

    public MyViewHolder(View view) {
        super(view);

        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_contributed_repos = (TextView) view.findViewById(R.id.tv_contributed_repos);
        tv_contributors = (TextView) view.findViewById(R.id.tv_contributors);
    }
}


    public MyAdapter(List<Contributor> moviesList,Context context) {
        this.contributorList = moviesList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Contributor contributor = contributorList.get(position);

        Glide.with(mContext).load(contributor.getAvatar_url()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.iv_avatar) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.iv_avatar.setImageDrawable(circularBitmapDrawable);
            }
        });

        
        holder.tv_name.setText(contributor.getName());
        holder.tv_contributed_repos.setText(contributor.getRepos_url());

        holder.tv_contributed_repos.setClickable(true);
        holder.tv_contributed_repos.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='" + contributor.getRepos_url() + "'>Contributed Repos</a>";
        holder.tv_contributed_repos.setText(Html.fromHtml(text));

        holder.tv_contributors.setText(mContext.getString(R.string.contributors) + contributor.getContributions());
    }

    @Override
    public int getItemCount() {
        if (contributorList != null){
            return contributorList.size();
        }else {
            return 0;
        }
    }



}
package name.caiyao.microreader.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.weixin.WeixinNews;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.WeixinNewsActivity;
import name.caiyao.microreader.utils.DBUtils;
import name.caiyao.microreader.utils.ScreenUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;

/**
 * Created by 蔡小木 on 2016/4/29 0029.
 */
public class WeixinAdapter extends RecyclerView.Adapter<WeixinAdapter.WeixinViewHolder> {

    public ArrayList<WeixinNews> weixinNewses;
    private Context mContext;

    public WeixinAdapter(Context context,ArrayList<WeixinNews> weixinNewses) {
        this.weixinNewses = weixinNewses;
        this.mContext = context;
    }

    @Override
    public WeixinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeixinViewHolder(LayoutInflater.from(mContext).inflate(R.layout.weixin_item, parent, false));
    }


    @Override
    public void onBindViewHolder(final WeixinViewHolder holder, int position) {
        final WeixinNews weixinNews = weixinNewses.get(position);
        if (DBUtils.getDB(mContext).isRead(Config.WEIXIN, weixinNews.getUrl(), 1))
            holder.tvTitle.setTextColor(Color.GRAY);
        else
            holder.tvTitle.setTextColor(Color.BLACK);
        holder.tvDescription.setText(weixinNews.getDescription());
        holder.tvTitle.setText(weixinNews.getTitle());
        holder.tvTime.setText(weixinNews.getHottime());
        if (!TextUtils.isEmpty(weixinNews.getPicUrl())) {
            Glide.with(mContext).load(weixinNews.getPicUrl()).placeholder(R.drawable.bg).into(holder.ivWeixin);
        } else {
            holder.ivWeixin.setImageResource(R.drawable.bg);
        }
        holder.btnWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.btnWeixin);
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.getMenu().removeItem(R.id.pop_fav);
                final boolean isRead = DBUtils.getDB(mContext).isRead(Config.WEIXIN, weixinNews.getUrl(), 1);
                if (!isRead)
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_read);
                else
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_unread);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop_unread:
                                if (isRead) {
                                    DBUtils.getDB(mContext).insertHasRead(Config.WEIXIN, weixinNews.getUrl(), 0);
                                    holder.tvTitle.setTextColor(Color.BLACK);
                                } else {
                                    DBUtils.getDB(mContext).insertHasRead(Config.WEIXIN, weixinNews.getUrl(), 1);
                                    holder.tvTitle.setTextColor(Color.GRAY);
                                }
                                break;
                            case R.id.pop_share:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, weixinNews.getTitle() + " " + weixinNews.getUrl() + mContext.getString(R.string.share_tail));
                                shareIntent.setType("text/plain");
                                //设置分享列表的标题，并且每次都显示分享列表
                                mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.share)));
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        runEnterAnimation(holder.itemView, position);
        holder.cvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB(mContext).insertHasRead(Config.WEIXIN, weixinNews.getUrl(), 1);
                holder.tvTitle.setTextColor(Color.GRAY);
                if (SharePreferenceUtil.isUseLocalBrowser(mContext)) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(weixinNews.getUrl())));
                } else {
                    Intent intent = new Intent(mContext, WeixinNewsActivity.class);
                    intent.putExtra("url", weixinNews.getUrl());
                    intent.putExtra("title", weixinNews.getTitle());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    private void runEnterAnimation(View view, int position) {
        view.setTranslationY(ScreenUtil.getScreenHight(mContext));
        view.animate()
                .translationY(0)
                .setStartDelay(100 * (position % 5))
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    @Override
    public int getItemCount() {
        return weixinNewses.size();
    }

    public class WeixinViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_weixin)
        ImageView ivWeixin;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.cv_main)
        CardView cvMain;
        @BindView(R.id.btn_weixin)
        Button btnWeixin;

        public WeixinViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

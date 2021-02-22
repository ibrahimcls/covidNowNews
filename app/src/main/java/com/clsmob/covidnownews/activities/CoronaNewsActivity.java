package com.clsmob.covidnownews.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clsmob.covidnownews.R;
import com.clsmob.covidnownews.models.CoronaNews;
import com.clsmob.covidnownews.models.ResponseModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoronaNewsActivity extends BaseActivity {
    Realm realm;
    String now;
    RecyclerView recyclerview_coronaNews;
    ProgressBar progressBar_coronaNews;
    CoronaNewsListAdapter coronaNewsListAdapter = new CoronaNewsListAdapter();
    ArrayList<CoronaNews> coronaNewss = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corona_news);
        realm = Realm.getDefaultInstance();

        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        now= DateFor.format(date);

        recyclerview_coronaNews = findViewById(R.id.recyclerview_coronaNews);
        recyclerview_coronaNews.setLayoutManager(new LinearLayoutManager(_activity));
        recyclerview_coronaNews.setAdapter(coronaNewsListAdapter);
        progressBar_coronaNews = findViewById(R.id.progressBar_coronaNews);
        if(!now.equals(sharedPreferences.getString("dateCoronaNews","")))
            getNews();
        else
            getNewsOffline();
    }

    private void getNewsOffline() {
        RealmResults<CoronaNews> results  = realm.where(CoronaNews.class).findAll();
        coronaNewss.clear();
        coronaNewss.addAll(results);
    }

    public void startLoad() {
        recyclerview_coronaNews.setEnabled(false);
        progressBar_coronaNews.setVisibility(View.VISIBLE);
    }

    public void endLoad() {
        recyclerview_coronaNews.setEnabled(true);
        progressBar_coronaNews.setVisibility(View.INVISIBLE);
    }

    public void getNews() {
        startLoad();
        apiService.getCoronaNews().enqueue(new Callback<ResponseModel<ArrayList<CoronaNews>>>() {
            @Override
            public void onResponse(Call<ResponseModel<ArrayList<CoronaNews>>> call, Response<ResponseModel<ArrayList<CoronaNews>>> response) {
                if (response.body().success && response.body().result != null) {
                    coronaNewss.addAll(response.body().result);
                    endLoad();
                    coronaNewsListAdapter.notifyDataSetChanged();

                    sharedPreferences.edit().putString("dateCoronaNews",now).apply();

                    realm.beginTransaction();
                    realm.copyToRealm(coronaNewss);
                    realm.commitTransaction();
                } else {
                    Toast.makeText(_activity, "Veri alınamadı", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<ArrayList<CoronaNews>>> call, Throwable t) {
                Toast.makeText(_activity, "Veri alınamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class CoronaNewsListAdapter extends RecyclerView.Adapter<CoronaNewsListAdapter.CoronaNewsListViewHolder> {

        public void animateTextViewMaxLinesChange(
                final TextView textView, final int maxLines, final int duration) {
            final int startHeight = textView.getMeasuredHeight();
            textView.setMaxLines(maxLines);
            textView.measure(View.MeasureSpec.makeMeasureSpec(
                    textView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED));
            final int endHeight = textView.getMeasuredHeight();
            ObjectAnimator animation = ObjectAnimator.ofInt(textView,
                    "maxHeight", startHeight, endHeight);
            animation.addListener(new AnimatorListenerAdapter() {
                                      @Override
                                      public void onAnimationEnd(Animator animation) {
                                          if (textView.getMaxHeight() == endHeight) {
                                              textView.setMaxLines(maxLines);
                                          }
                                      }
                                  }

            );
            animation.setDuration(duration).start();
        }

        @NonNull
        @Override
        public CoronaNewsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_coronanews_item, parent, false);
            return new CoronaNewsListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CoronaNewsListViewHolder holder, int position) {
            CoronaNews coronanews = coronaNewss.get(position);
            Glide.with(holder.newsImage.getContext())
                    .load(coronanews.getImage())
                    .placeholder(R.drawable.ic_search)
                    .error(R.drawable.ic_search)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.newsImage);
            holder.newsName_tv.setText(coronanews.getName());
            holder.newsDescription_tv.setText(coronanews.getDescription());
            holder.newsSource_tv.setText(coronanews.getSource());
            String date = coronanews.getDate().replace("T","  ").substring(0,19);
            holder.newsDate_tv.setText(date);
            holder.readMore_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CoronaNews realmCoronaNews = realm.where(CoronaNews.class).equalTo("name",coronanews.getName()).findFirst();
                    if (!realmCoronaNews.getToggle()) {
                        holder.readMore_tv.setText(R.string.shrink);
                        animateTextViewMaxLinesChange(holder.newsDescription_tv,10,500);

                    } else {
                        holder.readMore_tv.setText(getString(R.string.read_more));
                        animateTextViewMaxLinesChange(holder.newsDescription_tv,3,250);
                    }
                    realm.beginTransaction();
                    realmCoronaNews.setToggle(!coronanews.getToggle());
                    realm.commitTransaction();
                }
            });
            holder.news_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(_activity, BrowserActivity.class);
                    intent.putExtra("url", coronanews.getUrl());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return coronaNewss.size();
        }

        public class CoronaNewsListViewHolder extends RecyclerView.ViewHolder {
            ImageView newsImage;
            TextView newsName_tv, newsDescription_tv, newsSource_tv, newsDate_tv, readMore_tv;
            LinearLayout news_linear;


            public CoronaNewsListViewHolder(@NonNull View itemView) {
                super(itemView);
                news_linear = itemView.findViewById(R.id.news_linear);
                readMore_tv = itemView.findViewById(R.id.readMore_tv);
                newsName_tv = itemView.findViewById(R.id.newsName_tv);
                newsImage = itemView.findViewById(R.id.newsImage);
                newsDescription_tv = itemView.findViewById(R.id.newsDescription_tv);
                newsSource_tv = itemView.findViewById(R.id.newsSource_tv);
                newsDate_tv = itemView.findViewById(R.id.newsDate_tv);
            }
        }
    }
}
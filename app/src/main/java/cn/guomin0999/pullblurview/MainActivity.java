package cn.guomin0999.pullblurview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.guomin.adapter.RecyclerAdapter;
import com.guomin.adapter.ViewHolder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import cn.guomin0999.library.FastBlurView;
import cn.guomin0999.library.ZoomImageRecyclerView;

public class MainActivity extends AppCompatActivity {

    class ItemBean {
        int type = 0;
        String name;
        String iconUrl;

        public ItemBean(String name) {
            this(0, name, null);
        }

        public ItemBean(int type, String name, String iconUrl) {
            this.type = type;
            this.name = name;
            this.iconUrl = iconUrl;
        }
    }

    ZoomImageRecyclerView recyclerView;

    public static void initImageLoader(Context context) {

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(3)
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    RecyclerAdapter<ItemBean> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageLoader(getApplicationContext());
        recyclerView = (ZoomImageRecyclerView) findViewById(R.id.recycler);
        adapter = new RecyclerAdapter<ItemBean>(this, android.R.layout.simple_list_item_1, R.layout.head) {

            public int getItemViewType(int position) {
                return list.get(position).type;
            }

            public void onBindViewHolder(ViewHolder holder, int i, final ItemBean bean) {
                switch (bean.type) {
                    case 0:
                        holder.text(android.R.id.text1, bean.name);
                        break;
                    case 1:
                        recyclerView.setZoomView(holder.itemView());
                        final FastBlurView blurView = holder.get(R.id.blurView);
                        if (blurView.getTag() == null || !blurView.getTag().equals(bean.iconUrl)) {
                            ImageLoader.getInstance().loadImage(bean.iconUrl, new SimpleImageLoadingListener() {
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    blurView.setTag(bean.iconUrl);
                                    blurView.setBitmap(loadedImage);
                                    recyclerView.setOnPullinglistener(new ZoomImageRecyclerView.OnPullinglistener() {
                                        public void onPulling(float value) {
                                            blurView.setBlur(value);
                                        }
                                    });
                                }
                            });
                        }
                        holder.text(R.id.text, bean.name);
                        break;
                }
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    protected void onResume() {
        super.onResume();
        adapter.list.add(new ItemBean(1, "Head", "https://img3.doubanio.com/view/photo/photo/public/p2323065951.jpg"));
        for (int i = 0; i < 990; i++) {
            adapter.list.add(new ItemBean("Item" + i));
        }
        adapter.notifyDataSetChanged();
    }
}

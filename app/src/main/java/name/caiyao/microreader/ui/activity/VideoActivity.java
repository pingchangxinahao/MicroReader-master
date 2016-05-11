package name.caiyao.microreader.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;

public class VideoActivity extends AppCompatActivity {

    @BindView(R.id.vv_gank)
    VideoView vvGank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        final String url = getIntent().getStringExtra("url");
        final String shareUrl = getIntent().getStringExtra("shareUrl");
        final String title = getIntent().getStringExtra("title");
        vvGank.setVideoPath(url);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.common_loading));
        progressDialog.show();
        vvGank.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
            }
        });
        vvGank.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                progressDialog.dismiss();
                Toast.makeText(VideoActivity.this,"视频不存在或已被删除！",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        CustomMediaController customMediaController = new CustomMediaController(this);
        customMediaController.setListener(new OnMediaControllerInteractionListener() {
            @Override
            public void onShareClickListener() {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + shareUrl + getString(R.string.share_tail));
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            }
        });
        vvGank.setMediaController(customMediaController);
        vvGank.start();
    }

    public  interface OnMediaControllerInteractionListener {
        void onShareClickListener();
    }

     class CustomMediaController extends MediaController {

        Context mContext;
        private OnMediaControllerInteractionListener mListener;

        public CustomMediaController(Context context) {
            super(context);
            mContext = context;
        }

        public void setListener(OnMediaControllerInteractionListener listener) {
            mListener = listener;
        }

        @Override
        public void setAnchorView(View view) {
            super.setAnchorView(view);
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            frameParams.setMargins(0,50,500,0);
            frameParams.gravity = Gravity.RIGHT|Gravity.TOP;

            ImageButton fullscreenButton = (ImageButton) LayoutInflater.from(mContext)
                    .inflate(R.layout.share_buttion, null,false);

            fullscreenButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.onShareClickListener();
                    }
                }
            });

            addView(fullscreenButton, frameParams);
        }

        @Override
        public void show(int timeout) {
            super.show(timeout);
            // fix pre Android 4.3 strange positioning when used in Fragments
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                try {
                    Field field1 = MediaController.class.getDeclaredField("mAnchor");
                    field1.setAccessible(true);
                    View mAnchor = (View)field1.get(this);

                    Field field2 = MediaController.class.getDeclaredField("mDecor");
                    field2.setAccessible(true);
                    View mDecor = (View)field2.get(this);

                    Field field3 = MediaController.class.getDeclaredField("mDecorLayoutParams");
                    field3.setAccessible(true);
                    WindowManager.LayoutParams mDecorLayoutParams = (WindowManager.LayoutParams)field3.get(this);

                    Field field4 = MediaController.class.getDeclaredField("mWindowManager");
                    field4.setAccessible(true);
                    WindowManager mWindowManager = (WindowManager)field4.get(this);

                    // NOTE: this appears in its own Window so co-ordinates are screen co-ordinates
                    int [] anchorPos = new int[2];
                    mAnchor.getLocationOnScreen(anchorPos);

                    // we need to know the size of the controller so we can properly position it
                    // within its space
                    mDecor.measure(MeasureSpec.makeMeasureSpec(mAnchor.getWidth(), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(mAnchor.getHeight(), MeasureSpec.AT_MOST));

                    mDecor.setPadding(0,0,0,0);

                    WindowManager.LayoutParams p = mDecorLayoutParams;
                    p.verticalMargin = 0;
                    p.horizontalMargin = 0;
                    p.width = mAnchor.getWidth();
                    p.gravity = Gravity.LEFT|Gravity.TOP;
                    p.x = anchorPos[0];// + (mAnchor.getWidth() - p.width) / 2;
                    p.y = anchorPos[1] + mAnchor.getHeight() - mDecor.getMeasuredHeight();
                    mWindowManager.updateViewLayout(mDecor, mDecorLayoutParams);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

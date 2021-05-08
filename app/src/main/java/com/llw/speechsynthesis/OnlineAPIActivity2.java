package com.llw.speechsynthesis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.llw.speechsynthesis.listener.DownloadListener;
import com.llw.speechsynthesis.model.GetTokenResponse;
import com.llw.speechsynthesis.network.ApiService;
import com.llw.speechsynthesis.network.NetCallBack;
import com.llw.speechsynthesis.network.ServiceGenerator;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 在线API合成
 * @author llw
 */
public class OnlineAPIActivity2 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OnlineAPIActivity";

    /**
     * 输入框
     */
    private EditText etText;
    /**
     * 页面按钮
     */
    private Button btnSynthApi, btnPlay;

    /**
     * Api服务
     */
    private ApiService service;

    /**
     * 鉴权Toeken
     */
    private String accessToken;

    /**
     * 权限请求框架
     */
    private RxPermissions rxPermissions;

    /**
     * 默认文本，当输入框未输入使用，
     */
    private String defaultText = "你好！百度。";

    /**
     * 文件路径
     */
    private String mPath;

    /**
     * 缓冲区大小
     */
    private static int sBufferSize = 8192;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_api);

        //初始化
        initView();

        //访问API获取接口
        requestApiGetToken();
    }

    /**
     * 初始化
     */
    private void initView() {
        etText = findViewById(R.id.et_text);
        btnSynthApi = findViewById(R.id.btn_synth_api);
        btnPlay = findViewById(R.id.btn_play);
        btnSynthApi.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        rxPermissions = new RxPermissions(this);
    }


    /**
     * 访问API获取接口
     */
    private void requestApiGetToken() {
        String grantType = "client_credentials";
        String apiKey = "sKWlGNoBrNyaKaAycoiKFzdT";
        String apiSecret = "OwEPWPiSnMNxCF5GFZlORKzP01KwgC1Z";
        service = ServiceGenerator.createService(ApiService.class, 0);
        service.getToken(grantType, apiKey, apiSecret)
                .enqueue(new NetCallBack<GetTokenResponse>() {
                    @Override
                    public void onSuccess(Call<GetTokenResponse> call, Response<GetTokenResponse> response) {
                        if (response.body() != null) {
                            //鉴权Token
                            accessToken = response.body().getAccess_token();
                            Log.d(TAG, accessToken);
                        }
                    }

                    @Override
                    public void onFailed(String errorStr) {
                        Log.e(TAG, "获取Token失败，失败原因：" + errorStr);
                        accessToken = null;
                    }
                });
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    @SuppressLint("CheckResult")
    private void initPermission() {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(grant -> {
                    if (grant) {
                        String text;
                        if (etText.getText().toString().trim().isEmpty()) {
                            text = defaultText;
                        } else {
                            text = etText.getText().toString().trim();
                        }
                        //发起合成请求
                        requestSynth(text);
                    } else {
                        Toast.makeText(OnlineAPIActivity2.this,"未获取到权限",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 合成请求
     * @param text 需要合成语音的文本
     */
    private void requestSynth(String text) {
        service = ServiceGenerator.createService(ApiService.class, 1);
        service.synthesis(accessToken, "1", String.valueOf(System.currentTimeMillis()), "zh", text)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG,"请求成功");
                            //写入磁盘
                            writeToDisk(response, listener);
                        } else {
                            Log.d(TAG, "下载失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "error");
                    }
                });
    }

    /**
     * 写入磁盘
     * @param response 响应体
     * @param downloadListener 下载监听
     */
    private void writeToDisk(Response<ResponseBody> response, DownloadListener downloadListener) {
        //开始下载
        downloadListener.onStart();
        //输入流  将输入流写入文件
        InputStream is = response.body().byteStream();
        //文件总长
        long totalLength = response.body().contentLength();
        //设置文件存放路径
        file = new File(getExternalCacheDir() + "/Speech/" + "test.mp3");
        //创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onFail("createNewFile IOException");
            }
        }
        //输出流
        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                downloadListener.onProgress((int) (100 * currentLength / totalLength));
            }
            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onFail("IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载文件监听
     */
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onStart() {
            Log.d(TAG, "开始");
        }

        @Override
        public void onProgress(int progress) {
            Log.d(TAG, "进度：" + progress);
        }

        @Override
        public void onFinish(String path) {
            Log.d(TAG, "完成：" + path);
            mPath = path;
            //显示播放控件
            btnPlay.setVisibility(View.VISIBLE);

        }

        @Override
        public void onFail(String errorInfo) {
            Log.d(TAG, "异常：" + errorInfo);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_synth_api://在线API合成
                //请求权限
                initPermission();
                break;
            case R.id.btn_play://播放音频
                if(mPath != null){
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(mPath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}

package com.example.rhc.volleydemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button volley_get,volley_post,volley_json,volley_image1,volley_image2,volley_network;
    RequestQueue requestQueue;
    private TextView volley_result;
    private ImageView volley_image;
    private SimpleDraweeView volley_imageNet;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fresco.initialize(this);

        volley_get=(Button)findViewById(R.id.button_get);
        volley_post=(Button)findViewById(R.id.button_post);
        volley_json=(Button)findViewById(R.id.button_json);
        volley_image1=(Button)findViewById(R.id.button4_image1);
        volley_image2=(Button)findViewById(R.id.button4_image2);
        volley_network=(Button)findViewById(R.id.button_network);
        volley_result=(TextView)findViewById(R.id.tx_result);
        volley_image=(ImageView)findViewById(R.id.volley_imageView);
        volley_imageNet=(SimpleDraweeView)findViewById(R.id.volley_imagenetview);

        volley_get.setOnClickListener(this);
        volley_post.setOnClickListener(this);
        volley_json.setOnClickListener(this);
        volley_image1.setOnClickListener(this);
        volley_image2.setOnClickListener(this);
        volley_network.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_get:
                get();
                break;
            case R.id.button_post:
                post();
                break;
            case R.id.button_json:
                json();
                break;
            case R.id.button4_image1:
                image2();
                break;
            case R.id.button4_image2:
                image1();
                break;
            case R.id.button_network:
                network();
                break;
            default:
                break;
        }
    }

    private void get(){
        //创建一个请求队列
        requestQueue = Volley.newRequestQueue(MainActivity.this);//源码已看
        //创建一个请求
        String url = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";

        StringRequest stringRequest =new StringRequest(url, new Response.Listener<String>() {
            //正确接收数据回调
            @Override
            public void onResponse(String s) {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    volley_result.setText(s);
                    Log.e(TAG,"s="+jsonObject.getJSONArray("trailers").get(0)+"\n");
                    // }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {//异常后的监听数据
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volley_result.setText("加载错误"+volleyError);
            }
        });
        //将get请求添加到队列中
        requestQueue.add(stringRequest);
    }

    private void post(){
        //创建一个请求队列
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        //创建一个请求
        String url = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                volley_result.setText(s);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volley_result.setText("加载错误"+volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new HashMap<>();
                // map.put("value1","param1");//传入参数

                return map;
            }
        };

        //将post请求添加到队列中
        requestQueue.add(stringRequest);

    }

    private void json(){//JsonObjectRequest拓展自JsonRequest
        //创建一个请求队列
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        //创建一个请求
        String url = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                //TestData data = new Gson().fromJson(String.valueOf(jsonObject),TestData.class);

                volley_result.setText(jsonObject.toString());


                Log.e(TAG,"data="+jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volley_result.setText("加载错误"+volleyError);

            }
        });

        //将创建的请求添加到队列中
        requestQueue.add(jsonObjectRequest);

    }

    private void image1(){//ImageRequest
        //创建一个请求队列
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        //创建一个请求
        String url = "http://img5.mtime.cn/mg/2016/12/26/164311.99230575.jpg";
        //第二个参数,第三个：宽高，第四个：图片质量
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                //正确接收图片
                volley_image.setImageBitmap(bitmap);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volley_image.setImageResource(R.mipmap.ic_launcher);
            }
        });

        //将创建的请求添加到队列中
        requestQueue.add(imageRequest);
    }

    private void image2(){//ImageLoader，其实也是基于ImageRequest，但是前者可以自定义ImageCache，可以避免同一请求重复发送
        //创建一个请求队列
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        //创建一个请求
        ImageLoader imageLoader = new ImageLoader(requestQueue,new BitmapCache());//带缓存

        //加载图片
        String url = "http://img5.mtime.cn/mg/2016/12/26/164311.99230575.jpg";
        //加载不到，加载失败
        ImageLoader.ImageListener imageLister = imageLoader.getImageListener(volley_imageNet,R.mipmap.ic_launcher,R.mipmap.ic_launcher);
        imageLoader.get(url,imageLister);
    }

    private void network(){//虚拟机下无作用？
        //创建一个请求队列
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        //创建一个imageLoader
        ImageLoader imageLoader = new ImageLoader(requestQueue,new BitmapCache());

        //默认图片设置
        volley_imageNet.setImageResource(R.mipmap.ic_launcher);

        //加载图片
        String url = "http://img5.mtime.cn/mg/2016/12/26/164311.99230575.jpg";
        volley_imageNet.setImageURI(url,imageLoader);
    }


    @Override
    protected void onStop() {//少了删除请求队列的部分app启动会崩溃
        super.onStop();
        //取消队列里所有的请求
        requestQueue.cancelAll(this);

    }


}

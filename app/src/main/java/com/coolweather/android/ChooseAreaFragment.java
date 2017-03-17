package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 显示省市县的碎片
 * Created by Administrator on 2017/3/16.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;  // 省份所在的级别

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;  // 进度条

    private TextView titleText;  // 标题

    private Button backButton;  // 返回按钮

    private ListView listView;

    private ArrayAdapter<String> adapter;  // 适配器数组

    private List<String> dataList = new ArrayList<String>();  // 省市县数据

    private List<Province> provinceList;  // 所有省份集合

    private List<City> cityList;

    private List<County> countyList;

    private Province selectedProvince;  // 被选中的省份

    private City selectedCity;

    private int currentLevel;  // 当前选中的级别


    /**
     * 创建碎片
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return 返回view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                dataList);
        listView.setAdapter(adapter);
        return view;
    }

    /**
     * 创建活动
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * 按钮点击事件
             *
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    // 如果当前是省份级别
                    // 得到选中的省份的对象
                    selectedProvince = provinceList.get(position);
                    queryCities();  // 查询所有城市
                } else if (currentLevel == LEVEL_CITY) {
                    // 如果当前是城市级别
                    // 得到选中的城市的对象
                    selectedCity = cityList.get(position);
                    queryCounties();  // 查询所有区县
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();  // 销毁当前活动
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 返回事件
             * @param v
             */
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    // 如果当前是区县级别，返回后读取城市数据
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    // 城市级别，返回后读取省份数据
                    queryProvinces();
                }
            }
        });
        queryProvinces();  // 显示省份数据
    }

    /**
     * 查询所有省份，优先从数据库查询，没有则去服务器查询
     */
    private void queryProvinces() {
        titleText.setText("中国"); // 设置标题
        backButton.setVisibility(View.GONE);  // 按钮不可见
        provinceList = DataSupport.findAll(Province.class);  // 查询所有省份数据
        if (provinceList.size() > 0) {
            dataList.clear();  // 清空原先的数据
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();  // 刷新
            listView.setSelection(0);  // 返回顶部
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询所在省份的城市，优先从数据库查询，没有则去服务器查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());  // 设置标题为选中省份
        backButton.setVisibility(View.VISIBLE);  // 返回按钮可视化
        // 从数据库查询所有城市
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.
            getId())).find(City.class);
        if (cityList.size() > 0) {
            // 如果存在城市
            dataList.clear();
            // 获得所有城市并放在城市数组中
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();  // 刷新？
            listView.setSelection(0);  // 回到顶部
            currentLevel = LEVEL_CITY;  //城市等级
        } else {
            // 如果不存在城市，去服务器获取
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询所在城市的区县
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();;
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();

            Log.d("Code", String.valueOf(provinceCode));
            Log.d("Code", String.valueOf(cityCode));
            String address = "http://guolin.tech/api/china/" + provinceCode +
                    "/" + cityCode;
            Log.d("Code", address);
            queryFromServer(address, "county");
        }
    }


    /**
     * 如果数据库查询不到省市县数据，从服务器获取
     * @param address 访问地址
     * @param type 访问类型："province"/"city"/"county"
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();  // 显示查询进度条

        HttpUtil.sendOkHttpRequest(address, new Callback() {

            /**
             * 解析服务器传送过来的JSON数据，并保存到数据库中
             * @param call
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String repsonseText = response.body().string();  // 服务器返回数据
                Log.d("Code", repsonseText);
                boolean result = false;
                if ("province".equals(type)) {
                    // 如果类型是省份，则需要对省份数据（json）进行处理
                    // 注意同时会保存到数据库中
                    result = Utility.handleProvinceResponse(repsonseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(repsonseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(repsonseText, selectedCity.getId());
                    Log.d("Code", String.valueOf(result));
                }

                if (result) {
                    // 保存成功
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();  //关闭进度条
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();  // 加载失败，关闭进度条
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度条
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            // 如果进度条对象为空
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);  // 点击不消失，但返回消失
        }
        progressDialog.show();  // 显示进度条
    }

    /**
     * 关闭进度条
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}


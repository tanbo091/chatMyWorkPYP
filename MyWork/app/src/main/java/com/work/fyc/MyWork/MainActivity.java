package com.work.fyc.MyWork;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.work.fyc.MyWork.adapter.MainPostsAdapter;
import com.work.fyc.MyWork.entity.CardEntity;
import com.work.fyc.MyWork.entity.SectionTabEntity;
import com.work.fyc.MyWork.mvp.IMainView;
import com.work.fyc.MyWork.mvp.MainPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements IMainView {

    //final 和 static 参数尽量放在开头
    private final int CODE_LOOK = 1;
    private final int CODE_SEND = 2;
    private final int CODE_PERSON = 3;

    //Widget 控件 与 全局变量要换行分开.
    private Toolbar tool;
    private DrawerLayout drawerLayout;
    private LinearLayout llProgress;
    private TextView name;
    private TextView tvEmptyMsgShow;
    private ListView slip_list;
    //    private ListView main_list;//用 rvMainList 替换。这个RecyclerView就是ListView的修正版
    private TabLayout tabLayout;//这个就是你想要的那个标签控件，以后有经验就知道了
    private RecyclerView rvMainList;//这个是listview的新版，能解决一些listview的bug

    //新建一个类单独处理所有网络请求，网络数据会通过 IMainView 返回。也即MVP架构.具体请看里面的介绍
    private MainPresenter mMainPresenter;

    //上面的横向标签页
    private ArrayList<SectionTabEntity> mTabList = new ArrayList<>();

    private MainPostsAdapter mMainPostsAdapter;
    private ArrayList<CardEntity> mCardList = new ArrayList<>();

    //用户id，可以作为全局变量保存
    private String nameId = "";

    private ActionBarDrawerToggle toggle;

    //侧滑菜单
    private SimpleAdapter simpleAdapter;
    private String[] mMenuSettings = new String[]{"我的帖子", "我要发帖", "个人中心", "退出登陆"};
    private ArrayList<Map<String, Object>> arr;
    //    private ArrayList arrayList;
//    private ArrayList arrayList_sec, arrayList_t;


    //    private MainAdapter mainAdapter;
    private Menu menu;
    //    private MyHandler handler;
    public static int FLAG = 3;//标记我的帖子页面还是动态页面
    public static String mTabClicked = "全部";//记录点击的版块,原来是F，尽量让人能看懂这个参数
    private String sID;//记录板块版主id
    public static String[] spinner_arr;

    //全局变量命名尽量用驼峰命名法 ，例如  isGuest .
    private Boolean isGuest;//标记是否游客

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        isGuest = intent.getBooleanExtra("isVisit", false);

        init();//初始化对象
        mMainPresenter = new MainPresenter(this);

        if (isGuest) {
            mMenuSettings = new String[]{"我要登陆"};
        }

        tool.setTitle("");
        setSupportActionBar(tool);//设置toolbar
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //抽屉开关
        toggle = new ActionBarDrawerToggle(this, drawerLayout, tool, R.string.name, R.string.password) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        //设置侧滑菜单listview
        setListView();

        //获取帖子数据
//        getCard("http://2579f0157d.wicp.vip:80/MyForum1/main", "全部");
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("USER", MODE_PRIVATE);
        nameId = sharedPreferences.getString("id", "");

        showProgress(true);
        mMainPresenter.getMainCard(nameId, "全部");

        slip_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (mMenuSettings[position]) {
                    case "我的帖子":
                        FLAG = 2;
                        showProgress(true);
                        mMainPresenter.getSelfCard(nameId, "全部");
//                    getCard("http://2579f0157d.wicp.vip:80/MyForum1/selfCard", "全部");
                        break;
                    case "回到动态":
                        FLAG = 3;
                        showProgress(true);
                        mMainPresenter.getMainCard(nameId, "全部");
//                    getCard("http://2579f0157d.wicp.vip:80/MyForum1/main", "全部");
                        break;
                    case "我要发帖": {
                        Intent toSend = new Intent(MainActivity.this, SendActivity.class);
                        startActivityForResult(toSend, CODE_SEND);
                        break;
                    }
                    case "个人中心": {
                        Intent toSend = new Intent(MainActivity.this, PersonActivity.class);
                        startActivityForResult(toSend, CODE_PERSON);
                        break;
                    }
                    case "退出登陆":
                    case "我要登陆":
                        //修改登录信息
                        SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();

                        FLAG = 3;

                        Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                        break;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.onDestroy();
        mMainPresenter = null;
    }

    private void init() {
        tool = findViewById(R.id.tool);
        drawerLayout = findViewById(R.id.drawerLayout);
        name = findViewById(R.id.name);
        tabLayout = findViewById(R.id.tabLayout);
        rvMainList = findViewById(R.id.rvMainList);
        llProgress = findViewById(R.id.llProgress);
        tvEmptyMsgShow = findViewById(R.id.tvEmptyMsgShow);

//        handler = new MyHandler();

        slip_list = findViewById(R.id.slip_list);
        arr = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(this, arr, R.layout.main_listview, new String[]{"item"}, new int[]{R.id.list_tv});

//        main_list = findViewById(R.id.main_list);
//        arrayList = new ArrayList();
//        mainAdapter = new MainAdapter(this, arrayList, R.layout.main_listview_m, new int[]{R.id.person, R.id.name, R.id.pic, R.id.title_c, R.id.content}, handler);

//        arrayList_sec = new ArrayList();
//        arrayList_t = new ArrayList();

        //main post
        mMainPostsAdapter = new MainPostsAdapter(this, mCardList, onItemClick);
        rvMainList.setLayoutManager(new LinearLayoutManager(this));
        rvMainList.setAdapter(mMainPostsAdapter);
    }

    private MainPostsAdapter.OnItemClick onItemClick = new MainPostsAdapter.OnItemClick() {
        @Override
        public void onLook(int position) {
            CardEntity item = mCardList.get(position);
            Intent toLook = new Intent(MainActivity.this, LookActivity.class);
            toLook.putExtra("replyId", item.id);
            toLook.putExtra("sectionId", item.sectionId);
            startActivityForResult(toLook, CODE_LOOK);
        }

        @Override
        public void onDelete(int position) {
            CardEntity item = mCardList.get(position);
            showProgress(true);
            mMainPresenter.deletePost(item.id, position);
        }

        @Override
        public void onSetTop(int position) {
            CardEntity item = mCardList.get(position);
            showProgress(true);
            mMainPresenter.keepTop(item.id, position);
        }
    };

    /*这个已经被 MainPresenter 替代。网络请求都放在了 MainPresenter 里面。*/
//    private void getCard(final String path, final String s) {
//        final Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    //获取登录用户
//                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("USER", MODE_PRIVATE);
//                    String nameId = sharedPreferences.getString("id", "");
//                    jsonObject.put("nameId", nameId);
//                    jsonObject.put("section", s);
//
//                    NetWord netWord = new NetWord(path, jsonObject, handler);
//                    jsonObject = netWord.doPost();
//
//                    if (jsonObject == null) {
//                    } else {
//                        if (s.equals("全部")) {
//                            arrayList_sec.clear();
//                            JSONArray ja = ((JSONArray) jsonObject.get("section"));
//                            for (int i = 0; i < ja.length(); i++) {
//                                arrayList_sec.add(ja.get(i));
//                            }
//                        }
//                        JSONArray ja = ((JSONArray) jsonObject.get("card"));
//                        for (int i = 0; i < ja.length(); i++) {
//                            arrayList_t.add(ja.get(i));
//                        }
//                        mTabClicked = s;
//                        for (int i = 0; i < arrayList_sec.size(); i++) {
//                            try {
//                                if (mTabClicked.equals(((JSONObject) arrayList_sec.get(i)).get("name").toString())) {
//                                    sID = ((JSONObject) arrayList_sec.get(i)).get("host").toString();
//                                    mainAdapter.setsID(sID);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        handler.sendEmptyMessage(1);
//                        handler.sendEmptyMessage(FLAG);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    FLAG = 3;
//                    if (s.equals("全部"))
//                        handler.sendEmptyMessage(6);
//                    else
//                        handler.sendEmptyMessage(5);
//                }
//            }
//        });
//        thread.start();
//    }

    private void setListView() {
        SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        String n = sharedPreferences.getString("username", "");
        name.setText(n);
        slip_list.setAdapter(simpleAdapter);
//        main_list.setAdapter(mainAdapter);
        for (int i = 0; i < mMenuSettings.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item", mMenuSettings[i]);
            arr.add(map);
        }
        simpleAdapter.notifyDataSetChanged();
    }


    private void showProgress(boolean show) {
        if (show)
            llProgress.setVisibility(View.VISIBLE);
        else
            llProgress.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                if (!isGuest) {
                    item.setIcon(R.drawable.add2);
                    Intent toSend = new Intent(MainActivity.this, SendActivity.class);
                    startActivityForResult(toSend, CODE_SEND);
                } else {
                    Toast.makeText(getApplication(), "请登录", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode 即 startActivityForResult(Class，code)中的code
        menu.getItem(0).setIcon(R.drawable.add);
        FLAG = 3;
//        getCard("http://2579f0157d.wicp.vip:80/MyForum1/main", "全部");
        showProgress(true);
        mMainPresenter.getMainCard(nameId, "全部");
    }

    private void setTabLayoutItems(List<SectionTabEntity> items) {
        //这里尽量只走一遍。
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TabLayout.Tab tabAll = new TabLayout.Tab().setText("全部");
        tabLayout.addTab(tabAll);
        for (SectionTabEntity tabEntity : items) {
            //setText 会返回 Tab 回来。这样方便初始化 。
            //等效于
            // tab = new TabLayout.Tab() ;
            // tab.setText("asd")
            TabLayout.Tab tab = new TabLayout.Tab().setText(tabEntity.name);
            tabLayout.addTab(tab);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //例如点击Tab 2. 从 Tab 1 到 Tab 2. 会把 Tab 2 传过来
                if (tab.getText() != null) {
                    String title = tab.getText().toString();
                    if (FLAG == 2) {
//                        getCard("http://2579f0157d.wicp.vip:80/MyForum1/selfCard", title);
                        showProgress(true);
                        mMainPresenter.getSelfCard(nameId, title);
                    } else if (FLAG == 3) {
//                        getCard("http://2579f0157d.wicp.vip:80/MyForum1/main", title);
                        showProgress(true);
                        mMainPresenter.getMainCard(nameId, "全部");
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    private void setSectionView() {
//        hl.removeAllViews();
//        Button button0 = new Button(this);
//        button0.setText("全部");
//        button0.setBackgroundColor(Color.GRAY);
//        if (button0.getText().toString().equals(mTabClicked))
//            button0.setBackgroundColor(Color.BLACK);
//        hl.addView(button0);
//        button0.setOnClickListener(new Click());
//
//        spinner_arr = new String[arrayList_sec.size()];
//        for (int i = 0; i < arrayList_sec.size(); i++) {
//            Button button = new Button(this);
//            try {
//                button.setText(((JSONObject) arrayList_sec.get(i)).get("name").toString());
//                button.setBackgroundColor(Color.GRAY);
//                spinner_arr[i] = ((JSONObject) arrayList_sec.get(i)).get("name").toString();
//                if (button.getText().toString().equals(mTabClicked))
//                    button.setBackgroundColor(Color.BLACK);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            hl.addView(button);
//            button.setOnClickListener(new Click());
//        }
//    }

//    private class MyHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    arrayList.clear();
//                    for (int i = 0; i < arrayList_t.size(); i++) {
//                        arrayList.add(arrayList_t.get(i));
//                    }
//                    mainAdapter.notifyDataSetChanged();
//                    arrayList_t.clear();
//                    setSectionView();
//                    break;
//                case 2:
//                    mMenuSettings[0] = "回到动态";
//                    arr.clear();
//                    setListView();
//                    break;
//                case 3:
//                    if (!isGuest)
//                        mMenuSettings[0] = "我的帖子";
//                    arr.clear();
//                    setListView();
//                    break;
//                case 4:
//                    Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_SHORT).show();
//                    break;
//                case 5:
//                    Toast.makeText(getApplication(), "没有该板块的帖子哦", Toast.LENGTH_SHORT).show();
//                    break;
//                case 6:
//                    Toast.makeText(getApplication(), "您还没有发帖哦", Toast.LENGTH_SHORT).show();
//                    break;
//                case 7:
//                    Toast.makeText(getApplication(), "删除成功", Toast.LENGTH_SHORT).show();
//                    mainAdapter.notifyDataSetChanged();
//                    if (FLAG == 2) {
////                        getCard("http://192.168.123.1:8080/MyForum/selfCard", mTabClicked);
//                    } else if (FLAG == 3) {
////                        getCard("http://192.168.123.1:8080/MyForum/main", mTabClicked);
//                    }
//                    break;
//                case 8:
//                    Toast.makeText(getApplication(), "删除失败", Toast.LENGTH_SHORT).show();
//                    break;
//                case 9:
//                    Toast.makeText(getApplication(), "置顶成功", Toast.LENGTH_SHORT).show();
//                    if (FLAG == 2) {
//                        getCard("http://2579f0157d.wicp.vip:80/MyForum1/selfCard", mTabClicked);
//                    } else if (FLAG == 3) {
//                        getCard("http://2579f0157d.wicp.vip:80/MyForum1/main", mTabClicked);
//                    }
//                    break;
//                case 10:
//                    Toast.makeText(getApplication(), "置顶失败", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    }

    /**
     * 下面是继承自 interface IMainView 的接口
     * 相当于你的 MyHandler
     */
    @Override
    public void failToast(String message) {
        showProgress(false);
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnSectionCards(ArrayList<SectionTabEntity> sectionTabs, ArrayList<CardEntity> cards, String section) {
        //这里只刷新 帖子界面的list。 刷新全部，刷新自己的贴子，都走这里
        //侧滑菜单不在这里刷新。
        showProgress(false);
        if (sectionTabs != null && section.equals("全部")) {
            mTabList.clear();
            mTabList.addAll(sectionTabs);
            setTabLayoutItems(sectionTabs);
        }
        if (cards != null) {
            mTabClicked = section;
            mCardList.clear();
            mCardList.addAll(cards);

            for (SectionTabEntity tab : mTabList) {
                if (section.equals(tab.name)) {
                    sID = tab.host;
                    mMainPostsAdapter.setsID(sID);
                }
            }
            mMainPostsAdapter.notifyDataSetChanged();
        }
        if (mCardList.isEmpty()) {
            tvEmptyMsgShow.setText("没有该板块的帖子哦");
            tvEmptyMsgShow.setVisibility(View.VISIBLE);
        } else
            tvEmptyMsgShow.setVisibility(View.GONE);
    }

    @Override
    public void deleteSuccess(int position) {
        mCardList.remove(position);
        mMainPostsAdapter.notifyItemRemoved(position);
        Toast.makeText(getApplication(), "删除成功", Toast.LENGTH_SHORT).show();
        if (FLAG == 2) {
//            getCard("http://192.168.123.1:8080/MyForum/selfCard", mTabClicked);
            mMainPresenter.getSelfCard(nameId, mTabClicked);
        } else if (FLAG == 3) {
            //这里应该调用的就是 http://2579f0157d.wicp.vip:80/MyForum1/main ？
//        getCard("http://192.168.123.1:8080/MyForum/main", mTabClicked);
            mMainPresenter.getMainCard(nameId, "全部");
        }
    }

    @Override
    public void topSuccess(int position) {
        Toast.makeText(getApplication(), "置顶成功", Toast.LENGTH_SHORT).show();
        if (FLAG == 2) {
//            getCard("http://2579f0157d.wicp.vip:80/MyForum1/selfCard", mTabClicked);
            mMainPresenter.getSelfCard(nameId, mTabClicked);
        } else if (FLAG == 3) {
//                        getCard("http://2579f0157d.wicp.vip:80/MyForum1/main", mTabClicked);
            mMainPresenter.getMainCard(nameId, mTabClicked);
        }
    }
}

package vr.promulgator.com.vrmc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import vr.promulgator.com.vrmc.bean.IConstant;
import vr.promulgator.com.vrmc.presenter.LoginPresenter;
import vr.promulgator.com.vrmc.utils.ToastCommom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import vr.promulgator.com.vrmc.R;
import vr.promulgator.com.vrmc.adapter.MyAdapter;
import vr.promulgator.com.vrmc.bean.LocalInfo;
import vr.promulgator.com.vrmc.bean.LoginInfo;
import vr.promulgator.com.vrmc.bean.PatientInfo;
import vr.promulgator.com.vrmc.bean.Prescription;
import vr.promulgator.com.vrmc.bean.PrescriptionContent;
import vr.promulgator.com.vrmc.imp.LoginView;
import vr.promulgator.com.vrmc.imp.QueryPayient;
import vr.promulgator.com.vrmc.imp.QueryPrescription;
import vr.promulgator.com.vrmc.presenter.QueryPatientPresenter;
import vr.promulgator.com.vrmc.presenter.QueryPrescriptionPresenter;
import vr.promulgator.com.vrmc.utils.MyArrayAdapter;
import vr.promulgator.com.vrmc.utils.SpUtils;

/**
 * 点击输入框，跳转到患者页面
 * Created by raytine on 2017/4/25.
 */

public class PaintActivity extends BaseActivity implements View.OnClickListener, QueryPayient, QueryPrescription {
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    public static final String EXTRA_KEY_KEYWORD = "extra_key_keyword";
    public static final String KEY_SEARCH_HISTORY_KEYWORD = "key_search_history_keyword";
    LoginPresenter lp;
    @BindView(R.id.clear_history_btn)
    Button clearHistoryBtn;
    @BindView(R.id.tab_bar_keyword_et)
    EditText tabBarKeywordEt;
    @BindView(R.id.clear_keyword_iv)
    ImageView clearKeywordIv;
    @BindView(R.id.tab_bar_cancel_tv)
    TextView tabBarCancelTv;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.xinjing_name)
    TextView xinjingName;
    @BindView(R.id.xinjing_phone)
    TextView xinjingPhone;
    @BindView(R.id.search_history_ll)
    FrameLayout searchHistoryLl; // 1
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.action_three)
    LinearLayout actionThree;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private MyArrayAdapter mArrAdapter;
    private LocalInfo users;
    private QueryPatientPresenter queryPatientPresenter;
    private QueryPrescriptionPresenter queryPrescriptionPresenter;
    private ProgressDialog dialog;
    private List patient = new ArrayList();
    private MyAdapter myAdapter;
    private LoginInfo loginInfo;
    private String token;
    private List<String> mHistoryKeywords = new ArrayList<>();
    private List<String> mHistoryKeywordss = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint_activity);
        ButterKnife.bind(this);
        getData();
        queryPrescriptionPresenter = new QueryPrescriptionPresenter(PaintActivity.this);
        initData();
        mPref = getSharedPreferences("test", Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        initSearchHistory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tab_bar_cancel_tv://查询患者
// else {
//                    finish();
//                }
                if (patient.size() != 0) {
                    patient.clear();
                    myAdapter.notifyDataSetChanged();
                    xinjingName.setText("xxx");
                    xinjingPhone.setText("xxxxxxxxxxx");
                }
                String phone = tabBarKeywordEt.getText().toString().trim();

                if (phone.equals("")) {
                    ToastCommom.createInstance().ToastShow(PaintActivity.this, "手机号不能为空");
                    break;
                }
                Map<String, String> priArgs = new HashMap<>();
                priArgs.put("keyword", phone);
                priArgs.put("token", token);
                int i = checkNetWork();
                if (i == 0) {
                    ToastCommom.createInstance().ToastShow(PaintActivity.this, "请设置网络环境");

                } else {
                    Log.e("--------查询患者", phone);
                    if (isMobile(phone)) {
                        queryPatientPresenter = new QueryPatientPresenter(PaintActivity.this, priArgs);
                        queryPatientPresenter.getPatient();
                    } else {
                        ToastCommom.createInstance().ToastShow(PaintActivity.this, "请输入正确的手机号格式");
                    }
                }

                break;
            case R.id.clear_history_btn:
                cleanHistory();
                break;
        }
    }

    /**
     * 统一的mvp接口回调
     *
     * @param user
     */
    //查询患者
    @Override
    public void updateView(PatientInfo user) {
        searchHistoryLl.setVisibility(View.GONE);
        actionThree.setVisibility(View.VISIBLE);
        listview.setVisibility(View.VISIBLE);
        if (tabBarKeywordEt.getText().length() > 0) {
            save();
        }
        tabBarKeywordEt.setText("");
        xinjingName.setText(user.getData().getRealname());
        xinjingPhone.setText(user.getData().getMobile());
        getPrescription(user.getData().getUserId());
    }

    //查询患者处方列表
    @Override
    public void updateView(Prescription user) {
        if (user.getData() != null) {
            for (int i = 0; i < user.getData().size(); i++) {
                patient.add(user.getData().get(i));
            }
            myAdapter.notifyDataSetChanged();
        } else {
            ToastCommom.createInstance().ToastShow(PaintActivity.this, "没有数据");
        }

    }

    @Override
    public void update(PrescriptionContent p) {

    }

    @Override
    public void showProgressDialog() {
        dialog = ProgressDialog.show(PaintActivity.this, "查询患者中", "Please wait...", true, false);
    }

    @Override
    public void hideProgressDialog() {
        dialog.dismiss();
    }

    @Override
    public void showError(String msg) {
        ToastCommom.createInstance().ToastShow(PaintActivity.this, msg);
    }

    @Override
    public void change() {
        String st = xinjingName.getText().toString().trim();
        if (!st.equals("xxx")) {
            xinjingName.setText("xxx");
            xinjingPhone.setText("xxxxxxxxxxx");
        }
    }

    //token 发生变化时，重新登录获取token
    @Override
    public void tokenchange() {
        ToastCommom.createInstance().ToastShow(PaintActivity.this, "登录异常,请重新点击查询");
        Map<String, String> priArgs = new HashMap<>();
        priArgs.put("appId", IConstant.devoceId(this));
        priArgs.put("appVersion", IConstant.getVersion(this));
        priArgs.put("channel", "null");
        priArgs.put("deviceModel", IConstant.getModel());
        priArgs.put("deviceSystem", "android");
        priArgs.put("deviceVersion", IConstant.getVersionRelease());
        priArgs.put("password", users.getPassword());
        priArgs.put("username", users.getUsername());
        lp = new LoginPresenter(new LoginView() {
            @Override
            public void updateView(LoginInfo user) {
                loginInfo = user;
                token = user.getData().getToken();
                Log.e("------------重新的token", token);
                users.setToken(token);
                saveData(users, user);
//                xinjingSeacher.performClick();
            }

            @Override
            public void showProgressDialog() {

            }

            @Override
            public void hideProgressDialog() {

            }

            @Override
            public void showError(String msg) {

            }
        }, priArgs);

        lp.Login();
    }
    public void getSp(){
        String history = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "");
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            mHistoryKeywords = list;
        }
    }
    public void initSearchHistory() {
        ListView listViewx = (ListView) findViewById(R.id.search_history_lv);
        findViewById(R.id.clear_history_btn).setOnClickListener(this);
        String history = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "");
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            mHistoryKeywords = list;
        }
        if (mHistoryKeywords.size() > 0) {
            searchHistoryLl.setVisibility(View.VISIBLE);
        } else {
            searchHistoryLl.setVisibility(View.GONE);
        }
        mArrAdapter = new MyArrayAdapter(this,mHistoryKeywords);
        listViewx.setAdapter(mArrAdapter);
        listViewx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView viewById = (TextView) view.findViewById(R.id.contentTextView);
                tabBarKeywordEt.setText(viewById.getText());
                searchHistoryLl.setVisibility(View.GONE);
            }
        });
        mArrAdapter.notifyDataSetChanged();
    }

    public void save() {
        String text = tabBarKeywordEt.getText().toString();
        String oldText = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "");
        System.out.println("zlw=======" + oldText);
        if (!TextUtils.isEmpty(text) && !oldText.contains(text)) {
//            if (mHistoryKeywords.size() > 4) {
//                Toast.makeText(this, "最多保存5条历史", Toast.LENGTH_SHORT).show();
//                return;
//            }
            mEditor.putString(KEY_SEARCH_HISTORY_KEYWORD, text + "," + oldText);
            mEditor.commit();
            mHistoryKeywords.add(text);
        }
        mHistoryKeywords.clear();
        getSp();
        Log.e("--------baocunhou",mHistoryKeywords.size()+"");
        mArrAdapter.notifyDataSetChanged();
    }

    public void cleanHistory() {
        mEditor.clear();
        mEditor.commit();
        mHistoryKeywords.clear();
        mArrAdapter.notifyDataSetChanged();
        searchHistoryLl.setVisibility(View.GONE);
        Toast.makeText(this, "清除搜索历史记录成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String keyword = intent.getStringExtra(EXTRA_KEY_KEYWORD);
        if (!TextUtils.isEmpty(keyword)) {
            tabBarKeywordEt.setText(keyword);
        }
    }

    private void initData() {
        imgBack.setOnClickListener(this);
        clearKeywordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabBarKeywordEt.setText("");
                v.setVisibility(View.GONE);

            }
        });
        tabBarKeywordEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHistoryLl.setVisibility(View.VISIBLE);
                actionThree.setVisibility(View.GONE);
                listview.setVisibility(View.GONE);
                mArrAdapter.setList(mHistoryKeywords);
            }
        });
        tabBarKeywordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    return true;
                }
                return false;
            }
        });
        tabBarKeywordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 0) {
                    Log.e("-------s=0", "zengjia"+s);
                    tabBarCancelTv.setText("查询");
                    clearKeywordIv.setVisibility(View.GONE);
                    if (mHistoryKeywords.size() > 0) {
                        searchHistoryLl.setVisibility(View.GONE);
                    } else {
                        searchHistoryLl.setVisibility(View.GONE);
                        tabBarCancelTv.setText("查询");
                        clearKeywordIv.setVisibility(View.VISIBLE);
                    }
                    mArrAdapter.notifyDataSetChanged();
                } else {
                    searchHistoryLl.setVisibility(View.VISIBLE);
                    actionThree.setVisibility(View.GONE);
                    listview.setVisibility(View.GONE);
                    if (mHistoryKeywordss.size()!= 0){
                        mHistoryKeywordss.clear();
                    }
                    if (mHistoryKeywords.size()!= 0){
                        String st = tabBarKeywordEt.getText().toString();
                        for (int i = 0; i <mHistoryKeywords.size() ; i++) {
                            if (mHistoryKeywords.get(i).startsWith(st)){
                                Log.e("-------",mHistoryKeywords.get(i));
                                mHistoryKeywordss.add(mHistoryKeywords.get(i));
                            }
                        }
                        mHistoryKeywords.clear();
                    }
                        mHistoryKeywords.addAll(mHistoryKeywordss);
                    Log.e("-------+",mHistoryKeywords.size()+"");
                    mArrAdapter.setList(mHistoryKeywords);
                    getSp();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tabBarKeywordEt.requestFocus();
        tabBarCancelTv.setText("查询");
        tabBarCancelTv.setOnClickListener(this);

        myAdapter = new MyAdapter(PaintActivity.this, patient, R.layout.layout_chufang);
        listview.setAdapter(myAdapter);

        listview.setDividerHeight(25);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prescription.DataBean cf = (Prescription.DataBean) patient.get(position);
                int i = checkNetWork();
                if (i == 0) {
                    ToastCommom.createInstance().ToastShow(PaintActivity.this, "请设置网络环境");
                } else {
                    Intent ss = new Intent(PaintActivity.this, PrescriptionContentActivity.class);
                    ss.putExtra("id", cf.getId());
                    startActivity(ss);

                }
            }
        });

        clearHistoryBtn.setOnClickListener(this);
    }

    PrescriptionContent.DataBean datatimes;

    public void getPrescription(String patientId) {
        Map<String, String> priArgs = new HashMap<>();
        priArgs.put("patientId", patientId);
        priArgs.put("token", token);

        queryPrescriptionPresenter.setMap(priArgs);
        queryPrescriptionPresenter.getPrescription();
    }

    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    public void getData() {
        SpUtils instance = SpUtils.getInstance();
        instance.init(PaintActivity.this);
        users = instance.getUser();
        token = users.getToken();
        loginInfo = instance.getLogin();

    }

    public void saveData(LocalInfo info, LoginInfo infos) {
        SpUtils instance = SpUtils.getInstance();
        instance.init(PaintActivity.this);
        instance.saveUser(info);
        instance.saveLogin(infos);
    }

}

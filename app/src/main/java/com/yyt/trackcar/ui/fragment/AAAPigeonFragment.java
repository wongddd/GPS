package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.PigeonModel;
import com.yyt.trackcar.dbflow.PigeonModel_Table;
import com.yyt.trackcar.ui.adapter.PigeonsAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
@Page(name = "pigeonFragment", anim = CoreAnim.none)
public class AAAPigeonFragment extends BaseFragment {

    @BindView(R.id.et_search)
    EditText eTSearch;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private final List<PigeonModel> pigeons = new ArrayList<>();
    private PigeonsAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.aaa_fragment_pigeon;
    }

    @Override
    protected void initViews() {
        initDatas();
        eTSearch.setImeOptions(EditorInfo.IME_ACTION_GO);
        eTSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String str = eTSearch.getText().toString().trim();
                if (TextUtils.isEmpty(str))
                    showMessage(R.string.cannot_empty_prompt);
                else {
                    String message = null;
                    for (PigeonModel pigeon : pigeons
                    ) {
                        if (str.equals(pigeon.getNumber())) {
                            message = str;
                            break;
                        }
                    }
                    showMessage(message == null?getString(R.string.cannot_find_pigeon_by_number_prompt):message);
                }
                InputMethodManager imm =
                        (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(eTSearch.getWindowToken(), 0);
                return false;
            }
        });

        adapter = new PigeonsAdapter(pigeons);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                AlertDialog alertDialog = builder.create();
                builder.setTitle(R.string.prompt)
                        .setMessage(R.string.delete_pigeon_prompt)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLite.delete(PigeonModel.class).where(PigeonModel_Table.number.eq(pigeons.get(position).getNumber())).execute();
                                pigeons.remove(position);
                                adapter.notifyDataSetChanged();
                                alertDialog.cancel();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                }).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void initDatas() {
        if (pigeons.size() == 0)
        pigeons.addAll(SQLite.select().from(PigeonModel.class)
                .where(PigeonModel_Table.userId.eq(String.valueOf(getTrackUserModel().getUserId())))
                .queryList());
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.pigeon);
        TitleBar.Action action = new TitleBar.ImageAction(R.drawable.add) {
            @Override
            public void performAction(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                AlertDialog alertDialog = builder.create();
                final View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_pigeon, null);
                final EditText etNumberOfPigeon = v.findViewById(R.id.et_number_of_pigeon);
                final EditText etNicknameOfPigeon = v.findViewById(R.id.et_nickname_of_pigeon);
                final EditText etFeatherColorOfPigeon = v.findViewById(R.id.et_color_of_pigeon);
                builder.setTitle(R.string.add_pigeon)
                        .setView(v)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String number = etNumberOfPigeon.getText().toString().trim();
                                String nickname = etNicknameOfPigeon.getText().toString().trim();
                                String color = etFeatherColorOfPigeon.getText().toString().trim();
                                if (TextUtils.isEmpty(number)) {
                                    showMessage(String.format("%s%s", getString(R.string.number), getString(R.string.cannot_empty_prompt)));
                                } else if (TextUtils.isEmpty(nickname)) {
                                    showMessage(String.format("%s%s", getString(R.string.nickname), getString(R.string.cannot_empty_prompt)));
                                } else if (TextUtils.isEmpty(color)) {
                                    showMessage(String.format("%s%s", getString(R.string.plumage_color), getString(R.string.cannot_empty_prompt)));
                                } else {
                                    PigeonModel pigeon = new PigeonModel();
                                    pigeon.setNumber(number);
                                    pigeon.setNickname(nickname);
                                    pigeon.setColor(color);
                                    pigeon.setUserId(String.valueOf(getTrackUserModel().getUserId()));
                                    pigeon.save();
                                    pigeons.add(pigeon);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                }).show();
                Window window = alertDialog.getWindow();
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_border_default));
            }
        };
        titleBar.addAction(action);
        return titleBar;
    }
}

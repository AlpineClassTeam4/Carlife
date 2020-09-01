package com.alphine.team4.carlife.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.login.toolsBeans.DBHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG = "HomeDate";
    TextView textView;

    DBHelper dbHelper = new DBHelper(getActivity());

    String nickName;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((CollapsingToolbarLayout) requireActivity().findViewById(R.id.CollapsingToolbarLayout))
                .setTitle("User");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_date",MODE_PRIVATE);
        String input_email = sharedPreferences.getString("user_email",null);

        SQLiteDatabase db;

        textView = getActivity().findViewById(R.id.textView4);

        String path = getActivity().getFilesDir() + "/"+"user_info.db";
        db = SQLiteDatabase.openOrCreateDatabase(path,null);
        Cursor cursor = db.query("user_info",null,"user_email=?",
                new String[]{input_email},
                null,null,null,null);
        while (cursor.moveToNext()){
            nickName = cursor.getString(cursor.getColumnIndex("user_nickname"));
        }
        textView.setText(nickName+" 已登录");

        getActivity().findViewById(R.id.button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_date",MODE_PRIVATE);

                String logout_email;
                String logout_password;

                logout_email = sharedPreferences.getString("user_email",null);
                logout_password = sharedPreferences.getString("user_password",null);

                Intent intent = new Intent(getActivity(),com.alphine.team4.carlife.ui.login.LoginSystemActivity.class);
                intent.putExtra("logout_email",logout_email);
                intent.putExtra("logout_password",logout_password);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_email");
                editor.remove("user_password");
                editor.commit();

                getActivity().finish();

                startActivity(intent);
                Toast.makeText(getActivity(),nickName+" 已登出",Toast.LENGTH_LONG).show();
            }
        });
    }
}

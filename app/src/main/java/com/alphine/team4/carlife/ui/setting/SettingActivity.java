package com.alphine.team4.carlife.ui.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import com.alphine.team4.carlife.R;
import com.google.android.material.navigation.NavigationView;

public class SettingActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        navController = Navigation.findNavController(this, R.id.setting_fragment);
        DrawerLayout drawerLayout = findViewById(R.id.DrawerLayout);
        // appBarConfiguration 自动获取 toolbar 并将各个 Fragment 页面注册到其中
        appBarConfiguration = new AppBarConfiguration
//                .Builder(navController.getGraph())
                .Builder(R.id.wifiFragment, R.id.bluethoothFragment,R.id.userFragment)
                .setDrawerLayout(drawerLayout)
                .build();
        // 将 toolbar 和 navController 关联
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // 将 navigationView 和 navController 关联
        // navigationView 中的 menu 将会自动根据 navigation.xml 中的各 id 来绑定对应的 Fragment
        NavigationView navigationView = findViewById(R.id.NavigationView);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

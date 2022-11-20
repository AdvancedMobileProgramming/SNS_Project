package com.example.sns_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.sns_project.databinding.ActivityMainBinding
import androidx.navigation.ui.setupWithNavController


class MainActivity : AppCompatActivity() { //fragment 제어하는 메인
    private lateinit var appbarc: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nhf = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val topDest = setOf(R.id.homeFragment, R.id.myProfileFragment, R.id.profileFragment, R.id.friendsFragment, R.id.postingFragment, R.id.settingFragment)
        appbarc = AppBarConfiguration(topDest, binding.drawerLayout)
        setupActionBarWithNavController(nhf.navController, appbarc)
        binding.navigationView.setupWithNavController(nhf.navController)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragmentContainerView).navigateUp(appbarc) || super.onSupportNavigateUp()
    }


}
package com.example.sns_project

import android.content.Intent
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.sns_project.databinding.ActivityMainBinding
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var appbarc: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nhf = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val topDest = setOf(R.id.menu_item4, R.id.menu_item1, R.id.menu_item2, R.id.menu_item3)
        appbarc = AppBarConfiguration(topDest, binding.drawerLayout)
        setupActionBarWithNavController(nhf.navController, appbarc)
        binding.navigationView.setupWithNavController(nhf.navController)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragmentContainerView).navigateUp(appbarc) || super.onSupportNavigateUp()
    }
}
package com.example.passwordvault.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.passwordvault.R
import com.example.passwordvault.databinding.ActivityMainBinding
import com.example.passwordvault.service.CallReceiverService
import com.example.passwordvault.util.Permissions
import com.example.passwordvault.util.ServiceStarter
import com.example.passwordvault.util.WorkerProvider
import dagger.hilt.android.AndroidEntryPoint

private const val REQUEST_PERMISSIONS = 200

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var listener: NavController.OnDestinationChangedListener

    // Requesting permission to RECORD_AUDIO
    private var permissions: Array<String> =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navController = findNavController(R.id.fragment)
        NavigationUI.setupWithNavController(binding.navigationView, navController)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        Navigation.setViewNavController(binding.cardsDetails, navController)


        setUpOnClickListeners()
        checkPermissions()
        ServiceStarter.startCallReceiverService(applicationContext)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//            WorkerProvider.startUriChecker(applicationContext)
    }

    private fun checkPermissions() {

        if (Permissions.audioPermission(this) != PackageManager.PERMISSION_GRANTED)
            askPermission()

        if (Permissions.phoneStatePermission(this) != PackageManager.PERMISSION_GRANTED)
            askPermission()
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            permissions,
            REQUEST_PERMISSIONS
        )
    }

    private fun setUpOnClickListeners() {
        binding.cardsDetails.setOnClickListener {
            navController.navigate(R.id.action_global_addCardDetails)
            binding.floatingMenu.close(true)

        }

        binding.loginDetails.setOnClickListener {
            navController.navigate(R.id.action_global_addLoginDetails)
            binding.floatingMenu.close(true)

        }

        binding.bankDetails.setOnClickListener {
            navController.navigate(R.id.action_global_addBankDetails)
            binding.floatingMenu.close(true)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

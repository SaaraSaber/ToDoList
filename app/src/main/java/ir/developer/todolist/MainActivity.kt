package ir.developer.todolist

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ir.developer.todolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackground)

        navController = findNavController(R.id.my_nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)

        requestNotificationPermission()

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                R.id.categoryFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                }

                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }

        binding.btnClose.setOnClickListener {
            binding.layoutAdvertising.visibility = View.GONE
        }
        binding.btnAdvertising.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://cafebazaar.ir/app/ir.developre.chistangame")
            )
            startActivity(browserIntent)
        }

    }

    private var requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Log.d("POST_NOTIFICATION_PERMISSION", "USER DENIED PERMISSION")
            } else {
                Log.d("POST_NOTIFICATION_PERMISSION", "USER GRANTED PERMISSION")
                createNotificationChannel()
            }
        }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            when {
                ContextCompat.checkSelfPermission(
                    this, permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Action to take when permission is already granted

                    createNotificationChannel()
                }

                shouldShowRequestPermissionRationale(permission) -> {
                    // Action to take when permission was denied
                    requestPermissionLauncher.launch(permission)
                }

                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(permission)
                }
            }
        } else {
            // Device does not support required permission
//            Toast.makeText(this, "No required permission", Toast.LENGTH_LONG).show()
            Log.d("POST_NOTIFICATION_PERMISSION", "No required permission")
            createNotificationChannel()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "alarm_id",
            "alarm_name",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

    }
}
package ir.developer.todolist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.databinding.ActivityMainBinding
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.sharedPref.SharedPreferencesGame

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

//        window.navigationBarColor = ContextCompat.getColor(this, R.color.Watery)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.friendlyFrost)

        navController = findNavController(R.id.my_nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)


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

    }
}
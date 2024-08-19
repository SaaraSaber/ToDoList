package ir.developer.todolist

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.developer.todolist.databinding.ActivitySplashBinding


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBackground)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackground)
        animationTextView()

        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this, MainActivity::class.java)
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }, 1500)
    }

    private fun animationTextView() {
        // تنظیم موقعیت اولیه TextView در پایین صفحه
        binding.textSplash.translationY = 1000f
        binding.textSplash.scaleX = 0.5f
        binding.textSplash.scaleY = 0.5f

        // انیمیشن حرکت از پایین به بالا
        val translationAnimation = ObjectAnimator.ofFloat(binding.textSplash, "translationY", 0f)
        // انیمیشن بزرگ شدن در محور X
        val scaleXAnimation = ObjectAnimator.ofFloat(binding.textSplash, "scaleX", 1f)
        // انیمیشن بزرگ شدن در محور Y
        val scaleYAnimation = ObjectAnimator.ofFloat(binding.textSplash, "scaleY", 1f)

        // ترکیب انیمیشن‌ها با AnimatorSet
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnimation, scaleXAnimation, scaleYAnimation)
        animatorSet.duration = 1000 // مدت زمان انیمیشن
        animatorSet.start()
    }
}
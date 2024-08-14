package ir.developer.todolist.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ir.developer.todolist.R
import ir.developer.todolist.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationTextView()

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
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
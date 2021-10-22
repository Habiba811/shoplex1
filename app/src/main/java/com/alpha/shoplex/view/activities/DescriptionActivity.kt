package com.alpha.shoplex.view.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ActivityDescriptionBinding
import com.alpha.shoplex.model.extra.UserInfo
import java.util.*

class DescriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDescriptionBinding
    private var position = 0
    private var mediaControls: MediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnEnglish?.setOnClickListener {
            this.getSharedPreferences("LANG", MODE_PRIVATE).edit().putString("Language", "en")
                .apply()
            UserInfo.lang = "en"
            setLocale("en")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnArabic?.setOnClickListener {
            this.getSharedPreferences("LANG", MODE_PRIVATE).edit().putString("Language", "ar")
                .apply()
            UserInfo.lang = "ar"
            setLocale("ar")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.vvVideoView.setOnCompletionListener {
            binding.vvVideoView.start()
        }

        try {
            binding.vvVideoView.setMediaController(mediaControls)
            binding.vvVideoView.setVideoURI(
                Uri.parse(
                    binding.root.context.getString(R.string.android) + packageName + binding.root.context.getString(
                        R.string.splash
                    ) + R.raw.desc
                )
            )
        } catch (e: Exception) {
            e.message?.let { Log.e("Error", it) }
        }
        binding.vvVideoView.requestFocus()

        binding.vvVideoView.setOnPreparedListener {
            binding.vvVideoView.seekTo(position)
            if (position == 0) {
                binding.vvVideoView.start()
            } else {
                binding.vvVideoView.pause()
            }


        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            binding.root.context.getString(R.string.Position),
            binding.vvVideoView.currentPosition
        )
        binding.vvVideoView.pause()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        position = savedInstanceState.getInt("Position")

        binding.vvVideoView.seekTo(position)
    }

    fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        this.createConfigurationContext(config)
        this.resources.updateConfiguration(
            config, this.resources.displayMetrics
        )
    }
}
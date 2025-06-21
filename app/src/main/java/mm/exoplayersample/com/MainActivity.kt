package mm.exoplayersample.com

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mm.exoplayersample.com.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.linktextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://developer.android.com/media/media3/exoplayer".toUri()
            startActivity(intent)
        }

        binding.playBtn.setOnClickListener {
            startActivity(PlayerActivity.newIntent(this,
                binding.titleEditText.text.toString(),binding.urlEditText.text.toString()))
        }
    }
}
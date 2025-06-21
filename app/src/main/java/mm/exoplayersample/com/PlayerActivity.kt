package mm.exoplayersample.com

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import mm.exoplayersample.com.databinding.ActivityPlayerBinding
class PlayerActivity : AppCompatActivity(), Player.Listener {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var data: String
    private var title: String = ""
    private var player: Player? = null
    private var mediaTypeIndex = 0

    companion object {
        private const val EXTRA_URL = "url"
        private const val EXTRA_TITLE = "title"

        fun newIntent(context: Context, url: String, title: String) =
            Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_TITLE, title)
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        data = intent.getStringExtra(EXTRA_URL) ?: ""
        title = intent.getStringExtra(EXTRA_TITLE) ?: ""

        // Set title
        binding.txtTitle.text = title
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup UI controls
        setupControls()

    }

    private fun setupControls() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish() }
    }


    @UnstableApi
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
        }
    }

    @UnstableApi
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    @UnstableApi
    private fun initializePlayer() {
        try {
            val mediaType = ExoMediaTypeChecker().detectMediaType(data)
            mediaTypeIndex = when (mediaType) {
                ExoMediaTypeChecker.MediaType.HLS -> 2
                ExoMediaTypeChecker.MediaType.DASH -> 1
                ExoMediaTypeChecker.MediaType.PROGRESSIVE -> 4
                ExoMediaTypeChecker.MediaType.SMOOTH_STREAMING -> 3
                ExoMediaTypeChecker.MediaType.RTSP -> 5
                ExoMediaTypeChecker.MediaType.UNKNOWN -> 0
            }
            var mediaItem = when (mediaTypeIndex) {
                1 ->
                    MediaItem.Builder().setUri(data)
                        .setMimeType(MimeTypes.APPLICATION_MPD).build()

                2 -> MediaItem.Builder().setUri(data)
                    .setMimeType(MimeTypes.APPLICATION_M3U8).build()

                3 -> MediaItem.Builder().setUri(data)
                    .setMimeType(MimeTypes.APPLICATION_SS).build()

                4 -> MediaItem.Builder().setUri(data)
                    .setMimeType(MimeTypes.APPLICATION_MP4).build()

                5 -> MediaItem.Builder().setUri(data)
                    .setMimeType(MimeTypes.APPLICATION_RTSP).build()

                else -> MediaItem.Builder().setUri(data)
                    .setMimeType(MimeTypes.APPLICATION_MP4).build()
            }
            var mediaSource = when (mediaTypeIndex) {
                1 -> DashMediaSource.Factory( DefaultDashChunkSource.Factory( DefaultDataSource.Factory(this)),  DefaultDataSource.Factory(this))
                    .createMediaSource(mediaItem)


                2 -> HlsMediaSource.Factory(DefaultDataSource.Factory(this))
                    .createMediaSource(mediaItem)


                3 -> SsMediaSource.Factory(DefaultDataSource.Factory(this)).createMediaSource(mediaItem)


                4 -> ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this)).createMediaSource(mediaItem)


                5 -> RtspMediaSource.Factory().createMediaSource(mediaItem)


                else ->  ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this))
                    .createMediaSource(mediaItem)

            }


            player = ExoPlayer.Builder(this).build().apply {
                binding.videoView.player = this
                trackSelectionParameters = trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()
                setMediaSource(mediaSource)
                playWhenReady = true
                addListener(this@PlayerActivity)
                prepare()
                play()

            }

        } catch (e: Exception) {
            Log.e("PlayerActivity", "Error initializing player: ${e.message}")
            Toast.makeText(this, "Error initializing player", Toast.LENGTH_SHORT).show()
        }
    }

    private fun releasePlayer() {
        player?.let {
            it.removeListener(this)
            it.release()
        }
        player = null
    }
}
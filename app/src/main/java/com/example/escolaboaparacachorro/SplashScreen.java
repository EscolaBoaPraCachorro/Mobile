package com.example.escolaboaparacachorro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.escolaboaparacachorro.ui.home.HomeFragment;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        VideoView videoView = findViewById(R.id.videoSplash);

        // Caminho do vídeo na pasta raw
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.splash;
        videoView.setVideoPath(videoPath);

        // Quando o vídeo terminar, pula para a próxima tela
        videoView.setOnCompletionListener(mp -> {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        });

        videoView.start();
    }
}
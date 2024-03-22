package com.github.michaelwuensch.avathor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.michaelwuensch.avathorlibrary.AvathorFactory;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView mAvathorImage;
    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAvathorImage = findViewById(R.id.testImage);

        // Display test vector image as initial image
        mAvathorImage.setImageBitmap(AvathorFactory.getAvathor(MainActivity.this, "02ef2da605f5e9cc3f6a042f3258e7eec3ea442aadc4299ced0f8ec06d444ad8b8", AvathorFactory.AvatarSet.MIXED));

        Button randomizeButton = findViewById(R.id.button);
        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomAvathor();
            }
        });
    }

    public void randomAvathor() {
        Random rd = new Random();
        byte[] randomBytes = new byte[32];
        rd.nextBytes(randomBytes);
        mAvathorImage.setImageBitmap(AvathorFactory.getAvathor(MainActivity.this, bytesToHex(randomBytes), AvathorFactory.AvatarSet.MIXED));
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
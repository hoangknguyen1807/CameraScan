package com.example.camerascan;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class ShareActivity extends Activity {
    ImageButton buttonToShare;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);

        buttonToShare=findViewById(R.id.buttonToShare);

        buttonToShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToShareSheet = new Intent();
                intentToShareSheet.setAction(Intent.ACTION_SEND);
                //intentToShareSheet.putExtra(Intent.EXTRA_TEXT, "Check out this dope website! - https://mikemiller.design/"); // Simple text and URL to share
                intentToShareSheet.setType("text/plain");
                startActivity(intentToShareSheet);
            }
        });
    }

}

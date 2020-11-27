package com.ansdoship.virtualpixeldungeon;

import android.os.Bundle;
import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.utils.PDPlatformSupport;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.List;

public class AndroidLauncher extends AndroidApplication
{
    AndroidApplicationConfiguration config;

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1) {
			if (grantResults.length > 0) {
				if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
					finish();
				}
			}
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		requestPermissions();
		
        super.onCreate(savedInstanceState);

        Log.d("DEBUG", "AndroidLauncher::onCreate");
        GdxNativesLoader.load();
        FreeType.initFreeType();

        config = new AndroidApplicationConfiguration();
        config.useWakelock = true;
        launch(config);
    }
    
    private void launch(AndroidApplicationConfiguration config)
    {
        initialize(new PixelDungeon(new PDPlatformSupport<>("build 011", "virtualpixeldungeon/saves/", new AndroidInputProcessor(this))), config);
    }
	
	private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsList = new ArrayList<>();
            String[] permissions = {
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE
            };

            for (String permission : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission)) {
                    permissionsList.add(permission);
                }
            }

            if (!permissionsList.isEmpty()) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 1);
            }
			
        }
    }
	
    
}

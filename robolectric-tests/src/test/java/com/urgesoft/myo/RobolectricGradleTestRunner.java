package com.urgesoft.myo;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;

import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by szabol on 2014.11.11..
 */
public class RobolectricGradleTestRunner extends RobolectricTestRunner {

    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = 18;

    public RobolectricGradleTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {

        Path manifestPath = Paths.get("gopro-app", "AndroidManifest.xml");
        Path resPath = Paths.get("gopro-app/manifests/res");
        return new AndroidManifest(Fs.fileFromPath(manifestPath.toAbsolutePath().toString()), Fs.fileFromPath(resPath.toAbsolutePath().toString())) {
            @Override
            public int getTargetSdkVersion() {
                return MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;
            }
        };
    }
}

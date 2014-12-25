package com.urgesoft.gopro.controller;

import com.urgesoft.gopro.R;

public class GoProSettings {

    private GoProSettings() {

    }

    public enum Mode implements GoProSetting {
        CAMERA("%00", R.id.menu_gopro_cameramode_video),
        PHOTO("%01", R.id.menu_gopro_cameramode_photo),
        BURST("%02", R.id.menu_gopro_cameramode_burst),
        TIMELAPSE("%03", R.id.menu_gopro_cameramode_timelapse);

        private String param;

        private int statusValue;

        private int resourceId;

        private Mode(final String paramValue, int resourceId) {
            this.param = paramValue;
            this.resourceId = resourceId;
            statusValue = ordinal();
        }

        public static Mode forParamValue(String paramValue) {
            for (Mode mode : values()) {
                if (mode.param == paramValue) {
                    return mode;
                }
            }

            return null;
        }

        public static Mode forStatusValue(int status) {
            for (Mode mode : values()) {
                if (mode.statusValue == status) {
                    return mode;
                }
            }

            return null;
        }

        public static Mode forResourceId(int id) {
            for (Mode mode : values()) {
                if (mode.resourceId == id) {
                    return mode;
                }
            }

            return null;
        }

        @Override
        public String getParamValue() {
            return param;
        }

    }

    public enum Preview implements GoProSetting {
        OFF("%00"),
        ON("%02");

        private String param;

        private Preview(final String paramValue) {
            this.param = paramValue;
        }

        @Override
        public String getParamValue() {
            return param;
        }
    }
}

package com.zgamelogic.data.serializable.iOS;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class iOSNotification {
    private APS aps;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class APS {
        private Alert alert;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Alert {
            private String title;
            private String subtitle;
            private String body;
        }
    }
}

package com.pie.tlatoani.Util;

import java.util.ArrayList;

/**
 * Created by Tlatoani on 9/3/16.
 */
public abstract class Notification {

    public static class Query<N extends Notification> {
        private ArrayList<N> queriedNotifications = new ArrayList<>();
        private Handler<? super N> handler = null;

        public void notify(N notification) {
            if (handler == null) {
                queriedNotifications.add(notification);
            } else {
                handler.handle(notification);
            }
        }

        public void setHandler(Handler<? super N> handler) {
            if (this.handler == null && handler != null) {
                this.handler = handler;
                while (queriedNotifications.size() > 0 && this.handler != null) {
                    handler.handle(queriedNotifications.get(0));
                    queriedNotifications.remove(0);
                }
            } else {
                this.handler = handler;
            }
        }
    }

    public interface Handler<N extends Notification> {

        void handle(N notification);

    }
}

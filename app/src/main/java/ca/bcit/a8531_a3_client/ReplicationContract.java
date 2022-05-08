package ca.bcit.a8531_a3_client;

import android.provider.BaseColumns;

/**
 * This class is only defining database schema for use in other classes
 */
public final class ReplicationContract {
    // So the class cannot be instantiated
    private ReplicationContract() {}

    public static class ReplicationEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_CONTENTS = "contents";
    }
}

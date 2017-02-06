package me.zongren.databasesample;

import android.provider.BaseColumns;

/**
 * Created by 宗仁 on 2017/2/6.
 * All Rights Reserved By 秦皇岛商之翼网络科技有限公司.
 */

public final class PersonContract {

    private PersonContract() {
    }

    ;

    public static class PersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "person";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_AGE = "age";
    }
}

package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      AppDataBase
 * @ author:        QING
 * @ createTime:    2020-02-24 17:03
 * @ describe:      TODO 数据库
 */
@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION)
public class AppDataBase {
    static final String NAME = "LagenioDataBase"; // 数据库名
    static final int VERSION = 2; // 版本号

//    @Migration(version = 2, database = AppDataBase.class)
//    public static class Migration2 extends AlterTableMigration<DeviceModel> {
//
//        public Migration2(Class<DeviceModel> table) {
//            super(table);
//        }
//
//        @Override
//        public void onPreMigrate() {
//            addColumn(SQLiteType.INTEGER, "device_type");
//            addColumn(SQLiteType.TEXT, "ip");
//        }
//    }

}

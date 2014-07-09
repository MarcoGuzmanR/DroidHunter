package edu.training.droidbountyhunter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBProvider {
	public class DBhelper extends SQLiteOpenHelper {
		private static final String TAG = "DBManager";
		private static final String DATABASE_NAME = "droidBH.db";
		private static final int DATABASE_VERSION = 1;
		public static final String TABLE_NAME = "fugitivos";
		public static final String _ID = "id";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_STATUS = "status";

		DBhelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w("[CHECK]", "DBHelper.onCreate...");
			db.execSQL("CREATE TABLE " + DBhelper.TABLE_NAME + " ("
						+ DBhelper._ID + " INTEGER PRIMARY KEY,"
						+ DBhelper.COLUMN_NAME_NAME + " TEXT,"
						+ DBhelper.COLUMN_NAME_STATUS + " INTEGER"
						+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Actualización de BDD de la version " + oldVersion + " a la "
				  + newVersion + ", de la que se destruira la informacion anterior");

			db.execSQL("DROP TABLE IF EXISTS " + DBhelper.TABLE_NAME);

			onCreate(db);
		}

	}

	private DBhelper oDB;
	private SQLiteDatabase db;

	public DBProvider(Context context) {
		oDB = new DBhelper(context);
	}

	public void CloseDB() {
		if (db.isOpen()) {
			db.close();
		}
	}

	public boolean isOpenDB() {
		return(db.isOpen());
	}

	public long executeSQL(String sql, Object[] bindArgs) {
		long iRet = 0;
		db = oDB.getWritableDatabase();
		db.execSQL(sql, bindArgs);
		CloseDB();
		return (iRet);
	}

	public Cursor querySQL(String sql, String[] selectionArgs) {
		Cursor oRet = null;
		db = oDB.getReadableDatabase();
		oRet = db.rawQuery(sql, selectionArgs);
		return (oRet);
	}

	public void DeleteFugitivo(String pID) {
		Object[] aData = {pID};
		executeSQL("DELETE FROM " + DBhelper.TABLE_NAME + " WHERE " + DBhelper._ID + " = ?", aData);
	}

	public void UpdateFugitivo(String pStatus, String pID) {
		Object[] aData = {pStatus, pID};
		executeSQL("UPDATE " + DBhelper.TABLE_NAME + " SET " + DBhelper.COLUMN_NAME_STATUS + " = ? " +
					"WHERE " + DBhelper._ID + " = ?", aData);
	}

	public void InsertFugitivo(String pNombre) {
		Object[] aData = {pNombre, "0"};
		executeSQL("INSERT INTO " + DBhelper.TABLE_NAME + "(" + DBhelper.COLUMN_NAME_NAME + "," +
					DBhelper.COLUMN_NAME_STATUS + ") VALUES(?,?)", aData);
	}

	public String[][] ObtenerFugitivos(boolean pCapturado) {
		int iCnt = 0;
		String[][] aData = null;
		String [] aFils = {(pCapturado ? "1" : "0")};
		Cursor aRS = querySQL("SELECT * FROM " + DBhelper.TABLE_NAME + " WHERE " + DBhelper.COLUMN_NAME_STATUS + " = ? ORDER BY " + DBhelper.COLUMN_NAME_NAME, aFils);
		if (aRS.getCount() > 0) {
			aData = new String[aRS.getCount()][];
			while (aRS.moveToNext()) {
				aData[iCnt] = new String[3];
				aData[iCnt][0] = aRS.getString(aRS.getColumnIndex(DBhelper._ID));
				aData[iCnt][1] = aRS.getString(aRS.getColumnIndex(DBhelper.COLUMN_NAME_NAME));
				aData[iCnt][2] = aRS.getString(aRS.getColumnIndex(DBhelper.COLUMN_NAME_STATUS));
				iCnt++;
			}
		}
		else {
			aData = new String[0][];
		}

		aRS.close();
		CloseDB();
		return (aData);
	}

	public int ContarFugitivos() {
		int iCnt = 0;
		String[] aFils = {""};
		Cursor aRS = querySQL("SELET " + DBhelper._ID + " FROM " + DBhelper.TABLE_NAME + " WHERE id <> ?", aFils);
		iCnt = aRS.getCount();
		aRS.close();
		CloseDB();
		return (iCnt);
	}

}

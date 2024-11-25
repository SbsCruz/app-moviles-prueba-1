import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "SanJoseEvents.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "eventos"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "nombre_evento"
        const val COLUMN_ADDRESS = "direccion_evento"
        const val COLUMN_DATE = "fecha_evento"
        const val COLUMN_ASSISTANT = "assistants"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """ 
        CREATE TABLE $TABLE_NAME ( 
          $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
          $COLUMN_NAME TEXT NOT NULL, 
          $COLUMN_ADDRESS TEXT NOT NULL, 
          $COLUMN_DATE DATE NOT NULL,
          $COLUMN_ASSISTANT TEXT NOT NULL
        )
    """
        db.execSQL(createTable)
    }


    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertEvent(
        name: String, address: String, date: String, assistant: String
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_ADDRESS, address)
            put(COLUMN_DATE, date)
            put(COLUMN_ASSISTANT, assistant)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllEvents(): Cursor {
        val db = this.readableDatabase
        val projection = arrayOf(
            COLUMN_NAME, COLUMN_ADDRESS, COLUMN_DATE, COLUMN_ASSISTANT
        )
        return db.query(TABLE_NAME, null, null, null, null, null, null)
    }

}



package ch.heigvd.iict.and.rest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import ch.heigvd.iict.and.rest.database.converters.CalendarConverter
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.ContactState
import ch.heigvd.iict.and.rest.models.PhoneType
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.concurrent.thread

@Database(entities = [Contact::class], version = 1, exportSchema = true)
@TypeConverters(CalendarConverter::class)
abstract class ContactsDatabase : RoomDatabase() {

    abstract fun contactsDao() : ContactsDao

    companion object {

        @Volatile
        private var INSTANCE : ContactsDatabase? = null

        fun getDatabase(context: Context) : ContactsDatabase {

            return INSTANCE ?: synchronized(this) {
                val _instance = Room.databaseBuilder(context.applicationContext,
                ContactsDatabase::class.java, "contacts.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(MyDatabaseCallback()) // FIXME - can be removed
                    .build()

                INSTANCE = _instance
                _instance
            }
        }
    }

    // FIXME - can be removed
    private class MyDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let{ database ->
                thread {
                    if(database.contactsDao().getCount() == 0) {
                        val c1 =  Contact(  id = null,
                            state = ContactState.SYNCED,
                            name = "Pavarovich",
                            firstname = "Lazar",
                            birthday = GregorianCalendar.getInstance().apply {
                                set(Calendar.YEAR, 1997)
                                set(Calendar.MONTH, Calendar.DECEMBER)
                                set(Calendar.DAY_OF_MONTH, 16)
                                set(Calendar.HOUR_OF_DAY, 12)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            },
                            email = "l.pavarovich@heig-vd.ch",
                            address = "Route de Cheseaux 1",
                            zip = "1400", city = "Yverdon-les-Bains",
                            type = PhoneType.OFFICE, phoneNumber = "024 111 22 33" )

                        val c2 =  Contact(  id = null,
                            state = ContactState.SYNCED,
                            name = "Croussaz",
                            firstname = "Nicolas",
                            birthday = GregorianCalendar.getInstance().apply {
                                set(Calendar.YEAR, 2001)
                                set(Calendar.MONTH, Calendar.JULY)
                                set(Calendar.DAY_OF_MONTH, 9)
                                set(Calendar.HOUR_OF_DAY, 12)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            },
                            email = "n.croussaz@gmail.com",
                            address = "Avenue des Sports 20",
                            zip = "1400", city = "Yverdon-les-Bains",
                            type = PhoneType.MOBILE, phoneNumber = "079 111 22 33" )

                        val c3 =  Contact(  id = null,
                            state = ContactState.SYNCED,
                            name = "Schwartz",
                            firstname = "Maxime",
                            birthday = GregorianCalendar.getInstance().apply {
                                set(Calendar.YEAR, 1980)
                                set(Calendar.MONTH, Calendar.JANUARY)
                                set(Calendar.DAY_OF_MONTH, 1)
                                set(Calendar.HOUR_OF_DAY, 12)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            },
                            email = "m.schwartz@pornhub.com",
                            address = "Rue de la Gare 1",
                            zip = "1400", city = "Yverdon-les-Bains",
                            type = PhoneType.MOBILE, phoneNumber = "079 111 22 33" )


                        database.contactsDao().insert(c1)
                        database.contactsDao().insert(c2)
                        database.contactsDao().insert(c3)
                    }
                }
            }

        }
    }

}
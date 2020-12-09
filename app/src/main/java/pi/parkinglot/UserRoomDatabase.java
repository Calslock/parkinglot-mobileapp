package pi.parkinglot;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.net.ContentHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class UserRoomDatabase extends RoomDatabase {

    private static volatile UserRoomDatabase INSTANCE;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(1);

    static public UserRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (UserRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserRoomDatabase.class, "userDatabase").build();
                }
            }
        }
        return INSTANCE;
    }


    public abstract UserDao userDao();
}

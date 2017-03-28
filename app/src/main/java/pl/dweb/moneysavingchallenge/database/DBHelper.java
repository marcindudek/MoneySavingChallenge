package pl.dweb.moneysavingchallenge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import pl.dweb.moneysavingchallenge.model.ChallengeEntity;
import pl.dweb.moneysavingchallenge.model.DueEntity;

/**
 * Created by md on 3/28/17.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "challenges.db";
    private static final int DB_VERSION = 1;
    private Dao<ChallengeEntity, Integer> challengeDao;
    private Dao<DueEntity, Long> dueDao;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ChallengeEntity.class);
            TableUtils.createTable(connectionSource, DueEntity.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ChallengeEntity.class, true);
            TableUtils.dropTable(connectionSource, DueEntity.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(database, connectionSource);
    }

    public Dao<ChallengeEntity, Integer> getChallengeDao () throws SQLException {
        if(challengeDao == null) {
            challengeDao = getDao(ChallengeEntity.class);
        }
        return challengeDao;
    }

    public Dao<DueEntity, Long> getDueyDao () throws SQLException {
        if(dueDao == null) {
            dueDao = getDao(DueEntity.class);
        }
        return dueDao;
    }
}

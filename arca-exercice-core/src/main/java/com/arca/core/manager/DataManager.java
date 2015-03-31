package com.arca.core.manager;

import com.arca.core.entity.DataEntity;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Data manager
 */
public class DataManager {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(DataManager.class);

    private static ResourceBundle bundleConfigApplication = ResourceBundle.getBundle("application");
    private static final String ADDRESS = bundleConfigApplication.getString("db.address");
    private static final int PORT = Integer.valueOf(bundleConfigApplication.getString("db.port"));
    private static final String DB_NAME = bundleConfigApplication.getString("db.name");

    private static Morphia morphia;
    private static MongoClient mongoClient;
    private static Datastore datastore;

    private static MongoClient getMongoClient() throws UnknownHostException {
        if (mongoClient == null) {
            mongoClient = new MongoClient(ADDRESS, PORT);
        }
        return mongoClient;
    }

    private static Datastore getDatastore() throws UnknownHostException {
        if (datastore == null) {
            datastore = new Morphia().createDatastore(getMongoClient(), DB_NAME);
        }
        return datastore;
    }

    /**
     * Create a new Data com.arca.core.entity in database
     *
     * @param dataEntity
     * @return
     * @throws UnknownHostException
     */

    public static DataEntity createData(DataEntity dataEntity) throws UnknownHostException {
        getDatastore().save(dataEntity);
        return dataEntity;
    }

    /**
     * Return a list of data from offset to limit
     *
     * @param offset
     * @param limit
     * @return
     * @throws UnknownHostException
     */
    public static List<DataEntity> getAllData(int offset, int limit) throws UnknownHostException {
        return getDatastore().find(DataEntity.class).offset(offset).limit(limit).asList();
    }

    /**
     * Return the count of all data in database
     *
     * @return
     * @throws UnknownHostException
     */
    public static long getCount() throws UnknownHostException {
        return getDatastore().find(DataEntity.class).countAll();
    }

    /**
     * Return the last insert row in database
     *
     * @return
     * @throws java.net.UnknownHostException
     */
    public static DataEntity getLastData() throws UnknownHostException {
        return getDatastore().find(DataEntity.class).order("-line").get();
    }
}

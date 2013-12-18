package com.thinkaurelius.titan.diskstorage.persistit;

import com.thinkaurelius.titan.PersistitStorageSetup;
import com.thinkaurelius.titan.diskstorage.KeyColumnValueStoreTest;
import com.thinkaurelius.titan.diskstorage.StorageException;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.*;

import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.OrderedKeyValueStoreManagerAdapter;
import org.apache.commons.configuration.Configuration;

public class PersistitKeyColumnValueVariableTest extends KeyColumnValueStoreTest {

    public KeyColumnValueStoreManager openStorageManager() throws StorageException {
        PersistitStoreManager sm = new PersistitStoreManager(PersistitStorageSetup.getPersistitConfig());
        return new OrderedKeyValueStoreManagerAdapter(sm);
    }
}

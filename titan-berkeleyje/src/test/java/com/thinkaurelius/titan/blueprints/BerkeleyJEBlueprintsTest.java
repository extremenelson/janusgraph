package com.thinkaurelius.titan.blueprints;

import com.thinkaurelius.titan.BerkeleyJeStorageSetup;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.diskstorage.StorageException;
import com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager;
import com.thinkaurelius.titan.diskstorage.configuration.ModifiableConfiguration;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.Graph;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Assert;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class BerkeleyJEBlueprintsTest extends TitanBlueprintsTest {
    
    private static final String DEFAULT_SUBDIR = "standard";
    

    @Override
    public Graph generateGraph() {
        return generateGraph(DEFAULT_SUBDIR);
    }
    
    private final Set<String> openDirs = new HashSet<String>(); 

    @Override
    public Graph generateGraph(String uid) {
        String dir = BerkeleyJeStorageSetup.getHomeDir(uid);
        System.out.println("Opening graph in: " + dir);
        Graph graph = TitanFactory.open(BerkeleyJeStorageSetup.getBerkeleyJEConfiguration(dir));
        synchronized (openDirs) {
            openDirs.add(dir);
        }
        return graph;
    }

    @Override
    public boolean supportsMultipleGraphs() {
        return true;
    }

    @Override
    public void cleanUp() throws StorageException {
        synchronized (openDirs) {
            for (String dir : openDirs) {
                BerkeleyJEStoreManager s = new BerkeleyJEStoreManager(BerkeleyJeStorageSetup.getBerkeleyJEConfiguration(dir));
                s.clearStorage();
                File dirFile = new File(dir);
                Assert.assertFalse(dirFile.exists() && dirFile.listFiles().length > 0);
            }
        }
    }


    @Override
    public void startUp() {
        //Nothing
    }

    @Override
    public void shutDown() {
        synchronized (openDirs) {
            for (String dir : openDirs) {
                File dirFile = new File(dir);
                Assert.assertFalse(dirFile.exists() && dirFile.listFiles().length > 0);
            }
            openDirs.clear();
        }
    }
}

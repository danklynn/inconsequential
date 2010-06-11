/* Copyright (C) 2010 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.inconsequential.cassandra;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import org.grails.inconsequential.core.AbstractDatastore;
import org.grails.inconsequential.core.Connection;
import org.grails.inconsequential.kv.mapping.KeyValueMappingContext;
import org.grails.inconsequential.mapping.MappingContext;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.Map;

/**
 * @author Graeme Rocher
 * @since 1.0
 */
public class CassandraDatastore extends AbstractDatastore {
    public static final String DEFAULT_KEYSPACE = "Keyspace1";
    private CassandraClientPool connectionPool;

    public CassandraDatastore(MappingContext mappingContext) {
        super(mappingContext);
        this.connectionPool = CassandraClientPoolFactory.INSTANCE.get();
    }

    public CassandraDatastore() {
        this(new KeyValueMappingContext(DEFAULT_KEYSPACE));
    }

    @Override
    protected Connection createConnection(Map<String, String> connectionDetails) {
        final CassandraClient client;
        try {
            client = connectionPool.borrowClient("localhost", 9160);
            return new CassandraConnection(connectionDetails,getMappingContext(), connectionPool, client);
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed to obtain Cassandra client connection: " + e.getMessage(), e);
        }
    }
}

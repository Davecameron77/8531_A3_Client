package ca.bcit.a8531_a3_client;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * This object is intended to represent a proposed database transaction.  It can then
 * be serialized and passed around the network, when the quorum says it's OK to persist
 * it can be identified via the UUID.
 * Timestamp is there for convenience if things should be ordered.
 * @param <T> Setup to use generics, but everything is stored via toString()
 */
public class DbTransaction<T> {
    protected UUID transactionId;
    protected Instant transactionTimestamp;
    protected List<String> entries;
    //TODO - Likely add a list of quorum members so that those consenting to the transaction
    //       can be shared amongst members

    public DbTransaction(List<T> proposedEntries) {
        transactionId = UUID.randomUUID();
        transactionTimestamp = Instant.now();

        for (T entry : proposedEntries) {
            entries.add(entry.toString());
        }
    }
}

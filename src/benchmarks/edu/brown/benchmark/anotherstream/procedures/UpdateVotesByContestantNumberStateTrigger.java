package edu.brown.benchmark.anotherstream.procedures;

import org.voltdb.SQLStmt;
import org.voltdb.VoltTrigger;

public class UpdateVotesByContestantNumberStateTrigger extends VoltTrigger {

    @Override
    protected String toSetStreamName() {
        return "S4";
    }

    // step 5: Validate number of votes
    // FIXME: here we hack insert behavior to do update thing 
    public final SQLStmt insertVotesByContestantNumberStateStmt = 
        new SQLStmt("INSERT INTO votes_by_contestant_number_state ( contestant_number, state, num_votes ) SELECT contestant_number, state, num_votes + 1 FROM S4;", true);
    
    // FIXME, after using the tuple in stream, we should delete it. 
    public final SQLStmt deleteStreamStmt = 
        new SQLStmt("DELETE FROM S4");

}

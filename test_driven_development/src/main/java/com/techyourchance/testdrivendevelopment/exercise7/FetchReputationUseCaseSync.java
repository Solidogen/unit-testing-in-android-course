package com.techyourchance.testdrivendevelopment.exercise7;

public interface FetchReputationUseCaseSync {

    enum Status {
        FAILURE, SUCCESS
    }

    class UseCaseResult {

        private Status status;
        private int reputation;

        public UseCaseResult(Status mStatus, int reputation) {
            this.status = mStatus;
            this.reputation = reputation;
        }

        public Status getStatus() {
            return status;
        }

        public int getReputation() {
            return reputation;
        }

    }

    UseCaseResult fetchReputationSync() throws Exception;
}

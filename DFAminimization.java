import java.util.*;

public class DFAminimization {

    public DFA minimization(DFA dfa) {
        Set<DFAState> reachableStates = findReachableStates(dfa.startState);
        List<Set<DFAState>> partitions = createInitialPartitions(reachableStates);
        partitions = refinePartitions(partitions);
        return createMinimizedDFA(dfa.startState, partitions);
    }

    private static Set<DFAState> findReachableStates(DFAState startState) {
        Set<DFAState> reachableState = new HashSet<>();
        Queue<DFAState> queue = new LinkedList<>();
        queue.add(startState);
        reachableState.add(startState);

        while (!queue.isEmpty()) {
            DFAState state = queue.poll();
            for (DFAState nextState : state.transitions.values()) {
                if (!reachableState.contains(nextState)) {
                    reachableState.add(nextState);
                    queue.add(nextState);
                }
            }
        }
        return reachableState;
    }

    private List<Set<DFAState>> createInitialPartitions(Set<DFAState> states) {
        List<Set<DFAState>> partitions = new ArrayList<>();
        Set<DFAState> finalStates = new HashSet<>();
        Set<DFAState> nonFinalStates = new HashSet<>();

        for (DFAState state : states) {
            if (state.isFinal) {
                finalStates.add(state);
            } else {
                nonFinalStates.add(state);
            }
        }

        if (!finalStates.isEmpty()) partitions.add(finalStates);
        if (!nonFinalStates.isEmpty()) partitions.add(nonFinalStates);

        return partitions;
    }

    private List<Set<DFAState>> refinePartitions(List<Set<DFAState>> partitions) {
        boolean changed;
        do {
            changed = false;
            List<Set<DFAState>> newPartitions = new ArrayList<>();

            for (Set<DFAState> partition : partitions) {
                if (partition.size() <= 1) {
                    newPartitions.add(partition);
                    continue;
                }

                Map<String, Set<DFAState>> transitionGroups = new HashMap<>();

                for (DFAState state : partition) {
                    String transitionKey = getTransitionKey(state, partitions);
                    transitionGroups.computeIfAbsent(transitionKey, k -> new HashSet<>()).add(state);
                }

                if (transitionGroups.size() > 1) {
                    newPartitions.addAll(transitionGroups.values());
                    changed = true;
                } else {
                    newPartitions.add(partition);
                }
            }

            partitions = newPartitions;
        } while (changed);

        return partitions;
    }

    private String getTransitionKey(DFAState state, List<Set<DFAState>> partitions) {
        StringBuilder key = new StringBuilder();
        List<Character> sortedInputs = new ArrayList<>(state.transitions.keySet());
        Collections.sort(sortedInputs);

        for (char input : sortedInputs) {
            DFAState nextState = state.transitions.get(input);
            int partitionIndex = getPartitionIndex(nextState, partitions);
            key.append(input).append(':').append(partitionIndex).append(',');
        }

        return key.toString();
    }

    private int getPartitionIndex(DFAState state, List<Set<DFAState>> partitions) {
        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).contains(state)) {
                return i;
            }
        }
        return -1;
    }

    private DFA createMinimizedDFA(DFAState originalStart, List<Set<DFAState>> partitions) {
        Map<Set<DFAState>, DFAState> newStates = new HashMap<>();
        int stateId = 0;

        for (Set<DFAState> partition : partitions) {
            DFAState representative = partition.iterator().next();
            DFAState newState = new DFAState(stateId++, representative.isFinal);
            newStates.put(partition, newState);
        }

        for (Set<DFAState> partition : partitions) {
            DFAState representative = partition.iterator().next();
            DFAState newState = newStates.get(partition);

            for (Map.Entry<Character, DFAState> transition : representative.transitions.entrySet()) {
                char input = transition.getKey();
                DFAState targetState = findPartitionRepresentative(transition.getValue(), partitions);
                Set<DFAState> targetPartition = null;
                for (Set<DFAState> p : partitions) {
                    if (p.contains(targetState)) {
                        targetPartition = p;
                        break;
                    }
                }
                newState.transitions.put(input, newStates.get(targetPartition));
            }
        }

        DFAState startRepresentative = findPartitionRepresentative(originalStart, partitions);
        Set<DFAState> startPartition = null;
        for (Set<DFAState> p : partitions) {
            if (p.contains(startRepresentative)) {
                startPartition = p;
                break;
            }
        }
        DFAState newStartState = newStates.get(startPartition);

        DFA minimizedDFA = new DFA(newStartState);
        minimizedDFA.states = new ArrayList<>(newStates.values());
        return minimizedDFA;
    }

    private DFAState findPartitionRepresentative(DFAState state, List<Set<DFAState>> partitions) {
        for (Set<DFAState> partition : partitions) {
            if (partition.contains(state)) {
                return partition.iterator().next();
            }
        }
        return null;
    }
}
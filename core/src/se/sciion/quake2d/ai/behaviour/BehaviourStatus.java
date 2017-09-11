package se.sciion.quake2d.ai.behaviour;

// When traversing our behaviour trees,
// we don't know what states nodes will
// be in. So they will be UNDEFINED. If
// we are running this behaviour, it is
// RUNNING. If we leave an node, either
// we have SUCCEEDED or have an FAILURE
// for the behaviour. More are possible
// but for our very simple AI, it's ok.

public enum BehaviourStatus {
    UNDEFINED, RUNNING,
    SUCCESS,   FAILURE
}

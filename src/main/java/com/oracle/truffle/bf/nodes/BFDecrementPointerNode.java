package com.oracle.truffle.bf.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class BFDecrementPointerNode extends BFInstructionNode {

    private final FrameSlot ptr;

    public BFDecrementPointerNode(String name, FrameSlot ptr) {
        super(name);
        this.ptr = ptr;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        int ptrValue = FrameUtil.getIntSafe(frame, ptr);
        frame.setInt(ptr, --ptrValue);
    }
}

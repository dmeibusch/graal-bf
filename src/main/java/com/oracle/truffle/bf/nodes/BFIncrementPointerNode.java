package com.oracle.truffle.bf.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class BFIncrementPointerNode extends BFInstructionNode {

    private final FrameSlot ptr;

    public BFIncrementPointerNode(String name, FrameSlot ptr) {
        super(name);
        this.ptr = ptr;
    }

    @Override
    public void executeVoid(VirtualFrame frame)  {
        int ptrValue = FrameUtil.getIntSafe(frame, ptr);
        frame.setInt(ptr, ++ptrValue);
    }
}

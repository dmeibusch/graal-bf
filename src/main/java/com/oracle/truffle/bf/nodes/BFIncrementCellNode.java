package com.oracle.truffle.bf.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class BFIncrementCellNode extends BFInstructionNode {

    private final FrameSlot ptr;
    private final FrameSlot cells;

    public BFIncrementCellNode(String name, FrameSlot ptr, FrameSlot cells) {
        super(name);
        this.ptr = ptr;
        this.cells = cells;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        int ptrValue = FrameUtil.getIntSafe(frame, ptr);
        byte[] cellArray = (byte[]) frame.getValue(cells);
        cellArray[ptrValue]++;
    }
}

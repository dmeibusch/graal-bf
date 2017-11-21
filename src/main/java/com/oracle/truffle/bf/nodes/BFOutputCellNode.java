package com.oracle.truffle.bf.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.io.PrintWriter;

public class BFOutputCellNode extends BFInstructionNode {

    private final FrameSlot ptr;
    private final FrameSlot cells;
    private final FrameSlot stdout;

    public BFOutputCellNode(String name, FrameSlot ptr, FrameSlot cells, FrameSlot stdout) {
        super(name);
        this.ptr = ptr;
        this.cells = cells;
        this.stdout = stdout;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        int ptrValue = FrameUtil.getIntSafe(frame, ptr);
        byte[] cellArray = (byte[]) frame.getValue(cells);
        PrintWriter stdoutWriter = (PrintWriter) frame.getValue(stdout);
        stdoutWriter.print((char) cellArray[ptrValue]);
        stdoutWriter.flush();
    }
}

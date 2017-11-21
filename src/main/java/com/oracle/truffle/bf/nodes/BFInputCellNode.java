package com.oracle.truffle.bf.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.io.BufferedReader;
import java.io.IOException;

public class BFInputCellNode extends BFInstructionNode {

    private final FrameSlot ptr;
    private final FrameSlot cells;
    private final FrameSlot stdin;

    public BFInputCellNode(String name, FrameSlot ptr, FrameSlot cells, FrameSlot stdin) {
        super(name);
        this.ptr = ptr;
        this.cells = cells;
        this.stdin = stdin;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        int ptrValue = FrameUtil.getIntSafe(frame, ptr);
        byte[] cellArray = (byte[]) frame.getValue(cells);
        BufferedReader stdinReader = (BufferedReader) frame.getValue(stdin);
        try {
            cellArray[ptrValue] = (byte) stdinReader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

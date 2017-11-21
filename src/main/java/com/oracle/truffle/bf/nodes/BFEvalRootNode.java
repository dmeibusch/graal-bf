/*
 * Copyright (c) 2012, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.bf.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.bf.BFLanguage;
import com.oracle.truffle.bf.runtime.BFContext;

@NodeInfo(language = "BF", description = "The root node to evaluate")
public final class BFEvalRootNode extends RootNode {

    @CompilationFinal private BFContext context;
    private final FrameSlot ptr;
    private final FrameSlot cells;
    private final FrameSlot stdin;
    private final FrameSlot stdout;

    @Child
    private BFBlockNode bodyNode;

    /** The name of the function, for printing purposes only. */
    private final String name;

    public BFEvalRootNode(FrameDescriptor frameDescriptor, BFBlockNode bodyNode, SourceSection sourceSection, String name) {
        super(BFLanguage.class, sourceSection, frameDescriptor);
        this.bodyNode = bodyNode;
        this.name = name;
        this.ptr = frameDescriptor.findOrAddFrameSlot(BFLanguage.PTR, FrameSlotKind.Int);
        this.cells = frameDescriptor.findOrAddFrameSlot(BFLanguage.CELLS, FrameSlotKind.Object);
        this.stdin = frameDescriptor.findOrAddFrameSlot(BFLanguage.STDIN, FrameSlotKind.Object);
        this.stdout = frameDescriptor.findOrAddFrameSlot(BFLanguage.STDOUT, FrameSlotKind.Object);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        context = BFLanguage.INSTANCE.findContext();
        frame.setInt(ptr, 0);
        frame.setObject(cells, new byte[30_000]);
        frame.setObject(stdin, context.getInput());
        frame.setObject(stdout, context.getOutput());
        bodyNode.executeVoid(frame);
        return null;
    }

    public BFBlockNode getBodyNode() {
        return bodyNode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "root " + name;
    }
}

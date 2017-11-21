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
package com.oracle.truffle.bf;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.bf.nodes.BFBlockNode;
import com.oracle.truffle.bf.nodes.BFEvalRootNode;
import com.oracle.truffle.bf.parser.Parser;
import com.oracle.truffle.bf.runtime.BFContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@TruffleLanguage.Registration(name = "BF", version = "1.0", mimeType = BFLanguage.MIME_TYPE)
@ProvidedTags({StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class, DebuggerTags.AlwaysHalt.class})
public final class BFLanguage extends TruffleLanguage<BFContext> {

    public static final String MIME_TYPE = "application/x-brainfork";
    public static final String PTR = "ptr";
    public static final String CELLS = "cells";
    public static final String STDOUT = "stdout";
    public static final String STDIN = "stdin";

    /**
     * The singleton instance of the language.
     */
    public static final BFLanguage INSTANCE = new BFLanguage();

    /**
     * No instances allowed apart from the {@link #INSTANCE singleton instance}.
     */
    private BFLanguage() {
    }

    @Override
    protected BFContext createContext(Env env) {
        BufferedReader in = new BufferedReader(new InputStreamReader(env.in()));
        PrintWriter out = new PrintWriter(env.out(), true);
        return new BFContext(env, in, out);
    }

    @Override
    protected CallTarget parse(Source source, Node node, String... argumentNames) throws IOException {
        FrameDescriptor frameDescriptor = new FrameDescriptor();

        BFBlockNode rootNode;
        try {
            /*
             * Parse the provided source. At this point, we do not have a SLContext yet.
             * Registration of the functions with the SLContext happens lazily in SLEvalRootNode.
             */
            rootNode = Parser.parseBF(source, frameDescriptor);
        } catch (Throwable ex) {
            /*
             * The specification says that exceptions during parsing have to wrapped with an
             * IOException.
             */
            throw new IOException(ex);
        }

        BFEvalRootNode eval = new BFEvalRootNode(frameDescriptor, rootNode, null, "eval");
        return Truffle.getRuntime().createCallTarget(eval);
    }

    @Override
    protected Object findExportedSymbol(BFContext context, String globalName, boolean onlyExplicit) {
        return null;
    }

    @Override
    protected Object getLanguageGlobal(BFContext context) {
        /*
         * The context itself is the global function registry. SL does not have global variables.
         */
        return context;
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }

    @Override
    protected Object evalInContext(Source source, Node node, MaterializedFrame mFrame) throws IOException {
        throw new IllegalStateException("evalInContext not supported in SL");
    }

    public BFContext findContext() {
        CompilerAsserts.neverPartOfCompilation();
        return super.findContext(super.createFindContextNode());
    }
}

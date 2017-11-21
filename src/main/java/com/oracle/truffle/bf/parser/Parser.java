
package com.oracle.truffle.bf.parser;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.bf.BFException;
import com.oracle.truffle.bf.nodes.BFBlockNode;
import com.oracle.truffle.bf.nodes.BFDecrementCellNode;
import com.oracle.truffle.bf.nodes.BFDecrementPointerNode;
import com.oracle.truffle.bf.nodes.BFIncrementCellNode;
import com.oracle.truffle.bf.nodes.BFIncrementPointerNode;
import com.oracle.truffle.bf.nodes.BFInputCellNode;
import com.oracle.truffle.bf.nodes.BFLoopNode;
import com.oracle.truffle.bf.nodes.BFOutputCellNode;
import com.oracle.truffle.bf.nodes.BFStatementNode;

import java.util.ArrayList;
import java.util.List;

import static com.oracle.truffle.bf.BFLanguage.CELLS;
import static com.oracle.truffle.bf.BFLanguage.PTR;
import static com.oracle.truffle.bf.BFLanguage.STDIN;
import static com.oracle.truffle.bf.BFLanguage.STDOUT;

// Checkstyle: stop
// @formatter:off
public class Parser {
	public static final int _EOF = 0;
	public static final int maxT = 9;

    static final boolean _T = true;
    static final boolean _x = false;
    static final int minErrDist = 2;

    public Token t; // last recognized token
    public Token la; // lookahead token
    int errDist = minErrDist;

    public final Scanner scanner;
    public final Errors errors;
    private FrameDescriptor frameDescriptor;
    private BFBlockNode root;

    
    public Parser(Source source) {
        this.scanner = new Scanner(source.getInputStream());
        errors = new Errors();
    }

    void SynErr(int n) {
        if (errDist >= minErrDist)
            errors.SynErr(la.line, la.col, n);
        errDist = 0;
    }

    public void SemErr(String msg) {
        if (errDist >= minErrDist)
            errors.SemErr(t.line, t.col, msg);
        errDist = 0;
    }

    void Get() {
        for (;;) {
            t = la;
            la = scanner.Scan();
            if (la.kind <= maxT) {
                ++errDist;
                break;
            }

            la = t;
        }
    }

    void Expect(int n) {
        if (la.kind == n)
            Get();
        else {
            SynErr(n);
        }
    }

    boolean StartOf(int s) {
        return set[s][la.kind];
    }

    void ExpectWeak(int n, int follow) {
        if (la.kind == n)
            Get();
        else {
            SynErr(n);
            while (!StartOf(follow))
                Get();
        }
    }

    boolean WeakSeparator(int n, int syFol, int repFol) {
        int kind = la.kind;
        if (kind == n) {
            Get();
            return true;
        } else if (StartOf(repFol))
            return false;
        else {
            SynErr(n);
            while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
                Get();
                kind = la.kind;
            }
            return StartOf(syFol);
        }
    }

	void BrainFork() {
		List<BFStatementNode> nodes = new ArrayList<>(); 
		while (StartOf(1)) {
			BFStatementNode node = Instruction();
			nodes.add(node); 
		}
		root = new BFBlockNode(nodes.toArray(new BFStatementNode[] {})); 
	}

	BFStatementNode  Instruction() {
		BFStatementNode  result;
		result = null; 
		switch (la.kind) {
		case 1: {
			Get();
			result = new BFIncrementPointerNode(t.val, ptr());
			break;
		}
		case 2: {
			Get();
			result = new BFDecrementPointerNode(t.val, ptr());
			break;
		}
		case 3: {
			Get();
			result = new BFIncrementCellNode(t.val, ptr(), cells());
			break;
		}
		case 4: {
			Get();
			result = new BFDecrementCellNode(t.val, ptr(), cells());
			break;
		}
		case 5: {
			Get();
			result = new BFOutputCellNode(t.val, ptr(), cells(), stdout());
			break;
		}
		case 6: {
			Get();
			result = new BFInputCellNode(t.val, ptr(), cells(), stdin());
			break;
		}
		case 7: {
			BFLoopNode loopNode = Loop();
			result = loopNode; 
			break;
		}
		default: SynErr(10); break;
		}
		return result;
	}

	BFLoopNode  Loop() {
		BFLoopNode  result;
		Expect(7);
		result = null;
		List<BFStatementNode> nodes = new ArrayList<>(); 
		while (StartOf(1)) {
			BFStatementNode node = Instruction();
			nodes.add(node); 
		}
		Expect(8);
		result = new BFLoopNode(new BFBlockNode(nodes.toArray(new BFStatementNode[] {})), ptr(), cells()); 
		return result;
	}



    public void Parse() {
        la = new Token();
        la.val = "";
        Get();
		BrainFork();
		Expect(0);

    }

    private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _x,_x,_x}


    };

    public static BFBlockNode parseBF(Source source, FrameDescriptor frameDescriptor) {
        Parser parser = new Parser(source);
        parser.frameDescriptor = frameDescriptor;
        parser.Parse();
        if (parser.errors.errors.size() > 0) {
            StringBuilder msg = new StringBuilder("Error(s) parsing script:\n");
            for (String error : parser.errors.errors) {
                msg.append(error).append("\n");
            }
            throw new BFException(msg.toString());
        }
        return parser.root;
    }

    private FrameSlot ptr() {
        return frameDescriptor.findOrAddFrameSlot(PTR, FrameSlotKind.Int);
    }

    private FrameSlot cells() {
        return frameDescriptor.findOrAddFrameSlot(CELLS, FrameSlotKind.Int);
    }

    private FrameSlot stdin() {
        return frameDescriptor.findOrAddFrameSlot(STDIN, FrameSlotKind.Int);
    }

    private FrameSlot stdout() {
        return frameDescriptor.findOrAddFrameSlot(STDOUT, FrameSlotKind.Int);
    }

} // end Parser

class Errors {

    protected final List<String> errors = new ArrayList<>();
    public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text

    protected void printMsg(int line, int column, String msg) {
        StringBuffer b = new StringBuffer(errMsgFormat);
        int pos = b.indexOf("{0}");
        if (pos >= 0) {
            b.delete(pos, pos + 3);
            b.insert(pos, line);
        }
        pos = b.indexOf("{1}");
        if (pos >= 0) {
            b.delete(pos, pos + 3);
            b.insert(pos, column);
        }
        pos = b.indexOf("{2}");
        if (pos >= 0)
            b.replace(pos, pos + 3, msg);
        errors.add(b.toString());
    }

    public void SynErr(int line, int col, int n) {
        String s;
        switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "\">\" expected"; break;
			case 2: s = "\"<\" expected"; break;
			case 3: s = "\"+\" expected"; break;
			case 4: s = "\"-\" expected"; break;
			case 5: s = "\".\" expected"; break;
			case 6: s = "\",\" expected"; break;
			case 7: s = "\"[\" expected"; break;
			case 8: s = "\"]\" expected"; break;
			case 9: s = "??? expected"; break;
			case 10: s = "invalid Instruction"; break;
            default:
                s = "error " + n;
                break;
        }
        printMsg(line, col, s);
    }

    public void SemErr(int line, int col, String s) {
        printMsg(line, col, s);
    }

    public void SemErr(String s) {
        errors.add(s);
    }

    public void Warning(int line, int col, String s) {
        printMsg(line, col, s);
    }

    public void Warning(String s) {
        errors.add(s);
    }
} // Errors

class FatalError extends RuntimeException {

    public static final long serialVersionUID = 1L;

    public FatalError(String s) {
        super(s);
    }
}
